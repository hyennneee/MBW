package com.example.mbw.showPath;

import com.google.gson.annotations.SerializedName;

public class LikeNum {
    @SerializedName("myPathIdx")
    private int index;

    @SerializedName("likeNum")
    private int number;

    public LikeNum(int index, int number){
        this.index = index;
        this.number = number;
    }
}
