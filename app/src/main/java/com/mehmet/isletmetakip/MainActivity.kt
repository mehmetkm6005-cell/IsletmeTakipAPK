package com.mehmet.isletmetakip

import android.app.Activity
import android.os.Bundle
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
import kotlin.concurrent.thread

class MainActivity : Activity() {

private lateinit var root: LinearLayout
private val sheetNames = ArrayList<String>()
private lateinit var jsonData: JSONObject

private val jsonUrl =
    "https://raw.githubusercontent.com/mehmetkm6005-cell/IsletmeTakipAPK/main/app/src/main/assets/isletme_takip_data.json"

private fun dp(value: Int): Int {
    return (value * resources.displayMetrics.density).toInt()
}

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Önce ekrana giriş ekranını getir
    showLoginScreen()

    // Sonra GitHub'daki güncel JSON'u indir
    loadDataFromGitHub()
}

private fun loadDataFromGitHub() {

    thread {

        try {

            val url = URL(jsonUrl)

            val connection =
                url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.setRequestProperty(
                "Cache-Control",
                "no-cache"
            )

            val responseCode = connection.responseCode

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception(
                    "GitHub bağlantısı başarısız. Kod: $responseCode"
                )
            }

            val text =
                connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

            connection.disconnect()

            val newJson =
                JSONObject(text)

            val sheets =
                newJson.getJSONObject("sheets")

            val newSheetNames =
                ArrayList<String>()

            val keys = sheets.keys()

            while (keys.hasNext()) {

                val name = keys.next()

                if (name == "ANA SAYFA") {
                    continue
                }

                if (name.endsWith(
                        "-data",
                        ignoreCase = true
                    )
                ) {
                    continue
                }

                newSheetNames.add(name)
            }

            runOnUiThread {

                jsonData = newJson

                sheetNames.clear()
                sheetNames.addAll(newSheetNames)

                Toast.makeText(
                    this,
                    "Güncel veriler GitHub'dan alındı.",
                    Toast.LENGTH_SHORT
                ).show()

            }

        } catch (e: Exception) {

            runOnUiThread {

                Toast.makeText(
                    this,
                    "Güncel veri alınamadı.\n" +
                            "Hata: " +
                            e.message,
                    Toast.LENGTH_LONG
                ).show()

                // İnternet bağlantısı olmazsa APK içine
                // gömülü eski JSON'u kullanmayı dene
                loadLocalData()
            }
        }
    }
}

private fun loadLocalData() {

    try {

        val text =
            assets.open(
                "isletme_takip_data.json"
            )
                .bufferedReader()
                .use { it.readText() }

        jsonData =
            JSONObject(text)

        val sheets =
            jsonData.getJSONObject("sheets")

        sheetNames.clear()

        val keys =
            sheets.keys()

        while (keys.hasNext()) {

            val name =
                keys.next()

            if (name == "ANA SAYFA") {
                continue
            }

            if (name.endsWith(
                    "-data",
                    ignoreCase = true
                )
            ) {
                continue
            }

            sheetNames.add(name)
        }

    } catch (e: Exception) {

        Toast.makeText(
            this,
            "JSON okunamadı: " +
                    e.message,
            Toast.LENGTH_LONG
        ).show()
    }
}

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
        showMainMenu()
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

private fun showLoginScreen() {

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

    login.textSize = 16f

    login.setOnClickListener {
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

private fun showMainMenu() {

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

    for (name in sheetNames) {

        val button =
            Button(this)

        button.text =
            name

        button.textSize = 15f

        button.setAllCaps(false)

        button.setOnClickListener {
            showSheet(name)
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
}

private fun showSheet(
    sheetName: String
) {

    createRoot()

    addLogoBar()

    val titleBar =
        LinearLayout(this)

    titleBar.orientation =
        LinearLayout.VERTICAL

    titleBar.setGravity(
        Gravity.CENTER
    )

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

    title.textSize = 20f

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

        val rows =
            sheets.getJSONArray(
                sheetName
            )

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

        for (
            i in 0 until rows.length()
        ) {

            val row =
                rows.optJSONArray(i)

            if (row == null) {
                continue
            }

            val tableRow =
                TableRow(this)

            tableRow.setPadding(
                0,
                dp(1),
                0,
                dp(1)
            )

            for (
                j in 0 until row.length()
            ) {

                val value =
                    row.opt(j)

                val textValue =
                    if (
                        value == null ||
                        value == JSONObject.NULL
                    ) {
                        ""
                    } else {
                        value.toString()
                    }

                val cell =
                    TextView(this)

                cell.text =
                    textValue

                cell.textSize =
                    if (i == 0) {
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

                if (i == 0) {

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

                    if (i % 2 == 0) {

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
            "Veriler gösterilemedi.\n\n" +
                    e.message

        error.textSize = 16f

        error.gravity =
            Gravity.CENTER

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

    back.textSize = 15f

    back.setOnClickListener {
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

}
