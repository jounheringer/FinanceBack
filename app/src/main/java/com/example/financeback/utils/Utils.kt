package com.example.financeback.utils

import android.app.Activity
import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.financeback.MainActivity
import com.example.financeback.classes.DatabaseHelper
import com.example.financeback.classes.IncomeInfo
import com.example.financeback.classes.User
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

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

    fun getFileFromURI(context: Context,
                       contentURI: Uri,
                       nameToFile: String = "temp",
                       customPath: String? = null): File {
        val fileExtension = this.getFileExtension(context, contentURI)
        val fileName = nameToFile + if (fileExtension != null) ".$fileExtension" else ""

        val tempFile: File = if(customPath != null){
            val path = File(Environment.getExternalStorageDirectory().toString() + "/$customPath")
            if (!path.exists())
                if(!path.mkdirs())
                    Toast.makeText(context, "Erro ao criar pasta", Toast.LENGTH_SHORT).show()
            File(path, fileName)
        } else {
            File(context.cacheDir, fileName)
        }

        tempFile.createNewFile()

        try {
            val oStream = FileOutputStream(tempFile)
            val inputStream = context.contentResolver.openInputStream(contentURI)

            inputStream?.let {
                this.copy(inputStream, oStream)
            }

            oStream.flush()
        }catch (e: Exception){
            throw e
        }

        return tempFile
    }

    private fun getFileExtension(context: Context, uri: Uri): String? {
        val fileType: String? = context.contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    }

    private fun copy(source: InputStream, target: OutputStream) {
        val buffer = ByteArray(8192)
        var length: Int
        while (source.read(buffer).also { length = it } > 0)
            target.write(buffer, 0, length)
    }
}