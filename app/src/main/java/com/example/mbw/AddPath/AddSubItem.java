package com.example.mbw.AddPath;

public class AddSubItem {


    public String id;
    public String stationName;
    public String line;

    public AddSubItem(String id, String stationName, String line) {
        this.id = id;
        this.stationName = stationName;
        this.line = line;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

}
