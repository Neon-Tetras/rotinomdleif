package com.example.titomi.workertrackerloginmodule.ReportModule;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.titomi.workertrackerloginmodule.R;


public class VideoPlayer extends AppCompatActivity {

    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        videoView = (VideoView)findViewById(R.id.videoView);
        if(getIntent() != null){
            Bundle extras = getIntent().getExtras();

            String videoUrl = extras.getString("videoUrl");
             videoView.setVideoURI(Uri.parse(videoUrl));
            MediaController vidControl = new MediaController(this);

             vidControl.setAnchorView(videoView);
            videoView.setMediaController(vidControl);
            videoView.start();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

            default:

                return super.onOptionsItemSelected(item);
        }


    }
    @Override
    public void onConfigurationChanged(Configuration configuration){
        super.onConfigurationChanged(configuration);
    }
    @Override
    public void onResume() {
        videoView.resume();
        super.onResume();

    }
    @Override
    public  void onPause(){
        videoView.pause();
        super.onPause();

    }
    @Override
    public void onDestroy(){
        videoView.stopPlayback();
        super.onDestroy();

    }
}
