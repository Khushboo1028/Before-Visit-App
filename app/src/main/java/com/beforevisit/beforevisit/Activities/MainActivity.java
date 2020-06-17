package com.beforevisit.beforevisit.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beforevisit.beforevisit.Adapters.GridHomeCategoryAdapter;
import com.beforevisit.beforevisit.Adapters.ViewPagerAdapter;
import com.beforevisit.beforevisit.Fragments.AboutUsFragment;
import com.beforevisit.beforevisit.Fragments.FeedbackFragment;
import com.beforevisit.beforevisit.Fragments.HelpSupportFragment;
import com.beforevisit.beforevisit.Fragments.ListBusinessFragment;
import com.beforevisit.beforevisit.Fragments.ProfileFragment;
import com.beforevisit.beforevisit.Fragments.SavedPlacesFragment;
import com.beforevisit.beforevisit.Model.Category;
import com.beforevisit.beforevisit.Model.PlacesHomePage;
import com.beforevisit.beforevisit.R;
import com.beforevisit.beforevisit.Utility.DefaultTextConfig;
import com.beforevisit.beforevisit.Utility.LocationTrack;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;




import java.util.ArrayList;
import java.util.Timer;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "MainActivity";
    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 69;
    public static final int REQUEST_CHECK_SETTINGS = 23;

    public static final String CHANNEL_ID="NOTIF";
    public static final String CHANNEL_NAME="Notifications";
    public static final String CHANNEL_DESC="This channel is for all notifications";

    ImageView img_menu,img_notification_bell,img_top_logo;
    RelativeLayout expanded_menu,top_main_rel;
    Boolean is_menu_clicked = false;
    RelativeLayout super_rel;

    LoginMainActivity loginMainActivity;

    EditText et_search;

    TextView tv_profile, tv_saved_places, tv_feedback, tv_list_business,tv_help, tv_about, tv_home,tv_new_user;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    ListenerRegistration listenerRegistration;
    FirebaseFirestore db;

    FrameLayout frameLayout;

    // FROM HOME FRAGMENT
    ArrayList<String> imageUrlListTop;
    ViewPager viewPagerTop,viewPagerTrending, viewPagerPopular, viewPagerSponsored;
    ViewPagerAdapter viewPagerAdapter;
    Timer timer;
    private int current_positionTop=0;
    private LinearLayout llPagerDots,llPagerDotsTrend,llPagerDotsSpons,llPagerDotsPop;

    ImageView img_signout, img_top;

    GridHomeCategoryAdapter gridAdapter;
    ArrayList<Category> categoryArrayList;
    GridView gridView;

    NestedScrollView nestedScrollView;

    CardView cardview_top,cardview_trending,cardview_pop,cardview_sponsored;

    RelativeLayout trending_rel, sponsored_rel, popular_rel, tobbar_rel;

    Utils utils;

    View close_view;

    TextView view_more_popular,view_more_sponsored,view_more_trending;


    LocationTrack locationTrack;
    double user_longitude,user_latitude;
    Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), MainActivity.this);
        setContentView(R.layout.activity_main);

        init();

        //Notification
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH );
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!is_menu_clicked){
                    is_menu_clicked = true;
                    img_menu.setBackgroundColor(getColor(R.color.light_black));
                    expanded_menu.setVisibility(View.VISIBLE);
                    close_view.setVisibility(View.VISIBLE);
                }else{
                    is_menu_clicked = false;
                    img_menu.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                    expanded_menu.setVisibility(View.GONE);
                    close_view.setVisibility(View.GONE);
                }

            }
        });

        close_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_menu_clicked = false;
                expanded_menu.setVisibility(View.GONE);
                close_view.setVisibility(View.GONE);
            }
        });

        img_notification_bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
                startActivity(intent);
            }
        });

        top_main_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                is_menu_clicked = false;
                img_menu.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                expanded_menu.setVisibility(View.GONE);
                close_view.setVisibility(View.GONE);

            }
        });


        readUserData();


        img_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nestedScrollView.fullScroll(View.FOCUS_UP);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if(categoryArrayList.get(i).getIcon_name().equals("View More")){

                    Intent intent = new Intent(getApplicationContext(), ViewAllCategoriesActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);



                }else{
                    Intent intent = new Intent(getApplicationContext(), ViewCategoryDetailsActivity.class);
                    intent.putExtra("category_id",categoryArrayList.get(i).getCategory_id());
                    intent.putExtra("icon_url",categoryArrayList.get(i).getIcon_url());
                    intent.putExtra("icon_name",categoryArrayList.get(i).getIcon_name());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                View sharedView = et_search;
                String transitionName = getString(R.string.search_transition);

                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, sharedView, transitionName);
                startActivity(intent, transitionActivityOptions.toBundle());
            }
        });

        img_top_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeMenuItemInvisible(tv_home);
                loadFragment(null);
            }
        });
        view_more_sponsored.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("filterFromMainActivity",getString(R.string.sponsored));
                View sharedView = et_search;
                String transitionName = getString(R.string.search_transition);

                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, sharedView, transitionName);
                startActivity(intent, transitionActivityOptions.toBundle());
            }
        });

        view_more_trending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("filterFromMainActivity",getString(R.string.trending));

                View sharedView = et_search;
                String transitionName = getString(R.string.search_transition);

                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, sharedView, transitionName);
                startActivity(intent, transitionActivityOptions.toBundle());
            }
        });

        view_more_popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("filterFromMainActivity",getString(R.string.popular));

                View sharedView = et_search;
                String transitionName = getString(R.string.search_transition);

                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, sharedView, transitionName);
                startActivity(intent, transitionActivityOptions.toBundle());
            }
        });



            readCategories();
            readOfferedPlaces();
            readTrendingPlaces();
            readPopularPlaces();
            readSponsoredPlaces();








    }


    private void init(){
       img_menu = (ImageView) findViewById(R.id.img_menu);
       img_top_logo = (ImageView) findViewById(R.id.img_top_logo);
       img_notification_bell = (ImageView) findViewById(R.id.img_notification_bell);
       img_notification_bell.setVisibility(View.VISIBLE);

        img_signout = (ImageView) findViewById(R.id.img_signout);
        img_signout.setVisibility(View.GONE);

       expanded_menu = (RelativeLayout) findViewById(R.id.expanded_menu);
       super_rel = (RelativeLayout) findViewById(R.id.super_rel);
       top_main_rel = (RelativeLayout) findViewById(R.id.super_rel);

       tv_new_user = (TextView) findViewById(R.id.tv_new_user);
       view_more_popular = (TextView) findViewById(R.id.view_more_popular);
       view_more_sponsored = (TextView) findViewById(R.id.view_more_sponsored);
       view_more_trending = (TextView) findViewById(R.id.view_more_trending);

       tv_profile = (TextView) findViewById(R.id.tv_profile);
       tv_profile.setOnClickListener(this);

       tv_saved_places = (TextView) findViewById(R.id.tv_saved_places);
       tv_saved_places.setOnClickListener(this);

       tv_feedback = (TextView) findViewById(R.id.tv_feedback);
       tv_feedback.setOnClickListener(this);

       tv_list_business = (TextView) findViewById(R.id.tv_list_business);
       tv_list_business.setOnClickListener(this);

       tv_help = (TextView) findViewById(R.id.tv_help_support);
       tv_help.setOnClickListener(this);

       tv_about = (TextView) findViewById(R.id.tv_about_us);
       tv_about.setOnClickListener(this);

       tv_home = (TextView) findViewById(R.id.tv_home);
       tv_home.setOnClickListener(this);

       frameLayout = (FrameLayout) findViewById(R.id.fragment_container);


       loginMainActivity = new LoginMainActivity();

       mAuth = FirebaseAuth.getInstance();
       firebaseUser = mAuth.getCurrentUser();
       db = FirebaseFirestore.getInstance();





       //FROM HOME FRAGMENT
        viewPagerTop = (ViewPager) findViewById(R.id.viewpager_top);
        viewPagerTrending = (ViewPager) findViewById(R.id.viewpager_trending);
        viewPagerSponsored = (ViewPager) findViewById(R.id.viewpager_sponsored);
        viewPagerPopular = (ViewPager) findViewById(R.id.viewpager_popular);


        imageUrlListTop = new ArrayList<>();
        categoryArrayList = new ArrayList<>();

        llPagerDots = (LinearLayout) findViewById(R.id.pager_dots);
        llPagerDotsTrend = (LinearLayout) findViewById(R.id.pager_dots_trending);
        llPagerDotsSpons = (LinearLayout) findViewById(R.id.pager_dots_sponsored);
        llPagerDotsPop = (LinearLayout) findViewById(R.id.pager_dots_popular);

        img_signout = (ImageView)findViewById(R.id.img_signout);
        img_signout.setVisibility(View.INVISIBLE);
        img_notification_bell = (ImageView)findViewById(R.id.img_notification_bell);
        img_notification_bell.setVisibility(View.VISIBLE);

        cardview_top = (CardView) findViewById(R.id.cardview_top);
        cardview_pop = (CardView) findViewById(R.id.cardview_pop);
        cardview_sponsored = (CardView) findViewById(R.id.cardview_sponsored);
        cardview_trending = (CardView) findViewById(R.id.cardview_trending);


        img_top = (ImageView) findViewById(R.id.img_top);

        et_search = (EditText) findViewById(R.id.et_search);
        et_search.setFocusable(false);


        gridView = (GridView) findViewById(R.id.grid_view);
        gridAdapter = new GridHomeCategoryAdapter(this,categoryArrayList);
        gridView.setAdapter(gridAdapter);

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        utils = new Utils();

        tobbar_rel = (RelativeLayout) findViewById(R.id.top_rel);
        trending_rel = (RelativeLayout) findViewById(R.id.trending_rel);
        sponsored_rel = (RelativeLayout) findViewById(R.id.sponsored_rel);
        popular_rel = (RelativeLayout) findViewById(R.id.popular_rel);


        close_view = (View) findViewById(R.id.close_view);


    }




    @Override
    public void onClick(View view) {


        switch(view.getId()) {
            case R.id.tv_profile:
                makeMenuItemInvisible(tv_profile);
                fragment = new ProfileFragment();

                break;
            case R.id.tv_saved_places:
                makeMenuItemInvisible(tv_saved_places);
                fragment = new SavedPlacesFragment();

                break;
            case R.id.tv_feedback:
                makeMenuItemInvisible(tv_feedback);
                fragment = new FeedbackFragment();

                break;
            case R.id.tv_list_business:
                makeMenuItemInvisible(tv_list_business);
                fragment = new ListBusinessFragment();

                break;
            case R.id.tv_help_support:
                makeMenuItemInvisible(tv_help);
                fragment = new HelpSupportFragment();

                break;
            case R.id.tv_about_us:
                makeMenuItemInvisible(tv_about);
                fragment = new AboutUsFragment();

                break;

            case R.id.tv_home:
                makeMenuItemInvisible(tv_home);
                Log.i(TAG, "HOME CLICKED");
                fragment=null;
                readUserData();
                break;

        }
        loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        nestedScrollView.setVisibility(View.GONE);
        frameLayout.setVisibility(View.VISIBLE);

        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        else {
            for (Fragment fragmentNew : getSupportFragmentManager().getFragments()) {
                getSupportFragmentManager().beginTransaction().remove(fragmentNew).commit();
            }
            frameLayout.setVisibility(View.GONE);
            nestedScrollView.setVisibility(View.VISIBLE);
            return false;
        }

    }

    private void makeMenuItemInvisible(TextView textView){
        is_menu_clicked = false;
        img_menu.setBackgroundColor(getColor(R.color.colorPrimaryDark));
        expanded_menu.setVisibility(View.GONE);
        close_view.setVisibility(View.GONE);
        tv_profile.setTextColor(getColor(R.color.colorPrimary));
        tv_saved_places.setTextColor(getColor(R.color.colorPrimary));
        tv_feedback.setTextColor(getColor(R.color.colorPrimary));
        tv_list_business.setTextColor(getColor(R.color.colorPrimary));
        tv_help.setTextColor(getColor(R.color.colorPrimary));
        tv_about.setTextColor(getColor(R.color.colorPrimary));
        tv_home.setTextColor(getColor(R.color.colorPrimary));
        textView.setTextColor(getColor(R.color.colorAccent));
    }

    private void readUserData(){

        if(firebaseUser!=null){
            DocumentReference docref = db.collection(getString(R.string.users)).document(firebaseUser.getUid());

            listenerRegistration = docref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    if(e!=null){
                        Log.i(TAG,"An error occurred "+e.getMessage());
                    }else{

                        if(snapshot!=null && snapshot.exists()){
                            Log.i(TAG,"Data is here ");

                            if(snapshot.get(getString(R.string.name))!=null){
                                tv_new_user.setText(snapshot.getString(getString(R.string.name)));
                                Log.i(TAG, "name is "+snapshot.getString(getString(R.string.name)));
                            }


                        }
                    }
                }
            });
        }

    }


    private void readCategories(){

        listenerRegistration = db.collection(getString(R.string.categories)).whereEqualTo(getString(R.string.is_home),true)
                .limit(8)
                .orderBy(getString(R.string.date_created), Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if(e!=null){
                            Log.i(TAG,e.getLocalizedMessage());
                        }else{

                            String icon_url,icon_name;
                            categoryArrayList.clear();
                            for (final QueryDocumentSnapshot doc : snapshots) {
                                if(doc.get(getString(R.string.icon_url)) != null){
                                    icon_url = doc.getString(getString(R.string.icon_url));
                                }else{
                                    icon_url = "";
                                }

                                if(doc.get(getString(R.string.icon_name)) != null){
                                    icon_name = doc.getString(getString(R.string.icon_name));
                                }else{
                                    icon_name = "";
                                }

                                categoryArrayList.add(new Category(icon_name,icon_url,doc.getId()));

                            }


                            categoryArrayList.add(new Category("View More","https://firebasestorage.googleapis.com/v0/b/before-visit.appspot.com/o/ic_more.png?alt=media&token=ae62c601-5e4b-4884-b45f-0abc34fdc8da",""));


                            gridAdapter.notifyDataSetChanged();

                        }
                    }
                });

    }

    private void readOfferedPlaces(){


        listenerRegistration = db.collection(getString(R.string.places))
                .whereEqualTo(getString(R.string.is_offering_promo),true)
                .orderBy(getString(R.string.date_created), Query.Direction.DESCENDING)
                .limit(8)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if(e!=null){
                            Log.i(TAG,"Error in offered places: "+e.getMessage());
                        }else{

                            ArrayList<PlacesHomePage> placesHomePageArrayList = new ArrayList<>();
                            String image_url;

                            placesHomePageArrayList.clear();
                            for (final QueryDocumentSnapshot doc : snapshots) {
                                if(doc.get(getString(R.string.offer_image_url)) != null){

                                    image_url = doc.getString(getString(R.string.offer_image_url));
                                    placesHomePageArrayList.add(new PlacesHomePage(doc.getId(),image_url));
                                }




                            }

                            utils.setViewPager(tobbar_rel,viewPagerTop, placesHomePageArrayList,llPagerDots,MainActivity.this);


                        }
                    }
                });

    }

    private void readTrendingPlaces(){

            listenerRegistration = db.collection(getString(R.string.places))
                    .orderBy(getString(R.string.visitor_count), Query.Direction.DESCENDING)
                    .limit(8)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.i(TAG, "Error in Trending places: " + e.getMessage());
                            } else {

                                ArrayList<PlacesHomePage> placesHomePageArrayList = new ArrayList<>();
                                String image_url;

                                for (final QueryDocumentSnapshot doc : snapshots) {
                                    if (doc.get(getString(R.string.home_image_url)) != null) {

                                        image_url = doc.getString(getString(R.string.home_image_url));
                                        placesHomePageArrayList.add(new PlacesHomePage(doc.getId(), image_url));
                                    }


                                }

                                utils.setViewPager(trending_rel, viewPagerTrending, placesHomePageArrayList, llPagerDotsTrend, MainActivity.this);


                            }
                        }
                    });


    }

    private void readPopularPlaces(){



            listenerRegistration = db.collection(getString(R.string.places))
                    .orderBy(getString(R.string.saved_count), Query.Direction.DESCENDING)
                    .limit(8)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.i(TAG, "Error in offered places: " + e.getMessage());
                            } else {

                                ArrayList<PlacesHomePage> placesHomePageArrayList = new ArrayList<>();
                                String image_url;

                                placesHomePageArrayList.clear();
                                for (final QueryDocumentSnapshot doc : snapshots) {
                                    if (doc.get(getString(R.string.home_image_url)) != null) {

                                        image_url = doc.getString(getString(R.string.home_image_url));
                                        placesHomePageArrayList.add(new PlacesHomePage(doc.getId(), image_url));
                                    }


                                }

                                utils.setViewPager(popular_rel, viewPagerPopular, placesHomePageArrayList, llPagerDotsPop, MainActivity.this);


                            }
                        }
                    });


    }

    private void readSponsoredPlaces(){



            listenerRegistration = db.collection(getString(R.string.places))
                    .whereEqualTo(getString(R.string.is_sponsored), true)
                    .orderBy(getString(R.string.date_created), Query.Direction.DESCENDING)
                    .limit(8)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.i(TAG, "Error in sponsored places: " + e.getMessage());
                            } else {

                                ArrayList<PlacesHomePage> placesHomePageArrayList = new ArrayList<>();
                                String image_url;

                                placesHomePageArrayList.clear();
                                for (final QueryDocumentSnapshot doc : snapshots) {
                                    if (doc.get(getString(R.string.home_image_url)) != null) {

                                        image_url = doc.getString(getString(R.string.home_image_url));
                                        placesHomePageArrayList.add(new PlacesHomePage(doc.getId(), image_url));
                                    }


                                }


                                utils.setViewPager(sponsored_rel, viewPagerSponsored, placesHomePageArrayList, llPagerDotsSpons, MainActivity.this);


                            }
                        }
                    });


    }


    //Check For Location
    private void checkForLocation(){

        //When blocked or never asked
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            }
        }else{
            displayLocationSettingsRequest(getApplicationContext());
        }

    }

    private void displayLocationSettingsRequest(Context context) {
        //turn on location after permission is given
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
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);

                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
