package com.example.empty;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.empty.databinding.FragmentMapBinding;
import com.example.empty.databinding.FragmentPopupBinding;

public class Popup_frag extends Fragment {
    private PopupWindow popupWindow;
    private FragmentPopupBinding binding;
    private MainActivity main;
    private Spinner mSpinner;

    public View onCreateView(@NonNull LayoutInflater inflater,
    ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPopupBinding.inflate(inflater, container, false);
        main = (MainActivity) getActivity();
        Context context = getActivity().getApplicationContext();
        View rootView = inflater.inflate(R.layout.fragment_dwm_search_fab, container, false);
        View popupView = LayoutInflater.from(context).inflate(R.layout.fragment_popup, null);

        mSpinner = binding.spinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.type_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);



        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
        popupWindow.showAsDropDown(mSpinner, 0, 0);


        Button startButton = popupView.findViewById(R.id.button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });



        return rootView;

    }


    }
