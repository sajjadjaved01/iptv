package com.muparse;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;

import static com.muparse.Login.dir;
import static com.muparse.Login.filepath;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    final M3UParser parser = new M3UParser();
    TextView mPlaylistParams;
    RecyclerView mPlaylistList;
    InputStream is;
    PlaylistAdapter mAdapter;
    SharedPreferences.Editor editor;
    private android.app.AlertDialog mBrowser = null;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlaylistParams = (TextView) findViewById(R.id.playlist_params);
        mPlaylistList = (RecyclerView) findViewById(R.id.playlist_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mPlaylistList.setLayoutManager(layoutManager);
        File fCheck = new File(filepath.getPath());
        if (fCheck.exists()) {
            Log.d("Google", dir.getPath() + "/iptv_data.m3u");
        }
        loader("file:///storage/emulated/0/Netuptv/iptv_data.m3u");

    }

    void loader(String name) {
        mAdapter = new PlaylistAdapter(this);
        mPlaylistList.setAdapter(mAdapter);

        try {
//            File lfile = new File(name);
//            if (name.startsWith("file:///storage/emulated/0/")){
//                lfile = new File(Environment.getExternalStorageDirectory(), name.substring(27));
//            }
            is = getAssets().open(String.valueOf(name));
            M3UPlaylist playlist = parser.parseFile(is);
            mPlaylistParams.setText(playlist.getPlaylistParams());
            mAdapter.update(playlist.getPlaylistItems());
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public boolean isPackageInstalled(PackageManager packageManager) {
        try {
            packageManager.getPackageInfo("com.mxtech.videoplayer.ad", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    protected void onResume() {
        super.onResume();
        boolean isAccess = isLoggedIn();
        if (!isAccess) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
        }
    }

    boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return prefs.getBoolean("isLogged", false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        MenuItem search = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
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
                startActivity(intent);
                break;
            case R.id.browse:
                browser();
                break;
            case R.id.about:
                Intent abt = new Intent(getApplicationContext(), About.class);
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
            mBrowser = FileBrowser.createFileBrowser(this, new FileBrowser.OnFileSelectedListener() {

                @Override
                public void onFileSelected(String path) {
                    if (mBrowser != null && mBrowser.isShowing()) {
                        loader(path);
                        mBrowser.dismiss();
                    }

                }
            });
            mBrowser.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                }
            });
        }
        mBrowser.show();
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        Toast.makeText(this, "" + query, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}