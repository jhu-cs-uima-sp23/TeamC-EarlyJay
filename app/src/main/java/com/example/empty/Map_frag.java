package com.example.empty;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.empty.databinding.FragmentMapBinding;

public class Map_frag extends Fragment {

    private FragmentMapBinding binding;

    Context context;

    public boolean settingOff = true;

    private MainActivity mainActivity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        edit = sharedPreferences.edit();

        binding.settingButton.setOnClickListener(view1 -> {
            mainActivity = (MainActivity) getActivity();
            PopupMenu popup = new PopupMenu(mainActivity, view1);
            popup.setOnMenuItemClickListener(menuItem -> {
                switch (menuItem.getItemId()) {
                    case R.id.daily_option:
                        menuItem.setChecked(true);
                        Toast toast01 = Toast.makeText(context, "daily", Toast.LENGTH_SHORT);
                        toast01.show();
                        // archive(item);
                        return true;
                    case R.id.weekly_option:
                        menuItem.setChecked(true);
                        Toast toast02 = Toast.makeText(context, "weekly", Toast.LENGTH_SHORT);
                        toast02.show();
                        // delete(item);
                        return true;
                    case R.id.monthly_option:
                        menuItem.setChecked(true);
                        Toast toast03 = Toast.makeText(context, "monthly", Toast.LENGTH_SHORT);
                        toast03.show();
                        return true;
                    default:
                        return false;
                }
            });

            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.setting_map_frag_menu, popup.getMenu());
            popup.show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}