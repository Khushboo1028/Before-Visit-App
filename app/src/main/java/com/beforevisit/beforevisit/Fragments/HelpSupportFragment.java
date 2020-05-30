package com.beforevisit.beforevisit.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.beforevisit.beforevisit.Adapters.FaqAdapter;
import com.beforevisit.beforevisit.Model.Faq;
import com.beforevisit.beforevisit.R;
import com.beforevisit.beforevisit.utility.DefaultTextConfig;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class HelpSupportFragment extends Fragment {
    public static final String TAG = "HelpSupportFragment";
    View view;

    //Recycler View
    private RecyclerView recyclerView;
    private FaqAdapter mAdapter;
    private ArrayList<Faq> faqList;

    Button btn_report_issue;
    ImageView img_signout,img_notification_bell;

    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;

    String date_created, question, answer;

    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), getActivity());

        view = inflater.inflate(R.layout.fragment_help_support, container, false);

        init();

        getQuestions();



        btn_report_issue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ReportIssueFragment())
                        .commit();
            }
        });

        return view;
    }

    private void init(){
        faqList = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases

        btn_report_issue = view.findViewById(R.id.btn_report_issue);

        img_signout = (ImageView) getActivity().findViewById(R.id.img_signout);
        img_signout.setVisibility(View.GONE);

        img_notification_bell = (ImageView) getActivity().findViewById(R.id.img_notification_bell);
        img_notification_bell.setVisibility(View.VISIBLE);

        db = FirebaseFirestore.getInstance();
    }

    private void getQuestions(){
        db = FirebaseFirestore.getInstance();

        db.collection(mContext.getString(R.string.faq)).orderBy(mContext.getString(R.string.date_created))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d(TAG, "Error:" + e.getMessage());
                        }else{
                            faqList.clear();

                            for (final QueryDocumentSnapshot doc : snapshots) {

                                if(doc.getTimestamp(mContext.getString(R.string.date_created))!=null){
                                    Timestamp date_created_ts = doc.getTimestamp(mContext.getString(R.string.date_created));
                                    SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                                    date_created = sfd_viewFormat.format(date_created_ts.toDate());
                                }else{
                                    date_created = "";
                                }

                                if(doc.get(mContext.getString(R.string.question)) != null){
                                    question = doc.getString(mContext.getString(R.string.question));
                                }else{
                                    question = "";
                                }

                                if(doc.get(mContext.getString(R.string.answer)) != null){
                                    answer = doc.getString(mContext.getString(R.string.answer));
                                }else{
                                    answer = "";
                                }

                                Log.i(TAG,"answer is "+answer);

                                faqList.add(new Faq(question,answer,date_created));


                            }
                            mAdapter = new FaqAdapter(getActivity(),faqList);
                            recyclerView.setAdapter(mAdapter);
                        }
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = getContext();
    }
}
