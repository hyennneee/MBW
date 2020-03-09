package com.example.mbw.AddPath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.mbw.AddPath.AddItem;
import com.example.mbw.DetailPathActivity;
import com.example.mbw.MainActivity;
import com.example.mbw.R;
import com.example.mbw.route.DetailItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// TODO : json 파일 보내기 (1. 위험 spot, 2. 사용자 제보 경로)
public class AddPathActivity extends AppCompatActivity {

    private String startPoint, endPoint;
    // TODO : 앞에서 start, end 에 대한 latitude, longitude 받아오기
    private double startLongi, startLati, endLongi, endLati;
    private ArrayList<AddItem> addItemList = new ArrayList<AddItem>();
    Intent intent = null;
    RecyclerView recyclerView;
    TextView startTv, endTv;

    boolean usingSub, usingBus = false;
    int pathType = 0;

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

            Double startX = resultIntent.getDoubleExtra("startX", 0);
            Double startY = resultIntent.getDoubleExtra("startY", 0);
            Double endX = resultIntent.getDoubleExtra("endX", 0);
            Double endY = resultIntent.getDoubleExtra("endY", 0);

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
        JSONObject obj = new JSONObject();
        try{
            obj.put("startAddress", startPoint);
            obj.put("startLongi", startLongi);
            obj.put("startLati", startLati);
            obj.put("endAddress", endPoint);
            obj.put("endLongi", endLongi);
            obj.put("endLati", endLati);

            // path array
            JSONObject pathObj = new JSONObject();
            // path Type : 1-지하철, 2-버스, 3-버스+지하철
            pathObj.put("pathType", pathType);

            JSONArray subPathArray = new JSONArray();
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
                    subPathObj.put("stations", nowItem.getPassingStationList());
                }
                else{ // 지하철일 때
                    subPathObj.put("trafficType", 1);
                    subPathObj.put("startID", nowItem.getStartId());
                    subPathObj.put("endID",nowItem.getEndId());
                }
                subPathArray.put(subPathObj);
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
        // 어느 화면으로 돌아갈지 정하기
    }

}