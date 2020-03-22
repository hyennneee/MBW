package com.example.mbw.MyPage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mbw.DB.RouteDB;
import com.example.mbw.MainActivity;
import com.example.mbw.MyPage.data.LocationResponse;
import com.example.mbw.R;
import com.example.mbw.network.RetrofitClient;
import com.example.mbw.network.ServiceApi;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentPath fragmentPath;
    private FragmentPlace fragmentPlace;
    private FragmentTransaction transaction;
    private TextView path, place, userName;
    private View first, second;
    private String token;
    public static String homeAddress = "", officeAddress = "";
    ServiceApi service;
    boolean home = false, office = false;
    static ArrayList<RouteDB> ArrayListrouteDB;
    boolean getData = false;
    int test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);
    }

    @Override
    public void onStart(){
        super.onStart();

        service = RetrofitClient.getClient().create(ServiceApi.class);
        token = MainActivity.getToken();
        fragmentManager = getSupportFragmentManager();
        fragmentPath = new FragmentPath();
        fragmentPlace = new FragmentPlace();

        path = findViewById(R.id.pathView);
        place = findViewById(R.id.placeView);

        first = findViewById(R.id.firstLine);
        second = findViewById(R.id.secondLine);

        userName = findViewById(R.id.userNameTV);
        getData = false;
    }

    @Override
    public void onResume(){
        super.onResume();
        test = 0;
        String name = getIntent().getStringExtra("NAME");
        userName.setText(name);

        GetBookmarkTask bookmarkTask = new GetBookmarkTask(MainActivity.getToken());
        bookmarkTask.execute();

        GetLocationTask locationTask;
        for(int i = 1; i <= 2; i++) {
            locationTask = new GetLocationTask(token, i);
            locationTask.execute();
        }
    }

    private class GetBookmarkTask extends AsyncTask<Void, Void, Void> {
        private  String token;

        public GetBookmarkTask(String token) {
            this.token = token;
            ArrayListrouteDB = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            service.getFavoritePath(token).enqueue(new Callback<LocationResponse>() {
                @Override
                public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                    LocationResponse result = response.body();
                    boolean success = response.isSuccessful();
                    if(success) {
                        int code = result.getCode();
                        if (code == 200) {   //즐겨찾는 경로 조회 성공
                            JsonArray data = result.getData();
                            if(data != null) {
                                try {
                                    for (JsonElement jsonElement : data) {
                                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                                        String departure, destination, SX, SY, EX, EY;
                                        departure = jsonObject.get("startAddress").getAsString();
                                        destination = jsonObject.get("endAddress").getAsString();
                                        SX = jsonObject.get("SX").getAsString();
                                        SY = jsonObject.get("SY").getAsString();
                                        EX = jsonObject.get("EX").getAsString();
                                        EY = jsonObject.get("EY").getAsString();
                                        RouteDB routeDB = new RouteDB(departure, destination, SX, SY, EX, EY);
                                        ArrayListrouteDB.add(routeDB);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                Toast.makeText(MyPageActivity.this, "에러 발생", Toast.LENGTH_SHORT).show();
                            }
                        }
                        getData = true;
                        //Log.i("background", "" + test);
                    }
                    else {
                        try {
                            setError(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        getData = true;
                    }
                }
                @Override
                public void onFailure(Call<LocationResponse> call, Throwable t) {
                    getData = true;
                    Toast.makeText(MyPageActivity.this, "통신 에러 발생", Toast.LENGTH_SHORT).show();
                    Log.e("통신 에러 발생", t.getMessage());
                }
            });
            while(true){
                //test++;
                if(getData)
                    break;
                if(test == 1)
                    Log.i("background", "" + getData);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i("onpost", "성공");
            if(ArrayListrouteDB.size() != 0) {
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.myPageFrame, fragmentPath).commit();
            }
        }
    }

    public void setError(Response<LocationResponse> response) throws IOException {
        Gson gson = new Gson();

        LocationResponse result = gson.fromJson(response.errorBody().string(), LocationResponse.class);
        Toast.makeText(MyPageActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private class GetLocationTask extends AsyncTask<Void, Void, Void> {
        private  String token;
        private int category;

        public GetLocationTask(String token, int category) {
            this.token = token;
            this.category = category;
        }

        @Override
        protected Void doInBackground(Void... params) {

            service.getLocation(token, category).enqueue(new Callback<LocationResponse>() {
                @Override
                public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                    LocationResponse result = response.body();
                    boolean success = response.isSuccessful();
                    if (success) {
                        int code = result.getCode();
                        if (code == 200) {   //즐겨찾는 장소 조회 성공
                            JsonArray data = result.getData();
                            try {
                                JsonObject jsonObject = data.get(0).getAsJsonObject();
                                if (jsonObject.get("category").getAsInt() == 1) {  //집
                                    homeAddress = jsonObject.get("address").getAsString();
                                    home = true;
                                }
                                else {   //회사
                                    officeAddress = jsonObject.get("address").getAsString();
                                    office = true;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            setError(response, category);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //등록된 주소 없을 때
                        //"주소를 등록해주세요"
                    }
                }

                @Override
                public void onFailure(Call<LocationResponse> call, Throwable t) {
                    Toast.makeText(MyPageActivity.this, "MyPage 통신 에러 발생", Toast.LENGTH_SHORT).show();
                    Log.e("MyPage 통신 에러 발생", t.getMessage());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        }
    }

    public void setError(Response<LocationResponse> response, int category) throws IOException {
        Gson gson = new Gson();
        LocationResponse result = gson.fromJson(response.errorBody().string(), LocationResponse.class);
        if(result.getCode() == 404){    //등록된 주소 없음
            if(category == 1) {  //집
                homeAddress = "집을 등록해주세요";
                home = true;
            }
            else{
                officeAddress = "회사를 등록해주세요";
                office = true;
            }
        }
        else{
            Toast.makeText(MyPageActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void myPageFragmentHandler(View v) {

        transaction = fragmentManager.beginTransaction();

        switch (v.getId()) {
            case R.id.pathView:
                transaction.replace(R.id.myPageFrame, fragmentPath).commitAllowingStateLoss();
                path.setTextColor(Color.parseColor("#1abc9c"));
                place.setTextColor(getResources().getColor(android.R.color.darker_gray));

                first.setBackgroundColor(Color.parseColor("#1abc9c"));
                second.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                break;
            case R.id.placeView:
                if(home && office) {
                    transaction.replace(R.id.myPageFrame, fragmentPlace).commitAllowingStateLoss();

                    path.setTextColor(getResources().getColor(android.R.color.darker_gray));
                    place.setTextColor(Color.parseColor("#1abc9c"));

                    first.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    second.setBackgroundColor(Color.parseColor("#1abc9c"));
                }
                break;

        }
    }

    public void onClickBack(View v){
        //fragmentManager.beginTransaction().remove(fragmentPath).commit();
        finish();
    }
}
