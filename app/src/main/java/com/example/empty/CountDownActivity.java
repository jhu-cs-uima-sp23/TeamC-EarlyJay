package com.example.empty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import com.example.empty.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class CountDownActivity extends AppCompatActivity {

    Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    FloatingActionButton cancelButton;

    CountDownTimer cTimer = null;

    com.example.empty.databinding.ActivityMainBinding binding;


    int numSeconds;

    int hour;
    int min;
    int sec;

    int milisec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_count_down);

        cancelButton = findViewById(R.id.cancel_button);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        edit = sharedPreferences.edit();

        String category = sharedPreferences.getString("category", "");
        TextView taskCat = findViewById(R.id.task_cat);
        taskCat.setText(category);
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
                cancelButton.setVisibility(View.INVISIBLE);
                replaceFragment(R.id.stuff_on_count_down, new CompleteFragment());
            }

        };



        cancelButton.setOnClickListener(v -> {
            cancelTimer();
            edit.putInt("numSeconds", hour * 3600 + min * 60 + sec);
            edit.apply();
            cancelButton.setVisibility(View.INVISIBLE);
            replaceFragment(R.id.stuff_on_count_down, new OnQuitWarning());
        });

        cTimer.start();

    }


    private void setRemainTime(int hour, int min, int sec) {
        String hourStr = "0" + hour;
        String minStr = (min < 10) ? "0" + min : String.valueOf(min);
        String secStr = (sec < 10) ? "0" + sec : String.valueOf(sec);
        String formatted = getString(R.string.count_down_string, hourStr, minStr, secStr);
        TextView remaining = findViewById(R.id.remaining);
        remaining.setText(formatted);
    }



    private void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    public void replaceFragment(int frame, Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(frame, fragment);
        fragmentTransaction.commit();
    }
    public void removeFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

}