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

import com.example.empty.databinding.FragmentOnQuitWarningBinding;
import com.example.empty.databinding.FragmentPopupStartBinding;

public class OnQuitWarning extends Fragment {
    private FragmentOnQuitWarningBinding binding;
    private MainActivity main;
    private Context context;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        binding = FragmentOnQuitWarningBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        main = (MainActivity) getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        float reward_amount = sharedPreferences.getFloat("featherCount", 0);
        String formatted = getString(R.string.warning_txt, reward_amount);
        binding.warningTxt.setText(formatted);
        binding.yes.setOnClickListener(v -> {
            main.replaceFragment(R.id.stuff_on_map, new CountDownFragment());
        });

        binding.no.setOnClickListener(v -> {
            editor.putInt("complete_success", 2);
            main.replaceFragment(R.id.stuff_on_map, new dwm_search_fab());
        });

    }
}