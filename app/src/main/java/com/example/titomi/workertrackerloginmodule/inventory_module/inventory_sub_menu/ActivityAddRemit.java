package com.example.titomi.workertrackerloginmodule.inventory_module.inventory_sub_menu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.User;

public class ActivityAddRemit extends AppCompatActivity {

    User loggedInUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remit);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(getIntent().getExtras() != null){
            loggedInUser = (User)getIntent().getExtras().getSerializable(getString(R.string.loggedInUser));
        }


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
}
