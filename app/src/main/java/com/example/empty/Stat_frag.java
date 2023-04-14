package com.example.empty;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.empty.databinding.FragmentStatBinding;
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

    private MainActivity main;

    private Context context;

    private DatabaseReference reference;

    private SharedPreferences sharedPreferences;

    private StatsCalculator todayDailyStats;
    private StatsCalculator dailyPastStats;
    private StatsCalculator todayWeeklyStats;
    private StatsCalculator weeklyPastStats;
    private StatsCalculator todayMonthlyStats;
    private StatsCalculator monthlyPastStats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentStatBinding.inflate(inflater, container, false);

        main = (MainActivity) getActivity();
        context = main.getApplicationContext();


        main.replaceFragment(R.id.stuff_on_dates, new DailyStatsFragment());
        currDatePage = "Daily";

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        uid = sharedPreferences.getString("uid", "");
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
                            DateStr now = new DateStr();
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

        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                System.out.println("selected item: " + selected);
                if (selected.equals(currDatePage)) {
                    return;
                }
                switch(selected) {
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
                currDatePage = selected;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


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

}