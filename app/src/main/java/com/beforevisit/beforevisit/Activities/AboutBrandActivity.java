package com.beforevisit.beforevisit.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.beforevisit.beforevisit.utility.DefaultTextConfig;
import com.beforevisit.beforevisit.utility.ExpandableHeightGridView;
import com.beforevisit.beforevisit.utility.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AboutBrandActivity extends AppCompatActivity {

    public static final String TAG = "AboutBrandActivity";
    public static final String VisitorSharedPreference = "VisitorCount";
    RelativeLayout main_rel, call_rel, location_rel, share_rel, save_rel;

    YouTubePlayerSupportFragment youtubePlayerSupportFragment;
    TextView tv_place_name, tv_place_address, tv_about_store, tv_phone, tv_rating_avg, tv_rating_user;

    ExpandableHeightGridView images_grid_view;
    GridImagePlaceAdapter gridImagePlaceAdapter;

    RatingBar ratingBarAvg, rating_bar_user;
    String place_id;

    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;
    ArrayList<Places> placesArrayList;

    YouTubePlayer activePlayer;

    TextView text_phone;

    EditText et_review;
    ImageView img_done,img_save;

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




    ArrayList<VisitorCount> visitorCountArrayList;

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
                String map = "http://maps.google.co.in/maps?q=" + placesArrayList.get(0).getPlace_name() + "%20" + placesArrayList.get(0).getPlace_address();
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                startActivity(i);
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
                    Intent intent = new Intent(getApplicationContext(), ViewImageActivity.class);
                    intent.putExtra("image_url",placesArrayList.get(0).getImages_url().get(i));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(0,0);
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
    }


    private void init() {

        youtubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.youtube_fragment, youtubePlayerSupportFragment).commit();

        main_rel = (RelativeLayout) findViewById(R.id.main_rel);
        call_rel = (RelativeLayout) findViewById(R.id.call_rel);
        location_rel = (RelativeLayout) findViewById(R.id.location_rel);
        share_rel = (RelativeLayout) findViewById(R.id.share_rel);
        save_rel = (RelativeLayout) findViewById(R.id.save_rel);

        tv_place_name = (TextView) findViewById(R.id.tv_place_name);
        tv_place_address = (TextView) findViewById(R.id.tv_place_address);
        tv_about_store = (TextView) findViewById(R.id.tv_about_store);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        text_phone = (TextView) findViewById(R.id.text_phone);
        tv_rating_avg = (TextView) findViewById(R.id.tv_rating_avg);
        tv_rating_user = (TextView) findViewById(R.id.tv_rating_user);
        et_review = (EditText) findViewById(R.id.et_review);

        images_grid_view = (ExpandableHeightGridView) findViewById(R.id.images_grid_view);

        img_done = (ImageView) findViewById(R.id.img_done);

        ratingBarAvg = (RatingBar) findViewById(R.id.ratingBarAvg);
        rating_bar_user = (RatingBar) findViewById(R.id.rating_bar_user);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

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
        db.collection(getString(R.string.places)).document(place_id)
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
                                            images_url_5.add("https://firebasestorage.googleapis.com/v0/b/before-visit.appspot.com/o/view_more.png?alt=media&token=89817a9c-b3c6-4f54-b7aa-c1e376ccd098");
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
                                        avg_rating

                                ));

                                //TODO: Images suddenly not displaying
                                gridImagePlaceAdapter = new GridImagePlaceAdapter(AboutBrandActivity.this, images_url_5);
                                images_grid_view.setAdapter(gridImagePlaceAdapter);


                                youtubePlayerSupportFragment.initialize(getString(R.string.YOUTUBE_API_KEY), new YouTubePlayer.OnInitializedListener() {
                                    @Override
                                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                                        if (!b) {

                                            activePlayer = youTubePlayer;
                                            String myString = video_url;
                                            String newString = myString.substring(myString.lastIndexOf("=") + 1);
                                            activePlayer.cueVideo(newString);
                                        }
                                    }

                                    @Override
                                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                                    }
                                });

                            }
                        }
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

        db.collection(getString(R.string.places)).document(place_id)
                .update(getString(R.string.visitor_count),FieldValue.increment(1));




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

        if (activePlayer != null) {
            activePlayer.release();
            activePlayer = null;
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
