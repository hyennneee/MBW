package com.example.mbw.MyPage.data;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

public class LocationResponse {

    @SerializedName("statusCode")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private JsonArray data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public JsonArray getData(){return data;}

}
