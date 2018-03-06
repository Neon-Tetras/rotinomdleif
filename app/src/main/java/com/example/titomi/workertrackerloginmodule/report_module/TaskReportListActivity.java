package com.example.titomi.workertrackerloginmodule.report_module;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.HttpServicesClass;
import com.example.titomi.workertrackerloginmodule.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskReportListActivity extends AppCompatActivity {

    ListView taskReportsList;
    ProgressBar progressBar;
    String HttpUrl = "https://chemotropic-partiti.000webhostapp.com/sample/AllReports.php";
    List<String> IdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_report_list);

        taskReportsList = findViewById(R.id.listview1);
        progressBar = findViewById(R.id.progressBar1);

        new GetHttpResponse(TaskReportListActivity.this).execute();

        taskReportsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TaskReportListActivity.this,null);
                intent.putExtra("ListViewValue", IdList.get(position).toString());
                startActivity(intent);

                finish();
            }
        });
    }

    private class GetHttpResponse extends AsyncTask<Void, Void,Void> {
        public Context context;
        String JsonResult;
        List<Report> reportList;


        public GetHttpResponse(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            taskReportsList.setVisibility(View.VISIBLE);
            ListAdapterClass adapter = new ListAdapterClass(reportList, context);
            taskReportsList.setAdapter(adapter);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpServicesClass httpServicesClass = new HttpServicesClass(HttpUrl);
            try
            {
                httpServicesClass.ExecutePostRequest();

                if(httpServicesClass.getResponseCode() == 200)
                {
                    JsonResult = httpServicesClass.getResponse();

                    if(JsonResult != null)
                    {
                        JSONArray jsonArray = null;

                        try {
                            jsonArray = new JSONArray(JsonResult);

                            JSONObject jsonObject;

                            Report report;

                            reportList = new ArrayList<Report>();

                            for(int i=0; i<jsonArray.length(); i++)
                            {
                                report = new Report();

                                jsonObject = jsonArray.getJSONObject(i);

                                // Adding Student Id TO IdList Array.
                                IdList.add(jsonObject.getString("id").toString());

                                //Adding Student Name.
                                report.TaskId = jsonObject.getString("name").toString();

                                reportList.add(report);

                            }
                        }
                        catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                else
                {
                    Toast.makeText(context, httpServicesClass.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
    }
}