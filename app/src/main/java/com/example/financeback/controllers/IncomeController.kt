package com.example.financeback.controllers

import android.content.ContentValues
import android.content.Context
import com.example.financeback.Globals
import com.example.financeback.classes.CategoryInfo
import com.example.financeback.classes.DatabaseHelper
import com.example.financeback.classes.Income
import com.example.financeback.classes.IncomeInfo

class IncomeController(context: Context, userID: Int = Globals.getUser()) {
    private val income = Income(context, userID)
    private val user = userID
    fun getAllIncomes(valueFilter: String = "Total",
                      categoryFilter: Array<Int> = arrayOf(),
                      timeStamp: String,
                      orderBy: String = "DESC",
                      limit: Int = 5,
                      offset: Int = 0): List<Map<String, Any>> {
        var where = "strftime('%Y-%m', i.${DatabaseHelper.INCOME.COLUMN_DATESTAMP} / 1000, 'unixepoch') = ? "
        var whereArgs: Array<String> = arrayOf(timeStamp)
        var categoryWhere = ""
        val options = mapOf("OrderBy" to orderBy, "Limit" to limit, "Offset" to offset)

        if (valueFilter !== "Total"){
            where = where.plus("AND c.${DatabaseHelper.CATEGORIES.COLUMN_PROFIT} = ? ")
            whereArgs += when(valueFilter){
                "Positivo" -> arrayOf("1")
                else -> arrayOf("0")
            }
        }

        if (categoryFilter.isNotEmpty()){
            var count = 1
            categoryWhere = categoryWhere.plus("AND c.${DatabaseHelper.CATEGORIES.COLUMN_ID} IN (")
            categoryFilter.forEach {category ->
                categoryWhere = categoryWhere.plus(if (count < categoryFilter.size) "?, " else "?)")
                whereArgs += arrayOf(category.toString())
                count ++
            }
            where = where.plus(categoryWhere)
        }

        return income.getIncomes(where, whereArgs, options)
    }

    fun getIncomeByID(incomeID: Int): IncomeInfo {
        return income.getIncomeByID(incomeID)
    }

    fun editIncome(editIncome: IncomeInfo): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.INCOME.COLUMN_VALUE, editIncome.value)
            put(DatabaseHelper.INCOME.COLUMN_NAME, editIncome.name)
            put(DatabaseHelper.INCOME.COLUMN_DATESTAMP, editIncome.date)
            put(DatabaseHelper.INCOME.COLUMN_CATEGORY_ID, editIncome.categoryID)
            put(DatabaseHelper.INCOME.COLUMN_DESCRIPTION, editIncome.description)
        }

        val where = "${DatabaseHelper.INCOME.COLUMN_USER} = ? AND ${DatabaseHelper.INCOME.COLUMN_ID} = ?"
        val whereArgs = arrayOf(user.toString(), editIncome.id.toString())

        return income.editIncome(where, whereArgs, values)
    }

    fun saveIncome(incomeInfo: IncomeInfo, categoryInfo: CategoryInfo): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.INCOME.COLUMN_VALUE, incomeInfo.value)
            put(DatabaseHelper.INCOME.COLUMN_NAME, incomeInfo.name)
            put(DatabaseHelper.INCOME.COLUMN_DATESTAMP, incomeInfo.date)
            put(DatabaseHelper.INCOME.COLUMN_DESCRIPTION, incomeInfo.description)
            put(DatabaseHelper.INCOME.COLUMN_CATEGORY_ID, categoryInfo.id)
            put(DatabaseHelper.INCOME.COLUMN_USER, user)
        }

        return income.saveIncome(values)
    }

    fun getIncomeTotals(timeStamp: String): Map<String, String> {
        return income.getIncomeTotals(timeStamp)
    }

    fun deleteIncome(incomeID: Int): Boolean {
        val where = "${DatabaseHelper.INCOME.COLUMN_ID} = ? AND ${DatabaseHelper.INCOME.COLUMN_USER} = ?"
        val whereArgs = arrayOf(incomeID.toString(), user.toString())

        return income.deleteIncome(where, whereArgs)
    }

    fun getIncomesCount(filter: String, categoryFilter: Array<Int> = arrayOf<Int>()): Int {
        var where = if (filter != "Total" || categoryFilter.isNotEmpty()) "WHERE " else ""
        var whereArgs = arrayOf<String>()
        var categoryWhere = ""

        if (filter != "Total") {
            where = where.plus("c.${DatabaseHelper.CATEGORIES.COLUMN_PROFIT} = ?")
            whereArgs = if (filter == "Positivo") arrayOf("1") else arrayOf("0")
        }

        if (categoryFilter.isNotEmpty()){
            var count = 1
            categoryWhere = categoryWhere.plus((if (where.length > 6) "AND " else "") + "c.${DatabaseHelper.CATEGORIES.COLUMN_ID} IN (")
            categoryFilter.forEach {category ->
                categoryWhere = categoryWhere.plus(if (count < categoryFilter.size) "?, " else "?)")
                whereArgs += arrayOf(category.toString())
                count ++
            }
            where = where.plus(categoryWhere)
        }

        return income.getIncomesCount(where, whereArgs)
    }
}