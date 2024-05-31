package com.example.financeback.utils

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.example.financeback.MainActivity
import com.example.financeback.classes.DatabaseHelper
import com.example.financeback.classes.IncomeInfo
import com.example.financeback.classes.User

class Utils {
    fun logout(context: Context, classDestination: Class<*>, extraParam: String) {
        val intent = Intent(context, classDestination)
        if (extraParam.isNotEmpty())
            intent.putExtra("Operation", extraParam)
        context.startActivity(intent)
        (context as Activity).finish()
    }
    /*
    * validateRegister precisa de ...
    * ... usuario nao pode ser vazio
    * ... nome nao pode ser vazio
    * ... senha precisa ter mais de 5 digitos
    * ... confirmar senha precisa ser igual a senha
    * */
    fun validateRegister(
        userName: String,
        fullName: String,
        password: String,
        confirmPassword: String
    ): Boolean {
//        if (User().checkUser(, mapOf(DatabaseHelper.USERS.COLUMN_USERNAME to userName)) != -1)
//            return false
        if (userName.trim().isEmpty() || fullName.trim().isEmpty())
            return false
        if (password.trim().length < 5 || confirmPassword != password)
            return false
        return true
    }

    fun validateLogin(
        username: String,
        password: String
    ): Boolean {
        return !(username.trim().isEmpty() || password.trim().length < 5)
    }

    fun validateIncome(
        incomeInfo: IncomeInfo
    ): Boolean {
        return !(incomeInfo.name.trim().isEmpty() || incomeInfo.value == 0.0 || incomeInfo.categoryID == -1)
    }
}