package com.beforevisit.beforevisit.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.beforevisit.beforevisit.Activities.LoginMainActivity;
import com.beforevisit.beforevisit.R;
import com.beforevisit.beforevisit.Utility.DefaultTextConfig;
import com.beforevisit.beforevisit.Utility.Utils;
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

public class FeedbackFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    View view;

    RatingBar ratingBar;
    TextView tv_rating;

    ImageView img_done;
    EditText et_review;
    String review;

    AlertDialog.Builder builder;
    AlertDialog alert;
    ImageView img_signout,img_notification_bell;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    String rating = "",name;
    ListenerRegistration listenerRegistration;
    Utils utils;
    ProgressBar progressBar;

    Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), mContext);

        view = inflater.inflate(R.layout.fragment_feedback, container, false);
        Log.i(TAG,"In Feedback Fragment");

        init();

        readUserData();
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                tv_rating.setText((int)v + "/5");
                rating = String.valueOf(v);
            }
        });

        img_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"img_done pressed");
                review = et_review.getText().toString().trim();

                if(utils.isInternetAvailable(getActivity())) {
                    if(firebaseUser == null){

                        builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("Please Login To Provide Feedback")
                                .setCancelable(true)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        Intent intent = new Intent(getContext(), LoginMainActivity.class);
                                        mContext.startActivity(intent);
                                        getActivity().overridePendingTransition(0, 0);
                                    }
                                });
                        alert = builder.create();
                        alert.setTitle("You are not logged in!");
                        alert.show();
                    }
                    else if(review.isEmpty() && rating.isEmpty()){
                        utils.alertDialog(getActivity(),"Uh-Oh","Please complete all details");
                    }
                    else if(rating.isEmpty()){
                        utils.alertDialog(getActivity(),"Uh-Oh","Please give a rating to continue!");
                    }
                    else if (review.equals("")) {
                        builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("You didn't give the review. Do you want to proceed?")
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        sendReview(name, rating, review);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                        et_review.requestFocus();
                                    }
                                });

                        alert = builder.create();
                        alert.setTitle("Review Missing!");
                        alert.show();
                    }else{
                        sendReview(name, rating, review);
                    }
                }
            }
        });





        return view;
    }

    private void init(){
        img_done = (ImageView) view.findViewById(R.id.img_done);

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

        tv_rating = (TextView) view.findViewById(R.id.tv_rating);

        et_review = (EditText) view.findViewById(R.id.et_review);

        img_signout = (ImageView) getActivity().findViewById(R.id.img_signout);
        img_signout.setVisibility(View.INVISIBLE);

        img_notification_bell = (ImageView) getActivity().findViewById(R.id.img_notification_bell);
        img_notification_bell.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        utils = new Utils();
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

    }

    private void sendReview(String name,String rating, String review ){

        img_done.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        if(!name.isEmpty()){
            data.put(mContext.getString(R.string.name), name);
        }

        if(rating!=null && !rating.isEmpty()){
            data.put(mContext.getString(R.string.rating),rating);
        }

        if(review!=null && !review.isEmpty()){
            data.put(mContext.getString(R.string.review),review);
        }

        data.put(mContext.getString(R.string.userid),firebaseUser.getUid());
        data.put(mContext.getString(R.string.date_created),new Timestamp(new Date()));

       if(firebaseUser!=null) {
           db.collection(mContext.getString(R.string.feedback)).document()
                   .set(data)
                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           Log.d(TAG, "DocumentSnapshot successfully written!");
                           progressBar.setVisibility(View.GONE);
                           img_done.setEnabled(true);
                           builder = new AlertDialog.Builder(mContext);
                           builder.setMessage("Your feedback is valuable to us!")
                                   .setCancelable(false)
                                   .setPositiveButton("Okay!",null);
                           alert = builder.create();
                           alert.setTitle("Thank You!");
                           alert.show();

                           et_review.setText("");
                           ratingBar.setRating(0);
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
