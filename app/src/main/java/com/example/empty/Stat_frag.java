package com.example.empty;

import android.animation.ObjectAnimator;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.empty.databinding.FragmentStatBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

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
    private XAxis xAxis;

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
                        String dataDateStr = childSnapshot.getKey();
                        if (dataDateStr == null) {
                            // client is null, error out
                            Log.e("DBREF:", "Data is unexpectedly null");
                        } else {
                            if (now.isDaily(dataDateStr)) {
                                for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
                                    LocationStruct locStruct = grandChildSnapshot.getValue(LocationStruct.class);
                                    UpdateTimeInfo(locStruct, todayDailyStats);
                                }
                            }
                            if (now.isWeekly(dataDateStr)) {
                                for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
                                    LocationStruct locStruct = grandChildSnapshot.getValue(LocationStruct.class);
                                    UpdateTimeInfo(locStruct, todayWeeklyStats);
                                }
                            }
                            if (now.isMonthly(dataDateStr)) {
                                for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
                                    LocationStruct locStruct = grandChildSnapshot.getValue(LocationStruct.class);
                                    UpdateTimeInfo(locStruct, todayMonthlyStats);
                                }
                            }
                            if (now.isPastDay(dataDateStr)) {
                                for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
                                    LocationStruct locStruct = grandChildSnapshot.getValue(LocationStruct.class);
                                    UpdateTimeInfo(locStruct, dailyPastStats);
                                }
                            }
                            if (now.isPastWeek(dataDateStr)) {
                                for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
                                    LocationStruct locStruct = grandChildSnapshot.getValue(LocationStruct.class);
                                    UpdateTimeInfo(locStruct, weeklyPastStats);
                                }
                            }
                            if (now.isPastMonth(dataDateStr)) {
                                for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
                                    LocationStruct locStruct = grandChildSnapshot.getValue(LocationStruct.class);
                                    UpdateTimeInfo(locStruct, monthlyPastStats);
                                }
                            }
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
                adjustData();

                Log.d("datacheck", "adjusted data");

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

        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(false);
        barChart.setPinchZoom(false);
        barChart.setDrawBarShadow(false);

        barChart.setDrawGridBackground(false);
        //remove the bar shadow, default false if not set
        barChart.setDrawBarShadow(false);
        //remove border of the chart, default false if not set
        barChart.setDrawBorders(false);

        //remove the description label text located at the lower right corner
        Description description = new Description();
        description.setEnabled(false);
        barChart.setDescription(description);
        barChart.animateY(1000);
        barChart.animateX(1000);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(true);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawAxisLine(false);
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);

        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setTextColor(R.color.red);
    }

    private float calculateCompletion(String currDatePage) {
        if (currDatePage.equals("Daily")) {
            int totalTime = todayDailyStats.getTotalCompleteTask() + todayDailyStats.getTotalFailTask();
            if (totalTime != 0 ) {
                return (float)todayDailyStats.getTotalCompleteTask() / totalTime;
            }
        } else if (currDatePage.equals("Weekly")) {
            int totalTime = todayWeeklyStats.getTotalCompleteTask() + todayWeeklyStats.getTotalFailTask();
            if (totalTime != 0 ) {
                return (float)todayWeeklyStats.getTotalCompleteTask() / totalTime;
            }
        } else {
            int totalTime = todayMonthlyStats.getTotalCompleteTask() + todayMonthlyStats.getTotalFailTask();
            if (totalTime != 0 ) {
                return (float)todayMonthlyStats.getTotalCompleteTask() / totalTime;
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

    private double calculateProductionRate(String currDatePage) {
        double currRate= 0;
        double pastRate = 0;
        double productionRate;
        switch(currDatePage) {
            case "Weekly":
                currRate = (todayWeeklyStats.getTotalCompleteTime() + todayWeeklyStats.getTotalFailTime()== 0) ?
                        0 : (double) todayWeeklyStats.getTotalCompleteTime() /
                        (todayWeeklyStats.getTotalCompleteTime() + todayWeeklyStats.getTotalFailTime());
                pastRate = (weeklyPastStats.getTotalCompleteTime() + weeklyPastStats.getTotalFailTime()== 0) ?
                        0 : (double) weeklyPastStats.getTotalCompleteTime() /
                        (weeklyPastStats.getTotalCompleteTime() + weeklyPastStats.getTotalFailTime());
                Log.d("datacheck", "this week: " + currRate);
                Log.d("datacheck", "last week: " + pastRate);
                break;
            case "Monthly":
                currRate = (todayMonthlyStats.getTotalCompleteTime() + todayMonthlyStats.getTotalFailTime()== 0) ?
                        0 : (double) todayMonthlyStats.getTotalCompleteTime() /
                        (todayMonthlyStats.getTotalCompleteTime() + todayMonthlyStats.getTotalFailTime());
                pastRate = (monthlyPastStats.getTotalCompleteTime() + monthlyPastStats.getTotalFailTime()== 0) ?
                        0 : (double) monthlyPastStats.getTotalCompleteTime() /
                        (monthlyPastStats.getTotalCompleteTime() + monthlyPastStats.getTotalFailTime());
                Log.d("datacheck", "this month: " + currRate);
                Log.d("datacheck", "last month: " +  pastRate);
                break;
            default:
                currRate = (todayDailyStats.getTotalCompleteTime() + todayDailyStats.getTotalFailTime()== 0) ?
                        0 : (double) todayDailyStats.getTotalCompleteTime() /
                        (todayDailyStats.getTotalCompleteTime() + todayDailyStats.getTotalFailTime());
                pastRate = (dailyPastStats.getTotalCompleteTime() + dailyPastStats.getTotalFailTime()== 0) ?
                        0 : (double) dailyPastStats.getTotalCompleteTime() /
                        (dailyPastStats.getTotalCompleteTime() + dailyPastStats.getTotalFailTime());
                Log.d("datacheck", "today: " + currRate);
                Log.d("datacheck", "yesterday: " + pastRate);
                break;
        }
        productionRate = (currRate == 0) ?
                0 : (currRate - pastRate) / currRate;
        Log.d("datacheck", "production rate: " + productionRate);
        return productionRate;
    }

    private void adjustData() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );


        barChart = new BarChart(getContext());
        barChart.setLayoutParams(layoutParams);
        LinearLayout chartContainer = binding.containerForChart;
        chartContainer.addView(barChart);

        ArrayList<BarEntry> entries = new ArrayList<>();

        int work_time;
        int class_time;
        int team_time;
        int sports_time;
        String dayinfo = "";

        switch(currDatePage) {
            case "Weekly":
                work_time = todayWeeklyStats.getWorkTime();
                class_time = todayWeeklyStats.getClassTime();
                team_time = todayWeeklyStats.getTeamTime();
                sports_time = todayWeeklyStats.getSportTime();;
                dayinfo = "week";
                break;
            case "Monthly":
                work_time = todayMonthlyStats.getWorkTime();
                class_time = todayMonthlyStats.getClassTime();
                team_time = todayMonthlyStats.getTeamTime();
                sports_time = todayMonthlyStats.getSportTime();
                dayinfo = "month";
                break;
            default:
                work_time = todayDailyStats.getWorkTime();
                class_time = todayDailyStats.getClassTime();
                team_time = todayDailyStats.getTeamTime();
                sports_time = todayDailyStats.getSportTime();
                dayinfo = "day";
                break;
        }
        entries.add(new BarEntry(0,(float)work_time,"Work")); // Work
        entries.add(new BarEntry(1,(float)class_time,"Class")); // Study
        entries.add(new BarEntry(2,(float)team_time,"Team")); // Class
        entries.add(new BarEntry(3,(float)sports_time,"Sports")); // Sports

        BarDataSet barDataSet = new BarDataSet(entries, "");
        int[] colors = new int[] {Color.RED, Color.parseColor("#fab728"), Color.parseColor("#82cc58"), Color.parseColor("#69d9f5")};
        barDataSet.setColors(colors);

        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Format the value as needed, e.g., convert to int or round to specific decimal places
                // and return as String
                return String.format("%.2f", value);
            }
        });

        barDataSet.setDrawValues(true);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barData.setDrawValues(false);
        barData.setBarWidth(0.5f);
        xAxis = barChart.getXAxis();

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Convert the float value to an integer index for the barEntries ArrayList
                int index = (int) value;
                if (index >= 0 && index < entries.size()) {
                    // Return the custom label for the corresponding bar entry
                    switch (index) {
                        case 0:
                            return "Work";
                        case 1:
                            return "Study";
                        case 2:
                            return "Class";
                        case 3:
                            return "Sports";
                        default:
                            return "";
                    }
                } else {
                    return "";
                }
            }
        });
        // Get the X-axis from the BarChart object
        initBarChart();


        double completion = calculateCompletion(currDatePage) * 100;
        String completion_formatted = getString(R.string.percentage_display, completion);
        Log.d("datacheck", "completion: " + completion);
        progressBar = binding.progressBarDaily;

        if ((int) completion == 0) {
            Log.d("datacheck", "completion is 0");
            progressBar.setProgress(0);
        } else {
            // Create an ObjectAnimator to animate the ProgressBar progress
            ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar,
                    "progress", 0, (int) completion);

            // Set the duration of the animation (in milliseconds)
            progressAnimator.setDuration(1500);

            // Start the animation
            progressAnimator.start();

        }
        // progressBar.setProgress((int) completion);
        TextView progressPercentage = binding.progressPercentage;
        progressPercentage.setText(completion_formatted);

        featherNumber = binding.featherNumber;
        featherAmount = getFeather(currDatePage);
        Log.d("datacheck", "featherAmount: " + featherAmount);
        featherNumber.setText(Integer.toString(featherAmount));

        double productionPercentCalculated = calculateProductionRate(currDatePage) * 100;
        Log.d("datacheck", "completionPercentCalculated: " + productionPercentCalculated);
        if (productionPercentCalculated < 0) {
            String progress_formatted = getString(R.string.percentage_display, -1 * productionPercentCalculated);
            ImageView imageArrow = binding.upImage;
            imageArrow.setRotation(180);
            imageArrow.setColorFilter(getResources().getColor(R.color.red));
            binding.productivityPercentage.setText(progress_formatted);
            binding.productivityPercentage.setTextColor(getResources().getColor(R.color.red));
            binding.productivityComment.setText("less productive than previous " + dayinfo +  "... Focus!");

        } else {
            String progress_formatted = getString(R.string.percentage_display, productionPercentCalculated);
            binding.productivityPercentage.setText(progress_formatted);
            binding.productivityComment.setText("more productive than previous " + dayinfo +  "!");
        }

    }
}