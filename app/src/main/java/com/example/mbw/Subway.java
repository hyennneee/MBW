package com.example.mbw;

public class Subway {
    private String wholeTime, walkingTime, transferNum, money, busStation, subStation;
    //image는 int인가?

    public Subway(String wholeTime, String walkingTime, String transferNum, String money) {
        this.wholeTime = wholeTime;
        this.walkingTime = walkingTime;
        this.transferNum = transferNum;
        this.money = money;
    }

    public String getWholeTime() {
        return wholeTime;
    }

    public void setWholeTime(String wholeTime) {
        this.wholeTime = wholeTime;
    }

    public String getWalkingTime() {
        return walkingTime;
    }

    public void setWalkingTime(String walkingTime) {
        this.walkingTime = walkingTime;
    }

    public String getTransferTime() {
        return transferNum;
    }

    public void setTransferTime(String transferTime) {
        this.transferNum = transferTime;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
