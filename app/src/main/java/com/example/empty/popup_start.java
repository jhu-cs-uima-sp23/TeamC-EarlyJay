package com.example.empty;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.empty.databinding.FragmentPopupStartBinding;

import java.util.ArrayList;
import java.util.List;

import me.tankery.lib.circularseekbar.CircularSeekBar;


public class popup_start extends Fragment {

    private FragmentPopupStartBinding binding;

    private CircularSeekBar progressCircular;

    private final int MAX = 120;

    private int factorProgress;
    private Context context;
    private MainActivity main;
    private Spinner mSpinner;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;

    private int featherCount;

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

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        edit = sharedPreferences.edit();

        progressCircular = binding.circularSeekBar;


        progressCircular.setMax(MAX);
        factorProgress = 12;
        featherCount = 6;


        binding.saveButton.setOnClickListener(v -> {
            edit.putInt("numSeconds", factorProgress * 5 * 60);
            edit.putFloat("featherCount", featherCount);
            edit.apply();
            main.replaceFragment(R.id.stuff_on_map, new CountDownFragment());
        });

        binding.button.setOnClickListener(v -> {
            main.replaceFragment(R.id.stuff_on_map, new dwm_search_fab());
        });



        mSpinner = binding.spinner;
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
//                R.array.type_array, android.R.layout.simple_spinner_item);
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSpinner.setAdapter(adapter);

        List<SpinnerItem> spinnerItems = new ArrayList<>();
        spinnerItems.add(new SpinnerItem(R.drawable.circle_dashed_6_xxl, "         Work"));
        spinnerItems.add(new SpinnerItem(R.drawable.yellows, "         Class"));
        spinnerItems.add(new SpinnerItem(R.drawable.triangle_48, "         Team"));
        spinnerItems.add(new SpinnerItem(R.drawable.star_2_xxl, "         Sports"));

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), spinnerItems);
        mSpinner.setAdapter(adapter);








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
                        featherCount = factorProgress / 2;

                        binding.textView4.setText("Reward: " + featherCount);

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