package com.beforevisit.beforevisit.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beforevisit.beforevisit.Activities.AboutBrandActivity;
import com.beforevisit.beforevisit.Model.Places;
import com.beforevisit.beforevisit.Model.RatingAndReviews;
import com.beforevisit.beforevisit.Model.SearchResults;
import com.beforevisit.beforevisit.R;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecyclerSearchResultsAdapter extends RecyclerView.Adapter<RecyclerSearchResultsAdapter.ViewHolder> {

    public static final String TAG = "SearchResultsAdapter";
    ArrayList<Places> searchResultsArrayList;
    Activity mActivity;

    public RecyclerSearchResultsAdapter(ArrayList<Places> searchResultsArrayList, Activity mActivity) {
        this.searchResultsArrayList = searchResultsArrayList;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(mActivity);
        View view=layoutInflater.inflate(R.layout.row_search_places,null);
        ViewHolder holder=new RecyclerSearchResultsAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        final Places searchResults = searchResultsArrayList.get(i);

        holder.rating_bar_user.setRating(searchResults.getAvg_rating());
        holder.tv_place_name.setText(searchResults.getPlace_name());
        holder.tv_place_address.setText(searchResults.getPlace_address());

        Glide.with(mActivity.getApplicationContext()).load(searchResults.getHome_image_url()).into(holder.image_place);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, AboutBrandActivity.class);
                intent.putExtra("place_id",searchResultsArrayList.get(i).getDoc_id());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return searchResultsArrayList.size();
    }

    public void updateList(ArrayList<Places> list){
        searchResultsArrayList = list;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView  tv_place_name,tv_place_address;
        RatingBar rating_bar_user;
        ImageView image_place;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rating_bar_user = (RatingBar) itemView.findViewById(R.id.rating_bar_user);
            tv_place_address = (TextView) itemView.findViewById(R.id.tv_place_address);
            tv_place_name = (TextView) itemView.findViewById(R.id.tv_place_name);
            image_place = (ImageView) itemView.findViewById(R.id.image_place);

        }
    }
}
