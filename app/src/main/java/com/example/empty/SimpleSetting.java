package com.example.empty;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.empty.databinding.FragmentPopupStartBinding;
import com.example.empty.databinding.FragmentSimpleSettingBinding;

import java.util.ArrayList;
import java.util.List;

public class SimpleSetting extends Fragment {
    private FragmentSimpleSettingBinding binding;
    private Context context;
    private MainActivity main;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        binding = FragmentSimpleSettingBinding.inflate(inflater, container, false);
        main = (MainActivity) getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        spinner
        List<SpinnerItem> spinnerItems = new ArrayList<>();
        spinnerItems.add(new SpinnerItem(R.drawable.circle_dashed_6_xxl, "Work"));
        spinnerItems.add(new SpinnerItem(R.drawable.yellows, "Class"));
        spinnerItems.add(new SpinnerItem(R.drawable.triangle_48, "Team"));
        spinnerItems.add(new SpinnerItem(R.drawable.star_2_xxl, "Sports"));
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), spinnerItems);
        binding.workType.setAdapter(adapter);
        binding.advance.setOnClickListener(e->{
            main.replaceFragment(R.id.popUp, new AdvancedSetting());
        });
        binding.close.setOnClickListener(e->{
            main.removeFragment(R.id.popUp, this);
        });
        binding.done.setOnClickListener(e->{
            String title = binding.workType.getSelectedItem().toString();
            String startTime = binding.startTime.getText().toString();
            editor.putBoolean("newPlan", true);
//            editor.putString();
            editor.apply();
            main.removeFragment(R.id.popUp, this);
        });
    }
}