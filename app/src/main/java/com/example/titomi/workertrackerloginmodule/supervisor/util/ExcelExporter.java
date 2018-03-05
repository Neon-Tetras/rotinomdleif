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
import java.util.logging.Level;
import java.util.logging.Logger;

import jxl.CellView;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Font;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

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
                Log.e(this.getClass().getName(), e.getMessage(), e);
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

  /*  public boolean exportCMs(File destination){
        int cmCount = 0;
        try {



            WritableWorkbook workBook = Workbook.createWorkbook(destination);
            WritableSheet sheet = workBook.createSheet("Sheet1", 0);

            WritableFont arial10font = new WritableFont(WritableFont.ARIAL, 11,Font.BOLD);
            WritableCellFormat arial10format = new WritableCellFormat (arial10font);


            Label fullNameLabel = new Label(0,0,reader.getString("fullname").toUpperCase(),arial10format);
            Label sexLabel = new Label(1,0,reader.getString("sex").toUpperCase(),arial10format);
            Label stateLabel = new Label(2,0,reader.getString("state").toUpperCase(),arial10format);
            Label universityLabel = new Label(3,0,reader.getString("university").toUpperCase(),arial10format);
            Label courseLabel = new Label(4,0,reader.getString("course").toUpperCase(),arial10format);
            Label qualificationLabel = new Label(5,0,reader.getString("qualification").toUpperCase(),arial10format);
            Label callupNoLabel = new Label(6,0,reader.getString("callup").toUpperCase(),arial10format);
            Label stateCodeLabel = new Label(7,0,reader.getString("statecode").toUpperCase(),arial10format);

            for(String heading : header){


            sheet.addCell(fullNameLabel);
            sheet.addCell(sexLabel);
            sheet.addCell(stateLabel);
            sheet.addCell(universityLabel);
            sheet.addCell(courseLabel);
            sheet.addCell(qualificationLabel);
            sheet.addCell(callupNoLabel);
            sheet.addCell(stateCodeLabel);


            ArrayList<CorpsMember> allCms = getAll();

            cmCount = allCms.size();

            for(int i = 1; i<= cmCount; i++){
                // 1 is subtracted from i in List.get because List starts from 0 while the cells start from 1 because of the cell header
                sheet.addCell(new Label(0,i,allCms.get(i-1).getFullname()));
                sheet.addCell(new Label(1,i,allCms.get(i-1).getSex()));
                sheet.addCell(new Label(2,i,allCms.get(i-1).getState()));
                sheet.addCell(new Label(3,i,allCms.get(i-1).getUniversity()));
                sheet.addCell(new Label(4,i,allCms.get(i-1).getCourse()));
                sheet.addCell(new Label(5,i,allCms.get(i-1).getQualification()));
                sheet.addCell(new Label(6,i,allCms.get(i-1).getCallUpNo()));
                sheet.addCell(new Label(7,i,allCms.get(i-1).getStateCode()));


            }
            CellView cell;
            for(int i = 0; i<sheet.getColumns(); i++){
                cell = sheet.getColumnView(i);
                cell.setAutosize(true);
                sheet.setColumnView(i, cell);
            }
            workBook.write();
            workBook.close();
        } catch (IOException | WriteException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return cmCount != 0;
    }
*/
}

