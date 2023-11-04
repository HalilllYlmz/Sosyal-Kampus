package com.halil.myapplication.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.halil.myapplication.fragments.MessageFragment
import com.halil.myapplication.fragments.ProfileFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.halil.myapplication.R
import com.halil.myapplication.databinding.ActivityBaseBinding
import com.halil.myapplication.fragments.HomeFragment
import com.halil.myapplication.utils.SharedPreferencesHelper


class BaseActivity : AppCompatActivity() {

    private var userState = ""
    private var userClass = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var activityBaseBinding: ActivityBaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBaseBinding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(activityBaseBinding.root)

        val homeFragment = HomeFragment()
        loadFragment(homeFragment)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        //getState()
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this@BaseActivity)
        userState = sharedPreferencesHelper.getString("state","Öğrenci").toString()
        userClass = sharedPreferencesHelper.getString("class","Yazilim Mühendisliği 1. Sınıf").toString()


        with(activityBaseBinding) {

            val navigationView = findViewById<NavigationView>(R.id.navView)
            val shareNotification = navigationView.menu.findItem(R.id.notificationShareButton)
            val projectsShareButton = navigationView.menu.findItem(R.id.projectsShareButton)
            val acceptButton = navigationView.menu.findItem(R.id.userAcceptButton)

            if (userState != "Admin") {
                if (userState != "Yönetici") {
                    shareNotification.isVisible = false
                    projectsShareButton.isVisible = false
                    acceptButton.isVisible = false
                } else {
                    acceptButton.isVisible = false
                }
            }



            navigationView.setNavigationItemSelectedListener {
                when(it.itemId) {
                    R.id.profilesButton -> {
                        val intent = Intent(this@BaseActivity, ProfilesActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.notificationButton -> {
                        val intent = Intent(this@BaseActivity, AnnounceActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.notificationShareButton -> {
                        val intent = Intent(this@BaseActivity, ShareAnnounceActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.projectsButton -> {
                        val intent = Intent(this@BaseActivity, ProjectsActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.projectsShareButton -> {
                        val intent = Intent(this@BaseActivity, ShareProjectActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.userAcceptButton -> {
                        val intent = Intent(this@BaseActivity, UserAcceptActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.generalChat -> {
                        if  (userClass != "Yazilim Mühendisliği 1. Sınıf") {
                            Toast.makeText(this@BaseActivity,"Bu sohbete yalnızca 1. Sınıf öğrenciler katılabilir.",Toast.LENGTH_SHORT).show()
                        } else {
                            intentToChat("Yazilim Mühendisliği 1. Sınıf")
                        }
                    }
                    R.id.studentChat -> {
                        if  (userClass != "Yazilim Mühendisliği 2. Sınıf") {
                            Toast.makeText(this@BaseActivity,"Bu sohbete yalnızca 2. Sınıf öğrenciler katılabilir.",Toast.LENGTH_SHORT).show()
                        } else {
                            intentToChat("Yazilim Mühendisliği 2. Sınıf")
                        }
                    }
                    R.id.graduateChat -> {
                        if  (userClass != "Yazilim Mühendisliği 3. Sınıf") {
                            Toast.makeText(this@BaseActivity,"Bu sohbete yalnızca 3. Sınıf öğrenciler katılabilir.",Toast.LENGTH_SHORT).show()
                        } else {
                            intentToChat("Yazilim Mühendisliği 3. Sınıf")
                        }
                    }
                    R.id.managerChat -> {
                        if  (userClass != "Yazilim Mühendisliği 4. Sınıf") {
                            Toast.makeText(this@BaseActivity,"Bu sohbete yalnızca 4. Sınıf öğrenciler katılabilir.",Toast.LENGTH_SHORT).show()
                        } else {
                            intentToChat("Yazilim Mühendisliği 4. Sınıf")
                        }
                    }

                }
                activityBaseBinding.drawerLayout.closeDrawer(GravityCompat.START)
                return@setNavigationItemSelectedListener true
            }

            drawerImplement()

            bottomNavigation.setOnItemSelectedListener {
                when(it.itemId) {
                    R.id.homeButton -> {
                        loadFragment(homeFragment)
                    }
                    R.id.messageButton -> {
                        val messageFragment = MessageFragment()
                        loadFragment(messageFragment)
                    }
                    R.id.profileButton -> {
                        val profileFragment = ProfileFragment()
                        loadFragment(profileFragment)
                    }
                }
                return@setOnItemSelectedListener true
            }



            logoutButton.setOnClickListener {
                logout()
            }

            toolbarAnnounce.setOnClickListener {
                val intent = Intent(this@BaseActivity, AnnounceActivity::class.java)
                startActivity(intent)
            }


        }

    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (activityBaseBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            activityBaseBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.activityBaseFrameLayout, fragment,null).commit()
    }

    private fun drawerImplement() {
        val toolbar: Toolbar = findViewById(R.id.toolbar1)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this@BaseActivity, activityBaseBinding.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        activityBaseBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun logout() {
        val auth: FirebaseAuth = Firebase.auth
        auth.signOut()
        val intent = Intent(this@BaseActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun intentToChat(state: String) {
        val intent = Intent(this@BaseActivity, ChatActivity::class.java)
        intent.putExtra("User Class", state)
        startActivity(intent)
    }

    private fun getState() {
        firestore.collection("Users").document(auth.currentUser?.email.toString()).get().addOnSuccessListener {
            if (it != null) {
                userState = it.data!!["state"].toString()
            }
        }.addOnFailureListener {
            userState = "Öğrenci"
            Toast.makeText(this@BaseActivity, "Kullanıcı durumu çekilemedi.", Toast.LENGTH_SHORT).show()
        }
    }


}