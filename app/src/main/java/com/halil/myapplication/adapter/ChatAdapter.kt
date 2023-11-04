package com.halil.myapplication.adapter

import android.content.Context
import android.text.Html
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
import com.halil.myapplication.model.ChatModal
import com.halil.myapplication.utils.ShowUserProfile
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(private val context: Context) : RecyclerView.Adapter<ChatAdapter.ItemHolder>(){

    private val VIEW_TYPE_MESSAGE_SENT = 1
    private val VIEW_TYPE_MESSAGE_RECEIVED = 2
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    class ItemHolder(itemView: View) : ViewHolder(itemView) {}

    // Recycler view içerisindeki item'lerin aynılarının tutup yenileri eklemesini sağlar. verimlilik sağlar.
    private val diffUtil = object :DiffUtil.ItemCallback<ChatModal>() {
        override fun areItemsTheSame(oldItem: ChatModal, newItem: ChatModal): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatModal, newItem: ChatModal): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var messages: List<ChatModal>
        get() = recyclerListDiffer.currentList
        set(value) = recyclerListDiffer.submitList(value)

    override fun getItemViewType(position: Int): Int {
        val chat = messages[position]
        return if (chat.user == FirebaseAuth.getInstance().currentUser?.email.toString()) {
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
        return messages.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val chatMessageText = holder.itemView.findViewById<TextView>(R.id.chatMessageText)
        val chatProfileImage = holder.itemView.findViewById<CircleImageView>(R.id.chatProfileImage)
        val chatImageView = holder.itemView.findViewById<ImageView>(R.id.chatImageView)
        val chatTimeText = holder.itemView.findViewById<TextView>(R.id.timeText)
        val ozelText = "<b><font color=\"#222222\">${messages[position].fullName}</font></b><br>${messages[position].message}"
        if (messages[position].chatImageURL == "") {
            chatImageView.visibility = View.GONE
            chatMessageText.visibility = View.VISIBLE
            chatMessageText.text = Html.fromHtml(ozelText)
            chatTimeText.text = messages[position].time
            Glide.with(context).load(messages[position].profileImageURL).centerCrop().placeholder(R.drawable.loading_animation).error(
                R.drawable.people).into(chatProfileImage)
        } else {
            chatImageView.visibility = View.VISIBLE
            chatMessageText.visibility = View.GONE
            //chatMessageText.text = Html.fromHtml(ozelText)
            chatTimeText.text = messages[position].time
            Glide.with(context).load(messages[position].profileImageURL).centerCrop().placeholder(R.drawable.loading_animation).error(
                R.drawable.people).into(chatProfileImage)
            Glide.with(context).load(messages[position].chatImageURL).fitCenter().placeholder(R.drawable.loading_animation).error(
                R.drawable.ic_launcher_background).into(chatImageView)
        }

        chatProfileImage.setOnClickListener {
            if (auth.currentUser?.email.toString() != messages[position].user) {
                val showUserProfile = ShowUserProfile(context, messages[position].user)
                showUserProfile.show()
            }
        }

    }

}