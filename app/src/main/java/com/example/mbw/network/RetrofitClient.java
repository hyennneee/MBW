package com.example.mbw.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private final static String BASE_URL = "http:///";
    private static Retrofit retrofit = null;

    public RetrofitClient() {

    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL) // 요청을 보낼 base url
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create()) // JSON 파싱을 위한 GsonConverterFactory 추가
                    .build();
        }

        return retrofit;
    }

}