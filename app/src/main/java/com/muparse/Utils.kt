package com.muparse

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.muparse.activities.Login
import java.io.File
import java.io.InputStream

/**
 * Created by sajja on 3/4/2018.
 */
class Utils {

    val DEFA = Environment.getExternalStorageDirectory()
    val dir = File(Login.DEFA.path + "/NetupTV")
    val filepath = File(dir.path + "/data.m3u")


    fun isPackageInstalled(packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo("com.mxtech.videoplayer.ad", PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun activateWIFI(context: Context, title: String?, msg: String?) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(msg)
            setPositiveButton("Enable") { _, paramAnonymousInt -> context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }
            setNegativeButton("Continue") { _, paramAnonymousInt -> }
            setCancelable(false)
            create().show()
        }
    }

    fun Snack(ctx: Context, text: String?, view: View?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view != null) {
            val builder = SpannableStringBuilder()
            builder.append(" ").append("")
            builder.setSpan(ImageSpan(ctx, R.drawable.ic_info_black_24dp), builder.length - 1, builder.length, 0)
            builder.append(" ").append(text)
            Snackbar.make(view, builder, Snackbar.LENGTH_LONG).show()
        } else {
            Toast.makeText(ctx, text, Toast.LENGTH_LONG).show()
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetwork: NetworkInfo? = null
        activeNetwork = manager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    companion object {
        @JvmStatic
        @SuppressLint("StaticFieldLeak")
        var instance: Utils? = null
            get() {
                if (field == null) {
                    field = Utils()
                }
                return field
            }
            private set

        lateinit var tempChannels: InputStream

        fun Context.isNetworkAvailable(): Boolean {
            val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var activeNetwork: NetworkInfo? = null
            activeNetwork = manager.activeNetworkInfo
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }

    }
}