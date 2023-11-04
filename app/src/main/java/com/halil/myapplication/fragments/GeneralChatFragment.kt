package com.halil.myapplication.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.halil.myapplication.adapter.ChatAdapter
import com.halil.myapplication.databinding.FragmentGeneralChatBinding
import com.halil.myapplication.model.ChatModal
import com.halil.myapplication.utils.SharedPreferencesHelper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class GeneralChatFragment : Fragment() {

    private var profileImageURL = ""
    private var fullName = ""
    private var chatType = ""

    private lateinit var chatAdapter: ChatAdapter
    private var messagesArrayList = arrayListOf<ChatModal>()

    private var selectedImage: Uri? = null
    private lateinit var dataMap: HashMap<String, Any>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var fragmentGeneralChatBinding: FragmentGeneralChatBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentGeneralChatBinding = FragmentGeneralChatBinding.inflate(layoutInflater)

        chatType = arguments?.getString("Chat Type","BM 1").toString()
        changeChatText(chatType)

        auth = Firebase.auth
        firestore = Firebase.firestore
        firebaseStorage = Firebase.storage
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext())
        profileImageURL = sharedPreferencesHelper.getString("imageURL","").toString()
        fullName = "${sharedPreferencesHelper.getString("name","")} ${sharedPreferencesHelper.getString("surname","")}"

        registerLauncher()


        return fragmentGeneralChatBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        messagesArrayList = ArrayList()
        chatAdapter = ChatAdapter(requireContext())
        dataMap = HashMap<String, Any>()
        fragmentGeneralChatBinding.chatRecyclerView.adapter = chatAdapter


        with(fragmentGeneralChatBinding) {

            sendButton.setOnClickListener {
                if (!chatText.text.isNullOrEmpty()) {
                    val user = auth.currentUser!!.email!!
                    val message = chatText.text.toString()
                    val time = "${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())} ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}"
                    val date = FieldValue.serverTimestamp()
                    dataMap["user"] = user
                    dataMap["message"] = message
                    dataMap["date"] = date
                    dataMap["time"] = time
                    dataMap["fullName"] = fullName
                    dataMap["profileImageURL"] = profileImageURL
                    dataMap["chatImageURL"] = ""
                    saveMessage(chatType, dataMap)
                }
            }

            galleryButton.setOnClickListener {
                if  (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if  (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
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

        getMessage(chatType)

    }

    private fun saveMessage(chatType: String, dataMap: HashMap<String, Any>) {
        firestore.collection(chatType).add(dataMap).addOnSuccessListener {
            fragmentGeneralChatBinding.chatText.setText("")
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            fragmentGeneralChatBinding.chatText.setText("")
        }
    }

    private fun getMessage(chatType: String) {
        firestore.collection(chatType).orderBy("date", Query.Direction.ASCENDING).addSnapshotListener { value, error ->
            if (value != null) {
                if (value.isEmpty) {
                    Toast.makeText(requireContext(), "Hoş geldiniz. Sohbeti siz başlatın!", Toast.LENGTH_SHORT).show()
                } else {
                    val documents = value.documents
                    messagesArrayList.clear()
                    chatAdapter.messages = messagesArrayList
                    for (document in documents) {
                        val user = document.get("user") as String
                        val message = document.get("message") as String
                        val profileImageURL = document.get("profileImageURL") as String
                        val time = document.get("time") as String
                        val fullName = document.get("fullName") as String
                        val chatImageURL = document.get("chatImageURL") as String
                        val chatModal = ChatModal(user = user, fullName = fullName, message = message, profileImageURL = profileImageURL, time = time, chatImageURL = chatImageURL)
                        messagesArrayList.add(chatModal)
                        chatAdapter.messages = messagesArrayList
                    }
                    fragmentGeneralChatBinding.chatText.setText("")
                }
            }
            chatAdapter.notifyDataSetChanged()
        }
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if  (result.resultCode == Activity.RESULT_OK) {
                val intentFromResult = result.data
                selectedImage = intentFromResult?.data
                println("SELECTED : $selectedImage")
                selectedImage?.let {
                    uploadImage(chatType, selectedImage!!)
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if  (result) {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(galleryIntent)
            } else {
                Toast.makeText(requireContext(), "Galeriye erişim için izin veriniz.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun uploadImage(chatType: String,imageURI: Uri) { // aynı fonksiyon bu çözüm üretilecek profil ile aynı
        val uuid = UUID.randomUUID()
        val imageName = "${auth.currentUser!!.email}$uuid.jpg"
        val reference = firebaseStorage.reference
        val imageReference = reference.child("$chatType Images/$imageName")
        imageReference.putFile(imageURI).addOnSuccessListener {
            val uploadImageReference = firebaseStorage.reference.child("$chatType Images").child(imageName)
            uploadImageReference.downloadUrl.addOnSuccessListener {
                val downloadUrl = it.toString()
                val user = auth.currentUser!!.email!!
                val message = fragmentGeneralChatBinding.chatText.text.toString()
                val time = "${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())} ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}"
                val date = FieldValue.serverTimestamp()
                dataMap["user"] = user
                dataMap["message"] = ""
                dataMap["date"] = date
                dataMap["time"] = time
                dataMap["fullName"] = fullName
                dataMap["profileImageURL"] = profileImageURL
                dataMap["chatImageURL"] = downloadUrl
                saveMessage(chatType, dataMap)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"${it.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun changeChatText(chatType: String) {
        fragmentGeneralChatBinding.chatTypeText.text = when(chatType) {
            "BM 1" -> "BM 1"
            "BM 2" -> "BM 2"
            "BM 3" -> "BM 3"
            "BM 4" -> "BM 4"
            else -> "Sohbet"
        }
    }


}