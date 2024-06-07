package org.tensorflow.lite.examples.poseestimation


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//
//class RecycleViewAdapter(var gameList: List<GameItem>, var context: Context) :
//    RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder>() {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val view: View =
//            LayoutInflater.from(parent.context).inflate(R.layout.one_game_item, parent, false)
//        val holder: MyViewHolder = MyViewHolder(view)
//        return holder
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.tv_GameCategory.setText(gameList[position].getCategory())
//        holder.tv_GameHeading.setText(gameList[position].getHeading())
//        holder.tv_GameLevel.setText(gameList[position].getLevel())
//        holder.tv_GameTime.setText(gameList[position].getTime())
//        Glide.with(this.context).load(gameList[position].getImageURL())
//            .into(holder.iv_GameItemPicture)
//        holder.parentLayout.setOnClickListener { //send data
//            val intent = Intent(context, TheGame::class.java)
//            intent.putExtra("id", gameList[position].getId())
//            context.startActivity(intent)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return gameList.size
//    }
//
//    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var iv_GameItemPicture: ImageView =
//            itemView.findViewById<ImageView>(R.id.iv_GameItemPicture)
//        var tv_GameCategory: TextView = itemView.findViewById<TextView>(R.id.tv_GameCategory)
//        var tv_GameHeading: TextView = itemView.findViewById<TextView>(R.id.tv_GameHeading)
//        var tv_GameLevel: TextView = itemView.findViewById<TextView>(R.id.tv_GameLevel)
//        var tv_GameTime: TextView = itemView.findViewById<TextView>(R.id.tv_time)
//        var parentLayout: ConstraintLayout =
//            itemView.findViewById<ConstraintLayout>(R.id.OneGameLayout)
//    }
//}
