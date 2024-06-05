/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package org.tensorflow.lite.examples.poseestimation.camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Rect
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult

import android.media.ImageReader
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import android.widget.Toast
import kotlinx.coroutines.suspendCancellableCoroutine
import org.tensorflow.lite.examples.poseestimation.VisualizationUtils
import org.tensorflow.lite.examples.poseestimation.YuvToRgbConverter
import org.tensorflow.lite.examples.poseestimation.data.BodyPart
import org.tensorflow.lite.examples.poseestimation.data.KeyPoint
import org.tensorflow.lite.examples.poseestimation.data.KeyPointComparer
import org.tensorflow.lite.examples.poseestimation.data.Person
import org.tensorflow.lite.examples.poseestimation.ml.MoveNetMultiPose
import org.tensorflow.lite.examples.poseestimation.ml.PoseClassifier
import org.tensorflow.lite.examples.poseestimation.ml.PoseDetector
import org.tensorflow.lite.examples.poseestimation.ml.TrackerType
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CameraSource(
    //private val callback: EndGameListener,
    private val surfaceView: SurfaceView,
    private val listener: CameraSourceListener? = null
) {
    var test: List<Pair<String, Float>>? =null
        public var EndGame: Boolean = false
        private set
//    interface EndGameListener {
//
//        fun OnEndGame()
//    }


//    private var endGameListener: EndGameListener? = null
//
//    fun setEndGameListener(listener: EndGameListener) {
//        endGameListener = listener
//    }
    companion object {
        private const val PREVIEW_WIDTH = 640
        private const val PREVIEW_HEIGHT = 480

        /** Threshold for confidence score. */
        private const val MIN_CONFIDENCE = .2f
        private const val TAG = "Camera Source"
    }

    private var thePersons: List<Person> = listOf(
        Person(
            id = -1,
            keyPoints = listOf(
                KeyPoint(bodyPart = BodyPart.NOSE, coordinate = PointF(262.3125f, 262.5078f), score = 0.61399585f),
                KeyPoint(bodyPart = BodyPart.LEFT_EYE, coordinate = PointF(294.6723f, 233.80849f), score = 0.47138926f),
                KeyPoint(bodyPart = BodyPart.RIGHT_EYE, coordinate = PointF(252.67532f, 237.27197f), score = 0.5418733f),
                KeyPoint(bodyPart = BodyPart.LEFT_EAR, coordinate = PointF(373.3912f, 254.24982f), score = 0.48233688f),
                KeyPoint(bodyPart = BodyPart.RIGHT_EAR, coordinate = PointF(255.02353f, 251.37383f), score = 0.24887112f),
                KeyPoint(bodyPart = BodyPart.LEFT_SHOULDER, coordinate = PointF(428.4883f, 425.24216f), score = 0.62777394f),
                KeyPoint(bodyPart = BodyPart.RIGHT_SHOULDER, coordinate = PointF(220.79736f, 387.03735f), score = 0.7529446f),
                KeyPoint(bodyPart = BodyPart.LEFT_ELBOW, coordinate = PointF(416.0971f, 625.88104f), score = 0.31619754f),
                KeyPoint(bodyPart = BodyPart.RIGHT_ELBOW, coordinate = PointF(124.45984f, 512.50476f), score = 0.4252016f),
                KeyPoint(bodyPart = BodyPart.LEFT_WRIST, coordinate = PointF(287.91705f, 606.51953f), score = 0.09692693f),
                KeyPoint(bodyPart = BodyPart.RIGHT_WRIST, coordinate = PointF(107.27205f, 626.70416f), score = 0.26168692f),
                KeyPoint(bodyPart = BodyPart.LEFT_HIP, coordinate = PointF(324.33444f, 638.16925f), score = 0.2071212f),
                KeyPoint(bodyPart = BodyPart.RIGHT_HIP, coordinate = PointF(201.19821f, 638.6145f), score = 0.26187536f),
                KeyPoint(bodyPart = BodyPart.LEFT_KNEE, coordinate = PointF(384.43634f, 641.734f), score = 0.034328014f),
                KeyPoint(bodyPart = BodyPart.RIGHT_KNEE, coordinate = PointF(70.66431f, 642.3127f), score = 0.04376632f),
                KeyPoint(bodyPart = BodyPart.LEFT_ANKLE, coordinate = PointF(206.36691f, 637.9958f), score = 0.037734658f),
                KeyPoint(bodyPart = BodyPart.RIGHT_ANKLE, coordinate = PointF(76.84413f, 635.8355f), score = 0.03425151f)
            ),
            boundingBox = null,
            score = 0.321075f
        )
    )

    private val lock = Any()
    private var detector: PoseDetector? = null
    private var classifier: PoseClassifier? = null
    private var isTrackerEnabled = false
    private var yuvConverter: YuvToRgbConverter = YuvToRgbConverter(surfaceView.context)
    public lateinit var imageBitmap: Bitmap

    /** Frame count that have been processed so far in an one second interval to calculate FPS. */
    private var fpsTimer: Timer? = null
    private var frameProcessedInOneSecondInterval = 0
    private var framesPerSecond = 0

    /** Detects, characterizes, and connects to a CameraDevice (used for all camera operations) */
    private val cameraManager: CameraManager by lazy {
        val context = surfaceView.context
        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    /** Readers used as buffers for camera still shots */
    private var imageReader: ImageReader? = null

    /** The [CameraDevice] that will be opened in this fragment */
    private var camera: CameraDevice? = null

    /** Internal reference to the ongoing [CameraCaptureSession] configured with our parameters */
    private var session: CameraCaptureSession? = null

    /** [HandlerThread] where all buffer reading operations run */
    private var imageReaderThread: HandlerThread? = null

    /** [Handler] corresponding to [imageReaderThread] */
    private var imageReaderHandler: Handler? = null
    private var cameraId: String = ""

    suspend fun initCamera() {
        camera = openCamera(cameraManager, cameraId)
        imageReader =
            ImageReader.newInstance(PREVIEW_WIDTH, PREVIEW_HEIGHT, ImageFormat.YUV_420_888, 3)
        imageReader?.setOnImageAvailableListener({ reader ->
            val image = reader.acquireLatestImage()
            if (image != null) {
                if (!::imageBitmap.isInitialized) {
                    imageBitmap =
                        Bitmap.createBitmap(
                            PREVIEW_WIDTH,
                            PREVIEW_HEIGHT,
                            Bitmap.Config.ARGB_8888
                        )
                }
                yuvConverter.yuvToRgb(image, imageBitmap)
                // Create rotated version for portrait display
                val rotateMatrix = Matrix()
                rotateMatrix.postRotate(-90.0f) //90.0f if we are facing front
                // Mirror the image
                rotateMatrix.postScale(-1f, 1f, PREVIEW_WIDTH / 2f, PREVIEW_HEIGHT / 2f)

                val rotatedBitmap = Bitmap.createBitmap(
                    imageBitmap, 0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT,
                    rotateMatrix, false
                )
                processImage(rotatedBitmap)
                image.close()
            }
        }, imageReaderHandler)

        imageReader?.surface?.let { surface ->
            session = createSession(listOf(surface))
            val cameraRequest = camera?.createCaptureRequest(
                CameraDevice.TEMPLATE_PREVIEW
            )?.apply {
                addTarget(surface)
            }
            cameraRequest?.build()?.let {
                session?.setRepeatingRequest(it, null, null)
            }
        }
        // Take photo after 1 seconds delay
        //takePhotoAfterDelay(1000)
    }

    private suspend fun createSession(targets: List<Surface>): CameraCaptureSession =
        suspendCancellableCoroutine { cont ->
            camera?.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(captureSession: CameraCaptureSession) =
                    cont.resume(captureSession)

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    cont.resumeWithException(Exception("Session error"))
                }
            }, null)
        }

    @SuppressLint("MissingPermission")
    private suspend fun openCamera(manager: CameraManager, cameraId: String): CameraDevice =
        suspendCancellableCoroutine { cont ->
            manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) = cont.resume(camera)

                override fun onDisconnected(camera: CameraDevice) {
                    camera.close()
                }

                override fun onError(camera: CameraDevice, error: Int) {
                    if (cont.isActive) cont.resumeWithException(Exception("Camera error"))
                }
            }, imageReaderHandler)
        }

    fun prepareCamera() {
        for (cameraId in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)

            // We don't use a front facing camera in this sample.

            val cameraDirection = characteristics.get(CameraCharacteristics.LENS_FACING)//
            if (cameraDirection != null &&
                cameraDirection == CameraCharacteristics.LENS_FACING_BACK     //LENS_FACING_FRONT
            ) {
                continue
            }
            this.cameraId = cameraId


            break
        }
    }


    fun setDetector(detector: PoseDetector) {
        synchronized(lock) {
            if (this.detector != null) {
                this.detector?.close()
                this.detector = null
            }
            this.detector = detector
        }
    }

    fun setClassifier(classifier: PoseClassifier?) {
        synchronized(lock) {
            if (this.classifier != null) {
                this.classifier?.close()
                this.classifier = null
            }
            this.classifier = classifier
        }
    }

    /**
     * Set Tracker for Movenet MuiltiPose model.
     */
    fun setTracker(trackerType: TrackerType) {
        isTrackerEnabled = trackerType != TrackerType.OFF
        (this.detector as? MoveNetMultiPose)?.setTracker(trackerType)
    }

    fun resume() {
        imageReaderThread = HandlerThread("imageReaderThread").apply { start() }
        imageReaderHandler = Handler(imageReaderThread!!.looper)
        fpsTimer = Timer()
        fpsTimer?.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    framesPerSecond = frameProcessedInOneSecondInterval
                    frameProcessedInOneSecondInterval = 0
                }
            },
            0,
            1000
        )
    }

    fun close() {
        session?.close()
        session = null
        camera?.close()
        camera = null
        imageReader?.close()
        imageReader = null
        stopImageReaderThread()
        detector?.close()
        detector = null
        classifier?.close()
        classifier = null
        fpsTimer?.cancel()
        fpsTimer = null
        frameProcessedInOneSecondInterval = 0
        framesPerSecond = 0
    }

    // process image
    private fun processImage(bitmap: Bitmap) {
        val persons = mutableListOf<Person>()
        var classificationResult: List<Pair<String, Float>>? = null

        synchronized(lock) {
            detector?.estimatePoses(bitmap)?.let {
                persons.addAll(it)

                // if the model only returns one item, allow running the Pose classifier.
                if (persons.isNotEmpty()) {
                    classifier?.run {
                        classificationResult = classify(persons[0])
                        test =classificationResult
                    }
                }
            }
        }
        frameProcessedInOneSecondInterval++
        if (frameProcessedInOneSecondInterval == 1) {
            // send fps to view
            listener?.onFPSListener(framesPerSecond)
        }

        // if the model returns only one item, show that item's score.
        if (persons.isNotEmpty()) {
            listener?.onDetectedInfo(persons[0].score, classificationResult)
        }
        visualize(persons, bitmap)
    }

    private fun visualize(persons: List<Person>, bitmap: Bitmap) {

        val outputBitmap = VisualizationUtils.drawBodyKeypoints(
            bitmap,
            persons.filter { it.score > MIN_CONFIDENCE }, isTrackerEnabled
        )
        val threshold = 50.0f // Adjust this threshold as needed
        val comparer = KeyPointComparer()
        val result = comparer.areListPersonsSimilar(thePersons, persons, threshold)
        //Toast.makeText(surfaceView.context, "Result: $result", Toast.LENGTH_LONG).show()

        //println("Are persons similar: $result")
         if(result==true){
             Toast.makeText(surfaceView.context, "Result: $result", Toast.LENGTH_LONG).show()
             //takePhotoAfterDelay(10)
             saveImage(bitmap)
         }


        val holder = surfaceView.holder
        val surfaceCanvas = holder.lockCanvas()
        surfaceCanvas?.let { canvas ->
            val screenWidth: Int
            val screenHeight: Int
            val left: Int
            val top: Int

            if (canvas.height > canvas.width) {
                val ratio = outputBitmap.height.toFloat() / outputBitmap.width
                screenWidth = canvas.width
                left = 0
                screenHeight = (canvas.width * ratio).toInt()
                top = (canvas.height - screenHeight) / 2
            } else {
                val ratio = outputBitmap.width.toFloat() / outputBitmap.height
                screenHeight = canvas.height
                top = 0
                screenWidth = (canvas.height * ratio).toInt()
                left = (canvas.width - screenWidth) / 2
            }
            val right: Int = left + screenWidth
            val bottom: Int = top + screenHeight

            canvas.drawBitmap(
                outputBitmap, Rect(0, 0, outputBitmap.width, outputBitmap.height),
                Rect(left, top, right, bottom), null
            )
            surfaceView.holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun stopImageReaderThread() {
        imageReaderThread?.quitSafely()
        try {
            imageReaderThread?.join()
            imageReaderThread = null
            imageReaderHandler = null
        } catch (e: InterruptedException) {
            Log.d(TAG, e.message.toString())
        }
    }

    interface CameraSourceListener {
        fun onFPSListener(fps: Int)
        fun onDetectedInfo(personScore: Float?, poseLabels: List<Pair<String, Float>>?)
        fun onClose(boolean: Boolean)
    }
    //new
    private fun capturePhoto() {
        val captureRequest = camera?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        imageReader?.surface?.let {
            captureRequest?.addTarget(it)
            captureRequest?.build()?.let { it1 ->
                session?.capture(it1, object : CameraCaptureSession.CaptureCallback() {
                    override fun onCaptureCompleted(
                        session: CameraCaptureSession,
                        request: CaptureRequest,
                        result: TotalCaptureResult
                    ) {
                        super.onCaptureCompleted(session, request, result)
                        Toast.makeText(surfaceView.context, "Photo captured", Toast.LENGTH_SHORT).show()
                        saveImage(imageBitmap)//i think
                    }
                }, imageReaderHandler)
            }
        }
    }
    fun saveImage(imageBitmap: Bitmap) {
        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val myDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AppPictures")
        myDir.mkdirs()
        val fileName = "Image_${System.currentTimeMillis()}.jpg"
        val file = File(myDir, fileName)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            Toast.makeText(surfaceView.context, "Saved: $file", Toast.LENGTH_LONG).show()
            EndGame = true;
            //callback.onCameraReady()
            //stopImageReaderThread()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(surfaceView.context, "Error saving image", Toast.LENGTH_SHORT).show()
        }
       // close()

        //listener?.onDetectedInfo(21123.2f, test)
        //listener?.onClose(true)
        //onClose()

    }

    private fun takePhotoAfterDelay(delayMillis: Long) {
        imageReaderHandler?.postDelayed({
            capturePhoto()
        }, delayMillis)
    }

    //end new

}
