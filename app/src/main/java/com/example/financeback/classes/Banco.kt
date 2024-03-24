package com.example.financeback.classes

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    object INCOME {
        const val TABLE_NAME = "income"
        const val COLUMN_ID = "ID"
        const val COLUMN_USER = "UserID"
        const val COLUMN_VALUE = "Value"
        const val COLUMN_NAME = "Name"
        const val COLUMN_DATESTAMP = "DateStamp"
        const val COLUMN_PROFIT = "Profit"
    }

    object USERS {
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
        "CREATE TABLE IF NOT EXISTS ${INCOME.TABLE_NAME}(" +
                "${INCOME.COLUMN_ID} INTEGER PRIMARY KEY," +
                "${INCOME.COLUMN_USER} INTEGER," +
                "${INCOME.COLUMN_VALUE} REAL DEFAULT NULL," +
                "${INCOME.COLUMN_NAME} TEXT DEFAULT NULL," +
                "${INCOME.COLUMN_DATESTAMP} INTEGER DEFAULT NULL," +
                "${INCOME.COLUMN_PROFIT} BOOLEAN DEFAULT FALSE," +
                "FOREIGN KEY (${INCOME.COLUMN_USER})" +
                "REFERENCES ${USERS.TABLE_NAME}(${USERS.COLUMN_ID}))"

    private val CREATE_USERS =
        "CREATE TABLE IF NOT EXISTS ${USERS.TABLE_NAME}(" +
                "${USERS.COLUMN_ID} INTEGER PRIMARY KEY," +
                "${USERS.COLUMN_NAME} TEXT DEFAULT NULL," +
                "${USERS.COLUMN_EMAIL} TEXT DEFAULT NULL," +
                "${USERS.COLUMN_PASSWORD} TEXT DEFAULT NULL)"

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