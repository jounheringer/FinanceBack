package com.example.financeback.utils

import android.content.Intent
import com.example.financeback.MainActivity

class Utils {
    fun logout(activity: MainActivity, classDestination: Class<*>) {
        val intent = Intent(activity, classDestination)
        activity.startActivity(intent)
        activity.finish()
    }
}