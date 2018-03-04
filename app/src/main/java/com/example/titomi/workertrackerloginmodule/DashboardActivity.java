package com.example.titomi.workertrackerloginmodule;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.AlertManager.AlertMainActivity;
import com.example.titomi.workertrackerloginmodule.AttendanceModule.AttendanceMainActivity;
import com.example.titomi.workertrackerloginmodule.DashboardFragments.FragmentAttendance;
import com.example.titomi.workertrackerloginmodule.DashboardFragments.FragmentInventory;
import com.example.titomi.workertrackerloginmodule.DashboardFragments.FragmentTask;
import com.example.titomi.workertrackerloginmodule.DashboardFragments.ViewPagerAdapter;
import com.example.titomi.workertrackerloginmodule.InventoryModule.InventoryActivity;
import com.example.titomi.workertrackerloginmodule.ReportModule.ReportMainActivity;
import com.example.titomi.workertrackerloginmodule.SharedPrefManager.SharedPrefManager;
import com.example.titomi.workertrackerloginmodule.SupervisorManager.SupervisorMainActivity;
import com.example.titomi.workertrackerloginmodule.UserProfile.UserProfileActivity;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityInventoryRequestsListing;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityLeaveApplication;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityMessageListing;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityReportListing;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityTaskListing;
import com.example.titomi.workertrackerloginmodule.supervisor.services.FieldMonitorMessagingService;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ImageUtils;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class DashboardActivity extends AppCompatActivity {

    Toolbar toolbar;

    String EmailHolder;
    PrimaryDrawerItem mProfile, mTask, mInventory, mAttendance, mWorkerTrack, mSupervisorManager, mMessage, mAlert, mReport, mLiveChat,supervisorInventoryRequest,supervisorReports,supervisorTasks,leaveApplication;
    SecondaryDrawerItem mSettings, mLogout;
    SharedPrefManager sharedPrefManager;

//    Toolbar toolbar;

    User loggedInUser;
    private static Context cxt;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh:
                Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Dashboard");
        cxt = this;
        startService(new Intent(cxt, FieldMonitorMessagingService.class));

        //Get the user serializable sent from the login activity
        Bundle extras =getIntent().getExtras();
        if(extras != null){
            loggedInUser = (User)extras.getSerializable(getString(R.string.loggedInUser));
        }
        sharedPrefManager = new SharedPrefManager(getApplicationContext());

        TabLayout tabLayout =  findViewById(R.id.tabLayout_id);
        ViewPager viewPager =  findViewById(R.id.viewpager_id);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new FragmentTask(), "Task");
        adapter.AddFragment(new FragmentInventory(), "Inventory");
        adapter.AddFragment(new FragmentAttendance(), "Attendance");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        final String firstNameUser = sharedPrefManager.getSavedFirstName();
        final String lastNameUser = sharedPrefManager.getSavedLastName();
        final String emailUser = sharedPrefManager.getSavedEmail();
        final int Iduser = sharedPrefManager.getSavedUserId();

        final String userFirstName = getIntent().getStringExtra("UserFirstName");
        final String userId = getIntent().getStringExtra("UserId");
        final String userLastName = getIntent().getStringExtra("UserLastName");
        final String userEmail = getIntent().getStringExtra("UserEmail");

        ImageUtils.ImageStorage storage = new ImageUtils.ImageStorage(loggedInUser);
        String imageUrl = getString(R.string.server_url)+loggedInUser.getFeaturedImage();
        String imageName = ImageUtils.getImageNameFromUrlWithExtension(imageUrl);
        Drawable imageDrawable = null;

        if(storage.imageExists(imageName)){
             String imagePath = storage.getImage(imageName).getAbsolutePath();
             imageDrawable = Drawable.createFromPath(imagePath);

         }else{
            ImageUtils.GetImages getImages = new ImageUtils.GetImages(loggedInUser,imageUrl,imageName);

            getImages.execute();
        }

         if(imageDrawable == null){
            imageDrawable = getResources().getDrawable(R.drawable.profile);
         }
        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem().withName(loggedInUser.getFullName()).withEmail(loggedInUser.getEmail()).withIcon(imageDrawable)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();


        mProfile = new PrimaryDrawerItem().withIdentifier(1).withName("My Profile");
        mTask = new PrimaryDrawerItem().withIdentifier(2).withName("Tasks");
        mInventory = new PrimaryDrawerItem().withIdentifier(3).withName("Inventory Manager");
        mAttendance = new PrimaryDrawerItem().withIdentifier(4).withName("Attendance Report");
        mWorkerTrack = new PrimaryDrawerItem().withIdentifier(5).withName("Worker Tracking");
        mSupervisorManager = new PrimaryDrawerItem().withIdentifier(6).withName("Supervisor Manager");
        mMessage = new PrimaryDrawerItem().withIdentifier(7).withName("Message & Notice");
        mAlert = new PrimaryDrawerItem().withIdentifier(8).withName("Alert Manager");
        mLiveChat = new PrimaryDrawerItem().withIdentifier(9).withName("Live Chat");
        mReport = new PrimaryDrawerItem().withIdentifier(10).withName("Report & Insight");
        mSettings = new SecondaryDrawerItem().withIdentifier(11).withName("General Settings");
        mLogout = new SecondaryDrawerItem().withIdentifier(12).withName("Logout");
        supervisorInventoryRequest = new PrimaryDrawerItem().withIdentifier(13).withName("Inventory Requests");
        supervisorReports  = new PrimaryDrawerItem().withIdentifier(14).withName("Reports");
        supervisorTasks = new PrimaryDrawerItem().withIdentifier(15).withName("Tasks");
        leaveApplication = new PrimaryDrawerItem().withIdentifier(16).withName("Leave Application");


        DrawerBuilder drawerBuilder = new DrawerBuilder();
        drawerBuilder.withActivity(this);
        drawerBuilder.withToolbar(toolbar);
        drawerBuilder.withAccountHeader(headerResult);

        switch (loggedInUser.getRoleId()){
            case User.NURSE:
                drawerBuilder.addDrawerItems(
                        mProfile,
                        new DividerDrawerItem(),
                        mTask,
                        new DividerDrawerItem(),
                        mInventory,
                        new DividerDrawerItem(),
                        mAttendance,
                        new DividerDrawerItem(),
                       /* mWorkerTrack,
                        new DividerDrawerItem(),*/
                        mSupervisorManager,
                        new DividerDrawerItem(),
                        mMessage,
                        new DividerDrawerItem(),
                     /*   mAlert,
                        new DividerDrawerItem(),*/
                        mLiveChat,
                        new DividerDrawerItem(),

                      /*  mSettings,
                        new DividerDrawerItem(),*/
                        mLogout
                );
                break;
            case User.SUPERVISOR:

                drawerBuilder.addDrawerItems(
                        mProfile,
                        new DividerDrawerItem(),
                        mMessage,
                        new DividerDrawerItem(),
                        supervisorInventoryRequest,
                        new DividerDrawerItem(),
                        supervisorReports,
                        new DividerDrawerItem(),
                        supervisorTasks,
                        new DividerDrawerItem(),
                        leaveApplication,
                        new SectionDrawerItem(),
                        mReport,
                        new SectionDrawerItem(),
                        mSettings,
                        new DividerDrawerItem(),
                        mLogout
                );
                break;
        }
        drawerBuilder.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                if (drawerItem != null) {
                    Intent intent = null;
                    Long identifier = drawerItem.getIdentifier();
                    switch (identifier.intValue()){
                        case 1:
                            intent = new Intent(cxt, UserProfileActivity.class);

                            break;
                        case 2:

                            intent = new Intent(cxt, ActivityTaskListing.class);

                            break;
                        case 3:
                            intent = new Intent(cxt, InventoryActivity.class);
                            break;
                        case 4:
                            intent = new Intent(cxt, AttendanceMainActivity.class);
                            break;
                        case 5:

                            break;
                            case 6:
                                intent = new Intent(cxt, SupervisorMainActivity.class);
                            break;
                        case 7:
                            intent = new Intent(cxt, ActivityMessageListing.class);

                            break;
                        case 8:
                            intent = new Intent(cxt, AlertMainActivity.class);

                            break;
                        case 9:


                            break;
                        case 10:
                            intent = new Intent(cxt, ReportMainActivity.class);
                            break;
                        case 12:
                            sharedPrefManager.clearSession();
                            sharedPrefManager.logout();
                            startActivity(new Intent(cxt, LoginActivity.class));
                            finish();
                            break;
                        case 13:
                            intent = new Intent(cxt, ActivityInventoryRequestsListing.class);

                           // startActivity(intent);
                            break;
                        case 14:
                            intent = new Intent(cxt, ActivityReportListing.class);

                          //  startActivity(intent);
                            break;
                        case 15:
                            intent = new Intent(cxt, ActivityTaskListing.class);

                         //   startActivity(intent);
                            break;
                        case 16:
                            intent = new Intent(cxt, ActivityLeaveApplication.class);

                           // startActivity(intent);
                            break;
                    }

                    if (intent != null) {
                        intent.putExtra(getString(R.string.loggedInUser),loggedInUser);
                        intent.putExtra("UserFirstName", firstNameUser);
                        intent.putExtra("UserLastName", lastNameUser);
                        intent.putExtra("UserEmail", emailUser);
                        intent.putExtra("UserId", sharedPrefManager.getSavedUserId());
                        cxt.startActivity(intent);
                    }
                }
                return true;
            }
        });
        Drawer mDrawer = drawerBuilder.build();
    }


}
