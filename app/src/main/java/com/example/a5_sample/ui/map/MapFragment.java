package com.example.a5_sample.ui.map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.a5_sample.MainActivity;
import com.example.a5_sample.NewPark;
import com.example.a5_sample.R;
import com.example.a5_sample.databinding.FragmentChatBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MapFragment extends Fragment {

    private FragmentChatBinding binding;

    Context cntx;

    public boolean settingOff = true;

    private MainActivity myact;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View myview = inflater.inflate(R.layout.fragment_map, container, false);

        // ADDED THIS LINE TO AVOID USING THE ChatViewModel class
           //  binding.textChat.setText("This is the map tab.");

        cntx = getActivity().getApplicationContext();
        myact = (MainActivity) getActivity();

        ImageButton setting = myview.findViewById(R.id.setting_button);
        ImageButton search = myview.findViewById(R.id.search_button);

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


        return myview;


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}