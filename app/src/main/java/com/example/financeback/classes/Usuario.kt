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
    var iconImage: Int = R.drawable.user
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

    fun checkUser(context: Context, credentials: Credentials): Int {
        val where = "${DatabaseHelper.USERS.COLUMN_USERNAME} = ? AND ${DatabaseHelper.USERS.COLUMN_PASSWORD} = ?"
        val whereArgs = arrayOf(credentials.login, credentials.password)
        var returnUser: Int

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
                returnUser = if (cursor.moveToFirst()){
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.USERS.COLUMN_ID))
                } else {
                    -1
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
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.USERS.COLUMN_PASSWORD))

                }
            }

            return userInfo

        }catch (e: SQLiteException){
            throw e
        }
    }
}