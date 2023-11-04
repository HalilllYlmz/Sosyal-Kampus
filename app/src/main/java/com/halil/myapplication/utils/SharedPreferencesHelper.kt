package com.halil.myapplication.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesHelper private constructor(context: Context){

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("com.muhammetsenses.ybssosyalplatform", Context.MODE_PRIVATE)

    companion object {
        private var instance: SharedPreferencesHelper? = null
        @Synchronized
        fun getInstance(context: Context): SharedPreferencesHelper {
            if (instance == null) {
                instance = SharedPreferencesHelper(context)
            }
            return instance as SharedPreferencesHelper
        }
    }

    fun putString(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key,value)
        editor.apply()
    }

    fun getString(key: String, defaultValue: String): String? {
        return sharedPreferences.getString(key,defaultValue)
    }


}