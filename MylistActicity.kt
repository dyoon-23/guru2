package com.example.beepme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MylistActivity : AppCompatActivity() {
    private lateinit var tableLayout: TableLayout
    private lateinit var resetButton: Button
    private lateinit var savedDBHelper: SavedDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mylist)

        tableLayout = findViewById(R.id.tableLayout)
        resetButton = findViewById(R.id.resetButton)
        savedDBHelper = SavedDBHelper(this)

        resetButton.setOnClickListener {
            // 데이터 리셋 버튼 클릭 시
            GlobalScope.launch(Dispatchers.IO) {
                savedDBHelper.resetData()
                loadDataAndDisplay()
            }
        }

        loadDataAndDisplay()
    }

    private fun loadDataAndDisplay() {
        // 데이터 로드 및 화면에 표시
        GlobalScope.launch(Dispatchers.IO) {
            val data = loadDataFromDB()

            withContext(Dispatchers.Main) {
                displayDataInTable(data)
            }
        }
    }

    private suspend fun loadDataFromDB(): List<Triple<String, String, String>> {
        // 데이터베이스에서 데이터 로드
        val result = mutableListOf<Triple<String, String, String>>()

        val sqlDB = savedDBHelper.writableDatabase

        val query = "SELECT * FROM savedTBL"
        val cursor = sqlDB.rawQuery(query, null)

        // 제품명, 알러지 정보, 제조사를 불러옴
        while (cursor.moveToNext()) {
            val prdlstNmValue = cursor.getString(1)
            val allergyValue = cursor.getString(2)
            val manufactureValue = cursor.getString(4)

            result.add(Triple(prdlstNmValue, allergyValue, manufactureValue))
        }

        cursor.close()
        sqlDB.close()

        return result
    }

    private fun displayDataInTable(data: List<Triple<String, String, String>>) {
        // 테이블에 데이터 표시
        tableLayout.removeAllViews()

        // 테이블 헤더 추가
        val headerRow = TableRow(this)

        val prdlstNmHeader = TextView(this)
        prdlstNmHeader.text = "상품명"
        prdlstNmHeader.textSize = 18f
        prdlstNmHeader.gravity = Gravity.LEFT
        headerRow.addView(prdlstNmHeader)

        val allergyHeader = TextView(this)
        allergyHeader.text = "알러지정보"
        allergyHeader.textSize = 18f
        allergyHeader.gravity = Gravity.LEFT
        headerRow.addView(allergyHeader)

        val manufactureHeader = TextView(this)
        manufactureHeader.text = "제조사"
        manufactureHeader.textSize = 18f
        manufactureHeader.gravity = Gravity.LEFT
        headerRow.addView(manufactureHeader)

        tableLayout.addView(headerRow)

        // 데이터 행 추가
        for (item in data) {
            val row = TableRow(this)

            val prdlstNmTextView = TextView(this)
            prdlstNmTextView.text = item.first
            prdlstNmTextView.textSize = 16f
            prdlstNmTextView.gravity = Gravity.LEFT
            // 줄이 길어질 때 화면에서 잘리지 않기 위해 layout_width를 0dp로 설정하여 layout_weight를 사용하고, 가로 폭을 1:1:1로 나눔
            prdlstNmTextView.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            row.addView(prdlstNmTextView)

            val allergyTextView = TextView(this)
            allergyTextView.text = item.second
            allergyTextView.textSize = 16f
            allergyTextView.gravity = Gravity.LEFT
            allergyTextView.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            row.addView(allergyTextView)

            val manufactureTextView = TextView(this)
            manufactureTextView.text = item.third
            manufactureTextView.textSize = 16f
            manufactureTextView.gravity = Gravity.LEFT
            manufactureTextView.layoutParams = TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            row.addView(manufactureTextView)

            tableLayout.addView(row, TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ))
        }
    }
}
