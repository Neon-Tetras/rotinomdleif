package com.example.titomi.workertrackerloginmodule.inventory_module.inventory_sub_menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Inventory;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityInventoryRequest;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class InventoryReportActivity extends AppCompatActivity implements OnChartValueSelectedListener, View.OnClickListener {

    Toolbar toolbar;
    private int itemBalance;
    private int itemQuantitySold;
    private int itemQuantity;

    private static ListView listView;
    private FloatingActionButton actionButton;
    BarChart barInventoryChart;
    private User loggedInUser;
    private static SwipeRefreshLayout swipeRefreshLayout;
    Context cxt;
    ArrayList<BarEntry> yVals = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_report);
        cxt = this;
        barInventoryChart = findViewById(R.id.barChartInvt);
        barInventoryChart.getDescription().setEnabled(false);

        listView = findViewById(R.id.inventoryRequestList);
        barInventoryChart.setOnChartValueSelectedListener(this);

        actionButton = findViewById(R.id.newRequest);
        actionButton.setOnClickListener(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Inventory Report");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            loggedInUser = (User) extras.getSerializable(getString(R.string.loggedInUser));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInventory();
        loadRequests();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(cxt, ActivityInventoryRequest.class)
                .putExtra(getString(R.string.loggedInUser),
                        loggedInUser));
    }

    private class InventoryNetwork extends android.os.AsyncTask<String, Void, String> {

        ArrayList<Task> taskList = new ArrayList<>();

        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null, strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pb.setVisibility(View.VISIBLE);
            //   refreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            pb.setVisibility(View.GONE);

            //  refreshLayout.setRefreshing(false);
            if (s == null) {

                return;
            }

            try {
                JSONArray jsonArray = new JSONArray(s);
                taskList.clear();

                if (jsonArray.length() > 0) {

                } else {
                    // noTaskNotif.setVisibility(View.GONE);
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
                    SimpleDateFormat dtf2 = new SimpleDateFormat("yyyy/MM/dd");
                    Date dateGiven = dtf2.parse(obj.getString("dateGiven"));
//                    Date dateGiven = dtf.parse(obj.getString("dateGiven"));
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
                            obj.getInt("status"), obj.getInt("productId"));
                    task.setLatitude(obj.getDouble("latitude"));
                    task.setLongitude(obj.getDouble("longitude"));
                    task.setWorkerComment(obj.getString("workerComment"));
                    /*if(obj.getDouble("startLongitude") != 0.0 && obj.getDouble("startLatitude") != 0.0 &&
                            obj.getDouble("stopLongitude") != 0.0 && obj.getDouble("stopLatitude") != 0.0)*/
                    task.setStartLatitude(obj.getDouble("startLatitude"));
                    task.setStopLatitude(obj.getDouble("stopLatitude"));
                    task.setStartLongitude(obj.getDouble("startLongitude"));
                    task.setStopLongitude(obj.getDouble("stopLongitude"));


                    task.setInventoryBalance(obj.getInt("inventoryBalance"));
                    task.setQuantitySold(obj.getInt("quantity"));
                    task.setQuantity(obj.getInt("quantitySold"));

                    taskList.add(task);
                }

                loadChart(taskList);

            } catch (JSONException e) {
                e.printStackTrace();
                System.err.println(s);
            } catch (ParseException e) {

                e.printStackTrace();
            }
        }

    }

    private void loadChart(ArrayList<Task> list) {
        for (Task task : list) {
            if (task != null) {
                itemBalance += task.getInventoryBalance();
                itemQuantitySold += task.getQuantitySold();
                itemQuantity += task.getQuantity();

                // itemQuantity++;
                // itemQuantitySold++;
                //itemBalance++;
            }
        }
        yVals.clear();

        BarEntry balanceEntry = new BarEntry(0, itemBalance);
        BarEntry quantityEntry = new BarEntry(1, itemQuantity);
        BarEntry quantitySoldEntry = new BarEntry(2, itemQuantitySold);
        yVals.add(0, balanceEntry);
        yVals.add(1, quantitySoldEntry);
        yVals.add(2, quantityEntry);


        /*yVals.add(new BarEntry(1, itemQuantity));
        yVals.add(new BarEntry(2, itemQuantitySold));*/

        BarDataSet dataSet = new BarDataSet(yVals, "Inventory");


        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSet.setDrawValues(true);

        BarData set = new BarData(dataSet);

        barInventoryChart.setData(set);

        barInventoryChart.invalidate();
        barInventoryChart.setPinchZoom(false);
        barInventoryChart.animateY(500);
        barInventoryChart.getData().setHighlightEnabled(true);

        Legend legend = barInventoryChart.getLegend();
        LegendEntry le = new LegendEntry();

    }

    private void loadInventory() {

        String url = "";
        switch (loggedInUser.getRoleId()) {
            case User.SUPERVISOR:
                url = getString(R.string.api_url) + getString(R.string.task_url) + "?view=supervisor&key=" + getString(R.string.field_worker_api_key) + "&id=" + loggedInUser.getId();
                break;
            case User.NURSE:
                url = getString(R.string.api_url) + getString(R.string.task_url) + "?view=worker&key=" + getString(R.string.field_worker_api_key) + "&id=" + loggedInUser.getId();
                break;
        }
        new InventoryNetwork().execute(url);

    }

    private void loadRequests() {
        new InventoryRequestNetwork().execute(getString(R.string.api_url) + getString(R.string.inventory_view_requests_url) + "?key=" + getString(R.string.field_worker_api_key) + "&view=user_requests&id=" + loggedInUser.getId());
    }

    private class InventoryRequestNetwork extends android.os.AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            return Network.backgroundTask(null, strings[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String s) {

            if (s == null) {
                return;
            }

            try {
                JSONArray jsonArray = new JSONArray(s);
                //Toast.makeText(cxt,jsonArray.getJSONObject(0).getString("name"),Toast.LENGTH_SHORT).show();
                requestsList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject obj = jsonArray.getJSONObject(i);
                    Inventory.InventoryRequests requests = new Inventory.InventoryRequests();
                    requests.setId(obj.getInt("id"));
                    requests.setName(obj.getString("name"));
                    SimpleDateFormat dtf = new SimpleDateFormat("yyyy/M/dd H:m:s");
                    requests.setCreated(dtf.parse(obj.getString("created").replaceAll("-", "/")));
                    requests.setQuantity(obj.getInt("quantity"));
                    requests.setSupervisorMessage(obj.getString("supervisorMessage"));

                    User distributor = new User();
                    JSONObject dist = obj.getJSONObject("distributor");
                    distributor.setId(dist.getInt("id"));
                    distributor.setName(dist.getString("first_name") + " " + dist.get("last_name"));

                    User supervisor = new User();
                    JSONObject supervisor1 = obj.getJSONObject("supervisor");
                    supervisor.setId(supervisor1.getInt("id"));
                    supervisor.setName(supervisor1.getString("first_name") + " " + dist.get("last_name"));

                    requests.setDistributor(distributor);
                    requests.setSupervisor(supervisor);

                    requestsList.add(requests);


                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
                System.err.println(s);
                Log.d(getClass().getName(), e.getMessage());
                // Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_SHORT).show();

            }
            ArrayAdapter<Inventory.InventoryRequests>
                    listAdapter = new ArrayAdapter<Inventory.InventoryRequests>(cxt, R.layout.activity_inventory_request_single_item, requestsList) {

                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    LayoutInflater inflater = (LayoutInflater) cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.activity_inventory_request_single_item, null);


                    Inventory.InventoryRequests requests = requestsList.get(position);
                    TextView productText = convertView.findViewById(R.id.productText);
                    TextView distributorText = convertView.findViewById(R.id.distributorText);
                    TextView quantityText = convertView.findViewById(R.id.quantityText);
                    TextView commentText = convertView.findViewById(R.id.commentText);
                    TextView dateTimeText = convertView.findViewById(R.id.dateTimeText);


                    //   Toast.makeText(cxt,requests.getName(),Toast.LENGTH_SHORT).show();
                    productText.setText(requests.getName());
                    distributorText.setText(requests.getDistributor().getName());
                    quantityText.setText("" + requests.getQuantity());
                    commentText.setText(requests.getSupervisorMessage());
                    dateTimeText.setText(DateFormat.getDateTimeInstance().format(requests.getCreated()));


                    return convertView;
                }
            };
            listView.setAdapter(listAdapter);

        }

    }

    private static ArrayList<Inventory.InventoryRequests> requestsList = new ArrayList<>();
}
