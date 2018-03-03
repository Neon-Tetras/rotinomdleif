package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.example.titomi.workertrackerloginmodule.ReportModule.ReportActivity;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.DrawableManager;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;
import com.google.android.gms.maps.model.LatLng;

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
        SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemLongClickListener, LocationListener {

    static Context cxt;
    private static ListView taskListView;
    private static TextView noTaskNotif;
    private static SwipeRefreshLayout swipeRefreshLayout;
    LocationManager mLocationManager;
    Location mLocation;

    float[] results = new float[3];
    private CircleImageView userImage;
    private User loggedInUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_layout);
        cxt = this;
        initComponents();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            loggedInUser = (User) extras.getSerializable(getString(R.string.loggedInUser));
        }

        if (loggedInUser != null && loggedInUser.getRoleId() != User.SUPERVISOR) {
            findViewById(R.id.newTaskButton).setVisibility(View.GONE);
        }

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        noTaskNotif = findViewById(R.id.noTaskNotif);
        taskListView = findViewById(R.id.taskList);
        swipeRefreshLayout.setOnRefreshListener(this);
        findViewById(R.id.newTaskButton).setOnClickListener(this);
        taskListView.setOnItemLongClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onRefresh() {
        loadTasks();
    }

    private void loadTasks() {
        new AssignedTaskNetwork().execute(getString(R.string.api_url) + getString(R.string.task_url) + "?view=supervisor&key=" + getString(R.string.field_worker_api_key) + "&id=" + loggedInUser.getId());

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.newTaskButton:
                startActivity(new Intent(this, ActivityAssignTask.class).putExtra(getString(R.string.loggedInUser), loggedInUser));
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        ListAdapter listAdapter = taskListView.getAdapter();

        final Task task = (Task) listAdapter.getItem(i);

        final AlertDialog alertDialog = new AlertDialog.Builder(cxt).create();
        View promptView = View.inflate(cxt, R.layout.task_listing_long_click_menu_layout, null);
        alertDialog.setView(promptView);

        TextView viewReport = promptView.findViewById(R.id.viewReportText);
        final TextView deleteTask = promptView.findViewById(R.id.deleteTask);
        //TextView approveReport = promptView.findViewById(R.id.approveReport);
        TextView editTask = promptView.findViewById(R.id.editTask);
        TextView clockInText = promptView.findViewById(R.id.clock_in);


         /*
         * if user has uploaded images, then the task has been done
         *So the task cannot be deleted or edited*/
        switch (task.getStatus()) {
            case Task.PENDING:

                editTask.setVisibility(View.VISIBLE);
                deleteTask.setVisibility(View.VISIBLE);
                break;

            case Task.PENDING_APPROVAL:
            case Task.COMPLETED:
                viewReport.setVisibility(View.VISIBLE);
                break;
        }

        if (loggedInUser.getRoleId() != User.SUPERVISOR) {
            editTask.setVisibility(View.GONE);
            deleteTask.setVisibility(View.GONE);
            switch (task.getStatus()) {
                case Task.PENDING:
                    clockInText.setVisibility(View.VISIBLE);
                    clockInText.setTag(getString(R.string.clockIn));
                    break;
                case Task.ONGOING:
                    clockInText.setVisibility(View.VISIBLE);
                    clockInText.setText(getString(R.string.clockOut));
                    clockInText.setTag(getString(R.string.clockOut));
            }
        }


        clockInText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: call loadTask(); on clockin and clockout success
                if (v.getTag().toString().equals(getString(R.string.clockIn))) {
                    alertDialog.dismiss();
                    getLocation(task);
                    //TODO: implement clockin action here;
                    //TODO: clocking only when geofencing works
                    //TODO:show clockin error when geofencing fails
                    //TODO: send alert when geofencing fails


                } else if (v.getTag().toString().equals(getString(R.string.clockOut))) {

                    Intent i = new Intent(cxt, ReportActivity.class);
                    i.putExtra("task", task);
                    i.putExtra(getString(R.string.loggedInUser), loggedInUser);
                    startActivity(i);
                    alertDialog.dismiss();
                }
            }
        });
        viewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(cxt, ActivityViewReport.class);
                i.putExtra("task", task);
                startActivity(i);
                alertDialog.dismiss();
            }
        });

        editTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(cxt, ActivityAssignTask.class);
                i.putExtra("task", task);
                startActivity(i);
                alertDialog.dismiss();
            }
        });
        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog confirm = new AlertDialog.Builder(cxt).create();
                confirm.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirm.dismiss();
                    }
                });
                confirm.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteTask();
                    }
                });

                confirm.setMessage("Are you sure you want to delete this task?");
                confirm.show();

            }

            @SuppressLint("StaticFieldLeak")
            private void deleteTask() {
                new AssignedTaskNetwork() {
                    ProgressDialog progressDialog;

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        progressDialog = new ProgressDialog(cxt);
                        progressDialog.setMessage("Deleting task.\nPlease wait...");
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        //super.onPostExecute(s);
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        if (s == null) return;
                        try {
                            JSONObject obj = new JSONObject(s);
                            if (obj.getInt("statusCode") == Task.STATUS_OK) {
                                Toast.makeText(cxt, obj.getString("message"), Toast.LENGTH_LONG).show();
                                alertDialog.dismiss();
                                loadTasks();
                            } else {
                                Toast.makeText(cxt, obj.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.err.println(s);
                        }

                    }
                }.execute(getString(R.string.api_url) + getString(R.string.delete_task_url) + "?key=" + getString(R.string.field_worker_api_key) + "&id=" + task.getId());
            }
        });


        if (task.getStatus() != Task.ONGOING) {
            alertDialog.show();
        }
        return false;
    }

    private void getLocation(final Task task) {
        final ProgressDialog pg = new ProgressDialog(this);
        pg.setCancelable(false);
        pg.setMessage(getString(R.string.please_wait));
        pg.show();
        try {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (mLocationManager != null) {
                mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                        if (isWithinClockInRange(task.getLatitude(), task.getLongitude(), latLng.latitude, latLng.longitude)) {

                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                            Date date = new Date(location.getTime());
                            SimpleDateFormat dtf = new SimpleDateFormat("HH:mm:ss");
                            String formatedTime = dtf.format(date);
                            String uri = null;
                            try {
                                uri = URLEncoder.encode(String.format("%s%s?key=%s", getString(R.string.server_url), getString(R.string.clockInUrl), getString(R.string.field_worker_api_key)) + "&start_longitude=" + latLng.longitude + "&start_latitude=" + latLng.latitude + "&user_id=" + loggedInUser.getId() + "&task_id=" + task.getId() + "&start_time=" + formatedTime, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            StringRequest stringRequest = new StringRequest(Request.Method.GET, uri, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });
                            queue.add(stringRequest);

                        }

                        Toast.makeText(cxt, "" + latLng, Toast.LENGTH_SHORT).show();

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
                }, null);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

    }

    public boolean isWithinClockInRange(Double taskLat, Double taskLng, Double nurseLat, Double nurseLng) {
        Location.distanceBetween(taskLat, taskLng, nurseLat, nurseLng, results);
        return results[0] <= 15;
    }

    @Override
    public void onLocationChanged(Location location) {

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


    private static class AssignedTaskNetwork extends android.os.AsyncTask<String, Void, String> {


        static ArrayList<Task> taskList = new ArrayList<>();
        static ArrayAdapter<Task> taskArrayAdapter;

        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null, strings[0]);
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
            if (s == null) {
                noTaskNotif.setVisibility(View.INVISIBLE);
                return;
            }

            try {
                JSONArray jsonArray = new JSONArray(s);
                taskList.clear();

                if (jsonArray.length() > 0) {
                    noTaskNotif.setVisibility(View.INVISIBLE);
                } else {
                    noTaskNotif.setVisibility(View.GONE);
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    JSONObject supervisorObj = obj.getJSONObject("supervisor");
                    JSONObject workerObj = obj.getJSONObject("worker");
                    User supervisor = new User();
                    supervisor.setUserLevel(supervisorObj.getInt("roleId"));
                    supervisor.setUserLevelText(supervisorObj.getString("role"));
                    supervisor.setFeaturedImage(supervisorObj.getString("photo"));
                    supervisor.setName(String.format("%s %s", supervisorObj.getString("first_name"), supervisorObj.getString("last_name")));
                    supervisor.setEmail(supervisorObj.getString("email"));
                    supervisor.setId(supervisorObj.getInt("id"));
                    User worker = new User();
                    worker.setUserLevel(workerObj.getInt("roleId"));
                    worker.setUserLevelText(workerObj.getString("role"));
                    worker.setFeaturedImage(workerObj.getString("photo"));
                    worker.setName(String.format("%s %s", workerObj.getString("first_name"), supervisorObj.getString("last_name")));
                    worker.setEmail(workerObj.getString("email"));
                    worker.setId(workerObj.getInt("id"));
                    SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    //  DateFormat dtf = DateFormat.getDateTimeInstance();
                    Date dateGiven = dtf.parse(obj.getString("dateGiven"));
                    Date dateDelivered = dtf.parse(obj.getString("dateDelivered"));
                    SimpleDateFormat tf = new SimpleDateFormat("H:m:s");
                    String timeGiven = obj.getString("timeGiven");


                    Task task = new Task(obj.getInt("id"), supervisor, worker, dateGiven, dateDelivered,
                            obj.getString("name"), obj.getString("description"),
                            timeGiven, obj.getString("workType"), obj.getString("contactName"),
                            obj.getString("contactNumber"),
                            obj.getString("institution_name"),
                            obj.getString("location"),
                            obj.getString("lga"),
                            obj.getString("state"),
                            obj.getString("address"),
                            obj.getString("sales"),
                            obj.getString("images"),
                            obj.getInt("quantity"),
                            obj.getInt("inventoryBalance"),
                            obj.getInt("quantitySold"),
                            obj.getInt("participants"),
                            obj.getInt("status"));
                    task.setLatitude(obj.getDouble("latitude"));
                    task.setLongitude(obj.getDouble("longitude"));
                    /*if(obj.getDouble("startLongitude") != 0.0 && obj.getDouble("startLatitude") != 0.0 &&
                            obj.getDouble("stopLongitude") != 0.0 && obj.getDouble("stopLatitude") != 0.0)*/
                    task.setStartLatitude(obj.getDouble("startLatitude"));
                    task.setStopLatitude(obj.getDouble("stopLatitude"));
                    task.setStartLongitude(obj.getDouble("startLongitude"));
                    task.setStopLongitude(obj.getDouble("stopLongitude"));


                    taskList.add(task);
                }
                taskArrayAdapter = new ArrayAdapter<Task>(cxt, R.layout.report_single_item_layout, taskList) {


                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        LayoutInflater inflater = (LayoutInflater) cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.task_listing_single_item_layout, null);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 10, 0, 5);

                        convertView.setLayoutParams(layoutParams);

                        Task task = taskList.get(position);

                        ImageView userImage = convertView.findViewById(R.id.userImage);
                        TextView usernameText = convertView.findViewById(R.id.username);
                        TextView dateAssignedText = convertView.findViewById(R.id.dateTimeText);
                        TextView taskTitle = convertView.findViewById(R.id.taskTitleText);
                        TextView statusText = convertView.findViewById(R.id.taskStatusText);
                        TextView taskType = convertView.findViewById(R.id.taskTypeText);
                        TextView taskDescription = convertView.findViewById(R.id.taskDescriptionText);
                        TextView institutionName = convertView.findViewById(R.id.institutionText);
                        TextView locationText = convertView.findViewById(R.id.locationText);
                        TextView contactPersonText = convertView.findViewById(R.id.contactPersonText);
                        TextView quantityGivenText = convertView.findViewById(R.id.quantityGivenText);

                        institutionName.setText(task.getInstitution_name());
                        taskType.setText(String.format("(%s)", task.getWorkType()));
                        taskDescription.setText(task.getDescription());
                        locationText.setText(String.format("%s, %s, %s", task.getAddress(), task.getLga(), task.getState()).replaceAll("\n", "").replaceAll("\r", ""));
                        contactPersonText.setText(String.format("%s (%s)", task.getContactName(), task.getContactNumber()));
                        quantityGivenText.setText(NumberFormat.getInstance(Locale.getDefault()).format(task.getQuantity()));


                        /*
                        * If the worker has not uploaded images, then the task is pending
                        * if images have been uploaded then status is pending approval
                        * if the supervisor has approved the task done, then the status is completed */


                        String status = cxt.getResources().getStringArray(R.array.task_status)[task.getStatus()];
                        statusText.setText(status);
                        DrawableManager drm = new DrawableManager();
                        drm.fetchDrawableOnThread(cxt.getString(R.string.server_url)
                                + task.getWorker().getFeaturedImage(), userImage);
                        usernameText.setText(Util.toSentenceCase(String.format("%s ( %s )",
                                task.getWorker().getName(),
                                task.getWorker().getUserLevelText())));
                        // SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
                        DateFormat dtf = DateFormat.getDateInstance();
                        DateFormat tf = DateFormat.getTimeInstance();

                        dateAssignedText.setText(String.format("%s, %s", dtf.format(task.getDateGiven()), task.getTimeGiven()));
                        taskTitle.setText(task.getName());


                        return convertView;
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

}
