package com.beforevisit.beforevisitapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beforevisit.beforevisitapp.Model.Faq;
import com.beforevisit.beforevisitapp.R;

import java.util.ArrayList;

public class FaqAdapter extends RecyclerView.Adapter <FaqAdapter.ViewHolder>{

    public static final String TAG = "FaqAdapter";
    private Context context;
    private ArrayList<Faq> faqList;

    public FaqAdapter(Context context, ArrayList<Faq> faqList) {
        this.context = context;
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.row_faq,null);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {

        final Faq faq = faqList.get(i);
        if(faq.getAnswer()!=null && faq.getQuestion()!=null){
            holder.tv_question.setText((i+1)+". "+ faq.getQuestion());
            holder.tv_answer.setText(faq.getAnswer());
        }


    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {


        TextView tv_question, tv_answer;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_question = (TextView) itemView.findViewById(R.id.tv_question);
            tv_answer = (TextView) itemView.findViewById(R.id.tv_answer);

        }
    }
}
