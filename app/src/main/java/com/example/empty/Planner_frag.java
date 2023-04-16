package com.example.empty;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
                        durationTxt = durationTxt.substring(0, durationTxt.indexOf(" "));
                        int duration = Integer.parseInt(durationTxt);
                        int workType = sharedPreferences.getInt("workType", -1);
                        int notification = Integer.parseInt(sharedPreferences.getString("notification", "-1"));
                        addPlan(title,startTime,duration,workType,notification);
                        editor.putBoolean("newPlan", false);
                        editor.putString("durationTxt", getResources().getString(R.string.select_duration));
                        editor.apply();
                    }
                }else{
                    View content = binding.popUp.getChildAt(0);
                    int contentId = content.getId();
                    String viewName = getResources().getResourceName(contentId);
                    Log.d("TAG", "onLayoutChange: "+viewName);
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

    public void addPlan(String title, String startTime, int duration, int workType, int notification){
        plannerItemModels.add(new PlannerItemModel(title, startTime, duration, workType, notification));
//        recyclerView.getLayoutManager().addView(view);
        adapter.notifyItemInserted(plannerItemModels.size()-1);
        Log.d("TAG", "addPlan: added");
    }

}