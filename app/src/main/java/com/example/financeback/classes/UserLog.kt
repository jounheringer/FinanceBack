package com.example.financeback.classes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import com.example.financeback.screens.Credentials

class UserLog {
    fun saveUserLog(context: Context, credentials: Credentials){
        val values = ContentValues().apply {
            put(DatabaseHelper.USER_LOGS.COLUMN_USERNAME, credentials.login)
            put(DatabaseHelper.USER_LOGS.COLUMN_PASSWORD, credentials.password)
            put(DatabaseHelper.USER_LOGS.COLUMN_DATE, System.currentTimeMillis())
        }

        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase

            databaseCursor.insert(DatabaseHelper.USER_LOGS.TABLE_NAME, null, values)
        } catch (e: SQLiteException) {
            throw e
        }
    }

    fun getUserRemember(context: Context): Credentials {
        val select = arrayOf(DatabaseHelper.USER_LOGS.COLUMN_USERNAME, DatabaseHelper.USER_LOGS.COLUMN_PASSWORD)
        val credentials = Credentials()

        try{
            val databaseCursor = DatabaseHelper(context).readableDatabase

            databaseCursor.query(
                DatabaseHelper.USER_LOGS.TABLE_NAME,
                select,
                null,
                null,
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
                }
            }
            return credentials
        } catch (e: SQLiteException) {
            throw e
        }
    }

    fun refreshUserLog(context: Context){
        try{
            val databaseCursor = DatabaseHelper(context).writableDatabase

            databaseCursor.delete(DatabaseHelper.USER_LOGS.TABLE_NAME, null, null)
        }catch (e: SQLiteException){
            throw e
        }
    }
}