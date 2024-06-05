package org.tensorflow.lite.examples.poseestimation.data

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.examples.poseestimation.R
import java.io.File
import java.io.IOException

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.target.SimpleTarget;
//import com.bumptech.glide.request.transition.Transition;
//import com.example.modelinggame.ml.MobileNetModel;
class ShowResult : AppCompatActivity() {
    var result: StringBuilder = StringBuilder()
    var imageView: ImageView? = null
    var imageView2: ImageView? = null
    var img: Bitmap? = null
    var baseimg: Bitmap? = null
    var btn_tryagain: ImageButton? = null
    var btn_save: ImageButton? = null
    var imagename: String? = null
    var resultBase: StringBuilder = StringBuilder()
    var url: String? = null
    var list1: List<KeyPoint> = ArrayList()
    var baseImgResults: List<KeyPoint> = ArrayList()

    //MyApplication myApplication = (MyApplication) this.getApplication();
    //List<GameItem> gameItemList;
    var k: Int = 0

    //Person p1;
    var numKeypoints: Int = 18 // Number of keypoints per pose
    var dimensions: Int = 2 // Each keypoint has 2 dimensions (x and y)


    //2 because 3rd is visibility scale and we do not compare it
    // Initialize the arrays
    //float[][] keypoints1 = new float[numKeypoints][dimensions];
    //float[][] keypoints2 = new float[numKeypoints][dimensions];
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        //imagename = intent.extras!!.getString("imagename")
        //url = intent.extras!!.getString("url")
        val tv = findViewById<TextView>(R.id.tv_forid)
        imageView = findViewById(R.id.iv_resultphoto)
        imageView2 = findViewById(R.id.imageView2)
        // Get the directory where the image will be saved (e.g., Pictures folder)
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        // Create the file object for the image
        //val imageFile = File(storageDir, imagename)
        //imageView.setImageURI(Uri.fromFile(imageFile))


        //ml?
        //val uri = Uri.fromFile(imageFile)
//        try {
//        //    img = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
//        } catch (e: IOException) {
//            throw RuntimeException(e)
//        }


        val tv_result = findViewById<TextView>(R.id.resulttext)

        // String roundedResult = String.format("%.1f", percentCompleted);
        tv_result.text = "Great! % right! Keep going and try new pose challenge!"


        //tv_result.setText("Oops, your pose does not match. Would you like to try again?");


        //imageView.setImageURI(fileUri);
        btn_tryagain = findViewById(R.id.btn_again)
       // btn_tryagain.setOnClickListener(View.OnClickListener {
            //Intent intent = new Intent(ShowResult.this, Camera.class);
            //intent.putExtra("url",url);
            //intent.putExtra("imagename", imagename);
            //startActivity(intent);
       // })
        btn_save = findViewById(R.id.btn_tomenu)
      //  btn_save.setOnClickListener(View.OnClickListener {
            // Intent intent = new Intent(ShowResult.this, MainActivity.class);
            //startActivity(intent);
      //  })
        //        Glide.with(this)
//                .asBitmap()
//                .load(url)
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
//                        // Create a Bitmap with a silhouette effect (outline)
//                        imageView2.setImageBitmap(bitmap);
//
//                    }
//                });
//    }
    }
}