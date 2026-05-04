package com.example.lab04

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var etSearchWord: EditText
    private lateinit var btnLookup: Button
    private lateinit var tvResult: TextView
    private lateinit var dbHelper: DictionaryDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ View
        etSearchWord = findViewById(R.id.etSearchWord)
        btnLookup = findViewById(R.id.btnLookup)
        tvResult = findViewById(R.id.tvResult)

        // Khởi tạo Database Helper
        dbHelper = DictionaryDatabaseHelper(this)

        // Xử lý sự kiện khi bấm nút LOOKUP
        btnLookup.setOnClickListener {
            val inputWord = etSearchWord.text.toString().trim()
            if (inputWord.isNotEmpty()) {
                searchInDictionary(inputWord)
            } else {
                tvResult.text = "Vui lòng nhập từ cần tra."
            }
        }
    }

    private fun searchInDictionary(inputText: String) {
        val db = dbHelper.readableDatabase
        var cursor = db.rawQuery(
            "SELECT * FROM ${DictionaryDatabaseHelper.TABLE_WORDS} WHERE ${DictionaryDatabaseHelper.COLUMN_WORD} = ?",
            arrayOf(inputText)
        )

        if (cursor.moveToFirst()) {
            // Nếu có từ khớp chính xác (exact match) -> Hiển thị định nghĩa
            val definitionIndex = cursor.getColumnIndex(DictionaryDatabaseHelper.COLUMN_DEFINITION)
            if (definitionIndex != -1) {
                val definition = cursor.getString(definitionIndex)
                tvResult.text = "Định nghĩa:\n$definition"
            }
            cursor.close()
        } else {
            // Đóng cursor cũ trước khi mở cursor mới
            cursor.close()

            // Nếu không khớp chính xác -> Tìm chuỗi con (substring) dùng toán tử LIKE
            val likeQuery = "%$inputText%"
            cursor = db.rawQuery(
                "SELECT * FROM ${DictionaryDatabaseHelper.TABLE_WORDS} WHERE ${DictionaryDatabaseHelper.COLUMN_WORD} LIKE ?",
                arrayOf(likeQuery)
            )

            if (cursor.moveToFirst()) {
                // Liệt kê tất cả các từ có chứa chuỗi đã nhập
                val stringBuilder = StringBuilder()
                stringBuilder.append("Không tìm thấy từ chính xác. Các từ chứa '$inputText':\n\n")

                val wordIndex = cursor.getColumnIndex(DictionaryDatabaseHelper.COLUMN_WORD)
                if (wordIndex != -1) {
                    do {
                        val matchedWord = cursor.getString(wordIndex)
                        stringBuilder.append("- $matchedWord\n")
                    } while (cursor.moveToNext())
                }

                tvResult.text = stringBuilder.toString()
            } else {
                tvResult.text = "Không tìm thấy kết quả nào trong từ điển."
            }
            cursor.close()
        }
    }
}
