package com.example.empty;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.empty.databinding.FragmentDwmSearchFabBinding;
import com.example.empty.databinding.FragmentMapBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link dwm_search_fab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class dwm_search_fab extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FloatingActionButton startButton;

    private FragmentDwmSearchFabBinding binding;

    public dwm_search_fab() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment dwm_search_fab.
     */
    // TODO: Rename and change types and number of parameters
    public static dwm_search_fab newInstance(String param1, String param2) {
        dwm_search_fab fragment = new dwm_search_fab();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentDwmSearchFabBinding.inflate(inflater, container, false);
        startButton = binding.floatingActionButton;
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });

        return binding.getRoot();

//        return inflater.inflate(R.layout.fragment_dwm_search_fab, container, false);
    }


    private void showPopupWindow(View view){
        PopupWindow popupWindow = new PopupWindow(getActivity());
        View popupView = getLayoutInflater().inflate(R.layout.popup_start, null);
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        Button saveButton = popupView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });


    }
}