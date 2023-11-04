package com.halil.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.halil.myapplication.R
import com.halil.myapplication.model.ProfilesModal
import com.halil.myapplication.utils.ShowUserProfile
import de.hdodenhof.circleimageview.CircleImageView

class ProfilesAdapter(private val context: Context, private val arrayList: ArrayList<ProfilesModal>) : RecyclerView.Adapter<ProfilesAdapter.ItemHolder>() {

    class ItemHolder(val itemView: View) : ViewHolder(itemView) {
        val profilesImage:CircleImageView = itemView.findViewById(R.id.profilesImage)
        val profileName:TextView = itemView.findViewById(R.id.profileName)
        val profileState: TextView = itemView.findViewById(R.id.profileState)
        val showProfile: Button = itemView.findViewById(R.id.showProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_items, parent, false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        Glide.with(context).load(arrayList[position].imageURL).fitCenter().placeholder(R.drawable.loading_animation).error(
            R.drawable.people).into(holder.profilesImage)
        holder.profileName.text = "${arrayList[position].userName} ${arrayList[position].surname}"
        holder.profileState.text = "${arrayList[position].userState}"
        holder.showProfile.setOnClickListener {
            val showUserProfile = ShowUserProfile(context, arrayList[position].userMail)
            showUserProfile.show()
        }
    }


}