package com.halil.myapplication.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.myapplication.R
import com.halil.myapplication.adapter.ProfilesAdapter
import com.halil.myapplication.databinding.ActivityProfilesBinding
import com.halil.myapplication.model.ProfilesModal

class ProfilesActivity : AppCompatActivity() {

    private var state = "Öğrenci"
    private lateinit var profilesModal: ProfilesModal
    private lateinit var profilesArrayList: ArrayList<ProfilesModal>
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var activityProfilesBinding: ActivityProfilesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProfilesBinding = ActivityProfilesBinding.inflate(layoutInflater)
        setContentView(activityProfilesBinding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        profilesArrayList = ArrayList()

        with(activityProfilesBinding) {

            val listItems = listOf<String>("Öğrenci", "Yönetici")
            val adapter = ArrayAdapter(this@ProfilesActivity, R.layout.list_item, listItems)
            dropDownMenu.setAdapter(adapter)

            val profilesAdapter = ProfilesAdapter(this@ProfilesActivity, profilesArrayList)
            profilesRecyclerView.adapter = profilesAdapter

            getUsers(state)

            dropDownMenu.onItemClickListener = object : OnItemSelectedListener,
                AdapterView.OnItemClickListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    getUsers(listItems[position])
                    activityProfilesBinding.profilesRecyclerView.visibility = View.INVISIBLE
                    profilesArrayList.clear()
                    activityProfilesBinding.progressBar.visibility = View.VISIBLE
                }
            }
        }

    }


    private fun getUsers(state: String) {
        firestore.collection("Users").whereEqualTo("state", state).get()
            .addOnSuccessListener { document ->
                for (documents in document) {
                    if (documents.data!!["email"].toString() != auth.currentUser!!.email.toString()) {
                        profilesModal = ProfilesModal(
                            documents.data!!["imageURL"].toString(),
                            documents.data!!["email"].toString(),
                            documents.data!!["name"].toString(),
                            documents.data!!["surname"].toString(),
                            documents.data!!["state"].toString()
                        )
                        profilesArrayList.add(profilesModal)
                    }
                }
                activityProfilesBinding.profilesRecyclerView.adapter?.notifyDataSetChanged()
            }.addOnFailureListener {
            Toast.makeText(
                this@ProfilesActivity,
                "Kullanıcılar yüklenirken hata oluştu. ${it.message}",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnCompleteListener {
            activityProfilesBinding.profilesRecyclerView.visibility = View.VISIBLE
            activityProfilesBinding.progressBar.visibility = View.GONE
        }
    }


}



