package com.example.empty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.empty.databinding.FragmentPlannerWeeklyBinding;
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
import java.util.Arrays;

/**
 */
public class PlannerWeeklyFragment extends Fragment {

    private FragmentPlannerWeeklyBinding binding;

    private DateStr currDateStrObj;

    private BarChart barChart;

    private int[][] weeklyData;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        weeklyData = new int[7][4];
        binding = FragmentPlannerWeeklyBinding.inflate(inflater, container, false);
        Context context = getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String currDateStr = sharedPreferences.getString("currDateStr", new DateStr().getDateStr());
        currDateStrObj = new DateStr(currDateStr);
        String uid = sharedPreferences.getString("uid", "");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().
                child("planner").child(uid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    try {
                        String dataDateStr = childSnapshot.getKey();
                        assert dataDateStr != null;
                        DateStr dataDateStrObj = new DateStr(dataDateStr);
                        if (currDateStrObj.isWeekly(dataDateStr)) {
                            int dayOfTheWeek = currDateStrObj.comp(dataDateStrObj);
                            int index = currDateStrObj.getDayOfTheWeek() - 1 - dayOfTheWeek;
                            for (DataSnapshot grandChildSnapshot : childSnapshot.getChildren()) {
                                PlannerItemFirebase itemFirebase = grandChildSnapshot.getValue(PlannerItemFirebase.class);
                                assert itemFirebase != null;
                                String durationTxt = itemFirebase.getDuration();
                                int duration = Integer.parseInt(durationTxt.substring(0, durationTxt.indexOf(" ")));
                                int workType = itemFirebase.getWorkType();
                                switch (workType) {
                                    case R.drawable.yellows:
                                        // this.category = "Class"
                                        weeklyData[index][1] += duration;
                                        break;
                                    case R.drawable.triangle_48:
                                        // this.category = "Team";
                                        weeklyData[index][2] += duration;
                                        break;
                                    case R.drawable.star_2_xxl:
                                        // this.category = "Sport";
                                        weeklyData[index][3] += duration;
                                        break;
                                    default:
                                        // this.category = "Work";
                                        weeklyData[index][0] += duration;
                                        break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.d("datacheck", e.getClass().getSimpleName());
                        Log.d("datacheck", e.getMessage());
                    }
                }
                adjustData();

                Log.d("datacheck", Arrays.deepToString(weeklyData));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });



        return binding.getRoot();
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

    private void adjustData() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );


        barChart = new BarChart(getContext());
        barChart.setLayoutParams(layoutParams);
        LinearLayout chartContainer = binding.containerForChartPlanner;
        chartContainer.addView(barChart);

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < weeklyData.length; ++i) {
            entries.add(new BarEntry(i, new float[]{weeklyData[i][0],
                    weeklyData[i][1], weeklyData[i][2], weeklyData[i][3]}));
        }

        BarDataSet barDataSet = new BarDataSet(entries, "");
        int[] colors = new int[] {Color.RED, Color.parseColor("#fab728"), Color.parseColor("#82cc58"), Color.parseColor("#69d9f5")};
        barDataSet.setColors(colors);

        barDataSet.setValueFormatter(new ValueFormatter() {
            @SuppressLint("DefaultLocale")
            @Override
            public String getFormattedValue(float value) {
                // Format the value as needed, e.g., convert to int or round to specific decimal places
                // and return as String
                return String.format("%.2f", value);
            }
        });

        barDataSet.setDrawValues(true);
        barDataSet.setStackLabels(new String[]{"Work", "Class", "Team", "Sport"});
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barData.setDrawValues(false);
        barData.setBarWidth(0.75f);
        XAxis xAxis = barChart.getXAxis();



        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Convert the float value to an integer index for the barEntries ArrayList
                int index = (int) value;
                if (index >= 0 && index < entries.size()) {
                    // Return the custom label for the corresponding bar entry
                    switch (index) {
                        case 0:
                            return "Sun";
                        case 1:
                            return "Mon";
                        case 2:
                            return "Tue";
                        case 3:
                            return "Wed";
                        case 4:
                            return "Thu";
                        case 5:
                            return "Fri";
                        case 6:
                            return "Sat";
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

    }
}