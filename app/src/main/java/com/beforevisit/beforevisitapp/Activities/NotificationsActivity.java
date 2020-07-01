package com.beforevisit.beforevisitapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.beforevisit.beforevisitapp.R;
import com.beforevisit.beforevisitapp.Utility.DefaultTextConfig;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class NotificationsActivity extends AppCompatActivity {

    public static final String TAG = "NotificationsActivity";
    RelativeLayout allow_all_rel,allow_high_rel,block_all_rel;
    RadioButton allow_all_notif,allow_high_notif, block_all_notif;

    AlertDialog.Builder builder;
    AlertDialog alert;
    ProgressBar progressBar;

    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    String notif_pref = "all";

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
                allowAllSelected();
                showAlert("all");

            }
        });

        allow_high_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowHighSelected();
                showAlert("some");
            }
        });

        block_all_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               blockAllSelected();
                showAlert("blocked");
            }
        });

        allow_all_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowAllSelected();
                showAlert("all");
            }
        });

        allow_high_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allowHighSelected();
                showAlert("some");
            }
        });

        block_all_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blockAllSelected();
                showAlert("blocked");
            }
        });

        ImageView top_logo = (ImageView) findViewById(R.id.top_logo);
        top_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0,0);

            }
        });

        getUserNotificationPref();
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

                        updateNotificationPref(pref);
                    }


                })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (pref){
                    case "all":allow_all_notif.setChecked(false);break;
                    case "some":allow_high_notif.setChecked(false);break;
                    case "blocked":block_all_notif.setChecked(false);break;
                }
                if(notif_pref.equals(getString(R.string.notification_pref_all))){
                    allowAllSelected();
                }else if(notif_pref.equals(getString(R.string.notification_pref_some))){
                    allowHighSelected();
                }else{
                    blockAllSelected();
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

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


    }

    private void getUserNotificationPref(){
        if(firebaseUser!=null){

            listenerRegistration = db.collection(getString(R.string.users)).document(firebaseUser.getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                            if(e!=null){
                                Log.i(TAG,"error in getting notif data "+e.getMessage());
                            }else{
                                if(snapshot.get(getString(R.string.notification_pref)) != null){
                                    notif_pref = snapshot.getString(getString(R.string.notification_pref));
                                    Log.i(TAG,"notif pref is "+notif_pref);

                                    if(notif_pref.equals(getString(R.string.notification_pref_all))){
                                        allowAllSelected();
                                    }else if(notif_pref.equals(getString(R.string.notification_pref_some))){
                                        allowHighSelected();
                                    }else{
                                        blockAllSelected();

                                    }
                                }



                            }
                        }
                    });

        }else{

        }
    }

    private void updateNotificationPref(final String pref) {
        progressBar.setVisibility(View.VISIBLE);
        if(firebaseUser!=null){
            db.collection(getString(R.string.users)).document(firebaseUser.getUid())
                    .update(getString(R.string.notification_pref),pref)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            switch (pref){
                                case "all":allowAllSelected();break;
                                case "some":allowHighSelected();break;
                                case "blocked":blockAllSelected();break;
                            }

                            if(pref.equals("blocked")){
//                                Intent intent = new Intent();
//                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
//
//                                //for Android 5-7
//                                intent.putExtra("app_package", getPackageName());
//                                intent.putExtra("app_uid", getApplicationInfo().uid);
//
//                                // for Android 8 and above
//                                intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
//
//                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(),"Your preferences have been updated",Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG,"Error: "+e.getMessage());
                            Toast.makeText(getApplicationContext(),"Could not update your notification preference. PLease try again later",Toast.LENGTH_LONG).show();
                        }
                    });
        }else{
            progressBar.setVisibility(View.GONE);
            builder = new AlertDialog.Builder(NotificationsActivity.this);
            builder.setMessage("Please Login to update notification preferences")
                    .setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {


                            Intent intent = new Intent(getApplicationContext(), LoginMainActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alert.dismiss();
                        }
                    });
            alert = builder.create();
            alert.setTitle("You are not logged in!");
            alert.show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(listenerRegistration!=null){
            listenerRegistration = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = mAuth.getCurrentUser();
    }
}
