package com.example.financeback.classes

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    object Income {
        const val TABLE_NAME = "income"
        const val COLUMN_ID = "ID"
        const val COLUMN_USER = "UserID"
        const val COLUMN_VALUE = "Value"
        const val COLUMN_NAME = "Name"
        const val COLUMN_DATESTAMP = "DateStamp"
        const val COLUMN_PROFIT = "Profit"
    }

    object Users {
        const val TABLE_NAME = "users"
        const val COLUMN_ID = "ID"
        const val COLUMN_NAME = "Name"
        const val COLUMN_EMAIL = "Email"
        const val COLUMN_PASSWORD = "Password"
    }
    companion object {
        const val DATABASE_NAME = "FINANCEBACK"
        const val DATABASE_VERSION = 1
    }

    private val CREATE_INCOME =
        "CREATE TABLE IF NOT EXISTS ${Income.TABLE_NAME}(" +
                "${Income.COLUMN_ID} INTEGER PRIMARY KEY," +
                "${Income.COLUMN_USER} INTEGER," +
                "${Income.COLUMN_VALUE} REAL DEFAULT NULL," +
                "${Income.COLUMN_NAME} TEXT DEFAULT NULL," +
                "${Income.COLUMN_DATESTAMP} INTEGER DEFAULT NULL," +
                "${Income.COLUMN_PROFIT} BOOLEAN DEFAULT FALSE," +
                "FOREIGN KEY (${Income.COLUMN_USER})" +
                "REFERENCES ${Users.TABLE_NAME}(${Users.COLUMN_ID}))"

    private val CREATE_USERS =
        "CREATE TABLE IF NOT EXISTS ${Users.TABLE_NAME}(" +
                "${Users.COLUMN_ID} INTEGER PRIMARY KEY," +
                "${Users.COLUMN_NAME} TEXT DEFAULT NULL," +
                "${Users.COLUMN_EMAIL} TEXT DEFAULT NULL," +
                "${Users.COLUMN_PASSWORD} TEXT DEFAULT NULL)"

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(CREATE_USERS)
            db.execSQL(CREATE_INCOME)
        }catch (e: SQLException){
            throw e
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

}