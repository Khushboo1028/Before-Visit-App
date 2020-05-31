package com.beforevisit.beforevisit.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.beforevisit.beforevisit.Adapters.GridHomeCategoryAdapter;
import com.beforevisit.beforevisit.Model.Category;
import com.beforevisit.beforevisit.R;
import com.beforevisit.beforevisit.Utility.DefaultTextConfig;
import com.beforevisit.beforevisit.Utility.ExpandableHeightGridView;
import com.beforevisit.beforevisit.Utility.Utils;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewAllCategories extends AppCompatActivity {

    public static final String TAG = "ViewAllCategories";
    Utils utils;
    FirebaseFirestore db;
    ExpandableHeightGridView gridView;

    GridHomeCategoryAdapter gridHomeCategoryAdapter;
    ArrayList<Category> categoryArrayList;
    ListenerRegistration listenerRegistration;
    RelativeLayout go_to_top_rel;
    EditText et_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ViewAllCategories.this);
        setContentView(R.layout.activity_view_all_categories);

        init();

        readCategories();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                Intent intent = new Intent(getApplicationContext(), ViewCategoryDetailsActivity.class);
                intent.putExtra("category_id",categoryArrayList.get(i).getCategory_id());
                intent.putExtra("icon_url",categoryArrayList.get(i).getIcon_url());
                intent.putExtra("icon_name",categoryArrayList.get(i).getIcon_name());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

        //TODO GALA CHECK
        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ViewAllCategories.this, SearchActivity.class);
                View sharedView = et_search;
                String transitionName = getString(R.string.search_transition);

                ActivityOptions transitionActivityOptions = ActivityOptions.makeSceneTransitionAnimation(ViewAllCategories.this, sharedView, transitionName);
                startActivity(intent, transitionActivityOptions.toBundle());
            }
        });

    }

    private void init(){

        utils = new Utils();
        categoryArrayList = new ArrayList<>();

        gridView = (ExpandableHeightGridView) findViewById(R.id.grid_view);
        gridView.setExpanded(true);

        gridHomeCategoryAdapter = new GridHomeCategoryAdapter(this,categoryArrayList);
        gridView.setAdapter(gridHomeCategoryAdapter);

        et_search = (EditText) findViewById(R.id.et_search);
        et_search.setFocusable(false);


        db = FirebaseFirestore.getInstance();
    }


    private void readCategories(){

        listenerRegistration = db.collection(getString(R.string.categories))
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

                            gridHomeCategoryAdapter.notifyDataSetChanged();

                        }
                    }
                });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(listenerRegistration!=null){
            listenerRegistration=null;
        }
    }
}
