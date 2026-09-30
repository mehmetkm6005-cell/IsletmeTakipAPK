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
import android.widget.Space
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import kotlin.concurrent.thread

class MainActivity : Activity() {

    private lateinit var root: LinearLayout

    private val sheetNames = ArrayList<String>()

    private lateinit var jsonData: JSONObject

    private var currentSheetName: String? = null

    private var isLoadingData = false

    private var loggedInUsername: String? = null

    private val jsonUrl =
        "https://raw.githubusercontent.com/mehmetkm6005-cell/IsletmeTakipAPK/main/app/src/main/assets/isletme_takip_data.json"

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

    private fun dp(value: Int): Int {
        return (
            value * resources.displayMetrics.density
        ).toInt()
    }

    // ============================================================
    // KULLANICI YETKİLENDİRME
    // ============================================================

    data class AppUser(
        val username: String,
        val passwordHash: String,
        val active: Boolean,
        val allowedSheets: Set<String>
    )

    private fun sha256(text: String): String {

        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(text.toByteArray())

        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }

    private fun allSheetAccess(): Set<String> {
        return setOf("*")
    }

    private fun createUsers(): List<AppUser> {

        return listOf(

            AppUser(
                username = "MK0560",
                passwordHash = sha256("0560"),
                active = true,
                allowedSheets = allSheetAccess()
            ),

            AppUser(
                username = "BH0560",
                passwordHash = sha256("0560"),
                active = true,
                allowedSheets = allSheetAccess()
            )

        )
    }

    private fun authenticateUser(
        username: String,
        password: String
    ): AppUser? {

        val user = createUsers().firstOrNull {
            it.username.equals(
                username.trim(),
                ignoreCase = true
            )
        } ?: return null

        if (!user.active) {
            return null
        }

        val enteredHash = sha256(password)

        if (enteredHash != user.passwordHash) {
            return null
        }

        return user
    }

    private fun isSheetAuthorized(
        sheetName: String
    ): Boolean {

        val username = loggedInUsername
            ?: return false

        val user = createUsers().firstOrNull {
            it.username.equals(
                username,
                ignoreCase = true
            )
        } ?: return false

        if (!user.active) {
            return false
        }

        if (user.allowedSheets.contains("*")) {
            return true
        }

        return user.allowedSheets.any {
            it.equals(
                sheetName,
                ignoreCase = true
            )
        }
    }

    private fun getAuthorizedSheetNames(
        allNames: ArrayList<String>
    ): ArrayList<String> {

        val result = ArrayList<String>()

        for (name in allNames) {

            if (isSheetAuthorized(name)) {
                result.add(name)
            }
        }

        return result
    }

    // ============================================================
    // UYGULAMA
    // ============================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
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

        refreshHandler.removeCallbacks(
            refreshRunnable
        )

