package com.muparse.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.muparse.BuildConfig
import com.muparse.R
import java.text.SimpleDateFormat
import java.util.zip.ZipFile

class About : AppCompatActivity() {

    private var last: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val vercode = BuildConfig.VERSION_NAME
        val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        cB()

        val txtVer = findViewById<View>(R.id.txtver) as TextView
        txtVer.text = "                                  Version: " + vercode + "\n" +
                "LoggedIn As: " + prefs.getString("id", null) + "\n" +
                "DevelopedBy: SajjadJaved ☺" + "\n" +
                "LastBuild: " + last
    }

    private fun cB() {
        try {
            val ai = packageManager.getApplicationInfo(packageName, 0)
            val zf = ZipFile(ai.sourceDir)
            val ze = zf.getEntry("classes.dex")
            val time = ze.time
            last = SimpleDateFormat.getInstance().format(java.util.Date(time))
            zf.close()
        } catch (ignored: Exception) {
        }
    }
}
