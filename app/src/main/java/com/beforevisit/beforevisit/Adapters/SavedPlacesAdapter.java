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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.beforevisit.beforevisit.Activities.AboutBrandActivity;
import com.beforevisit.beforevisit.Model.CategoryPlaces;
import com.beforevisit.beforevisit.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class SavedPlacesAdapter extends BaseAdapter {
    public static final String TAG = "SavedPlacesAdapter";
    ArrayList<CategoryPlaces> savedPlacesList;
    Activity mActivity;

    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ListenerRegistration listenerRegistrationGetSaved;


    public SavedPlacesAdapter(ArrayList<CategoryPlaces> savedPlacesList, Activity mActivity) {
        this.savedPlacesList = savedPlacesList;
        this.mActivity = mActivity;
    }

    @Override
    public int getCount() {
        return savedPlacesList.size();
    }

    @Override
    public Object getItem(final int i) {
        Object object = new Object(){
            String image_url = savedPlacesList.get(i).getImage_url();
            String title= savedPlacesList.get(i).getStore_name();
            String address = savedPlacesList.get(i).getAddress();
            float rating = savedPlacesList.get(i).getRating();
            Boolean isSaved = savedPlacesList.get(i).getSaved();

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
            gridView=inflater.inflate(R.layout.grid_category_place,null);
        }


        final ImageView image_place = gridView.findViewById(R.id.image_place);
        final ImageView image_saved = gridView.findViewById(R.id.image_saved);
        TextView tv_store_name = gridView.findViewById(R.id.tv_store_name);
        TextView tv_place_address = gridView.findViewById(R.id.tv_place_address);
        RatingBar rating_bar_place = gridView.findViewById(R.id.rating_bar_place);
        //getSaved(savedPlacesList.get(i).getPlace_id(),i,image_saved);
        Log.i(TAG,"SavedPlacesList size is "+savedPlacesList.size());

        image_saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(savedPlacesList.get(i).getSaved()){
                    int pos = i;

                    String place_id = savedPlacesList.get(pos).getPlace_id();
                    setSaved(place_id,false, pos);
                    removeFromList(pos);


                }
            }
        });

        Glide.with(mActivity.getApplicationContext()).load(savedPlacesList.get(i).getImage_url()).into(image_place);
        tv_store_name.setText(savedPlacesList.get(i).getStore_name());
        tv_place_address.setText(savedPlacesList.get(i).getAddress());
        rating_bar_place.setRating((savedPlacesList.get(i).getRating()));
        image_saved.setImageResource(R.drawable.ic_heart_filled_alt);

        gridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, AboutBrandActivity.class);
                intent.putExtra("place_id", savedPlacesList.get(i).getPlace_id());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        });

        return gridView;
    }

    public void removeFromList(int i){
        if(!savedPlacesList.isEmpty() && i<savedPlacesList.size()){
            Log.i(TAG,"Removing position "+i);
            savedPlacesList.remove(i);
            notifyDataSetChanged();
        }

    }



    private void setSaved(final String place_id, Boolean isSaved, final int pos){
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if(firebaseUser!=null){
            if(!isSaved)
            {
                db.collection(mActivity.getString(R.string.users)).document(firebaseUser.getUid())
                        .update(mActivity.getString(R.string.places_saved),FieldValue.arrayRemove(place_id))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                                db.collection(mActivity.getString(R.string.places)).document(place_id)
                                        .update(mActivity.getString(R.string.saved_count),FieldValue.increment(-1))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(mActivity.getApplicationContext(),"Removed from your favourites",Toast.LENGTH_SHORT).show();


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i(TAG,"An error occurred in updating saved count "+e.getMessage());
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(mActivity.getApplicationContext(),"An error occurred",Toast.LENGTH_SHORT).show();

                        }
                    });
            }

        }
    }



}
