package com.example.financeback.classes

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

object Usuarios {
    const val TABELA_NOME = "usuarios"
    const val COLUNA_ID = "ID"
    const val COLUNA_NOME = "Nome"
    const val COLUNA_EMAIL = "Email"
    const val COLUNA_SENHA = "Senha"
}
class Banco(context: Context) : SQLiteOpenHelper(context, BANCO_NOME, null, BANCO_VERSAO) {
    object NotasFiscais {
        const val TABELA_NOME = "notas_fiscais"
        const val COLUNA_ID = "ID"
        const val COLUNA_USUARIO = "UsuarioID"
        const val COLUNA_VALOR = "Valor"
        const val COLUNA_NOME = "Nome"
        const val COLUNA_DATA = "DataStamp"
        const val COLUNA_LUCRO = "Lucro"
    }
    companion object {
        const val BANCO_NOME = "FINANCEBACK"
        const val BANCO_VERSAO = 1
    }

    private val CRIAR_NOTAS_FISCAIS =
        "CREATE TABLE IF NOT EXISTS ${NotasFiscais.TABELA_NOME}(" +
                "${NotasFiscais.COLUNA_ID} INTEGER PRIMARY KEY," +
                "${NotasFiscais.COLUNA_USUARIO} INTEGER," +
                "${NotasFiscais.COLUNA_VALOR} REAL DEFAULT NULL," +
                "${NotasFiscais.COLUNA_NOME} TEXT DEFAULT NULL," +
                "${NotasFiscais.COLUNA_DATA} INTEGER DEFAULT NULL," +
                "${NotasFiscais.COLUNA_LUCRO} BOOLEAN DEFAULT FALSE," +
                "FOREIGN KEY (${NotasFiscais.COLUNA_USUARIO})" +
                "REFERENCES ${Usuarios.TABELA_NOME}(${Usuarios.COLUNA_ID}))"

    private val CRIAR_USUARIOS =
        "CREATE TABLE IF NOT EXISTS ${Usuarios.TABELA_NOME}(" +
                "${Usuarios.COLUNA_ID} INTEGER PRIMARY KEY," +
                "${Usuarios.COLUNA_NOME} TEXT DEFAULT NULL," +
                "${Usuarios.COLUNA_EMAIL} TEXT DEFAULT NULL," +
                "${Usuarios.COLUNA_SENHA} TEXT DEFAULT NULL)"

    override fun onCreate(db: SQLiteDatabase) {
        try {
            db.execSQL(CRIAR_USUARIOS)
            db.execSQL(CRIAR_NOTAS_FISCAIS)
        }catch (e: SQLException){
            throw e
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

}