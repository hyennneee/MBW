package com.example.mbw.pathData;

import com.google.gson.annotations.SerializedName;

public class Position {
    @SerializedName("SX")
    private double SX;

    @SerializedName("SY")
    private double SY;

    @SerializedName("EX")
    private double EX;

    @SerializedName("EY")
    private double EY;

    @SerializedName("SearchPathType")
    private int type;

    public Position(){
        this.type = 0;
    }
    public Position(double SX, double SY, double EX, double EY) {
        this.SX = SX;
        this.SY = SY;
        this.EX = EX;
        this.EY = EY;
        this.type = 0;
    }

    public Position(double SX, double SY, double EX, double EY, int type) {
        this.SX = SX;
        this.SY = SY;
        this.EX = EX;
        this.EY = EY;
        this.type = type;
    }

    public double getSX() {
        return SX;
    }

    public void setSX(double SX) {
        this.SX = SX;
    }

    public double getSY() {
        return SY;
    }

    public void setSY(double SY) {
        this.SY = SY;
    }

    public double getEX() {
        return EX;
    }

    public void setEX(double EX) {
        this.EX = EX;
    }

    public double getEY() {
        return EY;
    }

    public void setEY(double EY) {
        this.EY = EY;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

