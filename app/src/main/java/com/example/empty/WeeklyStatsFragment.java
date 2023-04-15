package com.example.empty;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.empty.databinding.FragmentMonthlyStatsBinding;
import com.example.empty.databinding.FragmentWeeklyStatsBinding;

/**
 */
public class WeeklyStatsFragment extends Fragment {
    private FragmentWeeklyStatsBinding binding;

    private DateStr currDay;

    private int[] daysInAWeek;

    private SharedPreferences sharedPreferences;

    private MainActivity main;

    private Context context;

    private String[] Months = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        daysInAWeek = new int[7];

        binding = FragmentWeeklyStatsBinding.inflate(inflater, container, false);
        main = (MainActivity) getActivity();
        context = main.getApplicationContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        currDay = new DateStr(sharedPreferences.getString("currDateStr", new DateStr().getDateStr()));

        int i = 1;

        for (; i < currDay.getDayOfTheWeek(); ++i) {
            daysInAWeek[i - 1] = new DateStr(currDay.getPastDay(currDay.getDayOfTheWeek() - i)).getDay();
        }
        daysInAWeek[i - 1] = currDay.getDay();
        i++;
        for (; i <= 7; ++i) {
            daysInAWeek[i - 1] = new DateStr(currDay.getFutureDay(i - currDay.getDayOfTheWeek())).getDay();
        }

        binding.yearNumWeekly.setText(Integer.toString(currDay.getYear()));
        binding.monthNumWeekly.setText(Months[currDay.getMonth() - 1]);
        binding.sundayDate.setText(Integer.toString(daysInAWeek[0]));
        binding.mondayDate.setText(Integer.toString(daysInAWeek[1]));
        binding.tuesdayDate.setText(Integer.toString(daysInAWeek[2]));
        binding.wednesdayDate.setText(Integer.toString(daysInAWeek[3]));
        binding.thursdayDate.setText(Integer.toString(daysInAWeek[4]));
        binding.fridayDate.setText(Integer.toString(daysInAWeek[5]));
        binding.saturdayDate.setText(Integer.toString(daysInAWeek[6]));

        return binding.getRoot();
    }

}