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
private lateinit var content: LinearLayout
private var excelData: JSONObject = JSONObject()

private val screens = listOf(
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
    "RMS-A PEAK ÇEKİŞLERİ",
    "BR PEAK ÇEKİŞLERİ",
    "STOK GAZ MİKTARLARI",
    "ACİL EKİP İHBAR CİHAZLARI",
    "ENDÜSTRİYEL SAYAÇLAR",
    "LNG-CNG",
    "AMR ŞİFRELERİ",
    "CİHAZLAR",
    "ACİL-RMS-OFİS TELEFONLARI",
    "ACİL EKİP İHBAR CİHAZI HASARLAR",
    "HAT SONU BASINÇ ÖLÇÜM ORTALAMAS",
    "HAT SONU KOKU ÖLÇÜM ORTALAMASI",
    "İŞLETME SAPMA SÜRELERİ",
    "KAÇAK TARAMA FAALİYETLERİ",
    "DEPLASE İŞ EMİRLERİ",
    "REGÜLATÖR İŞ EMİRLERİ",
    "HAT İPTALLERİ",
    "YAKICI CİHAZ DEĞİŞİMLERİ",
    "ARIZA İŞ EMİRLERİ",
    "İZİN TAKİP",
    "FAZLA MESAİ TAKİP",
    "PATLAMA-YANGIN-ZEHİNLENME",
    "Mİ-RMS-B ÖZET",
    "Mİ-RMS-RS-C ÖZET"
)

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    loadExcelData()
    showLogin()
}

private fun loadExcelData() {
    try {
        val input = assets.open("isletme_takip_data.json")
        val reader = BufferedReader(InputStreamReader(input, Charsets.UTF_8))
        val text = reader.readText()
        reader.close()
        input.close()
        excelData = JSONObject(text)
    } catch (e: Exception) {
        excelData = JSONObject()
    }
}

private fun showLogin() {

    val root = FrameLayout(this)
    root.setBackgroundColor(Color.WHITE)

    val logoTop = ImageButton(this)
    logoTop.setImageResource(R.drawable.logo)
    logoTop.setBackgroundColor(Color.TRANSPARENT)
    logoTop.contentDescription = "Ana Menü"

    val logoParams = FrameLayout.LayoutParams(64, 64)
    logoParams.gravity = Gravity.TOP or Gravity.START
    root.addView(logoTop, logoParams)

    val box = LinearLayout(this)
    box.orientation = LinearLayout.VERTICAL
    box.gravity = Gravity.CENTER_HORIZONTAL
    box.setPadding(28, 75, 28, 28)

    val logo = ImageView(this)
    logo.setImageResource(R.drawable.logo)
    logo.scaleType = ImageView.ScaleType.CENTER_INSIDE

    box.addView(
        logo,
        LinearLayout.LayoutParams(125, 125)
    )

    val title = TextView(this)
    title.text = "İŞLETME TAKİP"
    title.textSize = 28f
    title.setTypeface(null, Typeface.BOLD)
    title.gravity = Gravity.CENTER
    title.setTextColor(blue)
    title.setPadding(0, 12, 0, 30)

    box.addView(
        title,
        LinearLayout.LayoutParams(-1, -2)
    )

    val username = EditText(this)
    username.hint = "Kullanıcı adı"
    username.textSize = 16f
    username.setSingleLine(true)

    val userParams = LinearLayout.LayoutParams(-1, 60)
    userParams.setMargins(0, 0, 0, 14)

    box.addView(username, userParams)

    val password = EditText(this)
    password.hint = "Şifre"
    password.textSize = 16f
    password.setSingleLine(true)
    password.inputType =
        InputType.TYPE_CLASS_TEXT or
        InputType.TYPE_TEXT_VARIATION_PASSWORD

    val passParams = LinearLayout.LayoutParams(-1, 60)
    passParams.setMargins(0, 0, 0, 20)

    box.addView(password, passParams)

    val loginButton = Button(this)
    loginButton.text = "GİRİŞ YAP"
    loginButton.textSize = 16f

    loginButton.setOnClickListener {
        showMainMenu()
    }

    box.addView(
        loginButton,
        LinearLayout.LayoutParams(-1, 60)
    )

    root.addView(
        box,
        FrameLayout.LayoutParams(-1, -1)
    )

    root.bringChildToFront(logoTop)

    setContentView(root)
}

