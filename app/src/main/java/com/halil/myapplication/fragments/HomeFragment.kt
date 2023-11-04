package com.halil.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.halil.myapplication.R
import com.halil.myapplication.adapter.PostAdapter
import com.halil.myapplication.databinding.FragmentHomeBinding
import com.halil.myapplication.model.PostModal
import com.halil.myapplication.utils.SharedPreferencesHelper
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    private var post = ""
    private var imageURL = ""
    private var userName = ""
    private var userSurname = ""

    private lateinit var postAdapter: PostAdapter
    private lateinit var postModal: PostModal
    private lateinit var postArrayList: ArrayList<PostModal>
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var fragmentHomeBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)
        fragmentHomeBinding.homeProgressBar.visibility = View.VISIBLE

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        postArrayList = ArrayList()

        loadImage()

        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext())
        userName = sharedPreferencesHelper.getString("name", "User").toString()
        userSurname = sharedPreferencesHelper.getString("surname", "").toString()
        imageURL = sharedPreferencesHelper.getString("imageURL", "").toString()

        getPosts()

        with(fragmentHomeBinding) {

            val postAdapter = PostAdapter(requireContext(), postArrayList)

            extendedFab.setOnClickListener {
                val time = "${
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(Date())
                } ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}"
                getPostLayout(time)
            }

        }

        return fragmentHomeBinding.root
    }

    private fun getPostLayout(time: String) {
        bottomSheetDialog = BottomSheetDialog(requireActivity())
        val view = layoutInflater.inflate(R.layout.bottom_dialog, null, false)
        val bottomUserText = view.findViewById<TextView>(R.id.bottomUserText)
        val postText = view.findViewById<TextInputEditText>(R.id.postText)
        val bottomUserImage = view.findViewById<CircleImageView>(R.id.bottomUserImage)
        val bottomPostDateText = view.findViewById<TextView>(R.id.bottomPostDateText)
        val postProgressBar = view.findViewById<ProgressBar>(R.id.postProgressBar)
        val shareButton = view.findViewById<Button>(R.id.shareButton)

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
        postProgressBar.visibility = View.GONE
        bottomUserText.text = "$userName $userSurname"
        bottomPostDateText.text = "$time"
        Glide.with(requireActivity()).load(imageURL).centerCrop()
            .placeholder(R.drawable.loading_animation).error(R.drawable.people)
            .into(bottomUserImage)


        shareButton.setOnClickListener {
            if (!postText.text.isNullOrEmpty()) {
                postProgressBar.visibility = View.VISIBLE
                post = postText.text.toString()
                val date = FieldValue.serverTimestamp()
                val postHashMap = hashMapOf<String, Any>(
                    "imageURL" to imageURL,
                    "name" to userName,
                    "surname" to userSurname,
                    "email" to auth.currentUser!!.email.toString(),
                    "time" to time,
                    "date" to date,
                    "post" to post
                )
                savePost(postHashMap)
                postModal = PostModal(
                    imageURL,
                    auth.currentUser!!.email.toString(),
                    userName,
                    userSurname,
                    time,
                    post
                )
                postArrayList.add(postModal)
                postAdapter = PostAdapter(requireContext(), postArrayList)
                fragmentHomeBinding.postRecyclerView.adapter?.notifyDataSetChanged()
            }
        }


    }

    private fun loadImage() {
        firestore.collection("Users").document(auth.currentUser?.email.toString()).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    sharedPreferencesHelper.putString("name", document.data!!["name"].toString())
                    sharedPreferencesHelper.putString(
                        "surname",
                        document.data!!["surname"].toString()
                    )
                    sharedPreferencesHelper.putString(
                        "number",
                        document.data!!["number"].toString()
                    )
                    sharedPreferencesHelper.putString("state", document.data!!["state"].toString())
                    sharedPreferencesHelper.putString("email", document.data!!["email"].toString())
                    sharedPreferencesHelper.putString("phone", document.data!!["phone"].toString())
                    sharedPreferencesHelper.putString(
                        "profession",
                        document.data!!["profession"].toString()
                    )
                    sharedPreferencesHelper.putString(
                        "biography",
                        document.data!!["biography"].toString()
                    )
                    imageURL = document.data!!["imageURL"].toString()
                }
            }.addOnFailureListener {
            Toast.makeText(requireContext(), "Görsel indirilemedi.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePost(postHashMap: HashMap<String, Any>) {
        firestore.collection("Posts").add(postHashMap).addOnSuccessListener {

        }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                "Durum Paylaşılamadı : ${it.message}",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnCompleteListener {
            bottomSheetDialog.cancel()
        }
    }

    private fun getPosts() {
        firestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    for (document in result) {
                        postModal = PostModal(
                            document.data["imageURL"].toString(),
                            document.data["email"].toString(),
                            document.data["name"].toString(),
                            document.data["surname"].toString(),
                            document.data["time"].toString(),
                            document.data["post"].toString()
                        )
                        postArrayList.add(postModal)
                    }
                }
            }.addOnFailureListener {
            Toast.makeText(
                requireContext(),
                "Durumlar yüklenirken bir sorun oluştu : ${it.message}",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnCompleteListener {
            fragmentHomeBinding.homeProgressBar.visibility = View.GONE
            postAdapter = PostAdapter(requireContext(), postArrayList)
            fragmentHomeBinding.postRecyclerView.adapter = postAdapter
        }
    }


}