package com.example.mbw.pathData;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import retrofit2.http.Query;

public class PathResponse {
    @SerializedName("statusCode")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private JsonElement data;

    public String getMessage() {
        return message;
    }

    public JsonElement getData() {
        return data;
    }
}
