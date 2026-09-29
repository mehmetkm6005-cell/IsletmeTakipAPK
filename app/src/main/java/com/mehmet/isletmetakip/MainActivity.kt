package com.mehmet.isletmetakip

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import org.json.JSONObject

class MainActivity : Activity() {
    private val blue = Color.rgb(21, 101, 192)
    private val lightBlue = Color.rgb(232, 240, 254)
    private lateinit var content: LinearLayout
    private var data: JSONObject? = null
    private var currentSheet = ""
    private var currentPage = 0
    private val pageSize = 30

    private val screens = listOf(
        "İHBARLAR","HASARLAR","ARAÇLAR","PERSONELLER","ABONE SAYISI","ŞEBEKE BİLGİSİ",
        "RMS-A GENEL BİLGİLER","RMS-A SAYAÇ","RMS-A GAZ ÇEKİŞLERİ","BÖLGE REGÜLATÖRLERİ",
        "MÜŞTERİ İSTASYONLARI","RMS-A PEAK ÇEKİŞLERİ","BR PEAK ÇEKİŞLERİ","STOK GAZ MİKTARLARI",
        "ACİL EKİP İHBAR CİHAZLARI","ENDÜSTRİYEL SAYAÇLAR","LNG-CNG","AMR ŞİFRELERİ","CİHAZLAR",
        "ACİL-RMS-OFİS TELEFONLARI","ACİL EKİP İHBAR CİHAZI HASARLAR","HAT SONU BASINÇ ÖLÇÜM ORTALAMAS",
        "HAT SONU KOKU ÖLÇÜM ORTALAMASI","İŞLETME SAPMA SÜRELERİ","KAÇAK TARAMA FAALİYETLERİ",
        "DEPLASE İŞ EMİRLERİ","REGÜLATÖR İŞ EMİRLERİ","HAT İPTALLERİ","YAKICI CİHAZ DEĞİŞİMLERİ",
        "ARIZA İŞ EMİRLERİ","İZİN TAKİP","FAZLA MESAİ TAKİP","PATLAMA-YANGIN-ZEHİRLENME",
        "Mİ-RMS-B ÖZET","Mİ-RMS-RS-C ÖZET"
    )

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        loadData()
        showLogin()
    }

    private fun loadData() {
        try {
            val json = assets.open("isletme_takip_data.json").bufferedReader(Charsets.UTF_8).use { it.readText() }
            data = JSONObject(json).getJSONObject("sheets")
        } catch (_: Exception) {
            data = null
        }
    }

    private fun page(title: String) {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
        }
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
        bar.addView(logo, LinearLayout.LayoutParams(58, 58))
        bar.addView(TextView(this).apply {
            text = title
            textSize = 19f
            setTextColor(Color.WHITE)
            setPadding(10, 0, 0, 0)
            gravity = Gravity.CENTER_VERTICAL
        }, LinearLayout.LayoutParams(0, 58, 1f))
        root.addView(bar)
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 12, 12, 12)
        }
        root.addView(ScrollView(this).apply { addView(content) }, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun showLogin() {
        val outer = FrameLayout(this).apply { setBackgroundColor(Color.WHITE) }
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(28, 60, 28, 28)
        }
        box.addView(ImageView(this).apply { setImageResource(R.drawable.logo) }, LinearLayout.LayoutParams(110, 110))
        box.addView(TextView(this).apply {
            text = "İŞLETME TAKİP"
            textSize = 28f
            gravity = Gravity.CENTER
            setTextColor(blue)
            setPadding(0, 12, 0, 28)
        })
        box.addView(EditText(this).apply {
            hint = "Kullanıcı adı"
            setSingleLine(true)
        }, LinearLayout.LayoutParams(-1, 60))
        box.addView(EditText(this).apply {
            hint = "Şifre"
            setSingleLine(true)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }, LinearLayout.LayoutParams(-1, 60))
        box.addView(Button(this).apply {
            text = "Giriş Yap"
            setOnClickListener { showMainMenu() }
        }, LinearLayout.LayoutParams(-1, 60))
        outer.addView(box)
        outer.addView(ImageButton(this).apply {
            setImageResource(R.drawable.logo)
            setBackgroundColor(Color.TRANSPARENT)
            contentDescription = "Logo"
            setOnClickListener { showMainMenu() }
        }, FrameLayout.LayoutParams(58, 58, Gravity.TOP or Gravity.START))
        setContentView(outer)
    }

    private fun showMainMenu() {
        page("Ana Menü")
        content.addView(TextView(this).apply {
            text = "İŞLETME TAKİP"
            textSize = 26f
            setTextColor(blue)
            setPadding(0, 0, 0, 10)
        })
        content.addView(TextView(this).apply {
            text = "Excel'deki 36 görünür ekran APK'ya aktarıldı. Gizli data sayfaları gösterilmez."
            textSize = 15f
            setTextColor(Color.DKGRAY)
            setPadding(0, 0, 0, 14)
        })
        screens.forEach { name ->
            content.addView(Button(this).apply {
                text = name
                setOnClickListener { showScreen(name) }
            }, LinearLayout.LayoutParams(-1, 58).apply { setMargins(0, 0, 0, 7) })
        }
    }

    private fun showScreen(name: String) {
        currentSheet = name
        currentPage = 0
        renderSheet()
    }

    private fun renderSheet() {
        page(currentSheet)
        val sheets = data
        if (sheets == null || !sheets.has(currentSheet)) {
            content.addView(TextView(this).apply {
                text = "Excel veri dosyası okunamadı."
                textSize = 17f
                setTextColor(Color.RED)
            })
            return
        }
        val rows = sheets.getJSONArray(currentSheet)
        val totalRows = rows.length()
        val totalPages = maxOf(1, (totalRows + pageSize - 1) / pageSize)
        if (currentPage >= totalPages) currentPage = totalPages - 1

        content.addView(TextView(this).apply {
            text = "$currentSheet  •  $totalRows satır"
            textSize = 20f
            setTextColor(blue)
            setPadding(0, 0, 0, 8)
        })

        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        nav.addView(Button(this).apply {
            text = "‹ Önceki"
            isEnabled = currentPage > 0
            setOnClickListener { currentPage--; renderSheet() }
        }, LinearLayout.LayoutParams(0, 52, 1f))
        nav.addView(TextView(this).apply {
            text = "  ${currentPage + 1} / $totalPages  "
            textSize = 15f
            gravity = Gravity.CENTER
        }, LinearLayout.LayoutParams(0, 52, 1f))
        nav.addView(Button(this).apply {
            text = "Sonraki ›"
            isEnabled = currentPage < totalPages - 1
            setOnClickListener { currentPage++; renderSheet() }
        }, LinearLayout.LayoutParams(0, 52, 1f))
        content.addView(nav)

        val start = currentPage * pageSize
        val end = minOf(totalRows, start + pageSize)
        val table = TableLayout(this)
        val horizontal = HorizontalScrollView(this).apply { addView(table) }
        content.addView(horizontal, LinearLayout.LayoutParams(-1, 0, 1f))

        for (r in start until end) {
            val arr = rows.getJSONArray(r)
            val tr = TableRow(this)
            tr.setBackgroundColor(if (r == 0) lightBlue else Color.WHITE)
            for (c in 0 until arr.length()) {
                val tv = TextView(this).apply {
                    text = arr.optString(c, "")
                    textSize = if (r == 0) 13f else 12f
                    setTextColor(Color.DKGRAY)
                    setPadding(10, 8, 10, 8)
                    gravity = Gravity.CENTER_VERTICAL
                    minWidth = 150
                    setBackgroundResource(android.R.drawable.editbox_background)
                }
                tr.addView(tv)
            }
            table.addView(tr)
        }
    }
}
