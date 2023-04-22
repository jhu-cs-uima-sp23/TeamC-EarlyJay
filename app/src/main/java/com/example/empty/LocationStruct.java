package com.example.empty;

import androidx.annotation.NonNull;
public class LocationStruct {

    private float latitude;

    private float longitude;

    private boolean complete;

    private String type;

    private String dateStr;

    private int timeInterval;

    private int featherNum;

    public LocationStruct() {
        latitude = 0;
        longitude = 0;
        complete = false;
        type = "work";
        dateStr = new DateStr().getDateStr();
        timeInterval = 60;
        featherNum = -1 * timeInterval / 5;
    }

    public LocationStruct(float latitude, float longitude, boolean complete, String type, String dateStr) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.complete = complete;
        this.type = type;
        this.dateStr = dateStr;
        timeInterval = 60;
        int bit = (complete) ? 1 : -1;
        featherNum = timeInterval / 5 * bit;
    }

    public LocationStruct(float latitude, float longitude, boolean complete, String type, String dateStr,
                          int timeInterval,
                          int featherNum) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.complete = complete;
        this.dateStr = dateStr;
        this.type = type;
        this.timeInterval = timeInterval;
        this.featherNum = featherNum;
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

    public int getTimeInterval() { return timeInterval; }

    public void setTimeInterval(int timeInterval) { this.timeInterval = timeInterval; }

    public int getFeatherNum() { return featherNum; }

    public void setFeatherNum(int featherNum) { this.featherNum = featherNum; }

    public void setDateStr(String dateStr) { this.dateStr = dateStr; }

    public String getDateStr() { return dateStr; }



    @NonNull
    @Override
    public String toString() {
        return getComplete() + " " + getLatitude() + " " +
                getLongitude() + " " + getType() + " "
                + getTimeInterval() + " " + getFeatherNum();
    }


}
