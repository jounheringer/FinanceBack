package com.example.financeback.classes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import com.example.financeback.screens.Credentials

class UserLog {
    fun saveUserLog(context: Context, credentials: Credentials): Long{
        val user = User()
        val userInfo = user.getUser(context, credentials)
        val values = ContentValues().apply {
            put(DatabaseHelper.USER_LOGS.COLUMN_USER_ID, userInfo.userID)
            put(DatabaseHelper.USER_LOGS.COLUMN_USERNAME, userInfo.login)
            put(DatabaseHelper.USER_LOGS.COLUMN_NAME, userInfo.fullName)
            put(DatabaseHelper.USER_LOGS.COLUMN_PASSWORD, userInfo.password)
            put(DatabaseHelper.USER_LOGS.COLUMN_REMEMBER, credentials.remember)
            put(DatabaseHelper.USER_LOGS.COLUMN_DATE, System.currentTimeMillis())
        }

        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase

            return databaseCursor.insert(DatabaseHelper.USER_LOGS.TABLE_NAME, null, values)

        } catch (e: SQLiteException) {
            throw e
        }
    }

    fun getUserRemember(context: Context): Credentials {
        val select = arrayOf(DatabaseHelper.USER_LOGS.COLUMN_USERNAME, DatabaseHelper.USER_LOGS.COLUMN_PASSWORD, DatabaseHelper.USER_LOGS.COLUMN_REMEMBER)
        val where = "${DatabaseHelper.USER_LOGS.COLUMN_REMEMBER} = ?"
        val whereArgs = arrayOf("1")
        val credentials = Credentials()

        try{
            val databaseCursor = DatabaseHelper(context).readableDatabase

            databaseCursor.query(
                DatabaseHelper.USER_LOGS.TABLE_NAME,
                select,
                where,
                whereArgs,
                null,
                null,
                "${DatabaseHelper.USER_LOGS.COLUMN_DATE} DESC",
                "1"
            ).use { cursor ->
                if(cursor.moveToFirst()) {
                    credentials.login =
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_LOGS.COLUMN_USERNAME))
                    credentials.password =
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_LOGS.COLUMN_PASSWORD))
                    credentials.remember =
                        (cursor.getColumnIndexOrThrow(DatabaseHelper.USER_LOGS.COLUMN_REMEMBER) == 1)
                }
            }
            return credentials
        } catch (e: SQLiteException) {
            throw e
        }
    }
}