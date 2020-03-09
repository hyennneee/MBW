package com.example.mbw.accountData;

import com.google.gson.annotations.SerializedName;

public class SignUpResponse {

    @SerializedName("statusCode")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private int data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getData(){return data;}
}
