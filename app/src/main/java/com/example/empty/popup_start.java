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
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.empty.databinding.FragmentDwmSearchFabBinding;
import com.example.empty.databinding.FragmentPopupStartBinding;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class popup_start extends Fragment {

    private FragmentPopupStartBinding binding;

    private CircularSeekBar progressCircular;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;

    private final int MAX = 120;

    private int factorProgress;
    private Context context;
    private MainActivity main;

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
        binding.textView4.setText(String.format(binding.textView4.getText().toString(), (int)(factorProgress/2)));

        binding.saveButton.setOnClickListener(v -> {
            edit.putInt("numSeconds", factorProgress * 5 * 60);
            edit.putInt("reward_amount", (int)(factorProgress/2));
            edit.apply();
            main.replaceFragment(R.id.stuff_on_map, new CountDownFragment());
        });

        binding.button.setOnClickListener(v -> {
            main.replaceFragment(R.id.stuff_on_map, new dwm_search_fab());
        });

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
                        binding.textView4.setText(R.string.reward_amount);
                        binding.textView4.setText(String.format(binding.textView4.getText().toString(), (int)(factorProgress/2)));
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