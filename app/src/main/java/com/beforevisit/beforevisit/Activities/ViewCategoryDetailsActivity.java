package com.beforevisit.beforevisit.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beforevisit.beforevisit.Adapters.GridViewCategoryDetailsAdapter;
import com.beforevisit.beforevisit.Model.CategoryPlaces;
import com.beforevisit.beforevisit.R;
import com.beforevisit.beforevisit.utility.DefaultTextConfig;
import com.beforevisit.beforevisit.utility.ExpandableHeightGridView;
import com.beforevisit.beforevisit.utility.Utils;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewCategoryDetailsActivity extends AppCompatActivity {

    public static final String TAG = "ViewCategoryDetailsActi";
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

    EditText et_search;
    RelativeLayout go_to_top_rel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ViewCategoryDetailsActivity.this);
        setContentView(R.layout.activity_view_catagory_details);

        init();


        img_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nestedScrollView.fullScroll(View.FOCUS_UP);
            }
        });


        if(category_id!=null){
            getPlacesFromCategory(category_id);
        }

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
    }

    private void init(){
        categoryPlacesArrayList = new ArrayList<>();
        gridView = (ExpandableHeightGridView) findViewById(R.id.grid_view);
        gridView.setExpanded(true);
        gridCategoryAdapter = new GridViewCategoryDetailsAdapter(categoryPlacesArrayList, ViewCategoryDetailsActivity.this);
        gridView.setAdapter(gridCategoryAdapter);

        category_id = getIntent().getStringExtra("category_id");
        icon_name = getIntent().getStringExtra("icon_name");
        icon_url = getIntent().getStringExtra("icon_url");
        utils = new Utils();

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

                                String store_name;
                                String address;
                                String image_url;
                                float rating;
                                Boolean isSaved;

                                categoryPlacesArrayList.clear();
                                for (final QueryDocumentSnapshot doc : snapshots) {

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

                                    //TODO: Put isSaved Logic Here

                                    isSaved = false;

                                    categoryPlacesArrayList.add(new CategoryPlaces(
                                            store_name,
                                            address,
                                            image_url,
                                            rating,
                                            isSaved,
                                            doc.getId()
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
    protected void onStop() {
        super.onStop();
        if(listenerRegistration!=null){
            listenerRegistration = null;
        }
    }
}
