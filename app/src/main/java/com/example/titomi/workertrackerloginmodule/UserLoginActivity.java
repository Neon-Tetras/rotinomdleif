package com.example.titomi.workertrackerloginmodule;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class UserLoginActivity extends AppCompatActivity {

    public static final String UserEmail = "";
    EditText mLoginCode;
    Button mLogIn;
    String mLoginCodeHolder;
    String finalResult;
    String HttpURL = "https://chemotropic-partiti.000webhostapp.com/sample/UserLogin.php";
    Boolean CheckEditText;
    ProgressDialog progressDialog;
    HashMap<String, String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginCode = findViewById(R.id.input_login);
        mLogIn = findViewById(R.id.btn_login);

            mLogIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckEditTextIsEmptyOrNot();
                    if (CheckEditText) {
                        UserLoginFunction(mLoginCodeHolder);
                    } else {
                        Toast.makeText(UserLoginActivity.this, "Please fill all form fields.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    public void CheckEditTextIsEmptyOrNot() {

        mLoginCodeHolder = mLoginCode.getText().toString();

        CheckEditText = !TextUtils.isEmpty(mLoginCodeHolder);
    }

    public void UserLoginFunction(final String loginCode) {

        class UserLoginClass extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog = ProgressDialog.show(UserLoginActivity.this, "Loading Data", null, true, true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {
                super.onPostExecute(httpResponseMsg);

                progressDialog.dismiss();

                if (httpResponseMsg.equalsIgnoreCase("Data Matched")) {
                    SharedPreferences shared = getSharedPreferences("Login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = shared.edit();
                    editor.putString("email", loginCode);
                    editor.apply();
                Intent intent = new Intent(UserLoginActivity.this, DashboardActivity.class);

                    intent.putExtra(UserEmail, loginCode);

                    startActivity(intent);

                } else {

                    Toast.makeText(UserLoginActivity.this, httpResponseMsg, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {

                hashMap.put("login_id", params[0]);

                finalResult = httpParse.postRequest(hashMap, HttpURL);

                Log.i("FinalResult Object", finalResult);

                SharedPreferences shared = getSharedPreferences("Login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = shared.edit();
                editor.putString("email", loginCode);
                editor.apply();

                return finalResult;
            }
        }

        UserLoginClass userLoginClass = new UserLoginClass();

        userLoginClass.execute(loginCode);
    }
}