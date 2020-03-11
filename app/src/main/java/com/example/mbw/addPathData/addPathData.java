package com.example.mbw.addPathData;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class addPathData {
    @SerializedName("startAddress")
    private String startAddress;

    @SerializedName("startLongi")
    private double startLongi;

    @SerializedName("startLati")
    private double startLati;

    @SerializedName("endAddress")
    private String endAddress;

    @SerializedName("endLongi")
    private double endLongi;

    @SerializedName("endLati")
    private double endLati;

    @SerializedName("path")
    private JsonElement path;

    public addPathData(String startAddress, double startLongi, double startLati, String endAddress, double endLongi, double endLati, JsonElement path) {
        this.startAddress = startAddress;
        this.startLongi = startLongi;
        this.startLati = startLati;
        this.endAddress = endAddress;
        this.endLongi = endLongi;
        this.endLati = endLati;
        this.path = path;
    }
}

