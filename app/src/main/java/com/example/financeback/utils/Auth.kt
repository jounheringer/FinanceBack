package com.example.financeback.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.financeback.MainActivity


data class Credentials(
    var login: String = "",
    var password: String = "",
    var remember: Boolean = false
){
    fun isNotEmpty(): Boolean {
        return login.trim().isNotEmpty() && password.trim().isNotEmpty()
    }
}

//TODO add more auth functions
class Auth {
    fun checkCredentials(credentials: Credentials, context: Context): Boolean {
        if (credentials.isNotEmpty() && credentials.login == "admin") {
            context.startActivity(Intent(context, MainActivity::class.java))
            (context as Activity).finish()
            return true
        }
        Toast.makeText(context, "Login ou senha incorretos.", Toast.LENGTH_SHORT).show()
        return false
    }
}