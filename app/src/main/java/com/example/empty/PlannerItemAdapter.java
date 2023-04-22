package com.example.empty;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private final OnDeleteButtonClickListener deleteListener;
    private final OnEditClickListener editListener;
    private final OnPinListener pinListener;
    Planner_frag planner_frag;
    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClicked(int position);
    }
    public interface OnEditClickListener {
        void onEditClick(int position);
    }
    public interface OnPinListener {
        void onPinClick(int position);
    }
    public PlannerItemAdapter(Planner_frag planner_frag, MainActivity main, ArrayList<PlannerItemModel> plannerItemModels){
        this.plannerItemModels = plannerItemModels;
        this.main = main;
        this.deleteListener = (OnDeleteButtonClickListener) planner_frag;
        this.editListener = (OnEditClickListener) planner_frag;
        this.pinListener = (OnPinListener) planner_frag;
        this.planner_frag = planner_frag;
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
        String timeRange = item.startTime + " - " + item.getEndTime();
        holder.startTime.setText(timeRange);
        holder.cardView.setCardBackgroundColor(item.cardBackgroundColor);
        int status = item.getStatus();
        switch(status) {
            case 1:
                holder.status.setText(R.string.done);
                holder.statusView.setBackgroundColor(context.getResources().getColor(R.color.complete_green));
                break;
            case 2:
                holder.status.setText(R.string.fail);
                holder.statusView.setBackgroundColor(context.getResources().getColor(R.color.fail_red));
                break;
            case 3:
                holder.status.setText(R.string.miss);
                holder.statusView.setBackgroundColor(context.getResources().getColor(R.color.miss_grey));
                break;
            default:
                holder.status.setText("");
                holder.statusView.setBackgroundColor(context.getResources().getColor(R.color.white));
                break;
        }
        if(item.pinned){
            holder.pin.setVisibility(View.VISIBLE);
        }else{
            holder.pin.setVisibility(View.INVISIBLE);
        }
        holder.menu.setOnClickListener(e->{
            PopupMenu popup = new PopupMenu(context, holder.menu);
            popup.getMenuInflater().inflate(R.menu.plan_item_menu, popup.getMenu());
            if(item.pinned){
                popup.getMenu().getItem(2).setTitle(res.getString(R.string.unpin));
            }
            popup.setOnMenuItemClickListener(menuItem -> {
                String selectedTxt = menuItem.toString();
                if(selectedTxt.equals(res.getString(R.string.start_task))){
                    editor.putString("category", item.category);
                    editor.putInt("workType", item.workType);
                    editor.putInt("numSeconds", item.duration *60);
                    editor.putInt("totalTimeInterval", item.duration);
                    editor.putInt("featherCount", item.duration /5);
                    editor.putBoolean("startPlanTask", true);
                    editor.putBoolean("PlannerTask", true);
                    editor.putString("PlanTaskStartTime", item.startTime);
                    editor.apply();
                    main.bottomNavigationView.setSelectedItemId(R.id.map);
                }else if(selectedTxt.equals(res.getString(R.string.edit))){
                    editListener.onEditClick(position);
                }else if(selectedTxt.equals(res.getString(R.string.unpin))){
                    holder.pin.setVisibility(View.INVISIBLE);
                    pinListener.onPinClick(position);
                }else if(selectedTxt.equals(res.getString(R.string.pin))){
                    holder.pin.setVisibility(View.VISIBLE);
                    pinListener.onPinClick(position);
                }else if(selectedTxt.equals(res.getString(R.string.delete))){
                    deleteListener.onDeleteButtonClicked(position);
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
//    @Override
//    public int getItemViewType(int position) {
//        // Return the view type of the item based on its pinning status
//        MyItem item = mDataList.get(position);
//        return item.isPinned() ? 1 : 0;
//    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView startTime;
        CardView cardView;
        ImageButton menu;
        ImageView pin;
        TextView status;

        View statusView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            startTime = itemView.findViewById(R.id.itemTime);
            cardView = itemView.findViewById(R.id.planner_card_view);
            menu = itemView.findViewById(R.id.optionMenu);
            pin = itemView.findViewById(R.id.pinned);
            status = itemView.findViewById(R.id.status);
            statusView = itemView.findViewById(R.id.view_status);
        }
    }
}
