package com.example.empty;

import static android.content.ContentValues.TAG;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.empty.databinding.FragmentSimpleSettingBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
    String originalStartTime;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = requireActivity().getApplicationContext();
        binding = FragmentSimpleSettingBinding.inflate(inflater, container, false);
        main = (MainActivity) getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        dateStr = sharedPreferences.getString("currDateStr", new DateStr().getDateStr());
        editor = sharedPreferences.edit();
        originalStartTime = sharedPreferences.getString("startTime", "");
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
        binding.close.setOnClickListener(e->{ main.removeFragment(this);});
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
                    String originalStartTime = sharedPreferences.getString("startTime", "");
                    Log.d(TAG, "onDataChange: onDataChange1");
                    String startTime = binding.startTime.getText().toString();
                    boolean editRequest = sharedPreferences.getBoolean("editRequest", false);
                    sameTime = false;
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        try {
                            PlannerItemFirebase itemFirebase = childSnapshot.getValue(PlannerItemFirebase.class);
                            assert itemFirebase != null;
                            String itemStartTime = itemFirebase.getStartTime();
                            if (itemStartTime.equals(startTime)) {
                                sameTime = true;
                                break;
                            }
                        } catch (Exception e) {
                            Log.d("datacheck", e.getClass().getSimpleName());
                            Log.d("datacheck", e.getMessage());
                        }
                    }
                    if(editRequest && startTime.equals(originalStartTime)){
                        sameTime = false;
                    }
                    if (sameTime) {
                        Toast.makeText(context, "The start time conflicts with an existing task", Toast.LENGTH_LONG).show();
                        return;
                    }
                    SpinnerItem selected = (SpinnerItem) binding.workType.getSelectedItem();
                    String title = selected.getText();
                    int workType = selected.getImageResId();
                    String durationTxt = binding.duration.getText().toString();
                    Log.d("TAG", "onViewCreated: "+durationTxt);

                    if(checkEmpty(startTime, getString(R.string.select_start_time))
                            || checkEmpty(durationTxt, getString(R.string.select_duration))) {
                        return;
                    }
                    editor.putBoolean("newPlan", true);
                    editor.putString("title", title);
                    editor.putInt("workType", workType);
                    editor.putString("startTime", startTime);
                    editor.putString("durationTxt", durationTxt);
                    editor.putString("notification", getString(R.string.none));
                    editor.apply();
                    if(editRequest){
                        Log.d(TAG, "onDataChange: onDataChange2-----"+startTime);
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            try {
                                PlannerItemFirebase plannerItemFirebase = childSnapshot.getValue(PlannerItemFirebase.class);
                                assert plannerItemFirebase != null;
                                String start = plannerItemFirebase.getStartTime();
                                System.out.println(start);
                                if (start.equals(originalStartTime)) {
                                    DatabaseReference itemRef = childSnapshot.getRef();
                                    plannerItemFirebase.setTitle(title);
                                    plannerItemFirebase.setDuration(durationTxt);
                                    plannerItemFirebase.setStartTime(startTime);
                                    plannerItemFirebase.setWorkType(workType);
                                    itemRef.child("title").setValue(title);
                                    itemRef.child("workType").setValue(workType);
                                    itemRef.child("startTime").setValue(startTime);
                                    itemRef.child("duration").setValue(durationTxt);
                                    itemRef.child("notification").setValue(getString(R.string.none));
                                    int durationNum = Integer.parseInt(durationTxt.substring(0, durationTxt.indexOf(" ")));
                                    itemRef.child("endTime").setValue(plannerItemFirebase.getEndTime(startTime, durationNum));
                                    Log.d(TAG, "onDataChange: called -------- "+startTime);
                                }
                            } catch (Exception e) {
                                Log.d("datacheck", e.getClass().getSimpleName());
                                Log.d("datacheck", e.getMessage());
                            }
                        }
                    }else{
                        reference.push().setValue(new PlannerItemFirebase(title, startTime, durationTxt,
                                workType, getString(R.string.none), dateStr));
                    }
                    main.removeFragment(currFragment);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
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