package com.example.empty;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.empty.databinding.FragmentPlannerBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class Planner_frag extends Fragment implements PlannerItemAdapter.OnDeleteButtonClickListener, PlannerItemAdapter.OnEditClickListener, PlannerItemAdapter.OnPinListener{
    ArrayList<PlannerItemModel> plannerItemModels = new ArrayList<>();
    RecyclerView recyclerView;
    private Context context;
    private FragmentPlannerBinding binding;
    private MainActivity mainActivity;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private PlannerItemAdapter adapter;

    private ArrayList<PlannerItemFirebase> locStructListByDay;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    private String currDateStr;
    private String currDatePage;

    private String uid;
    private int editPosition = -1;
    public Comparator<PlannerItemModel> comparator = (item1, item2) -> {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date time1;
        try {
            time1 = format.parse(item1.getStartTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Date time2;
        try {
            time2 = format.parse(item2.getStartTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        int result;
        if(item1.pinned && item2.pinned){
            if(time1.equals(time2)){
                result = item1.duration - item2.duration;
            }else{
                result = time1.compareTo(time2);
            }
        }else if(item1.pinned){
            result = -1;
        }else if(item2.pinned){
            result = 1;
        }else{
            if(time1.equals(time2)){
                result = item1.duration - item2.duration;
            }else{
                result = time1.compareTo(time2);
            }
        }

        return result;
    };
    private PlannerItemModel editedItem;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPlannerBinding.inflate(inflater, container, false);
        mainActivity = (MainActivity) getActivity();
        context = getContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.putBoolean("newPlan", false);
        editor.apply();

        currDateStr = sharedPreferences.getString("currDateStr", new DateStr().getDateStr());
        DateStr now = new DateStr(currDateStr);
        currDatePage = sharedPreferences.getString("currDatePage", "Daily");

        switch(currDatePage) {
            case "Weekly":
                mainActivity.replaceFragment(R.id.stuff_on_date, new WeeklyStatsFragment());
                mainActivity.replaceFragment(R.id.weekly_view, new PlannerWeeklyFragment());
                binding.newPlan.setVisibility(View.INVISIBLE);
                break;
            default:
                mainActivity.replaceFragment(R.id.stuff_on_date, new DailyStatsFragment());
                binding.newPlan.setVisibility(View.VISIBLE);
                break;
        }
        binding.newPlan.setOnClickListener(e-> mainActivity.replaceFragment(R.id.popUp, new SimpleSetting()));
        binding.popUp.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if(binding.popUp.getChildCount() == 0){
                binding.popupBackground.setVisibility(View.INVISIBLE);
                if(sharedPreferences.getBoolean("newPlan", false)){
                    String title = sharedPreferences.getString("title", "");
                    String startTime = sharedPreferences.getString("startTime", "0");
                    String durationTxt = sharedPreferences.getString("durationTxt", "0");
                    String notificationTxt = sharedPreferences.getString("notification", "");
                    int workType = sharedPreferences.getInt("workType", -1);
                    if(sharedPreferences.getBoolean("editRequest", false)){
                        editedItem.setTitle(sharedPreferences.getString("title", editedItem.title));
                        editedItem.setWorkType(sharedPreferences.getInt("workType", editedItem.workType));
                        editedItem.setDurationTxt(sharedPreferences.getString("durationTxt", editedItem.durationTxt));
                        editedItem.setNotification(sharedPreferences.getString("notification", editedItem.notification));
                        editedItem.setStartTime(sharedPreferences.getString("startTime", editedItem.startTime));
                        refreshList();
                    }else{
                        addPlan(title,startTime,durationTxt,workType, notificationTxt, false);
                    }
                }
                reset();
            }else{
                binding.popupBackground.setVisibility(View.VISIBLE);
            }
        });
        binding.leftRollButton2.setOnClickListener(view -> {
            String dateStr;
            if ("Weekly".equals(currDatePage)) {
                dateStr = now.getPastDay(7);
            } else {
                dateStr = now.getPastDay(1);
            }
            editor.putString("currDateStr", dateStr);
            editor.apply();
            mainActivity.replaceFragment(R.id.frame_layout, new Planner_frag());
        });
        binding.rightRollButton2.setOnClickListener(view -> {
            String dateStr;
            if ("Weekly".equals(currDatePage)) {
                dateStr = now.getFutureDay(7);
            } else {
                dateStr = now.getFutureDay(1);
            }
            editor.putString("currDateStr", dateStr);
            editor.apply();
            mainActivity.replaceFragment(R.id.frame_layout, new Planner_frag());
        });
        binding.spinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                if (selected.equals(currDatePage)) {
                    return;
                }
                editor.putString("currDatePage", selected);
                editor.putString("currDateStr", new DateStr().getDateStr());
                editor.apply();
                mainActivity.replaceFragment(R.id.frame_layout, new Planner_frag());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        ArrayAdapter myAdapter = (ArrayAdapter) binding.spinner3.getAdapter();
        binding.spinner3.setSelection(myAdapter.getPosition(currDatePage));

        uid = sharedPreferences.getString("uid", uid);
        rootNode = FirebaseDatabase.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().
                child("planner").child(uid).child(currDateStr);
//        grab things from data base to populate recycler view
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                locStructListByDay = new ArrayList<>();

                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    try {
                    PlannerItemFirebase plannerItemFirebase = childSnapshot.getValue(PlannerItemFirebase.class);
                    int workType = plannerItemFirebase.getWorkType();
                    addPlan(plannerItemFirebase.getTitle(), plannerItemFirebase.getStartTime(),
                            plannerItemFirebase.getDuration(), plannerItemFirebase.getWorkType(),
                            plannerItemFirebase.getNotification(), plannerItemFirebase.getPinned());

                    } catch (Exception e) {
                        System.out.println(e.getClass().getSimpleName());
                        System.out.println(e.getMessage());
                    }
                }
                reset();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = binding.plannerRecyclerView;
        adapter = new PlannerItemAdapter(this, mainActivity, plannerItemModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

    }

    public void addPlan(String title, String startTime, String duration, int workType, String notification, boolean pin){
        plannerItemModels.add(new PlannerItemModel(title, startTime, duration, workType, notification, pin));
        adapter.notifyItemInserted(plannerItemModels.size()-1);
        refreshList();
    }

    public void reset(){
        editor.putBoolean("newPlan", false);
        editor.putBoolean("editRequest", false);
        editor.putInt("lastSelected", 0);
        editor.putInt("workType", -1);
        editor.putString("title", "");
        editor.putString("startTime", getString(R.string.select_start_time));
        editor.putString("durationTxt", getResources().getString(R.string.select_duration));
        editor.putString("notification", getString(R.string.select_alert));
        editor.apply();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        reset();
    }

    @Override
    public void onDeleteButtonClicked(int position) {
        String startTime = plannerItemModels.get(position).getStartTime();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    try {
                        PlannerItemFirebase plannerItemFirebase = childSnapshot.getValue(PlannerItemFirebase.class);
                        String start = plannerItemFirebase.getStartTime();
                        if (start.equals(startTime)) {
                            DatabaseReference itemRef = childSnapshot.getRef();
                            itemRef.removeValue();
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
            }
        });
        plannerItemModels.remove(position);
        adapter.notifyItemRemoved(position);
        refreshList();
    }

    @Override
    public void onEditClick(int position) {
        editPosition = position;
        editedItem = plannerItemModels.get(position);
        editor.putBoolean("editRequest", true);
        int lastSelected = 0;
        if(editedItem.category.equals(getString(R.string.work))){lastSelected = 0;}
        else if(editedItem.category.equals(getString(R.string.class_))){lastSelected = 1;}
        else if(editedItem.category.equals(getString(R.string.team))){lastSelected = 2;}
        else if(editedItem.category.equals(getString(R.string.sport))){lastSelected = 3;}
        editor.putInt("lastSelected", lastSelected);
        editor.putInt("workType", editedItem.workType);
        editor.putString("title", editedItem.title);
        editor.putString("startTime", editedItem.startTime);
        editor.putString("durationTxt", ""+editedItem.durationTxt);
        editor.putString("notification", ""+editedItem.notification);
        editor.apply();
        
        mainActivity.replaceFragment(R.id.popUp, new SimpleSetting());
    }

    @Override
    public void onPinClick(int position) {
        String start_time = plannerItemModels.get(position).getStartTime();
        System.out.println(start_time);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    try {
                        PlannerItemFirebase plannerItemFirebase = childSnapshot.getValue(PlannerItemFirebase.class);
                        String start = plannerItemFirebase.getStartTime();
                        System.out.println(start);
                        if (start.equals(start_time)) {
                            System.out.println("yeah");
                            DatabaseReference itemRef = childSnapshot.getRef();
                            itemRef.child("pinned").setValue(!plannerItemFirebase.getPinned());
                            plannerItemFirebase.togglePin();
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });


        plannerItemModels.get(position).togglePin();
        refreshList();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void refreshList(){
        plannerItemModels.sort(comparator);
        adapter.notifyDataSetChanged();
    }
}