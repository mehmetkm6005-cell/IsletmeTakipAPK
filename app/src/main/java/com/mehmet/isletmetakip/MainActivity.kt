package com.mehmet.isletmetakip

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.widget.*
import org.json.JSONObject

class MainActivity : Activity() {

    private lateinit var root: LinearLayout

    // JSON içindeki Excel sayfaları
    private val sheetNames = mutableListOf<String>()

    // ---------------------------------------------------------
    // UYGULAMA BAŞLANGICI
    // ---------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadSheetNamesFromJson()
        showLoginScreen()
    }

    // ---------------------------------------------------------
    // JSON OKU
    // ---------------------------------------------------------

    private fun loadSheetNamesFromJson() {

        try {

            val inputStream = assets.open("isletme_takip_data.json")

            val jsonText = inputStream
                .bufferedReader()
                .use { it.readText() }

            val rootJson = JSONObject(jsonText)

            val sheetsObject = rootJson.getJSONObject("sheets")

            val keys = sheetsObject.keys()

            while (keys.hasNext()) {

                val sheetName = keys.next()

                // ANA SAYFA ana menü olduğu için ayrıca gösterilmiyor
                if (sheetName == "ANA SAYFA") {
                    continue
                }

                // Gizli data sayfalarını menüye alma
                if (sheetName.endsWith("-data", ignoreCase = true)) {
                    continue
                }

                sheetNames.add(sheetName)
            }

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "JSON okunamadı: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ---------------------------------------------------------
    // ANA ROOT
    // ---------------------------------------------------------

    private fun createRoot() {

        root = LinearLayout(this)

        root.orientation = LinearLayout.VERTICAL

        root.setBackgroundColor(Color.WHITE)

        root.setPadding(
            20,
            15,
            20,
            15
        )

        setContentView(root)
    }

    // ---------------------------------------------------------
    // LOGO / ÜST BAR
    // ---------------------------------------------------------

    private fun addLogoBar() {

        val bar = LinearLayout(this)

        bar.orientation = LinearLayout.HORIZONTAL

        bar.gravity = Gravity.CENTER_VERTICAL

        bar.setPadding(
            0,
            0,
            0,
            15
        )

        // LOGO
        val logo = TextView(this)

        logo.text = "İT"

        logo.textSize = 20f

        logo.setTypeface(
            null,
            Typeface.BOLD
        )

        logo.gravity = Gravity.CENTER

        logo.setTextColor(Color.WHITE)

        logo.setBackgroundColor(
            Color.rgb(0, 83, 155)
        )

        val logoParams =
            LinearLayout.LayoutParams(
                55,
                55
            )

        bar.addView(
            logo,
            logoParams
        )

        // Logoya basınca ANA MENÜ
        logo.setOnClickListener {
            showMainMenu()
        }

        // BAŞLIK
        val title = TextView(this)

        title.text = "  İŞLETME TAKİP"

        title.textSize = 20f

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.setTextColor(
            Color.rgb(0, 83, 155)
        )

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
    // GİRİŞ EKRANI
    // ---------------------------------------------------------

    private fun showLoginScreen() {

        createRoot()

        addLogoBar()

        val spacer = Space(this)

        root.addView(
            spacer,
            LinearLayout.LayoutParams(
                1,
                25
            )
        )

        val title = TextView(this)

        title.text = "Kullanıcı Girişi"

        title.textSize = 24f

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.gravity = Gravity.CENTER

        title.setTextColor(
            Color.rgb(0, 83, 155)
        )

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                55
            )
        )

        // Kullanıcı adı
        val username = EditText(this)

        username.hint = "Kullanıcı Adı"

        username.setSingleLine(true)

        root.addView(
            username,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                60
            ).apply {
                topMargin = 20
            }
        )

        // Şifre
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
                topMargin = 10
            }
        )

        // Giriş
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
                topMargin = 20
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

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.gravity = Gravity.CENTER

        title.setTextColor(
            Color.rgb(0, 83, 155)
        )

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                55
            )
        )

        val countText = TextView(this)

        countText.text =
            "${sheetNames.size} menü"

        countText.textSize = 14f

        countText.gravity = Gravity.CENTER

        countText.setTextColor(Color.DKGRAY)

        root.addView(
            countText,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                35
            )
        )

        // Kaydırılabilir menü
        val scrollView = ScrollView(this)

        val menuLayout = LinearLayout(this)

        menuLayout.orientation =
            LinearLayout.VERTICAL

        menuLayout.setPadding(
            0,
            10,
            0,
            20
        )

        // JSON'dan gelen GERÇEK sayfalar
        for (sheetName in sheetNames) {

            val button = Button(this)

            button.text = sheetName

            button.textSize = 15f

            button.setAllCaps(false)

            button.gravity =
                Gravity.CENTER_VERTICAL

            button.setOnClickListener {

                showSheet(sheetName)
            }

            menuLayout.addView(
                button,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    58
                ).apply {
                    bottomMargin = 7
                }
            )
        }

        scrollView.addView(menuLayout)

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )
    }

    // ---------------------------------------------------------
    // EXCEL SAYFASI
    // ---------------------------------------------------------

    private fun showSheet(sheetName: String) {

        createRoot()

        addLogoBar()

        val title = TextView(this)

        title.text = sheetName

        title.textSize = 21f

        title.setTypeface(
            null,
            Typeface.BOLD
        )

        title.gravity = Gravity.CENTER

        title.setTextColor(
            Color.rgb(0, 83, 155)
        )

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                55
            )
        )

        try {

            val inputStream =
                assets.open(
                    "isletme_takip_data.json"
                )

            val jsonText =
                inputStream
                    .bufferedReader()
                    .use { it.readText() }

            val rootJson =
                JSONObject(jsonText)

            val sheetsObject =
                rootJson.getJSONObject("sheets")

            val sheetData =
                sheetsObject.getJSONArray(sheetName)

            val horizontalScroll =
                HorizontalScrollView(this)

            val tableLayout =
                TableLayout(this)

            tableLayout.setPadding(
                5,
                10,
                5,
                20
            )

            // JSON'daki satırları oluştur
            for (i in 0 until sheetData.length()) {

                val rowArray =
                    sheetData.getJSONArray(i)

                val rowLayout =
                    LinearLayout(this)

                rowLayout.orientation =
                    LinearLayout.HORIZONTAL

                rowLayout.setPadding(
                    0,
                    2,
                    0,
                    2
                )

                for (j in 0 until rowArray.length()) {

                    val cell =
                        TextView(this)

                    val value =
                        if (rowArray.isNull(j)) {
                            ""
                        } else {
                            rowArray.get(j).toString()
                        }

                    cell.text = value

                    cell.textSize = 13f

                    cell.setPadding(
                        12,
                        10,
                        12,
                        10
                    )

                    cell.setTextColor(
                        Color.DKGRAY
                    )

                    cell.setBackgroundColor(
                        Color.WHITE
                    )

                    rowLayout.addView(
                        cell,
                        LinearLayout.LayoutParams(
                            180,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    )
                }

                tableLayout.addView(rowLayout)
            }

            horizontalScroll.addView(
                tableLayout
            )

            root.addView(
                horizontalScroll,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f
                )
            )

        } catch (e: Exception) {

            val errorText =
                TextView(this)

            errorText.text =
                "Bu sayfanın verileri okunamadı.\n\n${e.message}"

            errorText.textSize = 16f

            errorText.gravity =
                Gravity.CENTER

            errorText.setPadding(
                20,
                30,
                20,
                30
            )

            root.addView(
                errorText,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f
                )
            )
        }

        // Ana menü butonu
        val backButton =
            Button(this)

        backButton.text =
            "ANA MENÜYE DÖN"

        backButton.setOnClickListener {
            showMainMenu()
        }

        root.addView(
            backButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                60
            )
        )
    }
}
