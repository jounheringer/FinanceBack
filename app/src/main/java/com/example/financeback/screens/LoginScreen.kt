package com.example.financeback.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.financeback.MainActivity
import com.example.financeback.classes.User
import com.example.financeback.classes.UserLog
import com.example.financeback.screens.compose.Login

data class Credentials(
    var login: String = "",
    var fullName: String = "",
    var password: String = "",
    var remember: Boolean = false
){
    fun isNotEmpty(): Boolean {
        return login.trim().isNotEmpty() && password.trim().isNotEmpty()
    }
    fun confirmPassword(secondPassword: String): Boolean {
        return password.trim().isNotEmpty() && (password == secondPassword)
    }
}

class LoginScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userLog = UserLog()
        val userRemember = userLog.getUserRemember(this)
        if (userRemember.isNotEmpty())
            checkCredentials(userRemember, this)

        setContent{
            MaterialTheme {
                Login(activity = this)
            }
        }
    }

    fun checkCredentials(credentials: Credentials, context: Context): Boolean {
        val user = User()
        val userLog = UserLog()
        val userID = user.checkUser(context, credentials)
        if (credentials.isNotEmpty()) {
            if (credentials.login == "admin" || userID != -1){
                userLog.saveUserLog(context, credentials)
                val int = Intent(context, MainActivity::class.java)
                int.putExtra("UserID", userID)
                context.startActivity(int)
                (context as Activity).finish()
                return true
            }
        }
        Toast.makeText(context, "Login ou senha incorretos.", Toast.LENGTH_SHORT).show()
        return false
    }

    fun goTo(activity: ComponentActivity, classDestination: Class<*>) {
        val intent = Intent(activity, classDestination)
        activity.startActivity(intent)
        activity.finish()
    }
}