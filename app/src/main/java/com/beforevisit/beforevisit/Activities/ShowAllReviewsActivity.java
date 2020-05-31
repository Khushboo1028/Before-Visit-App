package com.beforevisit.beforevisit.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.beforevisit.beforevisit.Adapters.RecyclerRatingAdapter;
import com.beforevisit.beforevisit.Model.RatingAndReviews;
import com.beforevisit.beforevisit.R;
import com.beforevisit.beforevisit.Utility.DefaultTextConfig;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ShowAllReviewsActivity extends AppCompatActivity {

    public static final String TAG = "ShowReviewsActivity";
    RecyclerView recycler_view_ratings;
    RecyclerRatingAdapter ratingAdapter;
    ArrayList<RatingAndReviews> ratingAndReviewsArrayList;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    ListenerRegistration listenerRegistration;

    String place_id,place_name;

    TextView tv_place_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ShowAllReviewsActivity.this);
        setContentView(R.layout.activity_show_all_reviews);

        init();

        if(place_id!=null || !place_id.isEmpty()){
            getAllReviews();
        }

    }

    private void init(){

        ratingAndReviewsArrayList = new ArrayList<>();

        recycler_view_ratings = (RecyclerView) findViewById(R.id.recycler_view_ratings);
        ratingAdapter = new RecyclerRatingAdapter(ratingAndReviewsArrayList,ShowAllReviewsActivity.this);
        recycler_view_ratings.setAdapter(ratingAdapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        place_id = getIntent().getStringExtra("place_id");
        place_name = getIntent().getStringExtra("place_name");

        tv_place_name = (TextView) findViewById(R.id.tv_place_name);
        tv_place_name.setText(place_name);




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
}
