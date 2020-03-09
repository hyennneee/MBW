package com.example.mbw.accountData;

import com.google.gson.annotations.SerializedName;

public class SignInData {
    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public SignInData(String email, String password) {
        this.email = email;
        this.password = password;
    }
}


