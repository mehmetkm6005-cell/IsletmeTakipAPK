package com.mehmet.isletmetakip

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*

class MainActivity : Activity() {
    private val blue=Color.rgb(21,101,192)
    private lateinit var content:LinearLayout
    private val screens=listOf(
        "İHBARLAR","HASARLAR","ARAÇLAR","PERSONELLER","ABONE SAYISI","ŞEBEKE BİLGİSİ",
        "RMS-A GENEL BİLGİLER","RMS-A SAYAÇ","RMS-A GAZ ÇEKİŞLERİ","BÖLGE REGÜLATÖRLERİ",
        "MÜŞTERİ İSTASYONLARI","RMS-A PEAK ÇEKİŞLERİ","BR PEAK ÇEKİŞLERİ","STOK GAZ MİKTARLARI",
        "ACİL EKİP İHBAR CİHAZLARI","ENDÜSTRİYEL SAYAÇLAR","LNG-CNG","AMR ŞİFRELERİ","CİHAZLAR",
        "ACİL-RMS-OFİS TELEFONLARI","ACİL EKİP İHBAR CİHAZI HASARLAR","HAT SONU BASINÇ ÖLÇÜM ORTALAMAS",
        "HAT SONU KOKU ÖLÇÜM ORTALAMASI","İŞLETME SAPMA SÜRELERİ","KAÇAK TARAMA FAALİYETLERİ",
        "DEPLASE İŞ EMİRLERİ","REGÜLATÖR İŞ EMİRLERİ","HAT İPTALLERİ","YAKICI CİHAZ DEĞİŞİMLERİ",
        "ARIZA İŞ EMİRLERİ","İZİN TAKİP","FAZLA MESAİ TAKİP","PATLAMA-YANGIN-ZEHİRLENME",
        "Mİ-RMS-B ÖZET","Mİ-RMS-RS-C ÖZET")

    override fun onCreate(state:Bundle?){super.onCreate(state);showLogin()}

    private fun page(title:String){
        val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setBackgroundColor(Color.WHITE)}
        val bar=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;gravity=Gravity.CENTER_VERTICAL;setBackgroundColor(blue)}
        val logo=ImageButton(this).apply{setImageResource(R.drawable.logo);setBackgroundColor(Color.TRANSPARENT);contentDescription="Ana Menü";setOnClickListener{showMainMenu()}}
        bar.addView(logo,LinearLayout.LayoutParams(58,58))
        bar.addView(TextView(this).apply{text=title;textSize=19f;setTextColor(Color.WHITE);setPadding(10,0,0,0)},LinearLayout.LayoutParams(0,58,1f))
        root.addView(bar)
        content=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(18,18,18,18)}
        root.addView(ScrollView(this).apply{addView(content)},LinearLayout.LayoutParams(-1,0,1f))
        setContentView(root)
    }

    private fun showLogin(){
        val outer=FrameLayout(this)
        val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;gravity=Gravity.CENTER_HORIZONTAL;setPadding(28,60,28,28)}
        box.addView(ImageView(this).apply{setImageResource(R.drawable.logo)},LinearLayout.LayoutParams(110,110))
        box.addView(TextView(this).apply{text="İŞLETME TAKİP";textSize=28f;gravity=Gravity.CENTER;setTextColor(blue);setPadding(0,12,0,28)})
        box.addView(EditText(this).apply{hint="Kullanıcı adı";singleLine=true},LinearLayout.LayoutParams(-1,60))
        box.addView(EditText(this).apply{hint="Şifre";singleLine=true;inputType=0x81},LinearLayout.LayoutParams(-1,60))
        box.addView(Button(this).apply{text="Giriş Yap";setOnClickListener{showMainMenu()}},LinearLayout.LayoutParams(-1,60))
        outer.addView(box)
        outer.addView(ImageButton(this).apply{setImageResource(R.drawable.logo);setBackgroundColor(Color.TRANSPARENT);contentDescription="Logo";setOnClickListener{showMainMenu()}},FrameLayout.LayoutParams(58,58,Gravity.TOP or Gravity.START))
        setContentView(outer)
    }

    private fun showMainMenu(){
        page("Ana Menü")
        content.addView(TextView(this).apply{text="İŞLETME TAKİP";textSize=26f;setTextColor(blue);setPadding(0,0,0,18)})
        content.addView(TextView(this).apply{text="Excel ekran yapısı APK'ya bağlandı. Gizli data sayfaları menüde gösterilmez.";textSize=15f;setTextColor(Color.DKGRAY);setPadding(0,0,0,16)})
        screens.forEach{ name -> content.addView(Button(this).apply{text=name;setOnClickListener{showScreen(name)}},LinearLayout.LayoutParams(-1,62).apply{setMargins(0,0,0,8)}) }
    }

    private fun showScreen(name:String){
        page(name)
        content.addView(TextView(this).apply{text=name;textSize=23f;setTextColor(blue);setPadding(0,0,0,12)})
        content.addView(TextView(this).apply{text="Bu ekranın gerçek Excel verileri bir sonraki bağlantı aşamasında gösterilecek.\n\nSol üstteki logoya dokunarak Ana Menü'ye dönebilirsiniz.";textSize=17f;setTextColor(Color.DKGRAY)})
    }
}
