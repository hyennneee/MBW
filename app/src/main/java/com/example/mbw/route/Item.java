package com.example.mbw.route;

import java.util.Vector;

public class Item {
    private String stationName, remainingTime, arsID, currStation;
    Vector<String> busNum;
    private int subLine, busType, time = 0, publicCode;   //time은 남은 시간이 문자로 들어올 때, 또는 받지 않았을 때 0임
    boolean first;
    //오디세이 parameter: busID
    //공공데이터 포털 parameter: idx(=ord), localStationID(=stationID)
    //공통: 남은 시간, 역 이름; 지하철: 노선번호이미지, 버스: 버스번호, 버스타입, 저상여부, 정류장 번호

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

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public String getCurrStation() {
        return currStation;
    }

    public void setCurrStation(String currStation) {
        this.currStation = currStation;
    }

    public int getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(int publicCode) {
        this.publicCode = publicCode;
    }
}
