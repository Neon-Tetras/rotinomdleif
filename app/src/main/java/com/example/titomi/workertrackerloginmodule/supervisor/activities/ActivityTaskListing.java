package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.report_module.ReportActivity;
import com.example.titomi.workertrackerloginmodule.supervisor.Entity;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.DrawableManager;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by NeonTetras on 24-Feb-18.
 */

public class ActivityTaskListing extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemLongClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static Context cxt;
    private static ListView taskListView;
    private static TextView noTaskNotif;
    private static SwipeRefreshLayout swipeRefreshLayout;
    ProgressDialog pg;
    float results[] = new float[3];
    int alertType = 0;
    LatLng latLng;
    private CircleImageView userImage;
    private User loggedInUser;
    private Task selectedTask;
    private TextView clockInText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_layout);
        cxt = this;
        initComponents();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            loggedInUser = (User)extras.getSerializable(getString(R.string.loggedInUser));
        }

//        if(loggedInUser != null && loggedInUser.getRoleId() != User.SUPERVISOR){
//            findViewById(R.id.newTaskButton).setVisibility(View.GONE);
//        }
        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        checkForLocationRequest();
        checkForLocationSettings();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponents(){
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        noTaskNotif = findViewById(R.id.noTaskNotif);
        taskListView = findViewById(R.id.taskList);
        swipeRefreshLayout.setOnRefreshListener(this);
        findViewById(R.id.newTaskButton).setOnClickListener(this);
        taskListView.setOnItemLongClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
       /* if (googleApiClient != null) {
            googleApiClient.connect();
        }*/
    }

    @Override
    protected void onStop() {
      //  googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (result != ConnectionResult.SUCCESS && result != ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            Toast.makeText(this, "This Application needs Google Api client to run.", Toast.LENGTH_SHORT).show();
            return;
        }
        callCurrentLocation(null);
        loadTasks();
    }

    public void callLastKnownLocation(View view) {
        try {
            if (
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                requestPermissions(REQUEST_PERMISSIONS_LAST_LOCATION_REQUEST_CODE);
                return;
            }

            getLastLocation();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    public void callCurrentLocation(View view) {
        try {
            if (
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                requestPermissions(REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE);
                return;
            }

            mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    currentLocation = (Location) locationResult.getLastLocation();
                mLastLocation = currentLocation;
                    String result = "Current Location Latitude is " +
                            currentLocation.getLatitude() + "\n" +
                            "Current location Longitude is " + currentLocation.getLongitude();

                  //  resultTextView.setText(result);
                }
            }, null);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void checkForLocationRequest(){
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(MIN_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }



    //Check for location settings.
    public void checkForLocationSettings() {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.addLocationRequest(locationRequest);
            SettingsClient settingsClient = LocationServices.getSettingsClient(cxt);

            settingsClient.checkLocationSettings(builder.build())
                    .addOnSuccessListener(ActivityTaskListing.this, new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            //Setting is success...
                           // Toast.makeText(cxt, "Enabled the Location successfully. Now you can press the buttons..", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(ActivityTaskListing.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                    try {
                                        // Show the dialog by calling startResolutionForResult(), and check the
                                        // result in onActivityResult().
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult(ActivityTaskListing.this, REQUEST_PERMISSIONS_LOCATION_SETTINGS_REQUEST_CODE);
                                    } catch (IntentSender.SendIntentException sie) {
                                        sie.printStackTrace();
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                   // Toast.makeText(cxt, "Setting change is not available.Try in another device.", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {

        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();


                            String result = "Last known Location Latitude is " +
                                    mLastLocation.getLatitude() + "\n" +
                                    "Last known longitude Longitude is " + mLastLocation.getLongitude();

                            //resultTextView.setText(result);
                        } else {
                            showSnackbar("No Last known location found. Try current location..!");
                        }
                    }
                });
    }

    private void startLocationPermissionRequest(int requestCode) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
    }

    private void requestPermissions(final int requestCode) {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            showSnackbar("Permission is must to find the location", "Ok",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest(requestCode);
                        }
                    });

        } else {
            startLocationPermissionRequest(requestCode);
        }
    }
    private void showSnackbar(final String mainTextString, final String actionString,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                mainTextString,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(actionString, listener).show();
    }



    @Override
    public void onConnected(Bundle bundle) {
/*

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
             lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            double lat = lastLocation.getLatitude(), lon = lastLocation.getLongitude();
            String units = "imperial";
          //  String url = String.format("http://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&units=%s&appid=%s",
                    //lat, lon, units, getString(R.string.google_api_key);
            Toast.makeText(cxt,""+lat+"\t"+lon,Toast.LENGTH_LONG).show();
            //new GetWeatherTask(textView).execute(url);
        }else{
                ActivityCompat.requestPermissions(((Activity)cxt),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
*/

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(loggedInUser != null) {
            outState.putSerializable(getString(R.string.loggedInUser), loggedInUser);
        }
        if(selectedTask != null){
            outState.putSerializable(getString(R.string.task),selectedTask);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null) {
            loggedInUser = (User) savedInstanceState.getSerializable(getString(R.string.loggedInUser));
        }

        if(savedInstanceState.getSerializable(getString(R.string.task)) != null){
            selectedTask = (Task)savedInstanceState.getSerializable(getString(R.string.task));
        }
    }

    @Override
    public void onRefresh() {
        loadTasks();
    }

    private void loadTasks() {

        String url = "";
        switch (loggedInUser.getRoleId()){
            case User.SUPERVISOR:
                url = getString(R.string.api_url)+getString(R.string.task_url)+"?view=supervisor&key="+getString(R.string.field_worker_api_key)+"&id="+loggedInUser.getId();
                break;
            case User.NURSE:
                url = getString(R.string.api_url)+getString(R.string.task_url)+"?view=worker&key="+getString(R.string.field_worker_api_key)+"&id="+loggedInUser.getId();
                break;
        }
        new AssignedTaskNetwork().execute(url);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.newTaskButton:
                startActivity(new Intent(this,ActivityAssignTask.class).putExtra(getString(R.string.loggedInUser),loggedInUser));
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        ListAdapter listAdapter = taskListView.getAdapter();

        selectedTask = (Task)listAdapter.getItem(i);

        final AlertDialog alertDialog = new AlertDialog.Builder(cxt).create();
        View promptView = View.inflate(cxt,R.layout.task_listing_long_click_menu_layout,null);
         alertDialog.setView(promptView);

         TextView viewReport  = promptView.findViewById(R.id.viewReportText);
         final TextView deleteTask = promptView.findViewById(R.id.deleteTask);
         //TextView approveReport = promptView.findViewById(R.id.approveReport);
         TextView editTask = promptView.findViewById(R.id.editTask);
         clockInText = promptView.findViewById(R.id.clock_in);


         /*
         * if user has uploaded images, then the selectedTask has been done
         *So the selectedTask cannot be deleted or edited*/
         switch (selectedTask.getStatus()){
             case Task.PENDING:

                 //editTask.setVisibility(View.VISIBLE);
                 deleteTask.setVisibility(View.VISIBLE);
                 break;

             case Task.PENDING_APPROVAL:
             case Task.COMPLETED:
                 viewReport.setVisibility(View.VISIBLE);
                 break;
         }

        if(loggedInUser.getRoleId() ==
                User.SUPERVISOR &&
                selectedTask.getWorker().getId() == loggedInUser.getId()){
            editTask.setVisibility(View.GONE);
            deleteTask.setVisibility(View.GONE);
            switch (selectedTask.getStatus()){
                case Task.PENDING:
                clockInText.setVisibility(View.VISIBLE);
                clockInText.setTag(getString(R.string.clockIn));
                break;
                case Task.ONGOING:
                    clockInText.setVisibility(View.VISIBLE);
                    clockInText.setText(getString(R.string.writeReport));
                    clockInText.setTag(getString(R.string.clockOut));
            }
        }



            if (clockInText.getTag().toString().equalsIgnoreCase(getString(R.string.clockIn))) {
                alertType = 0;
            } else {
                alertType = 1;
            }


        clockInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    alertDialog.dismiss();


                if (ContextCompat.checkSelfPermission(cxt,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    final AlertDialog alertDialog = new AlertDialog.Builder(cxt).create();
                    if (isWithinClockInRange(selectedTask.getLatitude(), selectedTask.getLongitude(), mLastLocation.getLatitude(), mLastLocation.getLongitude())) {
                        if (clockInText == null) {
                            return;
                        }

                        if (clockInText.getTag().toString().equalsIgnoreCase(getString(R.string.clockOut))) {
                            Intent i = new Intent(cxt, ReportActivity.class);
                            i.putExtra("task", selectedTask);
                            i.putExtra(getString(R.string.loggedInUser), loggedInUser);
                            i.putExtra("stop_lat", "" + mLastLocation.getLatitude());
                            i.putExtra("stop_long", "" + mLastLocation.getLongitude());


                            startActivity(i);
                            return;
                        }

                        if(isClockedInOnATask){
                            Toast.makeText(cxt,
                                    "Please complete the ongoing task first before beginning another",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                        Date date = new Date(mLastLocation.getTime());
                        SimpleDateFormat dtf = new SimpleDateFormat("yyyy/M/dd HH:mm:ss");
                        String formatedTime = null;
                        try {
                            formatedTime = URLEncoder.encode(dtf.format(date), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String uri = null;

                        uri = String.format("%s%s?key=%s",
                                getString(R.string.api_url),
                                getString(R.string.clockInUrl),
                                getString(R.string.field_worker_api_key)) +
                                "&start_longitude=" + mLastLocation.getLongitude() +
                                "&start_latitude=" +
                                mLastLocation.getLatitude() + "&user_id=" +
                                loggedInUser.getId() + "&task_id=" +
                                selectedTask.getId() + "&start_time=" +
                                formatedTime;


                        StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, response -> {
                            if (response == null) return;

                            try {
                                JSONObject obj = new JSONObject(response);
                                if (obj.getInt("statusCode") == Entity.STATUS_OK) {

                                    alertDialog.setMessage("Clock-in successful");
                                    alertDialog.show();

                                    loadTasks();

                                } else {
                                    Toast.makeText(cxt, obj.getString("message"), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                System.err.println(response);
                            }
                        }, error -> {

                        });
                        queue.add(stringRequest);

                    } else {

                        //Report clock-in mismatch
                        new android.os.AsyncTask<String, Void, String>() {
                            @Override
                            protected String doInBackground(String... strings) {
                                return Network.backgroundTask(null, strings[0]);
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);
                            }
                        }.execute(getString(R.string.api_url) + getString(R.string.alert_api) + "?key=" + getString(R.string.field_worker_api_key) + "&task_id=" + selectedTask.getId() + "&longitude=" + mLastLocation.getLongitude() + "&latitude=" + mLastLocation.getLatitude() + "&alert_type=" + alertType);

                        alertDialog.setMessage("Clock-in/Clock out failed!\nPlease report at your place of assignment to clock-in/Clock-out");
                        alertDialog.show();


                }
                }
                else{
                    ActivityCompat.requestPermissions(((Activity)cxt),
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                }

                }

        });
         viewReport.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent i = new Intent(cxt,ActivityViewReport.class);
                 i.putExtra(getString(R.string.loggedInUser),loggedInUser);
                 i.putExtra("task", selectedTask);
                 startActivity(i);
                 alertDialog.dismiss();
             }
         });

        editTask.setOnClickListener(view1 -> {
            Intent i1 = new Intent(cxt,ActivityAssignTask.class);
            i1.putExtra("task", selectedTask);
            i1.putExtra(getString(R.string.loggedInUser),loggedInUser);
            startActivity(i1);
            alertDialog.dismiss();
        });
         deleteTask.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 final AlertDialog confirm = new AlertDialog.Builder(cxt).create();
                  confirm.setButton(DialogInterface.BUTTON_NEGATIVE, "No", (dialogInterface, i12) -> confirm.dismiss());
                  confirm.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", (dialogInterface, i13) -> deleteTask());

                  confirm.setMessage("Are you sure you want to delete this Task?");
                  confirm.show();

             }

             @SuppressLint("StaticFieldLeak")
             private void deleteTask() {
                new AssignedTaskNetwork(){
                    ProgressDialog progressDialog ;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog =new ProgressDialog(cxt);
                        progressDialog.setMessage("Deleting Task.\nPlease wait...");
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        //super.onPostExecute(s);
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        if(s == null) return;
                        try {
                            JSONObject obj = new JSONObject(s);
                            if(obj.getInt("statusCode") == Task.STATUS_OK){
                                Toast.makeText(cxt,obj.getString("message"),Toast.LENGTH_LONG).show();
                                alertDialog.dismiss();
                                loadTasks();
                            }else{
                                Toast.makeText(cxt,obj.getString("message"),Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.err.println(s);
                        }

                    }
                }.execute(getString(R.string.api_url)+getString(R.string.delete_task_url)+"?key="+getString(R.string.field_worker_api_key)+"&id="+ selectedTask.getId());
             }
         });

        if(loggedInUser.getRoleId() == User.SUPERVISOR && selectedTask.getStatus() == Task.ONGOING) {
           return false;
        }

      //  if(alertDialog.) {
            alertDialog.show();
      //  }

        return false;
    }

    private void getLocation(final Task task) {

        final String clockIn = getString(R.string.clockIn);
        final String clockOut = getString(R.string.clockOut);
        if (clockInText.getTag().toString().equalsIgnoreCase(getString(R.string.clockIn))) {
            alertType = 0;
        } else {
            alertType = 1;
        }
        final LocationManager mLocationManager;

        try {

            Criteria criteria = new Criteria();

            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(true);
            criteria.setCostAllowed(true);
            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
            criteria.setVerticalAccuracy(Criteria.ACCURACY_MEDIUM);
            criteria.setBearingAccuracy(Criteria.ACCURACY_LOW);
            criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);

            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


            if (mLocationManager != null) {
                final String bestProvider = mLocationManager.getBestProvider(criteria, true);
                //Toast.makeText(cxt,bestProvider,Toast.LENGTH_LONG).show();
                final LocationListener locationListener = new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        if (pg.isShowing()) {
                            pg.dismiss();
                        }
                        final AlertDialog alertDialog = new AlertDialog.Builder(cxt).create();
                        if (isWithinClockInRange(task.getLatitude(), task.getLongitude(), latLng.latitude, latLng.longitude)) {
                            if (clockInText == null) {
                                return;
                            }

                            if (clockInText.getTag().toString().equalsIgnoreCase(getString(R.string.clockOut))) {
                                Intent i = new Intent(cxt, ReportActivity.class);
                                i.putExtra("task", selectedTask);
                                i.putExtra(getString(R.string.loggedInUser), loggedInUser);
                                i.putExtra("stop_lat", "" + latLng.latitude);
                                i.putExtra("stop_long", "" + latLng.longitude);


                                startActivity(i);
                                return;
                            }
                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            Date date = new Date(location.getTime());
                            SimpleDateFormat dtf = new SimpleDateFormat("yyyy/M/dd HH:mm:ss");
                            String formatedTime = null;
                            try {
                                formatedTime = URLEncoder.encode(dtf.format(date), "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            String uri = null;

                            uri = String.format("%s%s?key=%s",
                                    getString(R.string.api_url),
                                    getString(R.string.clockInUrl),
                                    getString(R.string.field_worker_api_key)) +
                                    "&start_longitude=" + latLng.longitude +
                                    "&start_latitude=" +
                                    latLng.latitude + "&user_id=" +
                                    loggedInUser.getId() + "&task_id=" +
                                    task.getId() + "&start_time=" +
                                    formatedTime;


                            StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    if (response == null) return;

                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (obj.getInt("statusCode") == Entity.STATUS_OK) {

                                            alertDialog.setMessage("Clock-in successful");
                                            alertDialog.show();

                                            loadTasks();

                                        } else {
                                            Toast.makeText(cxt, obj.getString("message"), Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        System.err.println(response);
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });
                            queue.add(stringRequest);

                        } else {

                            //Report clock-in mismatch
                            new android.os.AsyncTask<String, Void, String>() {
                                @Override
                                protected String doInBackground(String... strings) {
                                    return Network.backgroundTask(null, strings[0]);
                                }

                                @Override
                                protected void onPostExecute(String s) {
                                    super.onPostExecute(s);
                                }
                            }.execute(getString(R.string.api_url) + getString(R.string.alert_api) + "?key=" + getString(R.string.field_worker_api_key) + "&task_id=" + task.getId() + "&longitude=" + latLng.longitude + "&latitude=" + latLng.latitude + "&alert_type=" + alertType);

                            alertDialog.setMessage("Clock-in/Clock out failed!\nPlease report at your place of assignment to clock-in/Clock-out");
                            alertDialog.show();

                            final AlertDialog navAlertDialog = new AlertDialog.Builder(cxt).create();
                            View navPromptView = View.inflate(cxt, R.layout.task_listing_long_click_nav_menu, null);
                            alertDialog.setView(navPromptView);

                            TextView viewNav = navPromptView.findViewById(R.id.viewNavText);
                            //TextView approveReport = promptView.findViewById(R.id.approveReport);
//                            TextView editTask = navPromptView.findViewById(R.id.editTask);
//                            clockInText = navPromptView.findViewById(R.id.clock_in);


                        }


                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };

                mLocationManager.requestSingleUpdate(bestProvider, locationListener, null);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mLocationManager.removeUpdates(locationListener);

                        if (latLng == null) {
                            Toast.makeText(cxt, "Sorry we could not detect your location.\nPlease try again", Toast.LENGTH_LONG).show();
                        }
                        pg.dismiss();
                    }
                }, 30000);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            pg.dismiss();
        }

    }

    public boolean isWithinClockInRange(Double taskLat, Double taskLng, Double nurseLat, Double nurseLng) {
        Location.distanceBetween(taskLat, taskLng, nurseLat, nurseLng, results);
        return results[0] <= 200;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    if(mLastLocation != null) {

    double lat = mLastLocation.getLatitude(), lon = mLastLocation.getLongitude();


   // Toast.makeText(cxt, "" + lat + "\t" + lon, Toast.LENGTH_LONG).show();
    }

        } else {
            Toast.makeText(cxt, "You need to grant location permission", Toast.LENGTH_LONG).show();
            if (pg.isShowing()) {
                pg.dismiss();
            }
        }
        if (requestCode == REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callCurrentLocation(null);
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private static class AssignedTaskNetwork extends android.os.AsyncTask<String,Void,String>{


        static ArrayList<Task> taskList = new ArrayList<>();
        static ArrayAdapter<Task> taskArrayAdapter;

        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null,strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //  progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if(s == null) {
                noTaskNotif.setVisibility(View.INVISIBLE);
                return;
            }

            try {
                JSONArray jsonArray = new JSONArray(s);
                taskList.clear();

                if(jsonArray.length() > 0){
                    noTaskNotif.setVisibility(View.INVISIBLE);
                }else{
                    noTaskNotif.setVisibility(View.GONE);
                }
                for(int i = 0; i<jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    JSONObject supervisorObj = obj.getJSONObject("supervisor");
                    JSONObject workerObj = obj.getJSONObject("worker");
                    User supervisor = new User();
                    supervisor.setUserLevel(supervisorObj.getInt("roleId"));
                    supervisor.setUserLevelText(supervisorObj.getString("role"));
                    supervisor.setFeaturedImage(supervisorObj.getString("photo"));
                    supervisor.setName(String.format("%s %s",supervisorObj.getString("first_name"),supervisorObj.getString("last_name")));
                    supervisor.setEmail(supervisorObj.getString("email"));
                    supervisor.setId(supervisorObj.getInt("id"));
                    User worker = new User();
                    worker.setUserLevel(workerObj.getInt("roleId"));
                    worker.setUserLevelText(workerObj.getString("role"));
                    worker.setFeaturedImage(workerObj.getString("photo"));
                    worker.setName(String.format("%s %s",workerObj.getString("first_name"),supervisorObj.getString("last_name")));
                    worker.setEmail(workerObj.getString("email"));
                    worker.setId(workerObj.getInt("id"));
                    SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//                    SimpleDateFormat dtf3 = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
                    //  DateFormat dtf = DateFormat.getDateTimeInstance();
                    SimpleDateFormat dtf2 = new SimpleDateFormat("yyyy/MM/dd");
                    Date dateGiven = dtf2.parse(obj.getString("dateGiven"));
                    Date stopTime = dtf.parse(obj.getString("stopTime"));
                    Date startTime = dtf.parse(obj.getString("startTime"));
                    Date dateDelivered = dtf.parse(obj.getString("dateDelivered"));
                    SimpleDateFormat tf = new SimpleDateFormat("H:m:s");
                    String timeGiven = obj.getString("timeGiven");


                    Task task = new Task(obj.getInt("id"),supervisor,worker,dateGiven,dateDelivered,
                            obj.getString("name"),obj.getString("description"),
                            timeGiven,obj.getString("workType"),obj.getString("contactName"),
                            obj.getString("contactNumber"),
                            obj.getString("institution_name"),
                            obj.getString("location"),
                            obj.getString("lga"),
                            obj.getString("state"),
                            obj.getString("address"),
                            obj.getString("sales"),
                            obj.getString("images"),
                            0,
                            obj.getInt("inventoryBalance"),
                            obj.getInt("quantitySold"),
                            obj.getInt("participants"),
                            obj.getInt("status"),obj.getInt("productId"));
                    task.setLatitude(obj.getDouble("latitude"));
                    task.setLongitude(obj.getDouble("longitude"));
                    /*if(obj.getDouble("startLongitude") != 0.0 && obj.getDouble("startLatitude") != 0.0 &&
                            obj.getDouble("stopLongitude") != 0.0 && obj.getDouble("stopLatitude") != 0.0)*/
                    task.setStartLatitude(obj.getDouble("startLatitude"));
                    task.setStopLatitude(obj.getDouble("stopLatitude"));
                    task.setStartLongitude(obj.getDouble("startLongitude"));
                    task.setStopLongitude(obj.getDouble("stopLongitude"));
                    task.setWorkerComment(obj.getString("workerComment"));
                    task.setStartTime(startTime);
                    task.setStopTime(stopTime);
                    task.setVideo(obj.getString("video"));


                    if(task.getStatus() == Task.ONGOING){
                        isClockedInOnATask = true;
                    }
                    taskList.add(task);
                }

                taskArrayAdapter = new ArrayAdapter<Task>(cxt,R.layout.report_single_item_layout,taskList){



                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        LayoutInflater inflater = (LayoutInflater)cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                      View myView  = View.inflate(cxt,R.layout.task_listing_single_item_layout,null);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0,10,0,5);

                        myView.setLayoutParams(layoutParams);

                        final Task task = taskList.get(position);

                        ImageView userImage = myView.findViewById(R.id.userImage);
                        TextView usernameText = myView.findViewById(R.id.username);
                        TextView dateAssignedText = myView.findViewById(R.id.dateTimeText);
                        TextView taskTitle = myView.findViewById(R.id.taskTitleText);
                        TextView statusText =myView.findViewById(R.id.taskStatusText);
                        TextView taskType = myView.findViewById(R.id.taskTypeText);
                        TextView taskDescription = myView.findViewById(R.id.taskDescriptionText);
                        TextView institutionName = myView.findViewById(R.id.institutionText);
                        TextView locationText = myView.findViewById(R.id.locationText);
                        TextView contactPersonText =  myView.findViewById(R.id.contactPersonText);
                        TextView quantityGivenText = myView.findViewById(R.id.quantityGivenText);
                        TextView getDirections = myView.findViewById(R.id.getDirection);


                          institutionName.setText(task.getInstitution_name());
                        taskType.setText(String.format("(%s)",task.getWorkType()));
                        taskDescription.setText(task.getDescription());
                        locationText.setText(String.format("%s, %s, %s",task.getAddress(),task.getLga(),task.getState()).replaceAll("\n","").replaceAll("\r",""));
                        contactPersonText.setText(String.format("%s (%s)",task.getContactName(),task.getContactNumber()));
                        quantityGivenText.setText(NumberFormat.getInstance(Locale.getDefault()).format(task.getQuantity()));


                        /*
                        * If the worker has not uploaded images, then the selectedTask is pending
                        * if images have been uploaded then status is pending approval
                        * if the supervisor has approved the selectedTask done, then the status is completed */


                        String status = cxt.getResources().getStringArray(R.array.task_status)[task.getStatus()];
                        statusText.setText(status);
                        DrawableManager drm = new DrawableManager();
                        drm.fetchDrawableOnThread(cxt.getString(R.string.server_url)
                                +task.getWorker().getFeaturedImage(),userImage);
                        usernameText.setText(Util.toSentenceCase(String.format("%s ( %s )",
                                task.getWorker().getName(),
                                task.getWorker().getUserLevelText())));
                        // SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
                        DateFormat dtf = DateFormat.getDateInstance();
                        DateFormat tf = DateFormat.getTimeInstance();

                        dateAssignedText.setText(String.format("%s, %s",dtf.format(task.getDateGiven()),task.getTimeGiven()));
                        taskTitle.setText(task.getName());


                        getDirections.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", task.getLatitude(), task.getLongitude());
                                try {
                                    String location = URLEncoder.encode(task.getLocation(),"UTF-8");

                                Uri gmmIntentUri = Uri.parse("google.navigation:q="+location);
                                Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                intent.setPackage("com.google.android.apps.maps");
                                ActivityTaskListing.cxt.startActivity(intent);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        return myView;
                    }
                };

                taskListView.setAdapter(taskArrayAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
                System.err.println(s);
            } catch (ParseException e) {

                e.printStackTrace();
            }
        }
    }

    private void showSnackbar(String text){
        Snackbar snackbar = Snackbar.make(findViewById(R.id.parent),text,Snackbar.LENGTH_LONG);
        snackbar.show();

    }
    private GoogleApiClient googleApiClient;
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_LOCATION_SETTINGS_REQUEST_CODE = 33;
    private static final int REQUEST_PERMISSIONS_LAST_LOCATION_REQUEST_CODE = 34;
    private static final int REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE = 35;

    private FusedLocationProviderClient mFusedLocationClient;

    protected static long MIN_UPDATE_INTERVAL = 30 * 1000; // 1  minute is the minimum Android recommends, but we use 30 seconds

    protected Location mLastLocation;

    private TextView resultTextView;
    LocationRequest locationRequest;
    Location lastLocation = null;
    Location currentLocation = null;
    private static boolean isClockedInOnATask;
}
