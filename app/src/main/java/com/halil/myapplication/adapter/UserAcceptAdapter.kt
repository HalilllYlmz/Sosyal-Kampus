package com.halil.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.myapplication.R
import com.halil.myapplication.model.UserAcceptModal

class UserAcceptAdapter(private val context: Context, private val filter: String, private val arrayList: ArrayList<UserAcceptModal>) : RecyclerView.Adapter<UserAcceptAdapter.ItemHolder>() {

    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    class ItemHolder(itemView: View): ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userState: TextView = itemView.findViewById(R.id.userState)
        val userNumber: TextView = itemView.findViewById(R.id.userNumber)
        val acceptButton: ImageView = itemView.findViewById(R.id.acceptButton)
        val waitButton: ImageView = itemView.findViewById(R.id.waitButton)
        val removeButton: ImageView = itemView.findViewById(R.id.removeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_accept_items, parent, false)
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.userName.text = arrayList[position].userName
        holder.userState.text = "Durum: ${arrayList[position].userState}"
        holder.userNumber.text = "Numara: ${arrayList[position].userNumber}"
        if (filter == "all") {
            if (arrayList[position].userApprovalState != "none") {
                holder.waitButton.visibility = View.GONE
            } else {
                holder.waitButton.visibility = View.VISIBLE
            }
        } else if (filter == "true" || filter == "false") {
            holder.acceptButton.visibility = View.GONE
            holder.waitButton.visibility = View.GONE
            holder.removeButton.visibility = View.GONE
        } else if (filter == "none") {
            holder.acceptButton.visibility = View.VISIBLE
            holder.waitButton.visibility = View.VISIBLE
            holder.removeButton.visibility = View.VISIBLE
        }

        holder.acceptButton.setOnClickListener {
            changeApprovalStatus("true", arrayList[position].userEmail)
        }

        holder.removeButton.setOnClickListener {
            changeApprovalStatus("false", arrayList[position].userEmail)
        }

    }

    fun changeApprovalStatus(status: String, email: String) {
        firestore.collection("User Accept").document(email).update("approvalStatus", status).addOnCompleteListener {
            Toast.makeText(context, "Başarılı", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
        }
    }


}