package com.muparse;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class About extends AppCompatActivity {

    String last;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        String vercode = BuildConfig.VERSION_NAME;
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        cB();

        TextView txtVer = (TextView) findViewById(R.id.txtver);
        txtVer.setText("                                  Version: " + vercode + "\n" +
                "LoggedIn As: " + prefs.getString("id", null) + "\n" +
                "DevelopedBy: Sj&SUSLink☺" + "\n" +
                "LastBuild: " + last
        );
    }

    void cB() {
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), 0);
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            last = SimpleDateFormat.getInstance().format(new java.util.Date(time));
            zf.close();
        } catch (Exception ignored) {
        }
    }
}
