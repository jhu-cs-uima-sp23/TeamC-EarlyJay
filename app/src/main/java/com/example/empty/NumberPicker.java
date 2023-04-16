package com.example.empty;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.empty.databinding.FragmentSeekBarBinding;

import me.tankery.lib.circularseekbar.CircularSeekBar;

public class NumberPicker extends DialogFragment implements CircularSeekBar.OnCircularSeekBarChangeListener {
    public interface OnDialogDismissedListener {
        void onDismissed();
    }
    private OnDialogDismissedListener listener;
    private CircularSeekBar progressCircular;
    private int factorProgress = 60;
    FragmentSeekBarBinding binding;
    SharedPreferences.Editor editor;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSeekBarBinding.inflate(inflater, container, false);
        context = getContext();
        editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        progressCircular = binding.circularSeekBar;
        progressCircular.setOnSeekBarChangeListener(this);
        binding.cancelButton.setOnClickListener(e->dismiss());
        binding.confirmButton.setOnClickListener(e->{
            String durationTxt = binding.countNum.getText().toString();
            if(factorProgress == 0){
                Toast.makeText(context, "Duration cannot be 0", Toast.LENGTH_LONG).show();
                return;
            }
            editor.putString("durationTxt",durationTxt);
            editor.apply();
            dismiss();
        });
        return binding.getRoot();
    }
    public void setOnDialogDismissedListener(OnDialogDismissedListener listener) {
        this.listener = listener;
    }
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onDismissed();
        }
    }
    @Override
    public void onProgressChanged(CircularSeekBar circularSeekBar,
                                  float progress, boolean fromUser) {
        if (progress <= 0) {
            factorProgress = 0;
        } else if (progress >= 120){
            factorProgress = 24;
        } else {
            factorProgress = (int) (progress / 5);
        }
        binding.countNum.setText(factorProgress * 5 + " min");
    }
    @Override
    public void onStopTrackingTouch(CircularSeekBar seekBar) {}
    @Override
    public void onStartTrackingTouch(CircularSeekBar seekBar) {}
}
