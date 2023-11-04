package com.halil.myapplication.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.halil.myapplication.R
import com.halil.myapplication.adapter.ProfilePostAdapter
import com.halil.myapplication.databinding.FragmentProfileBinding
import com.halil.myapplication.model.PostModal
import com.halil.myapplication.utils.SharedPreferencesHelper

class ProfileFragment : Fragment() {

    private var userName = ""
    private var userSurname = ""
    private var userState = ""
    private var userMail = ""
    private var userPhone = ""
    private var userImage = ""
    private var userProfession = ""
    private var userBiography = ""

    private lateinit var profilePostAdapter: ProfilePostAdapter
    private lateinit var postArrayList: ArrayList<PostModal>
    private var selectedImage: Uri? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var fragmentProfileBinding: FragmentProfileBinding

    @SuppressLint("IntentReset")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentProfileBinding = FragmentProfileBinding.inflate(layoutInflater)

        auth = Firebase.auth
        firebaseStorage = Firebase.storage
        firestore = FirebaseFirestore.getInstance()
        postArrayList = ArrayList()


        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext())

        with(fragmentProfileBinding) {

            registerLauncher()
            rootProfileLinear.visibility = View.INVISIBLE
            getUserData()
            getPosts(auth.currentUser!!.email.toString())


            profilePhotoImage.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    ) {
                        Snackbar.make(
                            it,
                            "Galeriye erişim için izin verin.",
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction("İzin ver") {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }.show()
                    } else {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }

                } else {
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(galleryIntent)
                }
            }

        }

        return fragmentProfileBinding.root
    }

    private fun getUserData() {
        firestore.collection("Users").document(auth.currentUser?.email.toString()).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userName = document.data!!["name"].toString()
                    userSurname = document.data!!["surname"].toString()
                    userMail = document.data!!["email"].toString()
                    userPhone = document.data!!["phone"].toString()
                    userProfession = document.data!!["profession"].toString()
                    userBiography = document.data!!["biography"].toString()
                    userState = document.data!!["state"].toString()
                    userImage = document.data!!["imageURL"].toString()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }.addOnCompleteListener {
                with(fragmentProfileBinding) {
                    if (userImage != "") {
                        Glide.with(requireActivity()).load(userImage).centerCrop()
                            .placeholder(R.drawable.loading_animation).error(R.drawable.people)
                            .into(profilePhotoImage)
                    }
                    profileProgressBar.visibility = View.GONE
                    rootProfileLinear.visibility = View.VISIBLE
                    userNameSurnameText.text = "$userName $userSurname"
                    userEmailText.text = userMail
                    stateText.text = userState
                    phoneText.text = userPhone
                    professionText.text = userProfession
                    biographyText.text = userBiography
                }

            }
    }

    private fun uploadImage(imageURI: Uri) {
        val imageName = "$userMail.jpg"
        val reference = firebaseStorage.reference
        val imageReference = reference.child("images/$imageName")
        imageReference.putFile(imageURI).addOnSuccessListener {
            val uploadImageReference = firebaseStorage.reference.child("images").child(imageName)
            uploadImageReference.downloadUrl.addOnSuccessListener {
                val downloadUrl = it.toString()
                firestore.collection("Users").document(userMail).update("imageURL", downloadUrl)
                    .addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Profil Resmi başarıyla güncellendi.",
                            Toast.LENGTH_SHORT
                        ).show()
                        sharedPreferencesHelper.putString("imageURL", downloadUrl
                        )
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intentFromResult = result.data
                    selectedImage = intentFromResult?.data
                    println("SELECTED : $selectedImage")
                    selectedImage?.let {
                        fragmentProfileBinding.profilePhotoImage.setImageURI(selectedImage)
                        uploadImage(selectedImage!!)
                    }
                }
            }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(galleryIntent)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Galeriye erişim için izin veriniz.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun getPosts(email: String) {
        firestore.collection("Posts").whereEqualTo("email", email).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    for (documents in document) {
                        val postModal = PostModal(
                            documents.data["imageURL"].toString(),
                            documents.data["email"].toString(),
                            documents.data["name"].toString(),
                            documents.data["surname"].toString(),
                            documents.data["time"].toString(),
                            documents.data["post"].toString()
                        )
                        postArrayList.add(postModal)
                        Log.d(
                            "Postt",
                            "${documents.data["name"].toString()} ${documents.data["post"].toString()}"
                        )
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Postları yüklerken bir sorun oluştu. ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnCompleteListener {
                if (postArrayList.size == 0) {
                    fragmentProfileBinding.sharePostText.visibility = View.GONE
                    fragmentProfileBinding.profilePostRecycler.visibility = View.GONE
                } else {
                    fragmentProfileBinding.sharePostText.visibility = View.VISIBLE
                    fragmentProfileBinding.profilePostRecycler.visibility = View.VISIBLE
                    profilePostAdapter = ProfilePostAdapter(requireContext(), postArrayList)
                    fragmentProfileBinding.profilePostRecycler.adapter = profilePostAdapter
                }

            }
    }

}