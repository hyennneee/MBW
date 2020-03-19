package com.example.mbw.DB;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RouteDB {
    @SerializedName("startAddress")
    private String departure;

    @SerializedName("endAddress")
    private String destination;

    @SerializedName("SX")
    private String SX;

    @SerializedName("SY")
    private String SY;

    @SerializedName("EX")
    private String EX;

    @SerializedName("EY")
    private String EY;

    public RouteDB(ArrayList<String> arrayList){
        this.departure = arrayList.get(0);
        this.destination = arrayList.get(1);
        this.SX = arrayList.get(2);
        this.SY = arrayList.get(3);
        this.EX = arrayList.get(4);
        this.EY = arrayList.get(5);
    }

    public RouteDB(String departure, String destination, String sx, String sy, String ex, String ey) {
        this.departure = departure;
        this.destination = destination;
        this.SX = sx;
        this.SY = sy;
        this.EX = ex;
        this.EY = ey;
    }

    public String getDeparture() {
        return departure;
    }

    public String getDestination() {
        return destination;
    }

    public String getSX() {
        return SX;
    }

    public String getSY() {
        return SY;
    }

    public String getEX() {
        return EX;
    }

    public String getEY() {
        return EY;
    }

    public ArrayList<String> getValue(){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(departure);
        arrayList.add(destination);
        arrayList.add(SX);
        arrayList.add(SY);
        arrayList.add(EX);
        arrayList.add(EY);
        return arrayList;
    }

}
