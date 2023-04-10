package com.example.empty;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.DialogFragmentNavigatorDestinationBuilder;

import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.empty.databinding.FragmentCompleteBinding;
import com.example.empty.databinding.FragmentCountDownBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CompleteFragment extends Fragment {

    private FragmentCompleteBinding binding;
    Context context;
    private MainActivity mainActivity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        binding = FragmentCompleteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        edit = sharedPreferences.edit();
        float featherCount = sharedPreferences.getFloat("featherCount", 0);


        float longitude = sharedPreferences.getFloat("longitude", 0);
        float latitude = sharedPreferences.getFloat("latitude", 0);
        String uid = sharedPreferences.getString("uid", "");
        String category = sharedPreferences.getString("category", "");
        category = category.trim().replaceAll("\\s+", "");
        DateStr dateStrObj = new DateStr();
        String datestr = dateStrObj.getDateStr();

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("users").child(uid);
        reference.push().setValue(new LocationStruct(latitude,longitude, true, category, new DateStr().getDateStr()));

        binding.rewardCount.setText("You have received " + featherCount);
        edit.putInt("complete_success", 1);
        edit.putFloat("featherCount", 0);
        edit.apply();

        binding.okButton.setOnClickListener(v -> {
            showBottomNavigationView();
            mainActivity.replaceFragment(R.id.stuff_on_map, new dwm_search_fab());
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