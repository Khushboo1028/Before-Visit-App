package com.beforevisit.beforevisit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.beforevisit.beforevisit.Activities.ViewImageActivity;
import com.beforevisit.beforevisit.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GridImagePlaceAdapter extends BaseAdapter {
    public static final String TAG = "GridImageAdapter";
    Activity mActivity;
    ArrayList<String> imageUrlList;

    public GridImagePlaceAdapter(Activity mActivity, ArrayList<String> imageUrlList) {
        this.mActivity = mActivity;
        this.imageUrlList = imageUrlList;
    }

    @Override
    public int getCount() {
        return imageUrlList.size();
    }

    @Override
    public Object getItem(final int i) {
        Object object = new Object(){
            String image_url = imageUrlList.get(i);

        };
        return object;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        View gridView=convertView;
        if(convertView==null){

            LayoutInflater inflater=(LayoutInflater) mActivity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView=inflater.inflate(R.layout.grid_image_gallery,null);
        }

        ImageView img = (ImageView) gridView.findViewById(R.id.grid_image);

        Log.i(TAG,"image url is "+imageUrlList.get(i));
        Glide.with(mActivity.getApplicationContext()).load(imageUrlList.get(i)).into(img);




        return gridView;

    }
}
