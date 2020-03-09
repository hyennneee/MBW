package com.example.mbw.AddPath;

public class AddBusItem {
    private String busStopName;
    private String busStopId;

    private boolean on; // 승차
    private boolean off; // 하차


    public AddBusItem(String busStopName, String busStopId, boolean on, boolean off) {
        this.busStopName = busStopName;
        this.busStopId = busStopId;
        this.on = on;
        this.off = off;
    }


    public String getBusStopName() {
        return busStopName;
    }

    public void setBusStopName(String busStopName) {
        this.busStopName = busStopName;
    }

    public String getBusStopId() {
        return busStopId;
    }

    public void setBusStopId(String busStopId) {
        this.busStopId = busStopId;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public boolean isOff() {
        return off;
    }

    public void setOff(boolean off) {
        this.off = off;
    }

}


