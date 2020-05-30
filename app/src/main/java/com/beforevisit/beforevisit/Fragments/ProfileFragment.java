package com.beforevisit.beforevisit.Fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beforevisit.beforevisit.Activities.LoginMainActivity;
import com.beforevisit.beforevisit.Adapters.InterestsAdapter;
import com.beforevisit.beforevisit.Model.Interests;
import com.beforevisit.beforevisit.R;
import com.beforevisit.beforevisit.utility.DefaultTextConfig;
import com.beforevisit.beforevisit.utility.Utils;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment{
    public static final String TAG = "ProfileFragment";
    View view;

    RelativeLayout main_rel, dob_rel;
    EditText et_name, et_email, et_mobile, et_address, et_dob;
    String name, mobile, address, dob, gender;
    ImageView img_done;
    TextView tv_error_email,tv_error_mobile,tv_dob,tv_profile_percent;

    AlertDialog.Builder builder;
    AlertDialog alert;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    RadioGroup gender_group;
    RadioButton gender_female, gender_male, gender_other;
    ImageView img_calender;

    ListenerRegistration listenerRegistration;
    Utils utils;
    ImageView img_signout,img_notification_bell;

    RecyclerView recyclerViewInterest;
    InterestsAdapter interestsAdapter;

    ArrayList<Interests> interestsList;
    ArrayList<String> usersUpdatedInterestsList;
    ArrayList<String> usersCurrentInterestsList;

    FlexboxLayoutManager manager;
    ProgressBar profile_progress_bar;

    Context mContext;
    double profile_percent;


    int completed_count=7;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), getActivity());

        view = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.i(TAG,"In Profile Fragment");

        init();
        if(getActivity()!=null){
            readUserData();
            getInterests();
        }


        main_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(main_rel.getWindowToken(), 0);
            }
        });

        tv_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateAndTimePicker();
            }
        });


        img_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_error_email.setVisibility(View.GONE);

                dob = tv_dob.getText().toString();
                name = et_name.getText().toString().trim();
                mobile = et_mobile.getText().toString().trim();
                address = et_address.getText().toString().trim();

                if(gender_group.getCheckedRadioButtonId()==-1)
                {
                    gender = "";
                }else if(gender_female.isChecked()){
                    gender = getString(R.string.gender_female);
                }else if(gender_male.isChecked()){
                    gender = getString(R.string.gender_female);
                }else{
                    gender = getString(R.string.gender_other);
                }

                usersUpdatedInterestsList = interestsAdapter.getUsersInterestsList();

                if(utils.isInternetAvailable(getActivity())) {
                    if (name.equals("") || mobile.equals("")) {
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
                    } else if (mobile.length() != 10) {
                        tv_error_mobile.setVisibility(View.VISIBLE);
                        et_mobile.requestFocus();
                        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(500);
                    } else {
                        Log.i(TAG, "Gender is " + gender);
                        Log.i(TAG, "DOB is " + dob);
                        builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Please check the details. These will be updated.")
                                .setCancelable(true)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        updateUserData(address, dob, gender, mobile,name, usersUpdatedInterestsList);
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

        img_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"SignOut Clicked");
                builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to logout?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                LoginMainActivity loginMainActivity = new LoginMainActivity();
                                loginMainActivity.signOut();
                                Toast.makeText(getContext(),"Sign Out Successful",Toast.LENGTH_LONG).show();

                                getActivity().onBackPressed();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alert.dismiss();
                    }
                });
                alert = builder.create();
                alert.setTitle("Logout!");
                alert.show();




            }
        });










        return view;
    }


    private void init(){
        main_rel = (RelativeLayout) view.findViewById(R.id.main_rel);
        dob_rel = (RelativeLayout) view.findViewById(R.id.dob_rel);

        et_name = (EditText) view.findViewById(R.id.et_name);
        et_email = (EditText) view.findViewById(R.id.et_email);
        et_mobile = (EditText) view.findViewById(R.id.et_mobile);
        et_address = (EditText) view.findViewById(R.id.et_address);
        tv_dob = (TextView) view.findViewById(R.id.tv_dob);


        gender_group = (RadioGroup) view.findViewById(R.id.gender_group);
        gender_female = (RadioButton) view.findViewById(R.id.gender_female);
        gender_male = (RadioButton) view.findViewById(R.id.gender_male);
        gender_other = (RadioButton) view.findViewById(R.id.gender_other);


        img_done = (ImageView) view.findViewById(R.id.img_done);

        img_calender = (ImageView) view.findViewById(R.id.img_calender);
        tv_error_email = (TextView) view.findViewById(R.id.tv_error_email);
        tv_error_mobile = (TextView) view.findViewById(R.id.tv_error_mobile);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        utils = new Utils();


        img_signout = (ImageView) getActivity().findViewById(R.id.img_signout);
        img_signout.setVisibility(View.VISIBLE);

        img_notification_bell = (ImageView) getActivity().findViewById(R.id.img_notification_bell);
        img_notification_bell.setVisibility(View.GONE);

        interestsList = new ArrayList<>();
        usersCurrentInterestsList = new ArrayList<>();
        usersUpdatedInterestsList = new ArrayList<>();
        recyclerViewInterest = (RecyclerView) view.findViewById(R.id.recycler_view_interest);
        recyclerViewInterest.setHasFixedSize(true);


        manager = new FlexboxLayoutManager(getContext());
        manager.setFlexDirection(FlexDirection.ROW);
        manager.setJustifyContent(JustifyContent.SPACE_EVENLY);
        manager.setFlexWrap(FlexWrap.WRAP);
        recyclerViewInterest.setLayoutManager(manager);

        interestsAdapter = new InterestsAdapter(getContext(),interestsList);
        recyclerViewInterest.setAdapter(interestsAdapter);

        profile_progress_bar = (ProgressBar) view.findViewById(R.id.profile_progress_bar);
        tv_profile_percent = (TextView) view.findViewById(R.id.tv_profile_percent);



    }

    private void showDateAndTimePicker(){
        final Calendar currentDate = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                currentDate.set(year, monthOfYear, dayOfMonth);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                dob = dateFormat.format(currentDate.getTime());
                tv_dob.setText(dob);


            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
        datePickerDialog.getDatePicker().setMaxDate(currentDate.getTimeInMillis());
        datePickerDialog.show();
    }

    private void updateUserData(String address, String dob, String gender, String mobile, String name, ArrayList<String> usersInterestsList){

        Map<String, Object> data = new HashMap<>();
        data.put(mContext.getString(R.string.date_created),new Timestamp(new Date()));

        if(!address.isEmpty()){
            data.put(mContext.getString(R.string.address), address);
        }

        if(!name.isEmpty()){
            data.put(mContext.getString(R.string.name), name);
        }

        if(!dob.isEmpty()){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            try {
                Date dob_ts = dateFormat.parse(dob);
                data.put(mContext.getString(R.string.dob), new Timestamp(dob_ts));
            } catch (ParseException e) {
                Log.i(TAG,e.getLocalizedMessage());
            }


        }

        if(!gender.isEmpty()){
            data.put(mContext.getString(R.string.gender),gender);
        }

        if(!mobile.isEmpty()){
            data.put(mContext.getString(R.string.mobile_no),mobile);
        }

        if(usersInterestsList!=null && !usersInterestsList.isEmpty()){
            data.put(mContext.getString(R.string.interests),usersInterestsList);
        }


        db.collection(mContext.getString(R.string.users)).document(firebaseUser.getUid())
                .update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Your Profile has been updated!")
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getActivity().onBackPressed();
                            }
                        });
                alert = builder.create();
                alert.setTitle("Success!");
                alert.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);

                builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(e.getLocalizedMessage())
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alert.dismiss();
                            }
                        });
                alert = builder.create();
                alert.setTitle("Uh -Oh!");
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

                            if(getActivity()!=null) {

                                if (snapshot.get(mContext.getString(R.string.name)) != null) {
                                    name = snapshot.getString(mContext.getString(R.string.name));
                                    et_name.setText(name);
                                    if(name.isEmpty()){
                                        completed_count=completed_count-1;
                                    }
                                }else{
                                    completed_count=completed_count-1;
                                }


                                if (snapshot.get(mContext.getString(R.string.mobile_no)) != null) {
                                    mobile = snapshot.getString(mContext.getString(R.string.mobile_no));
                                    et_mobile.setText(mobile);
                                    if(mobile.isEmpty()){
                                        completed_count=completed_count-1;
                                    }
                                }else{
                                    completed_count=completed_count-1;
                                }

                                if (snapshot.get(mContext.getString(R.string.dob)) != null) {
                                    Timestamp dob_ts = snapshot.getTimestamp(mContext.getString(R.string.dob));
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                                    dob = dateFormat.format(dob_ts.toDate());
                                    tv_dob.setText(dob);
                                }else{
                                    completed_count=completed_count-1;
                                }

                                if (snapshot.get(mContext.getString(R.string.address)) != null) {
                                    address = snapshot.getString(mContext.getString(R.string.address));
                                    et_address.setText(address);

                                    if(address.isEmpty()){
                                        completed_count=completed_count-1;
                                    }
                                }else{
                                    completed_count=completed_count-1;
                                }

                                if (snapshot.get(mContext.getString(R.string.interests)) != null) {
                                    usersCurrentInterestsList = (ArrayList<String>) snapshot.get(mContext.getString(R.string.interests));
                                    if(usersCurrentInterestsList.isEmpty()){
                                        completed_count=completed_count-1;
                                    }

                                }else{
                                    completed_count=completed_count-1;
                                }

                                if (snapshot.get(mContext.getString(R.string.gender)) != null) {
                                    gender = snapshot.getString(mContext.getString(R.string.gender));


                                    if (gender.equals(mContext.getString(R.string.gender_female))) {
                                        gender_female.setChecked(true);
                                    } else if (gender.equals(mContext.getString(R.string.gender_male))) {
                                        gender_male.setChecked(true);

                                    } else {
                                        gender_other.setChecked(true);
                                    }

                                }else{
                                    completed_count=completed_count-1;
                                }


                                profile_percent = Math.round((completed_count/7.0) * 100);
                                int progress = Integer.parseInt(String.format("%.0f", profile_percent));
                                profile_progress_bar.setProgress(progress);
                                if(progress == 100){
                                    tv_profile_percent.setText("Profile Complete");
                                }else{
                                    tv_profile_percent.setText(String.format("%.0f", profile_percent) + " %");
                                }
                                interestsAdapter.notifyDataSetChanged();
                            }

                        }
                    }
                }
            });


        }

    }

    private void getInterests(){
        if(getActivity()!=null) {
            listenerRegistration = db.collection(mContext.getString(R.string.interests))
                    .orderBy(getString(R.string.name))
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {

                            } else {
                                interestsList.clear();
                                String name;
                                for (final QueryDocumentSnapshot doc : snapshots) {
                                    if (doc.getString(mContext.getString(R.string.name)) != null) {
                                        name = doc.getString(mContext.getString(R.string.name));
                                    } else {
                                        name = "";
                                    }

                                    interestsList.add(new Interests(doc.getId(), name));
                                }

                                interestsAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if(firebaseUser!=null){
            et_email.setText(firebaseUser.getEmail());
            et_email.setEnabled(false);
        }else{
            if(firebaseUser == null){

                getActivity().onBackPressed();

                Intent intent = new Intent(getContext(), LoginMainActivity.class);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(0,0);
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        img_signout.setVisibility(View.GONE);
        img_notification_bell.setVisibility(View.VISIBLE);
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
