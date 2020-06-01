package com.beforevisit.beforevisit.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.beforevisit.beforevisit.Adapters.GridImagePlaceAdapter;
import com.beforevisit.beforevisit.R;
import com.beforevisit.beforevisit.Utility.DefaultTextConfig;
import com.beforevisit.beforevisit.Utility.ExpandableHeightGridView;
import com.bumptech.glide.Glide;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.util.ArrayList;

public class ShowAllImagesActivity extends AppCompatActivity {

    ExpandableHeightGridView images_grid_view;
    GridImagePlaceAdapter gridImagePlaceAdapter;
    ArrayList<String> images_url_list;
    TextView tv_place_name;
    String place_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ShowAllImagesActivity.this);
        setContentView(R.layout.activity_show_all_images);

        init();

        images_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {


                new StfalconImageViewer.Builder<>(ShowAllImagesActivity.this, images_url_list, new ImageLoader<String>() {
                    @Override
                    public void loadImage(ImageView imageView, String imageUrl) {
                        Glide.with(getApplicationContext()).load(imageUrl).into(imageView);
                    }
                })
                        .withStartPosition(i)
                        .show();



            }
        });
    }

    private void init(){
        images_url_list = new ArrayList<>();
        images_url_list = getIntent().getStringArrayListExtra("images_url");
        images_grid_view = (ExpandableHeightGridView) findViewById(R.id.images_grid_view);
        gridImagePlaceAdapter = new GridImagePlaceAdapter(ShowAllImagesActivity.this,images_url_list);
        images_grid_view.setAdapter(gridImagePlaceAdapter);

        tv_place_name = (TextView) findViewById(R.id.tv_place_name);
        place_name = getIntent().getStringExtra("place_name");

        if(place_name!=null){
            tv_place_name.setText(place_name);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
