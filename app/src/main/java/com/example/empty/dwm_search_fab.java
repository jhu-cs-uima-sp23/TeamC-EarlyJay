package com.example.empty;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.empty.databinding.FragmentDwmSearchFabBinding;
import com.example.empty.databinding.FragmentMapBinding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;

public class dwm_search_fab extends Fragment {
    private FragmentDwmSearchFabBinding binding;

    Context context;
    private MainActivity mainActivity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;

    private EditText searchEditText;

    private ImageButton searchButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        binding = FragmentDwmSearchFabBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        edit = sharedPreferences.edit();

        // Setting Button
        binding.settingButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(mainActivity, binding.settingButton);
            popup.getMenuInflater().inflate(R.menu.setting_map_frag_menu, popup.getMenu());
            int last_selected = sharedPreferences.getInt("last_selected", -1);
            if(last_selected != -1){
                popup.getMenu().getItem(last_selected).setChecked(true);
            } else {
                last_selected = 0;
                popup.getMenu().getItem(last_selected).setChecked(true);
            }
            popup.setOnMenuItemClickListener(menuItem -> {
                int selected = -1;
                switch (menuItem.getItemId()) {
                    case R.id.daily_option:
                        menuItem.setChecked(true);
                        selected = 0;
                        Toast toast01 = Toast.makeText(context, "daily", Toast.LENGTH_SHORT);
                        toast01.show();
                        // archive(item);
                        break;
                    case R.id.weekly_option:
                        menuItem.setChecked(true);
                        selected = 1;
                        Toast toast02 = Toast.makeText(context, "weekly", Toast.LENGTH_SHORT);
                        toast02.show();
                        // delete(item);
                        break;
                    case R.id.monthly_option:
                        menuItem.setChecked(true);
                        selected = 2;
                        Toast toast03 = Toast.makeText(context, "monthly", Toast.LENGTH_SHORT);
                        toast03.show();
                        break;
                    default:
                        break;
                }
                edit.putInt("last_selected", selected);
                edit.commit();
                mainActivity.replaceFragment(R.id.frame_layout, new Map_frag());
                return true;
            });

            popup.show();
        });

        binding.floatingActionButton.setOnClickListener(view1 -> {
            mainActivity.replaceFragment(R.id.stuff_on_map, new popup_start());
        });

        // Search Bar
        searchEditText = view.findViewById(R.id.loc_input);
        searchButton = binding.searchButton;
        searchButton.setOnClickListener(v -> {
            String searchString = searchEditText.getText().toString().trim();
           // searchString += "Johns Hopkins";
            double minLat = 39.327128;
            double maxLat = 39.332359;
            double minLng = -76.624858;
            double maxLng = -76.614914;
            if (!searchString.isEmpty()) {
                Geocoder geocoder = new Geocoder(context);
                try {
                    List<Address> addresses = geocoder.getFromLocationName(searchString, 1);
                    if (addresses.size() > 0) {
                        int test = 0;
                        for (Address address : addresses) {
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                            if (latLng.latitude >= minLat && latLng.latitude <= maxLat && latLng.longitude >= minLng && latLng.longitude <= maxLng) {
                                test = 1;
                                // send the LatLng to the Map fragment
                                Bundle args = new Bundle();
                                args.putParcelable("location", latLng);
                                args.putString("input_name", searchString);
                                Map_frag mapFrag = new Map_frag();
                                mapFrag.setArguments(args);
                                mainActivity.replaceFragment(R.id.frame_layout, mapFrag);
                                break;
                            }
                        }
                        if (test == 0) {
                            Toast.makeText(context, "Location is not within the Johns Hopkins Homewood campus", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "No results found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error searching for location", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Please enter a search query", Toast.LENGTH_SHORT).show();
            }
        });

        // Clear search mark
        ImageButton clearButton = binding.clearButton;
        clearButton.setOnClickListener(v -> {
            mainActivity.replaceFragment(R.id.frame_layout,  new Map_frag());
        });

    }
}