        super.onDestroy()
    }

    // ============================================================
    // GITHUB VERİ ALMA
    // ============================================================

    private fun loadDataFromGitHub(
        showSuccessMessage: Boolean,
        refreshCurrentSheet: Boolean
    ) {

        if (isLoadingData) {
            return
        }

        isLoadingData = true

        thread {

            try {

                val cacheBust =
                    System.currentTimeMillis()

                val url =
                    URL("$jsonUrl?t=$cacheBust")

                val connection =
                    url.openConnection()
                        as HttpURLConnection

                connection.requestMethod = "GET"

                connection.connectTimeout = 15000

                connection.readTimeout = 15000

                connection.useCaches = false

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

                if (
                    responseCode !=
                    HttpURLConnection.HTTP_OK
                ) {

                    throw Exception(
                        "GitHub bağlantısı başarısız. Kod: $responseCode"
                    )
                }

                val text =
                    connection
                        .inputStream
                        .bufferedReader()
                        .use {
                            it.readText()
                        }

                connection.disconnect()

                val newJson =
                    JSONObject(text)

                val allNames =
                    getVisibleSheetNames(
                        newJson
                    )

                runOnUiThread {

                    jsonData = newJson

                    if (loggedInUsername != null) {

                        sheetNames.clear()

                        sheetNames.addAll(
                            getAuthorizedSheetNames(
                                allNames
                            )
                        )

                    } else {

                        sheetNames.clear()

                        sheetNames.addAll(
                            allNames
                        )
                    }

                    isLoadingData = false

                    if (showSuccessMessage) {

                        Toast.makeText(
                            this,
                            "Güncel veriler GitHub'dan alındı.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    if (
                        refreshCurrentSheet &&
                        currentSheetName != null &&
                        loggedInUsername != null
                    ) {

                        val sheet =
                            currentSheetName

                        if (
                            sheet != null &&
                            sheetNames.contains(sheet)
                        ) {

                            showSheet(sheet)
                        }
                    }
                }

            } catch (e: Exception) {

                runOnUiThread {

                    isLoadingData = false

                    if (showSuccessMessage) {

                        loadLocalData()

                        Toast.makeText(
                            this,
                            "Güncel veri alınamadı. Yerel veri kullanılıyor.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    private fun getVisibleSheetNames(
        jsonObject: JSONObject
    ): ArrayList<String> {

        val result =
            ArrayList<String>()

        try {

            if (
                jsonObject.has("sheetOrder")
            ) {

                val order =
                    jsonObject.getJSONArray(
                        "sheetOrder"
                    )

                for (
                    i in 0 until order.length()
                ) {

                    val name =
                        order.getString(i)

                    if (
                        name.equals(
                            "ANA SAYFA",
                            ignoreCase = true
                        )
                    ) {
                        continue
                    }

                    if (
                        name.endsWith(
                            "-data",
                            ignoreCase = true
                        )
                    ) {
                        continue
                    }

                    result.add(name)
                }

            } else {

                val sheets =
                    jsonObject.getJSONObject(
                        "sheets"
                    )

                val keys =
                    sheets.keys()

                while (keys.hasNext()) {

                    val name =
                        keys.next()

                    if (
                        name.equals(
                            "ANA SAYFA",
                            ignoreCase = true
                        )
                    ) {
                        continue
                    }

                    if (
                        name.endsWith(
                            "-data",
                            ignoreCase = true
                        )
                    ) {
                        continue
                    }

                    result.add(name)
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

        return result
    }

    private fun loadLocalData() {

        try {

            val text =
                assets.open(
                    "isletme_takip_data.json"
                )
                    .bufferedReader()
                    .use {
                        it.readText()
                    }

            jsonData =
                JSONObject(text)

            sheetNames.clear()

            val allNames =
                getVisibleSheetNames(
                    jsonData
                )

            if (loggedInUsername != null) {

                sheetNames.addAll(
                    getAuthorizedSheetNames(
                        allNames
                    )
                )
            }

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "JSON okunamadı: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ============================================================
    // ORTAK TASARIM
    // ============================================================

    private fun createRoot() {

        root =
            LinearLayout(this)

        root.orientation =
            LinearLayout.VERTICAL

        root.setBackgroundColor(
            Color.WHITE
        )

        root.setPadding(
            dp(12),
            dp(10),
            dp(12),
            dp(10)
        )

        setContentView(root)
    }

    private fun addLogoBar() {

        val bar =
            LinearLayout(this)

        bar.orientation =
            LinearLayout.HORIZONTAL

        bar.gravity =
            Gravity.CENTER_VERTICAL

        val logo =
            TextView(this)

        logo.text = "İT"

        logo.textSize = 20f

        logo.setTypeface(
            null,
            Typeface.BOLD
        )

        logo.gravity =
            Gravity.CENTER

        logo.setTextColor(
            Color.WHITE
        )

        logo.setBackgroundColor(
            Color.rgb(
                0,
                83,
                155
            )
        )

        logo.setOnClickListener {

            currentSheetName = null

            if (loggedInUsername != null) {
                showMainMenu()
            } else {
                showLoginScreen()
            }
        }

        bar.addView(
            logo,
            LinearLayout.LayoutParams(
                dp(55),
                dp(55)
            )
        )

        val title =
            TextView(this)

        title.text =
            "  İŞLETME TAKİP"

        title.textSize = 20f

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.setTextColor(
            Color.rgb(
                0,
                83,
                155
            )
        )

        title.gravity =
            Gravity.CENTER_VERTICAL

        bar.addView(
            title,
            LinearLayout.LayoutParams(
                0,
                dp(55),
                1f
            )
        )

        root.addView(bar)

        val space =
            Space(this)

        root.addView(
            space,
            LinearLayout.LayoutParams(
                1,
                dp(10)
            )
        )
    }

    // ============================================================
    // GİRİŞ EKRANI
    // ============================================================

    private fun showLoginScreen() {

        currentSheetName = null

        createRoot()

        addLogoBar()

        val title =
            TextView(this)

        title.text =
            "Kullanıcı Girişi"

        title.textSize = 24f

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.gravity =
            Gravity.CENTER

        title.setTextColor(
            Color.rgb(
                0,
                83,
                155
            )
        )

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        val username =
            EditText(this)

        username.hint =
            "Kullanıcı Adı"

        username.setSingleLine(true)

        root.addView(
            username,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            ).apply {
                topMargin = dp(20)
            }
        )

        val password =
            EditText(this)

        password.hint =
            "Şifre"

        password.setSingleLine(true)

        password.inputType =
            InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PASSWORD

        root.addView(
            password,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            ).apply {
                topMargin = dp(10)
            }
        )

        val login =
            Button(this)

        login.text =
            "GİRİŞ YAP"

        login.textSize =
            16f

        login.setOnClickListener {

            val enteredUsername =
                username.text
                    .toString()
                    .trim()

            val enteredPassword =
                password.text
                    .toString()

            if (
                enteredUsername.isEmpty() ||
                enteredPassword.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Kullanıcı adı ve şifre giriniz.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val user =
                authenticateUser(
                    enteredUsername,
                    enteredPassword
                )

            if (user == null) {

                Toast.makeText(
                    this,
                    "Kullanıcı adı veya şifre hatalı.",
                    Toast.LENGTH_LONG
                ).show()

                password.text.clear()

                return@setOnClickListener
            }

            loggedInUsername =
                user.username

            val allNames =
                getVisibleSheetNames(
                    jsonData
                )

            sheetNames.clear()

            sheetNames.addAll(
                getAuthorizedSheetNames(
                    allNames
                )
            )

            showMainMenu()
        }

        root.addView(
            login,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            ).apply {
                topMargin = dp(20)
            }
        )
    }

    // ============================================================
    // ANA MENÜ
    // ============================================================

    private fun showMainMenu() {

        currentSheetName = null

        createRoot()

        addLogoBar()

        val title =
            TextView(this)

        title.text =
            "ANA MENÜ"

        title.textSize = 24f

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.gravity =
            Gravity.CENTER

        title.setTextColor(
            Color.rgb(
                0,
                83,
                155
            )
        )

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(50)
            )
        )

        val scroll =
            ScrollView(this)

        val menu =
            LinearLayout(this)

        menu.orientation =
            LinearLayout.VERTICAL

        menu.setPadding(
            0,
            dp(10),
            0,
            dp(20)
        )

        for (
            name in sheetNames
        ) {

            val button =
                Button(this)

            button.text =
                name

            button.textSize =
                15f

            button.setAllCaps(false)

            button.setOnClickListener {

                if (
                    isSheetAuthorized(name)
                ) {

                    showSheet(name)

                } else {

                    Toast.makeText(
                        this,
                        "Bu sekmeye yetkiniz yok.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            menu.addView(
                button,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(55)
                ).apply {
                    bottomMargin = dp(6)
                }
            )
        }

        scroll.addView(menu)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val logout =
            Button(this)

        logout.text =
            "ÇIKIŞ YAP"

        logout.textSize =
            15f

        logout.setOnClickListener {

            loggedInUsername = null

            currentSheetName = null

            sheetNames.clear()

            showLoginScreen()
        }

        root.addView(
            logout,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )
    }

    // ============================================================
    // SEKME GÖSTERİMİ
    // ============================================================

    private fun showSheet(
        sheetName: String
    ) {

        if (!isSheetAuthorized(sheetName)) {

            Toast.makeText(
                this,
                "Bu sekmeye yetkiniz yok.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        currentSheetName =
            sheetName

        createRoot()

        addLogoBar()

        val titleBar =
            LinearLayout(this)

        titleBar.orientation =
            LinearLayout.VERTICAL

        titleBar.gravity =
            Gravity.CENTER

        titleBar.setBackgroundColor(
            Color.rgb(
                0,
                83,
                155
            )
        )

        val title =
            TextView(this)

        title.text =
            sheetName

        title.textSize =
            20f

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.setTextColor(
            Color.WHITE
        )

        title.gravity =
            Gravity.CENTER

        titleBar.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(50)
            )
        )

        root.addView(
            titleBar,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        try {

            val sheets =
                jsonData.getJSONObject(
                    "sheets"
                )

            val sheetObject =
                sheets.getJSONObject(
                    sheetName
                )

            val cells =
                sheetObject.getJSONArray(
                    "cells"
                )

            val cellMap =
                HashMap<String, String>()

            var maxRow = 0
            var maxCol = 0

            for (
                i in 0 until cells.length()
            ) {

                val cell =
                    cells.getJSONObject(i)

                val address =
                    cell.optString("r")

                val value =
                    cell.optString(
                        "v",
                        ""
                    )

                if (
                    address.isEmpty()
                ) {
                    continue
                }

                cellMap[address] =
                    value

                val match =
                    Regex(
                        "^([A-Z]+)([0-9]+)$"
                    ).find(address)

                if (match != null) {

                    val colLetters =
                        match.groupValues[1]

                    val rowNumber =
                        match.groupValues[2].toInt()

                    var columnNumber =
                        0

                    for (
                        ch in colLetters
                    ) {

                        columnNumber =
                            columnNumber * 26 +
                                (
                                    ch - 'A' + 1
                                )
                    }

                    if (
                        rowNumber > maxRow
                    ) {

                        maxRow =
                            rowNumber
                    }

                    if (
                        columnNumber > maxCol
                    ) {

                        maxCol =
                            columnNumber
                    }
                }
            }

            val verticalScroll =
                ScrollView(this)

            verticalScroll.setFillViewport(
                true
            )

            val horizontalScroll =
                HorizontalScrollView(this)

            horizontalScroll.setFillViewport(
                true
            )

            val table =
                TableLayout(this)

            table.setPadding(
                dp(4),
                dp(6),
                dp(4),
                dp(20)
            )

            val visibleColumns =
                ArrayList<Int>()

            for (
                columnNumber in 1..maxCol
            ) {

                var hasValue =
                    false

                for (
                    rowNumber in 1..maxRow
                ) {

                    val address =
                        columnNumberToLetters(
                            columnNumber
                        ) + rowNumber

                    val value =
                        cellMap[address] ?: ""

                    if (
                        value.isNotBlank()
                    ) {

                        hasValue =
                            true

                        break
                    }
                }

                if (hasValue) {

                    visibleColumns.add(
                        columnNumber
                    )
                }
            }

            for (
                rowNumber in 1..maxRow
            ) {

                val tableRow =
                    TableRow(this)

                tableRow.setPadding(
                    0,
                    dp(1),
                    0,
                    dp(1)
                )

                for (
                    columnNumber in visibleColumns
                ) {

                    val columnLetters =
                        columnNumberToLetters(
                            columnNumber
                        )

                    val address =
                        columnLetters +
                            rowNumber

                    val textValue =
                        cellMap[address] ?: ""

                    val cell =
                        TextView(this)

                    cell.text =
                        textValue

                    cell.textSize =
                        if (
                            rowNumber <= 3
                        ) {
                            13f
                        } else {
                            12f
                        }

                    cell.gravity =
                        Gravity.CENTER_VERTICAL

                    cell.setPadding(
                        dp(10),
                        dp(9),
                        dp(10),
                        dp(9)
                    )

                    if (
                        rowNumber <= 3 &&
                        textValue.isNotBlank()
                    ) {

                        cell.setTypeface(
                            null,
                            Typeface.BOLD
                        )

                        cell.setTextColor(
                            Color.WHITE
                        )

                        cell.setBackgroundColor(
                            Color.rgb(
                                0,
                                83,
                                155
                            )
                        )

                    } else {

                        cell.setTextColor(
                            Color.rgb(
                                45,
                                45,
                                45
                            )
                        )

                        if (
                            rowNumber % 2 == 0
                        ) {

                            cell.setBackgroundColor(
                                Color.rgb(
                                    240,
                                    246,
                                    252
                                )
                            )

                        } else {

                            cell.setBackgroundColor(
                                Color.WHITE
                            )
                        }
                    }

                    val params =
                        TableRow.LayoutParams(
                            dp(145),
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )

                    params.setMargins(
                        dp(1),
                        dp(1),
                        dp(1),
                        dp(1)
                    )

                    tableRow.addView(
                        cell,
                        params
                    )
                }

                table.addView(
                    tableRow
                )
            }

            horizontalScroll.addView(
                table
            )

            verticalScroll.addView(
                horizontalScroll
            )

            root.addView(
                verticalScroll,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f
                )
            )

        } catch (e: Exception) {

            val error =
                TextView(this)

            error.text =
                "Veriler gösterilemedi.\n\n$e"

            error.textSize =
                16f

            error.gravity =
                Gravity.CENTER

            error.setPadding(
                dp(15),
                dp(15),
                dp(15),
                dp(15)
            )

            root.addView(
                error,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f
                )
            )
        }

        val back =
            Button(this)

        back.text =
            "ANA MENÜYE DÖN"

        back.textSize =
            15f

        back.setOnClickListener {

            currentSheetName =
                null

            showMainMenu()
        }

        root.addView(
            back,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )
    }

    private fun columnNumberToLetters(
        columnNumber: Int
    ): String {

        var number =
            columnNumber

        var result =
            ""

        while (
            number > 0
        ) {

            val remainder =
                (number - 1) % 26

            result =
                (
                    'A'.code + remainder
                )
                    .toChar()
                    .toString() +
                    result

            number =
                (number - 1) / 26
        }

        return result
    }
}
