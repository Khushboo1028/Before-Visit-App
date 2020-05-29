package com.beforevisit.beforevisit.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.beforevisit.beforevisit.Activities.LoginMainActivity;
import com.beforevisit.beforevisit.Adapters.GridImagePlaceAdapter;
import com.beforevisit.beforevisit.Adapters.GridViewCategoryDetailsAdapter;
import com.beforevisit.beforevisit.Model.CategoryPlaces;
import com.beforevisit.beforevisit.R;
import com.beforevisit.beforevisit.utility.ExpandableHeightGridView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class SavedPlacesFragment extends Fragment {
    public static final String TAG = "SavedPlacesFragment";
    View view;
    ImageView img_signout,img_notification_bell;

    ExpandableHeightGridView gridView;
    GridViewCategoryDetailsAdapter gridAdapter;
    ArrayList<CategoryPlaces> savedPlacesArrayList;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    Activity mActivity;
    ListenerRegistration listenerRegistration;
    ArrayList<String> savedPlacesIdList;

    Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_saved_places, container, false);
        init();

        if(mActivity!=null){
            getSavedPlacesID();
        }


        return view;
    }

    private void init(){
        img_signout = (ImageView) getActivity().findViewById(R.id.img_signout);
        img_signout.setVisibility(View.INVISIBLE);

        img_notification_bell = (ImageView) getActivity().findViewById(R.id.img_notification_bell);
        img_notification_bell.setVisibility(View.VISIBLE);

        savedPlacesArrayList = new ArrayList<>();
        savedPlacesIdList = new ArrayList<>();

        gridView = (ExpandableHeightGridView) view.findViewById(R.id.grid_view);
        gridAdapter = new GridViewCategoryDetailsAdapter(savedPlacesArrayList,getActivity());
        gridView.setAdapter(gridAdapter);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        mActivity = getActivity();

    }

    private void getSavedPlacesID(){
        if(firebaseUser!=null){

            listenerRegistration = db.collection(mContext.getString(R.string.users)).document(firebaseUser.getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                            if(e!=null){
                                Log.i(TAG,"Error: "+e.getMessage());
                            }else{
                                if(snapshot.get(mContext.getString(R.string.places_saved))!=null){
                                    savedPlacesIdList = (ArrayList<String>) snapshot.get(mActivity.getString(R.string.places_saved));
                                }
                            }

                            if(savedPlacesIdList!=null && !savedPlacesIdList.isEmpty()){
                                Log.i(TAG,"saved Places Id's are" + savedPlacesIdList);

                                for(int i=0;i<savedPlacesIdList.size();i++) {
                                    listenerRegistration = db.collection(mActivity.getString(R.string.places)).document(savedPlacesIdList.get(i))
                                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                                    if (e != null) {
                                                        Log.i(TAG, "Error " + e.getMessage());
                                                    } else {
                                                        Log.i(TAG, "Task successful");

                                                        String store_name;
                                                        String address;
                                                        String image_url;
                                                        float rating;
                                                        Boolean isSaved;

                                                        if(snapshot.get(mActivity.getString(R.string.place_name))!=null){
                                                            store_name = snapshot.getString(mActivity.getString(R.string.place_name));

                                                        }else{
                                                            store_name = "";
                                                        }


                                                        if(snapshot.get(mActivity.getString(R.string.address))!=null){
                                                            address = snapshot.getString(mActivity.getString(R.string.address));

                                                        }else{
                                                            address = "";
                                                        }

                                                        if(snapshot.get(mActivity.getString(R.string.home_image_url))!=null){
                                                            image_url = snapshot.getString(mActivity.getString(R.string.home_image_url));

                                                        }else{
                                                            image_url = "";
                                                        }

                                                        if(snapshot.get(mActivity.getString(R.string.avg_rating))!=null){
                                                            rating = Float.parseFloat(snapshot.get(mActivity.getString(R.string.avg_rating)).toString());


                                                        }else{
                                                            rating = 0;
                                                        }

                                                        //TODO: Put isSaved Logic Here

                                                        isSaved = true;

                                                        savedPlacesArrayList.add(new CategoryPlaces(
                                                                store_name,
                                                                address,
                                                                image_url,
                                                                rating,
                                                                isSaved,
                                                                snapshot.getId()
                                                        ));

                                                        gridAdapter.notifyDataSetChanged();


                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });


        }
    }



    @Override
    public void onStart() {
        super.onStart();
        if(firebaseUser == null){

            getActivity().onBackPressed();
            Intent intent = new Intent(getContext(), LoginMainActivity.class);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(0,0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(listenerRegistration!=null){
            listenerRegistration=null;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getContext();
    }
}
