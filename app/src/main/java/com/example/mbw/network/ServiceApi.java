package com.example.mbw.network;

import com.example.mbw.DB.PlaceDB;
import com.example.mbw.DB.RouteDB;
import com.example.mbw.MyPage.data.FavoritePathResponse;
import com.example.mbw.MyPage.data.LocationResponse;
import com.example.mbw.MyPage.data.PostResponse;
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
import com.example.mbw.showPath.LikeNum;

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

    @GET("/searchPath")
    Call<PathResponse>searchPath(@Query("SX") String SX, @Query("SY") String SY, @Query("EX") String EX, @Query("EY") String EY, @Query("SearchPathType") int type);

    @POST("/myPlace/addFavoritePath")
    Call<PostResponse>addFavoritePath(@Header("Content-Type") String contentType, @Header("token") String token, @Body RouteDB routeDB);

    @GET("/myPlace/getFavoritePath")
    Call<LocationResponse>getFavoritePath(@Header("token") String token);

    @POST("/myPlace/addLocation")
    Call<PostResponse>addLocation(@Header("token") String token, @Body PlaceDB placeDB);

    @GET("/myPlace/getLocation")
    Call<LocationResponse>getLocation(@Header("token") String token, @Query("category") int category);

    @POST("/searchPath/setLikeNum")
    Call<PostResponse>setLikeNum(@Header("Content-Type") String contentType, @Header("token") String token, @Body LikeNum likeNum);

    @POST("/addMyPath")
    Call<addPathResponse> addMyPath(@Header("Content-Type") String contentType, @Header("token") String token, @Body addPathData data);

    @POST("/problemArea")
    Call<problemAreaResponse> reportProblem(@Header("Content-Type") String contentType, @Body problemAreaData data);
}
