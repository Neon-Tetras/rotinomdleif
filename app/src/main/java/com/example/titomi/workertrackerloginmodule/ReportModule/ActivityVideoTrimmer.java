package com.example.titomi.workertrackerloginmodule.ReportModule;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.titomi.workertrackerloginmodule.R;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;



public class ActivityVideoTrimmer extends AppCompatActivity implements OnTrimVideoListener
{

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_trimmer);

        K4LVideoTrimmer videoTrimmer =  findViewById(R.id.timeLine);

        if (videoTrimmer != null) {

            videoTrimmer.setVideoURI(Uri.parse(getIntent().getExtras().getString("video")));
            videoTrimmer.setMaxDuration(30);
            videoTrimmer.setOnTrimVideoListener(this);

        }
    }


    @Override
    public void getResult(Uri uri) {

        Intent i = new Intent();
        i.putExtra("video",uri.toString());
        setResult(RESULT_OK,i);
        finish();

    }

    @Override
    public void cancelAction() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
