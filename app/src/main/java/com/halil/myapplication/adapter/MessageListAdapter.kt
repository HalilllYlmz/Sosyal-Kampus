package com.halil.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.halil.myapplication.R
import com.halil.myapplication.model.ListMessageModal
import com.halil.myapplication.view.MessageActivity
import de.hdodenhof.circleimageview.CircleImageView

class MessageListAdapter(private val context: Context, private val listMessageArrayList: ArrayList<ListMessageModal>) : RecyclerView.Adapter<MessageListAdapter.ItemHolder>(){

    class ItemHolder(itemView: View) : ViewHolder(itemView) {
        val messageProfileImage: CircleImageView = itemView.findViewById(R.id.messageProfileImage)
        val messageNameText: TextView = itemView.findViewById(R.id.messageNameText)
        val messageEmailText: TextView = itemView.findViewById(R.id.messageEmailText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_message_items, parent, false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return listMessageArrayList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        Glide.with(context).load(listMessageArrayList[position].imageURL).fitCenter().placeholder(R.drawable.loading_animation).error(
            R.drawable.ic_launcher_background).into(holder.messageProfileImage)
        holder.messageNameText.text = listMessageArrayList[position].name
        holder.messageEmailText.text = listMessageArrayList[position].receiverEmail

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra("userMail", listMessageArrayList[position].receiverEmail)
            intent.putExtra("imageURL", listMessageArrayList[position].imageURL)
            intent.putExtra("username", listMessageArrayList[position].name)
            context.startActivity(intent)
        }
    }


}