package com.example.financeback.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.financeback.screens.compose.Login

data class Credentials(
    var login: String = "",
    var password: String = "",
    var remember: Boolean = false
){
    fun isNotEmpty(): Boolean {
        return login.trim().isNotEmpty() && password.trim().isNotEmpty()
    }
}

class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            MaterialTheme {
                Login(activity = this)
            }
        }
    }

    fun goTo(activity: ComponentActivity, classDestination: Class<*>) {
        val intent = Intent(activity, classDestination)
        activity.startActivity(intent)
        activity.finish()
    }
}