private fun createHeader(title: String): LinearLayout {

    val header = LinearLayout(this)
    header.orientation = LinearLayout.HORIZONTAL
    header.gravity = Gravity.CENTER_VERTICAL
    header.setBackgroundColor(blue)

    val logo = ImageButton(this)
    logo.setImageResource(R.drawable.logo)
    logo.setBackgroundColor(Color.TRANSPARENT)
    logo.contentDescription = "Ana Menü"

    logo.setOnClickListener {
        showMainMenu()
    }

    header.addView(
        logo,
        LinearLayout.LayoutParams(60, 60)
    )

    val titleView = TextView(this)
    titleView.text = title
    titleView.textSize = 19f
    titleView.setTypeface(null, Typeface.BOLD)
    titleView.setTextColor(Color.WHITE)
    titleView.gravity = Gravity.CENTER_VERTICAL
    titleView.setPadding(10, 0, 10, 0)

    header.addView(
        titleView,
        LinearLayout.LayoutParams(0, 60, 1f)
    )

    return header
}

private fun createPage(title: String) {

    val root = LinearLayout(this)
    root.orientation = LinearLayout.VERTICAL
    root.setBackgroundColor(Color.WHITE)

    root.addView(
        createHeader(title),
        LinearLayout.LayoutParams(-1, 60)
    )

    content = LinearLayout(this)
    content.orientation = LinearLayout.VERTICAL
    content.setPadding(18, 18, 18, 24)

    val scroll = ScrollView(this)
    scroll.addView(content)

    root.addView(
        scroll,
        LinearLayout.LayoutParams(-1, 0, 1f)
    )

    setContentView(root)
}

private fun showMainMenu() {

    createPage("Ana Menü")

    val title = TextView(this)
    title.text = "İŞLETME TAKİP"
    title.textSize = 27f
    title.setTypeface(null, Typeface.BOLD)
    title.setTextColor(blue)
    title.setPadding(0, 0, 0, 8)

    content.addView(title)

    val info = TextView(this)
    info.text = "İşletme takip ekranları"
    info.textSize = 15f
    info.setTextColor(Color.DKGRAY)
    info.setPadding(0, 0, 0, 18)

    content.addView(info)

    for (name in screens) {

        val button = Button(this)
        button.text = name
        button.textSize = 15f

        button.setOnClickListener {
            showScreen(name)
        }

        val params = LinearLayout.LayoutParams(-1, 58)
        params.setMargins(0, 0, 0, 8)

        content.addView(button, params)
    }
}

private fun showScreen(name: String) {

    createPage(name)

    val title = TextView(this)
    title.text = name
    title.textSize = 23f
    title.setTypeface(null, Typeface.BOLD)
    title.setTextColor(blue)
    title.setPadding(0, 0, 0, 10)

    content.addView(title)

    val sheet = excelData.optJSONObject(name)

    if (sheet == null) {

        val message = TextView(this)
        message.text = "Bu ekran için Excel verisi bulunamadı."
        message.textSize = 16f
        message.setTextColor(Color.DKGRAY)

        content.addView(message)
        return
    }

    val rows = sheet.optJSONArray("rows")

    if (rows == null || rows.length() == 0) {

        val message = TextView(this)
        message.text = "Bu Excel sayfasında veri bulunamadı."
        message.textSize = 16f
        message.setTextColor(Color.DKGRAY)

        content.addView(message)
        return
    }

    val count = TextView(this)
    count.text = "Toplam kayıt: ${rows.length()}"
    count.textSize = 15f
    count.setTypeface(null, Typeface.BOLD)
    count.setTextColor(Color.DKGRAY)
    count.setPadding(0, 0, 0, 12)

    content.addView(count)

    val horizontal = HorizontalScrollView(this)

    val table = LinearLayout(this)
    table.orientation = LinearLayout.VERTICAL

    val maxRows = minOf(rows.length(), 30)

    for (r in 0 until maxRows) {

        val rowArray = rows.optJSONArray(r)

        if (rowArray == null) continue

        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL

        for (c in 0 until rowArray.length()) {

            val cell = TextView(this)
            cell.text = rowArray.optString(c, "")
            cell.textSize = 13f
            cell.setPadding(10, 8, 10, 8)
            cell.gravity = Gravity.CENTER_VERTICAL

            if (r == 0) {
                cell.setBackgroundColor(blue)
                cell.setTextColor(Color.WHITE)
                cell.setTypeface(null, Typeface.BOLD)
            } else {
                cell.setTextColor(Color.DKGRAY)

                if (r % 2 == 0) {
                    cell.setBackgroundColor(Color.WHITE)
                } else {
                    cell.setBackgroundColor(Color.rgb(245, 245, 245))
                }
            }

            row.addView(
                cell,
                LinearLayout.LayoutParams(180, 52)
            )
        }

        table.addView(
            row,
            LinearLayout.LayoutParams(-2, 52)
        )
    }

    horizontal.addView(table)

    content.addView(
        horizontal,
        LinearLayout.LayoutParams(-1, -2)
    )

    if (rows.length() > 30) {

        val note = TextView(this)
        note.text = "\nİlk 30 kayıt gösteriliyor."
        note.textSize = 14f
        note.setTextColor(Color.DKGRAY)

        content.addView(note)
    }
}

override fun onBackPressed() {
    showMainMenu()
}

}
