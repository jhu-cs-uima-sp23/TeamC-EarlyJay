package com.example.empty;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.empty.databinding.FragmentAdvancedSettingBinding;
import com.example.empty.databinding.FragmentPopupStartBinding;
import com.example.empty.databinding.FragmentSimpleSettingBinding;

import java.util.ArrayList;
import java.util.List;

public class AdvancedSetting extends Fragment {
    private FragmentAdvancedSettingBinding binding;
    private Context context;
    private MainActivity main;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        binding = FragmentAdvancedSettingBinding.inflate(inflater, container, false);
        main = (MainActivity) getActivity();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        spinner
        {List<SpinnerItem> spinnerItems = new ArrayList<>();
            spinnerItems.add(new SpinnerItem(R.drawable.circle_dashed_6_xxl, "Work"));
            spinnerItems.add(new SpinnerItem(R.drawable.yellows, "Class"));
            spinnerItems.add(new SpinnerItem(R.drawable.triangle_48, "Team"));
            spinnerItems.add(new SpinnerItem(R.drawable.star_2_xxl, "Sports"));
            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), spinnerItems);
            binding.workType.setAdapter(adapter);}
        binding.advance.setOnClickListener(e->{
            main.replaceFragment(R.id.popUp, new AdvancedSetting());
        });
    }
}