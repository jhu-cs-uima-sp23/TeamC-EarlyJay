package com.example.empty;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.empty.databinding.FragmentDailyStatsBinding;
import com.example.empty.databinding.FragmentMonthlyStatsBinding;

/**
 */
public class DailyStatsFragment extends Fragment {

    private FragmentDailyStatsBinding binding;

    private DateStr currDay;

    private MainActivity main;

    private Context context;

    private final String[] Months = {"January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November", "December"};

    private final String[] DayOfTheWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentDailyStatsBinding.inflate(inflater, container, false);
        main = (MainActivity) getActivity();
        context = main.getApplicationContext();

        currDay = new DateStr();

        String yearNumDaily = Months[currDay.getMonth() - 1] + " " + currDay.getYear();

        binding.yearNumDaily.setText(yearNumDaily);
        binding.todayDate.setText(Integer.toString(currDay.getDay()));
        binding.todayDay.setText(DayOfTheWeek[currDay.getDayOfTheWeek() - 1]);

        binding.yesterdayDate.setText(Integer.toString(new DateStr(currDay.getPastDay(1)).getDay()));
        binding.yesterdayDay.setText(DayOfTheWeek[new DateStr(currDay.getPastDay(1)).getDayOfTheWeek() - 1]);

        return binding.getRoot();
    }
}