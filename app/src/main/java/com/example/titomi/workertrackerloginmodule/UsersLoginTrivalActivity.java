package com.example.titomi.workertrackerloginmodule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.apis.rest.BusProvider;
import com.example.titomi.workertrackerloginmodule.apis.rest.Communicator;
import com.example.titomi.workertrackerloginmodule.apis.rest.ErrorEvent;
import com.example.titomi.workertrackerloginmodule.apis.rest.ServerEvent;
import com.example.titomi.workertrackerloginmodule.shared_pref_manager.SharedPrefManager;
import com.example.titomi.workertrackerloginmodule.supervisor.activities.ActivityMessageMenu;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import retrofit2.Retrofit;

public class UsersLoginTrivalActivity extends AppCompatActivity {

    public static Bus bus;
    private static Retrofit retrofit = null;
    EditText TvLineId;
    Button BtnLogin;
    ProgressDialog mProgressDialog;
    private SharedPrefManager sharedPrefManager;
    String TvLineHolder;
    private static final String TAG = "Communicator";
    Boolean checkEditText;
    private Communicator communicator;

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefManager = new SharedPrefManager(getApplicationContext());
        if (sharedPrefManager.isUserLogin()){
            if (sharedPrefManager.getSavedRole().equalsIgnoreCase("worker")) {
                Intent i = new Intent(this, DashboardActivity.class);
                startActivity(i);
                finish();
            }else if(sharedPrefManager.getSavedRole().equalsIgnoreCase("supervisor")){
                Intent i = new Intent(this, ActivityMessageMenu.class);
                startActivity(i);
                finish();
            }
        }else{
            setContentView(R.layout.activity_users_login_trival);

            communicator = new Communicator();

            TvLineId = findViewById(R.id.line_id);
            BtnLogin = findViewById(R.id.login);

            BtnLogin.setOnClickListener(onClickListener());
        }
    }

    private View.OnClickListener onClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mProgressDialog.show();
                checkIfEditTextisEmpty();

                if (checkEditText) {
                    TvLineHolder = TvLineId.getText().toString();
                    useGet(TvLineHolder);
                    goToHomeActivity();
                } else {
                    Toast.makeText(UsersLoginTrivalActivity.this, "Please fill all form fields.", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void useGet(String tvLineHolder) {
        communicator.loginGet(tvLineHolder);
        goToHomeActivity();

    }

    private void goToHomeActivity() {
        //saving data to Session (SharedPreferences)
        sharedPrefManager.setSavedLineId(TvLineId.getText().toString());
        sharedPrefManager.setUserLoggedIn(true);
/*
        //go to Home Screen
        Intent intent = new Intent(this, DashboardActivity.class);
        ServerEvent serverEvent = null;
        intent.putExtra("UserFirstName", serverEvent.getUsers().getFirstName());
        intent.putExtra("UserId", serverEvent.getUsers().getId());
        startActivity(intent);
        finish(); //finish LoginActivity*/
    }

    @Subscribe
    public void onServerEvent(ServerEvent serverEvent) {
//        mProgressDialog.dismiss();
        Toast.makeText(this, "You are a " + serverEvent.getUsers().getRole(), Toast.LENGTH_SHORT).show();
        if (serverEvent.getUsers().getLineId() != null) {

            Intent intent = null;
            if (serverEvent.getUsers().getRole().equalsIgnoreCase("supervisor")){
                intent = new Intent(this, ActivityMessageMenu.class);
                startActivity(intent);
            }else{
                intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
            }
            intent.putExtra("UserFirstName", serverEvent.getUsers().getFirstName());
            intent.putExtra("UserLastName", serverEvent.getUsers().getLastName());
            intent.putExtra("UserEmail", serverEvent.getUsers().getEmail());
            intent.putExtra("UserId", serverEvent.getUsers().getId());
            intent.putExtra("Role", serverEvent.getUsers().getRole());

            sharedPrefManager.setSavedFirstName(serverEvent.getUsers().getFirstName());
            sharedPrefManager.setSavedLastName(serverEvent.getUsers().getLastName());
            sharedPrefManager.setSavedEmail(serverEvent.getUsers().getEmail());
            sharedPrefManager.setSavedUserId(Integer.parseInt(serverEvent.getUsers().getId()));
            sharedPrefManager.setSavedRole(serverEvent.getUsers().getRole());
            startActivity(intent);
            finish();

        }
    }

    @Subscribe
    public void onErrorEvent(ErrorEvent errorEvent) {
        Toast.makeText(this, "Hello" + errorEvent.getErrorMsg(), Toast.LENGTH_SHORT).show();
    }

    private void checkIfEditTextisEmpty() {
        TvLineHolder = TvLineId.getText().toString();
        checkEditText = !TextUtils.isEmpty(TvLineHolder);
    }
}