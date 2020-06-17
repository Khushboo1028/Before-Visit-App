package com.beforevisit.beforevisit.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.ViewPager;


import com.beforevisit.beforevisit.Activities.MainActivity;
import com.beforevisit.beforevisit.Adapters.ViewPagerAdapter;
import com.beforevisit.beforevisit.Model.PlacesHomePage;
import com.beforevisit.beforevisit.R;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Utils {

    public static final String TAG = "Utils";
    int current_positionTop=1;
    AlertDialog.Builder builder;
    AlertDialog alert;
    ViewPagerAdapter viewPagerAdapter;
    int current_position_of_viewpager;

    public boolean isInternetAvailable(Activity mActivity) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {

                builder = new AlertDialog.Builder(mActivity);
                builder.setMessage("Seems like you are not connected to the internet! Kindly check your connection!")
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alert.dismiss();
                            }
                        });
                alert = builder.create();
                alert.setTitle("Uh - Oh!");
                alert.show();
        }
        return false;
    }


    public void vibrate(Context mContext){
        Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    public static boolean isValidEmailAddress(String email) {
        boolean stricterFilter = true;
        String stricterFilterString = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
        String laxString = ".+@.+\\.[A-Za-z]{2}[A-Za-z]*";
        String emailRegex = stricterFilter ? stricterFilterString : laxString;
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(emailRegex);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public void alertDialog(Activity mActivity, String title, String message){
        builder = new AlertDialog.Builder(mActivity);
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alert = builder.create();
        alert.setTitle(title);
        alert.show();
    }

    public void setViewPager(RelativeLayout relativeLayout, final ViewPager viewPager, final ArrayList<PlacesHomePage> imageUrlList, LinearLayout llPagerDots, Activity mActivity){


        //setting the dots
        if(imageUrlList.isEmpty()){
           relativeLayout.setVisibility(View.GONE);
        }else if(imageUrlList.size()!=0){
            imageUrlList.add(0,new PlacesHomePage("",""));
            imageUrlList.add(imageUrlList.size(),new PlacesHomePage("",""));
        }






        viewPagerAdapter = new ViewPagerAdapter(mActivity, imageUrlList);
        final ArrayList<ImageView> ivArrayDotsPagerList = new ArrayList<>();


        ivArrayDotsPagerList.clear();
        llPagerDots.removeAllViews();

        for (int i = 0; i < imageUrlList.size() - 2; i++) {

            ivArrayDotsPagerList.add(new ImageView(mActivity));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 0, 5, 0);
            ivArrayDotsPagerList.get(i).setLayoutParams(params);
            ivArrayDotsPagerList.get(i).setImageResource(R.drawable.dots_unselected);

            ivArrayDotsPagerList.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setAlpha(1);
                }
            });

            llPagerDots.addView(ivArrayDotsPagerList.get(i));
            llPagerDots.bringToFront();

        }

        viewPager.setAdapter(viewPagerAdapter);
//        viewPager.setOffscreenPageLimit(8);



        if(imageUrlList.size()>2){
            ivArrayDotsPagerList.get(0).setImageResource(R.drawable.dots_selected);

        }

        if(viewPager.getCurrentItem() == 0){
            viewPager.setCurrentItem(1,false);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


                current_position_of_viewpager = position;
                if (position==imageUrlList.size()-1){
                    viewPager.setCurrentItem(1,true);
                }
                current_position_of_viewpager = position;


                if (position == 0 && imageUrlList.size()>2) {
                    viewPager.setCurrentItem(1,true);
                    ivArrayDotsPagerList.get(0).setImageResource(R.drawable.dots_selected);
                }
            }

            @Override
            public void onPageSelected(int position) {

                    Log.i(TAG,"position is "+position);

                if (position == imageUrlList.size()-1){
                    ivArrayDotsPagerList.get(position-2).setImageResource(R.drawable.dots_selected);
                }else{

                    for (int i = 0; i < ivArrayDotsPagerList.size(); i++) {


                        ivArrayDotsPagerList.get(i).setImageResource(R.drawable.dots_unselected);
                        if (position != 0) {
                            ivArrayDotsPagerList.get(position - 1).setImageResource(R.drawable.dots_selected);
                        }


                    }
                }



                if (position == 0) {
                    viewPager.setCurrentItem(1);
                    ivArrayDotsPagerList.get(0).setImageResource(R.drawable.dots_selected);
                }







            }

            @Override
            public void onPageScrollStateChanged(int state) {





            }
        });


//        final Handler handler=new Handler();
//        final Runnable runnableTop=new Runnable() {
//            @Override
//            public void run() {
//
//
//                if (current_positionTop<imageUrlList.size() - 1){
//                    viewPager.setCurrentItem(current_positionTop,false);
//                    current_positionTop+=1;
//
//                }else{
//                    current_positionTop=1;
//                    viewPager.setCurrentItem(current_positionTop,false);
//                    current_positionTop=2;
//                }
//
//
//            }
//        };
//
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(runnableTop);
//            }
//        },0,5000);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
