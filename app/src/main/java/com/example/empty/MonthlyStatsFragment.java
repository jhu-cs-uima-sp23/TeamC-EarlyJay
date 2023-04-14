package com.example.empty;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.empty.databinding.FragmentMonthlyStatsBinding;
import com.example.empty.databinding.FragmentStatBinding;

import java.time.Month;

/**
 */
public class MonthlyStatsFragment extends Fragment {

    private FragmentMonthlyStatsBinding binding;

    private DateStr currDay;

    private MainActivity main;

    private Context context;

    private String[] Months = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentMonthlyStatsBinding.inflate(inflater, container, false);
        main = (MainActivity) getActivity();
        context = main.getApplicationContext();

        currDay = new DateStr();

        binding.yearNumMonthly.setText(Integer.toString(currDay.getYear()));
        binding.thisMonth.setText(Months[currDay.getMonth() - 1]);
        binding.lastMonth.setText(Months[(currDay.getMonth() - 2 + 12) % 12]);
        binding.nextMonth.setText(Months[currDay.getMonth() % 12]);


        return binding.getRoot();
    }

}