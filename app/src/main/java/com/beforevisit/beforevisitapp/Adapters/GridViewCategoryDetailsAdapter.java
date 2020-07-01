package com.beforevisit.beforevisitapp.Adapters;

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

import com.beforevisit.beforevisitapp.Activities.AboutBrandActivity;
import com.beforevisit.beforevisitapp.Model.CategoryPlaces;
import com.beforevisit.beforevisitapp.R;
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

public class GridViewCategoryDetailsAdapter extends BaseAdapter {
    public static final String TAG = "CategoryDetailsAdapter";
    ArrayList<CategoryPlaces> categoryPlacesArrayList;
    Activity mActivity;

    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;


    public GridViewCategoryDetailsAdapter(ArrayList<CategoryPlaces> categoryPlacesArrayList, Activity mActivity) {
        this.categoryPlacesArrayList = categoryPlacesArrayList;
        this.mActivity = mActivity;
    }

    @Override
    public int getCount() {
        return categoryPlacesArrayList.size();
    }

    @Override
    public Object getItem(final int i) {
        Object object = new Object(){
            String image_url = categoryPlacesArrayList.get(i).getImage_url();
            String title= categoryPlacesArrayList.get(i).getStore_name();
            String address = categoryPlacesArrayList.get(i).getAddress();
            float rating = categoryPlacesArrayList.get(i).getRating();
            Boolean isSaved = categoryPlacesArrayList.get(i).getSaved();

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
        getSaved(categoryPlacesArrayList.get(i).getPlace_id(),i,image_saved);

        image_saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(categoryPlacesArrayList.get(i).getSaved()){


                    image_saved.setImageResource(R.drawable.ic_heart_unfilled_alt);
                    setSaved(categoryPlacesArrayList.get(i).getPlace_id(),false,i,image_saved);

                }else{
                    image_saved.setImageResource(R.drawable.ic_heart_filled_alt);
                    setSaved(categoryPlacesArrayList.get(i).getPlace_id(),true,i,image_saved);
                }
            }
        });

        Glide.with(mActivity.getApplicationContext()).load(categoryPlacesArrayList.get(i).getImage_url()).into(image_place);
        tv_store_name.setText(categoryPlacesArrayList.get(i).getStore_name());
        Log.i(TAG,"Store name is "+categoryPlacesArrayList.get(i).getStore_name());
        tv_place_address.setText(categoryPlacesArrayList.get(i).getAddress());
        rating_bar_place.setRating((categoryPlacesArrayList.get(i).getRating()));

        gridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, AboutBrandActivity.class);
                intent.putExtra("place_id",categoryPlacesArrayList.get(i).getPlace_id());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(intent);
            }
        });

        return gridView;
    }

    private void getSaved(final String place_id, final int pos,final ImageView img_save){
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if(firebaseUser!=null){
          listenerRegistration =  db.collection(mActivity.getString(R.string.users)).document(firebaseUser.getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                            ArrayList<String> places_saved = new ArrayList<>();
                            if(e!=null){
                                Log.i(TAG,"An error occurred "+e.getMessage());
                            }else{

                                if(snapshot.get(mActivity.getString(R.string.places_saved))!=null){
                                    places_saved = (ArrayList<String>) snapshot.get(mActivity.getString(R.string.places_saved));
                                }

                            }

                            if(places_saved.contains(place_id)){
                                Log.i(TAG,"Set saved is true for place id "+place_id);
                                img_save.setImageResource(R.drawable.ic_heart_filled_alt);

                            }else{
                                img_save.setImageResource(R.drawable.ic_heart_unfilled_alt);
                            }
                        }
                    });
        }
    }


    private void setSaved(final String place_id, Boolean isSaved, final int i,final ImageView image_saved ){
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if(firebaseUser!=null){
            if(isSaved) {

                db.collection(mActivity.getString(R.string.users)).document(firebaseUser.getUid())
                        .update(mActivity.getString(R.string.places_saved), FieldValue.arrayUnion(place_id))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mActivity.getApplicationContext(),"Added to your favourites",Toast.LENGTH_SHORT).show();
                                image_saved.setImageResource(R.drawable.ic_heart_unfilled_alt);

                                db.collection(mActivity.getString(R.string.places)).document(place_id)
                                        .update(mActivity.getString(R.string.saved_count),FieldValue.increment(1))
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i(TAG,"An error occurred in updating saved count "+e.getMessage());
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mActivity.getApplicationContext(),"An error occurred",Toast.LENGTH_SHORT).show();


                    }
                });

            }else{
                db.collection(mActivity.getString(R.string.users)).document(firebaseUser.getUid())
                        .update(mActivity.getString(R.string.places_saved),FieldValue.arrayRemove(place_id))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                image_saved.setImageResource(R.drawable.ic_heart_unfilled_alt);
                                Toast.makeText(mActivity.getApplicationContext(),"Removed from your favourites",Toast.LENGTH_SHORT).show();
                                db.collection(mActivity.getString(R.string.places)).document(place_id)
                                        .update(mActivity.getString(R.string.saved_count),FieldValue.increment(-1))
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i(TAG,"An error occurred in updating saved count "+e.getMessage());
                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mActivity.getApplicationContext(),"An error occurred",Toast.LENGTH_SHORT).show();

                    }
                });
            }

        }
    }


}
