package com.example.titomi.workertrackerloginmodule.report_module;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.services.FieldMonitorReportUploadService;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ImageUtils;
import com.example.titomi.workertrackerloginmodule.supervisor.util.InputValidator;
import com.example.titomi.workertrackerloginmodule.supervisor.util.NetworkChecker;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int ACTIVITY_RECORD_SOUND = 0;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    private static final int TRIM_VIDEO = 10;
    FloatingActionButton fab_photo, fab_record, fab_send, fab_remove_photo,fab_video;
    FloatingActionMenu floatingActionMenu;
    TextView playVideoText;
    Context cxt;
    LinearLayout reportImagesLayout;
    File outputMedia;
    Uri file;
    String videoPath;
    ArrayList<String> reportImages = new ArrayList<>();
    private EditText timeEditText, taskTitleEdit,
            taskDescriptionEdit, institutionNameEdit,
            fullAddressEdit, quantityEdit, contactFullNameEdit, contactNumberEdit, locationEdit, dateEditText, stateEdit, lgaEdit, taskTypeEdit, participantsEdit, quantitySoldEdit, commentsEdit;
    private User loggedInUser;
    private Task selectedTask;
    private String stopLat,stopLong;

    private static File getOutputMediaFile() {

        String filePath = ImageUtils.ImageStorage.getStorageDirectory(new Task()).getAbsolutePath();
       /* File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "FieldMonitor");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }*/
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
       /* return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");*/
        return new File(filePath + File.separator +
                "IMG_" + timeStamp + ".jpg");
        //filePath;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
       /* Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
System.out.println(this.getClass().getPackage());
        cxt = this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        floatingActionMenu = findViewById(R.id.floatingActionMenu);
        fab_photo = findViewById(R.id.fab_photo);
        fab_record = findViewById(R.id.fab_record);
        fab_send = findViewById(R.id.fab_send);
        fab_remove_photo = findViewById(R.id.fab_remove_photo);
        fab_video = findViewById(R.id.fab_video);
        playVideoText = findViewById(R.id.playVideoText);

        dateEditText = findViewById(R.id.dateText);
        timeEditText = (EditText)findViewById(R.id.timeText);
        reportImagesLayout = findViewById(R.id.imagesLayout);
        stateEdit = findViewById(R.id.state);
        lgaEdit = findViewById(R.id.lga);

        taskTitleEdit = findViewById(R.id.taskTitle);
        taskDescriptionEdit = findViewById(R.id.description);
        institutionNameEdit = findViewById(R.id.institution);
        fullAddressEdit = findViewById(R.id.fullAddress);
//        quantityEdit = findViewById(R.id.quantity);
        contactFullNameEdit = findViewById(R.id.contactFullName);
        contactNumberEdit = findViewById(R.id.contactPhone);

        taskTypeEdit = findViewById(R.id.taskType);
        locationEdit = findViewById(R.id.location);

        commentsEdit = findViewById(R.id.commentField);
        participantsEdit = findViewById(R.id.participantsEdit);
        quantitySoldEdit = findViewById(R.id.quantityDistributedEdit);


        fab_photo.setOnClickListener(this);
        fab_video.setOnClickListener(this);
        fab_send.setOnClickListener(this);
        fab_record.setOnClickListener(this);

        if(getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            loggedInUser = (User)extras.getSerializable(getString(R.string.loggedInUser));
            selectedTask = (Task)extras.getSerializable("task");
            stopLat = extras.getString("stop_lat");
            stopLong = extras.getString("stop_long");

            try {
                setupView(selectedTask);
            }catch (NullPointerException e){
                Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();
            }

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            fab_photo.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

    }

    private void setupView(Task task){
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy/M/dd");
        dateEditText.setText(dtf.format(task.getDateGiven()));
        timeEditText.setText(task.getTimeGiven());

        stateEdit.setText(task.getState());
        lgaEdit.setText(task.getLga());

        taskTitleEdit.setText(task.getName());
        taskDescriptionEdit.setText(task.getDescription());
        institutionNameEdit.setText(task.getInstitution_name());
        fullAddressEdit.setText(task.getAddress());
        quantityEdit.setText(""+task.getQuantity());
        contactFullNameEdit.setText(task.getContactName());
        contactNumberEdit.setText(task.getContactNumber());

        taskTypeEdit.setText(task.getWorkType());
        locationEdit.setText(task.getLocation());

    }

    private void captureImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        outputMedia = getOutputMediaFile();
        file = Uri.fromFile(outputMedia);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        startActivityForResult(intent, 100);
      //dispatchTakePictureIntent();
       // onLaunchCamera(findViewById(R.id.image));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                fab_photo.setEnabled(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {


                ImageUtils.ImageStorage storage = new ImageUtils.ImageStorage(new Task());
                if(storage.imageExists(outputMedia.getName())) {
                    //  Util.getRealPathFromURI(cxt, file);
                    if(!reportImages.contains(outputMedia.getAbsolutePath())){
                        reportImages.add(outputMedia.getAbsolutePath());
                        loadReportImages(reportImages);
                    }
                }

            }
        }
        switch (requestCode){
            case Util.PICK_VIDEO:
                Uri videoUri = data.getData();
                Intent i = new Intent(this, ActivityVideoTrimmer.class);
                if (videoUri != null) {
                    i.putExtra("video",videoUri.toString());
                    startActivityForResult(i,TRIM_VIDEO);
                }

                break;
            case TRIM_VIDEO:
                String uri = data.getExtras().getString("video");
                videoPath =  Util.getVideoPath(this,Uri.parse(uri));

                if(videoPath != null){
                    playVideoText.setVisibility(View.VISIBLE);
                    playVideoText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(ReportActivity.this,VideoPlayer.class);
                            i.putExtra("videoUrl",videoPath);
                            startActivity(i);
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_photo:
                captureImage();
                break;
            case R.id.fab_send:
                if(!NetworkChecker.haveNetworkConnection(cxt)){return;}

                Intent i = new Intent(cxt,FieldMonitorReportUploadService.class);
                i.putExtra("video",videoPath);
                i.putStringArrayListExtra("images",reportImages);

                HashMap<String,String> postData = new HashMap<>();
                try {
                    postData.put("user_id",String.format("%d",loggedInUser.getId()));
                    postData.put("task_id",String.format("%d",selectedTask.getId()));
                    String stopTime = DateFormat.getDateTimeInstance().format(new Date()).replaceAll("/","-");
                    postData.put("stop_time",stopTime);
                    postData.put("stop_latitude",stopLat);
                    postData.put("stop_longitude",stopLong);
                    postData.put("product_id",""+selectedTask.getProductId());

                    postData.put("participants", InputValidator.validateText(participantsEdit,1));
                    postData.put("quantity_sold", InputValidator.validateText(quantitySoldEdit,1));
                    postData.put("challenges", commentsEdit.getText().toString());

                    i.putExtra("postData",postData);
                    i.putExtra(getString(R.string.loggedInUser),loggedInUser);


                    startService(i);

                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.coordinator), "Report will be submitted in the background.\nPlease do not resend", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    fab_send.setEnabled(false);
                    fab_photo.setEnabled(false);
                    fab_record.setEnabled(false);
                    fab_video.setEnabled(false);
                    fab_remove_photo.setEnabled(false);

                    snackbar.show();
                } catch (InputValidator.InvalidInputException e) {
                    Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();
                }



                break;
            case R.id.fab_video:
                Util.requestPermission(ReportActivity.this,Util.PICK_VIDEO);
                break;
        }


    }

    private void loadReportImages(final ArrayList<String> images){


        reportImagesLayout.removeAllViews();
        //String[] images = task.getImages().split(",");
        for(final String image : images) {
            final View view = getLayoutInflater().inflate(R.layout.report_image_single_item_with_delete_button, null);


            final ImageView reportImage = view.findViewById(R.id.reportImage);
            final ImageView deleteImage = view.findViewById(R.id.delete);

            reportImage.setImageDrawable(Drawable.createFromPath(image));

            deleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportImagesLayout.removeView(view);
                    images.remove(image);
                }
            });
            reportImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Util.viewImages(cxt, reportImage, images);
                }
            });
            reportImagesLayout.addView(view);
        }




    }

    private void recordAudio(){
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, ACTIVITY_RECORD_SOUND);
    }



}







