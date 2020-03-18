package com.example.mbw.DB;

import com.google.gson.annotations.SerializedName;

public class PlaceDB {

    @SerializedName("category")
    private Integer category;

    @SerializedName("address")
    private String address;

    @SerializedName("X")
    private String X;

    @SerializedName("Y")
    private String Y;

    public PlaceDB(int name, String place, String x, String y) {
        this.category = name;
        this.address = place;
        this.X = x;
        this.Y = y;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getPlace() {  //getAddress
        return address;
    }

    public void setPlace(String address) {
        this.address = address;
    }

    public String getX() {
        return X;
    }

    public void setX(String x) {
        X = x;
    }

    public String getY() {
        return Y;
    }

    public void setY(String y) {
        Y = y;
    }
}
