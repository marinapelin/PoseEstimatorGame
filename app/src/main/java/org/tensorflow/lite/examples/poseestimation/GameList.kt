package org.tensorflow.lite.examples.poseestimation

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding


import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberImagePainter
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import org.tensorflow.lite.examples.poseestimation.data.ShowResult

//import kotlin.coroutines.jvm.internal.CompletedContinuation.context


class GameList: AppCompatActivity() {

    // Define your lazy image list
    val lazyImageList: List<Drawable> by lazy {
        // This lambda expression will be executed only once, when the list is accessed for the first time
        listOf(
            loadImage(R.drawable.standingwoman),
            loadImage(R.drawable.womanwhitebg),
            loadImage(R.drawable.woman3dmodel),
            // Add more images here...
        )
    }

    // Function to load image from resource ID
    fun loadImage(@DrawableRes resourceId: Int): Drawable {
        return ContextCompat.getDrawable(this, resourceId)!!
    }
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamelist)
        //fillGameItemList()
       // accessLazyList()
        //var itemlist = findViewById<TextView>(R.id.itemlist)
        //itemlist.setText("Lazy List: $lazyList")
        // Call the function to access the lazy image list
        var imageView = findViewById<ImageView>(R.id.iv_GameItemPicture)
        imageView.setImageDrawable(lazyImageList[0])
        var tv1 = findViewById<TextView>(R.id.tv_GameCategory)
        tv1.setText("Standing pose")
        var tv2 = findViewById<TextView>(R.id.tv_GameHeading)
        tv2.setText("#1 Hands on Hips")
        var tv3 = findViewById<TextView>(R.id.tv_GameLevel)
        tv3.setText("beginner")
        var tv4 = findViewById<TextView>(R.id.tv_time)
        tv4.setText("No limits")
        var url = "https://content1.getnarrativeapp.com/static/8542a067-76df-49d2-b3c6-b347d6da1b85/Sam_0205.jpg?w=750"

        var buttongame = findViewById<ImageButton>(R.id.buttonplaymenu)



        buttongame.setOnClickListener {
            val intent = Intent(this, TheGame::class.java)
            intent.putExtra("url",  url)
            startActivity(intent)
        }
//        var imageView = findViewById<ImageView>(R.id.iv_GameItemPicture)
//        imageView.setImageDrawable(lazyImageList[0])
//        var imageView = findViewById<ImageView>(R.id.iv_GameItemPicture)
//        imageView.setImageDrawable(lazyImageList[0])


    }
    companion object {
        private val gameItemList = mutableListOf<GameItem>()

        fun getGameItemList(): List<GameItem> {
            return gameItemList
        }
    }

    init {
        fillGameItemList()
    }

    private fun fillGameItemList() {
        val g0 = GameItem(0, "Standing pose", "#1 Hands on Hips", "beginner", "No limits",
            "https://content1.getnarrativeapp.com/static/8542a067-76df-49d2-b3c6-b347d6da1b85/Sam_0205.jpg?w=750")
        val g1 = GameItem(1, "Standing pose", "#2 Look from the back", "medium", "15seconds",
            "https://celclipmaterialprod.s3-ap-northeast-1.amazonaws.com/62/89/1718962/thumbnail?1532772854")
        val g2 = GameItem(2, "Standing pose", "#3 Happy Pose", "hard", "15seconds",
            "https://nycphoto.com/wp-content/uploads/2021/11/e10d3ab3-smiling-standing-facing-camera.jpg")

        //gameItemList.addAll(listOf(g0, g1, g2))
    }

//    @Composable
//    fun LazyListWithImageAndTexts(items: List<GameItem>, modifier: Modifier) {
//        LazyColumn(modifier = modifier) {
//            items(items = items) { item ->
//                LazyRowItem(item = item)
//            }
//        }
//    }
//
//    @Composable
//    fun LazyRowItem(item: GameItem) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(40.dp)
//                .padding(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Image(
//                painter = rememberImagePainter(item.imageURL),
//                contentDescription = null,
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .aspectRatio(1f),
//                contentScale = ContentScale.Crop
//            )
//
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(start = 8.dp)
//            ) {
//                Text(text = item.category, fontSize = 20.sp)
//                Text(text = item.heading, fontSize = 16.sp)
//                Text(text = item.level, fontSize = 14.sp)
//                Text(text = item.time, fontSize = 14.sp)
//            }
//        }
//    }
//
//    @Preview
//    @Composable
//    fun PreviewLazyList() {
//        //val items = fillGameItemList()
//        LazyListWithImageAndTexts(items = gameItemList,modifier = Modifier)
//    }


// Call the function to access the lazy list


}
