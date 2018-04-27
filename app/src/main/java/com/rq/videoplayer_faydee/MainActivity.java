package com.rq.videoplayer_faydee;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements
        SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener,
        VideoControllerView.MediaPlayerControl {

    private static final String TAG = MainActivity.class.getSimpleName();

//        private String mVideoUrl = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/taeyeon.mp4";
    private String mVideoUrl = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/protraitVideo.mp4";

//    private VideoView mVideoView;
//    private MediaController mMediaController;

    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;
    private VideoControllerView mVideoControllerView;

    private FrameLayout mBackgroundFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBackgroundFrameLayout = findViewById(R.id.backgroundFrameLayout);

        // 舊寫法
//        mVideoView = findViewById(R.id.videoView);

//        mMediaController = new MediaController(this);
//        mMediaController.setAnchorView(mVideoView);
//        mVideoView.setMediaController(mMediaController);

//        mVideoView.setVideoURI(Uri.parse(mVideoUrl));
//        mVideoView.start();

        // 測試
        mSurfaceView = findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = mSurfaceView.getHolder();
        videoHolder.addCallback(this);

        mMediaPlayer = new MediaPlayer();
        mVideoControllerView = new VideoControllerView(this);

        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(this, Uri.parse(mVideoUrl));
            mMediaPlayer.setOnPreparedListener(this);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setVideoSize() {
        // // Get the dimensions of the video
        int videoWidth = mMediaPlayer.getVideoWidth();
        int videoHeight = mMediaPlayer.getVideoHeight();
        float videoProportion = (float) videoWidth / (float) videoHeight;

        // Get the width of the screen
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        float screenProportion = (float) screenWidth / (float) screenHeight;

        // Get the SurfaceView layout parameters
        android.view.ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
        if (videoProportion > screenProportion) {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth / videoProportion);
        } else {
            lp.width = (int) (videoProportion * (float) screenHeight);
            lp.height = screenHeight;
        }

//        Log.d(TAG, "lp.width: " + String.valueOf(lp.width));

//        float height = (float) lp.height;

        // 調整直向影片寬高
        if (videoHeight > videoWidth) {
            int newHeight = (int) DPTool.convertDpToPixel(350, this);
            lp.height = newHeight;

            lp.width = (int) (videoProportion * (float) newHeight);
//            Log.d(TAG, "new lp.width: " + String.valueOf(lp.width));

            // 直向影片的兩側給黑色背景
            mBackgroundFrameLayout.setBackgroundColor(Color.BLACK);
        }


        // Commit the layout parameters
        mSurfaceView.setLayoutParams(lp);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mVideoControllerView.show();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        setVideoSize();
        mVideoControllerView.setMediaPlayer(this);
        mVideoControllerView.setAnchorView((RelativeLayout) findViewById(R.id.videoSurfaceContainer));
        mMediaPlayer.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void start() {
        mMediaPlayer.start();
    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mMediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {

    }
}
