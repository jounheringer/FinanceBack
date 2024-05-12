package com.example.financeback.classes

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.financeback.utils.NumberFormatter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class IncomeInfo(
    var value: Double = 0.0,
    var name: String = "",
    var date: Long = System.currentTimeMillis(),
    var description: String = "",
    var profit: Boolean = false
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
                   offset:Int,
                   filter: String,
                   timeStamp: String,
                   orderByFlow: String = "DESC"): List<Map<String, Any>> { /*TODO adicionar condicao para pegar info de um so usuario*/
        try {
            val limitAndOffset = "${offset},${limit}"
            val databaseCursor = DatabaseHelper(context).writableDatabase
            val orderBy = "${DatabaseHelper.INCOME.COLUMN_DATESTAMP} ${orderByFlow}"
            var whereFilter = ""
            var whereArgsFilter = arrayOf<String>()
            var whereTimeStamp = ""
            var whereArgsTimeStamp = arrayOf<String>()
            var where = whereFilter
            var whereArgs = arrayOf<String>()

            whereTimeStamp = "strftime('%Y-%m', ${DatabaseHelper.INCOME.COLUMN_DATESTAMP} / 1000, 'unixepoch') = ?"
            whereArgsTimeStamp = arrayOf<String>(timeStamp)

            whereFilter = "${DatabaseHelper.INCOME.COLUMN_PROFIT} = ?"
            when (filter) {
                "Positivo" -> whereArgsFilter = arrayOf("1")
                "Negativo" -> whereArgsFilter = arrayOf("0")
            }

            where = if(filter != "Total")
                "$whereFilter AND $whereTimeStamp"
            else
                whereTimeStamp

            whereArgs = if (whereArgsFilter.isNotEmpty())
                    whereArgsFilter + whereArgsTimeStamp
                else
                    whereArgsTimeStamp

            val rows: Cursor = databaseCursor.query(
                DatabaseHelper.INCOME.TABLE_NAME,
                null,
                where,
                whereArgs,
                null,
                null,
                orderBy,
                limitAndOffset,
            )

            val incomesList = mutableListOf<Map<String, Any>>()

            rows.use { cursor ->
                if (cursor.moveToFirst()){
                    do{
                        val incomesReturn = mutableMapOf<String, Any>()
                        incomesReturn["ID"] = rows.getInt(rows.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_ID))
                        incomesReturn["User"] = rows.getInt(rows.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_USER))
                        incomesReturn["Value"] = NumberFormatter().currencyFormatterFloat(rows.getFloat(rows.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_VALUE)))
                        incomesReturn["Name"] = rows.getString(rows.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_NAME))
                        incomesReturn["Date"] = rows.getLong(rows.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_DATESTAMP))
                        incomesReturn["Profit"] = (rows.getInt(rows.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_PROFIT)) == 1)
                        incomesReturn["Description"] = rows.getString(rows.getColumnIndexOrThrow(DatabaseHelper.INCOME.COLUMN_DESCRIPTION))
                        incomesList.add(incomesReturn)
                    }while(cursor.moveToNext())
                }
            }
            return incomesList
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun getIncomesCount(context: Context, idUser: Int?, filter:String): Int{
        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase
            val where = "${DatabaseHelper.INCOME.COLUMN_PROFIT} = ?"

            var whereArgs = arrayOf<String>()

            when (filter){
                "Positivo" -> whereArgs = arrayOf("1")
                "Negativo" -> whereArgs = arrayOf("0")
            }

            val result = databaseCursor.query(
                DatabaseHelper.INCOME.TABLE_NAME,
                arrayOf("COUNT(*)"),
                if (filter == "Total") null else where,
                if (filter == "Total") null else whereArgs,
                null,
                null,
                null,
                null,
            )
            result.moveToFirst()
            val resultCount = result.getInt(0)

            result.close()

            return resultCount
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun getIncomeTotals(context: Context, timeStamp: String/*user: Int*/): MutableMap<String, String> {

        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase
            val results = mutableMapOf<String, Float>()
            val returnMap = mutableMapOf<String, String>()

            databaseCursor.rawQuery("SELECT SUM(${DatabaseHelper.INCOME.COLUMN_VALUE}) AS negative, \n" +
                        "(SELECT SUM(${DatabaseHelper.INCOME.COLUMN_VALUE})\n" +
                        "   FROM income\n" +
                        "   WHERE ${DatabaseHelper.INCOME.COLUMN_PROFIT} = ? AND strftime('%Y-%m', ${DatabaseHelper.INCOME.COLUMN_DATESTAMP} / 1000, 'unixepoch') = ?) as positive\n" +
                        "FROM income\n" +
                        "WHERE strftime('%Y-%m', ${DatabaseHelper.INCOME.COLUMN_DATESTAMP} / 1000, 'unixepoch') = ? AND ${DatabaseHelper.INCOME.COLUMN_PROFIT} = ?;",
                arrayOf("1", timeStamp, timeStamp, "0")
            ).use { cursor ->
                if (cursor.moveToFirst()){
                    results["Total"] = cursor.getFloat(cursor.getColumnIndexOrThrow("negative")).minus(cursor.getFloat(cursor.getColumnIndexOrThrow("positive")))
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

    fun saveIncome(context: Context, incomeInfo: IncomeInfo): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.INCOME.COLUMN_VALUE, incomeInfo.value)
            put(DatabaseHelper.INCOME.COLUMN_NAME, incomeInfo.name)
            put(DatabaseHelper.INCOME.COLUMN_DATESTAMP, incomeInfo.date)
            put(DatabaseHelper.INCOME.COLUMN_PROFIT, incomeInfo.profit)
            put(DatabaseHelper.INCOME.COLUMN_DESCRIPTION, incomeInfo.description)
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

    fun editIncome(context: Context, userID: Int?, incomeID: Int, value: Float, name: String, profit: Boolean){
//        atualizar dados de uma nota fiscal especifica
        val values = ContentValues().apply {
            put(DatabaseHelper.INCOME.COLUMN_VALUE, value)
            put(DatabaseHelper.INCOME.COLUMN_NAME, name)
            put(DatabaseHelper.INCOME.COLUMN_PROFIT, profit)
        }

        val where = "${DatabaseHelper.INCOME.COLUMN_ID} = ? ${if (userID != null) "AND ${DatabaseHelper.INCOME.COLUMN_USER} = ?" else ""}"
        var whereArgs = arrayOf(incomeID.toString())
        if (userID != null)
            whereArgs += userID.toString()

        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase

            val delete = databaseCursor?.update(DatabaseHelper.INCOME.TABLE_NAME, values, where, whereArgs)
        }catch (e: SQLiteException){
            throw e
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTotalIncomeValues(context: Context, userID: Int){
//        Atualizar o valor total do usuario X pegando do database todas as notas fiscais atribuida a ele dentro de um mes
        val format = DateTimeFormatter.ofPattern("MM-YYYY")
        val formatMonth = LocalDateTime.now().format(format)

        val select = arrayOf("${DatabaseHelper.INCOME.COLUMN_VALUE}, ${DatabaseHelper.INCOME.COLUMN_PROFIT}")
        val where = "${DatabaseHelper.INCOME.COLUMN_USER} = ? AND STRFTIME('%m-%Y', ${DatabaseHelper.INCOME.COLUMN_DATESTAMP}) < ?"
        val whereArgs = arrayOf(userID.toString(), formatMonth.toString())

        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase

            val result = databaseCursor?.query(
                DatabaseHelper.INCOME.TABLE_NAME, select,
                where, whereArgs, null, null, null)

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