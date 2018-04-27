package com.rq.videoplayer_faydee;

import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
import android.view.View;
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
    private static final String KEY_IS_FULLSCREEN = "KEY_IS_FULLSCREEN";
    private static final String KEY_CURRENTPOSITION = "KEY_CURRENTPOSITION";
    private static final String KEY_IS_PAUSE = "KEY_IS_PAUSE";

    private String mVideoUrl = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/taeyeon.mp4";
//    private String mVideoUrl = "https://s3-ap-northeast-1.amazonaws.com/mid-exam/Video/protraitVideo.mp4";

    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;
    private VideoControllerView mVideoControllerView;

    private FrameLayout mBackgroundFrameLayout;

    private boolean mIsFullScreen = false;
    private int mCurrentPosition;
    private boolean mIsPause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBackgroundFrameLayout = findViewById(R.id.backgroundFrameLayout);

        mSurfaceView = findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = mSurfaceView.getHolder();
        videoHolder.addCallback(this);

        mMediaPlayer = new MediaPlayer();
        mVideoControllerView = new VideoControllerView(this);

        if (mCurrentPosition == 0) {
            playVideo();
        }

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, "onCreate orientation: PORTRAIT");
            mIsFullScreen = false;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged");

        mMediaPlayer.pause();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //橫屏
            Log.d(TAG, "LANDSCAPE");
            mIsFullScreen = true;
            mVideoControllerView.updateFullScreen();

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //豎屏
            Log.d(TAG, "PORTRAIT");
            mIsFullScreen = false;
            mVideoControllerView.updateFullScreen();
        }
    }

    // 轉向時會先呼叫 onSaveInstanceState，再呼叫 onRestoreInstanceState
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        Log.d(TAG, "onSaveInstanceState");
        // 使用 key-value 型態來傳遞資料
        outState.putBoolean(KEY_IS_FULLSCREEN, mIsFullScreen);
        outState.putInt(KEY_CURRENTPOSITION, mMediaPlayer.getCurrentPosition());
        outState.putBoolean(KEY_IS_PAUSE, mIsPause);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        Log.d(TAG, "onRestoreInstanceState");
        mIsFullScreen = savedInstanceState.getBoolean(KEY_IS_FULLSCREEN);
        mCurrentPosition = savedInstanceState.getInt(KEY_CURRENTPOSITION);
        mIsPause = savedInstanceState.getBoolean(KEY_IS_PAUSE);
    }

    private void playVideo() {
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
//        if (!mVideoControllerView.isShowing()) {
//            mVideoControllerView.show();
//        } else {
//            mVideoControllerView.hide();
//        }
        mVideoControllerView.show();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        setVideoSize();
        mVideoControllerView.setMediaPlayer(this);
        mVideoControllerView.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        mMediaPlayer.start();

        // 直向時維持顯示播放控制器
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mVideoControllerView.show(0);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        mMediaPlayer.setDisplay(holder);
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCurrentPosition != 0){
            mMediaPlayer.seekTo(mCurrentPosition);
            if (mIsPause) {
                mMediaPlayer.pause();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
    }

    @Override
    public void start() {
        mMediaPlayer.start();
        mIsPause = false;
    }

    @Override
    public void pause() {
        mMediaPlayer.pause();
        mIsPause = true;
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
        return !mIsPause;
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
//        Log.d(TAG, "isFullScreen: " + String.valueOf(isFullScreen));
        return mIsFullScreen;
    }

    @Override
    public void toggleFullScreen() {
        mMediaPlayer.pause();

        if (!mIsFullScreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void mute() {
        mMediaPlayer.setVolume(0,0);
    }

    @Override
    public void unMute() {
        mMediaPlayer.setVolume(1,1);
    }

}
