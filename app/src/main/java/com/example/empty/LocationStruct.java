package com.example.empty;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;

public class LocationStruct {

    private float latitude;

    private float longitude;

    private boolean complete;

    private String type;

    public LocationStruct() {
        latitude = 0;
        longitude = 0;
        complete = false;
        type = "work";
    }

    public LocationStruct(float latitude, float longitude, boolean complete, String type) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.complete = complete;
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public boolean getComplete() { return complete; }

    public void setComplete(boolean complete) { this.complete = complete; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }


}
