package com.halil.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.halil.myapplication.R
import com.halil.myapplication.model.MessageModal
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(val context: Context, val messageArray: ArrayList<MessageModal>) : RecyclerView.Adapter<MessageAdapter.ItemHolder>(){

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2

    class ItemHolder(itemView: View) : ViewHolder(itemView){
        val chatMessageText = itemView.findViewById<TextView>(R.id.chatMessageText)
        val chatProfileImage = itemView.findViewById<CircleImageView>(R.id.chatProfileImage)
        val chatImageView = itemView.findViewById<ImageView>(R.id.chatImageView)
        val chatTimeText = itemView.findViewById<TextView>(R.id.timeText)
    }

    // Recycler view içerisindeki item'lerin aynılarının tutup yenileri eklemesini sağlar. verimlilik sağlar.
    private val diffUtil = object : DiffUtil.ItemCallback<MessageModal>() {
        override fun areItemsTheSame(oldItem: MessageModal, newItem: MessageModal): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MessageModal, newItem: MessageModal): Boolean {
            return oldItem == newItem
        }

    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var messages: List<MessageModal>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun getItemViewType(position: Int): Int {
        val chat = messages[position]
        return if (chat.from == FirebaseAuth.getInstance().currentUser?.email.toString()) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.receiver_items, parent, false)
            ItemHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_items, parent, false)
            ItemHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageArray.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.chatProfileImage.visibility = View.GONE
        if (messages[position].chatImageURL == "") {
            holder.chatImageView.visibility = View.GONE
            holder.chatMessageText.visibility = View.VISIBLE
            holder.chatMessageText.text = messages[position].message
            holder.chatTimeText.text = messages[position].time
        } else {
            holder.chatImageView.visibility = View.VISIBLE
            holder.chatMessageText.visibility = View.GONE
            holder.chatTimeText.text = messages[position].time
            Glide.with(context).load(messages[position].chatImageURL).fitCenter().placeholder(R.drawable.loading_animation).error(
                R.drawable.ic_launcher_background).into(holder.chatImageView)
        }
    }


}