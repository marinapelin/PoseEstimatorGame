package org.tensorflow.lite.examples.poseestimation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition


class TheGame : AppCompatActivity() {
    var gameItemList: List<GameItem>? = null

    var URL: String? = null

    var tv_id: TextView? = null

    var  url:String?=null
    var result: StringBuilder = StringBuilder()
    //var baseImgResults: MutableList<KeyPoint> = ArrayList<KeyPoint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_the_game)

        //gameItemList = myApplication.getGameItemList()
        var imageView = findViewById<ImageView>(R.id.iv_silhouette)
        tv_id = findViewById(R.id.tv_forid)
        val btn_start = findViewById<ImageButton>(R.id.btn_start)


        val intent1 = intent
        if (intent1 != null && intent1.hasExtra("url")) {
            url =
                intent1.getStringExtra("url") // default value in case "id" is not found
            // Use the ID as needed
        } else {
            // Handle the case when "id" is not passed
            Log.e("the game", "No URL passed in the intent")
        }

        //URL = java.lang.String.valueOf(url)
        //URL="https://nycphoto.com/wp-content/uploads/2021/11/e10d3ab3-smiling-standing-facing-camera.jpg";

        btn_start.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@TheGame, MainActivity::class.java)
            intent.putExtra("url", url)
            startActivity(intent)
        })
        URL = url.toString()
        Glide.with(this)
            .asBitmap()
            .load(URL)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    imageView.setImageBitmap(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle placeholder if needed
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    // Handle the error case
                }
            })


    }


    private fun resizeAndPad(img: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        // Resize the image to fit within the target dimensions, maintaining aspect ratio.
        val originalWidth = img.width
        val originalHeight = img.height
        val aspectRatio = originalWidth.toFloat() / originalHeight
        val newWidth: Int
        val newHeight: Int
        if (originalWidth > originalHeight) {
            newWidth = targetWidth
            newHeight = Math.round(targetWidth / aspectRatio)
        } else {
            newHeight = targetHeight
            newWidth = Math.round(targetHeight * aspectRatio)
        }
        val resizedImg = Bitmap.createScaledBitmap(img, newWidth, newHeight, true)

        // Create a new bitmap with target dimensions and draw the resized image onto it, centered.
        val paddedImg = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(paddedImg)
        canvas.drawARGB(0, 0, 0, 0) // Optional: fill with transparent color
        canvas.drawBitmap(
            resizedImg,
            (targetWidth - newWidth) / 2f,
            (targetHeight - newHeight) / 2f, null
        )

        return paddedImg
    }
}