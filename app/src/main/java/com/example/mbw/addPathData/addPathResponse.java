package com.example.mbw.addPathData;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class addPathResponse {

    @SerializedName("statusCode")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private JsonElement data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public JsonElement getData(){return data;}


}
