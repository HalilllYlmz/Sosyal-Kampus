package com.halil.myapplication.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.halil.myapplication.databinding.FragmentCreateAccSecondBinding
import com.halil.myapplication.utils.SharedPreferencesHelper
import com.halil.myapplication.view.MainActivity

class CreateAccSecondFragment : Fragment() {

    private var userName = ""
    private var userSurname = ""
    private var userNumber = ""
    private var userState = ""
    private var userPassword = ""
    private var userMail = ""
    private var userPhone = ""
    private var userClass = ""
    private var userProfession = ""
    private var userBiography = ""

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var userHashMap: HashMap<String, String>
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var fragmentCreateAccSecondBinding: FragmentCreateAccSecondBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentCreateAccSecondBinding = FragmentCreateAccSecondBinding.inflate(layoutInflater)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()
        userHashMap = HashMap()
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext())


        userName = arguments?.getString("Name").toString()
        userSurname = arguments?.getString("Surname").toString()
        userNumber = arguments?.getString("Number").toString()
        userState = arguments?.getString("State").toString()
        userClass = arguments?.getString("Class").toString()
        userPassword = arguments?.getString("Password").toString()

        with(fragmentCreateAccSecondBinding) {
            signUpButton.setOnClickListener {
                if (mailText.text.isNullOrEmpty() || phoneText.text.isNullOrEmpty() ||
                    professionText.text.isNullOrEmpty() || biografiText.text.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Boş alanları doldurunuz!", Toast.LENGTH_SHORT).show()
                } else {
                    userMail = mailText.text.toString()
                    userPhone = phoneText.text.toString()
                    userProfession = professionText.text.toString()
                    userBiography = biografiText.text.toString()
                    userHashMap = hashMapOf<String,String>(
                        "name" to userName,
                        "surname" to userSurname,
                        "number" to userNumber,
                        "state" to userState,
                        "class" to userClass,
                        "email" to userMail,
                        "phone" to userPhone,
                        "profession" to userProfession,
                        "biography" to userBiography,
                        "imageURL" to ""
                    )
                    userRegistration(userMail, userPassword)
                }
            }
        }

        return fragmentCreateAccSecondBinding.root
    }


    private fun userRegistration(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if  (task.isSuccessful) {
                saveUser(userHashMap)
                /*saveSharedPreferences()
                val intent = Intent(requireContext(), BaseActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()*/
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUser(hashMap: HashMap<String,String>) {
        firestore.collection("Users").document(userMail).set(hashMap).addOnSuccessListener {
            val userInformation = hashMapOf<String, Any>(
                "email" to userMail,
                "name" to userName,
                "surname" to userSurname,
                "state" to userState,
                "class" to userClass,
                "number" to userNumber,
                "approvalStatus" to "none"
            )
            userDisable(userMail, userInformation)
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"${it.message}",Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveSharedPreferences() {
        sharedPreferencesHelper.putString("name", userName)
        sharedPreferencesHelper.putString("surname", userSurname)
        sharedPreferencesHelper.putString("number", userNumber)
        sharedPreferencesHelper.putString("state", userState)
        sharedPreferencesHelper.putString("email", userMail)
        sharedPreferencesHelper.putString("phone", userPhone)
        sharedPreferencesHelper.putString("profession", userProfession)
        sharedPreferencesHelper.putString("biography", userBiography)
        sharedPreferencesHelper.putString("imageURL", "")
    }

    private fun userDisable(email: String, userInformation: HashMap<String, Any>) {
        firestore.collection("User Accept").document(email).set(userInformation).addOnSuccessListener {
            Toast.makeText(requireContext(), "Kaydınız başarıyla oluşturuldu. Onaylanınca sisteme giriş yapabilirsiniz.", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }.addOnFailureListener {
            Toast.makeText(requireContext(),"${it.message}",Toast.LENGTH_SHORT).show()
        }
    }

}