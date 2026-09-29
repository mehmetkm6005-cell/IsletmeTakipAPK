package com.mehmet.isletmetakip

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : Activity() {

    private lateinit var root: LinearLayout

    // İŞLETME TAKİP-MK26.xlsx dosyasından gelen görünür sayfalar
    private val menuItems = listOf(
        "İHBARLAR",
        "HASARLAR",
        "ARAÇLAR",
        "PERSONELLER",
        "ABONE SAYISI",
        "ŞEBEKE BİLGİSİ",
        "RMS-A GENEL BİLGİLER",
        "RMS-A SAYAÇ",
        "RMS-A GAZ ÇEKİŞLERİ",
        "BÖLGE REGÜLATÖRLERİ",
        "MÜŞTERİ İSTASYONLARI",
        "STOK GAZ MİKTARLARI",
        "ENDÜSTRİYEL SAYAÇLAR",
        "LNG-CNG",
        "AMR ŞİFRELERİ",
        "CİHAZLAR",
        "ACİL-RMS-OFİS TELEFONLARI",
        "HAT İPTALLERİ",
        "ARIZA İŞ EMİRLERİ",
        "İZİN TAKİP",
        "FAZLA MESAİ TAKİP",
        "PATLAMA-YANGIN-ZEHİRLENME",
        "Mİ-RMS-B ÖZET",
        "Mİ-RMS-RS-C ÖZET"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLoginScreen()
    }

    private fun createRoot() {
        root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(Color.WHITE)
        root.setPadding(24, 20, 24, 20)
        setContentView(root)
    }

    // ---------------------------------------------------------
    // LOGO
    // ---------------------------------------------------------

    private fun addLogoBar(showBackToMenu: Boolean = false) {

        val bar = LinearLayout(this)
        bar.orientation = LinearLayout.HORIZONTAL
        bar.gravity = Gravity.CENTER_VERTICAL
        bar.setPadding(0, 0, 0, 18)

        val logo = TextView(this)
        logo.text = "İT"
        logo.textSize = 22f
        logo.setTypeface(null, Typeface.BOLD)
        logo.gravity = Gravity.CENTER
        logo.setTextColor(Color.WHITE)
        logo.setBackgroundColor(Color.rgb(0, 83, 155))

        val logoParams = LinearLayout.LayoutParams(58, 58)
        bar.addView(logo, logoParams)

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
    }

    // ---------------------------------------------------------
    // KULLANICI GİRİŞİ
    // ---------------------------------------------------------

    private fun showLoginScreen() {

        createRoot()

        addLogoBar(false)

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
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val username = EditText(this)
        username.hint = "Kullanıcı Adı"
        username.setSingleLine(true)

        root.addView(
            username,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                60
            ).apply {
                topMargin = 30
            }
        )

        val password = EditText(this)
        password.hint = "Şifre"
        password.setSingleLine(true)
        password.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or
            android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        root.addView(
            password,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                60
            ).apply {
                topMargin = 12
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
                60
            ).apply {
                topMargin = 25
            }
        )
    }

    // ---------------------------------------------------------
    // ANA MENÜ
    // ---------------------------------------------------------

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
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val scroll = ScrollView(this)

        val menuLayout = LinearLayout(this)
        menuLayout.orientation = LinearLayout.VERTICAL
        menuLayout.setPadding(0, 20, 0, 20)

        for (menuName in menuItems) {

            val button = Button(this)
            button.text = menuName
            button.textSize = 15f
            button.setAllCaps(false)

            button.setOnClickListener {
                showSheet(menuName)
            }

            menuLayout.addView(
                button,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    58
                ).apply {
                    bottomMargin = 8
                }
            )
        }

        scroll.addView(menuLayout)

        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )
    }

    // ---------------------------------------------------------
    // SAYFA
    // ---------------------------------------------------------

    private fun showSheet(sheetName: String) {

        createRoot()

        addLogoBar(true)

        val title = TextView(this)
        title.text = sheetName
        title.textSize = 22f
        title.setTypeface(null, Typeface.BOLD)
        title.gravity = Gravity.CENTER
        title.setTextColor(Color.rgb(0, 83, 155))

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val info = TextView(this)
        info.text =
            "Bu ekran İŞLETME TAKİP-MK26.xlsx içerisindeki\n" +
            "\"$sheetName\" sayfasına karşılık gelir.\n\n" +
            "Veriler isletme_takip_data.json dosyasından okunacaktır."

        info.textSize = 16f
        info.gravity = Gravity.CENTER
        info.setPadding(20, 40, 20, 20)

        root.addView(
            info,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val backButton = Button(this)
        backButton.text = "ANA MENÜYE DÖN"
        backButton.setOnClickListener {
            showMainMenu()
        }

        root.addView(
            backButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                60
            ).apply {
                topMargin = 30
            }
        )
    }
}
