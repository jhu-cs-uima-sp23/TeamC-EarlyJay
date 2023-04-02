package com.example.empty;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

    Context cntx;

    public boolean settingOff = true;

    private MainActivity myact;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_map, container, false);

        // ADDED THIS LINE TO AVOID USING THE ChatViewModel class
        //  binding.textChat.setText("This is the map tab.");

        cntx = getActivity().getApplicationContext();
        myact = (MainActivity) getActivity();

        ImageButton setting = myView.findViewById(R.id.setting_button);
//        ImageButton search = myView.findViewById(R.id.search_button);

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(myact, view);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.daily_option:
                                menuItem.setChecked(true);
                                Toast toast01 = Toast.makeText(cntx, "daily", Toast.LENGTH_SHORT);
                                toast01.show();
                                // archive(item);
                                return true;
                            case R.id.weekly_option:
                                menuItem.setChecked(true);
                                Toast toast02 = Toast.makeText(cntx, "weekly", Toast.LENGTH_SHORT);
                                toast02.show();
                                // delete(item);
                                return true;
                            case R.id.monthly_option:
                                menuItem.setChecked(true);
                                Toast toast03 = Toast.makeText(cntx, "monthly", Toast.LENGTH_SHORT);
                                toast03.show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.setting_map_frag_menu, popup.getMenu());
                popup.show();
            }
        });


        return myView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}