package com.example.empty;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.example.empty.databinding.FragmentCompleteBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;


public class Analytics extends Fragment {

    private FragmentCompleteBinding binding;
    Context context;
    private BarChart barChart;;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        binding = FragmentCompleteBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_stat, container, false);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        barChart = new BarChart(getContext());
        barChart.setLayoutParams(layoutParams);
        LinearLayout chartContainer = view.findViewById(R.id.chartContainerForTime); // Replace with the ID of your chart container in your layout XML
        chartContainer.addView(barChart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 4f)); // Work
        entries.add(new BarEntry(1, 3f)); // Study
        entries.add(new BarEntry(2, 2f)); // Class
        entries.add(new BarEntry(3, 1f)); // Sports

        BarDataSet barDataSet = new BarDataSet(entries, "");
//        barDataSet.setColors(R.color.yellow, R.color.lightGray, Color.RED, Color.YELLOW);
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

        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisLeft().setAxisMaximum(5f);
        barChart.getAxisRight().setEnabled(false);

        barChart.setDrawGridBackground(false);
        barChart.getXAxis().setDrawGridLines(false);

        Legend legend = barChart.getLegend();
        legend.setEnabled(true);  // Set to true to enable the legend
        legend.setForm(Legend.LegendForm.SQUARE);  // Set the form of the legend
        legend.setWordWrapEnabled(true);  // Enable word wrapping for long legend labels
        initBarChart();

        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        int percentage = 80;
        progressBar.setProgress(20);
        progressBar.setIndeterminate(false);

        return view;
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


}
