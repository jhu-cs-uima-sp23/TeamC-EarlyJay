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

    private CircularSeekBar progressCircular;

    private final int MAX = 120;

    private int factorProgress;

    private boolean mIsDrag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentStatBinding.inflate(inflater, container, false);

        progressCircular = binding.circularSeekBar;


        progressCircular.setMax(MAX);
        factorProgress = 12;

        progressCircular.setOnSeekBarChangeListener(
                new CircularSeekBar.OnCircularSeekBarChangeListener() {

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
                    }

                    @Override
                    public void onStopTrackingTouch(CircularSeekBar seekBar) {


                    }

                    @Override
                    public void onStartTrackingTouch(CircularSeekBar seekBar) {

                    }
                });

        return binding.getRoot();
    }


}