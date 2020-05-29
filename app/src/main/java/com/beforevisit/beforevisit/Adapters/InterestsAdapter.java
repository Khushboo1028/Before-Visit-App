package com.beforevisit.beforevisit.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.beforevisit.beforevisit.Model.Interests;
import com.beforevisit.beforevisit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InterestsAdapter extends RecyclerView.Adapter <InterestsAdapter.ViewHolder>  {


    public static final String TAG = "InterestsAdapter";
    private Context context;
    String interest_name;
    private ArrayList<Interests> interestsList;
    private ArrayList<String> usersInterestsList;
    private ArrayList<String> usersCurrentInterestList;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ListenerRegistration listenerRegistration;
    FirebaseUser firebaseUser;


    public InterestsAdapter(Context context, ArrayList<Interests> interestsList) {
        this.context = context;
        this.interestsList = interestsList;
        this.usersCurrentInterestList = usersCurrentInterestList;
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        usersInterestsList= new ArrayList<>();

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.row_interest,null);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {

        Interests interests = interestsList.get(i);
        holder.checkBox.setText(interests.getName());


        final String name = holder.checkBox.getText().toString();

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if(holder.checkBox.isChecked()){
                   usersInterestsList.add(name);
               }else{
                   usersInterestsList.remove(name);
               }


            }
        });


        getUsersInterests(holder.checkBox);






    }

    public ArrayList<String> getUsersInterestsList(){
        return  usersInterestsList;
    }


    @Override
    public int getItemCount() {
        return interestsList.size();
    }


    private void getUsersInterests(final CheckBox checkBox){
        if(firebaseUser!=null){
            listenerRegistration = db.collection(context.getString(R.string.users)).document(firebaseUser.getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                            if(e!=null){
                                Log.i(TAG,"Error :" +e.getMessage());
                            }else{

                                if (snapshot.get(context.getString(R.string.interests)) != null) {
                                    usersCurrentInterestList = (ArrayList<String>) snapshot.get(context.getString(R.string.interests));

                                    if(usersCurrentInterestList!=null && !usersCurrentInterestList.isEmpty()){
                                        if(usersCurrentInterestList.contains(checkBox.getText().toString())){
                                            checkBox.setChecked(true);
                                        }else{
                                            checkBox.setChecked(false);
                                        }

                                    }
                                }
                            }
                        }
                    });
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        CheckBox checkBox;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_1);



        }
    }


    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if(listenerRegistration!=null){
            listenerRegistration=null;
        }
    }


}
