package com.example.empty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.empty.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences.Editor edit;
    BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.empty.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        edit = sharedPreferences.edit();
        setContentView(binding.getRoot());
        int frame = R.id.frame_layout;
        replaceFragment(frame, new Map_frag());
        bottomNavigationView = binding.bottomNavigationView;
        binding.bottomNavigationView.setSelectedItemId(R.id.map);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.map:
                    replaceFragment(frame, new Map_frag());
                    break;
                case R.id.stat:
                    edit.putString("currDatePage", "Daily");
                    edit.putString("currDateStr", new DateStr().getDateStr());
                    edit.apply();
                    replaceFragment(frame, new Stat_frag());
                    break;
                case R.id.planner:
                    edit.putString("currDatePage", "Daily");
                    edit.putString("currDateStr", new DateStr().getDateStr());
                    edit.apply();
                    replaceFragment(frame, new Planner_frag());
                    break;
            }
            return true;
        });
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