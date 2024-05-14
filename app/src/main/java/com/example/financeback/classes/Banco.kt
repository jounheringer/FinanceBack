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
        const val COLUMN_CATEGORY_ID = "CategoryID"
        const val COLUMN_VALUE = "Value"
        const val COLUMN_NAME = "Name"
        const val COLUMN_DATESTAMP = "DateStamp"
        const val COLUMN_DESCRIPTION = "Description"
    }

    object USERS {
        const val TABLE_NAME = "users"
        const val COLUMN_ID = "ID"
        const val COLUMN_NAME = "Name"
        const val COLUMN_USERNAME = "UserName"
        const val COLUMN_PASSWORD = "Password"
    }

    object USER_LOGS {
        const val TABLE_NAME = "user_logs"
        const val COLUMN_ID = "ID"
        const val COLUMN_USERNAME = "UserName"
        const val COLUMN_REMEMBER = "Remember"
        const val COLUMN_DATE = "LoginDate"
        const val COLUMN_PASSWORD = "Password"
    }

    object CATEGORIES {
        const val TABLE_NAME = "categories"
        const val COLUMN_ID = "ID"
        const val COLUMN_USER_ID = "UserID"
        const val COLUMN_NAME = "CategoryName"
        const val COLUMN_PROFIT = "Profit"
        const val COLUMN_DEFAULT_CATEGORY = "DefaultCategory"
    }
    companion object {
        const val DATABASE_NAME = "FINANCEBACK"
        const val DATABASE_VERSION = 19
    }

    private val CREATE_CATEGORIES =
        "CREATE TABLE IF NOT EXISTS ${CATEGORIES.TABLE_NAME}(" +
                "${CATEGORIES.COLUMN_ID} INTEGER PRIMARY KEY," +
                "${CATEGORIES.COLUMN_USER_ID} INTEGER DEFAULT NULL," +
                "${CATEGORIES.COLUMN_NAME} TEXT DEFAULT NULL," +
                "${CATEGORIES.COLUMN_PROFIT} BOOLEAN DEFAULT FALSE," +
                "${CATEGORIES.COLUMN_DEFAULT_CATEGORY} BOOLEAN DEFAULT FALSE," +
                "FOREIGN KEY (${CATEGORIES.COLUMN_USER_ID})" +
                "REFERENCES ${USERS.TABLE_NAME}(${USERS.COLUMN_ID}))"

    private val POPULATE_CATEGORIES =
        "INSERT INTO ${CATEGORIES.TABLE_NAME}" +
                "(${CATEGORIES.COLUMN_NAME}, ${CATEGORIES.COLUMN_PROFIT}, ${CATEGORIES.COLUMN_DEFAULT_CATEGORY})" +
                "VALUES (\"Venda\", TRUE, TRUE)," +
                "(\"Compra\", FALSE, TRUE)"

    private val CREATE_USER_LOGS =
        "CREATE TABLE IF NOT EXISTS ${USER_LOGS.TABLE_NAME}(" +
                "${USER_LOGS.COLUMN_ID} INTEGER PRIMARY KEY," +
                "${USER_LOGS.COLUMN_USERNAME} TEXT DEFAULT NULL," +
                "${USER_LOGS.COLUMN_PASSWORD} TEXT DEFAULT NULL," +
                "${USER_LOGS.COLUMN_REMEMBER} BOOLEAN DEFAULT FALSE," +
                "${USER_LOGS.COLUMN_DATE} INTEGER DEFAULT NULL)"

    private val CREATE_INCOME =
        "CREATE TABLE IF NOT EXISTS ${INCOME.TABLE_NAME}(" +
                "${INCOME.COLUMN_ID} INTEGER PRIMARY KEY," +
                "${INCOME.COLUMN_USER} INTEGER," +
                "${INCOME.COLUMN_CATEGORY_ID} INTEGER," +
                "${INCOME.COLUMN_VALUE} DECIMAL(10, 2) DEFAULT NULL," +
                "${INCOME.COLUMN_NAME} TEXT DEFAULT NULL," +
                "${INCOME.COLUMN_DATESTAMP} INTEGER DEFAULT NULL," +
                "${INCOME.COLUMN_DESCRIPTION} TEXT DEFAULT NULL," +
                "FOREIGN KEY (${INCOME.COLUMN_USER})" +
                "REFERENCES ${USERS.TABLE_NAME}(${USERS.COLUMN_ID})," +
                "FOREIGN KEY (${INCOME.COLUMN_CATEGORY_ID})" +
                "REFERENCES ${CATEGORIES.TABLE_NAME}(${CATEGORIES.COLUMN_ID}))"

    private val CREATE_USERS =
        "CREATE TABLE IF NOT EXISTS ${USERS.TABLE_NAME}(" +
                "${USERS.COLUMN_ID} INTEGER PRIMARY KEY," +
                "${USERS.COLUMN_NAME} TEXT DEFAULT NULL," +
                "${USERS.COLUMN_USERNAME} TEXT DEFAULT NULL," +
                "${USERS.COLUMN_PASSWORD} TEXT DEFAULT NULL)"

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(CREATE_USERS)
            db.execSQL(CREATE_INCOME)
            db.execSQL(CREATE_USER_LOGS)
            db.execSQL(CREATE_CATEGORIES)
            db.execSQL(POPULATE_CATEGORIES)
        }catch (e: SQLException){
            throw e
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        try {
            db.execSQL("DROP TABLE IF EXISTS ${INCOME.TABLE_NAME}")
            db.execSQL("DROP TABLE IF EXISTS ${USERS.TABLE_NAME}")
            db.execSQL("DROP TABLE IF EXISTS ${USER_LOGS.TABLE_NAME}")
            db.execSQL("DROP TABLE IF EXISTS ${CATEGORIES.TABLE_NAME}")
            onCreate(db)
        }catch (e: SQLException){
            throw e
        }
    }

}