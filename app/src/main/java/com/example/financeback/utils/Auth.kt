package com.example.financeback.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.financeback.MainActivity
import com.example.financeback.classes.Usuario
import com.example.financeback.screens.Credentials
import com.example.financeback.screens.UserInfo

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

    fun checkRegister(userInfo: UserInfo, context: Context): Boolean{
        val user = Usuario()
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