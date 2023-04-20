package com.example.empty;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.empty.databinding.FragmentOnQuitWarningBinding;
import com.example.empty.databinding.FragmentPopupStartBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OnQuitWarning extends Fragment {
    private FragmentOnQuitWarningBinding binding;
    private MainActivity main;
    private Context context;

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        binding = FragmentOnQuitWarningBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        main = (MainActivity) getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        int reward_amount = sharedPreferences.getInt("featherCount", 0);
        String formatted = getString(R.string.warning_txt, reward_amount);
        binding.warningTxt.setText(formatted);
        binding.yes.setOnClickListener(v -> {
            main.replaceFragment(R.id.stuff_on_map, new CountDownFragment());
        });

        binding.no.setOnClickListener(v -> {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            float longitude = sharedPreferences.getFloat("longitude", 0);
            float latitude = sharedPreferences.getFloat("latitude", 0);
            String uid = sharedPreferences.getString("uid", "");
            String category = sharedPreferences.getString("category", "");
            int numSeconds = sharedPreferences.getInt("numSeconds", 0);
            int timeInterval = sharedPreferences.getInt("totalTimeInterval", 0);
            int featherCount = -1 * numSeconds / 300;
            rootNode = FirebaseDatabase.getInstance();
            reference = rootNode.getReference().child("users").child(uid).child(new DateStr().getDateStr());
            reference.push().setValue(new LocationStruct(latitude,longitude, false, category,
                    new DateStr().getDateStr(), timeInterval, featherCount));
            editor.putInt("complete_success", 2);
            editor.apply();
            main.replaceFragment(R.id.stuff_on_map, new dwm_search_fab());
        });

    }

    public void onResume() {
        super.onResume();
        hideBottomNavigationView();
    }

    @Override
    public void onStop() {
        super.onStop();
        showBottomNavigationView();
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