package com.example.lab04

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DictionaryDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "DictionaryDB.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_WORDS = "words"
        const val COLUMN_ID = "id"
        const val COLUMN_WORD = "word"
        const val COLUMN_DEFINITION = "definition"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tạo bảng chứa từ vựng
        val createTableQuery = ("CREATE TABLE $TABLE_WORDS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_WORD TEXT, "
                + "$COLUMN_DEFINITION TEXT)")
        db.execSQL(createTableQuery)

        // Thêm dữ liệu mẫu vào từ điển khi vừa tạo xong
        insertSampleData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORDS")
        onCreate(db)
    }

    private fun insertSampleData(db: SQLiteDatabase) {
        val words = listOf(
            Pair("verisimilitude", "the appearance of being true or real"),
            Pair("apple", "a round fruit with red or green skin and a whitish interior"),
            Pair("application", "a formal request to an authority for something"),
            Pair("verify", "make sure or demonstrate that something is true"),
            Pair("android", "a mobile operating system developed by Google")
        )

        for (word in words) {
            val values = ContentValues().apply {
                put(COLUMN_WORD, word.first)
                put(COLUMN_DEFINITION, word.second)
            }
            db.insert(TABLE_WORDS, null, values)
        }
    }
}
