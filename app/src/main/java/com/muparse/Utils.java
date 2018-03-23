package com.muparse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Toast;

/**
 * Created by sajja on 3/4/2018.
 */

public class Utils {

    @SuppressLint("StaticFieldLeak")
    private static Utils instance = null;
    private Context ctx;
    private SharedPreferences.Editor editor;

    public Utils() {

    }

    public static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    boolean isPackageInstalled(PackageManager packageManager) {
        try {
            packageManager.getPackageInfo("com.mxtech.videoplayer.ad", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (manager != null) {
            activeNetwork = manager.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void activateWIFI(final Context context, String title, String msg) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
        localBuilder.setTitle(title);
        localBuilder.setMessage(msg);
        localBuilder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        localBuilder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
            }
        });
        localBuilder.setCancelable(false);
        localBuilder.create().show();
    }

    void Snack(Context ctx, String text, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(" ").append("");
            builder.setSpan(new ImageSpan(ctx, R.drawable.ic_info_black_24dp), builder.length() - 1, builder.length(), 0);
            builder.append(" ").append(text);
            Snackbar.make(view, builder, Snackbar.LENGTH_LONG).show();
        } else {
            Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
        }
    }
}
