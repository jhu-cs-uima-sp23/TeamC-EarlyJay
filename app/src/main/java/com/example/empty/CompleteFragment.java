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

import com.example.empty.databinding.FragmentCompleteBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CompleteFragment extends Fragment {

    private FragmentCompleteBinding binding;
    Context context;
    private MainActivity mainActivity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = requireActivity().getApplicationContext();
        binding = FragmentCompleteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        int featherCount = sharedPreferences.getInt("featherCount", 0);


        float longitude = sharedPreferences.getFloat("longitude", 0);
        float latitude = sharedPreferences.getFloat("latitude", 0);
        String uid = sharedPreferences.getString("uid", "");
        String category = sharedPreferences.getString("category", "");
        int timeInterval = sharedPreferences.getInt("totalTimeInterval", 0);

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference().child("users").child(uid).child(new DateStr().getDateStr());
        reference.push().setValue(new LocationStruct(latitude,longitude, true, category, new DateStr().getDateStr(),
                timeInterval, featherCount));

        String formatted = getString(R.string.warning_txt, featherCount);
        binding.rewardCount.setText(formatted);
        edit.putInt("complete_success", 1);
        edit.putInt("featherCount", 0);
        edit.apply();

        binding.okButton.setOnClickListener(v -> {
            showBottomNavigationView();
            mainActivity.replaceFragment(R.id.stuff_on_map, new dwm_search_fab());
        });

        String plannerDateStr = sharedPreferences.getString("currDateStr", new DateStr().getDateStr());
        boolean planner = sharedPreferences.getBoolean("PlannerTask", false);
        String startTime = sharedPreferences.getString("PlanTaskStartTime", "0:00");
        if (planner) {
            DatabaseReference reference_planner = rootNode.getReference().child("planner").child(uid).child(plannerDateStr);
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
                                itemRef.child("status").setValue(1);
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