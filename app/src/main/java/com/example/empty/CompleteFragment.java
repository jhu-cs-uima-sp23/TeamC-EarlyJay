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

import com.example.empty.databinding.FragmentCompleteBinding;
import com.example.empty.databinding.FragmentCountDownBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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


        float longitude = sharedPreferences.getFloat("longitude", 0);
        float latitude = sharedPreferences.getFloat("latitude", 0);
        String uid = sharedPreferences.getString("uid", "");
        String category = sharedPreferences.getString("category", "");
        DateStr dateStrObj = new DateStr();
        String datestr = dateStrObj.getDateStr();

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("users").child(uid).child(datestr);
        reference.push().setValue(new LocationStruct(longitude, latitude, true, category));


        Float featherCount = sharedPreferences.getFloat("featherCount", 0);
        binding.rewardCount.setText("You have received " + featherCount);

        edit.putFloat("featherCount", 0);
        edit.apply();

        binding.okButton.setOnClickListener(v -> {
            mainActivity.replaceFragment(R.id.stuff_on_map, new dwm_search_fab());
        });


    }


}