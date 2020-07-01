package com.beforevisit.beforevisitapp.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beforevisit.beforevisitapp.Activities.SearchActivity;
import com.beforevisit.beforevisitapp.R;
import com.google.gson.Gson;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter <SearchAdapter.ViewHolder>{

    private SearchActivity searchActivity;
    private ArrayList<String> searchList;

    public SearchAdapter(SearchActivity searchActivity, ArrayList<String> searchList) {
        this.searchActivity = searchActivity;
        this.searchList = searchList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(searchActivity);
        View view=layoutInflater.inflate(R.layout.row_search,null);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        final String search_name = searchList.get(i);
        holder.tv_search_name.setText(search_name);

        holder.img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchList.remove(i);
                deleteUserData();
                notifyDataSetChanged();
                storeSearchData();
                notifyDataSetChanged();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // PERFORM SEARCH HERE
                searchActivity.searchForPlace(search_name);
                searchActivity.filterPlaces(search_name);
            }
        });

    }

    public void deleteUserData(){

        searchActivity.getSharedPreferences(searchActivity.getString(R.string.user_preferences),Context.MODE_PRIVATE).edit().remove(searchActivity.getString(R.string.USER_SEARCH_LIST)).commit();

    }

    public void updateList(ArrayList<String> list){
        searchList = list;
        notifyDataSetChanged();
    }

    private void storeSearchData(){

        Gson gson = new Gson();
        String json = gson.toJson(searchList);

        SharedPreferences sharedPreferences = searchActivity.getSharedPreferences(searchActivity.getString(R.string.user_preferences),Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(searchActivity.getString(R.string.USER_SEARCH_LIST),json );
        editor.commit();
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        TextView tv_search_name;
        ImageView img_cancel;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_search_name = (TextView) itemView.findViewById(R.id.tv_search_name);
            img_cancel = (ImageView) itemView.findViewById(R.id.img_cancel);

        }
    }
}
