package com.halil.myapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.halil.myapplication.adapter.AnnounceAdapter
import com.halil.myapplication.databinding.ActivityAnnounceBinding
import com.halil.myapplication.model.AnnounceModal

class AnnounceActivity : AppCompatActivity() {

    private var time = ""
    private var email = ""
    private var message = ""
    private var fullName = ""
    private var announceImageURL = ""
    private var profileImageURL = ""

    private lateinit var announceAdapter: AnnounceAdapter
    private lateinit var announceArray: ArrayList<AnnounceModal>
    private lateinit var announceModal: AnnounceModal
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var activityAnnounceBinding: ActivityAnnounceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAnnounceBinding = ActivityAnnounceBinding.inflate(layoutInflater)
        setContentView(activityAnnounceBinding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        announceArray = ArrayList()
        announceAdapter = AnnounceAdapter(this@AnnounceActivity, announceArray)

        with(activityAnnounceBinding) {
            getAnnounce()

            backButton.setOnClickListener {
                val intent = Intent(this@AnnounceActivity, BaseActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

        }

    }


    private fun getAnnounce() {
        firestore
            .collection("Announces")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {     document ->
            if (document != null) {
                for (documents in document) {
                    email = documents.data["user"].toString()
                    fullName = documents.data["fullName"].toString()
                    message = documents.data["message"].toString()
                    profileImageURL = documents.data["profileImageURL"].toString()
                    time = documents.data["time"].toString()
                    announceImageURL = documents.data["announceImageURL"].toString()
                    announceModal = AnnounceModal(email, fullName, message, profileImageURL, time, announceImageURL)
                    announceArray.add(announceModal)
                }
            }

        }.addOnFailureListener {
            Toast.makeText(this@AnnounceActivity, "Duyurular yüklenemedi ${it.message}", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            announceAdapter = AnnounceAdapter(this@AnnounceActivity, announceArray)
            activityAnnounceBinding.announceRecyclerView.adapter = announceAdapter
        }
    }






}