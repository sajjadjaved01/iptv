package com.muparse.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.muparse.*
import com.muparse.adapter.PlaylistAdapter
import com.muparse.models.M3UParser
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.*

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    val parser = M3UParser()
    var spinner: ProgressBar? = null
    var mPlaylistParams: TextView? = null
    private lateinit var mPlaylistList: RecyclerView
    var `is`: InputStream? = null
    var mAdapter: PlaylistAdapter? = null
    private lateinit var editor: SharedPreferences.Editor
    var contactList = ArrayList<HashMap<String, String>>()
    private val TAG = MainActivity::class.java.simpleName
    private lateinit var mBrowser: AlertDialog
    private val url = Login.instance!!.urlLink

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mPlaylistParams = findViewById(R.id.playlist_params)
        mPlaylistList = findViewById(R.id.playlist_recycler)
        spinner = findViewById(R.id.login_progress)
        val layoutManager = LinearLayoutManager(this)
        mPlaylistList.layoutManager = layoutManager
        mAdapter = PlaylistAdapter(this)
        mPlaylistList.adapter = mAdapter
        loader()
        //        new _loadFile().execute(filepath.getPath()); // this will read direct channels from url
//new GetJson().execute(); // this is getting info about User, channels etc.
    }

    private fun loader(name: String? = Utils.instance!!.filepath.path) {
        `is` = try { //new FileInputStream (new File(name)
            FileInputStream(File(name))
        } catch (e: Exception) {
            Toast.makeText(
                applicationContext,
                "Unable to fetch data. Showing demo data",
                Toast.LENGTH_LONG
            ).show()
            assets.open("data.db") // if u r trying to open file from asstes InputStream is = getassets.open(); InputStream
        }
        val playlist = parser.parseFile(`is`)
        mAdapter!!.update(playlist.playlistItems!!)
    }

    override fun onResume() {
        super.onResume()
        val isAccess = PreferencesManager.getBoolean(this, "isLogged", false)
        if (!isAccess) {
            startActivity(Intent(this@MainActivity, Login::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        val search = menu.findItem(R.id.app_bar_search)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Search channel name"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return filter(query)
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return filter(newText)
            }
        })
        searchView.setOnCloseListener {
            val goo = LoadFile().execute(Utils.instance!!.filepath.path)
            if (goo.get() == false) {
                loader()
            }
            false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_search -> setContentView(R.layout.searchable)
            R.id.logout -> {
                editor = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).edit()
                editor.clear()
                editor.apply()
                val intent = Intent(applicationContext, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            R.id.moreInfo -> GetJson().execute()
            R.id.browse -> Snackbar.make(mPlaylistList, "Feature Deprecated", Snackbar.LENGTH_LONG).show() //browser()
            R.id.about -> {
                val abt = Intent(this@MainActivity, About::class.java)
                startActivity(abt)
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
        return true
    }

    private fun browser() {
        mBrowser = FileBrowser.createFileBrowser(this) { path: String? ->
            if (mBrowser.isShowing) {
                LoadFile().execute(path)
                mBrowser.dismiss()
            }
        }
        mBrowser.setOnDismissListener { dialog: DialogInterface? -> }
        mBrowser.show()
    }

    private fun filter(newText: String): Boolean {
        return if (mAdapter != null) {
            if (newText.isNotEmpty()) {
                mAdapter!!.filter.filter(newText)
            }
            true
        } else {
            loader()
            false
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return filter(query)
    }

    override fun onQueryTextChange(newText: String): Boolean {
        return filter(newText)
    }

    // Getting More Info about provided Line
    @SuppressLint("StaticFieldLeak")
    internal inner class GetJson : AsyncTask<Void?, Void?, Void?>() {

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            if (contactList.size == 0) {
                Snackbar.make(mPlaylistList, "Currently No info Available", Snackbar.LENGTH_LONG).show()
                return
            }
            val ff = contactList[1].toString()
            Log.e(TAG, contactList[1].toString())
            Toast.makeText(applicationContext, ff, Toast.LENGTH_LONG).show()
        }

        override fun doInBackground(vararg params: Void?): Void? {
            val jsonStr = HttpHandler().makeServiceCall(url)
            Log.i(TAG, "Response from url: $jsonStr")
            if (jsonStr != null) {
                try {
                    val usrObj = JSONObject(jsonStr)
                    // Getting All info about User
                    for (i in 0 until usrObj.length()) {
                        val c = usrObj.getJSONObject("user_info")
                        val username = c.getString("username")
                        val passwd = c.getString("password")
                        val msg = c.getString("message")
                        val auth = c.getString("auth")
                        val status = c.getString("status")
                        val exp = c.getString("exp_date")
                        val is_trial = c.getString("is_trial")
                        val activeCon = c.getString("active_cons")
                        val createdat = c.getString("created_at")
                        val max_connections = c.getString("max_connections")
                        // Getting Array
                        val phone = c.getJSONArray("allowed_output_formats")
                        val ph1 = phone.getString(0)
                        val ph2 = phone.getString(1)
                        val ph3 = phone.getString(2)
                        // Getting Server Info
                        val serverObj = usrObj.getJSONObject("server_info")
                        val servUrl = serverObj.getString("url")
                        val servPort = serverObj.getString("port")
                        val servRtmp = serverObj.getString("rtmp_port")
                        val servZone = serverObj.getString("timezone")
                        // adding each child node to HashMap key => value
                        val contact = HashMap<String, String>()
                        contact["username"] = username
                        contact["mobile"] = "$ph1, $ph2"
                        contact["passwd"] = passwd
                        contact["msg"] = msg
                        contact["auth"] = auth
                        contact["status"] = status
                        contact["exp"] = exp
                        contact["isTrial"] = is_trial
                        contact["activeCon"] = activeCon
                        contact["createdAt"] = createdat
                        contact["maxConn"] = max_connections
                        // adding contact to contact list
                        contactList.add(contact)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Json parsing error: $e")
                    runOnUiThread {
                        Toast.makeText(applicationContext,
                                "Json parsing error: " + e.message,
                                Toast.LENGTH_LONG).show()
                    }
                }
            }
            return null
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class LoadFile : AsyncTask<String?, Void?, Boolean>() {
        override fun onPreExecute() {
            super.onPreExecute()
            spinner!!.visibility = View.VISIBLE
        }

        override fun onPostExecute(aBoolean: Boolean) {
            super.onPostExecute(aBoolean)
            spinner!!.visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): Boolean {
            return try { //new FileInputStream (new File(name)
                `is` = FileInputStream(File(params[0])) // if u r trying to open file from asstes InputStream is = getassets.open(); InputStream
                val playlist = parser.parseFile(`is`)
                mAdapter!!.update(playlist.playlistItems!!)
                true
            } catch (e: Exception) {
                Log.d("Google", "_loadFile: $e")
                false
            }
        }
    }
}