package com.example.financeback.classes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import com.example.financeback.utils.NumberFormatter

data class IncomeInfo(
    var id: Int = -1,
    var value: Double = 0.0,
    var name: String = "",
    var date: Long = System.currentTimeMillis(),
    var description: String = "",
    var categoryID: Int = -1
){
    fun missingParam(): Array<String> {
        var missingParams = arrayOf<String>()
        if (value == 0.0)
            missingParams += "Valor"
        if (name.isEmpty())
            missingParams += "Nome"
        if (date == 0L)
            missingParams += "Data"

        return missingParams
    }
}

open class Income(context: Context, userID: Int) {
    private val incomeContext = context
    private val incomeUser = userID

    fun getIncomes(where: String, whereArgs: Array<String>, filters: Map<String, Any>): List<Map<String, Any>> {
        val incomesList = mutableListOf<Map<String, Any>>()
        val columns = "i.${DatabaseHelper.INCOME.COLUMN_ID}, " +
                "i.${DatabaseHelper.INCOME.COLUMN_NAME}, " +
                "i.${DatabaseHelper.INCOME.COLUMN_VALUE}, " +
                "i.${DatabaseHelper.INCOME.COLUMN_DATESTAMP}, " +
                "i.${DatabaseHelper.INCOME.COLUMN_DESCRIPTION}, " +
                "c.${DatabaseHelper.CATEGORIES.COLUMN_PROFIT}, " +
                "c.${DatabaseHelper.CATEGORIES.COLUMN_NAME}"

        try {
            val databaseCursor = DatabaseHelper(incomeContext).readableDatabase
            databaseCursor.rawQuery(
                "SELECT $columns " +
                        "FROM ${DatabaseHelper.INCOME.TABLE_NAME} i " +
                        "INNER JOIN ${DatabaseHelper.CATEGORIES.TABLE_NAME} c ON i.${DatabaseHelper.INCOME.COLUMN_CATEGORY_ID} = c.${DatabaseHelper.CATEGORIES.COLUMN_ID} " +
                        "WHERE $where " +
                        "ORDER BY i.${DatabaseHelper.INCOME.COLUMN_DATESTAMP} ${filters["OrderBy"]} LIMIT ${filters["Limit"]} OFFSET ${filters["Offset"]}",
                whereArgs,
            ).use { cursor ->
                if (cursor.moveToFirst()){
                    do{
                        val incomesReturn = mutableMapOf<String, Any>()
                        incomesReturn["ID"] = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_ID))
                        incomesReturn["Value"] = NumberFormatter().currencyFormatterFloat(cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_VALUE)))
                        incomesReturn["Name"] = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_NAME))
                        incomesReturn["Date"] = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_DATESTAMP))
                        incomesReturn["Description"] = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_DESCRIPTION))
                        incomesReturn["Profit"] = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_PROFIT)) == 1
                        incomesReturn["CategoryName"] = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.CATEGORIES.COLUMN_NAME))
                        incomesList.add(incomesReturn)
                    }while(cursor.moveToNext())
                }
            }
            return incomesList
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun getIncomeByID(incomeID: Int): IncomeInfo {
        val incomeInfo = IncomeInfo()
        val selects = arrayOf(
            DatabaseHelper.INCOME.COLUMN_ID,
            DatabaseHelper.INCOME.COLUMN_NAME,
            DatabaseHelper.INCOME.COLUMN_VALUE,
            DatabaseHelper.INCOME.COLUMN_DATESTAMP,
            DatabaseHelper.INCOME.COLUMN_DESCRIPTION,
            DatabaseHelper.INCOME.COLUMN_CATEGORY_ID
        )

        try{

            val databaseCursor = DatabaseHelper(incomeContext).readableDatabase

            databaseCursor.query(
                DatabaseHelper.INCOME.TABLE_NAME,
                selects,
                "${DatabaseHelper.INCOME.COLUMN_ID} = ?",
                arrayOf(incomeID.toString()),
                null,
                null,
                null,
                null
            ).use {cursor ->
                if(cursor.moveToFirst()) {
                    incomeInfo.id =
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_ID))
                    incomeInfo.name =
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_NAME))
                    incomeInfo.value =
                        cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_VALUE))
                    incomeInfo.date =
                        cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_DATESTAMP))
                    incomeInfo.categoryID =
                        cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_CATEGORY_ID))
                    incomeInfo.description =
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_DESCRIPTION))
                }
            }

            return incomeInfo
        }catch(e: SQLiteException){
            throw e
        }
    }

    fun getIncomesCount(where: String, whereArgs: Array<String>): Int{
        var resultCount = 0
        try {
            val databaseCursor = DatabaseHelper(incomeContext).readableDatabase

            databaseCursor.rawQuery(
                "SELECT COUNT(*) " +
                        "FROM ${DatabaseHelper.INCOME.TABLE_NAME} i " +
                        "INNER JOIN ${DatabaseHelper.CATEGORIES.TABLE_NAME} c ON c.${DatabaseHelper.CATEGORIES.COLUMN_ID} = i.${DatabaseHelper.INCOME.COLUMN_CATEGORY_ID} " +
                        where,
                        whereArgs
            ).use { cursor ->
                if (cursor.moveToFirst())
                    resultCount = cursor.getInt(0)
            }
            return resultCount
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun getIncomeTotals(timeStamp: String): Map<String, String> {
        val results = mutableMapOf<String, Float>()
        val returnMap = mutableMapOf<String, String>()

        try {
            val databaseCursor = DatabaseHelper(incomeContext).readableDatabase


            databaseCursor.rawQuery("SELECT SUM(i.${DatabaseHelper.INCOME.COLUMN_VALUE}) AS negative, \n" +
                        "(SELECT SUM(i.${DatabaseHelper.INCOME.COLUMN_VALUE})" +
                            "FROM ${DatabaseHelper.INCOME.TABLE_NAME} i " +
                            "INNER JOIN ${DatabaseHelper.CATEGORIES.TABLE_NAME} c ON i.${DatabaseHelper.INCOME.COLUMN_CATEGORY_ID} = c.${DatabaseHelper.CATEGORIES.COLUMN_ID} " +
                            "WHERE c.${DatabaseHelper.CATEGORIES.COLUMN_PROFIT} = ? AND " +
                            "strftime('%Y-%m', i.${DatabaseHelper.INCOME.COLUMN_DATESTAMP} / 1000, 'unixepoch') = ?) as positive\n" +
                        "FROM income i " +
                        "INNER JOIN ${DatabaseHelper.CATEGORIES.TABLE_NAME} c ON i.${DatabaseHelper.INCOME.COLUMN_CATEGORY_ID} = c.${DatabaseHelper.CATEGORIES.COLUMN_ID} " +
                        "WHERE strftime('%Y-%m', i.${DatabaseHelper.INCOME.COLUMN_DATESTAMP} / 1000, 'unixepoch') = ? AND " +
                        "c.${DatabaseHelper.CATEGORIES.COLUMN_PROFIT} = ?;",
                arrayOf("1", timeStamp, timeStamp, "0")
            ).use { cursor ->
                if (cursor.moveToFirst()){
                    results["Total"] = cursor.getFloat(cursor.getColumnIndexOrThrow("positive")).minus(cursor.getFloat(cursor.getColumnIndexOrThrow("negative")))
                    results["Positivo"] = cursor.getFloat(cursor.getColumnIndexOrThrow("positive"))
                    results["Negativo"] = cursor.getFloat(cursor.getColumnIndexOrThrow("negative"))
                }
            }
            databaseCursor.close()

            results.forEach { number ->
                returnMap[number.key] = NumberFormatter().currencyFormatterFloat(number.value)
            }

            return returnMap
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun saveIncome(values: ContentValues): Boolean {
        try {
            val databaseCursor = DatabaseHelper(incomeContext).writableDatabase

            return databaseCursor.insert(DatabaseHelper.INCOME.TABLE_NAME, null, values).toInt() != -1
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun deleteIncome(where: String, whereArgs: Array<String>): Boolean{
        try {
            val databaseCursor = DatabaseHelper(incomeContext).writableDatabase

            return databaseCursor.delete(DatabaseHelper.INCOME.TABLE_NAME,
                where,
                whereArgs) > 0
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun editIncome(where: String, whereArgs: Array<String>, values: ContentValues): Boolean{
        try {
            val databaseCursor = DatabaseHelper(incomeContext).writableDatabase

            return databaseCursor.update(DatabaseHelper.INCOME.TABLE_NAME, values, where, whereArgs) > 0
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun generateReport(){
//        TODO ("Listar e calcular o total de ganho, perda e valor total do usuario X dentro de um mes")
    }

    fun exportReport(){
//        TODO("Exportar em o relatorio do usuario X")
    }

}