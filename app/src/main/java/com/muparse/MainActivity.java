package com.muparse;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    final M3UParser parser = new M3UParser();
    ProgressBar spinner;
    TextView mPlaylistParams;
    RecyclerView mPlaylistList;
    InputStream is;
    PlaylistAdapter mAdapter;
    SharedPreferences.Editor editor;
    ArrayList<HashMap<String, String>> contactList = new ArrayList<>();
    private String TAG = MainActivity.class.getSimpleName();
    private android.app.AlertDialog mBrowser = null;
    private String url = Login.getInstance().urlLink;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlaylistParams = findViewById(R.id.playlist_params);
        mPlaylistList = findViewById(R.id.playlist_recycler);
        spinner = findViewById(R.id.login_progress);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mPlaylistList.setLayoutManager(layoutManager);
        mAdapter = new PlaylistAdapter(this);
        mPlaylistList.setAdapter(mAdapter);
        loader(Login.getInstance().filepath.getPath());
//        new _loadFile().execute(filepath.getPath()); // this will read direct channels from url
        //new GetJson().execute(); // this is getting info about User, channels etc.
    }

    void loader(String name) {

        try { //new FileInputStream (new File(name)
            is = getAssets().open("data.db"); // if u r trying to open file from asstes InputStream is = getassets.open(); InputStream
            M3UPlaylist playlist = parser.parseFile(is);
            mAdapter.update(playlist.getPlaylistItems());
        } catch (Exception e) {
            Log.d("Google", "" + e.toString());
        }
    }

    protected void onResume() {
        super.onResume();
        boolean isAccess = PreferencesManager.getBoolean(this, "isLogged", false);
//        if (!isAccess) {
//            startActivity(new Intent(MainActivity.this, Login.class));
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem search = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setQueryHint("Search channel name");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return filter(query);
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                //TODO here changes the search text)
                return filter(newText);
            }
        });
        searchView.setOnCloseListener(() -> {
            new _loadFile().execute(Login.getInstance().filepath.getPath());
            return false;
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_search:
                setContentView(R.layout.searchable);
                break;
            case R.id.logout:
                editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.moreInfo:
                new GetJson().execute();
                break;
            case R.id.browse:
                browser();
                break;
            case R.id.about:
                Intent abt = new Intent(MainActivity.this, About.class);
                startActivity(abt);
                break;
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }

    private void browser() {
        if (mBrowser == null) {
            mBrowser = FileBrowser.createFileBrowser(this, path -> {
                if (mBrowser != null && mBrowser.isShowing()) {
                    new _loadFile().execute(path);
                    mBrowser.dismiss();
                }
            });
            mBrowser.setOnDismissListener(dialog -> {
            });
        }
        mBrowser.show();
    }

    private boolean filter(final String newText) {
        if (mAdapter != null) {
            if (!newText.isEmpty()) {
                mAdapter.getFilter().filter(newText);
            }
            return true;
        } else {
            loader(Login.getInstance().filepath.getPath());
            return false;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return filter(query);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return filter(newText);
    }

    // Getting More Info about provided Line
    @SuppressLint("StaticFieldLeak")
    class GetJson extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            HttpHandler hh = new HttpHandler();
            String jsonStr = hh.makeServiceCall(url);
            Log.i(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject usrObj = new JSONObject(jsonStr);
                    //BsSF*{mR[NBW
                    //sajjadja_ved buhql0n33nfa
                    // looping through All Info
                    for (int i = 0; i < usrObj.length(); i++) {

                        // Getting All info about User
                        JSONObject c = usrObj.getJSONObject("user_info");
                        String username = c.getString("username");
                        String passwd = c.getString("password");
                        String msg = c.getString("message");
                        String auth = c.getString("auth");
                        String status = c.getString("status");
                        String exp = c.getString("exp_date");
                        String is_trial = c.getString("is_trial");
                        String activeCon = c.getString("active_cons");
                        String createdat = c.getString("created_at");
                        String max_connections = c.getString("max_connections");

                        // Getting Array
                        JSONArray phone = c.getJSONArray("allowed_output_formats");
                        String ph1 = phone.getString(0);
                        String ph2 = phone.getString(1);
                        String ph3 = phone.getString(2);

                        // Getting Server Info
                        JSONObject serverObj = usrObj.getJSONObject("server_info");
                        String servUrl = serverObj.getString("url");
                        String servPort = serverObj.getString("port");
                        String servRtmp = serverObj.getString("rtmp_port");
                        String servZone = serverObj.getString("timezone");

                        // adding each child node to HashMap key => value
                        HashMap<String, String> contact = new HashMap<>();
                        contact.put("username", username);
                        contact.put("mobile", ph1 + ", " + ph2);
                        contact.put("passwd", passwd);
                        contact.put("msg", msg);
                        contact.put("auth", auth);
                        contact.put("status", status);
                        contact.put("exp", exp);
                        contact.put("isTrial", is_trial);
                        contact.put("activeCon", activeCon);
                        contact.put("createdAt", createdat);
                        contact.put("maxConn", max_connections);

                        // adding contact to contact list
                        contactList.add(contact);
                    }
                } catch (final Exception e) {
                    Log.e(TAG, "Json parsing error: " + e.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String ff = String.valueOf(contactList.get(1));
            Log.e(TAG, String.valueOf(contactList.get(1)));
            Toast.makeText(getApplicationContext(), ff, Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class _loadFile extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try { //new FileInputStream (new File(name)
                is = new FileInputStream(new File(strings[0])); // if u r trying to open file from asstes InputStream is = getassets.open(); InputStream
                M3UPlaylist playlist = parser.parseFile(is);
                mAdapter.update(playlist.getPlaylistItems());
                return true;
            } catch (Exception e) {
                Log.d("Google", "_loadFile: " + e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            spinner.setVisibility(View.GONE);
        }
    }
}