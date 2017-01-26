package com.muparse;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity{

    private static final File DEFA = Environment.getExternalStorageDirectory();
    TextView mPlaylistParams;
    RecyclerView mPlaylistList;
    private File dir = new File(DEFA.getAbsolutePath()+"/Netuptv");

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PlaylistAdapter mAdapter;

        InputStream is;
        M3UParser parser = new M3UParser();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
            //Toast.makeText(this, playlist.getSingleParameter("getItemUrl"), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

//    public void blaClick(View view) {
//        Intent intent = new Intent(Intent.ACTION_VIEW); //http://portal.onlineiptv.net:5210/get.php?username=000&password=000&type=m3u&output=ts
//        Uri videoUri = Uri.parse("http://portal.onlineiptv.net:5210/live/000/000/8517.ts");
//        intent.setDataAndType( videoUri, "application/x-mpegURL" );
//        intent.setPackage( "com.mxtech.videoplayer.ad" );
//        startActivity( intent );
//    }

    public boolean isAppInstalled(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @SuppressWarnings("All")
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    if (!dir.exists()){
                        dir.mkdir();
                    }
                    //user: X6QXN76met passwd: Jyw2SMYjxe
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://portal.onlineiptv.net:5210/get.php?username=000&password=000&type=m3u&output=ts"));
//                    startActivity(browserIntent);
                } else {
                    // permission denied, boo! Disable the
                    Toast.makeText(this, "Permission denied ☻", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}