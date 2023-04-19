package com.example.empty;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PlannerItemFirebase {
    String title;
    String startTime;
    int duration;
    String endTime;
    int workType;
//    int notification = -1;
    String notification;
    Boolean pinned = false;

    int status = 0;
    // status = 0 - no record
    // status = 1 - done
    // status = 2 - fail

    String dateStr;

    public PlannerItemFirebase(){}

    public PlannerItemFirebase(String title, String startTime, int duration, int workType,
                            String notification, String dateStr){
        this.title = title;
        this.startTime = startTime;
        Log.d("check", startTime);
        this.duration = duration;
        this.workType = workType;
        this.notification = notification;
        this.endTime = getEndTime(startTime, duration);
        if(title.equals("")) {
            switch (workType) {
                case R.drawable.circle_dashed_6_xxl:
                    this.title = "Work";
                    break;
                case R.drawable.yellows:
                    this.title = "Class";
                    break;
                case R.drawable.triangle_48:
                    this.title = "Team";
                    break;
                case R.drawable.star_2_xxl:
                    this.title = "Sport";
                    break;
                default:
                    break;
            }
        }
        this.dateStr = dateStr;
    }

    public String getTitle() { return title; }

    public String getStartTime() { return startTime; }

    public int getDuration() { return duration; }

    public String getEndTime() { return endTime; }

    public int getWorkType() { return workType; }

    public String getNotification() { return notification; }

    public Boolean getPinned() { return pinned; }

    public int getStatus() { return status; }

    public String getDateStr() { return dateStr; }
    public void togglePin(){
        pinned = !pinned;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setStartTime(String startTime){
        this.startTime = startTime;
    }
    public void setDuration(int duration){
        this.duration = duration;
    }
    public void setWorkType(int workType) {
        this.workType = workType;
    }
    public void setNotification(String notification) {
        this.notification = notification;
    }
    public String getEndTime(String startTime, int duration){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date startDate;
        try {
            startDate = formatter.parse(startTime);
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MINUTE, duration);
        Date endDate = calendar.getTime();
        String formatDate = formatter.format(endDate);
        if(formatDate.charAt(0)=='0'){
            formatDate = formatDate.substring(1);
        }
        Log.d("check", formatDate);
        return formatDate;
    }
}