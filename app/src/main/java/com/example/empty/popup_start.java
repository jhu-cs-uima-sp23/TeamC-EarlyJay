package com.example.empty;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.empty.databinding.FragmentPopupStartBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import me.tankery.lib.circularseekbar.CircularSeekBar;


public class popup_start extends Fragment implements CircularSeekBar.OnCircularSeekBarChangeListener {

    private FragmentPopupStartBinding binding;

    private int factorProgress;
    private Context context;
    private MainActivity main;

    private FusedLocationProviderClient fusedLocationClient;

    private double latitude;
    private double longitude;
    private SharedPreferences.Editor edit;
    private int featherCount;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = requireActivity().getApplicationContext();
        binding = FragmentPopupStartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        main = (MainActivity) getActivity();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        edit = sharedPreferences.edit();

        CircularSeekBar progressCircular = binding.circularSeekBar;
        int MAX = 120;
        progressCircular.setMax(MAX);
        factorProgress = 12;
        featherCount = 12;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);


        binding.saveButton.setOnClickListener(v -> {
            int numSeconds = factorProgress * 5 * 60;
            if (factorProgress == 0) {
                Toast.makeText(context,
                        "Time interval needs to be greater than 0!", Toast.LENGTH_LONG).show();
               return;
            }
            SpinnerItem selectedItem = (SpinnerItem) binding.spinner.getSelectedItem();
            String[] categories = selectedItem.getText().split(" ");
            String category = categories[categories.length - 1];
            edit.putString("category", category);
//            category icon
            edit.putInt("workType", selectedItem.getImageResId());
            edit.putInt("numSeconds", numSeconds);
            edit.putInt("totalTimeInterval", factorProgress * 5);
            edit.putInt("featherCount", featherCount);
            edit.putFloat("latitude", (float) latitude);
            edit.putFloat("longitude", (float) longitude);
            edit.putBoolean("PlannerTask", false);
            edit.apply();
            startActivity(new Intent(main, CountDownActivity.class));
        });

        binding.button.setOnClickListener(v -> main.replaceFragment(R.id.stuff_on_map, new dwm_search_fab()));

        progressCircular.setOnSeekBarChangeListener(this);


        Spinner mSpinner = binding.spinner;

        List<SpinnerItem> spinnerItems = new ArrayList<>();
        spinnerItems.add(new SpinnerItem(R.drawable.circle_dashed_6_xxl, getString(R.string.work)));
        spinnerItems.add(new SpinnerItem(R.drawable.yellows, getString(R.string.class_)));
        spinnerItems.add(new SpinnerItem(R.drawable.triangle_48, getString(R.string.team)));
        spinnerItems.add(new SpinnerItem(R.drawable.star_2_xxl, getString(R.string.sport)));

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), spinnerItems);
        mSpinner.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    });
        } else {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onProgressChanged(CircularSeekBar circularSeekBar,
                                  float progress, boolean fromUser) {
        if (progress <= 0) {
            factorProgress = 0;
        } else if (progress >= 120){
            factorProgress = 24;
        } else {
            factorProgress = (int) (progress / 5);
        }
        String formatted = getString(R.string.num_picker_time_str, factorProgress * 5);
        binding.countNum.setText(formatted);
        featherCount = factorProgress;
        formatted = getString(R.string.reward, featherCount);
        binding.rewardLine.setText(formatted);
    }
    @Override
    public void onStopTrackingTouch(CircularSeekBar seekBar) {}
    @Override
    public void onStartTrackingTouch(CircularSeekBar seekBar) {}

}