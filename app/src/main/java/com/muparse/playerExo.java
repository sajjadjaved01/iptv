package com.muparse;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.devbrackets.android.exomedia.ui.widget.VideoView;

public class playerExo extends AppCompatActivity implements com.devbrackets.android.exomedia.listener.OnPreparedListener {

    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_exo);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle bundle = getIntent().getExtras();

        videoView = findViewById(R.id.video_view);
        videoView.setOnPreparedListener(this);
        videoView.setVideoURI(Uri.parse(bundle.getString("Url")));
    }

    @Override
    public void onPrepared() {
        videoView.start();
    }
}
