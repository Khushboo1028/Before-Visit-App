package com.beforevisit.beforevisit.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.beforevisit.beforevisit.Activities.LoginMainActivity;
import com.beforevisit.beforevisit.R;
import com.beforevisit.beforevisit.utility.DefaultTextConfig;
import com.beforevisit.beforevisit.utility.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ReportIssueFragment extends Fragment {

    public static final String TAG = "ReportIssueFragment";
    View view;

    ImageView img_done;
    EditText et_issue;
    String name, issue;

    AlertDialog.Builder builder;
    AlertDialog alert;

    ListenerRegistration listenerRegistration;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    ProgressBar progressBar;
    Utils utils;

    Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), getActivity());
        view = inflater.inflate(R.layout.fragment_report_issue, container, false);

        init();

        readUserData();
        img_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                issue = et_issue.getText().toString().trim();
                if(utils.isInternetAvailable(getActivity())){

                if (issue.equals("")){
                    builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Please enter all the details")
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    alert = builder.create();
                    alert.setTitle("Incomplete Details!");
                    alert.show();
                }
                else {

                    if (firebaseUser == null) {

                        builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Please Login To Report the Issue")
                                .setCancelable(true)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        Intent intent = new Intent(getContext(), LoginMainActivity.class);
                                        getActivity().startActivity(intent);
                                        getActivity().overridePendingTransition(0, 0);
                                    }
                                });
                        alert = builder.create();
                        alert.setTitle("You are not logged in!");
                        alert.show();


                    } else {
                        sendIssue(name, issue);
                    }
                }
            }
        }
    });


        return view;
    }

    private void init(){
        img_done = (ImageView) view.findViewById(R.id.img_done);
        et_issue = (EditText) view.findViewById(R.id.et_issue);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        utils = new Utils();


    }

    private void readUserData(){

        if(firebaseUser!=null){
            DocumentReference docref = db.collection(mContext.getString(R.string.users)).document(firebaseUser.getUid());

            listenerRegistration = docref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    if(e!=null){
                        Log.i(TAG,"An error occurred "+e.getMessage());
                    }else{

                        if(snapshot!=null && snapshot.exists()){
                            Log.i(TAG,"Data is here ");

                            if(snapshot.get(mContext.getString(R.string.name))!=null){
                                name = snapshot.getString(mContext.getString(R.string.name));

                            }else{
                                name = "";
                            }


                        }
                    }
                }
            });
        }

    }

    private void sendIssue(String name,String issue){

        img_done.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        if(!name.isEmpty()){
            data.put(mContext.getString(R.string.name), name);
        }

        data.put(mContext.getString(R.string.is_hidden), false);
        data.put(mContext.getString(R.string.issue_description),issue);
        data.put(mContext.getString(R.string.userid),firebaseUser.getUid());
        data.put(mContext.getString(R.string.date_created),new Timestamp(new Date()));

        if(firebaseUser!=null) {
            db.collection(mContext.getString(R.string.issues_reported)).document()
                    .set(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            progressBar.setVisibility(View.GONE);
                            img_done.setEnabled(true);
                            builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("We will get back to you shortly!")
                                    .setCancelable(false)
                                    .setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });

                            alert = builder.create();
                            alert.setTitle("Thank you!");
                            alert.show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error writing document", e);
                            progressBar.setVisibility(View.GONE);
                            utils.alertDialog(getActivity(),"Uh-Oh",e.getLocalizedMessage());
                            img_done.setEnabled(true);
                        }
                    });
        }

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();
        if(listenerRegistration!=null){
            listenerRegistration = null;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getContext();
    }
}
