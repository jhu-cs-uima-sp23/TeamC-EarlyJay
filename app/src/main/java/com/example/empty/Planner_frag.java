package com.example.empty;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.empty.databinding.FragmentPlannerBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Planner_frag extends Fragment {
    ArrayList<PlannerItemModel> plannerItemModels = new ArrayList<>();
    RecyclerView recyclerView;
    private Context context;
    private FragmentPlannerBinding binding;
    private MainActivity mainActivity;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private PlannerItemAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPlannerBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();
        context = getContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putBoolean("newPlan", false);
        editor.apply();
        binding.newPlan.setOnClickListener(e->{
            mainActivity.replaceFragment(R.id.popUp, new SimpleSetting());
        });
        binding.popUp.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(binding.popUp.getChildCount() == 0){
                    if(sharedPreferences.getBoolean("newPlan", false)){
                        String title = sharedPreferences.getString("title", "");
                        String startTime = sharedPreferences.getString("startTime", "0");
                        String durationTxt = sharedPreferences.getString("durationTxt", "0");
                        String notificationTxt = sharedPreferences.getString("notification", "");
                        durationTxt = durationTxt.substring(0, durationTxt.indexOf(" "));
                        int duration = Integer.parseInt(durationTxt);
                        int notification = -1;
                        if(!notificationTxt.equals(getString(R.string.select_alert))){
                            notificationTxt = notificationTxt.substring(0, notificationTxt.indexOf(" "));
                            notification = Integer.parseInt(notificationTxt);
                        }
                        int workType = sharedPreferences.getInt("workType", -1);
                        String cardBackgroundColor = "#D04C25";
                        switch (workType){
                            case R.drawable.yellows:
                                cardBackgroundColor = "#F3A83B";
                                break;
                            case R.drawable.triangle_48:
                                cardBackgroundColor = "#ACCC8C";
                                break;
                            case R.drawable.star_2_xxl:
                                cardBackgroundColor = "#65BFF5";
                                break;
                            default:
                                break;
                        }
                        addPlan(title,startTime,duration,workType,notification, Color.parseColor(cardBackgroundColor));
                        reset();
                    }
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = binding.plannerRecyclerView;
        adapter = new PlannerItemAdapter(context, plannerItemModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    public void addPlan(String title, String startTime, int duration, int workType, int notification, int color){
        plannerItemModels.add(new PlannerItemModel(title, startTime, duration, workType, notification, color));
        adapter.notifyItemInserted(plannerItemModels.size()-1);
        Log.d("TAG", "addPlan: added");
    }

    public void reset(){
        editor.putBoolean("newPlan", false);
        editor.putInt("lastSelected", 0);
        editor.putInt("workType", -1);
        editor.putString("title", "");
        editor.putString("startTime", getString(R.string.select_start_time));
        editor.putString("durationTxt", getResources().getString(R.string.select_duration));
        editor.putString("notification", getString(R.string.select_alert));
        editor.apply();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        reset();
    }
}