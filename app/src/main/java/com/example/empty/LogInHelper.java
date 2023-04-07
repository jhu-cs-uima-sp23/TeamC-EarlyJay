package com.example.empty;

public class LogInHelper {

    private String uid;

    public LogInHelper() {
        this.uid = "";
    }

    public LogInHelper(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}