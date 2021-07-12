package com.muparse.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.muparse.PreferencesManager
import com.muparse.R
import com.muparse.R.id
import com.muparse.Utils
import com.muparse.Utils.Companion.isNetworkAvailable
import com.muparse.databinding.ActivityLoginBinding
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class Login : AppCompatActivity() {
    private val dir = File(DEFA.path + "/NetupTV")
    val filepath = File(dir.path + "/data.m3u")

    @JvmField
    val urlLink = "Add your own link"
    val domain = "Add your Iptv server" //http://portal.example.com:8001";
    var firebaseAnalytics: FirebaseAnalytics? = null
    lateinit var editor: SharedPreferences.Editor
    private var spinner: ProgressBar? = null
    lateinit var loginBinding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        window.setBackgroundDrawable(null)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )
        spinner = findViewById(id.login_progress)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        loginBinding.password.setOnEditorActionListener { textView: TextView?, id: Int, keyEvent: KeyEvent? ->
            checkNet()
            true
        }
        loginBinding.emailSignInButton.setOnClickListener { view: View? -> checkNet() }
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "LoginActivity")
        firebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    private fun checkNet() {
        if (this@Login.isNetworkAvailable()) {
            attemptLogin()
        } else {
            attemptLogin()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!dir.exists()) {
                        dir.mkdir()
                    }
                } else {
                    Utils.instance!!.Snack(
                        this,
                        "Permission denied",
                        findViewById(id.activity_login)
                    )
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1
                    )
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    private fun attemptLogin() { // Reset errors.
        loginBinding.email.error = null
        loginBinding.password.error = null
        // Store values at the time of the login attempt.
        var cancel = false
        var focusView: View? = null
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(loginBinding.password.text.toString()) && !isPasswordValid(
                loginBinding.password.text.toString()
            )
        ) {
            loginBinding.password.error = getString(R.string.error_invalid_password)
            focusView = loginBinding.password
            cancel = true
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(loginBinding.email.text.toString())) {
            loginBinding.email.error = getString(R.string.error_field_required)
            focusView = loginBinding.email
            cancel = true
        }
        /*else if (!isEmailValid(email)) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }*/if (cancel) { // There was an error; don't attempt login and focus the first form field with an error.
            focusView!!.requestFocus()
        } else if (loginBinding.email.text.toString() == "admin" && loginBinding.password.text.toString() == "admin") {
            DwnloadFileFromUrl().execute("https://iptv-org.github.io/iptv/languages/urd.m3u")
        } else {
            CheckNetworkAvailable().execute("$domain/get.php?username=" + loginBinding.email.text + "&password=" + loginBinding.password.text + "&type=m3u&output=ts")
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    override fun onResume() {
        super.onResume()
        val isAccess = PreferencesManager.getBoolean(this, "isLogged", false)
        isNetworkAvailable()
        if (isAccess) {
            val intent = Intent(applicationContext, MainActivity::class.java)
//            startActivity(intent)
//            finish()
        }
        //if (!isAva){activateWIFI();}
    }

    @SuppressLint("StaticFieldLeak")
    private inner class CheckNetworkAvailable : AsyncTask<String?, Void?, Boolean>() {
        override fun onPreExecute() {
            spinner!!.visibility = View.VISIBLE
        }

        override fun onPostExecute(result: Boolean) {
            if (result) {

                DwnloadFileFromUrl().execute(domain + "/get.php?username=" + loginBinding.email.text + "&password=" + loginBinding.password.text + "&type=m3u&output=ts")
                Utils.instance!!.Snack(
                    this@Login,
                    "Loading channels...",
                    findViewById(id.activity_login)
                )
            } else {
                spinner!!.visibility = View.GONE
                Utils.instance!!.Snack(
                    applicationContext,
                    "Account not found.",
                    findViewById(id.activity_login)
                )
            }
        }

        override fun doInBackground(vararg params: String?): Boolean {
            return try {
                val myUrl = URL(params[0]) //Arrays.toString(params)
                val con = myUrl.openConnection() as HttpURLConnection
                con.apply {
                    instanceFollowRedirects = true
                    connectTimeout = 2000
                    readTimeout = 2000
                    requestMethod = "POST"
                    doOutput = true
                    doInput = true
                    connect()
                }
                val check = con.responseCode == HttpURLConnection.HTTP_OK
                Log.e("Google", check.toString())
                check
            } catch (e: Exception) {
                Log.e("Google", e.toString())
                false
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class DwnloadFileFromUrl : AsyncTask<String?, String?, String?>() {

        override fun onProgressUpdate(vararg values: String?) {}
        override fun onPostExecute(file_url: String?) {
            editor = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit()
            editor.apply {
                putString("name", "admin")
                putString("id", loginBinding.email.text.toString())
                putBoolean("isLogged", true)
                apply()
            }
            val intent = Intent(applicationContext, MainActivity::class.java)
            spinner!!.visibility = View.GONE
            startActivity(intent)
            finish()
        }

        override fun doInBackground(vararg params: String?): String? {
            try {
                val yahoo = URL(params[0])
                val reader = BufferedReader(
                    InputStreamReader(yahoo.openStream())
                )
                Utils.tempChannels = yahoo.openStream()
                var inputLine: String
//                val myOutWriter: OutputStreamWriter = FileWriter(dir.path + "/" + "data.m3u")
//                while (reader.readLine().also { inputLine = it } != null) {
//                    myOutWriter.write(inputLine + "\n")
//                }
//                    Log.e("TAG", "doInBackground: ${reader.readLines()}", )
//                myOutWriter.flush()
//                myOutWriter.close()
                reader.close()
                Log.e("Google", "File done")
            } catch (e: Exception) {
                Log.d("Google", "DownloadFileFromUrl " + e.message)
            }
            return null
        }
    }

    companion object {
        val DEFA = Environment.getExternalStorageDirectory()

        @JvmStatic
        var instance: Login? = null
            get() {
                if (field == null) {
                    field = Login()
                }
                return field
            }
            private set
    }
}