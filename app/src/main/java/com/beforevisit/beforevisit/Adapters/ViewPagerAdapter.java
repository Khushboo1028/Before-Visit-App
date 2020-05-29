package com.beforevisit.beforevisit.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.beforevisit.beforevisit.Activities.AboutBrandActivity;
import com.beforevisit.beforevisit.Model.PlacesHomePage;
import com.beforevisit.beforevisit.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter{

    public static final String TAG = "ViewPagerAdapter";
    Activity mActivity;
    ArrayList<PlacesHomePage> placesImageList;

    public ViewPagerAdapter(Activity mActivity, ArrayList<PlacesHomePage> placesImageList) {
        this.mActivity = mActivity;
        this.placesImageList = placesImageList;
    }

    @Override
    public int getCount() {
        return placesImageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
         return (view==(FrameLayout)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater layoutInflater=(LayoutInflater) mActivity.getApplicationContext().getSystemService(mActivity.getApplicationContext().LAYOUT_INFLATER_SERVICE);
        View itemView=layoutInflater.inflate(R.layout.row_viewpager,container,false);

        ImageView imageView=itemView.findViewById(R.id.image_view);

        Glide.with(mActivity.getApplicationContext()).load(placesImageList.get(position).getOffer_image_url()).into(imageView);


        container.addView(itemView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, AboutBrandActivity.class);
                intent.putExtra("place_id",placesImageList.get(position).getDoc_id());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((FrameLayout) object);
    }
}
