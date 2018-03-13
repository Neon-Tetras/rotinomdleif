package com.example.titomi.workertrackerloginmodule.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.report_module.ReportActivity;
import com.example.titomi.workertrackerloginmodule.supervisor.DatabaseAdapter;
import com.example.titomi.workertrackerloginmodule.supervisor.Messages;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ImageUtils;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Titomi on 3/9/2018.
 */

public class FieldMonitorRecordService extends Service {

    public static final int RequestPermissionCode = 1;
    private final IBinder binder = new FieldMonitorRecordService.MyBinder();
    private final int notification_id = 32;
    public String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    ReportActivity activity;
    NotificationManagerCompat managerCompat;
    Task task;
    NotificationCompat.Builder mBuilder;
    private Context cxt;
    private Timer mTimer1;
    private TimerTask mTt1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String t;
        t = intent.getStringExtra("filePath");

        AudioSavePathInDevice = t;
        MediaRecorderReady();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaRecorder.stop();
        mediaRecorder.release();
        managerCompat.cancel(32);
        Toast.makeText(this, "Recording Stopped", Toast.LENGTH_SHORT).show();
    }


    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }

    @SuppressLint("StaticFieldLeak")
    private void getNewTask() {

        new android.os.AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... strings) {
                return Network.backgroundTask(null, strings[0]);
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                // findViewById(R.id.inBoxProgressBar).setVisibility(View.GONE);

                if (s == null) {
                    //getInboxFromDb();
                    return;
                }


                try {
                    JSONArray jsonArray = new JSONArray(s);


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject msgs = jsonArray.getJSONObject(i);
                        JSONObject obj = msgs.getJSONObject("message");
                        Messages msg = new Messages();
                        msg.setPriority(obj.getString("priority"));
                        msg.setBody(obj.getString("body"));
                        msg.setId(obj.getInt("id"));
                        msg.setTitle(obj.getString("subject"));
                        SimpleDateFormat dtf = new SimpleDateFormat("dd/M/yyyy H:m:s");
                        //DateFormat dtf = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
                        msg.setDatePosted(dtf.parse(msgs.getString("date")));


                        JSONObject recipient = msgs.getJSONObject("receipient");
                        User receiver = new User();
                        receiver.setId(recipient.getInt("id"));
                        receiver.setName(recipient.getString("username"));

                        receiver.setUserLevel(recipient.getInt("roleId"));
                        receiver.setUserLevelText(recipient.getString("role"));
                        JSONObject sender = msgs.getJSONObject("sender");
                        User _sender = new User();
                        _sender.setId(sender.getInt("id"));
                        _sender.setName(sender.getString("username"));
                        _sender.setFeaturedImage(sender.getString("photo"));
                        _sender.setUserLevel(sender.getInt("roleId"));
                        _sender.setUserLevelText(sender.getString("role"));
                        msg.setFeaturedImage(_sender.getFeaturedImage());

                        msg.setPoster(_sender);
                        msg.setReceiver(receiver);

                        saveInboxMessages(msg);
                        //inboxMessages.add(msg);

                        //getInboxFromDb();
                        //   populateMessage(inboxMessages,messageInboxList);
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                    System.err.println(s);
                }

            }
        }.execute(getString(R.string.api_url) + getString(R.string.view_message_url) + "?key=" + getString(R.string.field_worker_api_key) + "&msg_type=inbox&user_id=" + 5);

    }

    private void saveInboxMessages(Messages msg) {
        DatabaseAdapter db = DatabaseAdapter.getInstance(cxt);
        if (!db.messageExists(msg.getId())) {
            ImageUtils.ImageStorage storage = new ImageUtils.ImageStorage(msg.getPoster());
            String imageUrl = getString(R.string.api_url) + msg.getFeaturedImage();
            String imageName = ImageUtils.getImageNameFromUrlWithExtension(imageUrl);
            if (!storage.imageExists(imageName)) {
                ImageUtils.GetImages getImages = new ImageUtils.GetImages(msg.getPoster(), imageUrl, imageName);
                getImages.execute();
            }


            //    Toast.makeText(cxt,"Message does not exists so save",Toast.LENGTH_SHORT).show();
          /*  if(db.saveInBox(msg.getPoster().getId(),msg.getId(),msg.getTitle(),msg.getBody(),msg.getPoster().getName(),msg.getPoster().getFeaturedImage(),msg.getPriority()) != -1){

            }*/
        }
    }


    private void startTimer() {
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            @Override
            public void run() {

                getNewTask();
            }

        };

        int minutes = 60000; //1 minute;
        mTimer1.schedule(mTt1, minutes);

    }

    private static class ReportNetwork extends android.os.AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null, strings[0]);
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  progressBar.setVisibility(View.GONE);

            if (s == null) return;

            try {
                JSONArray jsonArray = new JSONArray(s);

                if (jsonArray.length() > 0) {

                }

            } catch (JSONException e) {
                e.printStackTrace();
                System.err.println(s);
            }
        }
    }

    public class MyBinder extends Binder {
        public FieldMonitorRecordService getService() {
            return FieldMonitorRecordService.this;
        }

    }
}
