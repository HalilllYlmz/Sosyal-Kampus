package com.halil.myapplication.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.halil.myapplication.fragments.GeneralChatFragment
import com.halil.myapplication.R
import com.halil.myapplication.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private var openChat = ""
    private lateinit var bundle: Bundle
    private lateinit var generalChatFragment: GeneralChatFragment
    private lateinit var activityChatBinding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityChatBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(activityChatBinding.root)

        openChat = intent.getStringExtra("User Class").toString()
        bundle = Bundle()
        generalChatFragment = GeneralChatFragment()

        when(openChat) {
            "Yazilim Mühendisliği 1. Sınıf" -> {
                bundle.putString("Chat Type", "BM 1")
                generalChatFragment.arguments = bundle
                loadFragment(generalChatFragment)
            }
            "Yazilim Mühendisliği 2. Sınıf" -> {
                bundle.putString("Chat Type", "BM 2")
                generalChatFragment.arguments = bundle
                loadFragment(generalChatFragment)
            }
            "Yazilim Mühendisliği 3. Sınıf" -> {
                bundle.putString("Chat Type", "BM 3")
                generalChatFragment.arguments = bundle
                loadFragment(generalChatFragment)
            }
            "Yazilim Mühendisliği 4. Sınıf" -> {
                bundle.putString("Chat Type", "BM 4")
                generalChatFragment.arguments = bundle
                loadFragment(generalChatFragment)
            }
        }

    }


    private fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.chatFrameLayout, fragment, null).commit()
    }



}