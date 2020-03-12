package com.example.mbw.pathData;

import com.google.gson.annotations.SerializedName;

public class Position {
    @SerializedName("SX")
    private String SX;

    @SerializedName("SY")
    private String SY;

    @SerializedName("EX")
    private String EX;

    @SerializedName("EY")
    private String EY;

    @SerializedName("SearchPathType")
    private int type;

    public Position(){
        this.type = 0;
    }
    //default type = 0인거랑 입력 받는거 하나씩


    public Position(String SX, String SY, String EX, String EY) {
        this.SX = SX;
        this.SY = SY;
        this.EX = EX;
        this.EY = EY;
        this.type = 0;
    }

    public Position(String SX, String SY, String EX, String EY, int type) {
        this.SX = SX;
        this.SY = SY;
        this.EX = EX;
        this.EY = EY;
        this.type = type;
    }

    public String getSX() {
        return SX;
    }

    public void setSX(String SX) {
        this.SX = SX;
    }

    public String getSY() {
        return SY;
    }

    public void setSY(String SY) {
        this.SY = SY;
    }

    public String getEX() {
        return EX;
    }

    public void setEX(String EX) {
        this.EX = EX;
    }

    public String getEY() {
        return EY;
    }

    public void setEY(String EY) {
        this.EY = EY;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

