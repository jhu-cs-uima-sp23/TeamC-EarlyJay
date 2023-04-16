package com.example.empty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlannerItemAdapter extends RecyclerView.Adapter<PlannerItemAdapter.MyViewHolder>{
    Context context;
    ArrayList<PlannerItemModel> plannerItemModels;
    public PlannerItemAdapter(Context context, ArrayList<PlannerItemModel> plannerItemModels){
        this.context = context;
        this.plannerItemModels = plannerItemModels;
    }
    @NonNull
    @Override
    public PlannerItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.planner_item, parent, false);
        return new PlannerItemAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlannerItemAdapter.MyViewHolder holder, int position) {
        PlannerItemModel item = plannerItemModels.get(position);
        holder.title.setText(item.title);
        String timeRange = item.startTime + " - " + item.endTime;
        holder.startTime.setText(timeRange);
        holder.cardView.setCardBackgroundColor(item.cardBackgroundColor);
    }

    @Override
    public int getItemCount() {
        return plannerItemModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView startTime;
        CardView cardView;
        int duration;
        String workType;
        int notification;
        Boolean pinned;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            startTime = itemView.findViewById(R.id.itemTime);
            cardView = itemView.findViewById(R.id.planner_card_view);
        }
    }
}
