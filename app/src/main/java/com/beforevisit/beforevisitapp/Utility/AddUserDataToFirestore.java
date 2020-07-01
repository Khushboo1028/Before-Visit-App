package com.beforevisit.beforevisitapp.Utility;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.beforevisit.beforevisitapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddUserDataToFirestore {

    public static final String TAG = "AddUserDataToFirestore";

    FirebaseFirestore db;
    public void addUsersDataToFirestore(final Context mContext,final String userId,final String name, final String email,final String mobile_no){

        db = FirebaseFirestore.getInstance();

        Map<String, Object> data = new HashMap<>();
        data.put(mContext.getString(R.string.name), name);
        data.put(mContext.getString(R.string.date_created),new Timestamp(new Date()));
        data.put(mContext.getString(R.string.email), email);
        data.put(mContext.getString(R.string.mobile_no),mobile_no);
        data.put(mContext.getString(R.string.notification_pref),mContext.getString(R.string.notification_pref_all));

        db.collection(mContext.getString(R.string.users)).document(userId)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });






    }
}
