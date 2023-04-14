package com.example.empty;

import android.content.Context;
import android.view.ContentInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlannerItemAdapter extends RecyclerView.Adapter<PlannerItemAdapter.MyViewHolder>{
    Context context;
    ArrayList<PlannerItemModel> plannerItemModels;
    public PlannerItemAdapter(Context context, ArrayList<PlannerItemModel> plannerItemModels){

    }
    @NonNull
    @Override
    public PlannerItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PlannerItemAdapter.MyViewHolder holder, int position) {
        holder.title.setText(plannerItemModels.get(position).title);
        holder.startTime.setText(plannerItemModels.get(position).startTime);
    }

    @Override
    public int getItemCount() {
        return plannerItemModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView startTime;
        int duration;
        String workType;
        int notification;
        Boolean pinned;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.Title);
            startTime = itemView.findViewById(R.id.time);

        }
    }
}
