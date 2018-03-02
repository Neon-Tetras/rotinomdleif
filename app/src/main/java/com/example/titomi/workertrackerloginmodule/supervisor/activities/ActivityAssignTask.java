package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.DatabaseAdapter;
import com.example.titomi.workertrackerloginmodule.supervisor.Entity;

import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.util.DrawableManager;
import com.example.titomi.workertrackerloginmodule.supervisor.util.InputValidator;
import com.example.titomi.workertrackerloginmodule.supervisor.util.DateTimeUtil;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.example.titomi.workertrackerloginmodule.supervisor.util.NetworkChecker;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;
import static com.example.titomi.workertrackerloginmodule.supervisor.util.Network.backgroundTask;

public class ActivityAssignTask extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    Context cxt;
    private EditText dateEditText;
    private EditText timeEditText,taskTitleEdit,
            taskDescriptionEdit,institutionNameEdit,
            fullAddressEdit,quantityEdit,contactFullNameEdit,contactNumberEdit,locationEdit;
    private Spinner workerSpinner,stateSpinner,lgaSpinner,taskTypeSpinner;
    private TextView selectLocationText;
    private Button assignTaskBut;
    private ProgressBar lgaLoading;
    private Task selectedTask;
    private User loggedInUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_assign_task);

        cxt = this;
        dateEditText = findViewById(R.id.dateText);
        timeEditText = (EditText)findViewById(R.id.timeText);
        workerSpinner = (Spinner)findViewById(R.id.workers);
        stateSpinner = (Spinner)findViewById(R.id.state);
        lgaSpinner = (Spinner)findViewById(R.id.lga);
        selectLocationText = findViewById(R.id.selectLocation);
        taskTitleEdit = findViewById(R.id.taskTitle);
        taskDescriptionEdit = findViewById(R.id.description);
        institutionNameEdit = findViewById(R.id.institution);
        fullAddressEdit = findViewById(R.id.fullAddress);
        quantityEdit = findViewById(R.id.quantity);
        contactFullNameEdit = findViewById(R.id.contactFullName);
        contactNumberEdit = findViewById(R.id.contactPhone);
        assignTaskBut = findViewById(R.id.assignTaskBut);
        taskTypeSpinner = findViewById(R.id.taskType);
        locationEdit = findViewById(R.id.location);
        lgaLoading = findViewById(R.id.lgaLoading);

        dateEditText.setOnClickListener(this);
        timeEditText.setOnClickListener(this);
       // workerSpinner.setOnItemSelectedListener(this);
        stateSpinner.setOnItemSelectedListener(this);
        selectLocationText.setOnClickListener(this);
        assignTaskBut.setOnClickListener(this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.app_logo);
        actionBar.setDisplayUseLogoEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras() != null){
            loggedInUser = (User)getIntent().getExtras().getSerializable(getString(R.string.loggedInUser));
            selectedTask = (Task)getIntent().getExtras().getSerializable("task");
            setupView(selectedTask);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.dateText:
                DateTimeUtil.showDatePicker(cxt,dateEditText);
                break;
            case R.id.timeText:
                DateTimeUtil.showTimePicker(cxt,timeEditText);
                break;
            case R.id.selectLocation:
                Util.showPlacesPicker(cxt);
                break;
            case R.id.assignTaskBut:
                if(!NetworkChecker.haveNetworkConnection(cxt))return;
                if(assignTaskBut.getTag().equals("update")){
                    assignTask(getString(R.string.api_url)+getString(R.string.edit_task_url)+"?key="+getString(R.string.field_worker_api_key));
                }else{
                    assignTask(getString(R.string.api_url)+getString(R.string.add_task_url)+"?key="+getString(R.string.field_worker_api_key));
                }

            break;
        }
    }

    private void setupView(Task task){
       // workerIds
        DateFormat dtf = DateFormat.getDateInstance();
        dateEditText.setText(dtf.format(task.getDateGiven()));
        timeEditText.setText(task.getTimeGiven());
        //workerSpinner.set
        stateSpinner.setSelection(Arrays.asList(states.toArray()).indexOf(task.getState()));
        lgaSpinner.setSelection(Arrays.asList(lgas.toArray()).indexOf(task.getLga()));
        workerSpinner.setSelection(Arrays.asList(workerIds.toArray()).indexOf(task.getWorker().getId()));

        taskTitleEdit.setText(task.getName());
        taskDescriptionEdit.setText(task.getDescription());
        institutionNameEdit.setText(task.getInstitution_name());
        fullAddressEdit.setText(task.getAddress());
        quantityEdit.setText(String.format("%d",task.getQuantity()));
        contactFullNameEdit.setText(task.getContactName());
        contactNumberEdit.setText(task.getContactNumber());


        locationEdit.setText(task.getLocation());

        assignTaskBut.setTag("Update");
        assignTaskBut.setText("Update");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) return;
        switch (requestCode){
            case Util.PICK_PLACES:

                if(data == null) return;
                Place place = PlacePicker.getPlace(this, data);
                locationEdit.setText(place.getAddress());
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWorkerSpinner();

        //loadLgaSpinner();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) return;
                selectedState =(String) parent.getItemAtPosition(position);

        try {
            loadLgaSpinner(selectedState);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }

    private void loadWorkerSpinner(){
      //  final ProgressDialog d = new ProgressDialog(cxt);
        new android.os.AsyncTask<String,Void,String>(){
            @Override
            protected String doInBackground(String... params) {
                return backgroundTask(null,params[0]);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
              //  d.setMessage(getString(R.string.please_wait));
              //  d.show();
            }

            @Override
            protected void onPostExecute(String s) {
               // super.onPostExecute(s);

              //  Toast.makeText(cxt,s,Toast.LENGTH_LONG).show();
              //  d.dismiss();
                if(s == null){
                  //  Toast.makeText(cxt,"returned null",Toast.LENGTH_LONG).show();
                    return;

                }

                try {
                    JSONArray array = new JSONArray(s);

                    workerIds.clear();
                    workerNames.clear();
                   // Toast.makeText(cxt,""+array.length(),Toast.LENGTH_LONG).show();
                    for(int i = 0; i< array.length(); i++){
                        JSONObject obj = array.getJSONObject(i);
                        workerIds.add(obj.getLong("id"));
                        workerNames.add(String.format("%s %s - %s",
                                obj.getString("first_name"),
                                obj.getString("last_name"),
                                obj.getString("line_id")));


                    }
                  //  Toast.makeText(cxt,workerNames.get(0),Toast.LENGTH_LONG).show();
                    setupWorkerSpinner();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }

            //TODO: get the supervisor id programmatically

        }.execute(String.format("%s?key=%s&view=get_workers&id=%d",getString(R.string.api_url)+getString(R.string.supervisor_view_url),getString(R.string.field_worker_api_key),loggedInUser.getId()));
    }




    private void setupLgaSpinner(){
        ArrayAdapter<String> lgaAdapter = new ArrayAdapter<>(cxt, android.R.layout.simple_spinner_dropdown_item, lgas);
        lgaSpinner.setAdapter(lgaAdapter);
    }

    private void loadLgaSpinner(String state) throws UnsupportedEncodingException {
        selectedState = Network.encodeUrl(state);
        //  final ProgressDialog d = new ProgressDialog(cxt);
        new android.os.AsyncTask<String,Void,String>(){
            @Override
            protected String doInBackground(String... params) {
                return backgroundTask(null,params[0]);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            lgaSpinner.setEnabled(false);
            lgaLoading.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                lgaLoading.setVisibility(View.INVISIBLE);

                if(s == null){

                    return;

                }
                lgaSpinner.setEnabled(true);

                try {


                    JSONArray array = new JSONArray(s);
                    lgas.clear();


                    for(int i = 0; i<array.length(); i++){
                        lgas.add(array.getString(i));
                    }

                    setupLgaSpinner();

                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println(s);
                    Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }





        }.execute(getString(R.string.api_url)+getString(R.string.state_api_url)+"?view=lga&state="+selectedState);
    }
    private void setupWorkerSpinner(){
        if(workerNames.size() == 0){
            return;
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(cxt,android.R.layout.simple_spinner_dropdown_item,workerNames);
        workerSpinner.setAdapter(spinnerAdapter);
    }

    private void assignTask(String api_url){
        try {
            taskData.put(getString(R.string.supervisor_id),""+5);
            taskData.put(getString(R.string.worker_id),""+
                    workerIds.get(InputValidator.validateSpinner(workerSpinner,-1)));
            taskData.put(getString(R.string._task_type),taskTypeSpinner.getSelectedItem().toString());
            taskData.put(getString(R.string.task_title),InputValidator.validateText(taskTitleEdit,2));
            taskData.put(getString(R.string.task_description),InputValidator.validateText(taskDescriptionEdit,3));
            taskData.put(getString(R.string.task_location),InputValidator.validateText(locationEdit,2));
            taskData.put(getString(R.string.full_address),InputValidator.validateText(fullAddressEdit,5));

            taskData.put(getString(R.string.state),InputValidator.validateText(stateSpinner.getSelectedItem().toString(),2));
            taskData.put(getString(R.string.lga),lgas.get(InputValidator.validateSpinner(lgaSpinner,-1)));
            taskData.put(getString(R.string.quantity),InputValidator.validateText(quantityEdit,1));
            taskData.put(getString(R.string.state),InputValidator.validateText(stateSpinner.getSelectedItem().toString(),2));
            taskData.put(getString(R.string.contact_full_name),InputValidator.validateText(contactFullNameEdit,2));
            taskData.put(getString(R.string.date_given),InputValidator.validateText(dateEditText,8).replaceAll("/","-"));//.toString());
            taskData.put(getString(R.string.time_given),InputValidator.validateText(timeEditText,5));
            taskData.put(getString(R.string.institution_name),InputValidator.validateText(institutionNameEdit,2));
            taskData.put(getString(R.string.contact_number),InputValidator.validateText(contactNumberEdit,11));

            sendToNework(taskData,api_url);
        } catch (InputValidator.InvalidInputException e) {
            Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();
        }



    }

    private void sendToNework(final HashMap<String, String> taskData,String api_url) {
        new android.os.AsyncTask<String,Void,String>(){

            @Override
            protected String doInBackground(String... strings) {
                return Network.performPostCall(strings[0],taskData);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast toast = Toast.makeText(cxt,"Please wait..",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                toast.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s == null){
                    return;
                }

                try {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject obj = jsonArray.getJSONObject(0);
                    if(obj.getInt(getString(R.string.statusCode)) == Entity.STATUS_OK){
                        AlertDialog alertDialog = new AlertDialog.Builder(cxt).create();
                        alertDialog.setMessage("Task Added Successfully");
                        alertDialog.show();

                        //Toast.makeText(cxt,"Task Added Successfully",Toast.LENGTH_LONG).show();
                        clearAllFields();
                    }else{
                        AlertDialog alertDialog = new AlertDialog.Builder(cxt).create();
                        alertDialog.setMessage(obj.getString(getString(R.string.message)));
                        alertDialog.show();
                      //  Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();

                     //   Toast.makeText(cxt,obj.getString(getString(R.string.message)),Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    AlertDialog alertDialog = new AlertDialog.Builder(cxt).create();
                    alertDialog.setMessage(e.getMessage());
                    alertDialog.show();
                    //e.printStackTrace();
                 //   Log.d("test", s);
                    System.out.println(s);
                   // Toast.makeText(cxt,e.getMessage(),Toast.LENGTH_LONG).show();

                }

            }
        }.execute(api_url);
    }


    private void clearAllFields() {
        Util.clearEditTexts(dateEditText, timeEditText, taskTitleEdit,
                taskDescriptionEdit, institutionNameEdit,
                fullAddressEdit, quantityEdit, contactFullNameEdit, contactNumberEdit, locationEdit);
        Util.clearSpinner(workerSpinner, stateSpinner, lgaSpinner, taskTypeSpinner);
    }


    ArrayList<Long> workerIds = new ArrayList<>();
    ArrayList<String> workerNames = new ArrayList<>();
    ArrayList<String> states = new ArrayList<>();
    ArrayList<String> lgas = new ArrayList<>();
    String selectedState;
    HashMap<String,String> taskData = new HashMap<>();
    public static final String update = "update";
    final String assign = "assign";
}
