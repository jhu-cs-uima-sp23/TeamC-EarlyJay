package com.example.empty;

import java.util.Arrays;

public class StatsCalculator {

    private int[] timeStats;

    private int[] taskStats;

    private int featherNum;


    public StatsCalculator() {
        // the 6 slots in timeStats are:
        // class; work; team; sport; total complete; total failed
        timeStats = new int[6];
        Arrays.fill(timeStats, 0);
        // the 2 slots in timeStats are:
        // total complete; total failed
        taskStats = new int[2];
        Arrays.fill(taskStats, 0);

        featherNum = 0;
    }

    public void addToClass(int time) {
        timeStats[0] += time;
        timeStats[4] += time;
        taskStats[0]++;
        featherNum += time / 5;
    }

    public void addToWork(int time) {
        timeStats[1] += time;
        timeStats[4] += time;
        taskStats[0]++;
        featherNum += time / 5;
    }

    public void addToTeam(int time) {
        timeStats[2] += time;
        timeStats[4] += time;
        taskStats[0]++;
        featherNum += time / 5;
    }

    public void addToSport(int time) {
        timeStats[3] += time;
        timeStats[4] += time;
        taskStats[0]++;
        featherNum += time / 5;
    }

    public void addToFail(int time, int feather) {
        timeStats[5] += time;
        taskStats[1]++;
        featherNum += feather;
    }

    public int getClassTime() { return timeStats[0]; }

    public int getWorkTime() { return timeStats[1]; }

    public int getTeamTime() { return timeStats[2]; }

    public int getSportTime() { return timeStats[3]; }

    public int getTotalCompleteTime() { return timeStats[4]; }

    public int getTotalFailTime() { return timeStats[5]; }

    public int getTotalCompleteTask() { return taskStats[0]; }

    public int getTotalFailTask() { return taskStats[1]; }

    public int getTotalFeather() { return featherNum; }
}
