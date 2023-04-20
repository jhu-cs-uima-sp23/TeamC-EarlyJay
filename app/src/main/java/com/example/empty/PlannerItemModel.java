package com.example.empty;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PlannerItemModel {
    String title;
    String category;
    String startTime;
    String durationTxt;
    int duration;
    String endTime;
    int workType;
    String notification;
    Boolean pinned;
    int cardBackgroundColor;
    public PlannerItemModel(String title, String startTime, String durationTxt_, int workType,
                            String notification, int color, boolean pin){
        this.title = title;
        this.startTime = startTime;
        Log.d("check", startTime);
        this.durationTxt = durationTxt_;
        Log.d("datacheck", "duration: " + duration);
        this.duration = Integer.parseInt(durationTxt.substring(0, durationTxt.indexOf(" ")));
        this.workType = workType;
        this.notification = notification;
        this.endTime = getEndTime(startTime, duration);
        this.cardBackgroundColor = color;
        this.pinned = pin;
        switch (workType) {
            case R.drawable.circle_dashed_6_xxl:
                this.category = "Work";
                break;
            case R.drawable.yellows:
                this.category = "Class";
                break;
            case R.drawable.triangle_48:
                this.category = "Team";
                break;
            case R.drawable.star_2_xxl:
                this.category = "Sport";
                break;
            default:
                this.category = "";
                break;
        }
        if(title.equals("")) {
            this.title = category;
        }
    }

    public String getTitle() { return title; }

    public String getStartTime() { return startTime; }

    public String getDurationTxt() { return durationTxt; }

    public String getEndTime() { return endTime; }

    public int getWorkTime() { return workType; }

//    public String getNotification() { return notification; }

    public int getNotification(){
        notification = notification.substring(0, notification.indexOf(" "));
        return Integer.parseInt(notification);
    }

    public Boolean getPinned() { return pinned; }
    public void togglePin(){
        pinned = !pinned;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public void setStartTime(String startTime){
        this.startTime = startTime;
    }
    public void setDurationTxt(String durationTxt){
        this.durationTxt = durationTxt;
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
        return formatDate;
    }
}
