package com.beforevisit.beforevisitapp.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beforevisit.beforevisitapp.Model.RatingAndReviews;
import com.beforevisit.beforevisitapp.R;

import java.util.ArrayList;

public class RecyclerRatingAdapter extends RecyclerView.Adapter<RecyclerRatingAdapter.ViewHolder> {

    public static final String TAG = "RatingAndReviewAdap";
    ArrayList<RatingAndReviews> ratingArrayList;
    Activity mActivity;

    public RecyclerRatingAdapter(ArrayList<RatingAndReviews> ratingArrayList, Activity mActivity) {
        this.ratingArrayList = ratingArrayList;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(mActivity);
        View view=layoutInflater.inflate(R.layout.row_user_reviews_ratings,null);
        ViewHolder holder=new RecyclerRatingAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        final RatingAndReviews ratingAndReviews = ratingArrayList.get(i);

        Log.i(TAG, ratingAndReviews.getUser_name());
        holder.tv_user_review.setText(ratingAndReviews.getReview());
        if(ratingAndReviews.getReview().isEmpty()){
            holder.tv_user_review.setVisibility(View.GONE);
        }
        holder.rating.setRating(ratingAndReviews.getRating());
        holder.tv_user_name.setText(ratingAndReviews.getUser_name());
        holder.tv_user_review_date.setText(ratingAndReviews.getDate_created());

        holder.tv_rating_avg.setText((int) ratingAndReviews.getRating() + "/5");



    }

    @Override
    public int getItemCount() {
        return ratingArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_user_name, tv_user_review, tv_user_review_date, tv_rating_avg;
        RatingBar rating;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_user_name = (TextView) itemView.findViewById(R.id.tv_user_name);
            tv_user_review = (TextView) itemView.findViewById(R.id.tv_user_review);
            tv_user_review_date = (TextView) itemView.findViewById(R.id.tv_user_review_date);
            tv_rating_avg = (TextView) itemView.findViewById(R.id.tv_rating_avg);
            rating = (RatingBar) itemView.findViewById(R.id.rating_bar_user);

        }
    }
}
