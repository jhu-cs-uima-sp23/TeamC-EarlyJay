package com.example.empty;

import android.os.Build;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;

public class LocationStruct {

    private float latitude;

    private float longitude;

    private boolean complete;

    private String type;

    private String dateStr;

    public LocationStruct() {
        latitude = 0;
        longitude = 0;
        complete = false;
        type = "work";
        dateStr = new DateStr().getDateStr();
    }

    public LocationStruct(float latitude, float longitude, boolean complete, String type, String dateStr) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.complete = complete;
        this.type = type;
        this.dateStr = dateStr;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public boolean getComplete() { return complete; }

    public void setComplete(boolean complete) { this.complete = complete; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getDateStr() { return dateStr; }

    public void setDateStr(String dateStr) { this.dateStr = dateStr; }

    @NonNull
    @Override
    public String toString() {
        return getComplete() + " " + getLatitude() + " " +
                getLongitude() + " " + getType() + " " +
                getDateStr() + " ";
    }


}
