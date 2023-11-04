package com.halil.myapplication.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.halil.myapplication.fragments.CreateAccFirstFragment
import com.halil.myapplication.R
import com.halil.myapplication.databinding.ActivityCreateAccountBinding


class CreateAccountActivity : AppCompatActivity() {


    private lateinit var activityCreateAccountBinding: ActivityCreateAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCreateAccountBinding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(activityCreateAccountBinding.root)

        with(activityCreateAccountBinding) {
            val createAccFirstFragment = CreateAccFirstFragment()
            loadFragment(createAccFirstFragment)
        }

    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment,null).commit()
    }


}