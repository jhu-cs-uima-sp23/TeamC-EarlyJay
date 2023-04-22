package com.example.empty;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.empty.databinding.FragmentMonthlyStatsBinding;

/**
 */
public class MonthlyStatsFragment extends Fragment {

    private final String[] Months = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        com.example.empty.databinding.FragmentMonthlyStatsBinding binding = FragmentMonthlyStatsBinding.inflate(inflater, container, false);
        MainActivity main = (MainActivity) getActivity();
        assert main != null;
        Context context = main.getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        DateStr currDay = new DateStr(sharedPreferences.getString("currDateStr", new DateStr().getDateStr()));

        String yearNumMonthlyStr = Integer.toString(currDay.getYear());
        binding.yearNumMonthly.setText(yearNumMonthlyStr);
        binding.thisMonth.setText(Months[currDay.getMonth() - 1]);
        binding.lastMonth.setText(Months[(currDay.getMonth() - 2 + 12) % 12]);
        binding.nextMonth.setText(Months[currDay.getMonth() % 12]);

        return binding.getRoot();
    }

}