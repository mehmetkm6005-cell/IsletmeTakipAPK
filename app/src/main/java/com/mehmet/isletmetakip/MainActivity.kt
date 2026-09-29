package com.mehmet.isletmetakip

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {

    private lateinit var root: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showLoginScreen()
    }

    private fun createRoot(): LinearLayout {
        root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(Color.WHITE)

        return root
    }

    private fun showLoginScreen() {
        root = createRoot()

        val header = TextView(this)
        header.text = "İŞLETME TAKİP"
        header.textSize = 24f
        header.setTypeface(null, Typeface.BOLD)
        header.gravity = Gravity.CENTER
        header.setPadding(20, 40, 20, 40)

        root.addView(
            header,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val title = TextView(this)
        title.text = "Kullanıcı Girişi"
        title.textSize = 22f
        title.setTypeface(null, Typeface.BOLD)
        title.gravity = Gravity.CENTER
        title.setPadding(20, 30, 20, 30)

        root.addView(title)

        val username = EditText(this)
        username.hint = "Kullanıcı Adı"
        username.setSingleLine(true)
        username.setPadding(30, 20, 30, 20)

        root.addView(
            username,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(40, 20, 40, 10)
            }
        )

        val password = EditText(this)
        password.hint = "Şifre"
        password.setSingleLine(true)
        password.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        password.setPadding(30, 20, 30, 20)

        root.addView(
            password,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(40, 10, 40, 20)
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
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(40, 20, 40, 20)
            }
        )

        setContentView(root)
    }

    private fun showMainMenu() {
        root = createRoot()

        val header = LinearLayout(this)
        header.orientation = LinearLayout.HORIZONTAL
        header.gravity = Gravity.CENTER_VERTICAL
        header.setPadding(20, 25, 20, 25)

        val logo = TextView(this)
        logo.text = "İŞLETME\nTAKİP"
        logo.textSize = 18f
        logo.setTypeface(null, Typeface.BOLD)
        logo.gravity = Gravity.CENTER

        logo.setOnClickListener {
            showMainMenu()
        }

        header.addView(
            logo,
            LinearLayout.LayoutParams(
                120,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val title = TextView(this)
        title.text = "ANA MENÜ"
        title.textSize = 22f
        title.setTypeface(null, Typeface.BOLD)
        title.gravity = Gravity.CENTER

        header.addView(
            title,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        root.addView(header)

        val scrollView = ScrollView(this)

        val menu = LinearLayout(this)
        menu.orientation = LinearLayout.VERTICAL
        menu.setPadding(25, 15, 25, 30)

        val menuItems = arrayOf(
            "Abone Bilgileri",
            "İş Emirleri",
            "Bildirimler",
            "Arıza İşlemleri",
            "Hat İptal İşlemleri",
            "Teknik Tamamlama",
            "Kutu Bekleyen Aboneler",
            "Döküman Yükleme",
            "Sayaç İşlemleri",
            "Malzeme Bilgileri",
            "Saha İşlemleri",
            "Raporlar"
        )

        for (item in menuItems) {
            val button = Button(this)
            button.text = item
            button.textSize = 16f

            button.setOnClickListener {
                showPage(item)
            }

            menu.addView(
                button,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
            )
        }

        scrollView.addView(menu)
        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)
    }

    private fun showPage(pageName: String) {
        root = createRoot()

        val header = LinearLayout(this)
        header.orientation = LinearLayout.HORIZONTAL
        header.gravity = Gravity.CENTER_VERTICAL
        header.setPadding(15, 20, 15, 20)

        val logo = TextView(this)
        logo.text = "İŞLETME\nTAKİP"
        logo.textSize = 16f
        logo.setTypeface(null, Typeface.BOLD)
        logo.gravity = Gravity.CENTER

        logo.setOnClickListener {
            showMainMenu()
        }

        header.addView(
            logo,
            LinearLayout.LayoutParams(
                110,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        val title = TextView(this)
        title.text = pageName
        title.textSize = 20f
        title.setTypeface(null, Typeface.BOLD)
        title.gravity = Gravity.CENTER

        header.addView(
            title,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        root.addView(header)

        val content = TextView(this)
        content.text = "\n$pageName\n\nBu ekranın Excel verileri burada gösterilecek."
        content.textSize = 18f
        content.gravity = Gravity.CENTER
        content.setPadding(20, 50, 20, 50)

        root.addView(
            content,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        val backButton = Button(this)
        backButton.text = "ANA MENÜ"
        backButton.setOnClickListener {
            showMainMenu()
        }

        root.addView(
            backButton,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(30, 10, 30, 20)
            }
        )

        setContentView(root)
    }
}
