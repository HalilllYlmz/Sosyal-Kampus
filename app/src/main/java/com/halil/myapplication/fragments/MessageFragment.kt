package com.halil.myapplication.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.myapplication.adapter.MessageListAdapter
import com.halil.myapplication.databinding.FragmentMessageBinding
import com.halil.myapplication.model.ListMessageModal

class MessageFragment : Fragment() {

    private var imageURL = ""
    private var nameSurname = ""
    private var userEmail = ""

    private lateinit var messageListAdapter: MessageListAdapter
    private lateinit var listMessageModal: ListMessageModal
    private lateinit var listArray: ArrayList<ListMessageModal>
    private lateinit var new: ArrayList<String>
    private lateinit var singleChatList: ArrayList<String>
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var fragmentMessageBinding: FragmentMessageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentMessageBinding = FragmentMessageBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        singleChatList = ArrayList()
        new = ArrayList()
        listArray = ArrayList()
        fragmentMessageBinding.messageProgressBar.visibility = View.VISIBLE

        getList()

        return fragmentMessageBinding.root
    }

    private fun getList() {
        firestore.collection("Single Chat").get().addOnSuccessListener {document ->
            if (document != null) {
                for (documents in document) {
                    singleChatList.add(documents.id)
                }
            } else {
                Toast.makeText(requireContext(), "Kayıt bulunamadı", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Bir hata meydana geldi. ${it.message}", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            for (i in 0 until singleChatList.size) {
                control(singleChatList[i])
            }
        }
    }

    private fun control(check: String) {
        firestore.collection("Single Chat").document(auth.currentUser?.email.toString()).collection(check).get().addOnSuccessListener { documents ->
            if  (!documents.isEmpty) {
                new.add(check)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Bir hata meydana geldi. ${it.message}", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            if (new.isNotEmpty()) {
                for (i in 0 until new.size) {
                    getUserData(new[i])
                }
            } else {
                fragmentMessageBinding.messageProgressBar.visibility = View.GONE
            }
        }
    }

    private fun getUserData(email:String) {
        firestore.collection("Users").document(email).get().addOnSuccessListener {
            imageURL = it.data!!["imageURL"].toString()
            nameSurname = "${it.data!!["name"]} ${it.data!!["surname"]}"
            userEmail = it.data!!["email"].toString()
            listMessageModal = ListMessageModal(imageURL,email, nameSurname)
            if (!listArray.contains(listMessageModal)) {
                listArray.add(listMessageModal)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Bir hata meydana geldi. ${it.message}", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            fragmentMessageBinding.messageProgressBar.visibility = View.GONE
            messageListAdapter = MessageListAdapter(requireContext(), listArray)
            fragmentMessageBinding.messageList.adapter = messageListAdapter
        }
    }



}