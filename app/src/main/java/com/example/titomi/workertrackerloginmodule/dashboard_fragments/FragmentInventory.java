package com.example.titomi.workertrackerloginmodule.dashboard_fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Titomi on 2/8/2018.
 */

public class FragmentInventory extends Fragment implements OnChartValueSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    protected RectF mOnValueSelectedRectF = new RectF();
    View view;
    Context ctx;
    BarChart barInventoryChart;
    ArrayList<BarEntry> yVals = new ArrayList<>();
    float itemQuantity = 0;
    float itemQuantitySold = 0;
    float itemBalance = 0;
    private User loggedInUser;
    private ProgressBar pb;
    private SwipeRefreshLayout refreshLayout;

    public FragmentInventory() {
    }

    @Override
    public void onResume() {
        super.onResume();
        loadInventory();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ctx = getActivity();
        view = inflater.inflate(R.layout.inventory_fragment, container, false);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            loggedInUser = (User) extras.getSerializable(getString(R.string.loggedInUser));
        }

        pb = view.findViewById(R.id.progressBar);
        barInventoryChart= view.findViewById(R.id.barChartInvt);
        refreshLayout = view.findViewById(R.id.swipe_to_refresh_inventory);
        refreshLayout.setOnRefreshListener(this);

        barInventoryChart.getDescription().setEnabled(false);
        barInventoryChart.setOnChartValueSelectedListener(this);
        loadInventory();
        return view;
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

    private void loadChart(ArrayList<Task> list) {
        for (Task task : list) {
            if (task != null) {
                itemBalance += task.getInventoryBalance();
                itemQuantitySold += task.getQuantitySold();
                itemQuantity += task.getQuantity();
            }
        }
        yVals.clear();

        BarEntry balanceEntry = new BarEntry(0,itemBalance);
        BarEntry quantityEntry = new BarEntry(1,itemQuantity);
        BarEntry quantitySoldEntry = new BarEntry(2,itemQuantitySold);
        yVals.add(0,  balanceEntry);
        yVals.add(1,  quantitySoldEntry);
        yVals.add(2,  quantityEntry);


        /*yVals.add(new BarEntry(1, itemQuantity));
        yVals.add(new BarEntry(2, itemQuantitySold));*/

        BarDataSet dataSet = new BarDataSet(yVals, "Inventory");


//        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        dataSet.setColors(R.color.balanceColor, R.color.quantitySoldColor, R.color.quantityColor);
        dataSet.setDrawValues(true);

        BarData set = new BarData(dataSet);

        barInventoryChart.setData(set);

        barInventoryChart.invalidate();
        barInventoryChart.setPinchZoom(false);
        barInventoryChart.animateY(500);
        barInventoryChart.getData().setHighlightEnabled(true);

        Legend legend = barInventoryChart.getLegend();
        LegendEntry le = new LegendEntry();
//        legend.setCustom();

    }

    @Override
    public void onRefresh() {
        loadInventory();

    }

    @SuppressLint("NewApi")
    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = mOnValueSelectedRectF;
        barInventoryChart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = barInventoryChart.getPosition(e, YAxis.AxisDependency.LEFT);

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {

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
            refreshLayout.setRefreshing(true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            pb.setVisibility(View.GONE);

            refreshLayout.setRefreshing(false);
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
                            obj.getInt("status"),obj.getInt("productId"));
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

}
