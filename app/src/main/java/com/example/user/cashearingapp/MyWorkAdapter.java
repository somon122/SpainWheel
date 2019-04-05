package com.example.user.cashearingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MyWorkAdapter extends RecyclerView.Adapter<MyWorkAdapter.ViewHolder>{

    private Context context;
    private List<MyWorkClass> myWorkList;


    public MyWorkAdapter(Context context, List<MyWorkClass> myWorkList) {
        this.context = context;
        this.myWorkList = myWorkList;
    }

    @NonNull
    @Override
    public MyWorkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.my_work_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyWorkAdapter.ViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        MyWorkClass myWorkClass = myWorkList.get(position);
        holder.descriptionTV.setText(myWorkClass.getWorkDescription());
        Picasso.get().load(myWorkClass.getWorkImageUrl()).placeholder(R.drawable.account).into(holder.myWorkImage);


    }

    @Override
    public int getItemCount() {
        return myWorkList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView myWorkImage;
        private TextView descriptionTV;

        public ViewHolder(View itemView) {
            super(itemView);

            myWorkImage = itemView.findViewById(R.id.myWorkImageShow_id);
            descriptionTV = itemView.findViewById(R.id.workDescriptionId);


        }
    }
}
