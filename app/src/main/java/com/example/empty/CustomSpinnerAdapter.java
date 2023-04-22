package com.example.empty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<SpinnerItem> {

    public CustomSpinnerAdapter(Context context, List<SpinnerItem> spinnerItems) {
        super(context, 0, spinnerItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item_layout, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.spinner_item_image);
        TextView textView = convertView.findViewById(R.id.spinner_item_text);

        SpinnerItem spinnerItem = getItem(position);

        if (spinnerItem != null) {
            imageView.setImageResource(spinnerItem.getImageResId());
            textView.setText(spinnerItem.getText());
        }

        return convertView;
    }
}
