package com.halil.myapplication.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.halil.myapplication.R
import com.halil.myapplication.databinding.ActivityShareAnnounceBinding
import com.halil.myapplication.utils.GetFileName
import com.halil.myapplication.utils.SharedPreferencesHelper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ShareAnnounceActivity : AppCompatActivity() {

    private var post = ""
    private var imageURL = ""
    private var userName = ""
    private var userSurname = ""
    private var message = ""

    private var selectedImage: Uri? = null
    private lateinit var dataMap: HashMap<String, Any>
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var binding: ActivityShareAnnounceBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareAnnounceBinding.inflate(layoutInflater)
        setContentView(binding.root)


        with(binding) {

            imageLayout.visibility = View.GONE
            postProgressBar.visibility = View.GONE
            firebaseAuth = FirebaseAuth.getInstance()
            firebaseFirestore = FirebaseFirestore.getInstance()
            firebaseStorage = FirebaseStorage.getInstance()
            dataMap = HashMap()
            registerLauncher()

            sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this@ShareAnnounceActivity)
            userName = "${sharedPreferencesHelper.getString("name","User").toString()} ${sharedPreferencesHelper.getString("surname","User").toString()}"
            userSurname = sharedPreferencesHelper.getString("surname","").toString()
            imageURL = sharedPreferencesHelper.getString("imageURL","").toString()

            bottomUserText.text = userName
            bottomPostDateText.text = "${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())} ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}"
            Glide.with(this@ShareAnnounceActivity).load(imageURL).centerCrop().placeholder(R.drawable.loading_animation).error(R.drawable.people).into(bottomUserImage)

            announceShareButton.setOnClickListener {
                if (!postText.text.toString().isNullOrEmpty()) {
                    postProgressBar.visibility = View.VISIBLE
                    if (!announceImageText.text.isNullOrEmpty()) {
                        uploadImage(selectedImage!!)
                    } else {
                        val time = "${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())} ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}"
                        val date = FieldValue.serverTimestamp()
                        message = postText.text.toString()
                        dataMap["user"] = firebaseAuth.currentUser?.email.toString()
                        dataMap["message"] = message
                        dataMap["date"] = date
                        dataMap["time"] = time
                        dataMap["fullName"] = userName
                        dataMap["profileImageURL"] = imageURL
                        dataMap["announceImageURL"] = ""
                        saveAnnounce(dataMap)
                    }
                } else {
                    if (announceImageText.text.isNullOrEmpty()) {
                        Toast.makeText(this@ShareAnnounceActivity, "Boş bırakmayınız.", Toast.LENGTH_SHORT).show()
                    } else {
                        postProgressBar.visibility = View.VISIBLE
                        uploadImage(selectedImage!!)
                    }
                }
            }

            announceImageButton.setOnClickListener {
                if  (ContextCompat.checkSelfPermission(this@ShareAnnounceActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if  (ActivityCompat.shouldShowRequestPermissionRationale(this@ShareAnnounceActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Snackbar.make(it, "Galeriye erişim için izin verin.", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver") {
                            permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }.show()
                    } else {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }

                } else {
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(galleryIntent)
                }
            }

        }
    }



    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if  (result.resultCode == Activity.RESULT_OK) {
                val intentFromResult = result.data
                selectedImage = intentFromResult?.data
                println("SELECTED : $selectedImage")
                selectedImage?.let {
                    binding.imageLayout.visibility = View.VISIBLE
                    val getFileName = GetFileName(this@ShareAnnounceActivity)
                    binding.announceImageText.setText(getFileName.fileName(selectedImage!!))
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if  (result) {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(galleryIntent)
            } else {
                Toast.makeText(this@ShareAnnounceActivity, "Galeriye erişim için izin veriniz.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImage(imageURI: Uri) { // aynı fonksiyon bu çözüm üretilecek profil ile aynı
        val uuid = UUID.randomUUID()
        val imageName = "${firebaseAuth.currentUser!!.email}$uuid.jpg"
        val reference = firebaseStorage.reference
        val imageReference = reference.child("Announce Images/$imageName")
        imageReference.putFile(imageURI).addOnSuccessListener {
            val uploadImageReference = firebaseStorage.reference.child("Announce Images").child(imageName)
            uploadImageReference.downloadUrl.addOnSuccessListener {
                val downloadUrl = it.toString()
                val user = firebaseAuth.currentUser!!.email!!
                val message = binding.postText.text.toString()
                val time = "${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())} ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())}"
                val date = FieldValue.serverTimestamp()
                dataMap["user"] = user
                dataMap["message"] = message
                dataMap["date"] = date
                dataMap["time"] = time
                dataMap["fullName"] = userName
                dataMap["profileImageURL"] = imageURL
                dataMap["announceImageURL"] = downloadUrl
                saveAnnounce(dataMap)
            }
        }.addOnFailureListener {
            Toast.makeText(this@ShareAnnounceActivity,"${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveAnnounce(dataMap: HashMap<String, Any>) {
        firebaseFirestore.collection("Announces").add(dataMap).addOnSuccessListener {
            val intent = Intent(this@ShareAnnounceActivity, AnnounceActivity::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this@ShareAnnounceActivity,"${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

}