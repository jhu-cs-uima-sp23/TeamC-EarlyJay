package com.example.empty;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.empty.databinding.FragmentStatBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.tankery.lib.circularseekbar.CircularSeekBar;


public class Stat_frag extends Fragment {

    private FragmentStatBinding binding;

    private String uid;

    private String currDatePage;

    private String currDateStr;

    private MainActivity main;

    private Context context;

    private DatabaseReference reference;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;

    private StatsCalculator todayDailyStats;
    private StatsCalculator dailyPastStats;
    private StatsCalculator todayWeeklyStats;
    private StatsCalculator weeklyPastStats;
    private StatsCalculator todayMonthlyStats;
    private StatsCalculator monthlyPastStats;
    private BarChart barChart;
    private ProgressBar progressBar;
    private TextView txtper;
    private TextView featherNumber;
    int featherAmount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentStatBinding.inflate(inflater, container, false);

        main = (MainActivity) getActivity();
        context = main.getApplicationContext();




        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        edit = sharedPreferences.edit();

        uid = sharedPreferences.getString("uid", "");
        currDateStr = sharedPreferences.getString("currDateStr", new DateStr().getDateStr());
        DateStr now = new DateStr(currDateStr);
        currDatePage = sharedPreferences.getString("currDatePage", "Daily");

        switch(currDatePage) {
            case "Weekly":
                main.replaceFragment(R.id.stuff_on_dates, new WeeklyStatsFragment());
                break;
            case "Monthly":
                main.replaceFragment(R.id.stuff_on_dates, new MonthlyStatsFragment());
                break;
            default:
                main.replaceFragment(R.id.stuff_on_dates, new DailyStatsFragment());
                break;
        }

        reference = FirebaseDatabase.getInstance().getReference().
                child("users").child(uid);

        todayDailyStats = new StatsCalculator();
        dailyPastStats = new StatsCalculator();
        todayWeeklyStats = new StatsCalculator();
        weeklyPastStats = new StatsCalculator();
        todayMonthlyStats = new StatsCalculator();
        monthlyPastStats = new StatsCalculator();

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    try {
                        LocationStruct locStruct = childSnapshot.getValue(LocationStruct.class);
                        if (locStruct == null) {
                            // client is null, error out
                            Log.e("DBREF:", "Data is unexpectedly null");
                        } else {
                            String dataDateStr = locStruct.getDateStr();
                            if (now.isDaily(dataDateStr)) {
                                UpdateTimeInfo(locStruct, todayDailyStats);
                            }
                            if (now.isWeekly(dataDateStr)) {
                                UpdateTimeInfo(locStruct, todayWeeklyStats);
                            }
                            if (now.isMonthly(dataDateStr)) {
                                UpdateTimeInfo(locStruct, todayMonthlyStats);
                            }
                            if (now.isPastDay(dataDateStr)) {
                                UpdateTimeInfo(locStruct, dailyPastStats);
                            }
                            if (now.isPastWeek(dataDateStr)) {
                                UpdateTimeInfo(locStruct, weeklyPastStats);
                            }
                            if (now.isPastMonth(dataDateStr)) {
                                UpdateTimeInfo(locStruct, monthlyPastStats);
                            }
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        Spinner dateSpinner = binding.spinner2;
        ArrayAdapter myAdapter = (ArrayAdapter) dateSpinner.getAdapter();
        dateSpinner.setSelection(myAdapter.getPosition(currDatePage));
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                if (selected.equals(currDatePage)) {
                    return;
                }
                edit.putString("currDatePage", selected);
                edit.putString("currDateStr", new DateStr().getDateStr());
                edit.apply();
                main.replaceFragment(R.id.frame_layout, new Stat_frag());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });

