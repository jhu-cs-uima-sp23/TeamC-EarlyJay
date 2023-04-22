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

import com.example.empty.databinding.FragmentWeeklyStatsBinding;

/**
 */
public class WeeklyStatsFragment extends Fragment {

    private final String[] Months = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        int[] daysInAWeek = new int[7];

        com.example.empty.databinding.FragmentWeeklyStatsBinding binding = FragmentWeeklyStatsBinding.inflate(inflater, container, false);
        MainActivity main = (MainActivity) getActivity();
        assert main != null;
        Context context = main.getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        DateStr currDay = new DateStr(sharedPreferences.getString("currDateStr", new DateStr().getDateStr()));

        int i = 1;

        for (; i < currDay.getDayOfTheWeek(); ++i) {
            daysInAWeek[i - 1] = new DateStr(currDay.getPastDay(currDay.getDayOfTheWeek() - i)).getDay();
        }
        daysInAWeek[i - 1] = currDay.getDay();
        i++;
        for (; i <= 7; ++i) {
            daysInAWeek[i - 1] = new DateStr(currDay.getFutureDay(i - currDay.getDayOfTheWeek())).getDay();
        }

        String yearNumWeeklyStr = Integer.toString(currDay.getYear());
        binding.yearNumWeekly.setText(yearNumWeeklyStr);
        String monthNumWeeklyStr = Months[currDay.getMonth() - 1];
        binding.monthNumWeekly.setText(monthNumWeeklyStr);
        String sundayDateStr = Integer.toString(daysInAWeek[0]);
        binding.sundayDate.setText(sundayDateStr);
        String mondayDateStr = Integer.toString(daysInAWeek[1]);
        binding.mondayDate.setText(mondayDateStr);
        String tuesdayDateStr = Integer.toString(daysInAWeek[2]);
        binding.tuesdayDate.setText(tuesdayDateStr);
        String wednesdayDateStr = Integer.toString(daysInAWeek[3]);
        binding.wednesdayDate.setText(wednesdayDateStr);
        String thursdayDateStr = Integer.toString(daysInAWeek[4]);
        binding.thursdayDate.setText(thursdayDateStr);
        String fridayDateStr = Integer.toString(daysInAWeek[5]);
        binding.fridayDate.setText(fridayDateStr);
        String saturdayDateStr = Integer.toString(daysInAWeek[6]);
        binding.saturdayDate.setText(saturdayDateStr);

        return binding.getRoot();
    }

}