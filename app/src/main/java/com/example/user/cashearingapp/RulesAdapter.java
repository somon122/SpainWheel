package com.example.user.cashearingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RulesAdapter extends RecyclerView.Adapter<RulesAdapter.ViewHolder> {

    private Context context;
    private List<RulesClass> rulesList;

    public RulesAdapter(Context context, List<RulesClass> rulesList) {
        this.context = context;
        this.rulesList = rulesList;
    }

    @NonNull
    @Override
    public RulesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.trams_and_conditions_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RulesAdapter.ViewHolder holder, int position) {


        RulesClass rulesClass = rulesList.get(position);

        holder.questionTV.setText(rulesClass.getQuestion());
        holder.banglaAns.setText(rulesClass.getBanglaAns());
        holder.englishAns.setText(rulesClass.getEnglishAns());


    }

    @Override
    public int getItemCount() {
        return rulesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView questionTV,banglaAns,englishAns;

        public ViewHolder(View itemView) {
            super(itemView);

            questionTV = itemView.findViewById(R.id.rulesQuestion_id);
            banglaAns = itemView.findViewById(R.id.rulesBanglaAns_id);
            englishAns = itemView.findViewById(R.id.rulesEnglishAns_id);


        }
    }
}
