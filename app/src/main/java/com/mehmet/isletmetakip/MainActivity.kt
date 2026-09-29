package com.mehmet.isletmetakip

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : Activity() {
    private val blue = Color.rgb(21, 101, 192)
    private val dark = Color.rgb(35, 35, 35)
    private lateinit var content: LinearLayout
    private var excelData = JSONObject()

    private val screens = listOf(
        "İHBARLAR", "HASARLAR", "ARAÇLAR", "PERSONELLER", "ABONE SAYISI", "ŞEBEKE BİLGİSİ",
        "RMS-A GENEL BİLGİLER", "RMS-A SAYAÇ", "RMS-A GAZ ÇEKİŞLERİ", "BÖLGE REGÜLATÖRLERİ",
        "MÜŞTERİ İSTASYONLARI", "RMS-A PEAK ÇEKİŞLERİ", "BR PEAK ÇEKİŞLERİ", "STOK GAZ MİKTARLARI",
        "ACİL EKİP İHBAR CİHAZLARI", "ENDÜSTRİYEL SAYAÇLAR", "LNG-CNG", "AMR ŞİFRELERİ", "CİHAZLAR",
        "ACİL-RMS-OFİS TELEFONLARI", "ACİL EKİP İHBAR CİHAZI HASARLAR", "HAT SONU BASINÇ ÖLÇÜM ORTALAMAS",
        "HAT SONU KOKU ÖLÇÜM ORTALAMASI", "İŞLETME SAPMA SÜRELERİ", "KAÇAK TARAMA FAALİYETLERİ",
        "DEPLASE İŞ EMİRLERİ", "REGÜLATÖR İŞ EMİRLERİ", "HAT İPTALLERİ", "YAKICI CİHAZ DEĞİŞİMLERİ",
        "ARIZA İŞ EMİRLERİ", "İZİN TAKİP", "FAZLA MESAİ TAKİP", "PATLAMA-YANGIN-ZEHİNLENME",
        "Mİ-RMS-B ÖZET", "Mİ-RMS-RS-C ÖZET"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadExcelData()
        showLogin()
    }

    private fun loadExcelData() {
        try {
            assets.open("isletme_takip_data.json").use { input ->
                val text = BufferedReader(InputStreamReader(input, Charsets.UTF_8)).readText()
                val root = JSONObject(text)
                excelData = root.optJSONObject("sheets") ?: root
            }
        } catch (_: Exception) {
            excelData = JSONObject()
        }
    }

    private fun header(title: String): LinearLayout {
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(blue)
        }
        val logo = ImageButton(this).apply {
            setImageResource(R.drawable.logo)
            setBackgroundColor(Color.TRANSPARENT)
            contentDescription = "Ana Menü"
            setOnClickListener { showMainMenu() }
        }
        bar.addView(logo, LinearLayout.LayoutParams(60, 60))
        val tv = TextView(this).apply {
            text = title
            textSize = 19f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(10, 0, 10, 0)
        }
        bar.addView(tv, LinearLayout.LayoutParams(0, 60, 1f))
        return bar
    }

    private fun page(title: String) {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
        }
        root.addView(header(title), LinearLayout.LayoutParams(-1, 60))
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 24)
        }
        val scroll = ScrollView(this).apply { addView(content) }
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun showLogin() {
        val root = FrameLayout(this).apply { setBackgroundColor(Color.WHITE) }
        val top = ImageButton(this).apply {
            setImageResource(R.drawable.logo)
            setBackgroundColor(Color.TRANSPARENT)
            contentDescription = "Logo"
        }
        root.addView(top, FrameLayout.LayoutParams(60, 60, Gravity.TOP or Gravity.START))

        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28, 72, 28, 28)
        }
        val logo = ImageView(this).apply { setImageResource(R.drawable.logo) }
        box.addView(logo, LinearLayout.LayoutParams(120, 120))
        val title = TextView(this).apply {
            text = "İŞLETME TAKİP"
            textSize = 28f
            setTextColor(blue)
            setTypeface(null, Typeface.BOLD)
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 28)
        }
        box.addView(title, LinearLayout.LayoutParams(-1, -2))
        val user = EditText(this).apply {
            hint = "Kullanıcı adı"
            setSingleLine(true)
            textSize = 16f
        }
        box.addView(user, LinearLayout.LayoutParams(-1, 60).apply { setMargins(0, 0, 0, 12) })
        val pass = EditText(this).apply {
            hint = "Şifre"
            setSingleLine(true)
            textSize = 16f
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        box.addView(pass, LinearLayout.LayoutParams(-1, 60).apply { setMargins(0, 0, 0, 18) })
        val login = Button(this).apply {
            text = "GİRİŞ YAP"
            textSize = 16f
            setOnClickListener { showMainMenu() }
        }
        box.addView(login, LinearLayout.LayoutParams(-1, 60))
        root.addView(box, FrameLayout.LayoutParams(-1, -1))
        root.bringChildToFront(top)
        setContentView(root)
    }

    private fun showMainMenu() {
        page("Ana Menü")
        content.addView(TextView(this).apply {
            text = "İŞLETME TAKİP"
            textSize = 26f
            setTextColor(blue)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 6)
        })
        content.addView(TextView(this).apply {
            text = "Excel ekranları"
            textSize = 15f
            setTextColor(Color.DKGRAY)
            setPadding(0, 0, 0, 14)
        })
        screens.forEach { name ->
            val row = TextView(this).apply {
                text = name
                textSize = 16f
                setTextColor(dark)
                gravity = Gravity.CENTER_VERTICAL
                setTypeface(null, Typeface.BOLD)
                setPadding(18, 0, 12, 0)
                setBackgroundColor(Color.rgb(245, 247, 250))
                setOnClickListener { showScreen(name) }
            }
            content.addView(row, LinearLayout.LayoutParams(-1, 58).apply { setMargins(0, 0, 0, 8) })
        }
    }

    private fun showScreen(name: String) {
        page(name)
        content.addView(TextView(this).apply {
            text = name
            textSize = 22f
            setTextColor(blue)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 8)
        })
        val sheet = excelData.optJSONObject(name)
        val rows = sheet?.optJSONArray("rows")
        if (rows == null || rows.length() == 0) {
            content.addView(TextView(this).apply {
                text = "Veri bulunamadı."
                textSize = 16f
                setTextColor(Color.DKGRAY)
            })
            return
        }
        content.addView(TextView(this).apply {
            text = "Toplam kayıt: ${rows.length()}"
            textSize = 15f
            setTextColor(Color.DKGRAY)
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 10)
        })
        val tableScroll = HorizontalScrollView(this)
        val table = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val count = minOf(rows.length(), 30)
        for (r in 0 until count) {
            val arr = rows.optJSONArray(r) ?: continue
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            for (c in 0 until arr.length()) {
                val cell = TextView(this).apply {
                    text = arr.optString(c, "")
                    textSize = 13f
                    setPadding(10, 8, 10, 8)
                    gravity = Gravity.CENTER_VERTICAL
                    if (r == 0) {
                        setBackgroundColor(blue)
                        setTextColor(Color.WHITE)
                        setTypeface(null, Typeface.BOLD)
                    } else {
                        setTextColor(dark)
                        setBackgroundColor(if (r % 2 == 0) Color.WHITE else Color.rgb(245, 245, 245))
                    }
                }
                row.addView(cell, LinearLayout.LayoutParams(180, 52))
            }
            table.addView(row, LinearLayout.LayoutParams(-2, 52))
        }
        tableScroll.addView(table)
        content.addView(tableScroll, LinearLayout.LayoutParams(-1, -2))
        if (rows.length() > 30) {
            content.addView(TextView(this).apply {
                text = "İlk 30 kayıt gösteriliyor."
                textSize = 14f
                setTextColor(Color.DKGRAY)
                setPadding(0, 10, 0, 0)
            })
        }
    }

    override fun onBackPressed() {
        showMainMenu()
    }
}
