package com.example.mbw.AddPath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mbw.MainActivity;
import com.example.mbw.R;
import com.example.mbw.addPathData.addPathData;
import com.example.mbw.addPathData.addPathResponse;
import com.example.mbw.network.RetrofitClient;
import com.example.mbw.network.ServiceApi;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPathActivity extends AppCompatActivity {

    private String startPoint= "숙명여자대학교 명신관";
    private String endPoint = "동대문디자인플라자";

    ServiceApi service;
    JSONObject obj, tokenObj;
    private double startLongi = 126.9614495;
    private double startLati = 37.545708;
    private double endLongi = 127.0073163;
    private double endLati = 37.5667509;
    JSONObject pathObj;
    private ArrayList<AddItem> addItemList = new ArrayList<AddItem>();
    Intent intent = null;
    RecyclerView recyclerView;
    TextView startTv, endTv;


    boolean usingSub, usingBus = false;
    int pathType = 0;
    String token;
    String resultMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_path);

        recyclerView = findViewById(R.id.addPath_rv) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        startTv = findViewById(R.id.startPoint);
        endTv = findViewById(R.id.endPoint);
        startPoint = startTv.getText().toString();
        endPoint = endTv.getText().toString();

        service = RetrofitClient.getClient().create(ServiceApi.class);
        token = getIntent().getStringExtra("token");
        startLongi = getIntent().getDoubleExtra("sx", 126.9614495);
        startLati = getIntent().getDoubleExtra("sy", 37.545708);
        endLongi = getIntent().getDoubleExtra("ex", 126.9614495);
        endLati = getIntent().getDoubleExtra("ey", 37.545708);
        startPoint = getIntent().getStringExtra("departureName");
        endPoint = getIntent().getStringExtra("destinationName");

        startTv.setText(startPoint);
        endTv.setText(endPoint);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent)
    {
        super.onActivityResult(requestCode, resultCode, resultIntent);

        if(requestCode == Code.requestCode && resultCode == Code.subresultCode) { // 지하철 경로 추가 되었을때

            String selectedLine = resultIntent.getStringExtra("line");
            String startName = resultIntent.getStringExtra("start");
            String endName = resultIntent.getStringExtra("end");
            String startID = resultIntent.getStringExtra("startID");
            String endID = resultIntent.getStringExtra("endID");

            Log.i("startId", startID);
            Log.i("endId", endID);
            addItemList.add(new AddItem(null, selectedLine, startName, startID, endName,  endID));
            usingSub = true;
        }

        else if(requestCode == Code.requestCode && resultCode == Code.busresultCode) { // 버스 경로 추가 되었을때
            int passingStation = resultIntent.getIntExtra("passingStation",0);
            String startID = resultIntent.getStringExtra("startID");
            ArrayList<String> passingStationList = resultIntent.getStringArrayListExtra("passingStationList");
            //ArrayList<String> passingStationList = (ArrayList<String>) getIntent().getSerializableExtra("passingStationList");
            Log.i("List", passingStationList.get(0));
            String startName = resultIntent.getStringExtra("startName");
            String endName = resultIntent.getStringExtra("endName");
            String busType = resultIntent.getStringExtra("busType");
            String busNum = resultIntent.getStringExtra("busNum");

            Double startX = resultIntent.getDoubleExtra("startX", startLongi);
            Double startY = resultIntent.getDoubleExtra("startY", startLati);
            Double endX = resultIntent.getDoubleExtra("endX", endLongi);
            Double endY = resultIntent.getDoubleExtra("endY", endLati);

            addItemList.add(new AddItem(busType, busNum, startName, endName, startX, startY, endX, endY, startID, passingStation, passingStationList));

            usingBus = true;
        }
        AddItemAdapter adapter = new AddItemAdapter(addItemList, this);
        recyclerView.setAdapter(adapter);

    }

    public void addSubItem(View v){
        intent = new Intent(this, AddSubActivity.class);
        startActivityForResult(intent, Code.requestCode);
    }

    public void addBusItem(View v){
        intent = new Intent(AddPathActivity.this, AddBusActivity.class);
        startActivityForResult(intent, Code.requestCode);
    }

    public void makePathJSON(){
        obj = new JSONObject();
        tokenObj = new JSONObject();
        try{
            //tokenObj.put("Content-Type", "application/json");
            tokenObj.put("token", token);
            obj.put("startAddress", startPoint);
            obj.put("startLongi", startLongi);
            obj.put("startLati", startLati);
            obj.put("endAddress", endPoint);
            obj.put("endLongi", endLongi);
            obj.put("endLati", endLati);

            // path array
            pathObj = new JSONObject();
            // path Type : 1-지하철, 2-버스, 3-버스+지하철
            pathObj.put("pathType", pathType);

            /* 경로 시작에 도보 추가*/
            JSONArray subPathArray = new JSONArray();
            JSONObject subPathStartObj = new JSONObject();
            subPathStartObj.put("trafficType", 3);
            subPathStartObj.put("distance", 0);
            subPathStartObj.put("sectionTime", 0);
            subPathArray.put(subPathStartObj);

            for (int i=0; i<addItemList.size(); i++){
                AddItem nowItem = addItemList.get(i);
                JSONObject subPathObj = new JSONObject();
                if(addItemList.get(i).getBusType()!=null){ // 버스일 때
                    subPathObj.put("trafficType", 2);
                    subPathObj.put("stationCount", nowItem.getPassingStation());
                    subPathObj.put("startName", nowItem.getStart());
                    subPathObj.put("startX", nowItem.getStartX());
                    subPathObj.put("startY", nowItem.getStartY());
                    subPathObj.put("endName", nowItem.getEnd());
                    subPathObj.put("endX", nowItem.getEndX());
                    subPathObj.put("endY", nowItem.getEndY());
                    subPathObj.put("startID", nowItem.getStartId());
                    subPathObj.put("busNo", nowItem.getNum());
                    JSONArray list = new JSONArray(nowItem.getPassingStationList());
                    subPathObj.put("stations", list);
                }
                else{ // 지하철일 때
                    subPathObj.put("trafficType", 1);
                    subPathObj.put("startID", nowItem.getStartId());
                    subPathObj.put("endID",nowItem.getEndId());
                }
                subPathArray.put(subPathObj);

                /*대중교통 끝에 도보 추가*/
                JSONObject subPathEndObj = new JSONObject();
                subPathEndObj.put("trafficType", 3);
                subPathEndObj.put("distance", 0);
                subPathEndObj.put("sectionTime", 0);
                subPathArray.put(subPathEndObj);
            }

            pathObj.put("subPath", subPathArray);

            obj.put("path", pathObj);

            Log.i("JSON Test", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onClick(View view){
        if (usingBus == true & usingSub == true)
            pathType =3;
        else if (usingBus == true)
            pathType = 2;
        else if (usingSub == true)
            pathType = 1;
        else
            pathType = 0; // 추가된 경로 없음.
        makePathJSON();
        Gson gson = new Gson();
        JsonElement element = gson.fromJson(pathObj.toString(), JsonElement.class);
        sendMyPath(token, new addPathData(startPoint, startLongi, startLati, endPoint, endLongi, endLati, element));
    }

    private void sendMyPath(String token, addPathData data){

        service.addMyPath("application/json", token, data).enqueue(new Callback<addPathResponse>() {
            @Override
            public void onResponse(Call<addPathResponse> call, Response<addPathResponse> response) {
                addPathResponse result = response.body();

                boolean success = response.isSuccessful();

                if(success) {
                    int code = result.getCode();

                    if (code == 200) {   // 서버 전송 성공
                        JsonElement userData = result.getData();
                        try {

                            resultMessage = userData.getAsJsonObject().get("message").getAsString();
                            Log.i("resultMessage", resultMessage);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getApplicationContext(),
                                "경로 공유 성공",
                                Toast.LENGTH_LONG)
                                .show();
                        finish();
                    }
                }
                else {
                    try {
                        setError(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<addPathResponse> call, Throwable t) {
                Toast.makeText(AddPathActivity.this, "경로 추가 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("경로 추가 에러 발생", t.getMessage());

            }
        });
    }

    public void setError(Response<addPathResponse> response) throws IOException {
        Gson gson = new Gson();
        addPathResponse result = gson.fromJson(response.errorBody().string(), addPathResponse.class);
        int code = result.getCode();

        if (code == 400) {   // 버스, 지하철, 도보 추가 실패 or 헤더에 토큰값 없을 때
            Toast.makeText(AddPathActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show(); //오류메시지 출력
            Log.i("구체적 에러", result.getMessage());
        }

        Toast.makeText(AddPathActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show(); //오류메시지 출력
        Log.i("구체적 에러", result.getMessage());
    }


}