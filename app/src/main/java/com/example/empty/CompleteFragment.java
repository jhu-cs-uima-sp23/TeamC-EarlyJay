package com.example.empty;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.DialogFragmentNavigatorDestinationBuilder;

import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.empty.databinding.FragmentCompleteBinding;
import com.example.empty.databinding.FragmentCountDownBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

public class CompleteFragment extends Fragment {

    private FragmentCompleteBinding binding;
    Context context;
    private MainActivity mainActivity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    private Map_child_viewModel shared_data;
    private LatLng latLng;
    private Map_frag parentFragment;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parentFragment = (Map_frag) getParentFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        binding = FragmentCompleteBinding.inflate(inflater, container, false);
        shared_data = new ViewModelProvider(requireActivity()).get(Map_child_viewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) getActivity();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        edit = sharedPreferences.edit();
        float featherCount = sharedPreferences.getFloat("featherCount", 0);
        binding.rewardCount.setText("You have received " + featherCount);
        edit.putInt("complete_success", 1);
        edit.putFloat("featherCount", 0);
        edit.apply();

        binding.okButton.setOnClickListener(v -> {
            mainActivity.replaceFragment(R.id.stuff_on_map, new dwm_search_fab());
        });
    }
}