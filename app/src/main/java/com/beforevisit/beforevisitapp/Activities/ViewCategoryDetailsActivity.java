package com.beforevisit.beforevisitapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.beforevisit.beforevisitapp.Adapters.GridViewCategoryDetailsAdapter;
import com.beforevisit.beforevisitapp.Model.CategoryPlaces;
import com.beforevisit.beforevisitapp.R;
import com.beforevisit.beforevisitapp.Utility.DefaultTextConfig;
import com.beforevisit.beforevisitapp.Utility.DisctanceCalculator;
import com.beforevisit.beforevisitapp.Utility.ExpandableHeightGridView;
import com.beforevisit.beforevisitapp.Utility.LocationTrack;
import com.beforevisit.beforevisitapp.Utility.SortingClassCategory;
import com.beforevisit.beforevisitapp.Utility.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class ViewCategoryDetailsActivity extends AppCompatActivity {

    public static final String TAG = "ViewCategoryDetailsActi";
    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 69;
    public static final int REQUEST_CHECK_SETTINGS = 23;
    ExpandableHeightGridView gridView;
    GridViewCategoryDetailsAdapter gridCategoryAdapter;
    ArrayList<CategoryPlaces> categoryPlacesArrayList;
    ImageView img_top,img_icon;
    String category_id,icon_name,icon_url;
    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;
    Utils utils;
    NestedScrollView nestedScrollView;
    TextView tv_header;

    LocationTrack locationTrack;
    double user_longitude,user_latitude;
    EditText et_search;
    RelativeLayout go_to_top_rel;

    Boolean isSaved;

    String store_name;
    String address;
    String image_url;
    float rating;

    double latitude,longitude;

    ArrayList<String> places_saved;

    Spinner spinner;
    ArrayList<String> filterList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ViewCategoryDetailsActivity.this);
        setContentView(R.layout.activity_view_catagory_details);

        init();

        getSavedPlaces();

        img_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nestedScrollView.fullScroll(View.FOCUS_UP);
            }
        });




        if(icon_name!=null){
            tv_header.setText(icon_name);
        }

        if(icon_url!=null){
            Glide.with(getApplicationContext()).load(icon_url).into(img_icon);
        }

        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ViewCategoryDetailsActivity.this, SearchActivity.class);
                View sharedView = et_search;
                String transitionName = getString(R.string.search_transition);

                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(ViewCategoryDetailsActivity.this, sharedView, transitionName);
                startActivity(intent, transitionActivityOptions.toBundle());
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

        //adding spinner

        filterList.add(getString(R.string.nearest));
        filterList.add(getString(R.string.newest));
        filterList.add(getString(R.string.trending));
        filterList.add(getString(R.string.popular));
        filterList.add(getString(R.string.sponsored));


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner_filter, filterList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected_filter =  spinner.getSelectedItem().toString();

                if(selected_filter.equals(getString(R.string.newest))){
                    getFilteredData(getString(R.string.newest));

                }else if(selected_filter.equals(getString(R.string.trending))){
                    getFilteredData(getString(R.string.trending));

                }else if(selected_filter.equals(getString(R.string.popular))){
                    getFilteredData(getString(R.string.popular));

                }else if(selected_filter.equals(getString(R.string.sponsored))){
                    getFilteredData(getString(R.string.sponsored));

                }else{
                     getNearestData();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

    }

    private void init(){
        categoryPlacesArrayList = new ArrayList<>();
        filterList = new ArrayList<>();
        places_saved = new ArrayList<>();
        gridView = (ExpandableHeightGridView) findViewById(R.id.grid_view);
        gridView.setExpanded(true);
        gridCategoryAdapter = new GridViewCategoryDetailsAdapter(categoryPlacesArrayList, ViewCategoryDetailsActivity.this);
        gridView.setAdapter(gridCategoryAdapter);

        category_id = getIntent().getStringExtra("category_id");
        icon_name = getIntent().getStringExtra("icon_name");
        icon_url = getIntent().getStringExtra("icon_url");
        utils = new Utils();

        spinner = (Spinner) findViewById(R.id.spinner);

        db = FirebaseFirestore.getInstance();

        go_to_top_rel = (RelativeLayout) findViewById(R.id.go_to_top_rel);
        go_to_top_rel.setVisibility(View.GONE);

        img_top = (ImageView) findViewById(R.id.img_top);
        img_icon = (ImageView) findViewById(R.id.img_head);
        nestedScrollView = (NestedScrollView) findViewById(R.id.scrollView);
        tv_header = (TextView) findViewById(R.id.tv_header);
        et_search = (EditText) findViewById(R.id.et_search);
        et_search.setFocusable(false);


    }


    private void getNearestData(){
        if(category_id!=null && !category_id.isEmpty()){
            Log.i(TAG,"Category id is "+category_id);
            if (ContextCompat.checkSelfPermission(ViewCategoryDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(ViewCategoryDetailsActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                } else {
                    ActivityCompat.requestPermissions(ViewCategoryDetailsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                }
            }else{
                displayLocationSettingsRequest(getApplicationContext());
                locationTrack = new LocationTrack(getApplicationContext());
                if (locationTrack.canGetLocation()) {

                    user_latitude = locationTrack.getLatitude();
                    user_longitude = locationTrack.getLongitude();

                }
            }


        }
    }


    private void getPlacesFromCategory(final String category_id){

        listenerRegistration = db.collection(getString(R.string.places))
                .whereEqualTo(getString(R.string.category_id),category_id)
                .orderBy(getString(R.string.date_created), Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                        if(e!=null){
                            Log.i(TAG,"Error in getting places from category: "+e.getMessage());
                        }else{

                            if(snapshots!=null && !snapshots.isEmpty()){


                                categoryPlacesArrayList.clear();
                                for (final QueryDocumentSnapshot doc : snapshots) {

                                    if(doc.get(getString(R.string.category_id))!=null){
                                        if(!doc.getString(getString(R.string.category_id)).isEmpty()) {

                                        }
                                    }


                                    if(doc.getString(getString(R.string.place_name))!=null){
                                        store_name = doc.getString(getString(R.string.place_name));

                                    }else{
                                        store_name = "";
                                    }


                                    if(doc.getString(getString(R.string.address))!=null){
                                        address = doc.getString(getString(R.string.address));

                                    }else{
                                        address = "";
                                    }

                                    if(doc.getString(getString(R.string.home_image_url))!=null){
                                        image_url = doc.getString(getString(R.string.home_image_url));

                                    }else{
                                        image_url = "";
                                    }

                                    if(doc.get(getString(R.string.avg_rating))!=null){
                                        rating = Float.parseFloat(doc.get(getString(R.string.avg_rating)).toString());


                                    }else{
                                        rating = 0;
                                    }

                                    if(doc.get(getString(R.string.latitude))!=null){
                                        latitude = doc.getDouble(getString(R.string.latitude));
                                    }else{
                                        latitude = 0.0;
                                    }

                                    if(doc.get(getString(R.string.longitude))!=null){
                                        longitude = doc.getDouble(getString(R.string.longitude));
                                    }else{
                                        longitude = 0.0;
                                    }



                                    isSaved = false;
                                    if(places_saved.contains(doc.getId())){
                                        isSaved = true;
                                    }else{
                                        isSaved = false;
                                    }


                                    float results[] = new float[1];
//                                    DisctanceCalculator disctanceCalculator = new DisctanceCalculator();
//                                    double distanceFromCurrentLocation = disctanceCalculator.distance(user_latitude, user_longitude,latitude , longitude, "K");

                                    android.location.Location.distanceBetween(user_latitude,user_longitude,latitude,longitude,results);
                                    double distanceFromCurrentLocation = results[0];


                                    categoryPlacesArrayList.add(new CategoryPlaces(
                                            store_name,
                                            address,
                                            image_url,
                                            rating,
                                            isSaved,
                                            doc.getId(),
                                            latitude,
                                            longitude,
                                            distanceFromCurrentLocation
                                    ));


                                    SortingClassCategory sortingClass = new SortingClassCategory(categoryPlacesArrayList);
                                    Collections.reverse(sortingClass.sortDistanceLowToHigh());




                                    gridCategoryAdapter.notifyDataSetChanged();









                                }

                                if(categoryPlacesArrayList.size() > 4){
                                    go_to_top_rel.setVisibility(View.VISIBLE);
                                }


                            }else{
                                Log.i(TAG,"There are no places under this category!");
                            }
                        }
                    }
                });

    }

    private void getFilteredData(String filter_name){
        Query query;
        if(filter_name.equals(getString(R.string.trending))){

            query = db.collection(getString(R.string.places))
                    .whereEqualTo(getString(R.string.category_id),category_id)
                    .orderBy(getString(R.string.visitor_count), Query.Direction.DESCENDING);

        }else if(filter_name.equals(getString(R.string.popular))){

            query = db.collection(getString(R.string.places))
                    .whereEqualTo(getString(R.string.category_id),category_id)
                    .orderBy(getString(R.string.saved_count), Query.Direction.DESCENDING);

        }else if(filter_name.equals(getString(R.string.sponsored))){
            query = db.collection(getString(R.string.places))
                    .whereEqualTo(getString(R.string.category_id),category_id)
                    .whereEqualTo(getString(R.string.is_sponsored),true)
                    .orderBy(getString(R.string.date_created), Query.Direction.DESCENDING);
        }else{
            query = db.collection(getString(R.string.places))
                    .whereEqualTo(getString(R.string.category_id),category_id)
                    .orderBy(getString(R.string.date_created), Query.Direction.DESCENDING);
        }

       listenerRegistration= query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.i(TAG,"Query error: "+e.getMessage());
                }else{

                    if(snapshots!=null && !snapshots.isEmpty()){


                        categoryPlacesArrayList.clear();
                        for (final QueryDocumentSnapshot doc : snapshots) {

                            if(doc.get(getString(R.string.category_id))!=null){
                                if(!doc.getString(getString(R.string.category_id)).isEmpty()) {

                                }
                            }


                            if(doc.getString(getString(R.string.place_name))!=null){
                                store_name = doc.getString(getString(R.string.place_name));

                            }else{
                                store_name = "";
                            }


                            if(doc.getString(getString(R.string.address))!=null){
                                address = doc.getString(getString(R.string.address));

                            }else{
                                address = "";
                            }

                            if(doc.getString(getString(R.string.home_image_url))!=null){
                                image_url = doc.getString(getString(R.string.home_image_url));

                            }else{
                                image_url = "";
                            }

                            if(doc.get(getString(R.string.avg_rating))!=null){
                                rating = Float.parseFloat(doc.get(getString(R.string.avg_rating)).toString());


                            }else{
                                rating = 0;
                            }

                            if(doc.get(getString(R.string.latitude))!=null){
                                latitude = doc.getDouble(getString(R.string.latitude));
                            }else{
                                latitude = 0.0;
                            }

                            if(doc.get(getString(R.string.longitude))!=null){
                                longitude = doc.getDouble(getString(R.string.longitude));
                            }else{
                                longitude = 0.0;
                            }



                            isSaved = false;
                            if(places_saved.contains(doc.getId())){
                                isSaved = true;
                            }else{
                                isSaved = false;
                            }


                            categoryPlacesArrayList.add(new CategoryPlaces(
                                    store_name,
                                    address,
                                    image_url,
                                    rating,
                                    isSaved,
                                    doc.getId(),
                                    latitude,
                                    longitude,
                                    0
                            ));


                            gridCategoryAdapter.notifyDataSetChanged();



                        }

                        if(categoryPlacesArrayList.size() > 4){
                            go_to_top_rel.setVisibility(View.VISIBLE);
                        }


                    }else{
                        Log.i(TAG,"There are no places under this category!");
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION:

                if (grantResults!=null && grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    displayLocationSettingsRequest(getApplicationContext());
                    locationTrack = new LocationTrack(getApplicationContext());
                    if (locationTrack.canGetLocation()) {

                        user_latitude = locationTrack.getLatitude();
                        user_longitude = locationTrack.getLongitude();

                        if(category_id!=null){
                            Log.i(TAG,"Running from onPermissions");
                            getPlacesFromCategory(category_id);

                        }

                    }
                    return;

                }else{

                    utils.alertDialog(ViewCategoryDetailsActivity.this,"Uh-Oh","Seems like you have denied permission. The app cannot function normally");

                }

                break;




        }
    }


    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        if(category_id!=null){
                            getPlacesFromCategory(category_id);
                        }

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        if(category_id!=null){
                            getPlacesFromCategory(category_id);
                        }

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(ViewCategoryDetailsActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                            utils.alertDialog(ViewCategoryDetailsActivity.this,"Error","Can't find your location! Please ensure Mobile GPS is on!");

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        utils.alertDialog(ViewCategoryDetailsActivity.this,"Error","Can't find your location! Please ensure Mobile GPS is on!");

                        break;
                }
            }
        });
    }

    private void getSavedPlaces(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if(firebaseUser!=null) {
            listenerRegistration = db.collection(getString(R.string.users)).document(firebaseUser.getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {

                            if (e != null) {
                                Log.i(TAG, "An error occurred " + e.getMessage());
                            } else {

                                if (snapshot.get(getString(R.string.places_saved)) != null) {
                                    places_saved = (ArrayList<String>) snapshot.get(getString(R.string.places_saved));
                                }

                            }


                        }
                    });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        displayLocationSettingsRequest(getApplicationContext());

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(listenerRegistration!=null){
            listenerRegistration = null;
        }
    }
}
