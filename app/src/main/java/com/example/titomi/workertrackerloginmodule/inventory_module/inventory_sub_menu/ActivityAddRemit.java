package com.example.titomi.workertrackerloginmodule.inventory_module.inventory_sub_menu;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;

import org.w3c.dom.Text;

public class ActivityAddRemit extends AppCompatActivity implements View.OnClickListener {

    User loggedInUser;
    EditText amountText;
    TextView attachText;
    Context cxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cxt = this;
        setContentView(R.layout.activity_add_remit);
        amountText = findViewById(R.id.amountEdit);
        attachText = findViewById(R.id.attach);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(getIntent().getExtras() != null){
            loggedInUser = (User)getIntent().getExtras().getSerializable(getString(R.string.loggedInUser));
        }

        attachText.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_enter_remittance_record,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.send:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Util.pickMultiPhoto(cxt);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == RESULT_OK && data !=null)
        {
            switch (requestCode){
                case Util.PICK_IMAGE_MULTIPLE:


                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
