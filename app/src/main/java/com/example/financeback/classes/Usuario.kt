package com.example.financeback.classes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import com.example.financeback.screens.Credentials
import com.example.financeback.screens.UserInfo

class User {
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

    fun checkUser(context: Context, credentials: Credentials): Boolean {
        val where = "${DatabaseHelper.USERS.COLUMN_USERNAME} = ? AND ${DatabaseHelper.USERS.COLUMN_USERNAME} = ?"
        val whereArgs = arrayOf(credentials.login, credentials.password)
        var returnUser: Int = 0

        try {
            val databaseCursor = DatabaseHelper(context).readableDatabase

            val result = databaseCursor.query(
                DatabaseHelper.USERS.TABLE_NAME,
                arrayOf("1"),
                where,
                whereArgs,
                null,
                null,
                null,
                null,
            )

            if (result.moveToFirst()){
                returnUser = result.count
            }
            result.close()
            return returnUser > 0
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun getUser(context: Context, credentials: Credentials): Credentials {
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
                    userInfo.userID =
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.USERS.COLUMN_ID))
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
}