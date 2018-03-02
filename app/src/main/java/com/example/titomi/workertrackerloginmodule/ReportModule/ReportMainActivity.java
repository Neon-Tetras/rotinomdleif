package com.example.titomi.workertrackerloginmodule.ReportModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;

public class ReportMainActivity extends AppCompatActivity implements View.OnClickListener {
    CardView engage_people_report, task_report_history, task_report_vid, task_exection_report, inventory_report_main, sales_report_main, attendance_report_main;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Report & Insight");

        engage_people_report = findViewById(R.id.engage_people_report);
        task_report_history = findViewById(R.id.task_report);
        task_report_vid = findViewById(R.id.task_report_vid);
        task_exection_report = findViewById(R.id.task_execution_report);
        inventory_report_main = findViewById(R.id.inventory_report_main);
        sales_report_main = findViewById(R.id.sales_report_main);
        attendance_report_main = findViewById(R.id.attendance_report_main);

        engage_people_report.setOnClickListener(this);
        task_report_history.setOnClickListener(this);
        task_report_vid.setOnClickListener(this);
        task_exection_report.setOnClickListener(this);
        inventory_report_main.setOnClickListener(this);
        sales_report_main.setOnClickListener(this);
        attendance_report_main.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.engage_people_report:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.task_report:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.task_report_vid:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.task_execution_report:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.inventory_report_main:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sales_report_main:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.attendance_report_main:
                Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
