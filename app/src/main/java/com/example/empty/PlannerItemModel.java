package com.example.empty;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PlannerItemModel {
    String title;
    String startTime;
    int duration;
    String endTime;
    int workType;
    int notification = -1;
    Boolean pinned = false;
    int cardBackgroundColor;
    public PlannerItemModel(String title, String startTime, int duration, int workType, int notification, int color){
        this.title = title;
        this.startTime = startTime;
        this.duration = duration;
        this.workType = workType;
        this.notification = notification;
        this.endTime = getEndTime(startTime, duration);
        this.cardBackgroundColor = color;
        if(title.equals("")){
            switch (workType){
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
    }
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
    public void setNotification(int notification) {
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
        return formatDate;
    }
}
