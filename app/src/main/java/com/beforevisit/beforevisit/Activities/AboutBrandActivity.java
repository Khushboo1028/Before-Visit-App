package com.beforevisit.beforevisit.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beforevisit.beforevisit.Adapters.GridImagePlaceAdapter;
import com.beforevisit.beforevisit.Adapters.RecyclerRatingAdapter;
import com.beforevisit.beforevisit.Model.Places;
import com.beforevisit.beforevisit.Model.RatingAndReviews;
import com.beforevisit.beforevisit.Model.VisitorCount;
import com.beforevisit.beforevisit.R;
import com.beforevisit.beforevisit.Utility.DefaultTextConfig;
import com.beforevisit.beforevisit.Utility.ExpandableHeightGridView;
import com.beforevisit.beforevisit.Utility.Utils;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AboutBrandActivity extends AppCompatActivity {

    public static final String TAG = "AboutBrandActivity";
    public static final String VisitorSharedPreference = "VisitorCount";
    RelativeLayout main_rel, call_rel, location_rel, share_rel, save_rel;

    TextView tv_place_name, tv_place_address, tv_about_store, tv_phone, tv_rating_avg, tv_rating_user, tv_total_reviews;

    ExpandableHeightGridView images_grid_view;
    GridImagePlaceAdapter gridImagePlaceAdapter;

    RatingBar ratingBarAvg, rating_bar_user;
    String place_id;

    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;
    ArrayList<Places> placesArrayList;


    TextView text_phone;

    EditText et_review;
    ImageView img_done,img_save,img_share;

    Utils utils;
    String rating;
    int int_rating;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    AlertDialog.Builder builder;
    AlertDialog alert;
    String name, review;
    double total_reviews;
    int avg_rating;
    ProgressBar progressBar;

    String doc_update_id;
    Boolean isSaved = false;

    ArrayList<RatingAndReviews> ratingAndReviewsArrayList;
    RecyclerView recycler_view_ratings;
    RecyclerRatingAdapter ratingAdapter;
    int previous_rating = 0;

    String share_link, share_text;

    Button btn_show_more;
    ArrayList<VisitorCount> visitorCountArrayList;
    float current_seconds;

    YouTubePlayerView youTubePlayerView;
    NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), AboutBrandActivity.this);

        setContentView(R.layout.activity_about_brand);


        init();


        call_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customCallDialog(placesArrayList.get(0).getMobile_no_array());
            }
        });

        location_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(placesArrayList.get(0).getLatitude()!=0 && placesArrayList.get(0).getLongitude()!=0){
//                    Uri gmmIntentUri = Uri.parse("google.navigation:"+placesArrayList.get(0).getLatitude()+","+placesArrayList.get(0).getLongitude());
//                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                    mapIntent.setPackage("com.google.android.apps.maps");
//
//                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
//                        startActivity(mapIntent);
//                    }
//
//                }else{
                    String map = "http://maps.google.co.in/maps?q=" + placesArrayList.get(0).getPlace_name() + "%20" + placesArrayList.get(0).getPlace_address();
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                    startActivity(i);
//                }

            }
        });

        tv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tel = "tel:+91" + tv_phone.getText().toString();
                Intent acCall = new Intent(Intent.ACTION_DIAL);
                acCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                acCall.setData(Uri.parse(tel));
                startActivity(acCall);
            }
        });


        if (place_id != null && !place_id.isEmpty()) {
            getPlaceData(place_id);
            getAllReviews();
            getUserReviews();
            getUserData();
            getTotalReviews();

        }



        storeVisitorCount();



        rating_bar_user.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                rating = String.valueOf(v);
                int_rating = (int)v;
                tv_rating_user.setText((int) v + "/5");
            }
        });

        img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share_text = "Check out this place:\n"+placesArrayList.get(0).getPlace_name()+"\n\nDownload from Play Store:\n"+ share_link
                        +"\n\nBefore you visit check out Before Visit :)";



                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, share_text);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });

        img_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "img_done pressed");
                review = et_review.getText().toString().trim();


                if (utils.isInternetAvailable(AboutBrandActivity.this)) {
                    if (rating==null || rating.isEmpty()) {
                        utils.alertDialog(AboutBrandActivity.this, "Uh-Oh", "Seems like you haven't provided any rating!");
                    } else if (review.isEmpty()) {
                        builder = new AlertDialog.Builder(AboutBrandActivity.this);
                        builder.setMessage("You didn't give the review. Do you want to proceed?")
                                .setCancelable(true)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        sendReview();
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
                    } else {
                        sendReview();
                    }
                }
            }
        });

        images_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if(i == 5){
                    Intent intent = new Intent(getApplicationContext(), ShowAllImagesActivity.class);
                    intent.putStringArrayListExtra("images_url",placesArrayList.get(0).getImages_url());
                    intent.putExtra("place_name",placesArrayList.get(0).getPlace_name());
                    startActivity(intent);
                    overridePendingTransition(0,0);

                }else{

                    new StfalconImageViewer.Builder<>(AboutBrandActivity.this, placesArrayList.get(0).getImages_url(), new ImageLoader<String>() {
                        @Override
                        public void loadImage(ImageView imageView, String imageUrl) {
                            Glide.with(getApplicationContext()).load(imageUrl).into(imageView);
                        }
                    })
                            .withStartPosition(i)
                            .show();


                }
            }
        });

        save_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isSaved){
                    savePlace(place_id, false);
                }else{
                    savePlace(place_id, true);
                }

            }
        });
        btn_show_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ShowAllReviewsActivity.class);
                intent.putExtra("place_name",placesArrayList.get(0).getPlace_name());
                intent.putExtra("place_id",placesArrayList.get(0).getDoc_id());
                startActivity(intent);
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


    private void init() {


        main_rel = (RelativeLayout) findViewById(R.id.main_rel);
        call_rel = (RelativeLayout) findViewById(R.id.call_rel);
        location_rel = (RelativeLayout) findViewById(R.id.location_rel);
        share_rel = (RelativeLayout) findViewById(R.id.share_rel);
        save_rel = (RelativeLayout) findViewById(R.id.save_rel);

        tv_place_name = (TextView) findViewById(R.id.tv_place_name);
        tv_place_address = (TextView) findViewById(R.id.tv_place_address);
        tv_about_store = (TextView) findViewById(R.id.tv_about_store);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_total_reviews = (TextView) findViewById(R.id.tv_total_reviews);
        text_phone = (TextView) findViewById(R.id.text_phone);
        tv_rating_avg = (TextView) findViewById(R.id.tv_rating_avg);
        tv_rating_user = (TextView) findViewById(R.id.tv_rating_user);
        et_review = (EditText) findViewById(R.id.et_review);

        images_grid_view = (ExpandableHeightGridView) findViewById(R.id.images_grid_view);

        img_done = (ImageView) findViewById(R.id.img_done);
        img_share = (ImageView) findViewById(R.id.img_share);

        ratingBarAvg = (RatingBar) findViewById(R.id.ratingBarAvg);
        rating_bar_user = (RatingBar) findViewById(R.id.rating_bar_user);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        scrollView = (NestedScrollView) findViewById(R.id.scrollView);

        btn_show_more = (Button)findViewById(R.id.btn_show_more);
        place_id = getIntent().getStringExtra("place_id");
        Log.i(TAG, "Place id is " + place_id);

        db = FirebaseFirestore.getInstance();
        placesArrayList = new ArrayList<>();
        ratingAndReviewsArrayList = new ArrayList<>();
        visitorCountArrayList = new ArrayList<>();
        recycler_view_ratings = (RecyclerView) findViewById(R.id.recycler_view_ratings);
        ratingAdapter = new RecyclerRatingAdapter(ratingAndReviewsArrayList,AboutBrandActivity.this);
        recycler_view_ratings.setAdapter(ratingAdapter);
        utils = new Utils();
        img_save = (ImageView) findViewById(R.id.img_save);
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        current_seconds =0;



        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
    }


    public void customCallDialog(ArrayList<String> mobile_arrray_list) {

        if(mobile_arrray_list!=null && !mobile_arrray_list.isEmpty()) {
            final CharSequence[] items = new CharSequence[mobile_arrray_list.size()];
            for (int i = 0; i < mobile_arrray_list.size(); i++) {
                items[i] = mobile_arrray_list.get(i);
            }
            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AboutBrandActivity.this);

            builder.setTitle("Select Number");

            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {

                    String tel = (String) items[item];
                    tel = "tel:" + tel;
                    Intent acCall = new Intent(Intent.ACTION_DIAL);
                    acCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    acCall.setData(Uri.parse(tel));
                    startActivity(acCall);
                }
            });


            builder.show();
            builder.setCancelable(true);
        }


    }

    private void getPlaceData(final String place_id) {
       listenerRegistration = db.collection(getString(R.string.places)).document(place_id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.i(TAG, "An error occurred in getting place" + e.getMessage());
                        } else {


                            String about_Store;
                            String place_address;
                            String category_id;
                            String home_image_url;
                            ArrayList<String> images_url = new ArrayList<>();
                            ArrayList<String> images_url_5 = new ArrayList<>();
                            Boolean is_offering_promo;
                            Boolean is_sponsored;
                            ArrayList<String> mobile_no_array = new ArrayList<>();
                            String place_name;
                            String offer_image_url;
                            long saved_count;
                            String search_keywords;
                            final String video_url;
                            long visitor_count;
                            int avg_rating;
                            double latitude,longitude;

                            if (snapshot != null && snapshot.exists()) {


                                if (snapshot.getString(getString(R.string.place_name)) != null) {
                                    place_name = snapshot.getString(getString(R.string.place_name));
                                    tv_place_name.setText(place_name);
                                } else {
                                    place_name = "";
                                }

                                if (snapshot.getString(getString(R.string.home_image_url)) != null) {
                                    home_image_url = snapshot.getString(getString(R.string.home_image_url));
                                } else {
                                    home_image_url = "";
                                }

                                if (snapshot.getString(getString(R.string.about_store)) != null) {
                                    about_Store = snapshot.getString(getString(R.string.about_store));
                                    tv_about_store.setText(about_Store);
                                } else {
                                    about_Store = "";
                                }

                                if (snapshot.getString(getString(R.string.address)) != null) {
                                    place_address = snapshot.getString(getString(R.string.address));
                                    tv_place_address.setText(place_address);
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


                                        for(int i = 0; i<images_url.size();i++) {
                                            if(images_url_5.size() < 5) {
                                                images_url_5.add(images_url.get(i));
                                            }
                                        }

                                        if(images_url_5.size() == 5) {
                                            images_url_5.add("https://firebasestorage.googleapis.com/v0/b/before-visit.appspot.com/o/view_more_text.png?alt=media&token=0c840df9-3d3c-4c58-a3d3-9e93d76057ef");
                                        }


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
                                    tv_phone.setText(String.valueOf(mobile_no_array.get(0)));
                                }else{
                                    tv_phone.setVisibility(View.GONE);
                                    text_phone.setVisibility(View.GONE);
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

                                if (snapshot.get(getString(R.string.search_keywords)) != null) {
                                    search_keywords = snapshot.getString(getString(R.string.search_keywords));

                                } else {
                                    search_keywords = "";
                                }


                                if (snapshot.getString(getString(R.string.video_url)) != null) {
                                    video_url = snapshot.getString(getString(R.string.video_url));

                                } else {
                                    video_url = "";
                                }

                                if (snapshot.get(getString(R.string.visitor_count)) != null) {
                                    visitor_count = Long.parseLong(String.valueOf(snapshot.get(getString(R.string.visitor_count))));

                                } else {
                                    visitor_count = 0;
                                }

                                if (snapshot.get(getString(R.string.avg_rating)) != null) {
                                    avg_rating = Integer.parseInt(String.valueOf(snapshot.getLong(getString(R.string.avg_rating))));

                                    ratingBarAvg.setRating(avg_rating);
                                    tv_rating_avg.setText((int) ratingBarAvg.getRating() + "/5");
                                } else {
                                    avg_rating = 0;
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
                                        place_id,
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


                                initYouTubePlayerView(video_url);
                                gridImagePlaceAdapter = new GridImagePlaceAdapter(AboutBrandActivity.this, images_url_5);
                                images_grid_view.setAdapter(gridImagePlaceAdapter);




                            }
                        }
                    }
                });


        //getting share link

        listenerRegistration = db.collection(getString(R.string.share_link))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                        if(e!=null){
                            Log.i(TAG,"Error is "+e.getMessage());
                        }else{
                            if(snapshots!=null && !snapshots.isEmpty()) {

                                for (final QueryDocumentSnapshot doc : snapshots) {
                                    share_link = doc.getString(getString(R.string.share_link));

                                }


                            }
                        }

                    }
                });

    }

    private void initYouTubePlayerView(final String video_url) {
        // The player will automatically release itself when the fragment is destroyed.
        // The player will automatically pause when the fragment is stopped
        // If you don't add YouTubePlayerView as a lifecycle observer, you will have to release it manually.
//        getLifecycle().addObserver(youTubePlayerView);


        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer) {


                String url_cue = video_url.substring(video_url.lastIndexOf("=")+1);
                YouTubePlayerUtils.loadOrCueVideo(
                        youTubePlayer, getLifecycle(),
                        url_cue,0f
                );
                youTubePlayer.pause();
            }

            @Override
            public void onCurrentSecond(com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, float second) {
                super.onCurrentSecond(youTubePlayer, second);
                current_seconds = Math.abs(second-1);


            }
        });


        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {

                Intent intent = new Intent(getApplicationContext(), FullScreenActivity.class);
                intent.putExtra("current_seconds", current_seconds);
                intent.putExtra("video_url",video_url);
                startActivity(intent);

            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                Intent intent = new Intent(getApplicationContext(), FullScreenActivity.class);
                intent.putExtra("current_seconds", current_seconds);
                intent.putExtra("video_url",video_url);
                startActivity(intent);
            }
        });
    }

    private void getUserData() {


        if (firebaseUser != null) {
            DocumentReference docref = db.collection(getString(R.string.users)).document(firebaseUser.getUid());

            listenerRegistration = docref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.i(TAG, "An error occurred " + e.getMessage());
                    } else {
                        ArrayList<String> places_saved = new ArrayList<>();
                        if (snapshot != null && snapshot.exists()) {
                            Log.i(TAG, "Data is here ");

                            if (snapshot.get(getString(R.string.name)) != null) {
                                name = snapshot.getString(getString(R.string.name));

                            } else {
                                name = "";
                            }

                            if(snapshot.get(getString(R.string.places_saved))!=null){
                                places_saved = (ArrayList<String>) snapshot.get(getString(R.string.places_saved));
                            }



                        }

                        if(places_saved.contains(place_id)){
                            isSaved = true;
                            img_save.setImageResource(R.drawable.ic_heart_filled);

                        }else{
                            isSaved = false;
                            img_save.setImageResource(R.drawable.ic_heart_unfilled);
                        }


                    }
                }
            });
        }

    }

    private void sendReview() {

        utils.hideKeyboard(AboutBrandActivity.this);
        img_done.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> data = new HashMap<>();
        if (!name.isEmpty()) {
            data.put(getString(R.string.user_name), name);
        }

        if (review != null && !review.isEmpty()) {
            data.put(getString(R.string.review), review);
        }

        if (place_id != null && !place_id.isEmpty()) {
            data.put(getString(R.string.place_id), place_id);
        }

        if (placesArrayList.get(0).getPlace_name() != null && !placesArrayList.get(0).getPlace_name().isEmpty()) {
            data.put(getString(R.string.place_name_review), placesArrayList.get(0).getPlace_name());
        }

        data.put(getString(R.string.userid), firebaseUser.getUid());
        data.put(getString(R.string.date_created), new Timestamp(new Date()));






        if (firebaseUser != null) {
            if (doc_update_id == null || doc_update_id.isEmpty()) {

                if (rating != null || !rating.isEmpty()) {
                    data.put(getString(R.string.rating), int_rating);
                    avg_rating = (int)Math.round((((placesArrayList.get(0).getAvg_rating() * total_reviews) + (int_rating)) / (total_reviews + 1)));
                    placesArrayList.get(0).setAvg_rating(avg_rating);

                }
                db.collection(getString(R.string.ratings_and_reviews)).document()
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                progressBar.setVisibility(View.GONE);
                                img_done.setEnabled(true);
                                utils.alertDialog(AboutBrandActivity.this, "Thank You!", "Your feedback is valuable to us!");
                                scrollView.fullScroll(View.FOCUS_UP);
                               if(rating!=null && !rating.isEmpty()) {
                                   //Updating average Review in places
                                   db.collection(getString(R.string.places)).document(place_id)
                                           .update(getString(R.string.avg_rating), avg_rating)
                                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                                               @Override
                                               public void onSuccess(Void aVoid) {
                                                   Log.i(TAG, "avg rating under places updated");
                                               }
                                           }).addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           Log.i(TAG, "An error occurred in updating avg_rating in places " + e.getMessage());
                                       }
                                   });
                               }


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                                progressBar.setVisibility(View.GONE);
                                utils.alertDialog(AboutBrandActivity.this, "Uh-Oh", e.getLocalizedMessage());
                                img_done.setEnabled(true);
                            }
                        });
            }else{
                //update review
                if (rating != null && !rating.isEmpty()) {
                    data.put(getString(R.string.rating), int_rating);
                    avg_rating = (int)Math.round((((placesArrayList.get(0).getAvg_rating() * total_reviews) + (int_rating - previous_rating)) / (total_reviews)));
                    placesArrayList.get(0).setAvg_rating(avg_rating);

                }
                db.collection(getString(R.string.ratings_and_reviews))
                        .document(doc_update_id)
                        .update(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                img_done.setEnabled(true);
                                utils.alertDialog(AboutBrandActivity.this, "Thank You!", "Your feedback is valuable to us!");
                                progressBar.setVisibility(View.GONE);
                                scrollView.fullScroll(View.FOCUS_UP);

                                if(rating!=null && !rating.isEmpty()) {
                                    //Updating average Review in places
                                    db.collection(getString(R.string.places)).document(place_id)
                                            .update(getString(R.string.avg_rating), avg_rating)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.i(TAG, "avg rating under places updated");

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i(TAG, "An error occurred in updating avg_rating in places " + e.getMessage());
                                        }
                                    });
                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        progressBar.setVisibility(View.GONE);
                        utils.alertDialog(AboutBrandActivity.this, "Uh-Oh", e.getLocalizedMessage());
                        img_done.setEnabled(true);
                    }
                });
            }


        }
    }

    private void getTotalReviews(){
        listenerRegistration = db.collection(getString(R.string.ratings_and_reviews))
                .whereEqualTo(getString(R.string.place_id),place_id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.i(TAG,"error"+e.getMessage());
                }else{

                    total_reviews = snapshots.size();

                    if(snapshots.isEmpty()){
                        total_reviews = 0;
                        btn_show_more.setVisibility(View.GONE);
                    }

                    tv_total_reviews.setText("( "+ Math.round(total_reviews) + " reviews )");




                }
            }
        });
    }

    private void getUserReviews(){
        if(firebaseUser!=null) {
            listenerRegistration = db.collection(getString(R.string.ratings_and_reviews))
                    .whereEqualTo(getString(R.string.userid), firebaseUser.getUid())
                    .whereEqualTo(getString(R.string.place_id),place_id)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.i(TAG, "getUserReviews: error " + e.getMessage());
                            } else {

                                for (final QueryDocumentSnapshot doc : snapshots) {
                                    if (doc.get(getString(R.string.rating)) != null) {
                                        previous_rating = Math.round(doc.getLong(getString(R.string.rating)));
                                        tv_rating_user.setText(previous_rating + "/5");
                                        rating_bar_user.setRating(previous_rating);

                                    }

                                    if (doc.get(getString(R.string.review)) != null) {
                                        et_review.setText((doc.get(getString(R.string.review)).toString()));
                                    }

                                    doc_update_id = doc.getId();
                                }
                            }
                        }
                    });
        }
    }

    private void getAllReviews(){
        listenerRegistration = db.collection(getString(R.string.ratings_and_reviews))
                .whereEqualTo(getString(R.string.place_id),place_id)
                .orderBy(getString(R.string.date_created), Query.Direction.DESCENDING)
                .limit(5)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.i(TAG,"An error occurred in getting reviews" +e.getMessage());
                        }else{
                            String doc_id;
                            float rating;
                            String review;
                            String date_created;
                            String user_name;

                            if(snapshots!=null && !snapshots.isEmpty()){
                                ratingAndReviewsArrayList.clear();
                                for(final QueryDocumentSnapshot doc: snapshots){
                                    doc_id = doc.getId();

                                    if (doc.get(getString(R.string.rating)) != null) {
                                        rating = Float.parseFloat(doc.get(getString(R.string.rating)).toString());
                                    }else{
                                        rating = 0;
                                    }

                                    if (doc.get(getString(R.string.review)) != null) {
                                        review = doc.get(getString(R.string.review)).toString();
                                    }else{
                                        review = "";
                                    }

                                    if(doc.getTimestamp(getString(R.string.date_created))!=null){
                                        Timestamp date_created_ts = doc.getTimestamp(getString(R.string.date_created));
                                        SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("d MMMM yyyy");
                                        date_created = sfd_viewFormat.format(date_created_ts.toDate());
                                    }else{
                                        date_created = "";
                                    }

                                    if (doc.get(getString(R.string.user_name)) != null) {
                                        user_name = doc.get(getString(R.string.user_name)).toString();
                                    }else{
                                        user_name = "";
                                    }

                                    ratingAndReviewsArrayList.add(new RatingAndReviews(
                                            doc_id,
                                            rating,
                                            review,
                                            date_created,
                                            user_name
                                    ));
                                }

                                ratingAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    private void savePlace(final String place_id, Boolean isSaved){
        if(firebaseUser!=null){
            Log.i(TAG,"userid is "+firebaseUser.getUid());
            Log.i(TAG,"placeid is "+place_id);
            if(isSaved) {
                db.collection(getString(R.string.users)).document(firebaseUser.getUid())
                        .update(getString(R.string.places_saved),FieldValue.arrayUnion(place_id))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Added to your favourites",Toast.LENGTH_SHORT).show();
                                db.collection(getString(R.string.places)).document(place_id)
                                        .update(getString(R.string.saved_count),FieldValue.increment(1))
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i(TAG,"Could not update saved count"+e.getMessage());
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"An error occurred",Toast.LENGTH_SHORT).show();


                            }
                });

            }else{
                db.collection(getString(R.string.users)).document(firebaseUser.getUid())
                        .update(getString(R.string.places_saved),FieldValue.arrayRemove(place_id))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Removed from your favourites",Toast.LENGTH_SHORT).show();
                                db.collection(getString(R.string.places)).document(place_id)
                                        .update(getString(R.string.saved_count),FieldValue.increment(-1))
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i(TAG,"Could not update saved count"+e.getMessage());

                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"An error occurred",Toast.LENGTH_SHORT).show();

                            }
                });
            }

        }
    }

    private void storeVisitorCount(){
        if(firebaseUser!=null){
            db.collection(getString(R.string.places)).document(place_id)
                    .update(getString(R.string.visitor_count),FieldValue.increment(1));
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
            listenerRegistration=null;
        }





    }

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseUser == null){
            finish();
            Intent intent = new Intent(getApplicationContext(),LoginMainActivity.class);
            startActivity(intent);
            overridePendingTransition(0,0);
        }
    }
}
