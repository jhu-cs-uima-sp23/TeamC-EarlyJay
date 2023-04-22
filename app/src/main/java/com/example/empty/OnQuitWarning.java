package com.example.empty;

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

import com.example.empty.databinding.FragmentOnQuitWarningBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OnQuitWarning extends Fragment {
    private FragmentOnQuitWarningBinding binding;
    private MainActivity main;
    private Context context;

    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

    private DatabaseReference reference_planner;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = requireActivity().getApplicationContext();
        binding = FragmentOnQuitWarningBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        main = (MainActivity) getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        int reward_amount = sharedPreferences.getInt("featherCount", 0);
        String formatted = getString(R.string.warning_txt, reward_amount);
        binding.warningTxt.setText(formatted);
        binding.yes.setOnClickListener(v -> main.replaceFragment(R.id.stuff_on_map, new CountDownFragment()));

        binding.no.setOnClickListener(v -> {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            float longitude = sharedPreferences.getFloat("longitude", 0);
            float latitude = sharedPreferences.getFloat("latitude", 0);
            String uid = sharedPreferences.getString("uid", "");
            String category = sharedPreferences.getString("category", "");
            String plannerDateStr = sharedPreferences.getString("currDateStr", new DateStr().getDateStr());
            boolean planner = sharedPreferences.getBoolean("PlannerTask", false);
            String startTime = sharedPreferences.getString("PlanTaskStartTime", "0:00");
            int numSeconds = sharedPreferences.getInt("numSeconds", 0);
            int timeInterval = sharedPreferences.getInt("totalTimeInterval", 0);
            int featherCount = -1 * numSeconds / 300;
            rootNode = FirebaseDatabase.getInstance();
            reference = rootNode.getReference().child("users").child(uid).child(new DateStr().getDateStr());
            System.out.println("DateStr: " + plannerDateStr);
            System.out.println("planner: " + planner);
            System.out.println("startTime: " + startTime);
            Log.d("datacheck", "DateStr: " + plannerDateStr);
            Log.d("datacheck", "planner: " + planner);
            Log.d("datacheck", "startTime: " + startTime);
            if (planner) {
                reference_planner = rootNode.getReference().child("planner").child(uid).child(plannerDateStr);
                reference_planner.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            try {
                                PlannerItemFirebase plannerItemFirebase = childSnapshot.getValue(PlannerItemFirebase.class);
                                assert plannerItemFirebase != null;
                                String start = plannerItemFirebase.getStartTime();
                                if (start.equals(startTime)) {
                                    DatabaseReference itemRef = childSnapshot.getRef();
                                    itemRef.child("status").setValue(2);
                                }
                            } catch (Exception e) {
                                Log.d("datacheck", e.getClass().getSimpleName());
                                Log.d("datacheck", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
            }
            reference.push().setValue(new LocationStruct(latitude,longitude, false, category,
                    new DateStr().getDateStr(), timeInterval, featherCount));
            editor.putInt("complete_success", 2);
            editor.apply();
            main.replaceFragment(R.id.stuff_on_map, new dwm_search_fab());
        });

    }

    public void onResume() {
        super.onResume();
        hideBottomNavigationView();
    }

    @Override
    public void onStop() {
        super.onStop();
        showBottomNavigationView();
    }

    private void hideBottomNavigationView() {
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.GONE);
    }

    private void showBottomNavigationView() {
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.animate().translationY(0).setDuration(300);
    }
}