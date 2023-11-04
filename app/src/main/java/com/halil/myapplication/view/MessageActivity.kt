package com.halil.myapplication.view

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.halil.myapplication.R
import com.halil.myapplication.adapter.MessageAdapter
import com.halil.myapplication.databinding.ActivityMessageBinding
import com.halil.myapplication.model.MessageModal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MessageActivity : AppCompatActivity() {

    private var email = ""
    private var imageURL = ""
    private var username = ""
    private lateinit var sendMessageArrayList: kotlin.collections.ArrayList<String>
    private lateinit var messagesArrayList: ArrayList<MessageModal>
    private lateinit var messageAdapter: MessageAdapter
    private var selectedImage: Uri? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var chatHashMap: HashMap<String, Any>
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var activityMessageBinding: ActivityMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMessageBinding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(activityMessageBinding.root)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        chatHashMap = HashMap()
        messagesArrayList = ArrayList()
        sendMessageArrayList = ArrayList()
        messageAdapter = MessageAdapter(this@MessageActivity, messagesArrayList)
        activityMessageBinding.singleChatRecycler.adapter = messageAdapter

        registerLauncher()

        email = intent.getStringExtra("userMail").toString()
        username = intent.getStringExtra("username").toString()
        imageURL = intent.getStringExtra("imageURL").toString()

        with(activityMessageBinding) {
            Glide.with(this@MessageActivity).load(imageURL).centerCrop().placeholder(R.drawable.loading_animation).error(
            R.drawable.people).into(singleChatImage)
            singleChatName.text = username
            sendButton.setOnClickListener {
                if (!chatText.text.isNullOrEmpty()) {
                    chatHashMap["message"] = chatText.text.toString()
                    chatHashMap["from"] = auth.currentUser!!.email.toString()
                    chatHashMap["to"] = email
                    chatHashMap["receiverName"] = username
                    chatHashMap["chatImageURL"] = ""
                    chatHashMap["date"] = FieldValue.serverTimestamp()
                    chatHashMap["time"] = "${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())} ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}"
                    saveSenderMessage(email, chatHashMap)
                    saveReceiverMessage(email,chatHashMap)
                }
            }
            galleryButton.setOnClickListener {
                if  (ContextCompat.checkSelfPermission(this@MessageActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if  (ActivityCompat.shouldShowRequestPermissionRationale(this@MessageActivity, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
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

        getMessage()


    }

    private fun saveSenderMessage(receiverEmail: String, chatHashMap: HashMap<String, Any>) {
        firestore.collection("Single Chat").document(auth.currentUser!!.email.toString()).collection(receiverEmail).add(chatHashMap).addOnSuccessListener {
            Toast.makeText(this@MessageActivity, "Başarılı", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this@MessageActivity, "Başarısız ${it.message}", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {

        }
    }

    private fun saveReceiverMessage(receiverEmail: String, chatHashMap: HashMap<String, Any>) {
        firestore.collection("Single Chat").document(receiverEmail).collection(auth.currentUser?.email.toString()).add(chatHashMap).addOnSuccessListener {
            Toast.makeText(this@MessageActivity, "Başarılı", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this@MessageActivity, "Başarısız ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if  (result.resultCode == Activity.RESULT_OK) {
                val intentFromResult = result.data
                selectedImage = intentFromResult?.data
                println("SELECTED : $selectedImage")
                selectedImage?.let {
                    uploadImage(selectedImage!!)
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if  (result) {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(galleryIntent)
            } else {
                Toast.makeText(this@MessageActivity, "Galeriye erişim için izin veriniz.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun uploadImage(imageURI: Uri) { // aynı fonksiyon bu çözüm üretilecek profil ile aynı
        val uuid = UUID.randomUUID()
        val imageName = "${auth.currentUser!!.email}$uuid.jpg"
        val reference = firebaseStorage.reference
        val imageReference = reference.child("Single Chat Images/$imageName")
        imageReference.putFile(imageURI).addOnSuccessListener {
            val uploadImageReference = firebaseStorage.reference.child("Single Chat Images").child(imageName)
            uploadImageReference.downloadUrl.addOnSuccessListener {
                val downloadUrl = it.toString()
                val user = auth.currentUser!!.email!!
                val message = activityMessageBinding.chatText.text.toString()
                val time = "${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())} ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}"
                val date = FieldValue.serverTimestamp()
                chatHashMap["from"] = user
                chatHashMap["to"] = email
                chatHashMap["message"] = ""
                chatHashMap["receiverName"] = username
                chatHashMap["date"] = date
                chatHashMap["time"] = time
                chatHashMap["chatImageURL"] = downloadUrl
                saveSenderMessage(email, chatHashMap)
                saveReceiverMessage(email,chatHashMap)
            }
        }.addOnFailureListener {
            Toast.makeText(this@MessageActivity,"${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMessage() {
        firestore.collection("Single Chat").document(auth.currentUser?.email.toString()).collection(email).orderBy("date", Query.Direction.ASCENDING).addSnapshotListener { value, error ->
            if (value != null) {
                if (value.isEmpty) {
                    Toast.makeText(this@MessageActivity, "Hoş geldiniz. Sohbeti siz başlatın!", Toast.LENGTH_SHORT).show()
                } else {
                    val documents = value.documents
                    messagesArrayList.clear()
                    messageAdapter.messages = messagesArrayList
                    for (document in documents) {
                        val from = document.get("from") as String
                        val message = document.get("message") as String
                        val to = document.get("to") as String
                        //val profileImageURL = document.get("profileImageURL") as String
                        val time = document.get("time") as String
                        val receiverName = document.get("receiverName") as String
                        val chatImageURL = document.get("chatImageURL") as String
                        val messageModal = MessageModal(message, from, to, receiverName, chatImageURL, time)
                        messagesArrayList.add(messageModal)
                        messageAdapter.messages = messagesArrayList
                    }
                    activityMessageBinding.chatText.setText("")
                }
            }
            messageAdapter.notifyDataSetChanged()
        }
    }


}