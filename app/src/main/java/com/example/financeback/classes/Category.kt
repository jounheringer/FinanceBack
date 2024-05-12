package com.example.financeback.classes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException

data class CategoryInfo(
    var name: String = "",
    var profit: Boolean = false
)
class Category {

    fun getCategoriesByUser(context: Context, userID: Int): MutableList<CategoryInfo> {
        val returnCategories = mutableListOf<CategoryInfo>()

        try {
            val dataBaseCursor = DatabaseHelper(context).writableDatabase

            dataBaseCursor.query(
                DatabaseHelper.CATEGORIES.TABLE_NAME,
                arrayOf(DatabaseHelper.CATEGORIES.COLUMN_NAME, DatabaseHelper.CATEGORIES.COLUMN_PROFIT),
                "${DatabaseHelper.CATEGORIES.COLUMN_USER_ID} = ? OR ${DatabaseHelper.CATEGORIES.COLUMN_DEFAULT_CATEGORY} = ?",
                arrayOf(userID.toString(), "1"),
                null,
                null,
                null,
                null
            ).use{ cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val categoryInfo = CategoryInfo()
                        categoryInfo.name =
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_NAME))
                        categoryInfo.profit =
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_PROFIT)) == 1
                        returnCategories.add(categoryInfo)
                    } while (cursor.moveToNext())
                }
            }
            return returnCategories
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun saveCategoryByUser(context: Context, userID: Int, category: CategoryInfo): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.CATEGORIES.COLUMN_NAME, category.name)
            put(DatabaseHelper.CATEGORIES.COLUMN_PROFIT, category.profit)
            put(DatabaseHelper.CATEGORIES.COLUMN_USER_ID, userID)
        }
        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase

            return databaseCursor.insert(DatabaseHelper.CATEGORIES.TABLE_NAME, null, values) != 1L
        }catch(e: SQLiteException){
            throw e
        }
    }
}