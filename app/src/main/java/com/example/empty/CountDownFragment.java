package com.example.empty;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.empty.databinding.FragmentCountDownBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 */
public class CountDownFragment extends Fragment {

    private FragmentCountDownBinding binding;
    Context context;
    private MainActivity mainActivity;
    private SharedPreferences.Editor edit;

    CountDownTimer cTimer = null;


    int numSeconds;

    int hour;
    int min;
    int sec;

    int milisec;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = requireActivity().getApplicationContext();
        binding = FragmentCountDownBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        edit = sharedPreferences.edit();

        String category = sharedPreferences.getString("category", "");
        binding.taskCat.setText(category);
        numSeconds = sharedPreferences.getInt("numSeconds", 0);
        hour = numSeconds / 3600;
        min = (numSeconds %  3600) / 60;
        sec = numSeconds % 60;
        milisec = numSeconds * 1000;

        setRemainTime(hour, min, sec);

        //Declare timer
        cTimer = new CountDownTimer(milisec, 1000) {

            public void onTick(long millisUntilFinished) {
                if (sec == 0) {
                    sec = 59;
                    if (min == 0) {
                        hour--;
                        min = 59;
                    } else {
                        min--;
                    }
                } else {
                    sec--;
                }
                setRemainTime(hour, min, sec);
                // logic to set the EditText could go here
            }

            public void onFinish() {
                mainActivity.replaceFragment(R.id.stuff_on_map, new CompleteFragment());
            }

        };

        hideBottomNavigationView();

        binding.cancelButton.setOnClickListener(v -> {
            cancelTimer();
            edit.putInt("numSeconds", hour * 3600 + min * 60 + sec);
            edit.apply();
            mainActivity.replaceFragment(R.id.stuff_on_map, new OnQuitWarning());
        });

        cTimer.start();



    }

    private void setRemainTime(int hour, int min, int sec) {
        String hourStr = "0" + hour;
        String minStr = (min < 10) ? "0" + min : String.valueOf(min);
        String secStr = (sec < 10) ? "0" + sec : String.valueOf(sec);
        String formatted = getString(R.string.count_down_string, hourStr, minStr, secStr);
        binding.remaining.setText(formatted);
    }



    private void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }


    private void hideBottomNavigationView() {
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);
    }

}