package com.example.titomi.workertrackerloginmodule.supervisor.activities;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;

import de.hdodenhof.circleimageview.CircleImageView;import com.example.titomi.workertrackerloginmodule.R;
import com.example.titomi.workertrackerloginmodule.supervisor.Action;
import com.example.titomi.workertrackerloginmodule.supervisor.ConfirmDialog;
import com.example.titomi.workertrackerloginmodule.supervisor.DatabaseAdapter;
import com.example.titomi.workertrackerloginmodule.supervisor.Entity;

import com.example.titomi.workertrackerloginmodule.supervisor.Messages;
import com.example.titomi.workertrackerloginmodule.supervisor.User;
import com.example.titomi.workertrackerloginmodule.supervisor.Task;
import com.example.titomi.workertrackerloginmodule.supervisor.util.DrawableManager;
import com.example.titomi.workertrackerloginmodule.supervisor.util.ImageUtils;
import com.example.titomi.workertrackerloginmodule.supervisor.util.InputValidator;
import com.example.titomi.workertrackerloginmodule.supervisor.util.DateTimeUtil;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Network;
import com.example.titomi.workertrackerloginmodule.supervisor.util.NetworkChecker;
import com.example.titomi.workertrackerloginmodule.supervisor.util.Util;
import static com.example.titomi.workertrackerloginmodule.supervisor.util.Network.backgroundTask;

/**
 * Created by NeonTetras on 13-Feb-18.
 */
public class ActivityViewMessage extends AppCompatActivity implements View.OnClickListener {

    private User user;
    private Context cxt;
    private TextView resend;
    private TextView delete;
    private TextView usernameTv;
    private TextView messageTitleTv;
    private TextView messageBodyTv;
    private TextView dateTimeTv;
    private CircleImageView posterIv;
    private Messages message;
    private ImageView priorityImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_message);
        cxt = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(getIntent() != null){
            Bundle extras = getIntent().getExtras();

            user = (User)extras.getSerializable(getString(R.string.user));
            message = (Messages)extras.getSerializable(getString(R.string.message));

        }

        usernameTv = (TextView)findViewById(R.id.username);
        posterIv = (CircleImageView)findViewById(R.id.user_icon);
        dateTimeTv = (TextView)findViewById(R.id.dateTime);
        messageTitleTv = (TextView)findViewById(R.id.title);
        messageBodyTv = (TextView)findViewById(R.id.messageBody);
        priorityImage = findViewById(R.id.priorityImage);
        resend = (TextView)findViewById(R.id.resend);
        delete = (TextView)findViewById(R.id.delete);

        if(user != null) {
            switch (user.getUserLevel()) {
                case User.NURSE:
                    resend.setVisibility(View.GONE);
                    delete.setVisibility(View.GONE);
                    break;
                case User.SUPERVISOR:
                    break;
            }
        }
        resend.setOnClickListener(this);
        delete.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(getString(R.string.message), message);
        outState.putSerializable(getString(R.string.user), user);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        user = (User)savedInstanceState.getSerializable(getString(R.string.user));
        message = (Messages)savedInstanceState.getSerializable(getString(R.string.message));
    }

    @Override
    public void onClick(View v) {
        String text = "";
        Action action = null;
        switch (v.getId()){
            case R.id.resend:
                action = new Action() {
                    @Override
                    public void negativeActionPerformed() {

                    }

                    @Override
                    public void execute() {
                        resendMessage();
                    }
                };
                text = "Are you sure you want to resend this message?";

                break;
            case R.id.delete:
                action = new Action() {
                    @Override
                    public void negativeActionPerformed() {

                    }

                    @Override
                    public void execute() {
                        deleteMessage();
                    }
                };
                text = "Are you sure you want to delete this message?";
                break;
        }

        ConfirmDialog confirmDialog = new ConfirmDialog(cxt,action);
        confirmDialog.setMessage(text);
        confirmDialog.show();


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

    private void setUpView(){

        messageBodyTv.setText(message.getBody());
        messageTitleTv.setText(message.getTitle());
        usernameTv.setText(Util.toSentenceCase(String.format("%s ",message.getPoster().getName())));
       /* DrawableManager drm = new DrawableManager();

        drm.fetchDrawableOnThread(getString(R.string.server_url) +
                message.getPoster().getFeaturedImage(), posterIv);*/

           ImageUtils.ImageStorage storage = new ImageUtils.ImageStorage(message.getPoster());
           if(message.getFeaturedImage() != null) {
               String imageName = ImageUtils.getImageNameFromUrlWithExtension(message.getFeaturedImage());
               if (storage.imageExists(imageName)) {
                   posterIv.setImageURI(Uri.parse(storage.getImage(imageName).getAbsolutePath()));
               }
           }

        DateFormat dtf = DateFormat.getDateTimeInstance();
        dateTimeTv.setText(dtf.format(message.getDatePosted()));

        if(!message.getPriority().equalsIgnoreCase("high")){
            priorityImage.setVisibility(View.GONE);
        }

        if(!message.isRead()) {
            DatabaseAdapter db = DatabaseAdapter.getInstance(cxt);
           // Toast.makeText(cxt,""+message.getId(),Toast.LENGTH_SHORT).show();
           if(db.readMessage(message.getId())){
               //Toast.makeText(cxt,"Message read",Toast.LENGTH_SHORT).show();
           }
        }

    }


    private void resendMessage(){}
    private void deleteMessage(){
        new AsyncTask<String,Void,String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {
                return null;
            }
        }.execute(getString(R.string.api_url)+getString(R.string.delete_msg_url)+""+message.getId());
    }
}
