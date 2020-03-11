package com.example.mbw.DB;

import java.util.ArrayList;

public class DBvalue {

    String departure, destination, sx, sy, ex, ey;

    public DBvalue(ArrayList<String> arrayList){
        this.departure = arrayList.get(0);
        this.destination = arrayList.get(1);
        this.sx = arrayList.get(2);
        this.sy = arrayList.get(3);
        this.ex = arrayList.get(4);
        this.ey = arrayList.get(5);
    }

    public DBvalue(String departure, String destination, String sx, String sy, String ex, String ey) {
        this.departure = departure;
        this.destination = destination;
        this.sx = sx;
        this.sy = sy;
        this.ex = ex;
        this.ey = ey;
    }

    public String getDeparture() {
        return departure;
    }

    public String getDestination() {
        return destination;
    }

    public String getSX() {
        return sx;
    }

    public String getSY() {
        return sy;
    }

    public String getEX() {
        return ex;
    }

    public String getEY() {
        return ey;
    }

    public ArrayList<String> getValue(){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(departure);
        arrayList.add(destination);
        arrayList.add(sx);
        arrayList.add(sy);
        arrayList.add(ex);
        arrayList.add(ey);
        return arrayList;
    }

}
