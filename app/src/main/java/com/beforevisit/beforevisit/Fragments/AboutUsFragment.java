package com.beforevisit.beforevisit.Fragments;

import android.content.Context;
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

import com.beforevisit.beforevisit.Model.Category;
import com.beforevisit.beforevisit.R;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
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

    Context mContext;

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

    }

    private  void getAboutUsData(){
        listenerRegistration = db.collection(mContext.getString(R.string.about_us))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if(e!=null){
                            Log.i(TAG,"error "+e.getMessage());
                        }else {

                            for (final DocumentSnapshot doc : snapshots) {
                                if(doc.getString(mContext.getString(R.string.description))!=null){
                                    tv_about_desc.setText(doc.getString(mContext.getString(R.string.description)));
                                }

                                if(doc.getString(mContext.getString(R.string.video_url))!=null){
                                    video_url = doc.getString(mContext.getString(R.string.video_url));
                                    initYouTubePlayerView(video_url);
                                }

                                if(doc.getString(mContext.getString(R.string.visit_us_url))!=null){
                                    visit_us_url = doc.getString(mContext.getString(R.string.visit_us_url));
                                }
                            }



//                            youtubePlayerSupportFragment.initialize(getString(R.string.YOUTUBE_API_KEY), new YouTubePlayer.OnInitializedListener() {
//                                @Override
//                                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
//                                    if(!b){
//
//                                        Log.i(TAG, "video url is "+ video_url);
//                                        if(video_url!=null && !video_url.isEmpty()){
//                                            activePlayer = youTubePlayer;
//                                            String myString = video_url;
//                                            String newString = myString.substring(myString.lastIndexOf("=")+1);
//                                            activePlayer.cueVideo(newString);
//                                            activePlayer.
//                                        }
//
//                                    }
//                                }
//
//                                @Override
//                                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//                                }
//                            });

                        }
                    }
                });

    }

    private void initYouTubePlayerView(final String video_url) {
        // The player will automatically release itself when the fragment is destroyed.
        // The player will automatically pause when the fragment is stopped
        // If you don't add YouTubePlayerView as a lifecycle observer, you will have to release it manually.
        getLifecycle().addObserver(youTubePlayerView);

//        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//            @Override
//            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
//
//
//                String url_cue = video_url.substring(video_url.lastIndexOf("=")+1);
//
//                YouTubePlayerUtils.loadOrCueVideo(
//                        youTubePlayer, getLifecycle(),
//                        url_cue,0f
//                );
//            }
//        });

        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                youTubePlayerView.toggleFullScreen();
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
            }
        });
    }



    @Override
    public void onStop() {
        super.onStop();
        if(listenerRegistration!=null){
            listenerRegistration = null;
        }

//        activePlayer.release();
//        activePlayer = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getContext();
    }
}
