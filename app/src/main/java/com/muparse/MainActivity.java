package com.muparse;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SearchViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.muparse.R.id.channelName;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    TextView mPlaylistParams;
    RecyclerView mPlaylistList;
    SessionManagement sess;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PlaylistAdapter mAdapter;

        InputStream is;
        M3UParser parser = new M3UParser();

        EditText channelName = (EditText) findViewById(R.id.channelName);
        ListView lv = (ListView) findViewById(R.id.list_view);
        AutoCompleteTextView mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPlaylistParams = (TextView) findViewById(R.id.playlist_params);
        mPlaylistList = (RecyclerView) findViewById(R.id.playlist_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mPlaylistList.setLayoutManager(layoutManager);
        mAdapter = new PlaylistAdapter(this);
        mPlaylistList.setAdapter(mAdapter);

        try {
            is = getAssets().open("tv_channels.m3u");
            M3UPlaylist playlist = parser.parseFile(is);
            mPlaylistParams.setText(playlist.getPlaylistParams());
            mAdapter.update(playlist.getPlaylistItems());
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_search:
                setContentView(R.layout.searchable);
                break;
            case R.id.logout:
                sess.logoutUser();
                sess.deleteDirectory(Login.dir.getAbsoluteFile());
                break;
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        // Here is where we are going to implement the filter logic
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}