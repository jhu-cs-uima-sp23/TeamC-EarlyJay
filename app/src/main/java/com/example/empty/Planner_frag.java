package com.example.empty;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.empty.databinding.FragmentPlannerBinding;

import java.util.ArrayList;

public class Planner_frag extends Fragment {
    ArrayList<PlannerItemModel> plannerItemModels = new ArrayList<>();
    private Context context;
    private FragmentPlannerBinding binding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPlannerBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();
        binding.newPlan.setOnClickListener(e->{
            mainActivity.replaceFragment(R.id.popUp, new SimpleSetting());
        });
        return binding.getRoot();
    }
}