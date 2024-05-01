package com.example.financeback.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.financeback.MainActivity
import com.example.financeback.classes.User
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

    fun checkRegister(userInfo: UserInfo, context: Context): Boolean{
        val user = User()
        if (userInfo.isNotEmpty()){
            if(user.saveUser(context, userInfo).toInt() == -1){
                Toast.makeText(context, "Erro ao cadastrar usuario tente novamente.", Toast.LENGTH_SHORT).show()
                return false
            }
            context.startActivity(Intent(context, MainActivity::class.java))
            (context as Activity).finish()
            return true
        }
        Toast.makeText(context, "Campos obrigatorios devem ser preenchidos.", Toast.LENGTH_SHORT).show()
        return false
    }
}