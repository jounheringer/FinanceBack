package com.example.financeback.classes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.core.database.getIntOrNull
import com.example.financeback.Globals

data class CategoryInfo(
    var id: Int = -1,
    var name: String = "",
    var profit: Boolean = false,
    var userID: Int? = null,
    var isDeleted: Boolean = false
){
    fun isEmpty(): Boolean{
        return (id == -1 && name.isEmpty())
    }
}
class Category(context: Context) {
    private val categoryContext: Context = context
    val userID = Globals.getUser()
    fun getCategoriesByUser(userID: Int, showIsDeleted: Boolean = true): MutableList<CategoryInfo> {
        val returnCategories = mutableListOf<CategoryInfo>()

        var where = "(${DatabaseHelper.CATEGORIES.COLUMN_USER_ID} = ? OR ${DatabaseHelper.CATEGORIES.COLUMN_DEFAULT_CATEGORY} = ?)"
        var whereArgs = arrayOf(userID.toString(), "1")

        if (!showIsDeleted){
            where = where.plus(" AND ${DatabaseHelper.CATEGORIES.COLUMN_IS_DELETED} = ?")
            whereArgs += arrayOf("0")
        }

        try {
            val dataBaseCursor = DatabaseHelper(categoryContext).readableDatabase

            dataBaseCursor.query(
                DatabaseHelper.CATEGORIES.TABLE_NAME,
                arrayOf(DatabaseHelper.CATEGORIES.COLUMN_ID,
                    DatabaseHelper.CATEGORIES.COLUMN_NAME,
                    DatabaseHelper.CATEGORIES.COLUMN_PROFIT,
                    DatabaseHelper.CATEGORIES.COLUMN_USER_ID,
                    DatabaseHelper.CATEGORIES.COLUMN_IS_DELETED),
                where,
                whereArgs,
                null,
                null,
                null,
                null
            ).use{ cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        val categoryInfo = CategoryInfo()
                        categoryInfo.id =
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_ID))
                        categoryInfo.name =
                            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_NAME))
                        categoryInfo.profit =
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_PROFIT)) == 1
                        categoryInfo.userID =
                            cursor.getIntOrNull(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_USER_ID))
                        categoryInfo.isDeleted =
                            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_IS_DELETED)) == 1
                        returnCategories.add(categoryInfo)
                    } while (cursor.moveToNext())
                }
            }
            return returnCategories
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun getCategoryByID(categoryID: Int): CategoryInfo {
        val categoryInfo = CategoryInfo()

        try {
            val databaseCursor = DatabaseHelper(categoryContext).readableDatabase

            databaseCursor.query(
                DatabaseHelper.CATEGORIES.TABLE_NAME,
                arrayOf(DatabaseHelper.CATEGORIES.COLUMN_ID, DatabaseHelper.CATEGORIES.TABLE_NAME, DatabaseHelper.CATEGORIES.COLUMN_PROFIT),
                "${DatabaseHelper.CATEGORIES.COLUMN_ID} = ?",
                arrayOf(categoryID.toString()),
                null,
                null,
                null,
                null
            ).use {cursor ->
                categoryInfo.id =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_ID))
                categoryInfo.name =
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_NAME))
                categoryInfo.profit =
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_PROFIT)) == 1
            }

            return categoryInfo
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun saveCategoryByUser(userID: Int, category: CategoryInfo): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.CATEGORIES.COLUMN_NAME, category.name)
            put(DatabaseHelper.CATEGORIES.COLUMN_PROFIT, category.profit)
            put(DatabaseHelper.CATEGORIES.COLUMN_USER_ID, userID)
        }
        try {
            val databaseCursor = DatabaseHelper(categoryContext).writableDatabase
            val checkCategoryName = this.checkIfCategoryIsDeleted(category.name)

            if (!checkCategoryName.isEmpty())
                return restoreCategoryDeleted(checkCategoryName)

            return databaseCursor.insert(DatabaseHelper.CATEGORIES.TABLE_NAME, null, values) != 1L
        }catch(e: SQLiteException){
            throw e
        }
    }

    fun deleteCategory(categoryID: Int ): Boolean{
        if (categoryID <= 1)
            return false

        val values = ContentValues().apply {
            put(DatabaseHelper.CATEGORIES.COLUMN_IS_DELETED, 1)
        }
        try {
            val databaseCursor = DatabaseHelper(categoryContext).writableDatabase

            return databaseCursor.update(
                DatabaseHelper.CATEGORIES.TABLE_NAME,
                values,
                "${DatabaseHelper.CATEGORIES.COLUMN_ID} = ?",
                arrayOf(categoryID.toString())
            ) > 0
        }catch (e: SQLiteException){
            throw e
        }
    }

    private fun checkIfCategoryIsDeleted(categoryName: String): CategoryInfo {
        val categoryInfo = CategoryInfo()

        try {
            val databaseCursor = DatabaseHelper(categoryContext).readableDatabase

            databaseCursor.query(
                DatabaseHelper.CATEGORIES.TABLE_NAME,
                arrayOf(DatabaseHelper.CATEGORIES.COLUMN_ID,
                    DatabaseHelper.CATEGORIES.COLUMN_NAME,
                    DatabaseHelper.CATEGORIES.COLUMN_PROFIT,
                    DatabaseHelper.CATEGORIES.COLUMN_USER_ID,
                    DatabaseHelper.CATEGORIES.COLUMN_IS_DELETED),
                "${DatabaseHelper.CATEGORIES.COLUMN_NAME} = ?",
                arrayOf(categoryName),
                null,
                null,
                null,
                null
            ).use { cursor ->
                if (cursor.moveToFirst()) {
                    categoryInfo.id =
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_ID))
                    categoryInfo.name =
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_NAME))
                    categoryInfo.profit =
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_PROFIT)) == 1
                    categoryInfo.userID =
                        cursor.getIntOrNull(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_USER_ID))
                    categoryInfo.isDeleted =
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_IS_DELETED)) == 1
                }
            }
            return categoryInfo
        }catch (e: SQLiteException){
            throw e
        }
    }

    private fun restoreCategoryDeleted(category: CategoryInfo): Boolean {
        if (!category.isDeleted)
            return true

        val values = ContentValues().apply {
            put(DatabaseHelper.CATEGORIES.COLUMN_IS_DELETED, 0)
        }
        try {
            val databaseCursor = DatabaseHelper(categoryContext).writableDatabase

            return databaseCursor.update(
                DatabaseHelper.CATEGORIES.TABLE_NAME,
                values,
                "${DatabaseHelper.CATEGORIES.COLUMN_ID} = ?",
                arrayOf(category.id.toString())
            ) > 0
        }catch (e: SQLiteException){
            throw e
        }
    }
}