package com.example.titomi.workertrackerloginmodule.AttendanceModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.titomi.workertrackerloginmodule.AttendanceModule.AttendanceSubMenu.AttendanceActivity;
import com.example.titomi.workertrackerloginmodule.AttendanceModule.AttendanceSubMenu.AttendanceReportActivity;
import com.example.titomi.workertrackerloginmodule.AttendanceModule.AttendanceSubMenu.LeaveHistoryActivity;
import com.example.titomi.workertrackerloginmodule.AttendanceModule.AttendanceSubMenu.LeaveRequestActivity;
import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityLeaveApplication;

public class AttendanceMainActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    CardView clock, attendance, leave, approvedLeave;
    private User loggedInUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Attendance");

        clock = findViewById(R.id.clock_in_out);
        attendance = findViewById(R.id.attendance_report);
        leave = findViewById(R.id.leave_request);
        approvedLeave = findViewById(R.id.leave_approved);

        if(getIntent().getExtras() != null) {
            loggedInUser = (User) getIntent().getExtras().getSerializable(getString(R.string.loggedInUser));
        }
        clock.setOnClickListener(this);
        attendance.setOnClickListener(this);
        leave.setOnClickListener(this);
        approvedLeave.setOnClickListener(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent i;
        final String userFirstName = getIntent().getStringExtra("UserFirstName");
        final String userId = getIntent().getStringExtra("UserId");
        final String userLastName = getIntent().getStringExtra("UserLastName");
        final String userEmail = getIntent().getStringExtra("UserEmail");
        switch (v.getId()) {
            case R.id.clock_in_out:
                i = new Intent(this, AttendanceActivity.class);
                i.putExtra("UserFirstName", userFirstName);
                i.putExtra("UserLastName", userLastName);
                i.putExtra("UserEmail", userEmail);
                i.putExtra("UserId", userId);
                startActivity(i);
                break;
            case R.id.attendance_report:
                i = new Intent(this, AttendanceReportActivity.class);
                i.putExtra("UserFirstName", userFirstName);
                i.putExtra("UserLastName", userLastName);
                i.putExtra("UserEmail", userEmail);
                i.putExtra("UserId", userId);
                startActivity(i);
                break;
            case R.id.leave_request:
                i = new Intent(this, ActivityLeaveApplication.class);
                i.putExtra(getString(R.string.loggedInUser),loggedInUser);

                startActivity(i);
                break;
            case R.id.leave_approved:
                i = new Intent(this, LeaveHistoryActivity.class);
                i.putExtra(getString(R.string.loggedInUser),loggedInUser);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}
