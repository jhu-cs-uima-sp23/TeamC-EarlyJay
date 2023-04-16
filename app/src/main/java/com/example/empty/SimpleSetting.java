package com.example.empty;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.empty.databinding.FragmentPopupStartBinding;
import com.example.empty.databinding.FragmentSimpleSettingBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SimpleSetting extends Fragment implements NumberPicker.OnDialogDismissedListener{
    private FragmentSimpleSettingBinding binding;
    private Context context;
    private MainActivity main;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String startTime;
    private Resources res;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        binding = FragmentSimpleSettingBinding.inflate(inflater, container, false);
        main = (MainActivity) getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        res = getResources();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        spinner
        List<SpinnerItem> spinnerItems = new ArrayList<>();
        spinnerItems.add(new SpinnerItem(R.drawable.circle_dashed_6_xxl, "Work"));
        spinnerItems.add(new SpinnerItem(R.drawable.yellows, "Class"));
        spinnerItems.add(new SpinnerItem(R.drawable.triangle_48, "Team"));
        spinnerItems.add(new SpinnerItem(R.drawable.star_2_xxl, "Sports"));
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), spinnerItems);
        TimePickerDialog.OnTimeSetListener onTimeSetListener= new TimePickerDialog.OnTimeSetListener(){

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
                String hour = ""+hourOfDay, minute = ""+minuteOfDay;
                if(hourOfDay<10){
                    hour = "0"+hour;
                }
                if(minuteOfDay<10){
                    minute = "0"+minute;
                }
                startTime = hour+":"+minute;
                binding.startTime.setText(startTime);
            }
        };
        binding.workType.setAdapter(adapter);
        binding.advance.setOnClickListener(e->{
            main.replaceFragment(R.id.popUp, new AdvancedSetting());
        });
        binding.close.setOnClickListener(e->{
            editor.putString("durationTxt", res.getString(R.string.select_duration));
            editor.commit();
            main.removeFragment(R.id.popUp, this);
        });
        binding.startTime.setOnClickListener(e->{
            TimePickerDialog timePickerDialog = new TimePickerDialog(main, onTimeSetListener, 0, 0, true);
            timePickerDialog.show();

        });
        Button durationButton = binding.duration;
        durationButton.setOnClickListener(e->{
            NumberPicker numberPicker = new NumberPicker();
            numberPicker.setOnDialogDismissedListener(this);
            numberPicker.show(getChildFragmentManager(), "");
        });
        binding.done.setOnClickListener(e->{
            SpinnerItem selected = (SpinnerItem) binding.workType.getSelectedItem();
            int workType = selected.getImageResId();
            Log.d("TAG", "onViewCreated: "+workType);
            String startTime = binding.startTime.getText().toString();
            String durationTxt = binding.duration.getText().toString();
            if(checkEmpty(startTime, res.getString(R.string.select_start_time)) ||
                    checkEmpty(durationTxt, res.getString(R.string.select_duration))) {
                return;
            }
            editor.putBoolean("newPlan", true);
            editor.putInt("workType",workType);
            editor.putString("startTime", startTime);
            editor.putString("durationTxt", durationTxt);
            editor.apply();
            main.removeFragment(R.id.popUp, this);
        });
    }
    public boolean checkEmpty(String source, String target){
        if(source.equals(target)){
            Toast.makeText(context, "Please fill out "+target, Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
    @Override
    public void onDismissed() {
        String duration = sharedPreferences.getString("durationTxt", res.getString(R.string.select_duration));
        binding.duration.setText(duration);
    }
}