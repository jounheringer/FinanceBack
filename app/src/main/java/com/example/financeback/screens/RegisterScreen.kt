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

class RegisterScreen: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Register(activity = this)
        }
    }

    fun checkRegister(credentials: Credentials, context: Context): Boolean{
        val user = User()
        if (credentials.isNotEmpty()){
            if(user.saveUser(context, credentials).toInt() == -1){
                Toast.makeText(context, "Erro ao cadastrar usuario tente novamente.", Toast.LENGTH_SHORT).show()
                return false
            }
            val userID = user.checkUser(context, credentials)
            val int = Intent(context, MainActivity::class.java)
            int.putExtra("UserID", userID)
            context.startActivity(int)
            (context as Activity).finish()
            return true
        }
        Toast.makeText(context, "Campos obrigatorios devem ser preenchidos.", Toast.LENGTH_SHORT).show()
        return false
    }
}