package com.halil.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.halil.myapplication.R
import com.halil.myapplication.model.AnnounceModal
import de.hdodenhof.circleimageview.CircleImageView

class AnnounceAdapter(private val context: Context, private val announceArray: ArrayList<AnnounceModal>) : RecyclerView.Adapter<AnnounceAdapter.ItemHolder>() {

    class ItemHolder(itemView: View) : ViewHolder(itemView) {
        val announceNameSurnameText: TextView = itemView.findViewById(R.id.announceNameSurnameText)
        val announceDateText: TextView = itemView.findViewById(R.id.announceDateText)
        val announceContentsText: TextView = itemView.findViewById(R.id.announceContentsText)
        val announceProfileImage: CircleImageView = itemView.findViewById(R.id.announceProfileImage)
        val announceImage: ImageView = itemView.findViewById<CircleImageView>(R.id.announceImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.announce_items,parent,false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return announceArray.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        if  (announceArray[position].announceImageURL == "") {
            holder.announceImage.visibility = View.GONE
            holder.announceNameSurnameText.text = announceArray[position].fullName
            holder.announceDateText.text = announceArray[position].time
            holder.announceContentsText.text = announceArray[position].message
            Glide.with(context).load(announceArray[position].profileImageURL).centerCrop().placeholder(R.drawable.loading_animation).error(
                R.drawable.people).into(holder.announceProfileImage)
        } else {
            holder.announceImage.visibility = View.VISIBLE
            holder.announceNameSurnameText.text = announceArray[position].fullName
            holder.announceDateText.text = announceArray[position].time
            holder.announceContentsText.text = announceArray[position].message
            Glide.with(context).load(announceArray[position].profileImageURL).centerCrop().placeholder(R.drawable.loading_animation).error(
                R.drawable.people).into(holder.announceProfileImage)
            Glide.with(context).load(announceArray[position].announceImageURL).fitCenter().placeholder(R.drawable.loading_animation).error(
                R.drawable.people).into(holder.announceImage)
        }

    }


}