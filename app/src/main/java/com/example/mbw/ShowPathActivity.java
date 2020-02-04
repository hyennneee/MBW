package com.example.mbw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mbw.showPathFragment.FragmentAll;
import com.example.mbw.showPathFragment.FragmentBus;
import com.example.mbw.showPathFragment.FragmentBusNSub;
import com.example.mbw.showPathFragment.FragmentSub;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


public class ShowPathActivity extends AppCompatActivity {
    TextView departure, destination;

    private FragmentAll fragmentAll;
    private FragmentBus fragmentBus;
    private FragmentSub fragmentSub;
    private FragmentBusNSub fragmentBusNSub;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private TextView all, bus, sub, busNsub, exTV;
    private View first, second, third, fourth;
    JSONObject jsonObject;

    private int searchType = 0, FLAG = 0, AUTOCOMPLETE_REQUEST_CODE = 1;;
    private double longitude[] = new double[2], latitude[] = new double[2];
    private Intent searchIntent = null;

    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_path);
        departure = findViewById(R.id.departureText);
        destination = findViewById(R.id.destinationText);

        fragmentAll = new FragmentAll();
        fragmentBus = new FragmentBus();
        fragmentSub = new FragmentSub();
        fragmentBusNSub = new FragmentBusNSub();

        all = findViewById(R.id.showAll);
        bus = findViewById(R.id.showBus);
        sub = findViewById(R.id.showSub);
        busNsub = findViewById(R.id.showBusNSub);

        first = findViewById(R.id.allLine);
        second = findViewById(R.id.busLine);
        third = findViewById(R.id.subLine);
        fourth = findViewById(R.id.busNSubLine);

        exTV = findViewById(R.id.fragA);

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.showPathframe, fragmentAll).commitAllowingStateLoss();
        String []strings;
        strings = getIntent().getStringArrayExtra("LOC_DATA");
        latitude[0] = Double.parseDouble(strings[0]);
        latitude[1] = Double.parseDouble(strings[1]);
        longitude[0] = Double.parseDouble(strings[2]);
        longitude[1] = Double.parseDouble(strings[3]);
        departure.setText(strings[4]);
        destination.setText(strings[5]);

        OdsayAPi(longitude[0], latitude[0], longitude[1], latitude[1], 0);
        Toast.makeText(ShowPathActivity.this,"" + longitude[0] + latitude[0] + longitude[1] + latitude[1], Toast.LENGTH_SHORT).show();
    }

    public void onClickShowPath(View v){
        switch (v.getId()){
            case R.id.star:
                //즐겨찾는 경로에 추가
                break;
            case R.id.swap:
                //swap 버튼 누르면 departure랑 destination "내용" 바뀌게
                String deptTmp = departure.getText().toString();
                String destTmp = destination.getText().toString();
                departure.setText(destTmp);
                destination.setText(deptTmp);

                //위도, 경도 바뀌게
                double lt, lg;
                lt = latitude[0];
                lg = longitude[0];
                latitude[0] = latitude[1];
                longitude[0] = longitude[1];
                latitude[1] = lt;
                longitude[1] = lg;
                break;
            case R.id.departureText:
                FLAG = 1;
                searchIntent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .build(this);
                startActivityForResult(searchIntent, AUTOCOMPLETE_REQUEST_CODE);
                break;
            case R.id.destinationText:
                FLAG = 2;
                searchIntent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, fields)
                        .build(this);
                startActivityForResult(searchIntent, AUTOCOMPLETE_REQUEST_CODE);
                break;
        }
    }

    public void showPathFragmentHandler(View v) {

        transaction = fragmentManager.beginTransaction();

        switch (v.getId()) {
            case R.id.showAll:
                transaction.replace(R.id.showPathframe, fragmentAll).commitAllowingStateLoss();
                all.setTextColor(Color.parseColor("#1ABC9C"));
                bus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                sub.setTextColor(getResources().getColor(android.R.color.darker_gray));
                busNsub.setTextColor(getResources().getColor(android.R.color.darker_gray));

                first.setBackgroundColor(Color.parseColor("#1abc9c"));
                second.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                third.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                fourth.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                break;
            case R.id.showBus:
                transaction.replace(R.id.showPathframe, fragmentBus).commitAllowingStateLoss();

                all.setTextColor(getResources().getColor(android.R.color.darker_gray));
                bus.setTextColor(Color.parseColor("#1abc9c"));
                sub.setTextColor(getResources().getColor(android.R.color.darker_gray));
                busNsub.setTextColor(getResources().getColor(android.R.color.darker_gray));

                first.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                second.setBackgroundColor(Color.parseColor("#1abc9c"));
                third.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                fourth.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                break;
            case R.id.showSub:
                transaction.replace(R.id.showPathframe, fragmentSub).commitAllowingStateLoss();
                all.setTextColor(getResources().getColor(android.R.color.darker_gray));
                bus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                sub.setTextColor(Color.parseColor("#1abc9c"));
                busNsub.setTextColor(getResources().getColor(android.R.color.darker_gray));

                first.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                second.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                third.setBackgroundColor(Color.parseColor("#1abc9c"));
                fourth.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                break;

            case R.id.showBusNSub:
                transaction.replace(R.id.showPathframe, fragmentBusNSub).commitAllowingStateLoss();
                all.setTextColor(getResources().getColor(android.R.color.darker_gray));
                bus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                sub.setTextColor(getResources().getColor(android.R.color.darker_gray));
                busNsub.setTextColor(Color.parseColor("#1abc9c"));

                first.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                second.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                third.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                fourth.setBackgroundColor(Color.parseColor("#1abc9c"));
                break;
        }
    }

    public void onBackClick(View v){
        finish();
    }

    // 이동방법 검색
    private void OdsayAPi(Double startlng, Double startlat, Double endlng, Double endlat, int i) {
        searchType = i; // 이동방법 1 지하철/ /
        ODsayService odsayService;
        odsayService = ODsayService.init(getApplicationContext(), getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);
// 서버 통신
        odsayService.requestSearchPubTransPath(Double.toString(startlng), Double.toString(startlat), Double.toString(endlng),Double.toString(endlat), "1", "0", "0", onResultCallbackListener);
    }

    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        // 호출 성공시 데이터 들어옴
        @Override
        public void onSuccess(ODsayData odsayData, API api) {

                // API Value 는 API 호출 메소드 명을 따라갑니다.
                if (api == API.SEARCH_PUB_TRANS_PATH) {
                    //이 코드가 없어서 null pointer exception error
                    jsonObject = odsayData.getJson();

                    transportation(jsonObject);
                    //Log.d("Station name : %s", stationName);
                }

        }
        // 호출 실패 시 실행
        @Override
        public void onError(int i, String errorMessage, API api) {
            if (api == API.BUS_STATION_INFO) {
                Toast.makeText(ShowPathActivity.this,errorMessage, Toast.LENGTH_SHORT).show();
                Log.i("SearchAPi",errorMessage);}
        }
    };

    // 이동방법 파싱
    private void transportation(JSONObject jsonObject) {
        try{
            JSONObject result = jsonObject.getJSONObject("result");
            JSONArray pathArray = result.getJSONArray("path");
// pathArray 안의 경로 갯수
            int pathArrayCount = pathArray.length();

            /*for(int a = 0; a<pathArrayCount; a++) {
                JSONObject pathArrayDetailOBJ = pathArray.getJSONObject(a);
// 경로 타입 1 지하철 2 버스 3도보
                int pathType = pathArrayDetailOBJ.getInt("pathType");
                if( pathType == searchType){
                    JSONObject infoOBJ = pathArrayDetailOBJ.getJSONObject("info");
                    int totalWalk = infoOBJ.getInt("totalWalk"); // 총 도보 이동거리
                    int payment = infoOBJ.getInt("payment"); // 요금
                    int totalTime = infoOBJ.getInt("totalTime"); // 소요시간
                    String mapObj = infoOBJ.getString("mapObj"); // 경로 디테일 조회 아이디
                    String firstStartStation = infoOBJ.getString("firstStartStation"); // 출발 정거장
                    String lastEndStation = infoOBJ.getString("lastEndStation"); // 도착 정거장

// 세부경로 디테일
                    JSONArray subPathArray = pathArrayDetailOBJ.getJSONArray("subPath");
                    int subPathArraycount = subPathArray.length();
// 반환 데이터 스트링으로

                    int busID=0;
                    for(int b = 0; b<subPathArraycount; b++){
                        JSONObject subPathOBJ = subPathArray.getJSONObject(b);
                        int Type = subPathOBJ.getInt("trafficType"); // 이동방법
                        switch (Type){
                            case 1:
                                routedetail += "지하철 ";
                                break;
                            case 2:
                                routedetail += "버스 ";
                                break;
                            default:
                                routedetail += "도보 ";
                                break;
                        }
// 버스 또는 지하철 이동시에만
                        if(Type == 1 || Type ==2){
                            String startName = subPathOBJ.getString("startName"); // 출발지
                            routedetail += startName+" 에서 ";
                            String endName = subPathOBJ.getString("endName"); // 도착지
                            routedetail += endName;
// 버스및 지하철 정보 가져옴 (정보가 많으므로 array로 가져오기)
                            JSONArray laneObj = subPathOBJ.getJSONArray("lane");
                            if(Type == 1 ){ // 지하철
                                String subwayName = laneObj.getJSONObject(0).getString("name"); // 지하철 정보(몇호선)
                                routedetail += subwayName+" 탑승 ";
                            }
                            if(Type == 2 ) { // 버스..
                                String busNo = laneObj.getJSONObject(0).getString("busNo"); // 버스번호정보
                                String busroute = " ["+busNo+ "] 번 탑승 ";
                                routedetail += busroute;
                                busID = laneObj.getJSONObject(0).getInt("busID"); // 버스정류장 id
                            }
                        }
                        int distance = subPathOBJ.getInt("distance"); // 이동길이
                        routedetail += "\n( "+Integer.toString(distance)+"m 이동. ";
                        int sectionTime = subPathOBJ.getInt("sectionTime"); // 이동시간
                        routedetail += Integer.toString(sectionTime)+"분 소요 )\n";
                        totalTime += sectionTime ;
////////////////////////////////////////////////////////////addlist 넣기!!! 한줄마다 listview설정하기
                    } // 세부경로 종료
                    routedetail += "총" + Integer.toString(totalTime) + "분 소요\n " ;
// api 경로 좌표 요청
                    OdsayAPiroute(mapObj);
// 화면에 버스 및 지하철 경로 출력
                    Dialogview();
                    break;
                }
            }*/
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
