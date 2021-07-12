package com.muparse.activities

import android.net.Uri
import android.os.Bundle
import android.view.WindowManager

import androidx.appcompat.app.AppCompatActivity

import com.devbrackets.android.exomedia.ui.widget.VideoView
import com.muparse.R

class PlayerExo : AppCompatActivity(), com.devbrackets.android.exomedia.listener.OnPreparedListener {

    private lateinit var videoView: VideoView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_exo)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val bundle = intent.extras

        videoView = findViewById(R.id.video_view)
        videoView.setOnPreparedListener(this)
        videoView.setVideoURI(Uri.parse(bundle!!.getString("Url")))
    }

    override fun onPrepared() {
        videoView.start()
    }
}
