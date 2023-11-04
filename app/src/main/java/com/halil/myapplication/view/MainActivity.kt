package com.halil.myapplication.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.halil.myapplication.databinding.ActivityMainBinding
import com.halil.myapplication.utils.SharedPreferencesHelper

class MainActivity : AppCompatActivity() {


    private var userName = ""
    private var userSurname = ""
    private var userState = ""
    private var userMail = ""
    private var userPhone = ""
    private var userImage = ""
    private var userClass = ""
    private var userProfession = ""
    private var userBiography = ""

    private var userEmail = ""
    private var userPassword = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

        auth = Firebase.auth
        firestore = Firebase.firestore
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this@MainActivity)


        with(activityMainBinding) {

            loginProgress.visibility = View.GONE

            loginButton.setOnClickListener {
                userEmail = userEmailText.text.toString()
                userPassword = userPasswordText.text.toString()
                if (userEmail.isEmpty() || userPassword.isEmpty()) {
                    Toast.makeText(this@MainActivity, "Boş alan bırakmayınız!", Toast.LENGTH_SHORT).show()
                } else {
                    loginProgress.visibility = View.VISIBLE
                    loginUser(email = userEmail, password = userPassword)
                }
            }

            createAccount.setOnClickListener {
                val intent = Intent(this@MainActivity, CreateAccountActivity::class.java)
                startActivity(intent)
            }

        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            checkCurrentApprovalStatus(currentUser.email!!)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                checkLoginApprovalStatus(email)
            }
        }.addOnFailureListener {
            Toast.makeText(this@MainActivity, "${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserData(userEmail: String) {
        firestore.collection("Users").document(userEmail).get().addOnSuccessListener {document ->
            if (document != null) {
                userName = document.data!!["name"].toString()
                userSurname = document.data!!["surname"].toString()
                userMail = document.data!!["email"].toString()
                userPhone = document.data!!["phone"].toString()
                userProfession = document.data!!["profession"].toString()
                userBiography = document.data!!["biography"].toString()
                userState = document.data!!["state"].toString()
                userClass = document.data!!["class"].toString()
                userImage = document.data!!["imageURL"].toString()

                sharedPreferencesHelper.putString("name", userName)
                sharedPreferencesHelper.putString("surname", userSurname)
                sharedPreferencesHelper.putString("state", userState)
                sharedPreferencesHelper.putString("class", userClass)
                sharedPreferencesHelper.putString("email", userMail)
                sharedPreferencesHelper.putString("phone", userPhone)
                sharedPreferencesHelper.putString("profession", userProfession)
                sharedPreferencesHelper.putString("biography", userBiography)
                sharedPreferencesHelper.putString("imageURL", userImage)
                val intent = Intent(this@MainActivity, BaseActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Veriler kaydedilemedi. ${it.message}", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener {

        }
    }

    private fun checkCurrentApprovalStatus(email: String) {
        firestore.collection("User Accept").document(email).get().addOnSuccessListener {
            if  (it.data!!["approvalStatus"].toString().toBoolean()) {
                val intent = Intent(this@MainActivity, BaseActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                activityMainBinding.loginProgress.visibility = View.GONE
                Toast.makeText(this@MainActivity, "Kullanıcı kaydınız henüz onaylanmadı.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this@MainActivity, "Kullanıcı bilgileri alınamadı. ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkLoginApprovalStatus(email: String) {
        firestore.collection("User Accept").document(email).get().addOnSuccessListener {
            if  (it.data!!["approvalStatus"].toString().toBoolean()) {
                getUserData(email)
            } else {
                activityMainBinding.loginProgress.visibility = View.GONE
                Toast.makeText(this@MainActivity, "Kullanıcı kaydınız henüz onaylanmadı.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this@MainActivity, "Kullanıcı bilgileri alınamadı. ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }











}