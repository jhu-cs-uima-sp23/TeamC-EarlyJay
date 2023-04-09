package com.example.empty;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.preference.PreferenceManager;
import android.util.Log;
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

    private CircularSeekBar progressCircular;

    private final int MAX = 120;

    private int factorProgress;
    private Context context;
    private MainActivity main;

    private FusedLocationProviderClient fusedLocationClient;

    private double latitude;
    private double longitude;
    private Spinner mSpinner;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    private Map_child_viewModel shared_data;
    private float featherCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        binding = FragmentPopupStartBinding.inflate(inflater, container, false);
        shared_data = new ViewModelProvider(requireActivity()).get(Map_child_viewModel.class);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        main = (MainActivity) getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        edit = sharedPreferences.edit();

        progressCircular = binding.circularSeekBar;


        progressCircular.setMax(MAX);
        factorProgress = 12;
        featherCount = 6;

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);


        binding.saveButton.setOnClickListener(v -> {
            int numSeconds = factorProgress * 5 * 60;
            if (factorProgress == 0) {
                Toast.makeText(context,
                        "Time interval needs to be greater than 0!", Toast.LENGTH_LONG).show();
                numSeconds = 1;
//                return;
            }
            SpinnerItem selectedItem = (SpinnerItem) binding.spinner.getSelectedItem();
            String category = selectedItem.getText();
            edit.putString("category", category);
//            category icon
            edit.putInt("workType", selectedItem.getImageResId());
            edit.putInt("numSeconds", numSeconds);
            edit.putFloat("featherCount", featherCount);
            edit.putFloat("latitude", (float) latitude);
            edit.putFloat("longitude", (float) longitude);
            edit.apply();
            main.replaceFragment(R.id.stuff_on_map, new CountDownFragment());
        });

        binding.button.setOnClickListener(v -> {
            main.replaceFragment(R.id.stuff_on_map, new dwm_search_fab());
            shared_data.getData().observe(getViewLifecycleOwner(), data -> {
                //TODO: you can add stuff here to get data
            });
        });

        progressCircular.setOnSeekBarChangeListener(this);


        mSpinner = binding.spinner;
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
//                R.array.type_array, android.R.layout.simple_spinner_item);
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSpinner.setAdapter(adapter);

        List<SpinnerItem> spinnerItems = new ArrayList<>();
        spinnerItems.add(new SpinnerItem(R.drawable.circle_dashed_6_xxl, "Work"));
        spinnerItems.add(new SpinnerItem(R.drawable.yellows, "Class"));
        spinnerItems.add(new SpinnerItem(R.drawable.triangle_48, "Team"));
        spinnerItems.add(new SpinnerItem(R.drawable.star_2_xxl, "Sports"));

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
                           // Log.d("Location", "Longitude: " + longitude + " Latitude: " + latitude);

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
        binding.countNum.setText(factorProgress * 5 + " min");
        featherCount = (float) factorProgress / 2;

        binding.rewardLine.setText("Reward: " + featherCount);
    }
        @Override
        public void onStopTrackingTouch(CircularSeekBar seekBar) {


        }

        @Override
        public void onStartTrackingTouch(CircularSeekBar seekBar) {

        }

}