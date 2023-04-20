package com.example.empty;

import static android.content.ContentValues.TAG;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.empty.databinding.FragmentAdvancedSettingBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdvancedSetting extends Fragment implements NumberPicker.OnDialogDismissedListener{
    private FragmentAdvancedSettingBinding binding;
    private Context context;
    private MainActivity main;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String startTime;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

    private String dateStr;

    private boolean sameTime;

    private Fragment currFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        binding = FragmentAdvancedSettingBinding.inflate(inflater, container, false);
        main = (MainActivity) getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        dateStr = sharedPreferences.getString("currDateStr","");
        currFragment = this;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String uid = sharedPreferences.getString("uid", "");
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference().child("planner").child(uid).child(dateStr);

//        spinner
        List<SpinnerItem> spinnerItems = new ArrayList<>();
        spinnerItems.add(new SpinnerItem(R.drawable.circle_dashed_6_xxl, "Work"));
        spinnerItems.add(new SpinnerItem(R.drawable.yellows, "Class"));
        spinnerItems.add(new SpinnerItem(R.drawable.triangle_48, "Team"));
        spinnerItems.add(new SpinnerItem(R.drawable.star_2_xxl, "Sports"));
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), spinnerItems);
        binding.workType.setAdapter(adapter);
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
//        for switching from simple view
        binding.workType.setSelection(sharedPreferences.getInt("lastSelected", 0));
        binding.startTime.setText(sharedPreferences.getString("startTime", getString(R.string.select_start_time)));
        binding.duration.setText(sharedPreferences.getString("durationTxt", getString(R.string.select_duration)));
        binding.cusomeTitle.setText(sharedPreferences.getString("title", ""));
        binding.notification.setText(sharedPreferences.getString("notification", getString(R.string.select_alert)));

//        click handlers
        binding.simple.setOnClickListener(e->{
            editor.putInt("lastSelected", binding.workType.getSelectedItemPosition());
            editor.putString("startTime", binding.startTime.getText().toString());
            editor.putString("durationTxt", binding.duration.getText().toString());
            editor.putString("title", binding.cusomeTitle.getEditableText().toString());
            editor.putString("notification", binding.notification.getText().toString());
            editor.apply();
            main.replaceFragment(R.id.popUp, new SimpleSetting());
        });
        binding.close.setOnClickListener(e->{
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
                    String originalStartTime = sharedPreferences.getString("startTime", "");
                    String startTime = binding.startTime.getText().toString();
                    boolean editRequest = sharedPreferences.getBoolean("editRequest", false);
                    sameTime = false;
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        try {
                            PlannerItemFirebase itemFirebase = childSnapshot.getValue(PlannerItemFirebase.class);
                            String itemStartTime = itemFirebase.getStartTime();
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
                    if(editRequest && startTime.equals(originalStartTime)){
                        sameTime = false;
                    }
                    if (sameTime) {
                        Toast.makeText(context, "The start time conflicts with an existing task", Toast.LENGTH_LONG).show();
                        return;
                    }
                    SpinnerItem selected = (SpinnerItem) binding.workType.getSelectedItem();
                    int workType = selected.getImageResId();
                    String title = binding.cusomeTitle.getEditableText().toString();
                    String durationTxt = binding.duration.getText().toString();
                    String notification = binding.notification.getText().toString();
                    if(checkEmpty(title, "")
                            || checkEmpty(startTime, getString(R.string.select_start_time))
                            || checkEmpty(durationTxt, getString(R.string.select_duration))
                            || checkEmpty(notification, getString(R.string.select_alert)) ) {
                        return;
                    }
                    editor.putBoolean("newPlan", true);
                    editor.putString("title", title);
                    editor.putInt("workType",workType);
                    editor.putString("startTime", startTime);
                    editor.putString("durationTxt", durationTxt);
                    editor.putString("notification", notification);
                    editor.apply();
                    if(editRequest){
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            try {
                                PlannerItemFirebase plannerItemFirebase = childSnapshot.getValue(PlannerItemFirebase.class);
                                String start = plannerItemFirebase.getStartTime();
                                System.out.println(start);
                                if (start.equals(originalStartTime)) {
                                    DatabaseReference itemRef = childSnapshot.getRef();
                                    plannerItemFirebase.setTitle(title);
                                    plannerItemFirebase.setDuration(durationTxt);
                                    plannerItemFirebase.setStartTime(startTime);
                                    plannerItemFirebase.setNotification(notification);
                                    plannerItemFirebase.setWorkType(workType);
                                    itemRef.child("title").setValue(title);
                                    itemRef.child("workType").setValue(workType);
                                    itemRef.child("startTime").setValue(startTime);
                                    itemRef.child("duration").setValue(durationTxt);
                                    int durationNum = Integer.parseInt(durationTxt.substring(0, durationTxt.indexOf(" ")));
                                    itemRef.child("endTime").setValue(plannerItemFirebase.getEndTime(startTime, durationNum));
                                }
                            } catch (Exception e) {
                                continue;
                            }
                        }
                    }else{
                        reference.push().setValue(new PlannerItemFirebase(title, startTime, durationTxt,
                            workType, notification, dateStr));
                    }
                    main.removeFragment(R.id.popUp, currFragment);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });

        });

        binding.notification.setOnClickListener(e->{
            PopupMenu popup = new PopupMenu(context, binding.notification);
            popup.getMenuInflater().inflate(R.menu.notification_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(menuItem -> {
                binding.notification.setText(menuItem.getTitle().toString());
                return true;
            });
            popup.show();
        });



    }
    public boolean checkEmpty(String source, String target){
        if(source.equals(target)){
            if(target.equals("")){
                target = "title";
            }
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