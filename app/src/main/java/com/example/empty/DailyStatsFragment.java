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

import com.example.empty.databinding.FragmentDailyStatsBinding;

public class DailyStatsFragment extends Fragment {

    private final String[] Months = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};

    private final String[] DayOfTheWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        com.example.empty.databinding.FragmentDailyStatsBinding binding = FragmentDailyStatsBinding.inflate(inflater, container, false);
        MainActivity main = (MainActivity) getActivity();
        assert main != null;
        Context context = main.getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        DateStr currDay = new DateStr(sharedPreferences.getString("currDateStr", new DateStr().getDateStr()));

        String yearNumDaily = Months[currDay.getMonth() - 1] + " " + currDay.getYear();

        binding.yearNumDaily.setText(yearNumDaily);
        String currDayStr = Integer.toString(currDay.getDay());
        binding.todayDate.setText(currDayStr);
        binding.todayDay.setText(DayOfTheWeek[currDay.getDayOfTheWeek() - 1]);

        String yesterdayDayStr = Integer.toString(new DateStr(currDay.getPastDay(1)).getDay());
        binding.yesterdayDate.setText(yesterdayDayStr);
        binding.yesterdayDay.setText(DayOfTheWeek[new DateStr(currDay.getPastDay(1)).getDayOfTheWeek() - 1]);

        String tomorrowDateStr = Integer.toString(new DateStr(currDay.getFutureDay(1)).getDay());
        binding.tomorrowDate.setText(tomorrowDateStr);
        binding.tomorrowDay.setText(DayOfTheWeek[new DateStr(currDay.getFutureDay(1)).getDayOfTheWeek() - 1]);

        return binding.getRoot();
    }
}