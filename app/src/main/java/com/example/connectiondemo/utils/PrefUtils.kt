package com.example.connectiondemo.utils

import android.content.Context
import android.content.SharedPreferences

class PrefUtils {
    companion object {
        private const val PREF_NAME: String = "pref_name"
        private const val PREF_STATUS = "pref_status"

        private fun getSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }

        fun clearSharedPreferences(context: Context) {
            val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.clear()
            editor.apply()
        }

        fun setStatus(context: Context, status: Boolean) {
            val editor = getSharedPreferences(context).edit()
            editor.putBoolean(PREF_STATUS, status)
            editor.apply()
            editor.commit()
        }

        fun getStatus(context: Context): Boolean {
            return getSharedPreferences(context).getBoolean(PREF_STATUS, false)
        }

    }
}