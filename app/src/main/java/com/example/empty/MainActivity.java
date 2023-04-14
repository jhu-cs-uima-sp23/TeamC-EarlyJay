package com.example.empty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.Bundle;

import com.example.empty.databinding.ActivityMainBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int frame = R.id.frame_layout;
        replaceFragment(frame, new Map_frag());
        binding.bottomNavigationView.setSelectedItemId(R.id.map);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.map:
                    replaceFragment(frame, new Map_frag());
                    break;
                case R.id.stat:
//                    replaceFragment(frame, new Stat_frag());
                    replaceFragment(frame,new Analytics());
                    break;
                case R.id.planner:
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
}