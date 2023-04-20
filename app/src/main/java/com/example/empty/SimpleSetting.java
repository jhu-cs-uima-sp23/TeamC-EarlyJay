package com.example.empty;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    private String uid;
    private boolean sameTime;

    private String dateStr;

    private Fragment currFragment;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        binding = FragmentSimpleSettingBinding.inflate(inflater, container, false);
        main = (MainActivity) getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        dateStr = sharedPreferences.getString("currDateStr", new DateStr().getDateStr());
        editor = sharedPreferences.edit();
        sameTime = false;
        currFragment = this;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        spinner


        uid = sharedPreferences.getString("uid", uid);
        rootNode = FirebaseDatabase.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().
                child("planner").child(uid).child(dateStr);

        List<SpinnerItem> spinnerItems = new ArrayList<>();
        spinnerItems.add(new SpinnerItem(R.drawable.circle_dashed_6_xxl, getString(R.string.work)));
        spinnerItems.add(new SpinnerItem(R.drawable.yellows, getString(R.string.class_)));
        spinnerItems.add(new SpinnerItem(R.drawable.triangle_48, getString(R.string.team)));
        spinnerItems.add(new SpinnerItem(R.drawable.star_2_xxl, getString(R.string.sport)));
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), spinnerItems);
        TimePickerDialog.OnTimeSetListener onTimeSetListener= (view1, hourOfDay, minuteOfDay) -> {
            String hour = ""+hourOfDay, minute = ""+minuteOfDay;
            if(hourOfDay<10){
                hour = "0"+hour;
            }
            if(minuteOfDay<10){
                minute = "0"+minute;
            }
            startTime = hour+":"+minute;
            if(startTime.charAt(0)=='0'){
                startTime = startTime.substring(1);
            }
            binding.startTime.setText(startTime);
        };

//        for switching from advanced view
        binding.workType.setAdapter(adapter);
        binding.workType.setSelection(sharedPreferences.getInt("lastSelected", 0));
        binding.startTime.setText(sharedPreferences.getString("startTime", getString(R.string.select_start_time)));
        binding.duration.setText(sharedPreferences.getString("durationTxt", getString(R.string.select_duration)));

//        click handlers
        binding.advance.setOnClickListener(e->{
            editor.putInt("lastSelected", binding.workType.getSelectedItemPosition());
            editor.putString("startTime", binding.startTime.getText().toString());
            editor.putString("durationTxt", binding.duration.getText().toString());
            editor.apply();
            main.replaceFragment(R.id.popUp, new AdvancedSetting());
        });
        binding.close.setOnClickListener(e->{
            editor.putBoolean("newPlan", true);
            editor.putInt("lastSelected", 0);
            editor.putInt("workType", -1);
            editor.putString("title", "");
            editor.putString("startTime", getString(R.string.select_start_time));
            editor.putString("durationTxt", getResources().getString(R.string.select_duration));
            editor.putString("notification", getString(R.string.select_alert));
            editor.apply();
            main.removeFragment(R.id.popUp, this);
        });
        binding.startTime.setOnClickListener(e->{
            TimePickerDialog timePickerDialog = new TimePickerDialog(main, onTimeSetListener, 0, 0, true);
            timePickerDialog.show();

        });
        binding.duration.setOnClickListener(e->{
            NumberPicker numberPicker = new NumberPicker();
            numberPicker.setOnDialogDismissedListener(this);
            numberPicker.show(getChildFragmentManager(), "");
        });
        binding.done.setOnClickListener(e->{

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String startTime = binding.startTime.getText().toString();
                    Log.d("datacheck", "start time: " + startTime);

                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        try {
                            PlannerItemFirebase itemFirebase =
                                    childSnapshot.getValue(PlannerItemFirebase.class);
                            String itemStartTime = itemFirebase.getStartTime();
                            Log.d("datacheck", "check time: " + itemStartTime);
                            if (itemStartTime.equals(startTime)) {
                                sameTime = true;
                                break;
                            }
                        } catch (Exception e) {
                            System.out.println(e.getClass().getSimpleName());
                            System.out.println(e.getMessage());
                            continue;
                        }
                    }

                    if (sameTime) {
                        Toast.makeText(context, "The start time conflicts with an existing task",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    SpinnerItem selected = (SpinnerItem) binding.workType.getSelectedItem();
                    int workType = selected.getImageResId();
                    Log.d("TAG", "onViewCreated: "+workType);
                    String durationTxt = binding.duration.getText().toString();
                    if(checkEmpty(startTime, getString(R.string.select_start_time)) ||
                            checkEmpty(durationTxt, getString(R.string.select_duration))) {
                        return;
                    }
                    editor.putBoolean("newPlan", true);
                    editor.putInt("workType",workType);
                    editor.putString("startTime", startTime);
                    editor.putString("durationTxt", durationTxt);
                    editor.apply();
                    String cardBackgroundColor = "#D04C25";
                    switch (workType){
                        case R.drawable.yellows:
                            cardBackgroundColor = "#F3A83B";
                            break;
                        case R.drawable.triangle_48:
                            cardBackgroundColor = "#ACCC8C";
                            break;
                        case R.drawable.star_2_xxl:
                            cardBackgroundColor = "#65BFF5";
                            break;
                        default:
                            break;
                    }
                    reference.push().setValue(new PlannerItemFirebase("", startTime, durationTxt,
                            workType, "", dateStr));
                    main.removeFragment(R.id.popUp, currFragment);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
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
        String duration = sharedPreferences.getString("durationTxt", getString(R.string.select_duration));
        binding.duration.setText(duration);
    }
}