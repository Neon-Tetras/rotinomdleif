package com.example.titomi.workertrackerloginmodule.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityTaskListing;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ImageUtils;
import com.example.titomi.workertrackerloginmodule.supervisor.util.MediaUploader;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by NeonTetras on 27-Feb-18.
 */

public class FieldMonitorReportUploadService extends Service {
    private static Context cxt;
    private Timer mTimer1;
    private TimerTask mTt1;
    ArrayList<String> images;
    String video;
    HashMap<String,String> postData;
    User loggedInUser;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        postData = ( HashMap<String,String> )intent.getSerializableExtra("postData");
        images = intent.getStringArrayListExtra("images");
        video = intent.getStringExtra("video");
        loggedInUser = (User)intent.getSerializableExtra(getString(R.string.loggedInUser));
        cxt = this;

        notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //startTimer();

        NotificationCompat.Builder notifBuilder =
                new NotificationCompat
                        .Builder(cxt,getString(R.string.submitting_report))
                        .setSmallIcon(R.mipmap.app_logo)
                        .setContentText("Submitting report...,Please wait")
                        .setContentTitle("Field monitor");



        notifBuilder.setAutoCancel(true);
        notifBuilder.setLights(Color.GREEN,60000,60000);

        notifManager.notify(submittingReportNotif, notifBuilder.build());

        images = ImageUtils.compressImages(this,new Task(),images);
        StringBuilder sb = new StringBuilder();
        for (String img : images) {
            sb.append(String.format("images/reports/%s,",new File(img).getName()));
        }
        sb.deleteCharAt(sb.toString().lastIndexOf(","));
        postData.put("photo",sb.toString());
        postData.put("video",String.format("videos/%s,",new File(video).getName()));
        if (video == null || Objects.equals(video,"")) {
            NUM_ACTIONS = 2;
        }
        sendReport();

        uploadImages();
        if(video != null || Objects.equals(video, "")) {

            uploadVideo();
        }

        return START_STICKY;

    }



    private void uploadImages(){

        ImageUploader imageUploader = new ImageUploader(this, String.format("%s%s", getString(R.string.api_url), getString(R.string.image_upload_url)));
        imageUploader.execute(images);
    }

    private void uploadVideo(){

        ArrayList<String> videos = new ArrayList<>();
         videos.add(video);
        VideoUploader videoUploader = new VideoUploader(this, String.format("%s%s", getString(R.string.api_url), getString(R.string.video_upload_url)));
        videoUploader.execute(videos);
    }
    private void sendReport() {
        try {
            ReportSubmitNetwork network = new ReportSubmitNetwork();
            String getData = Network.getPostDataString(postData);
            System.out.printf("Outputing get data: %s", getData);

            network.execute(getString(R.string.api_url) + getString(R.string.clockOutUrl) + "?key=" + getString(R.string.field_worker_api_key) + "&" + getData);
        }catch(UnsupportedEncodingException e){
            Log.e(getClass().getName(),e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private final IBinder binder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

            return binder;
    }


    public class MyBinder extends Binder {
        public FieldMonitorReportUploadService getService() {
            return FieldMonitorReportUploadService.this;
        }

    }


    private  class ReportSubmitNetwork extends AsyncTask<String,Void,String>{




        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null,strings[0]);//strings[0],this.postData);
        }

        @Override
        protected void onPostExecute(String strings) {

            super.onPostExecute(strings);
            System.out.println(strings);
            if(strings == null) return;
            actionCount++;
            if(actionCount == NUM_ACTIONS){
                notifyCompletion();
                stopSelf();
            }
        }
    }

    class ImageUploader extends MediaUploader{
        public ImageUploader(Context cxt, String uploadApiUrl) {
            super(cxt, uploadApiUrl);
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            if(strings == null) return;
            actionCount++;
            if(actionCount == NUM_ACTIONS){
                notifyCompletion();
                stopSelf();
            }
        }


    }

    class VideoUploader extends  MediaUploader{
        public VideoUploader(Context cxt, String uploadApiUrl) {
            super(cxt, uploadApiUrl);
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            super.onPostExecute(strings);
            if(strings == null) return;
            actionCount++;
            if(actionCount == NUM_ACTIONS){
                notifyCompletion();
                stopSelf();
            }
        }
    }

    private void notifyCompletion(){

        notifManager.cancel(submittingReportNotif);
        NotificationCompat.Builder notifBuilder =
                new NotificationCompat
                        .Builder(cxt,getString(R.string.submitting_report))
                        .setSmallIcon(R.mipmap.app_logo)
                        .setContentText("Submitting complete")
                        .setContentTitle("Field monitor");

        Intent resultIntent = new Intent(cxt, ActivityTaskListing.class);
         resultIntent.putExtra(getString(R.string.loggedInUser),loggedInUser);

        PendingIntent pendingIntent =   PendingIntent.getActivity(
                cxt,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );



        Toast.makeText(cxt,"Report submitted successfully",Toast.LENGTH_LONG).show();
      //  startActivity(resultIntent);

        notifBuilder.setContentIntent(pendingIntent);
        notifBuilder.setAutoCancel(true);
        notifBuilder.setLights(Color.GREEN,60000,60000);
        notifBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        notifBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });



        notifManager.notify(submissionComplete, notifBuilder.build());

    }

    NotificationManager notifManager;
    int submittingReportNotif = 001;
    int submissionComplete = 002;
    private static int actionCount = 0;
    private static int NUM_ACTIONS = 3;
}
