package com.beforevisit.beforevisitapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.beforevisit.beforevisitapp.R;
import com.beforevisit.beforevisitapp.Utility.DefaultTextConfig;

public class LocationDenyActivity extends AppCompatActivity {

    Button btn_give_access, btn_close_app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), LocationDenyActivity.this);
        setContentView(R.layout.activity_location_deny);

        init();

        btn_close_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_give_access.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });

    }

    private void init(){

        btn_give_access = (Button) findViewById(R.id.btn_give_access);
        btn_close_app = (Button) findViewById(R.id.btn_close_app);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
