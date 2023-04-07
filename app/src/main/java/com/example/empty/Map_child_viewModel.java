package com.example.empty;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Map_child_viewModel extends ViewModel {
    private MutableLiveData<String> data = new MutableLiveData<>();

    public void setData(String value) {
        data.setValue(value);
    }

    public LiveData<String> getData() {
        return data;
    }
}
