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

class Income() {

    fun getIncomes(context: Context,
                   limit: Int,
                   offset: Int = 0,
                   filter: String = "Total",
                   categoryFilter: String = "All",
                   timeStamp: String,
                   orderBy: String = "DESC"): List<Map<String, Any>> {
        var whereArgsFilter = arrayOf<String>()

        val whereTimeStamp = "strftime('%Y-%m', i.${DatabaseHelper.INCOME.COLUMN_DATESTAMP} / 1000, 'unixepoch') = ?"
        val whereArgsTimeStamp: Array<String> = arrayOf(timeStamp)
        val incomesList = mutableListOf<Map<String, Any>>()
        val columns = "i.${DatabaseHelper.INCOME.COLUMN_ID}, " +
                "i.${DatabaseHelper.INCOME.COLUMN_NAME}, " +
                "i.${DatabaseHelper.INCOME.COLUMN_VALUE}, " +
                "i.${DatabaseHelper.INCOME.COLUMN_DATESTAMP}, " +
                "i.${DatabaseHelper.INCOME.COLUMN_DESCRIPTION}, " +
                "c.${DatabaseHelper.CATEGORIES.COLUMN_PROFIT}, " +
                "c.${DatabaseHelper.CATEGORIES.COLUMN_NAME}"

        val whereFilter = "c.${DatabaseHelper.CATEGORIES.COLUMN_PROFIT} = ?"
        when (filter) {
            "Positivo" -> whereArgsFilter = arrayOf("1")
            "Negativo" -> whereArgsFilter = arrayOf("0")
        }

        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase
            databaseCursor.rawQuery(
                "SELECT $columns " +
                        "FROM ${DatabaseHelper.INCOME.TABLE_NAME} i " +
                        "INNER JOIN ${DatabaseHelper.CATEGORIES.TABLE_NAME} c ON i.${DatabaseHelper.INCOME.COLUMN_CATEGORY_ID} = c.${DatabaseHelper.CATEGORIES.COLUMN_ID} " +
                        "WHERE ${ if(filter != "Total") "$whereFilter AND $whereTimeStamp" else whereTimeStamp } " +
                        "ORDER BY i.${DatabaseHelper.INCOME.COLUMN_DATESTAMP} $orderBy LIMIT $limit OFFSET $offset",
                if (whereArgsFilter.isNotEmpty()) whereArgsFilter + whereArgsTimeStamp else whereArgsTimeStamp,
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

    fun getIncomeByID(context: Context, incomeID: Int): IncomeInfo {
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

            val databaseCursor = DatabaseHelper(context).readableDatabase

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

    fun getIncomesCount(context: Context, idUser: Int?, filter:String): Int{
        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase
            val where = "c.${DatabaseHelper.CATEGORIES.COLUMN_PROFIT} = ?"
            var resultCount = 0

            var whereArgs = arrayOf<String>()

            when (filter){
                "Positivo" -> whereArgs = arrayOf("1")
                "Negativo" -> whereArgs = arrayOf("0")
            }

            databaseCursor.rawQuery(
                "SELECT COUNT(*) " +
                        "FROM ${DatabaseHelper.INCOME.TABLE_NAME} i " +
                        "INNER JOIN ${DatabaseHelper.CATEGORIES.TABLE_NAME} c ON c.${DatabaseHelper.CATEGORIES.COLUMN_ID} = i.${DatabaseHelper.INCOME.COLUMN_CATEGORY_ID} " +
                        if (filter == "Total") "WHERE $where" else "",
                        if (filter == "Total") whereArgs else arrayOf<String>()
            ).use { cursor ->
                if (cursor.moveToFirst())
                    resultCount = cursor.getInt(0)
            }
            return resultCount
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun getIncomeTotals(context: Context, timeStamp: String/*user: Int*/): Map<String, String> {

        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase
            val results = mutableMapOf<String, Float>()
            val returnMap = mutableMapOf<String, String>()

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

    fun saveIncome(context: Context, incomeInfo: IncomeInfo, categoryInfo: CategoryInfo, userID: Int): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.INCOME.COLUMN_VALUE, incomeInfo.value)
            put(DatabaseHelper.INCOME.COLUMN_NAME, incomeInfo.name)
            put(DatabaseHelper.INCOME.COLUMN_DATESTAMP, incomeInfo.date)
            put(DatabaseHelper.INCOME.COLUMN_DESCRIPTION, incomeInfo.description)
            put(DatabaseHelper.INCOME.COLUMN_CATEGORY_ID, categoryInfo.id)
            put(DatabaseHelper.INCOME.COLUMN_USER, userID)
        }

        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase

            return databaseCursor.insert(DatabaseHelper.INCOME.TABLE_NAME, null, values).toInt() != -1
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun deleteIncome(context: Context, userID: Int?, incomeID: Int): Boolean{
//        Excluir nota x do usuario x
        val where = "${DatabaseHelper.INCOME.COLUMN_ID} = ? ${if (userID != null) "AND ${DatabaseHelper.INCOME.COLUMN_USER} = ?" else ""}"
        var whereArgs = arrayOf(incomeID.toString())
        if (userID != null)
            whereArgs += userID.toString()

        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase

            return databaseCursor.delete(DatabaseHelper.INCOME.TABLE_NAME,
                where,
                whereArgs) > 0
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun editIncome(context: Context, userID: Int, income: IncomeInfo): Boolean{
        val values = ContentValues().apply {
            put(DatabaseHelper.INCOME.COLUMN_VALUE, income.value)
            put(DatabaseHelper.INCOME.COLUMN_NAME, income.name)
            put(DatabaseHelper.INCOME.COLUMN_DATESTAMP, income.date)
            put(DatabaseHelper.INCOME.COLUMN_CATEGORY_ID, income.categoryID)
            put(DatabaseHelper.INCOME.COLUMN_DESCRIPTION, income.description)
        }

        val where = "${DatabaseHelper.INCOME.COLUMN_USER} = ? AND ${DatabaseHelper.INCOME.COLUMN_ID} = ?"
        val whereArgs = arrayOf(userID.toString(), income.id.toString())

        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase

            return databaseCursor.update(DatabaseHelper.INCOME.TABLE_NAME, values, where, whereArgs) > 0
        }catch (e: SQLiteException){
            throw e
        }
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    fun updateTotalIncomeValues(context: Context, userID: Int){
////        Atualizar o valor total do usuario X pegando do database todas as notas fiscais atribuida a ele dentro de um mes
//        val format = DateTimeFormatter.ofPattern("MM-YYYY")
//        val formatMonth = LocalDateTime.now().format(format)
//
//        val select = arrayOf("${DatabaseHelper.INCOME.COLUMN_VALUE}, ${DatabaseHelper.INCOME.COLUMN_PROFIT}")
//        val where = "${DatabaseHelper.INCOME.COLUMN_USER} = ? AND STRFTIME('%m-%Y', ${DatabaseHelper.INCOME.COLUMN_DATESTAMP}) < ?"
//        val whereArgs = arrayOf(userID.toString(), formatMonth.toString())
//
//        try {
//            val databaseCursor = DatabaseHelper(context).writableDatabase
//
//            val result = databaseCursor?.query(
//                DatabaseHelper.INCOME.TABLE_NAME, select,
//                where, whereArgs, null, null, null)
//
//        }catch (e: SQLiteException){
//            throw e
//        }
//    }

    fun generateReport(){
//        TODO ("Listar e calcular o total de ganho, perda e valor total do usuario X dentro de um mes")
    }

    fun exportReport(){
//        TODO("Exportar em o relatorio do usuario X")
    }

}