//                            utils.alertDialog(MainActivity.this,"Error","Can't find your location! Please ensure Mobile GPS is on!");


                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
//                        utils.alertDialog(MainActivity.this,"Error","Can't find your location! Please ensure Mobile GPS is on!");
                        Intent i = new Intent(getApplicationContext(),LocationDenyActivity.class);
                        startActivity(i);

                        break;
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

                        user_latitude = locationTrack.getLongitude();
                        user_longitude = locationTrack.getLatitude();

                    }
                    return;

                }else{

                    Intent intent = new Intent(getApplicationContext(),LocationDenyActivity.class);
                    startActivity(intent);

                    //utils.alertDialog(SearchActivity.this,"Uh-Oh","Seems like you have denied permission. The app cannot function normally");

                }

                break;




        }
    }




    @Override
    public void onBackPressed() {

        if(frameLayout.getVisibility()==View.VISIBLE){
            makeMenuItemInvisible(tv_home);
            loadFragment(null);

        }else{
            super.onBackPressed();
            finishAffinity();
        }
    }

    public void getPermissionStatus(Activity activity, String androidPermissionName) {
        if(ContextCompat.checkSelfPermission(activity, androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName)){
                Log.i(TAG,"Blocked or never asked");
                checkForLocation();
                return;
            }
            Log.i(TAG,"Permission was denied");
            Intent intent = new Intent(getApplicationContext(),LocationDenyActivity.class);
            startActivity(intent);
            return;

        }
        Log.i(TAG,"Permission was granted");
        displayLocationSettingsRequest(getApplicationContext());

    }

    @Override
    protected void onStart() {
        super.onStart();
        makeMenuItemInvisible(tv_home);
        img_notification_bell.setVisibility(View.VISIBLE);
        img_signout.setVisibility(View.GONE);

        if(firebaseUser!=null){
            readUserData();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPermissionStatus(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(listenerRegistration!=null){
            listenerRegistration = null;
        }
    }
}
