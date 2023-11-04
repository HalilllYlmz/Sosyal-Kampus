package com.halil.myapplication.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.halil.myapplication.R
import com.halil.myapplication.adapter.UserAcceptAdapter
import com.halil.myapplication.databinding.ActivityUserAcceptBinding
import com.halil.myapplication.model.UserAcceptModal

class UserAcceptActivity : AppCompatActivity() {

    private lateinit var userAcceptAdapter: UserAcceptAdapter
    private lateinit var arrayList: ArrayList<UserAcceptModal>
    private lateinit var userAcceptModal: UserAcceptModal
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityUserAcceptBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserAcceptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        arrayList = ArrayList()

        with(binding) {
            val listItems = listOf<String>("Tüm Kayıt Listesi", "Onay Bekleyenler", "Onaylananlar", "Reddedilenler")
            val adapter = ArrayAdapter(this@UserAcceptActivity, R.layout.list_item, listItems)
            dropDownMenu.setAdapter(adapter)

            getAllUserApproval()

            dropDownMenu.onItemClickListener = object : AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    arrayList.clear()
                    binding.progressBar.visibility = View.VISIBLE
                    when (position) {
                        0 -> {
                            getAllUserApproval()
                        }
                        1 -> {
                            getUserApproval("none")
                        }
                        2 -> {
                            getUserApproval("true")
                        }
                        3 -> {
                            getUserApproval("false")
                        }
                    }
                }
            }
        }
    }

    private fun getUserApproval(filter: String) {
        firestore.collection("User Accept").whereEqualTo("approvalStatus",filter).get().addOnSuccessListener { documents ->
            if (documents != null) {
                for (document in documents) {
                    if (document.data!!["email"].toString() != auth.currentUser?.email.toString()) {
                        val fullName = "${document.data!!["name"].toString()} ${document.data!!["surname"].toString()}"
                        userAcceptModal = UserAcceptModal(document.data!!["email"].toString(), fullName,
                        document.data!!["state"].toString(), document.data!!["number"].toString(),
                        document.data!!["approvalStatus"].toString())
                        arrayList.add(userAcceptModal)
                    }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this@UserAcceptActivity, "${it.message}", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            binding.progressBar.visibility = View.GONE
            userAcceptAdapter = UserAcceptAdapter(this@UserAcceptActivity, filter, arrayList)
            binding.recyclerView.adapter = userAcceptAdapter
        }
    }

    private fun getAllUserApproval() {
        firestore.collection("User Accept").get().addOnSuccessListener { documents ->
            if (documents != null) {
                for (document in documents) {
                    if (document.data!!["email"].toString() != auth.currentUser?.email.toString()) {
                        val fullName = "${document.data!!["name"].toString()} ${document.data!!["surname"].toString()}"
                        userAcceptModal = UserAcceptModal(document.data!!["email"].toString(), fullName,
                            document.data!!["state"].toString(), document.data!!["number"].toString(),
                            document.data!!["approvalStatus"].toString())
                        arrayList.add(userAcceptModal)
                    }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this@UserAcceptActivity, "${it.message}", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {
            binding.progressBar.visibility = View.GONE
            userAcceptAdapter = UserAcceptAdapter(this@UserAcceptActivity,"all",arrayList)
            binding.recyclerView.adapter = userAcceptAdapter
        }
    }

}