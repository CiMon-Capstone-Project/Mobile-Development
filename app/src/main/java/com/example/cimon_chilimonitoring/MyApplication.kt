package com.example.cimon_chilimonitoring

import android.app.Application
import android.content.Context


class MyApplication : Application() {
    override fun onTerminate() {
        super.onTerminate()
//        clearSharedPreferences()
    }

    private fun clearSharedPreferences() {
        val sharedPreferences = getSharedPreferences("chat_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}