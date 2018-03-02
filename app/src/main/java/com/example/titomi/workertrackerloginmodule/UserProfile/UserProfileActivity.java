package com.example.titomi.workertrackerloginmodule.UserProfile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.titomi.workertrackerloginmodule.R;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;

public class UserProfileActivity extends AppCompatActivity {

    Button editProfileBtn;
    TextView count_items, count_task_comp, count_pending;
    Toolbar toolbar;
    String EmailHolder;
    PrimaryDrawerItem mProfile, mTask, mInventory, mAttendance, mWorkerTrack, mSupervisorManager, mMessage, mAlert, mReport, mLiveChat;
    SecondaryDrawerItem mSettings,mLogout;

    SharedPreferences sp;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        editProfileBtn = findViewById(R.id.editProfile);
        count_items = findViewById(R.id.count_items);
        count_task_comp = findViewById(R.id.count_task_comp);
        count_pending = findViewById(R.id.count_pending);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        toolbar.setTitleTextColor(Color.WHITE);

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfile = new Intent(UserProfileActivity.this, EditProfileActivity.class);
                startActivity(editProfile);
            }
        });

        /*AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName("Damola Davies").withEmail(EmailHolder).withIcon(getResources().getDrawable(R.drawable.profile))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();
        headerResult.getView().setHovered(true);

        mProfile = new PrimaryDrawerItem().withIdentifier(1).withName("My Profile");
        mTask = new PrimaryDrawerItem().withIdentifier(2).withName("Task Manager");
        mInventory = new PrimaryDrawerItem().withIdentifier(3).withName("Inventory");
        mAttendance = new PrimaryDrawerItem().withIdentifier(4).withName("Attendance Report");
        mWorkerTrack = new PrimaryDrawerItem().withIdentifier(5).withName("Worker Tracking");
        mSupervisorManager = new PrimaryDrawerItem().withIdentifier(6).withName("Supervisor Manager");
        mMessage = new PrimaryDrawerItem().withIdentifier(7).withName("Message & Notice");
        mAlert = new PrimaryDrawerItem().withIdentifier(8).withName("Alert Manager");
        mLiveChat = new PrimaryDrawerItem().withIdentifier(9).withName("Live Chat");
        mReport = new PrimaryDrawerItem().withIdentifier(10).withName("Report & Insight");
        mSettings = new SecondaryDrawerItem().withIdentifier(11).withName("General Settings");
        mLogout = new SecondaryDrawerItem().withIdentifier(12).withName("Logout");


        Drawer mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        mProfile,
                        new DividerDrawerItem(),
                        mTask,
                        new DividerDrawerItem(),
                        mInventory,
                        new DividerDrawerItem(),
                        mAttendance,
                        new DividerDrawerItem(),
                        mWorkerTrack,
                        new DividerDrawerItem(),
                        mSupervisorManager,
                        new DividerDrawerItem(),
                        mMessage,
                        new DividerDrawerItem(),
                        mAlert,
                        new DividerDrawerItem(),
                        mLiveChat,
                        new DividerDrawerItem(),
                        mReport,
                        new SectionDrawerItem(),
                        mSettings,
                        new DividerDrawerItem(),
                        mLogout
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null){
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1){
                                intent = new Intent(UserProfileActivity.this, DashboardActivity.class);
                            } else if (drawerItem.getIdentifier() == 2){
                                intent = new Intent(UserProfileActivity.this, TaskActivity.class);
                            }else if (drawerItem.getIdentifier() == 3){
                                intent = new Intent(UserProfileActivity.this, InventoryActivity.class);
                            }else if (drawerItem.getIdentifier() == 4){
                                intent = new Intent(UserProfileActivity.this, AttendanceMainActivity.class);
                            }else if (drawerItem.getIdentifier() == 5){
                                Toast.makeText(UserProfileActivity.this, "you clicked " + mWorkerTrack.getName().toString(), Toast.LENGTH_SHORT).show();
                            }else if (drawerItem.getIdentifier() == 6){
                                intent = new Intent (UserProfileActivity.this, SupervisorMainActivity.class);
                            }else if (drawerItem.getIdentifier() == 7){
                                Toast.makeText(UserProfileActivity.this, "you clicked " + mMessage.getName().toString(), Toast.LENGTH_SHORT).show();
                            }else if (drawerItem.getIdentifier() == 8){
                                intent = new Intent (UserProfileActivity.this, AlertMainActivity.class);
                            }else if (drawerItem.getIdentifier() == 9){
                                Toast.makeText(UserProfileActivity.this, "you clicked " + mLiveChat.getName().toString(), Toast.LENGTH_SHORT).show();
                            }else if (drawerItem.getIdentifier() == 10){
                                intent = new Intent(UserProfileActivity.this, ReportMainActivity.class);
                            }else if (drawerItem.getIdentifier() == 11){
                                Toast.makeText(UserProfileActivity.this, "you clicked " + mSettings.getName().toString(), Toast.LENGTH_SHORT).show();
                            }else if (drawerItem.getIdentifier() == 12){
                                SharedPreferences sp = getSharedPreferences("Login",MODE_PRIVATE);
                                SharedPreferences.Editor e =sp.edit();
                                e.clear();
                                e.commit();

                                startActivity(new Intent(UserProfileActivity.this, UserLoginActivity.class));
                                finish();
                            }
                            if (intent != null){
                                UserProfileActivity.this.startActivity(intent);
                            }
                        }
                        return true;
                    }
                })
                .build();*/
    }
}
