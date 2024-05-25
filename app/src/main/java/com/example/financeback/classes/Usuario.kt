package com.example.financeback.classes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.core.database.getIntOrNull
import com.example.financeback.R
import com.example.financeback.screens.Credentials

data class UserInfo(
    var userName: String = "Usuario",
    var fullName: String = "Nome Completo",
    var userID: Int = 0,
    var iconImage: Int = R.drawable.user,
    var dateCreated: Int = 1041972582
)

class User {
    fun saveUser(context: Context, credentials: Credentials): Long{
        val values = ContentValues().apply {
            put(DatabaseHelper.USERS.COLUMN_USERNAME, credentials.login)
            put(DatabaseHelper.USERS.COLUMN_NAME, credentials.fullName)
            put(DatabaseHelper.USERS.COLUMN_PASSWORD, credentials.password)
        }

        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase

            return databaseCursor.insert(DatabaseHelper.USERS.TABLE_NAME, null, values)

        } catch (e: SQLiteException) {
            throw e
        }
    }

    fun checkUser(context: Context, credentials: Map<String, String>): Int {
        var where = ""
        var whereArgs = arrayOf<String>()
        var returnUser = -1
        var counter = 0

        credentials.forEach {cred ->
            where = where.plus(if (credentials.size > 1 && counter != (credentials.size -1)) "${cred.key} = ? AND " else "${cred.key} = ?")
            whereArgs += cred.value
            counter++
        }

        try {
            val databaseCursor = DatabaseHelper(context).readableDatabase

            databaseCursor.query(
                DatabaseHelper.USERS.TABLE_NAME,
                arrayOf(DatabaseHelper.USERS.COLUMN_ID),
                where,
                whereArgs,
                null,
                null,
                null,
                null,
            ).use {cursor ->
                if (cursor.moveToFirst()){
                    returnUser = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.USERS.COLUMN_ID))
                }
            }
            return returnUser
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun getCredentials(context: Context, credentials: Credentials): Credentials {
        val where = "${DatabaseHelper.USERS.COLUMN_USERNAME} = ?"
        val whereArgs = arrayOf(credentials.login)
        val userInfo = Credentials()

        try {
            val databaseHelper = DatabaseHelper(context).readableDatabase

            databaseHelper.query(
                DatabaseHelper.USERS.TABLE_NAME,
                null,
                where,
                whereArgs,
                null,
                null,
                null,
                null,
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    userInfo.login =
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USERS.COLUMN_USERNAME))
                    userInfo.password =
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USERS.COLUMN_PASSWORD))
                    userInfo.fullName =
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USERS.COLUMN_NAME))
                }
            }

            return userInfo

        }catch (e: SQLiteException){
            throw e
        }
    }

    fun getUserByID(context: Context, id: Int): UserInfo {
        val userInfo = UserInfo()

        try {
            val databaseHelper = DatabaseHelper(context).readableDatabase

            databaseHelper.query(
                DatabaseHelper.USERS.TABLE_NAME,
                null,
                "${DatabaseHelper.USERS.COLUMN_ID} = ?",
                arrayOf(id.toString()),
                null,
                null,
                null,
                null,
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    userInfo.userName =
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USERS.COLUMN_USERNAME))
                    userInfo.fullName =
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USERS.COLUMN_NAME))
                    userInfo.userID =
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.USERS.COLUMN_ID))

                }
            }

            return userInfo

        }catch (e: SQLiteException){
            throw e
        }
    }

    fun deleteUser(context: Context, userInfo: UserInfo): Boolean {
        val where = "${DatabaseHelper.USERS.COLUMN_USERNAME} = ? AND ${DatabaseHelper.USERS.COLUMN_ID} = ?"
        val whereArgs = arrayOf(userInfo.userName, userInfo.userID.toString())

         try{
            val databaseCursor = DatabaseHelper(context).writableDatabase

            return databaseCursor.delete(
                DatabaseHelper.USERS.TABLE_NAME,
                where,
                whereArgs) > 0
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun editUser(context: Context, userInfo: UserInfo): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.USERS.COLUMN_USERNAME, userInfo.userName)
            put(DatabaseHelper.USERS.COLUMN_NAME, userInfo.fullName)
        }

        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase

            return databaseCursor.update(DatabaseHelper.USERS.TABLE_NAME,
                values,
                "${DatabaseHelper.USERS.COLUMN_ID} = ?",
                arrayOf(userInfo.userID.toString())) > 0
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun changePassword(context: Context, userID: Int, password: String, oldPassword: String?): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.USERS.COLUMN_PASSWORD, password)
        }
        var where = "${DatabaseHelper.USERS.COLUMN_ID} = ?"
        val whereArgs = arrayOf(userID.toString())
        if (!oldPassword.isNullOrEmpty()){
            where = where.plus(" AND ${DatabaseHelper.USERS.COLUMN_PASSWORD} = ?")
            whereArgs + oldPassword
        }

        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase

            return databaseCursor.update(DatabaseHelper.USERS.TABLE_NAME,
                values,
                where,
                whereArgs) > 0
        }catch (e: SQLiteException){
            throw e
        }
    }
}