        ImageButton leftButton = binding.leftRollButton;
        ImageButton rightButton = binding.rightRollButton;

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dateStr = "";
                switch(currDatePage) {
                    case "Weekly":
                        dateStr = now.getPastDay(7);
                        break;
                    case "Monthly":
                        dateStr = now.getPastDay(new DateStr(now.getPastDay(now.getDay())).getMonthDays());
                        break;
                    default:
                        dateStr = now.getPastDay(1);
                        break;
                }
                edit.putString("currDateStr", dateStr);
                edit.apply();
                main.replaceFragment(R.id.frame_layout, new Stat_frag());
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dateStr = "";
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
                edit.putString("currDateStr", dateStr);
                edit.apply();
                main.replaceFragment(R.id.frame_layout, new Stat_frag());
            }
        });

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        barChart = new BarChart(getContext());
        barChart.setLayoutParams(layoutParams);
        LinearLayout chartContainer = binding.containerForChart;
        chartContainer.addView(barChart);

        ArrayList<BarEntry> entries = new ArrayList<>();

        if (currDatePage == "Daily") {
            int work_time = todayDailyStats.getWorkTime();
            int class_time = todayDailyStats.getClassTime();
            int team_time = todayDailyStats.getTeamTime();
            int sports_time = todayDailyStats.getSportTime();
            entries.add(new BarEntry(0, (float)work_time)); // Work
            entries.add(new BarEntry(1, (float)class_time)); // Study
            entries.add(new BarEntry(2, (float)team_time)); // Class
            entries.add(new BarEntry(3, (float)sports_time)); // Sports
        }

        BarDataSet barDataSet = new BarDataSet(entries, "");
        int[] colors = new int[] {Color.CYAN, Color.BLUE, Color.GRAY, Color.BLACK};
        barDataSet.setColors(colors);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf(value);
            }
        });
        barData.setDrawValues(true);
        barData.setBarWidth(0.5f);
        initBarChart();

        int completion = (int) calculateCompletion(currDatePage);
        progressBar = binding.progressBarDaily;
        progressBar.setProgress(completion*100);


        featherNumber = binding.featherNumber;
        featherAmount = getFeather(currDatePage);
        featherNumber.setText(Integer.toString(featherAmount));

        return binding.getRoot();
    }

    private void UpdateTimeInfo(LocationStruct locStruct, StatsCalculator calculator) {
        if (! locStruct.getComplete()) {
            calculator.addToFail(locStruct.getTimeInterval(), locStruct.getFeatherNum());
        }
        String[] types = locStruct.getType().split(" ");
        String type = types[types.length - 1];

        switch (type) {
            case "Work":
                calculator.addToWork(locStruct.getTimeInterval());
                break;
            case "Class":
                calculator.addToClass(locStruct.getTimeInterval());
                break;
            case "Team":
                calculator.addToTeam(locStruct.getTimeInterval());
                break;
            default:
                calculator.addToSport(locStruct.getTimeInterval());
                break;
        }
    }

    private void initBarChart(){
        //hiding the grey background of the chart, default false if not set
        barChart.setDrawGridBackground(false);
        //remove the bar shadow, default false if not set
        barChart.setDrawBarShadow(false);
        //remove border of the chart, default false if not set
        barChart.setDrawBorders(false);

        //remove the description label text located at the lower right corner
        Description description = new Description();
        description.setEnabled(false);
        barChart.setDescription(description);

        //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
        barChart.animateY(1000);
        //setting animation for x-axis, the bar will pop up separately within the time we set
        barChart.animateX(1000);

        XAxis xAxis = barChart.getXAxis();
        //change the position of x-axis to the bottom
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //set the horizontal distance of the grid line
        xAxis.setGranularity(1f);
        //hiding the x-axis line, default true if not set
        xAxis.setDrawAxisLine(false);
        //hiding the vertical grid lines, default true if not set
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = barChart.getAxisLeft();
        //hiding the left y-axis line, default true if not set
        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = barChart.getAxisRight();
        //hiding the right y-axis line, default true if not set
        rightAxis.setDrawAxisLine(false);

        Legend legend = barChart.getLegend();
        //setting the shape of the legend form to line, default square shape
        legend.setForm(Legend.LegendForm.LINE);
        //setting the text size of the legend
        legend.setTextSize(11f);
        //setting the alignment of legend toward the chart
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        //setting the stacking direction of legend
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //setting the location of legend outside the chart, default false if not set
        legend.setDrawInside(false);

    }

    private float calculateCompletion(String currDatePage) {
        if (currDatePage.equals("Daily")) {
            int totalTime = todayDailyStats.getTotalCompleteTask() + todayDailyStats.getTotalFailTask();
            if (totalTime != 0 ) {
                return (float)todayDailyStats.getTotalFailTask() / totalTime;
            }
        } else if (currDatePage.equals("Weekly")) {
            int totalTime = todayWeeklyStats.getTotalCompleteTask() + todayWeeklyStats.getTotalFailTask();
            if (totalTime != 0 ) {
                return (float)todayWeeklyStats.getTotalFailTask() / totalTime;
            }
        } else {
            int totalTime = todayMonthlyStats.getTotalCompleteTask() + todayMonthlyStats.getTotalFailTask();
            if (totalTime != 0 ) {
                return (float)todayMonthlyStats.getTotalFailTask() / totalTime;
            }
        }
        return 0;
    }

    private int getFeather(String currDatePage) {
        if (currDatePage.equals("Daily")) {
            return todayDailyStats.getTotalFeather();
        } else if (currDatePage.equals("Weekly")) {
            return todayWeeklyStats.getTotalFeather();
        }
        return todayMonthlyStats.getTotalFeather();
    }


}