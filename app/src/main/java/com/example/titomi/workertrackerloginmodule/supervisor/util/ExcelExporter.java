package com.example.titomi.workertrackerloginmodule.supervisor.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.os.Environment;
import android.widget.Toast;


import com.example.titomi.workertrackerloginmodule.R;

import au.com.bytecode.opencsv.CSVWriter;


/**
 * Created by NeonTetras on 23-Feb-18.
 */


public class ExcelExporter  extends android.os.AsyncTask<String ,String, String>{
    private  final ProgressDialog dialog;
    private String fileName;
    private final Context cxt;
    private String[] header;
    private ArrayList<String[]> data;

        public ExcelExporter(Context cxt, String[] header, ArrayList<String[]> data){
            this.cxt = cxt;
            dialog = new ProgressDialog(cxt);
            SimpleDateFormat dtf = new SimpleDateFormat("yyyymmddhhmmss");
            String dateText = dtf.format(new Date());
            fileName =  cxt.getString(R.string.app_name)
                    .replace(" ","_")+""+dateText+"_Export.csv";
            this.header = header;
            this.data = data;
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting csv...");
            this.dialog.show();
        }

        protected String doInBackground(final String... args){
            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            File file = new File(exportDir, fileName);
            try {

                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));


                csvWrite.writeNext(header);


                String[] datas = null;
                for(int i = 0; i<data.size(); i++){
                    datas= data.get(i);
                    csvWrite.writeNext(datas);

                }

                csvWrite.close();
                return file.getAbsolutePath();
            }
            catch (IOException e){
                Log.e("MainActivity", e.getMessage(), e);
                return "";
            }
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(final String success) {

            if (this.dialog.isShowing()){
                this.dialog.dismiss();
            }
            if (!success.isEmpty()){
                AlertDialog alertDialog = new AlertDialog.Builder(cxt).create();
                alertDialog.setMessage("Export successful!\r\nFile saved to: "+success);
             //   Toast.makeText(cxt, "Export successful!", Toast.LENGTH_SHORT).show();
                alertDialog.show();
            }
            else {
                Toast.makeText(cxt, "Export failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

