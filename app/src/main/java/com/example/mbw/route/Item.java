package com.example.mbw.route;

public class Item {
    private String stationName, remainingTime,  busNum, stationNo, finalStation;
    private int subLine, busType;
    private boolean non_step;
    //오디세이 parameter: busID
    //공공데이터 포털 parameter: idx(=ord), localStationID(=stationID)
    //공통: 남은 시간, 역 이름; 지하철: 노선번호이미지, 버스: 버스번호, 버스타입, 저상여부, 정류장 번호


    public Item(String stationName, String remainingTime, String busNum, String stationNo, String finalStation, int subLine, int busType, boolean non_step) {
        this.stationName = stationName;
        this.remainingTime = remainingTime;
        this.busNum = busNum;
        this.stationNo = stationNo;
        this.finalStation = finalStation;
        this.subLine = subLine;   //subwayCode
        this.busType = busType;
        this.non_step = non_step;
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

    public int getSubLineImage() {
        return subLine;
    }

    public void setSubLineImage(int subLineImage) {
        this.subLine = subLineImage;
    }

    public String getBusNum() {
        return busNum;
    }

    public void setBusNum(String busNum) {
        this.busNum = busNum;
    }

    public int getBusType() {
        return busType;
    }

    public void setBusType(int busType) {
        this.busType = busType;
    }

    public String getStationNo() {
        return stationNo;
    }

    public void setStationNo(String stationNo) {
        this.stationNo = stationNo;
    }

    public boolean isNon_step() {
        return non_step;
    }

    public void setNon_step(boolean non_step) {
        this.non_step = non_step;
    }

    public String getFinalStation() {
        return finalStation;
    }

    public void setFinalStation(String finalStation) {
        this.finalStation = finalStation;
    }
}
