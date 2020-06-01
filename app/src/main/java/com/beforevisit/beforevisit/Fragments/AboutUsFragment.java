package com.beforevisit.beforevisit.Fragments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.beforevisit.beforevisit.Activities.FullScreenActivity;
import com.beforevisit.beforevisit.Model.Category;
import com.beforevisit.beforevisit.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class AboutUsFragment extends Fragment {
    public static final String TAG = "AboutUsFragment";
    View view;
    ImageView img_signout,img_notification_bell;

//    YouTubePlayerSupportFragment youtubePlayerSupportFragment;

    TextView tv_about_desc;

    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;
    String video_url;
    String visit_us_url;
    float current_seconds;


    private YouTubePlayerView youTubePlayerView;;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about_us, container, false);

        init();

//        https://www.youtube.com/watch?v=ZbxbbOVAG6I



        getAboutUsData();



        return view;
    }

    private void init(){
        img_signout = (ImageView) getActivity().findViewById(R.id.img_signout);
        img_signout.setVisibility(View.INVISIBLE);

        img_notification_bell = (ImageView) getActivity().findViewById(R.id.img_notification_bell);
        img_notification_bell.setVisibility(View.VISIBLE);

//        youtubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance();
//        getChildFragmentManager().beginTransaction().replace(R.id.youtube_fragment, youtubePlayerSupportFragment).commit();

        tv_about_desc = (TextView) view.findViewById(R.id.tv_about_desc);

        db = FirebaseFirestore.getInstance();


        youTubePlayerView = view.findViewById(R.id.youtube_player_view);
        current_seconds =0;


    }

    private  void getAboutUsData(){
        listenerRegistration = db.collection(getActivity().getString(R.string.about_us))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if(e!=null){
                            Log.i(TAG,"error "+e.getMessage());
                        }else {

                            for (final DocumentSnapshot doc : snapshots) {
                                if(doc.getString(getActivity().getString(R.string.description))!=null){
                                    tv_about_desc.setText(doc.getString(getActivity().getString(R.string.description)));
                                }

                                if(doc.getString(getActivity().getString(R.string.video_url))!=null){
                                    video_url = doc.getString(getActivity().getString(R.string.video_url));
                                }

                                if(doc.getString(getActivity().getString(R.string.visit_us_url))!=null){
                                    visit_us_url = doc.getString(getActivity().getString(R.string.visit_us_url));
                                }
                            }

                            if(video_url!=null || !video_url.isEmpty()){
                                initYouTubePlayerView(video_url);
                            }



                        }
                    }
                });

    }

    private void initYouTubePlayerView(final String video_url) {
        // The player will automatically release itself when the fragment is destroyed.
        // The player will automatically pause when the fragment is stopped
        // If you don't add YouTubePlayerView as a lifecycle observer, you will have to release it manually.
        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {


                String url_cue = video_url.substring(video_url.lastIndexOf("=")+1);
                YouTubePlayerUtils.loadOrCueVideo(
                        youTubePlayer, getLifecycle(),
                        url_cue,0f
                );
            }

            @Override
            public void onCurrentSecond(YouTubePlayer youTubePlayer, float second) {
                super.onCurrentSecond(youTubePlayer, second);
                current_seconds = Math.abs(second-1);


            }
        });


        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {

                Intent intent = new Intent(getActivity(), FullScreenActivity.class);
                intent.putExtra("current_seconds", current_seconds);
                intent.putExtra("video_url",video_url);
                startActivity(intent);

//

            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                Intent intent = new Intent(getActivity(), FullScreenActivity.class);
                intent.putExtra("current_seconds", current_seconds);
                intent.putExtra("video_url",video_url);
                startActivity(intent);

            }
        });
    }





    @Override
    public void onStop() {
        super.onStop();
        if(listenerRegistration!=null){
            listenerRegistration = null;
        }

    }
}
