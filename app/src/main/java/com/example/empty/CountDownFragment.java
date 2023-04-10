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
import android.widget.Button;

import com.example.empty.databinding.FragmentCountDownBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.empty.databinding.FragmentDwmSearchFabBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 */
public class CountDownFragment extends Fragment {

    private FragmentCountDownBinding binding;
    Context context;
    private MainActivity mainActivity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;

    CountDownTimer cTimer = null;


    int numSeconds;

    int hour;
    int min;
    int sec;

    int milisec;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        binding = FragmentCountDownBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        edit = sharedPreferences.edit();

        String category = sharedPreferences.getString("category", "");
        binding.taskCat.setText(category);
        numSeconds = sharedPreferences.getInt("numSeconds", 0);
        hour = numSeconds / 3600;
        System.out.println("hour = " + hour);
        min = (numSeconds %  3600) / 60;
        System.out.println("minutes = " + min);
        sec = numSeconds % 60;
        System.out.println("seconds = " + sec);
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
                showBottomNavigationView();
            }

        };

        hideBottomNavigationView();
        cTimer.start();

        binding.cancelButton.setOnClickListener(v -> {
            cancelTimer();
            edit.putInt("numSeconds", hour * 3600 + min * 60 + sec);
            edit.apply();
            mainActivity.replaceFragment(R.id.stuff_on_map, new OnQuitWarning());
            showBottomNavigationView();
        });

        cTimer.start();



    }

    private void setRemainTime(int hour, int min, int sec) {
        String hourStr = "0" + hour;
        String minStr = (min < 10) ? "0" + min : String.valueOf(min);
        String secStr = (sec < 10) ? "0" + sec : String.valueOf(sec);
        binding.remaining.setText(hourStr + ":" + minStr + ":" + secStr + "    Remains");
    }



    private void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }


    private void hideBottomNavigationView() {
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);
    }

    private void showBottomNavigationView() {
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.animate().translationY(0).setDuration(300);
    }

}