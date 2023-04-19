package com.example.empty;

import static android.content.ContentValues.TAG;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlannerItemAdapter extends RecyclerView.Adapter<PlannerItemAdapter.MyViewHolder> {
    Context context;
    ArrayList<PlannerItemModel> plannerItemModels;
    MainActivity main;
    Resources res;
    SharedPreferences.Editor editor;
    private OnDeleteButtonClickListener mListener;
    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClicked(int position);
    }
    public PlannerItemAdapter(OnDeleteButtonClickListener listener, MainActivity main, ArrayList<PlannerItemModel> plannerItemModels){
        this.plannerItemModels = plannerItemModels;
        this.main = main;
        this.mListener = listener;
        this.context = main.getApplicationContext();
        this.editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        res = context.getResources();
    }
    @NonNull
    @Override
    public PlannerItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.planner_item, parent, false);
        return new PlannerItemAdapter.MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PlannerItemModel item = plannerItemModels.get(position);
        holder.title.setText(item.title);
        String timeRange = item.startTime + " - " + item.endTime;
        holder.startTime.setText(timeRange);
        holder.cardView.setCardBackgroundColor(item.cardBackgroundColor);
        holder.menu.setOnClickListener(e->{
            PopupMenu popup = new PopupMenu(context, holder.menu);
            popup.getMenuInflater().inflate(R.menu.plan_item_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(menuItem -> {
                String selectedTxt = menuItem.toString();
                if(selectedTxt.equals(res.getString(R.string.start_task))){
                    editor.putString("category", item.category);
                    editor.putInt("workType", item.workType);
                    editor.putInt("numSeconds", item.duration*60);
                    editor.putInt("totalTimeInterval", item.duration);
                    editor.putInt("featherCount", item.duration/5);
                    editor.putBoolean("startPlanTask", true);
                    editor.apply();
                    main.bottomNavigationView.setSelectedItemId(R.id.map);
                }else if(selectedTxt.equals(res.getString(R.string.edit))){
                }else if(selectedTxt.equals(res.getString(R.string.pin))){

                }else if(selectedTxt.equals(res.getString(R.string.delete))){
                    mListener.onDeleteButtonClicked(position);
                }
                return true;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return plannerItemModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView startTime;
        CardView cardView;
        ImageButton menu;
        int duration;
        String workType;
        int notification;
        Boolean pinned;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            startTime = itemView.findViewById(R.id.itemTime);
            cardView = itemView.findViewById(R.id.planner_card_view);
            menu = itemView.findViewById(R.id.optionMenu);
        }
    }
}
