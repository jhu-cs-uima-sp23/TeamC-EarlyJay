package com.example.empty;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PlannerItemFirebase {
    String title;
    String startTime;
    String duration;
    String endTime;
    int workType;
    // int notification = -1;
    String notification;
    Boolean pinned = false;
    int durationNum;
    int status = 0;
    // status = 0 - no record
    // status = 1 - done
    // status = 2 - fail
    // status = 3 - miss

    String dateStr;

    public PlannerItemFirebase(){}

    @SuppressLint("NonConstantResourceId")
    public PlannerItemFirebase(String title, String startTime, String duration, int workType,
                               String notification, String dateStr){
        this.title = title;
        this.startTime = startTime;
        this.duration = duration;
        this.workType = workType;
        this.notification = notification;
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

    public String getDuration() { return duration; }

    public String getEndTime() {
        durationNum = Integer.parseInt(this.duration.substring(0, this.duration.indexOf(" ")));
        this.endTime = getEndTime(startTime, durationNum);
        return endTime;
    }

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
    public void setDuration(String duration){
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
        return formatDate;
    }
}