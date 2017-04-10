package com.muparse;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;

public class playerExo extends AppCompatActivity {

    EMVideoView emVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_exo);

        emVideoView = (EMVideoView) findViewById(R.id.video_view);
        try{
            emVideoView.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared() {
                    emVideoView.setVideoURI(Uri.parse("http://portal.onlineiptv.net:5210/live/fNOaPbcqCB/yttnwNGpCR/6952.ts"));
                    emVideoView.start();
                }
            });
        }catch (Exception e){ Log.d("Google",""+e.toString()); }
    }
}
