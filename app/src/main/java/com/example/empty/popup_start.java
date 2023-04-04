package com.example.empty;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.empty.databinding.FragmentDwmSearchFabBinding;
import com.example.empty.databinding.FragmentPopupStartBinding;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class popup_start extends Fragment {

    private FragmentPopupStartBinding binding;

    private CircularSeekBar progressCircular;

    private final int MAX = 120;

    private int factorProgress;


    private Context context;
    private MainActivity main;
    private Spinner mSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        binding = FragmentPopupStartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        main = (MainActivity) getActivity();
        binding.saveButton.setOnClickListener(v -> {
            main.replaceFragment(R.id.stuff_on_map, new dwm_search_fab());
        });


        mSpinner = binding.spinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.type_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);


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

    }
}