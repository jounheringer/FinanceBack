package com.example.financeback.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.financeback.screens.compose.Register

data class UserInfo(
    var userName: String = "",
    var fullName: String = "",
    var password: String = ""
) {
    fun isNotEmpty(): Boolean {
        return userName.trim().isNotEmpty() && fullName.trim().isNotEmpty() && password.trim().isNotEmpty()
    }

    fun confirmPassword(secondPassword: String): Boolean {
        return password.trim().isNotEmpty() && (password == secondPassword)
    }
}

class RegisterScreen: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Register(activity = this)
        }
    }
}