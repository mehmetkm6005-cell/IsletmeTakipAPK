package com.mehmet.isletmetakip

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : Activity() {

    private val jsonUrl =
        "https://raw.githubusercontent.com/mehmetkm6005-cell/IsletmeTakipAPK/main/app/src/main/assets/isletme_takip_data.json"

    private var data: JSONObject? = null
    private var sheetNames = ArrayList<String>()
    private var currentSheetName: String? = null

    private lateinit var rootLayout: LinearLayout

    private val refreshHandler = Handler(Looper.getMainLooper())

    private val refreshRunnable = object : Runnable {
        override fun run() {
            loadDataFromGitHub(
                showSuccessMessage = false,
                refreshCurrentSheet = true
            )

            refreshHandler.postDelayed(
                this,
                5 * 60 * 1000L
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showLoginScreen()

        loadDataFromGitHub(
            showSuccessMessage = true,
            refreshCurrentSheet = false
        )

        refreshHandler.postDelayed(
            refreshRunnable,
            5 * 60 * 1000L
        )
    }

    override fun onDestroy() {
        refreshHandler.removeCallbacks(refreshRunnable)
        super.onDestroy()
    }

    private fun loadDataFromGitHub(
        showSuccessMessage: Boolean,
        refreshCurrentSheet: Boolean
    ) {
        thread {

            try {
                val url = URL(
                    jsonUrl + "?t=" + System.currentTimeMillis()
                )

                val connection =
                    url.openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 20000

                connection.setRequestProperty(
                    "Cache-Control",
                    "no-cache, no-store"
                )

                connection.setRequestProperty(
                    "Pragma",
                    "no-cache"
                )

                val responseCode =
                    connection.responseCode

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw Exception(
                        "HTTP $responseCode"
                    )
                }

                val jsonText =
                    connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                connection.disconnect()

                val jsonObject =
                    JSONObject(jsonText)

                val newSheetNames =
                    ArrayList<String>()

                if (jsonObject.has("sheetOrder")) {

                    val orderArray =
                        jsonObject.getJSONArray(
                            "sheetOrder"
                        )

                    for (
                        i in 0 until orderArray.length()
                    ) {
                        newSheetNames.add(
                            orderArray.getString(i)
                        )
                    }

                } else {

                    val sheetsObject =
                        jsonObject.getJSONObject(
                            "sheets"
                        )

                    val keys =
                        sheetsObject.keys()

                    while (keys.hasNext()) {
                        newSheetNames.add(
                            keys.next()
                        )
                    }
                }

                data = jsonObject
                sheetNames = newSheetNames

                runOnUiThread {

                    if (
                        refreshCurrentSheet &&
                        currentSheetName != null
                    ) {
                        showSheet(
                            currentSheetName!!
                        )
                    }

                    if (showSuccessMessage) {
                        Toast.makeText(
                            this,
                            "Veriler GitHub'dan güncellendi.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {

                e.printStackTrace()

                runOnUiThread {

                    if (data == null) {
                        loadLocalData()
                    }

                    if (showSuccessMessage) {
                        Toast.makeText(
                            this,
                            "GitHub verisine ulaşılamadı. Yerel veri kullanılıyor.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun loadLocalData() {

        try {

            val inputStream =
                assets.open(
                    "isletme_takip_data.json"
                )

            val jsonText =
                inputStream
                    .bufferedReader()
                    .use { it.readText() }

            inputStream.close()

            val jsonObject =
                JSONObject(jsonText)

            data = jsonObject

            val newSheetNames =
                ArrayList<String>()

            if (jsonObject.has("sheetOrder")) {

                val orderArray =
                    jsonObject.getJSONArray(
                        "sheetOrder"
                    )

                for (
                    i in 0 until orderArray.length()
                ) {
                    newSheetNames.add(
                        orderArray.getString(i)
                    )
                }

            } else {

                val sheetsObject =
                    jsonObject.getJSONObject(
                        "sheets"
                    )

                val keys =
                    sheetsObject.keys()

                while (keys.hasNext()) {
                    newSheetNames.add(
                        keys.next()
                    )
                }
            }

            sheetNames = newSheetNames

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createRoot(): LinearLayout {

        rootLayout =
            LinearLayout(this)

        rootLayout.orientation =
            LinearLayout.VERTICAL

        rootLayout.setBackgroundColor(
            Color.WHITE
        )

        rootLayout.layoutParams =
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

        return rootLayout
    }

    private fun addLogoBar(
        parent: LinearLayout
    ) {

        val logoBar =
            LinearLayout(this)

        logoBar.orientation =
            LinearLayout.HORIZONTAL

        logoBar.gravity =
            Gravity.CENTER_VERTICAL

        logoBar.setPadding(
            20,
            14,
            20,
            14
        )

        logoBar.setBackgroundColor(
            Color.rgb(0, 102, 204)
        )

        val logo =
            TextView(this)

        logo.text =
            "İŞLETME TAKİP"

        logo.textSize =
            19f

        logo.setTextColor(
            Color.WHITE
        )

        logo.setTypeface(
            null,
            Typeface.BOLD
        )

        logo.gravity =
            Gravity.CENTER_VERTICAL

        logo.setOnClickListener {

            currentSheetName = null

            showMainMenu()
        }

        logoBar.addView(
            logo,
            LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        parent.addView(
            logoBar,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun showLoginScreen() {

        val root =
            createRoot()

        addLogoBar(root)

        val scroll =
            ScrollView(this)

        val content =
            LinearLayout(this)

        content.orientation =
            LinearLayout.VERTICAL

        content.gravity =
            Gravity.CENTER_HORIZONTAL

        content.setPadding(
            40,
            70,
            40,
            40
        )

        val title =
            TextView(this)

        title.text =
            "İŞLETME TAKİP"

        title.textSize =
            28f

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.setTextColor(
            Color.rgb(0, 102, 204)
        )

        title.gravity =
            Gravity.CENTER

        content.addView(
            title,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val subtitle =
            TextView(this)

        subtitle.text =
            "Kullanıcı Girişi"

        subtitle.textSize =
            20f

        subtitle.setTypeface(
            null,
            Typeface.BOLD
        )

        subtitle.gravity =
            Gravity.CENTER

        val subtitleParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        subtitleParams.setMargins(
            0,
            20,
            0,
            35
        )

        content.addView(
            subtitle,
            subtitleParams
        )

        val username =
            EditText(this)

        username.hint =
            "Kullanıcı Adı"

        username.inputType =
            InputType.TYPE_CLASS_TEXT

        content.addView(
            username,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val password =
            EditText(this)

        password.hint =
            "Şifre"

        password.inputType =
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_VARIATION_PASSWORD

        val passwordParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

        passwordParams.setMargins(
            0,
            15,
            0,
            20
        )

        content.addView(
            password,
            passwordParams
        )

        val loginButton =
            Button(this)

        loginButton.text =
            "GİRİŞ YAP"

        loginButton.setOnClickListener {

            showMainMenu()
        }

        content.addView(
            loginButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        scroll.addView(content)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
    }

    private fun showMainMenu() {

        currentSheetName = null

        val root =
            createRoot()

        addLogoBar(root)

        val scroll =
            ScrollView(this)

        val menu =
            LinearLayout(this)

        menu.orientation =
            LinearLayout.VERTICAL

        menu.setPadding(
            20,
            20,
            20,
            30
        )

        for (sheetName in sheetNames) {

            if (
                sheetName.equals(
                    "ANA SAYFA",
                    ignoreCase = true
                )
            ) {
                continue
            }

            if (
                sheetName.endsWith(
                    "-data",
                    ignoreCase = true
                )
            ) {
                continue
            }

            val button =
                Button(this)

            button.text =
                sheetName

            button.textSize =
                16f

            button.gravity =
                Gravity.CENTER

            button.setOnClickListener {
                showSheet(sheetName)
            }

            val params =
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

            params.setMargins(
                0,
                0,
                0,
                10
            )

            menu.addView(
                button,
                params
            )
        }

        scroll.addView(menu)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
    }

    private fun showSheet(
        sheetName: String
    ) {

        currentSheetName =
            sheetName

        val root =
            createRoot()

        addLogoBar(root)

        val scroll =
            ScrollView(this)

        val horizontal =
            HorizontalScrollView(this)

        val table =
            TableLayout(this)

        table.setPadding(
            10,
            10,
            10,
            30
        )

        val jsonData =
            data

        if (jsonData == null) {

            val error =
                TextView(this)

            error.text =
                "Veri bulunamadı."

            error.textSize =
                18f

            error.setPadding(
                30,
                30,
                30,
                30
            )

            table.addView(error)

        } else {

            try {

                val sheetsObject =
                    jsonData.getJSONObject(
                        "sheets"
                    )

                val sheetObject =
                    sheetsObject.getJSONObject(
                        sheetName
                    )

                val cells =
                    sheetObject.getJSONArray(
                        "cells"
                    )

                val rows =
                    LinkedHashMap<
                            Int,
                            MutableList<CellData>
                            >()

                for (
                    i in 0 until cells.length()
                ) {

                    val cell =
                        cells.getJSONObject(i)

                    val address =
                        cell.optString(
                            "r",
                            ""
                        )

                    val value =
                        cell.optString(
                            "v",
                            ""
                        )

                    val rowNumber =
                        extractRowNumber(
                            address
                        )

                    val columnNumber =
                        extractColumnNumber(
                            address
                        )

                    if (rowNumber > 0) {

                        if (
                            !rows.containsKey(
                                rowNumber
                            )
                        ) {
                            rows[rowNumber] =
                                ArrayList()
                        }

                        rows[rowNumber]!!.add(
                            CellData(
                                columnNumber,
                                value
                            )
                        )
                    }
                }

                for (
                    (_, rowCells) in rows
                ) {

                    rowCells.sortBy {
                        it.column
                    }

                    val row =
                        TableRow(this)

                    for (
                        cell in rowCells
                    ) {

                        val text =
                            TextView(this)

                        text.text =
                            cell.value

                        text.textSize =
                            14f

                        text.setTextColor(
                            Color.BLACK
                        )

                        text.setPadding(
                            12,
                            10,
                            12,
                            10
                        )

                        text.setBackgroundResource(
                            android.R.drawable.editbox_background
                        )

                        row.addView(
                            text,
                            TableRow.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                        )
                    }

                    table.addView(row)
                }

            } catch (e: Exception) {

                e.printStackTrace()

                val error =
                    TextView(this)

                error.text =
                    "Bu sekmenin verileri okunamadı."

                error.textSize =
                    18f

                error.setPadding(
                    30,
                    30,
                    30,
                    30
                )

                table.addView(error)
            }
        }

        horizontal.addView(table)

        scroll.addView(horizontal)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
    }

    private fun extractRowNumber(
        address: String
    ): Int {

        val digits =
            address.filter {
                it.isDigit()
            }

        return digits.toIntOrNull() ?: 0
    }

    private fun extractColumnNumber(
        address: String
    ): Int {

        val letters =
            address
                .filter {
                    it.isLetter()
                }
                .uppercase()

        var result = 0

        for (char in letters) {

            result =
                result * 26 +
                        (char - 'A' + 1)
        }

        return result
    }

    private data class CellData(
        val column: Int,
        val value: String
    )
}
