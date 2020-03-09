package com.example.mbw.route;

import java.util.Vector;

public class Item {
    private String stationName, remainingTime, arsID, non_step;
    Vector<String> busNum;
    private int subLine, busType;
    boolean first;
    //오디세이 parameter: busID
    //공공데이터 포털 parameter: idx(=ord), localStationID(=stationID)
    //공통: 남은 시간, 역 이름; 지하철: 노선번호이미지, 버스: 버스번호, 버스타입, 저상여부, 정류장 번호


    public Item(String stationName, String remainingTime, Vector<String> busNum, String arsID, int subLine, int busType, String non_step, boolean first) {
        this.stationName = stationName;
        this.remainingTime = remainingTime;
        this.busNum = busNum;
        this.arsID = arsID;
        this.subLine = subLine;
        this.busType = busType;
        this.non_step = non_step;
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

    public void setSubLine(int subLine) {
        this.subLine = subLine;
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

    public void setArsID(String arsID) {
        this.arsID = arsID;
    }

    public String getNon_step() {
        return non_step;
    }

    public void setNon_step(String non_step) {
        this.non_step = non_step;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }
}
