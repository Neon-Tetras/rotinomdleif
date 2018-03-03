package com.example.titomi.workertrackerloginmodule.AttendanceModule.AttendanceSubMenu;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.titomi.workertrackerloginmodule.APIs.model.Users;
import com.example.titomi.workertrackerloginmodule.APIs.model.leaveModel.LeaveCommunicator;
import com.example.titomi.workertrackerloginmodule.APIs.model.leaveModel.LeaveModel;
import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.SharedPrefManager.SharedPrefManager;
import com.example.titomi.workertrackerloginmodule.supervisor.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LeaveHistoryActivity extends AppCompatActivity {

    private static final String TAG = "LeaveCommunicator";
    private static final String API_KEY = "98SY.4T1nXhPI";
    private static String requestURL;
    final String view = "by_id";
    final String api_view = "by_id";
    LeaveAdapter adapter;
    List<LeaveModel> leaveModels;
    LeaveCommunicator communicator;
    Users users;
    Toolbar toolbar;
//    String userId = getIntent().getStringExtra("UserId");
    SharedPrefManager sharedPrefManager;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private LinearLayoutManager  linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private RecyclerView.Adapter adapter2;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_history);
        recyclerView = findViewById(R.id.leave_history);

        leaveModels = new ArrayList<>();

        adapter2 = new AdapterLeave(getApplicationContext(), leaveModels);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter2);

        if(getIntent().getExtras() != null) {
            loggedInUser = (User) getIntent().getExtras().getSerializable(getString(R.string.loggedInUser));
        }
        requestURL = getString(R.string.api_url)+"leave/view.php?key="+getString(R.string.field_worker_api_key)+"&view=user_requests&user_id="+loggedInUser.getId();
        getData();

        /*recyclerView = findViewById(R.id.leave_history);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        String userId = getIntent().getStringExtra("UserId");


        communicator = new LeaveCommunicator();
        users = new Users();
        loadLeaveData(api_view, userId);*/

//        getLeaveList();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Leave Requests");
    }

    private void getData() {
        sharedPrefManager = new SharedPrefManager(getApplicationContext());
        int userId = sharedPrefManager.getSavedUserId();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(requestURL, new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e(TAG, "success");
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        LeaveModel leaveModel = new LeaveModel();
                        leaveModel.setReason(jsonObject.getString("reason"));
                        leaveModel.setStatus(jsonObject.getString("status"));
//                        leaveModel.setDate(jsonObject.getInt("date"));
                        leaveModels.add(leaveModel);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }
                adapter2.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /*private void getLeaveList() {
        try {
            LeaveApiService service = LeaveApiClient.getRetrofit().create(LeaveApiService.class);
            String userId = getIntent().getStringExtra("UserId");
            Call<List<LeaveModel>> call = service.getLeave(API_KEY, view, userId);
            call.enqueue(new Callback<List<LeaveModel>>() {
                @Override
                public void onResponse(Call<List<LeaveModel>> call, Response<List<LeaveModel>> response) {
                    if (response.body() == null) {
                        Log.e(TAG, "Failed");
                        return;
                    }
                    BusProvider.getInstance().post(new ServerEvent(response.body()));
                    Log.e(TAG, "Success");
                    List<LeaveModel> leaveList = response.body();
                    layoutManager = new LinearLayoutManager(LeaveHistoryActivity.this);
                    RecyclerView recyclerView = findViewById(R.id.leave_history);
                    recyclerView.setLayoutManager(layoutManager);

                    LeaveAdapter leaveAdapter = new LeaveAdapter(leaveList);
                    recyclerView.setAdapter(leaveAdapter);

                }

                @Override
                public void onFailure(Call<List<LeaveModel>> call, Throwable t) {
                    BusProvider.getInstance().post(new ErrorEvent(-2, t.getMessage()));

                }
            });
        } catch (Exception e) {

        }
    }

    private void loadLeaveData(String view, String id) {
        communicator.leaveGet(view, id);
    }

    @Subscribe
    public void onServerEvent(ServerEvent serverEvent) {
        Toast.makeText(this, "" + serverEvent.getLeaveModel().get(0), Toast.LENGTH_SHORT).show();
        if (serverEvent.getLeaveModel().get(0) != null) {


        }
    }

    @Subscribe
    public void onErrorEvent(ErrorEvent errorEvent) {
        Toast.makeText(this, "" + errorEvent.getErrorMsg(), Toast.LENGTH_SHORT).show();
    }*/
}