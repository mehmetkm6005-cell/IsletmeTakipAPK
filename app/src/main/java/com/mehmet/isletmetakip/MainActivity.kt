package com.mehmet.isletmetakip

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.view.Gravity
import android.widget.*
import org.json.JSONObject

class MainActivity : Activity() {

    private lateinit var root: LinearLayout
    private val sheetNames = ArrayList<String>()

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadSheets()

        // İlk açılışta giriş ekranı
        showLoginScreen()
    }

    // =========================================================
    // JSON'DAN GERÇEK EXCEL SEKME İSİMLERİNİ OKU
    // =========================================================

    private fun loadSheets() {
        try {
            val jsonText = assets.open("isletme_takip_data.json")
                .bufferedReader()
                .use { it.readText() }

            val json = JSONObject(jsonText)
            val sheets = json.getJSONObject("sheets")

            val iterator = sheets.keys()

            while (iterator.hasNext()) {
                val name = iterator.next()

                // ANA SAYFA menü olarak gösterilmeyecek
                if (name == "ANA SAYFA") continue

                // Gizli data sayfaları gösterilmeyecek
                if (name.endsWith("-data", ignoreCase = true)) continue

                sheetNames.add(name)
            }

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Veri dosyası okunamadı",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // =========================================================
    // TEMEL EKRAN
    // =========================================================

    private fun createRoot() {

        root = LinearLayout(this)

        root.orientation = LinearLayout.VERTICAL

        root.setBackgroundColor(Color.WHITE)

        root.setPadding(
            dp(18),
            dp(12),
            dp(18),
            dp(12)
        )

        setContentView(root)
    }

    // =========================================================
    // LOGO
    // =========================================================

    private fun addLogoBar() {

        val bar = LinearLayout(this)

        bar.orientation = LinearLayout.HORIZONTAL

        bar.gravity = Gravity.CENTER_VERTICAL

        val logo = TextView(this)

        logo.text = "İT"
        logo.textSize = 20f
        logo.setTypeface(null, Typeface.BOLD)
        logo.gravity = Gravity.CENTER
        logo.setTextColor(Color.WHITE)
        logo.setBackgroundColor(Color.rgb(0, 83, 155))

        bar.addView(
            logo,
            LinearLayout.LayoutParams(
                dp(55),
                dp(55)
            )
        )

        logo.setOnClickListener {
            showMainMenu()
        }

        val title = TextView(this)

        title.text = "  İŞLETME TAKİP"
        title.textSize = 20f
        title.setTypeface(null, Typeface.BOLD)
        title.setTextColor(Color.rgb(0, 83, 155))

        bar.addView(
            title,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        root.addView(bar)

        val line = Space(this)

        root.addView(
            line,
            LinearLayout.LayoutParams(
                1,
                dp(15)
            )
        )
    }

    // =========================================================
    // KULLANICI GİRİŞİ
    // =========================================================

    private fun showLoginScreen() {

        createRoot()

        addLogoBar()

        val title = TextView(this)

        title.text = "Kullanıcı Girişi"
        title.textSize = 24f
        title.setTypeface(null, Typeface.BOLD)
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.rgb(0, 83, 155))

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            )
        )

        val username = EditText(this)

        username.hint = "Kullanıcı Adı"
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

        val password = EditText(this)

        password.hint = "Şifre"
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

        val loginButton = Button(this)

        loginButton.text = "GİRİŞ YAP"
        loginButton.textSize = 16f

        loginButton.setOnClickListener {
            showMainMenu()
        }

        root.addView(
            loginButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(55)
            ).apply {
                topMargin = dp(20)
            }
        )
    }

    // =========================================================
    // ANA MENÜ
    // =========================================================

    private fun showMainMenu() {

        createRoot()

        addLogoBar()

        val title = TextView(this)

        title.text = "ANA MENÜ"
        title.textSize = 24f
        title.setTypeface(null, Typeface.BOLD)
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.rgb(0, 83, 155))

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(50)
            )
        )

        val scroll = ScrollView(this)

        val menu = LinearLayout(this)

        menu.orientation = LinearLayout.VERTICAL

        menu.setPadding(
            0,
            dp(10),
            0,
            dp(20)
        )

        // JSON'DAN GELEN GERÇEK EXCEL SAYFALARI
        for (name in sheetNames) {

            val button = Button(this)

            button.text = name
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

    // =========================================================
    // SEKMENİN İÇERİĞİ
    // =========================================================

    private fun showSheet(sheetName: String) {

        createRoot()

        addLogoBar()

        val title = TextView(this)

        title.text = sheetName
        title.textSize = 21f
        title.setTypeface(null, Typeface.BOLD)
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.rgb(0, 83, 155))

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(50)
            )
        )

        val info = TextView(this)

        info.text =
            "Excel sekmesi:\n\n$sheetName\n\n" +
            "Bu bölümdeki veriler isletme_takip_data.json dosyasından alınmaktadır."

        info.textSize = 16f
        info.gravity = Gravity.CENTER
        info.setPadding(
            dp(20),
            dp(30),
            dp(20),
            dp(20)
        )

        root.addView(
            info,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val back = Button(this)

        back.text = "ANA MENÜYE DÖN"

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
