package com.example.titomi.workertrackerloginmodule.Task;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.ReportModule.ReportActivity;

public class TaskActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    CardView assignTask,submit_sche,task_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Task Manager");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        assignTask = findViewById(R.id.assign_task);
        submit_sche = findViewById(R.id.submit_schedule);
        task_history = findViewById(R.id.task_history);

        assignTask.setOnClickListener(this);
        submit_sche.setOnClickListener(this);
        task_history.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.assign_task:
                i = new Intent(this, RoutePlanActivity.class);
                startActivity(i);
                break;
            case R.id.submit_schedule:
                i = new Intent(this, ReportActivity.class);
                startActivity(i);
                break;
            case R.id.task_history:
                i = new Intent(this, TaskDetailsActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}
