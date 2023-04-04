package com.example.empty;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.empty.databinding.FragmentStatBinding;

import me.tankery.lib.circularseekbar.CircularSeekBar;


public class Stat_frag extends Fragment {

    private FragmentStatBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentStatBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


}