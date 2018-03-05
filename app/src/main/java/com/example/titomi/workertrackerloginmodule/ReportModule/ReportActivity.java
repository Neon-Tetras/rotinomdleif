package com.example.titomi.workertrackerloginmodule.ReportModule;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.HttpParse;
import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ImageUtils;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ReportActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    FloatingActionButton fab_photo, fab_record, fab_send, fab_remove_photo,fab_video;
    FloatingActionMenu floatingActionMenu;
    private EditText timeEditText,taskTitleEdit,
            taskDescriptionEdit,institutionNameEdit,
            fullAddressEdit,quantityEdit,contactFullNameEdit,contactNumberEdit,locationEdit, dateEditText,stateEdit, lgaEdit, taskTypeEdit;

    TextView report_date,playVideoText;
    String mReportRemarkHolder, institutionHolder, productSoldHolder, participantsHolder, mTaskTitleHolder, task_idHolder, productTypeHolder, mReportPhotoHolder;
    Button userClockIn, userClockOut;
    ImageView field_image;
    ProgressDialog progressDialog;
    HashMap<String, String> hashMap = new HashMap<>();
    String HttpURL = "https://chemotropic-partiti.000webhostapp.com/sample/InsertReports.php";
    HttpParse httpParse = new HttpParse();
    Boolean checkEditText;
    String finalResult;
    private Uri fileUri;

    Context cxt;

    LinearLayout reportImagesLayout;

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");
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
        reportImagesLayout = findViewById(R.id.reportImagesLayout);
        stateEdit = findViewById(R.id.state);
        lgaEdit = findViewById(R.id.lga);

        taskTitleEdit = findViewById(R.id.taskTitle);
        taskDescriptionEdit = findViewById(R.id.description);
        institutionNameEdit = findViewById(R.id.institution);
        fullAddressEdit = findViewById(R.id.fullAddress);
        quantityEdit = findViewById(R.id.quantity);
        contactFullNameEdit = findViewById(R.id.contactFullName);
        contactNumberEdit = findViewById(R.id.contactPhone);

        taskTypeEdit = findViewById(R.id.taskType);
        locationEdit = findViewById(R.id.location);

        fab_photo.setOnClickListener(this);
        fab_video.setOnClickListener(this);
        fab_send.setOnClickListener(this);
        fab_record.setOnClickListener(this);



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            fab_photo.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

    }


    public void insertReport(final String S_taskId, final String S_taskTitle, final String S_institution, final String S_participants, final String S_product_type, final String S_product_sold, final String S_remark, final String S_photo) {
        class insertReportClass extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = progressDialog.show(ReportActivity.this, "Loading", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {
                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();
                Toast.makeText(ReportActivity.this, httpResponseMsg, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            protected String doInBackground(String... params) {
                hashMap.put("TaskId", params[0]);
                hashMap.put("TaskTitle", params[1]);
                hashMap.put("Institution", params[2]);
                hashMap.put("Participants", params[3]);
                hashMap.put("ProductType", params[4]);
                hashMap.put("ProductSold", params[5]);
                hashMap.put("ReportRemark", params[6]);
                hashMap.put("ReportPhoto", params[7]);

                finalResult = httpParse.postRequest(hashMap, HttpURL);
                return finalResult;
            }
        }

        insertReportClass insertReportClass = new insertReportClass();
        insertReportClass.execute(S_taskId, S_taskTitle, S_institution, S_participants, S_product_type, S_product_sold, S_remark, S_photo);
    }

    private void captureImage() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       fileUri = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, 100);

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
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                //field_image.setImageURI(fileUri);
                Uri tempUri = Util.getImageUri(getApplicationContext(), photo);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                File finalFile = new File(Util.getRealPathFromURI(cxt,tempUri));
                if(!reportImages.contains(finalFile.getAbsolutePath())){
                    reportImages.add(finalFile.getAbsolutePath());
                    loadReportImages(reportImages);
                }
                field_image.setImageURI(data.getData());
                fab_remove_photo.setVisibility(View.VISIBLE);


            }
        }
        switch (requestCode){
            case Util.PICK_VIDEO:
                Uri videoUri = data.getData();
                Intent i = new Intent(this, ActivityVideoTrimmer.class);
                i.putExtra("video",videoUri.toString());
              //  i.putExtra("video",videoUri);
                startActivityForResult(i,TRIM_VIDEO);
               // mVideoView.setVideoURI(videoUri);
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

    String videoPath;
    private static final int TRIM_VIDEO = 10;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_photo:
                captureImage();
                break;
            case R.id.fab_send:
                mReportPhotoHolder = fileUri.getPath();


                if (checkEditText) {
                    insertReport(task_idHolder, mTaskTitleHolder, institutionHolder, participantsHolder, productTypeHolder, productSoldHolder, mReportRemarkHolder, mReportPhotoHolder);

                } else {
                    Toast.makeText(ReportActivity.this, "Please fill the form completely", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fab_video:
                Util.requestPermission(ReportActivity.this,Util.PICK_VIDEO);
                break;
        }


    }

    private void loadReportImages(final ArrayList<String> images){



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


ArrayList<String> reportImages = new ArrayList<>();

}









