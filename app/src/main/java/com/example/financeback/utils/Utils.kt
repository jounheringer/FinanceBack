package com.example.financeback.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.example.financeback.MainActivity

class Utils {
    fun logout(context: Context, classDestination: Class<*>, extraParam: String) {
        val intent = Intent(context, classDestination)
        if (extraParam.isNotEmpty())
            intent.putExtra("Operation", extraParam)
        context.startActivity(intent)
        (context as Activity).finish()
    }
}