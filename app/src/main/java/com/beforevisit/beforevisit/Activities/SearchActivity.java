package com.beforevisit.beforevisit.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.beforevisit.beforevisit.Adapters.RecyclerSearchResultsAdapter;
import com.beforevisit.beforevisit.Adapters.SearchAdapter;
import com.beforevisit.beforevisit.Model.Location;
import com.beforevisit.beforevisit.Model.Places;
import com.beforevisit.beforevisit.R;
import com.beforevisit.beforevisit.Utility.DefaultTextConfig;
import com.beforevisit.beforevisit.Utility.DisctanceCalculator;
import com.beforevisit.beforevisit.Utility.LocationTrack;
import com.beforevisit.beforevisit.Utility.SortingClass;
import com.beforevisit.beforevisit.Utility.Utils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = "SearchActivity";
    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 69;
    public static final int REQUEST_CHECK_SETTINGS = 23;
    RecyclerView recyclerViewSearch,recycler_view_results;
    SearchAdapter adapter;
    ArrayList<String> searchList;

    SharedPreferences sharedPreferences;

    EditText et_search;

    Spinner spinner;

    RelativeLayout recent_search_rel, places_search_rel;
    TextView text_no_search_found;

    ArrayList<String> filterList;
    RecyclerSearchResultsAdapter searchResultsAdapter;
    ArrayList<Places> placesArrayList;

    ListenerRegistration listenerRegistration;
    FirebaseFirestore db;

    String filterFromMainActivity;
    Utils utils;
    LocationTrack locationTrack;
    double user_longitude,user_latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), SearchActivity.this);

        setContentView(R.layout.activity_search);

        init();

        readSearchData();
        adapter = new SearchAdapter(this,searchList);
        recyclerViewSearch.setAdapter(adapter);

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    final String searchText = et_search.getText().toString().trim();
                    deleteUserData();

                    if (searchInList(searchText)){
                        searchList.remove(searchList.indexOf(searchText));
                    }
                    searchList.add(0,searchText);
                    storeSearchData();
                    adapter.notifyDataSetChanged();
                    searchForPlace(searchText);

                    return true;
                }
                return false;
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString().toLowerCase().trim());


            }
        });


        filterList.add(getString(R.string.newest));
        filterList.add(getString(R.string.trending));
        filterList.add(getString(R.string.popular));
        filterList.add(getString(R.string.sponsored));
        filterList.add(getString(R.string.nearest));

        getNewestPlacesData("");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_spinner_filter, filterList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        if(filterFromMainActivity!=null){
            et_search.setFocusable(false);
            recent_search_rel.setVisibility(View.GONE);
            places_search_rel.setVisibility(View.VISIBLE);
            getNewestPlacesData(filterFromMainActivity);

            spinner.setSelection(dataAdapter.getPosition(filterFromMainActivity));
        }



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
               String selected_filter =  spinner.getSelectedItem().toString();
               if(!selected_filter.equals(getString(R.string.nearest))){
                   getNewestPlacesData(selected_filter);
               }else{
                   //nearest location

                   if (ContextCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                       if (ActivityCompat.shouldShowRequestPermissionRationale(SearchActivity.this,
                               Manifest.permission.ACCESS_FINE_LOCATION)) {

                       } else {
                           ActivityCompat.requestPermissions(SearchActivity.this,
                                   new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                   MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                       }
                   }else{
                       displayLocationSettingsRequest(getApplicationContext());
                       locationTrack = new LocationTrack(getApplicationContext());
                       if (locationTrack.canGetLocation()) {

                           user_latitude = locationTrack.getLongitude();
                           user_longitude = locationTrack.getLatitude();

                           sortByNearest();
                       }
                   }
               }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
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


    }

    private void init(){


        searchList = new ArrayList<>();
        filterList = new ArrayList<>();
        placesArrayList = new ArrayList<>();

        utils = new Utils();
        recent_search_rel = (RelativeLayout) findViewById(R.id.recent_search_rel);
        recent_search_rel.setVisibility(View.VISIBLE);
        places_search_rel = (RelativeLayout) findViewById(R.id.places_search_rel);
        places_search_rel.setVisibility(View.GONE);

        recyclerViewSearch = (RecyclerView) findViewById(R.id.recycler_view_search);
        recyclerViewSearch.setHasFixedSize(true);

        recycler_view_results = (RecyclerView) findViewById(R.id.recycler_view_results);
        recycler_view_results.setHasFixedSize(true);

        text_no_search_found = (TextView) findViewById(R.id.text_no_search_found);


        searchResultsAdapter = new RecyclerSearchResultsAdapter(placesArrayList,SearchActivity.this);
        recycler_view_results.setAdapter(searchResultsAdapter);


        filterFromMainActivity = getIntent().getStringExtra("filterFromMainActivity");
        sharedPreferences = getSharedPreferences(getString(R.string.user_preferences), Context.MODE_PRIVATE);

        et_search = (EditText) findViewById(R.id.et_search);
        et_search.requestFocus();

        spinner = (Spinner) findViewById(R.id.spinner);


        db = FirebaseFirestore.getInstance();




    }

    private void filter(String text){
        ArrayList<String> temp = new ArrayList();

        for(int i=0;i<searchList.size();i++){
            if(searchList.get(i).toLowerCase().contains(text)){
                temp.add(searchList.get(i));
            }
        }

        //update recyclerview
        adapter.updateList(temp);

    }

    public void filterPlaces(String text){

        ArrayList<Places> tempPlaces = new ArrayList();
        text_no_search_found.setVisibility(View.GONE);
        for (Places d : placesArrayList) {

            if (d.getPlace_name().toLowerCase().contains(text.toLowerCase())
                    ||(d.getSearch_keywords().toLowerCase().contains(text.toLowerCase()))
                    || (d.getPlace_address().toLowerCase().contains(text.toLowerCase()))

            )
            {
                tempPlaces.add(d);
            }
        }
        //update recyclerview
        searchResultsAdapter.updateList(tempPlaces);

        if(tempPlaces.isEmpty()){
            text_no_search_found.setVisibility(View.VISIBLE);
        }


    }

    private void readSearchData(){

        Gson gson = new Gson();
        String json = sharedPreferences.getString(getString(R.string.USER_SEARCH_LIST), "");
        if (json.isEmpty()) {
            //Toast.makeText(getApplicationContext(),"There is some error",Toast.LENGTH_LONG).show();
            Log.i(TAG,"No recent searches");
        } else {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> arrPackageData = gson.fromJson(json, type);
            searchList = new ArrayList<>();
            for (String data : arrPackageData) {
                searchList.add(data);
            }
        }

    }

    private void storeSearchData(){

        Gson gson = new Gson();
        String json = gson.toJson(searchList);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.USER_SEARCH_LIST),json );
        editor.commit();
    }

    public void deleteUserData(){

        getSharedPreferences(getString(R.string.user_preferences),Context.MODE_PRIVATE).edit().remove(getString(R.string.USER_SEARCH_LIST)).commit();

    }

    private boolean searchInList(String searchText){

        for (String item : searchList){
            if (item.equalsIgnoreCase(searchText)){
                return true;
            }
        }
        return false;

    }


    public void searchForPlace(String searchText){
        // SEARCH FOR PLACE HERE
        Log.i(TAG,"Searched: "+ searchText);
        et_search.setText(searchText.trim());
        recent_search_rel.setVisibility(View.GONE);
        places_search_rel.setVisibility(View.VISIBLE);

       filterPlaces(searchText);

    }

    private void getNewestPlacesData(String selected) {
        text_no_search_found.setVisibility(View.GONE);
        Query query;
        if(selected.equals(getString(R.string.popular))){
            Log.i(TAG,"In query 1");
            query =  db.collection(getString(R.string.places))
                    .orderBy(getString(R.string.saved_count), Query.Direction.DESCENDING);

        }else if(selected.equals(getString(R.string.trending))){
            Log.i(TAG,"In query 2");
            query =  db.collection(getString(R.string.places))
                    .orderBy(getString(R.string.visitor_count), Query.Direction.DESCENDING);
        }else{
            Log.i(TAG,"In query default");
            query =  db.collection(getString(R.string.places))
                    .orderBy(getString(R.string.date_created), Query.Direction.DESCENDING);
        }
        if(query!=null) {
            listenerRegistration = query
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.i(TAG, "An error occurred in getting place" + e.getMessage());
                            } else {

                                String about_Store;
                                String place_address;
                                String category_id;
                                ArrayList<String> images_url = new ArrayList<>();
                                Boolean is_offering_promo;
                                Boolean is_sponsored;
                                ArrayList<String> mobile_no_array = new ArrayList<>();
                                String place_name;
                                String offer_image_url;
                                long saved_count;
                                String search_keywords;
                                String video_url;
                                long visitor_count;
                                int avg_rating;
                                String home_image_url;
                                double latitude,longitude;

                                placesArrayList.clear();
                                for (final QueryDocumentSnapshot snapshot : snapshots) {
                                    if (snapshot.getString(getString(R.string.place_name)) != null) {
                                        place_name = snapshot.getString(getString(R.string.place_name));
                                    } else {
                                        place_name = "";
                                    }

                                    if (snapshot.getString(getString(R.string.about_store)) != null) {
                                        about_Store = snapshot.getString(getString(R.string.about_store));
                                    } else {
                                        about_Store = "";
                                    }

                                    if (snapshot.getString(getString(R.string.address)) != null) {
                                        place_address = snapshot.getString(getString(R.string.address));
                                    } else {
                                        place_address = "";
                                    }

                                    if (snapshot.getString(getString(R.string.category_id)) != null) {
                                        category_id = snapshot.getString(getString(R.string.category_id));

                                    } else {
                                        category_id = "";
                                    }

                                    if (snapshot.get(getString(R.string.images_url)) != null) {
                                        images_url = (ArrayList<String>) snapshot.get(getString(R.string.images_url));

                                    }

                                    if (snapshot.get(getString(R.string.is_offering_promo)) != null) {
                                        is_offering_promo = snapshot.getBoolean(getString(R.string.is_offering_promo));

                                    } else {
                                        is_offering_promo = false;
                                    }

                                    if (snapshot.get(getString(R.string.is_sponsored)) != null) {
                                        is_sponsored = snapshot.getBoolean(getString(R.string.is_sponsored));

                                    } else {
                                        is_sponsored = false;
                                    }

                                    if (snapshot.get(getString(R.string.mobile_no_array)) != null) {
                                        mobile_no_array = (ArrayList<String>) snapshot.get(getString(R.string.mobile_no_array));
                                    }

                                    if (snapshot.getString(getString(R.string.offer_image_url)) != null) {
                                        offer_image_url = snapshot.getString(getString(R.string.offer_image_url));

                                    } else {
                                        offer_image_url = "";
                                    }

                                    if (snapshot.get(getString(R.string.saved_count)) != null) {
                                        saved_count = Long.parseLong(String.valueOf(snapshot.get(getString(R.string.saved_count))));

                                    } else {
                                        saved_count = 0;
                                    }

                                    if (snapshot.getString(getString(R.string.video_url)) != null) {
                                        video_url = snapshot.getString(getString(R.string.video_url));

                                    } else {
                                        video_url = "";
                                    }


                                    if (snapshot.get(getString(R.string.search_keywords)) != null) {
                                        search_keywords = snapshot.getString(getString(R.string.search_keywords));

                                    } else {
                                        search_keywords = "";
                                    }


                                    if (snapshot.get(getString(R.string.visitor_count)) != null) {
                                        visitor_count = Long.parseLong(String.valueOf(snapshot.get(getString(R.string.visitor_count))));

                                    } else {
                                        visitor_count = 0;
                                    }

                                    if (snapshot.get(getString(R.string.avg_rating)) != null) {
                                        avg_rating = Integer.parseInt(String.valueOf(snapshot.getLong(getString(R.string.avg_rating))));
//
//                                    ratingBarAvg.setRating(avg_rating);
//                                    tv_rating_avg.setText((int) ratingBarAvg.getRating() + "/5");
                                    } else {
                                        avg_rating = 0;
                                    }

                                    if (snapshot.getString(getString(R.string.home_image_url)) != null) {
                                        home_image_url = snapshot.getString(getString(R.string.home_image_url));
                                    } else {
                                        home_image_url = "";
                                    }

                                    if(snapshot.get(getString(R.string.latitude))!=null){
                                        latitude = snapshot.getDouble(getString(R.string.latitude));
                                    }else{
                                        latitude = 0.0;
                                    }

                                    if(snapshot.get(getString(R.string.longitude))!=null){
                                        longitude = snapshot.getDouble(getString(R.string.longitude));
                                    }else{
                                        longitude = 0.0;
                                    }


                                    placesArrayList.add(new Places(
                                            snapshot.getId(),
                                            about_Store,
                                            place_address,
                                            category_id,
                                            home_image_url,
                                            images_url,
                                            is_offering_promo,
                                            is_sponsored,
                                            mobile_no_array,
                                            place_name,
                                            offer_image_url,
                                            saved_count,
                                            search_keywords,
                                            video_url,
                                            visitor_count,
                                            avg_rating,
                                            latitude,
                                            longitude

                                    ));

                                }

                                searchResultsAdapter = new RecyclerSearchResultsAdapter(placesArrayList,SearchActivity.this);
                                recycler_view_results.setAdapter(searchResultsAdapter);


                                filterPlaces(et_search.getText().toString().toLowerCase().trim());

                            }
                        }
                    });
        }



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

                        user_latitude = locationTrack.getLongitude();
                        user_longitude = locationTrack.getLatitude();

                        sortByNearest();
                    }
                    return;

                }else{

                    utils.alertDialog(SearchActivity.this,"Uh-Oh","Seems like you have denied permission. The app cannot function normally");

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
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(SearchActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                            utils.alertDialog(SearchActivity.this,"Error","Can't find your location! Please ensure Mobile GPS is on!");

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        utils.alertDialog(SearchActivity.this,"Error","Can't find your location! Please ensure Mobile GPS is on!");

                        break;
                }
            }
        });
    }

    private void sortByNearest(){
        DisctanceCalculator disctanceCalculator = new DisctanceCalculator();
        ArrayList<Location> locationArrayList = new ArrayList<>();
        ArrayList<Places> nearbySortedList = new ArrayList<>();
        for(int i =0 ;i<placesArrayList.size();i++){
            double distanceFromCurrentLocation = disctanceCalculator.distance(user_latitude, user_longitude,placesArrayList.get(i).getLatitude() , placesArrayList.get(i).getLongitude(), "K");
            Log.i(TAG,"distance is "+distanceFromCurrentLocation);
            locationArrayList.add(new Location(placesArrayList.get(i).getDoc_id(),String.format("%.0f", distanceFromCurrentLocation)));
        }

        SortingClass sortingClass = new SortingClass(locationArrayList);
        Collections.reverse(sortingClass.sortDistanceLowToHigh());

        for(int i = 0; i<locationArrayList.size();i++){

            String doc_id = locationArrayList.get(i).getDoc_id();
            Log.i(TAG,"location doc id is "+doc_id);

            for(int j =0 ;j<placesArrayList.size();j++){
                if(placesArrayList.get(j).getDoc_id().equals(doc_id)){
                    nearbySortedList.add(placesArrayList.get(j));
                }
            }


        }



        searchResultsAdapter = new RecyclerSearchResultsAdapter(nearbySortedList,SearchActivity.this);
        recycler_view_results.setAdapter(searchResultsAdapter);


    }


    @Override
    protected void onStop() {
        super.onStop();
        if(listenerRegistration!=null){
            listenerRegistration=null;
        }
    }
}
