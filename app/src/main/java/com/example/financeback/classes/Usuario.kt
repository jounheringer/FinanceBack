package com.example.financeback.classes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import com.example.financeback.screens.UserInfo

class Usuario {

    fun saveUser(context: Context, userInfo: UserInfo): Long{
        val values = ContentValues().apply {
            put(DatabaseHelper.USERS.COLUMN_USERNAME, userInfo.userName)
            put(DatabaseHelper.USERS.COLUMN_NAME, userInfo.fullName)
            put(DatabaseHelper.USERS.COLUMN_PASSWORD, userInfo.password)
        }

        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase

            return databaseCursor.insert(DatabaseHelper.USERS.TABLE_NAME, null, values)

        } catch (e: SQLiteException) {
            throw e
        }
    }
}