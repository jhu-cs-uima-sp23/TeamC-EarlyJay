package com.example.empty;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
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
    private OnDeleteButtonClickListener deleteListener;
    private OnEditClickListener editListener;
    private OnPinListener pinListener;
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
            if(item.pinned){
                popup.getMenu().getItem(2).setTitle(res.getString(R.string.unpin));
            }
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
                    editListener.onEditClick(position);
                }else if(selectedTxt.equals(res.getString(R.string.unpin))){
                    holder.pin.setVisibility(View.GONE);
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

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView startTime;
        CardView cardView;
        ImageButton menu;
        ImageView pin;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            startTime = itemView.findViewById(R.id.itemTime);
            cardView = itemView.findViewById(R.id.planner_card_view);
            menu = itemView.findViewById(R.id.optionMenu);
            pin = itemView.findViewById(R.id.pinned);
            pin.setVisibility(View.GONE);
        }
    }
}
