package com.beforevisit.beforevisit.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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

public class ListBusinessFragment extends Fragment {
    public static final String TAG = "ListBusinessFragment";
    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;

    View view;

    RelativeLayout main_rel;
    EditText et_shop_name, et_name, et_email, et_mobile, et_address;
    String shop_name, owner_name, email, mobile, address;
    ImageView img_done;
    TextView tv_error_email,tv_error_mobile;

    AlertDialog.Builder builder;
    AlertDialog alert;

    Utils utils;
    ListenerRegistration listenerRegistration;
    ImageView img_signout,img_notification_bell;
    
    Context mContext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), getActivity());

        view = inflater.inflate(R.layout.fragment_business_list, container, false);

        init();
        readUserData();

        main_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(main_rel.getWindowToken(), 0);
            }
        });

        img_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_error_email.setVisibility(View.GONE);
                tv_error_mobile.setVisibility(View.GONE);

                shop_name = et_shop_name.getText().toString().trim();
                owner_name = et_name.getText().toString().trim();
                email = et_email.getText().toString().trim();
                mobile = et_mobile.getText().toString().trim();
                address = et_address.getText().toString().trim();

                if(utils.isInternetAvailable(getActivity())) {
                    if (shop_name.equals("") || owner_name.equals("") || email.equals("") || mobile.equals("")) {
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
                    } else if (!isValidEmailAddress(email)) {
                        tv_error_email.setVisibility(View.VISIBLE);
                        et_email.requestFocus();
                        et_email.setBackgroundResource(R.drawable.red_border_rectangle);
                        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(500);
                    } else if (mobile.length() != 10) {
                        tv_error_mobile.setVisibility(View.VISIBLE);
                        et_mobile.requestFocus();
                        et_mobile.setBackgroundResource(R.drawable.red_border_rectangle);
                        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(500);
                    } else {
                        builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Please check the details. These will be sent to the admin.")
                                .setCancelable(true)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        sendBusinessData();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                });

                        alert = builder.create();
                        alert.setTitle("Confirm Your Details!");
                        alert.show();
                    }
                }
            }
        });


        return view;
    }

    private void init(){
        main_rel = (RelativeLayout) view.findViewById(R.id.main_rel);

        et_name = (EditText) view.findViewById(R.id.et_name);
        et_email = (EditText) view.findViewById(R.id.et_email);
        et_mobile = (EditText) view.findViewById(R.id.et_mobile);
        et_address = (EditText) view.findViewById(R.id.et_address);
        et_shop_name = (EditText) view.findViewById(R.id.et_shop_name);

        img_done = (ImageView) view.findViewById(R.id.img_done);

        tv_error_email = (TextView) view.findViewById(R.id.tv_error_email);
        tv_error_mobile = (TextView) view.findViewById(R.id.tv_error_mobile);
        utils = new Utils();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        img_signout = (ImageView) getActivity().findViewById(R.id.img_signout);
        img_signout.setVisibility(View.INVISIBLE);

        img_notification_bell = (ImageView) getActivity().findViewById(R.id.img_notification_bell);
        img_notification_bell.setVisibility(View.VISIBLE);
    }

    public static boolean isValidEmailAddress(String email) {
        boolean stricterFilter = true;
        String stricterFilterString = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
        String laxString = ".+@.+\\.[A-Za-z]{2}[A-Za-z]*";
        String emailRegex = stricterFilter ? stricterFilterString : laxString;
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(emailRegex);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    private void sendBusinessData(){

        Map<String, Object> data = new HashMap<>();
        data.put(mContext.getString(R.string.name), owner_name);
        data.put(mContext.getString(R.string.date_created),new Timestamp(new Date()));
        data.put(mContext.getString(R.string.business_email), email);
        data.put(mContext.getString(R.string.business_mobile_no),mobile);
        data.put(mContext.getString(R.string.business_address),address);
        data.put(mContext.getString(R.string.shop_name),shop_name);
        data.put(mContext.getString(R.string.is_hidden),false);
        data.put(mContext.getString(R.string.userid),firebaseUser.getUid());


        db.collection(mContext.getString(R.string.business_requests))
                .document().set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("We have received your request! We will get back to you shortly! ")
                                .setCancelable(true)
                                .setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });


                        alert = builder.create();
                        alert.setTitle("Success!");
                        alert.show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(e.getLocalizedMessage())
                        .setCancelable(true)
                        .setPositiveButton("Okay!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alert.dismiss();
                            }
                        });


                alert = builder.create();
                alert.setTitle("Uh - Oh!");
                alert.show();
            }
        });
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
                                owner_name = snapshot.getString(mContext.getString(R.string.name));
                                et_name.setText(owner_name);
                            }

                            if(snapshot.get(mContext.getString(R.string.mobile_no))!=null){
                                mobile = snapshot.getString(mContext.getString(R.string.mobile_no));
                                et_mobile.setText(mobile);
                            }


                            if(snapshot.get(mContext.getString(R.string.address))!=null){
                                address = snapshot.getString(mContext.getString(R.string.address));
                                et_address.setText(address);
                            }

                            et_email.setText(firebaseUser.getEmail());



                        }
                    }
                }
            });
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        if(firebaseUser == null){

            getActivity().onBackPressed();
            Intent intent = new Intent(getContext(), LoginMainActivity.class);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(0,0);

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
