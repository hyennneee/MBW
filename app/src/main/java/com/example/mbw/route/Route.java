package com.example.mbw.route;

public class Route {
    private String busStation, busNum, busID, subStation;
    private Integer remainingTime, busType, stationId, subCode;
    private boolean non_step;
    //localStationID: 공공데이터 포털에 입력할 정류장 id
    //idx = ord

    public Route(String busStation, String busNum, String busID, String subStation, Integer remainingTime, Integer busType, Integer stationId, Integer subCode, boolean non_step) {
        this.busStation = busStation;
        this.busNum = busNum;
        this.busID = busID; //localBusID
        this.subStation = subStation;
        this.remainingTime = remainingTime;
        this.busType = busType;
        this.stationId = stationId;
        this.subCode = subCode;
        this.non_step = non_step;
    }


    public String getBusStation() {
        return busStation;
    }

    public void setBusStation(String busStation) {
        this.busStation = busStation;
    }

    public String getBusNum() {
        return busNum;
    }

    public String getBusID() {
        return busID;
    }

    public void setBusID(String busID) {
        this.busID = busID;
    }

    public void setBusNum(String busNum) {
        this.busNum = busNum;
    }

    public String getSubStation() {
        return subStation;
    }

    public void setSubStation(String subStation) {
        this.subStation = subStation;
    }

    public Integer getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(Integer remainingTime) {
        this.remainingTime = remainingTime;
    }

    public Integer getBusType() {
        return busType;
    }

    public void setBusType(Integer busType) {
        this.busType = busType;
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public Integer getSubCode() {
        return subCode;
    }

    public void setSubCode(Integer subCode) {
        this.subCode = subCode;
    }

    public boolean isNon_step() {
        return non_step;
    }

    public void setNon_step(boolean non_step) {
        this.non_step = non_step;
    }
}