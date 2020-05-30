package com.beforevisit.beforevisit.utility;

import android.app.job.JobParameters;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;

import static com.beforevisit.beforevisit.Activities.AboutBrandActivity.VisitorSharedPreference;

public class JobService extends android.app.job.JobService {
    public static final String TAG = "JobService";
    private boolean jobCancelled = false;
    SharedPreferences sharedPreferences;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        doBackgroundWork(params);
        return true;
    }
    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    Log.d(TAG, "run: " + i);
                    //readVisitorCount();
                    if (jobCancelled) {
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "Job finished");
                jobFinished(params, false);
            }
        }).start();
    }
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }

    private void readVisitorCount(){

        sharedPreferences = getApplicationContext().getSharedPreferences(VisitorSharedPreference, Context.MODE_PRIVATE); // 0 - for private mode

        Map<String,?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d(TAG, entry.getKey() + ": " + entry.getValue().toString());
        }
    }

}
