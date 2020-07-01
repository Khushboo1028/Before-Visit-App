package com.beforevisit.beforevisitapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beforevisit.beforevisitapp.Model.Category;
import com.beforevisit.beforevisitapp.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GridHomeCategoryAdapter extends BaseAdapter {

    public static final String TAG = "GridCategoryAdapter";
    private Activity activity;
    private ArrayList<Category> categoryArrayList;
    int more_pos;

    public GridHomeCategoryAdapter(Activity activity, ArrayList<Category> categoryArrayList) {
        this.activity = activity;
        this.categoryArrayList = categoryArrayList;
    }

    @Override
    public int getCount() {
        return categoryArrayList.size();
    }

    @Override
    public Object getItem(final int i) {

        Object object = new Object(){
            String icon_name = categoryArrayList.get(i).getIcon_name();
            String icon_url = categoryArrayList.get(i).getIcon_url();

        };
        return object;    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        View gridView=convertView;

        if(convertView==null){

            LayoutInflater inflater=(LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView=inflater.inflate(R.layout.row_grid_category,null);
        }

        ImageView img_icon=(ImageView) gridView.findViewById(R.id.img_icon);
        TextView tv_category_name=(TextView)gridView.findViewById(R.id.tv_category_name);

        Glide.with(activity.getApplicationContext()).load(categoryArrayList.get(i).getIcon_url()).into(img_icon);



        if(categoryArrayList.get(i).getIcon_name() != null && !categoryArrayList.get(i).getIcon_name().isEmpty()){
            tv_category_name.setText(categoryArrayList.get(i).getIcon_name());
        }






        return gridView;
    }
}
