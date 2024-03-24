package com.example.financeback.classes

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteException
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Income(userID: Int? = null) {
    private var userID: Int? = if (userID == null) userID else null
    private var incomeID: Int? = null
    var value: Double? = null
    var name: String? = null
    var dateStamp: Long? = null
    var profit: Boolean = false

    fun saveIncome(context: Context, value: Double, name: String, profit: Boolean) {
//        Salvar nota com valor X, name X, data x e se for ou nao lucro
        val values = ContentValues().apply {
            put(DatabaseHelper.INCOME.COLUMN_VALUE, value)
            put(DatabaseHelper.INCOME.COLUMN_NAME, name)
            put(DatabaseHelper.INCOME.COLUMN_DATESTAMP, System.currentTimeMillis())
            put(DatabaseHelper.INCOME.COLUMN_PROFIT, profit)
        }

        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase

            databaseCursor?.insert(DatabaseHelper.INCOME.TABLE_NAME, null, values)

        }catch (e: SQLiteException){
            throw e
        }
    }

    fun deleteIncome(context: Context, userID: Int?, incomeID: Int){
//        Excluir nota x do usuario x
        val where = "${DatabaseHelper.INCOME.COLUMN_ID} = ? ${if (userID != null) "AND ${DatabaseHelper.INCOME.COLUMN_USER} = ?" else ""}"
        var whereArgs = arrayOf(incomeID.toString())
        if (userID != null)
            whereArgs += userID.toString()

        try {
            val databaseCursor = DatabaseHelper(context).writableDatabase

            databaseCursor?.delete(DatabaseHelper.INCOME.TABLE_NAME, where, whereArgs)
        }catch (e: SQLiteException){
            throw e
        }
    }

    fun editIncome(context: Context, userID: Int?, incomeID: Int, value: Double, name: String, profit: Boolean){
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

    fun generateReport(userID: Int? = this.userID){
//        TODO ("Listar e calcular o total de ganho, perda e valor total do usuario X dentro de um mes")
    }

    fun exportReport(userID: Int? = this.userID){
//        TODO("Exportar em o relatorio do usuario X")
    }

}