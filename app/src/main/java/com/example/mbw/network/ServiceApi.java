package com.example.mbw.network;

import com.example.mbw.accountData.SignInData;
import com.example.mbw.accountData.SignInResponse;
import com.example.mbw.accountData.SignUpData;
import com.example.mbw.accountData.SignUpResponse;
import com.example.mbw.addPathData.addPathData;
import com.example.mbw.addPathData.addPathResponse;
import com.example.mbw.problemArea.problemAreaData;
import com.example.mbw.problemArea.problemAreaResponse;
import com.example.mbw.pathData.PathResponse;
import com.example.mbw.problemArea.problemAreaResponse;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ServiceApi {
    @POST("/users/signin")
    Call<SignInResponse> userSignIn(@Body SignInData data);

    @POST("/users/signup")
    Call<SignUpResponse> userSignUp(@Body SignUpData data);

    @GET("searchPath")
    Call<PathResponse>searchPath(@Query("SX") String SX, @Query("SY") String SY, @Query("EX") String EX, @Query("EY") String EY, @Query("SearchPathType") int type);

    @POST("/addMyPath")
    Call<addPathResponse> addMyPath(@Header("Content-Type") String contentType, @Header("token") String token, @Body addPathData data);

    @POST("/problemArea")
    Call<problemAreaResponse> reportProblem(@Header("Content-Type") String contentType, @Body problemAreaData data);
}
