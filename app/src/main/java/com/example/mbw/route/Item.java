package com.example.mbw.route;

import java.util.Vector;

public class Item {
    private String stationName, remainingTime, arsID, currStation;
    Vector<String> busNum;
    private int subLine, busType, time = 0, publicCode;   //time은 남은 시간이 문자로 들어올 때, 또는 받지 않았을 때 0임
    boolean first;

    public Item(String stationName, Vector<String> busNum, String arsID, int subLine, int busType, boolean first) {
        this.stationName = stationName;
        this.remainingTime = "";
        this.busNum = busNum;
        this.arsID = arsID;
        this.subLine = subLine;
        this.busType = busType;
        this.first = first;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getSubLine() {
        return subLine;
    }

    public Vector<String> getBusNum() {
        return busNum;
    }

    public void setBusNum(Vector<String> busNum) {
        this.busNum = busNum;
    }

    public int getBusType() {
        return busType;
    }   //저상버스 말고 마을, 간선, 지선 등

    public void setBusType(int busType) {
        this.busType = busType;
    }

    public String getArsID() {
        return arsID;
    }

    public boolean isFirst() {
        return first;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setPublicCode(int publicCode) {
        this.publicCode = publicCode;
    }
}
