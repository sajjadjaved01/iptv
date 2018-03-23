package com.muparse;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;

public class playerExo extends AppCompatActivity {

    VideoView emVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_exo);

        emVideoView = findViewById(R.id.video_view);
        try{
            emVideoView.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared() {
                    emVideoView.setVideoURI(Uri.parse("http://portal.onlineiptv.net:5210/live/fNOaPbcqCB/yttnwNGpCR/6952.ts"));
                    emVideoView.start();
                }
            });
        } catch (Exception ignored) {
        }
    }
}
