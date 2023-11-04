package com.halil.myapplication.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.halil.myapplication.R
import com.halil.myapplication.adapter.ProfilePostAdapter
import com.halil.myapplication.model.PostModal
import com.halil.myapplication.view.MessageActivity
import de.hdodenhof.circleimageview.CircleImageView

class ShowUserProfile(val context: Context, val email: String) {

    private var imageURL = ""
    private lateinit var profilePostAdapter: ProfilePostAdapter
    private lateinit var postArrayList: ArrayList<PostModal>
    private var auth = Firebase.auth
    private var firestore = FirebaseFirestore.getInstance()
    private lateinit var bottomSheetDialog: BottomSheetDialog

    @SuppressLint("MissingInflatedId", "InflateParams")
    fun show() {
        bottomSheetDialog = BottomSheetDialog(context)
        postArrayList = ArrayList()
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.bottom_profile_dialog, null, false)

        val rootProfileLinear = view.findViewById<LinearLayout>(R.id.rootProfileLinear)
        val profilePhotoImage = view.findViewById<CircleImageView>(R.id.profilePhotoImage)
        val userNameSurnameText = view.findViewById<TextView>(R.id.userNameSurnameText)
        val stateText = view.findViewById<TextView>(R.id.stateText)
        val biographyText = view.findViewById<TextView>(R.id.biographyText)
        val userEmailText = view.findViewById<TextView>(R.id.userEmailText)
        val phoneText = view.findViewById<TextView>(R.id.phoneText)
        val professionText = view.findViewById<TextView>(R.id.professionText)
        val profileProgressBar = view.findViewById<ProgressBar>(R.id.profileProgressBar)
        val sendMessage = view.findViewById<ImageView>(R.id.sendMessage)
        val sharePostText = view.findViewById<TextView>(R.id.sharePostText)
        val profilePostRecycler = view.findViewById<RecyclerView>(R.id.profilePostRecycler)

        bottomSheetDialog.setContentView(view)

        val parent = view.parent as View
        val layoutParams = parent.layoutParams
        layoutParams.height = context.resources.displayMetrics.heightPixels
        parent.layoutParams = layoutParams
        bottomSheetDialog.show()

        rootProfileLinear.visibility = View.INVISIBLE

        sendMessage.setOnClickListener {
            createSenderDocument(hashMapOf())
            createReceiverDocument(hashMapOf())
            val intent = Intent(context, MessageActivity::class.java)
            intent.putExtra("userMail", email)
            intent.putExtra("imageURL", imageURL)
            intent.putExtra("username", userNameSurnameText.text.toString())
            context.startActivity(intent)
        }

        firestore.collection("Users").document(email).get().addOnSuccessListener {document ->

            if (document != null) {
                imageURL = document.data!!["imageURL"].toString()
                userNameSurnameText.text = "${document.data!!["name"].toString()} ${document.data!!["surname"].toString()}"
                userEmailText.text = document.data!!["email"].toString()
                phoneText.text = document.data!!["phone"].toString()
                professionText.text = document.data!!["profession"].toString()
                biographyText.text = document.data!!["biography"].toString()
                stateText.text = document.data!!["state"].toString()
                Glide.with(context).load(imageURL).centerCrop().placeholder(R.drawable.loading_animation).error(
                    R.drawable.people).into(profilePhotoImage)
            }

        }.addOnFailureListener {
            Toast.makeText(context,"Kullanıcı bilgileri yüklenemedi. ${it.message}", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            rootProfileLinear.visibility = View.VISIBLE
            profileProgressBar.visibility = View.INVISIBLE
        }

        firestore.collection("Posts").whereEqualTo("email",email).get().addOnSuccessListener { document ->
            if (document != null) {
                for (documents in document) {
                    val postModal = PostModal(documents.data["imageURL"].toString(), documents.data["email"].toString(),documents.data["name"].toString(),documents.data["surname"].toString(),
                        documents.data["time"].toString(), documents.data["post"].toString())
                    postArrayList.add(postModal)
                    Log.d("Postt","${documents.data["name"].toString()} ${documents.data["post"].toString()}")
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Postları yüklerken bir sorun oluştu. ${it.message}", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            if (postArrayList.size == 0) {
                sharePostText.visibility = View.GONE
                profilePostRecycler.visibility = View.GONE
            } else { sharePostText.visibility = View.VISIBLE
                profilePostRecycler.visibility = View.VISIBLE
                profilePostAdapter = ProfilePostAdapter(context, postArrayList)
                profilePostRecycler.adapter = profilePostAdapter
            }

        }

    }

    private fun createSenderDocument(emptyHashMap: HashMap<String,Any>) {
        firestore.collection("Single Chat").document(auth.currentUser?.email.toString()).set(emptyHashMap).addOnSuccessListener {
            Toast.makeText(context, "Başarıli!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Koleksiyon oluştururken bir hata oluştu : ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createReceiverDocument(emptyHashMap: HashMap<String,Any>) {
        firestore.collection("Single Chat").document(email).set(emptyHashMap).addOnSuccessListener {
            Toast.makeText(context, "Başarıli!", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Koleksiyon oluştururken bir hata oluştu : ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }



}