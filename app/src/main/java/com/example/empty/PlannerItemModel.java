package com.example.empty;
import android.content.res.Resources;
import android.util.Log;

public class PlannerItemModel {
    String title;
    String startTime;
    int duration;
    String workType;
    int notification = -1;
    Boolean pinned = false;


    public PlannerItemModel(String startTime, int duration, String workType){
        this.startTime = startTime;
        this.duration = duration;
        this.workType = workType;
        this.title = workType;
    }
    public PlannerItemModel(String title, String startTime, int duration, String workType, int notification){
        this.title = title;
        this.startTime = startTime;
        this.duration = duration;
        this.workType = workType;
        this.notification = notification;
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
    public void setWorkType(String workType) {
        this.workType = workType;
        String[] workTypes = Resources.getSystem().getStringArray(R.array.type_array);
        if(workType.equals(workTypes[0])){

        }else if(workType.equals(workTypes[1])){

        }else if(workType.equals(workTypes[2])){

        }else if(workType.equals(workTypes[3])){

        }else{
            Log.d("TAG", "setWorkType: invalidType");
        }
    }
    public void setNotification(int notification) {
        this.notification = notification;
    }
}
