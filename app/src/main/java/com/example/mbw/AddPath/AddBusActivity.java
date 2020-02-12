package com.example.mbw.AddPath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.mbw.R;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddBusActivity extends AppCompatActivity {

    private String busNum, busId;
    public JSONObject jsonObject1, jsonObject2;
    private EditText editText;
    ArrayList<AddBusItem> busItemList = new ArrayList<AddBusItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);
        editText = (EditText) findViewById(R.id.editText);

        // EditText 빈칸이면 보이던 경로 없애기 위해
        editText.addTextChangedListener(new TextWatcher(){
            @Override
            public void afterTextChanged(Editable edit) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editText.getText().toString().length() == 0){
                    busItemList.clear();
                }

            }
        });

        RecyclerView recyclerView = findViewById(R.id.addItem_rv) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        AddBusAdapter adapter = new AddBusAdapter(busItemList) ;
        recyclerView.setAdapter(adapter) ;

    }


    // 사용자는 버스 번호 입력 -> 버스 번호를 버스 아이디로 바꾸기
    // 버스 아이디로 버스 노선 경로 검색
    // 버스 정류장 옆에 승차, 하차 클릭

    // 사용자 입력 가지고 버스 아이디 알아내기
    public void search(View view){

        //busNum = "400"; // 이걸로 오디세이 콜
        busNum = editText.getText().toString();
        Log.i("search - bus num : %s", busNum);
        ODSayApi(busNum);
        //Log.i("search - bus ID : %s", busId);
        //busId = "703";

        //busId = getBusId();

        //ODSayApi2(busId);
    }
/*
    public String getBusId(){
        return busId;
    }


 */
    public void ODSayApi(String busNum){
        ODsayService odsayService;
        odsayService = ODsayService.init(getApplicationContext(), getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);
        odsayService.requestSearchBusLane(busNum, "1000", null, null, null, onResultCallbackListener);
    }

    public void ODSayApi2(String busId){
        ODsayService odsayService;
        odsayService = ODsayService.init(getApplicationContext(), getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);
        odsayService.requestBusLaneDetail(busId, onResultCallbackListener);
    }

    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        // 호출 성공시 데이터 들어옴
        @Override
        public void onSuccess(ODsayData odsayData, API api) {
            // API Value 는 API 호출 메소드 명을 따라갑니다.
            if (api == API.SEARCH_BUS_LANE) {
                //이 코드가 없어서 null pointer exception error
                jsonObject1 = odsayData.getJson();
                findBusId(jsonObject1);
                Log.i("bus num : %s", busNum);
            } else if (api == API.BUS_LANE_DETAIL) {
                jsonObject2 = odsayData.getJson();
                findBusRoute(jsonObject2);
            }
        }
        // 호출 실패 시 실행
        @Override
        public void onError(int i, String errorMessage, API api) {
            if (api == API.BUS_STATION_INFO) {
                Log.i("Add BusApi Error", errorMessage);
            }
        }
    };

    // 버스 번호 -> 버스 아이디
    private void findBusId(JSONObject jsonObject1){
        try{
            // 서울 안에서 같은 번호 버스 존재 X
            JSONObject result = jsonObject1.getJSONObject("result");
            JSONArray laneArray = result.getJSONArray("lane");

            JSONObject laneArray0 = laneArray.getJSONObject(0);
            busId = laneArray0.getString("busID"); // 도착 정거장

            ODSayApi2(busId);

            Log.i("busId", busId);

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    // 버스 아이디 -> 버스 경로 리스트
    private void findBusRoute(JSONObject jsonObject2){
        try{
            busItemList.clear();
            JSONObject result = jsonObject2.getJSONObject("result");
            JSONArray stationArray = result.getJSONArray("station");

            int stationCnt = stationArray.length();

            for (int i=0; i<stationCnt; i++){
                JSONObject stationArrayDetail = stationArray.getJSONObject(i);

                String stationName = stationArrayDetail.getString("stationName");
                Log.i("stationName", stationName);

                String arsId = stationArrayDetail.getString("arsID");
                Log.i("arsID", arsId);

                AddBusItem item = new AddBusItem(stationName, arsId, false, false);
                busItemList.add(item);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}
