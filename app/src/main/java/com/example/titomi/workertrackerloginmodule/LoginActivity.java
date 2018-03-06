package com.example.titomi.workertrackerloginmodule;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.shared_pref_manager.SharedPrefManager;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.InputValidator;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by NeonTetras on 01-Mar-18.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static EditText lineIdEdit;
    private static Button loginButton;
    private static ProgressBar progressBar;
    private static Context cxt;

    private static void goToDashBoard(User user) {
        Intent i = new Intent(cxt, DashboardActivity.class);
        i.putExtra(cxt.getString(R.string.loggedInUser), user);
        cxt.startActivity(i);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_login_trival);
        cxt = this;
        lineIdEdit = findViewById(R.id.line_id);
        loginButton = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);

        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {


        try {
            String username = URLEncoder.encode(InputValidator.validateText(lineIdEdit, 6),"UTF-8");
            progressBar.setVisibility(View.VISIBLE);
            new LoginNetworkTask().execute(getString(R.string.api_url) + getString(R.string.login_url) + "?key=" + getString(R.string.field_worker_api_key) + "&username=" + username);
        } catch (InputValidator.InvalidInputException | UnsupportedEncodingException e) {
            Toast.makeText(cxt, "" + getClass().getName() + "\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();


        }
    }

    private static final class LoginNetworkTask extends AsyncTask<String, Void, String> {


        @Override
        protected java.lang.String doInBackground(java.lang.String[] strings) {
            return Network.backgroundTask(null, strings[0]);
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);

            if (s == null) {
                return;
            }

            System.err.println(s);
            try {
                JSONObject obj = new JSONObject(s);
                if (obj.getInt("statusCode") == User.STATUS_OK) {
                    User user = new User();

                    user.setId(obj.getInt("id"));
                    user.setEmail(obj.getString("email"));
                    user.setRole(obj.getString("role"));
                    user.setRoleId(obj.getInt("roleId"));
                    user.setUsername(obj.getString("username"));
                    user.setLineId(obj.getString("line_id"));
                    user.setSupervisorId(obj.getLong("supervisor_id"));
                    user.setActive(obj.getInt("active") != 0);
                    user.setFullName(String.format("%s %s", obj.getString("last_name"), obj.getString("first_name")));
                    user.setAddress(obj.getString("user_address"));
                    user.setCity(obj.getString("user_city"));
                    user.setState(obj.getString("user_state"));
                    user.setCountry(obj.getString("user_country"));
                    user.setWorkType(obj.getString("work_type"));
                    user.setPhoneNumber(obj.getString("phone_number"));
                    user.setFeaturedImage(obj.getString("photo"));
                    user.setState(obj.getString("status"));
                    user.setStatusCode(obj.getInt("statusCode"));

                    SharedPrefManager sharedPrefManager = new SharedPrefManager(cxt);
                    sharedPrefManager.setSavedCity(user.getCity());
                    sharedPrefManager.setSavedEmail(user.getEmail());
                    sharedPrefManager.setSavedAddress(user.getAddress());
                    sharedPrefManager.setUserFullname(user.getFullName());
                    sharedPrefManager.setSavedLineId(user.getLineId());
                    sharedPrefManager.setSavedPhoneNumber(user.getPhoneNumber());
                    sharedPrefManager.setSavedPhoto(user.getFeaturedImage());
                    sharedPrefManager.setSavedRole(user.getRole());
                    Long userId = user.getId();
                    sharedPrefManager.setSavedUserId(userId.intValue());
                    sharedPrefManager.setSavedRoleId(user.getRoleId());


                    goToDashBoard(user);
                    //LoginActivity.cxt.fin

                }
            } catch (JSONException e) {
                e.printStackTrace();
                System.err.println(s);
            }

        }
    }
}
