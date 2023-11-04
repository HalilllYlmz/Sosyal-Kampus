package com.halil.myapplication.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.halil.myapplication.R
import com.halil.myapplication.model.PostModal
import de.hdodenhof.circleimageview.CircleImageView

class ProfilePostAdapter(private val context: Context, private val postArrayList: ArrayList<PostModal>) : RecyclerView.Adapter<ProfilePostAdapter.ItemHolder>() {

    class ItemHolder(itemView: View) : ViewHolder(itemView) {
        val postNameSurnameText: TextView = itemView.findViewById<TextView>(R.id.postNameSurnameText)
        val postDateText: TextView = itemView.findViewById<TextView>(R.id.postDateText)
        val postContentsText: TextView = itemView.findViewById<TextView>(R.id.postContentsText)
        val postProfileImage: CircleImageView = itemView.findViewById<CircleImageView>(R.id.postProfileImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_items, parent,false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return postArrayList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        Glide.with(context).load(postArrayList[position].imageURL).centerCrop().placeholder(R.drawable.loading_animation).error(
            R.drawable.people).into(holder.postProfileImage)
        holder.postNameSurnameText.text = "${postArrayList[position].name} ${postArrayList[position].surname}"
        holder.postDateText.text = "${postArrayList[position].date}"
        holder.postContentsText.text = postArrayList[position].post
    }


}