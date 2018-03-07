package com.example.titomi.workertrackerloginmodule.inventory_module.inventory_sub_menu;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;

import java.util.ArrayList;
import java.util.List;

public class ActivityAddRemit extends AppCompatActivity implements View.OnClickListener {

    private static final int MAX_IMAGE = 6;
    User loggedInUser;
    EditText amountText;
    TextView attachText;
    Context cxt;
    LinearLayout proofMediaLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cxt = this;
        setContentView(R.layout.activity_add_remit);
        amountText = findViewById(R.id.amountEdit);
        attachText = findViewById(R.id.attach);
        proofMediaLayout = findViewById(R.id.proofMediaLayout);
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

        if(resultCode == RESULT_OK && data !=null)
        {
            switch (requestCode){
                case Util.PICK_IMAGE_MULTIPLE:
                    String[] imagesPath = data.getStringExtra("data").split("\\|");
                    for (int i = 0; i < imagesPath.length; i++) {
                        if (!canTakeImages()) {
                            Toast.makeText(this,
                                    String.format("Can only accept a maximum of %d images"
                                            , MAX_IMAGE), Toast.LENGTH_LONG).show();
                            break;
                        }
                        if (i >= MAX_IMAGE) {
                            Toast.makeText(this,
                                    String.format("Can only accept a maximum of %d images"
                                            , MAX_IMAGE), Toast.LENGTH_LONG).show();
                            break;
                        }

                        proofImages.add(imagesPath[i]);


                    }


                    loadFeaturedImages(proofImages);

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean canTakeImages(){
        return proofImages.size() < MAX_IMAGE;
    }

    private void  loadFeaturedImages(final ArrayList<String> imageList){



        proofMediaLayout.removeAllViews();


        for(final String path : imageList) {


            // featuredImages.add(path); //and new image to list
            final View view = View.inflate(cxt,R.layout.report_image_single_item_with_delete_button,null);
            final ImageView image =view.findViewById(R.id.reportImage);
            ImageView delete = view.findViewById(R.id.delete);
            image.setImageURI(Uri.parse(path));
            image.setTag(path);


            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            //image.setLeft(5);
            image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Util.viewImages(cxt,image,imageList);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    proofMediaLayout.removeView(view);
                    imageList.remove(path);
                }
            });


            proofMediaLayout.addView(view);
        }

    }

    ArrayList<String> proofImages = new ArrayList<>();
}
