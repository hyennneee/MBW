package com.example.mbw.problemArea;

import com.google.gson.annotations.SerializedName;

public class problemAreaResponse {
    @SerializedName("statusCode")
    private int code;

    @SerializedName("message")
    private String message;


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}