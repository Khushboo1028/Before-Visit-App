package com.beforevisit.beforevisit.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.beforevisit.beforevisit.R;
import com.beforevisit.beforevisit.Utility.DefaultTextConfig;

public class NotificationsActivity extends AppCompatActivity {

    public static final String TAG = "NotificationsActivity";
    RelativeLayout allow_all_rel,allow_high_rel,block_all_rel;
    RadioButton allow_all_notif,allow_high_notif, block_all_notif;

    AlertDialog.Builder builder;
    AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), NotificationsActivity.this);
        setContentView(R.layout.activity_notifications);

        init();

        allow_all_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allow_all_notif.setChecked(true);
            }
        });

        allow_high_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allow_high_notif.setChecked(true);
            }
        });

        block_all_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                block_all_notif.setChecked(true);
            }
        });

        allow_all_notif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    showAlert("all");
                }
            }
        });

        allow_high_notif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    showAlert("high");
                }
            }
        });

        block_all_notif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    showAlert("no");
                }
            }
        });
    }

    private void allowAllSelected(){
        allow_all_notif.setChecked(true);
        allow_high_notif.setChecked(false);
        block_all_notif.setChecked(false);

    }

    private void allowHighSelected(){
        allow_all_notif.setChecked(false);
        allow_high_notif.setChecked(true);
        block_all_notif.setChecked(false);

    }

    private void blockAllSelected(){
        allow_all_notif.setChecked(false);
        allow_high_notif.setChecked(false);
        block_all_notif.setChecked(true);

    }

    private void showAlert(final String pref){
        builder = new AlertDialog.Builder(NotificationsActivity.this);
        builder.setMessage("Do you want to update your preferences?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // UPDATE PREFRENCES
                        switch (pref){
                            case "all":allowAllSelected();break;
                            case "high":allowHighSelected();break;
                            case "no":blockAllSelected();break;
                        }
                    }
                })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (pref){
                    case "all":allow_all_notif.setChecked(false);break;
                    case "high":allow_high_notif.setChecked(false);break;
                    case "no":block_all_notif.setChecked(false);break;
                }
                dialogInterface.cancel();
            }
        });
        alert = builder.create();
        alert.setTitle("Update Preferences");
        alert.show();
    }



    private void init(){

        allow_all_rel = (RelativeLayout) findViewById(R.id.allow_all_rel);
        allow_high_rel = (RelativeLayout) findViewById(R.id.allow_high_rel);
        block_all_rel = (RelativeLayout) findViewById(R.id.block_all_rel);

        allow_all_notif = (RadioButton) findViewById(R.id.allow_all_notif);
        allow_high_notif = (RadioButton) findViewById(R.id.allow_high_notif);
        block_all_notif = (RadioButton) findViewById(R.id.block_all_notif);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
