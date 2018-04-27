package com.rq.videoplayer_faydee;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private String mVideoUrl = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/taeyeon.mp4";
//    private String mVideoUrl = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/protraitVideo.mp4";

    private VideoView mVideoView;
    private MediaController mMediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = findViewById(R.id.videoView);

        mMediaController = new MediaController(this);
        mMediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mMediaController);

        mVideoView.setVideoURI(Uri.parse(mVideoUrl));
        mVideoView.start();
    }
}
