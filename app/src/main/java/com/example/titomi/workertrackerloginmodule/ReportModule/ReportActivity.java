package com.example.titomi.workertrackerloginmodule.ReportModule;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.HttpParse;
import com.example.titomi.workertrackerloginmodule.R;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ReportActivity extends AppCompatActivity {

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final String IMAGE_DIRECTORY_NAME = "Hello Camera";
    FloatingActionButton fab_photo, fab_record, fab_send, fab_remove_photo;
    FloatingActionMenu floatingActionMenu;
    EditText mReportRemark, report_institution, product_sold, participants, mTaskTitle, report_state, report_town, contact_person, message, mum_presents, classes_sampled, report_total, report_demo, school_type, sex;
    TextView report_date;
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

    public void checkEditTextIsEmptyOrNot() {
        mReportRemarkHolder = mReportRemark.getText().toString();
        institutionHolder = report_institution.getText().toString();
        productSoldHolder = product_sold.getText().toString();
        participantsHolder = participants.getText().toString();
        mTaskTitleHolder = mTaskTitle.getText().toString();
//        mReportPhotoHolder = fileUri;

        if (TextUtils.isEmpty(mReportRemarkHolder) || TextUtils.isEmpty(institutionHolder) || TextUtils.isEmpty(productSoldHolder) || TextUtils.isEmpty(participantsHolder) || TextUtils.isEmpty(mTaskTitleHolder)) {
            checkEditText = false;
        } else {
            checkEditText = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        floatingActionMenu = findViewById(R.id.floatingActionMenu);
      /*  classes_sampled = findViewById(R.id.classes_sampled);
        report_total = findViewById(R.id.report_total);
        product_sold = findViewById(R.id.product_sold);
        report_demo = findViewById(R.id.report_demo);
        school_type = findViewById(R.id.school_type);
        sex = findViewById(R.id.sex);
        report_date = findViewById(R.id.report_date);
        mum_presents = findViewById(R.id.mum_presents);
        report_institution = findViewById(R.id.report_institution);
        message = findViewById(R.id.message);
        report_town = findViewById(R.id.report_town);
        report_state = findViewById(R.id.report_state);
        report_town = findViewById(R.id.report_town);
        contact_person = findViewById(R.id.contact_person);
        fab_photo = findViewById(R.id.fab_photo);
        fab_record = findViewById(R.id.fab_record);
        fab_send = findViewById(R.id.fab_send);
        fab_remove_photo = findViewById(R.id.fab_remove_photo);
        field_image = findViewById(R.id.field_image);
*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            fab_photo.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        fab_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        fab_remove_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "CameraDemo");

                if (mediaStorageDir.exists()) {
                    mediaStorageDir.delete();
                }
                field_image.setImageURI(null);
                fab_remove_photo.setVisibility(View.GONE);
            }
        });

        fab_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReportPhotoHolder = fileUri.getPath();
                checkEditTextIsEmptyOrNot();

                if (checkEditText) {
                    insertReport(task_idHolder, mTaskTitleHolder, institutionHolder, participantsHolder, productTypeHolder, productSoldHolder, mReportRemarkHolder, mReportPhotoHolder);

                } else {
                    Toast.makeText(ReportActivity.this, "Please fill the form completely", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "FieldMonitor Images");
        imagesFolder.mkdirs();
        File image = new File(imagesFolder, "image_001.jpg");
        fileUri = Uri.fromFile(image);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);*/
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
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                field_image.setImageURI(fileUri);
                fab_remove_photo.setVisibility(View.VISIBLE);
            }
        }
    }
}


