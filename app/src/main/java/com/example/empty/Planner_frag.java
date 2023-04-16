package com.example.empty;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.empty.databinding.FragmentPlannerBinding;

import java.util.ArrayList;

public class Planner_frag extends Fragment {
    ArrayList<PlannerItemModel> plannerItemModels = new ArrayList<>();
    RecyclerView recyclerView;
    private Context context;
    private FragmentPlannerBinding binding;
    private MainActivity mainActivity;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private PlannerItemAdapter adapter;

    private String currDateStr;
    private String currDatePage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPlannerBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();
        context = getContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putBoolean("newPlan", false);
        editor.apply();

        currDateStr = sharedPreferences.getString("currDateStr", new DateStr().getDateStr());
        DateStr now = new DateStr(currDateStr);
        currDatePage = sharedPreferences.getString("currDatePage", "Daily");

        switch(currDatePage) {
            case "Weekly":
                mainActivity.replaceFragment(R.id.stuff_on_date, new WeeklyStatsFragment());
                break;
            case "Monthly":
                mainActivity.replaceFragment(R.id.stuff_on_date, new MonthlyStatsFragment());
                break;
            default:
                mainActivity.replaceFragment(R.id.stuff_on_date, new DailyStatsFragment());
                break;
        }

        binding.newPlan.setOnClickListener(e-> mainActivity.replaceFragment(R.id.popUp, new SimpleSetting()));
        binding.popUp.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if(binding.popUp.getChildCount() == 0){
                binding.newPlan.show();
                binding.popupBackground.setVisibility(View.INVISIBLE);
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
            }else{
                binding.newPlan.hide();
                binding.popupBackground.setVisibility(View.VISIBLE);
            }
        });
        binding.leftRollButton2.setOnClickListener(view -> {
            String dateStr;
            if ("Weekly".equals(currDatePage)) {
                dateStr = now.getPastDay(7);
            } else {
                dateStr = now.getPastDay(1);
            }
            editor.putString("currDateStr", dateStr);
            editor.apply();
            mainActivity.replaceFragment(R.id.frame_layout, new Planner_frag());
        });
        binding.rightRollButton2.setOnClickListener(view -> {
            String dateStr;
            switch(currDatePage) {
                case "Weekly":
                    dateStr = now.getFutureDay(7);
                    break;
                case "Monthly":
                    dateStr = now.getFutureDay(now.getMonthDays());
                    break;
                default:
                    dateStr = now.getFutureDay(1);
                    break;
            }
            if (new DateStr(dateStr).comp(new DateStr()) > 0) {
                Toast.makeText(context,
                        "This is the most recent day!", Toast.LENGTH_LONG).show();
                return;
            }
            editor.putString("currDateStr", dateStr);
            editor.apply();
            mainActivity.replaceFragment(R.id.frame_layout, new Planner_frag());
        });
        binding.spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                if (selected.equals(currDatePage)) {
                    return;
                }
                editor.putString("currDatePage", selected);
                editor.putString("currDateStr", new DateStr().getDateStr());
                editor.apply();
                mainActivity.replaceFragment(R.id.frame_layout, new Planner_frag());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });
        ArrayAdapter myAdapter = (ArrayAdapter) binding.spinner3.getAdapter();
        binding.spinner3.setSelection(myAdapter.getPosition(currDatePage));
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