package com.example.mbw.showPath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mbw.R;
import com.example.mbw.route.Item;
import com.example.mbw.route.Route;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.JsonArray;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

class BusInfo{
    String arrmsg, busType;
    public BusInfo(String a, String b){
        arrmsg = a;
        busType = b;
    }
}

public class ShowPathActivity extends AppCompatActivity {
    TextView departure, destination;

    private FragmentAll fragmentAll;
    private FragmentBus fragmentBus;
    private FragmentSub fragmentSub;
    private FragmentBusNSub fragmentBusNSub;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private TextView all, bus, sub, busNsub;
    private View first, second, third, fourth;
    public JSONObject jsonObject, jsonObjectBus;

    private int searchType = 0, FLAG = 0, AUTOCOMPLETE_REQUEST_CODE = 1, totalWalk, cost, totalTime, GET_RESULT = 0;
    private double longitude[] = new double[2], latitude[] = new double[2];
    String busNum;
    private Intent searchIntent = null;
    Document doc;


    //variables to measure time
    long startTime = 0, endTime = 0;
    Vector<String> arsIdInfo = new Vector<>();
    Vector<String> busNumInfo = new Vector<>();
    Vector<BusInfo> busStationInfo = new Vector<>();
    int numOfBus, numOfCalled, pathArrayCount;

    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
    static ArrayList<Route> routeArrayList;

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

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        //이 부분 수정!!
        String[] strings;
        strings = getIntent().getStringArrayExtra("LOC_DATA");
        latitude[0] = Double.parseDouble(strings[0]);
        latitude[1] = Double.parseDouble(strings[1]);
        longitude[0] = Double.parseDouble(strings[2]);
        longitude[1] = Double.parseDouble(strings[3]);
        departure.setText(strings[4]);
        destination.setText(strings[5]);

        OdsayAPi(longitude[0], latitude[0], longitude[1], latitude[1], 0);
    }

    public void onClickShowPath(View v) {
        switch (v.getId()) {
            case R.id.star:
                //즐겨찾는 경로에 추가
                break;
            case R.id.swap:
                //swap 버튼 누르면 departure랑 destination 내용 바뀌게
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

                //길찾기 실행
                OdsayAPi(longitude[0], latitude[0], longitude[1], latitude[1], 0);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                double lt, lg;
                lt = place.getLatLng().latitude;
                lg = place.getLatLng().longitude;
                latitude[FLAG - 1] = lt;
                longitude[FLAG - 1] = lg;
                if (FLAG == 1) {
                    departure.setText(place.getName());
                } else if (FLAG == 2) {
                    destination.setText(place.getName());
                }
                //길찾기 실행
                OdsayAPi(longitude[0], latitude[0], longitude[1], latitude[1], 0);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                //Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public void showPathFragmentHandler(View v) {

        transaction = fragmentManager.beginTransaction();

        switch (v.getId()) {
            case R.id.showAll:
                searchType = 0;
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
                searchType = 2;
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
                searchType = 1;
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
                searchType = 3;
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

    public void onBackClick(View v) {
        finish();
    }

    public void OdsayAPi(Double startlng, Double startlat, Double endlng, Double endlat, int i) {
        searchType = i; // 이동방법: 0 모두(all, subNbus) 1 지하철(sub) 2 버스(bus)
        String type = new Integer(i).toString();
        ODsayService odsayService;
        odsayService = ODsayService.init(getApplicationContext(), getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);
        // 서버 통신
        odsayService.requestSearchPubTransPath(Double.toString(startlng), Double.toString(startlat), Double.toString(endlng), Double.toString(endlat), "1", type, "0", onResultCallbackListener);
    }

    public void OdsayAPi(int stationID) {
        ODsayService odsayService;
        odsayService = ODsayService.init(getApplicationContext(), getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);
// 서버 통신
        odsayService.requestBusStationInfo(Integer.toString(stationID), onResultCallbackListener);
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
            } else{//if(api == API.BUS_STATION_INFO) {
                jsonObjectBus = odsayData.getJson();
                getStationInfo(jsonObjectBus);
            }

        }

        // 호출 실패 시 실행
        @Override
        public void onError(int i, String errorMessage, API api) {
            if (api == API.BUS_STATION_INFO) {
                Log.i("SearchAPi", errorMessage);
            }
        }
    };

    // 이동방법 파싱
    private void transportation(JSONObject jsonObject) {   //여기서 mArrayList의 내용 채우기 - route 내용 채우기
         routeArrayList = new ArrayList<>();
         numOfBus = numOfCalled = 0;
        try {
            JSONObject result = jsonObject.getJSONObject("result");
            JSONArray pathArray = result.getJSONArray("path");

            // pathArray 안의 경로 개수
            pathArrayCount = pathArray.length();
            //a < pathArrayCount

            //doInBackground
            for (int a = 0; a < pathArrayCount; a++) { //경로 개수만큼
                JSONObject pathArrayDetailOBJ = pathArray.getJSONObject(a);
                totalWalk = 0;

                //한 경로에 대해 한 itemArrayList 가짐
                ArrayList<Item> itemArrayList = new ArrayList<Item>();

// 경로 타입 1 지하철 2 버스 3도보
                int pathType = pathArrayDetailOBJ.getInt("pathType");
                if (searchType ==  pathType || searchType == 0) {   //검색 타입과 일치하는 것만 출력
                    JSONObject infoOBJ = pathArrayDetailOBJ.getJSONObject("info");
                    cost = infoOBJ.getInt("payment"); // 요금
                    totalTime = infoOBJ.getInt("totalTime"); // 소요시간
                    String finalStation = infoOBJ.getString("lastEndStation"); // 도착 정거장
                    numOfBus += infoOBJ.getInt("busTransitCount");

                    // 세부경로 디테일
                    JSONArray subPathArray = pathArrayDetailOBJ.getJSONArray("subPath");
                    int subPathArraycount = subPathArray.length();
                    boolean is_first = true;
                    for (int b = 0; b < subPathArraycount; b++) {   //한 경로당

                        int subLine, busType, stationID;
                        String stationName, non_step1, remainingTime1 = "", arsID;

                        JSONObject subPathOBJ = subPathArray.getJSONObject(b);
                        int Type = subPathOBJ.getInt("trafficType"); // 이동방법
                        if (Type == 1 || Type == 2) {
                            stationName = subPathOBJ.getString("startName"); // 출발지
                            JSONArray laneObj = subPathOBJ.getJSONArray("lane");
                            if (Type == 1) { // 지하철
                                subLine = laneObj.getJSONObject(0).getInt("subwayCode"); // 지하철 정보(몇호선)
                                busNum = "";
                                arsID = "";
                                busType = -1;
                                non_step1 = "";
                                if (is_first) {   //첫 타자면
                                    //아직 지하철 남은 시간 정보는 안 가져왔음
                                    is_first = false;
                                }
                                else{
                                    remainingTime1 = "";
                                }
                            } else { // 버스
                                busNum = laneObj.getJSONObject(0).getString("busNo"); // 버스번호정보
                                busNumInfo.add(busNum);
                                stationID = subPathOBJ.getInt("startID"); // 버스정류소번호 -> 버스정류장 세부정보 조회에 사용
                                busType = laneObj.getJSONObject(0).getInt("type");
                                OdsayAPi(stationID);
                                arsID = "";
                                //!!!!!이거 해야돼!!!!!
                                //OdsayApi(stationID)에서 getStation하기 전에 밑에 코드 실행되는듯
                                non_step1 = "";
                                subLine = 0;
                                if (is_first) {   //첫 타자면
                                    remainingTime1 = "";
                                    is_first = false;
                                }
                                else{
                                    remainingTime1 = "";
                                }

                            }
                            //stationNo: layout에 띄워줄 정류장 번호, busID: 버스노선조회하면 나오는거(ex 500)

                            Item item = new Item(stationName, remainingTime1, busNum, arsID, subLine, busType, non_step1);
                            itemArrayList.add((item));
                        }
                        else{
                            totalWalk += subPathOBJ.getInt("sectionTime");
                        }

                    }
                    Item lastItem = new Item(finalStation, "", "", "", -1, 0, "");
                    itemArrayList.add(lastItem);
                }


                //Route: totalTime, walkingTime, cost
                Route route = new Route(totalTime, totalWalk, cost, itemArrayList);
                routeArrayList.add(route);
            }

            //onPostExcute


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getStationInfo(JSONObject jsonObjectBus) {
        //busStationInfo
        try {
            JSONObject result = jsonObjectBus.getJSONObject("result");
            String arsID = result.getString("arsID");
            //'-' 빼야돼
            String[] array = arsID.split("-");
            arsID = "";
            for(int i = 0; i < array.length; i++){
                arsID += array[i];
            }
            arsIdInfo.add(arsID);

            //남은 시간, 저상버스 여부
            String rss = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?ServiceKey=A5%2BhqLkSjuKIqcYXSgmPaQ8lZU%2FU4ygMfBqxJ7rQG%2Fs4j1TV1troG0srDXSfN99HJOqX6Mmqdw3zmEdZLfODXQ%3D%3D&arsId=" + arsID;  // RSS URL 구성
            GetXMLTask task = new GetXMLTask(this);
            task.execute(rss);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private class GetXMLTask extends AsyncTask<String, Void, Document> {
        private Activity context;

        public GetXMLTask(Activity context) {
            this.context = context;
        }

        @Override
        protected Document doInBackground(String... urls) {

            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db;

                db = dbf.newDocumentBuilder();
                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();

            } catch (Exception e) {

                Toast.makeText(getBaseContext(), "Parsing Error",
                        Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            numOfCalled++;

            NodeList nodeList = doc.getElementsByTagName("itemList");
            for(int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;

                NodeList rtNmList = fstElmnt.getElementsByTagName("rtNm");
                Element rtNmElement = (Element) rtNmList.item(0);
                rtNmList = rtNmElement.getChildNodes();
                String rtNm = ((Node) rtNmList.item(0)).getNodeValue();
                if(rtNm.equals(busNumInfo.get(numOfCalled - 1))){
                    NodeList busType1List = fstElmnt.getElementsByTagName("busType1");
                    Element busType1Element = (Element) busType1List.item(0);
                    busType1List = busType1Element.getChildNodes();
                    String busType1 = ((Node) busType1List.item(0)).getNodeValue();

                    NodeList busType2List = fstElmnt.getElementsByTagName("busType2");
                    Element busType2Element = (Element) busType2List.item(0);
                    busType2List = busType2Element.getChildNodes();
                    String busType2 = ((Node) busType2List.item(0)).getNodeValue();

                    NodeList arrmsg1List = fstElmnt.getElementsByTagName("arrmsg1");
                    Element arrmsg1Element = (Element) arrmsg1List.item(0);
                    arrmsg1List = arrmsg1Element.getChildNodes();
                    String arrmsg1 = ((Node) arrmsg1List.item(0)).getNodeValue();

                    NodeList arrmsg2List = fstElmnt.getElementsByTagName("arrmsg2");
                    Element arrmsg2Element = (Element) arrmsg2List.item(0);
                    arrmsg2List = arrmsg2Element.getChildNodes();
                    String arrmsg2 = ((Node) arrmsg2List.item(0)).getNodeValue();

                    busStationInfo.add(new BusInfo(arrmsg1, busType1));

                }
            }

            if(numOfBus == numOfCalled) {

                int index = 0;
                for (int i = 0; i < pathArrayCount; i++) {
                    int size = routeArrayList.get(i).getItemList().size();
                    ArrayList<Item> items = routeArrayList.get(i).getItemList();
                    for (int j = 0; j < size - 1; j++) {
                        Item item = items.get(j);
                        if (item.getSubLine() == 0) {
                            item.setArsID(arsIdInfo.get(index));
                            BusInfo busInfo = busStationInfo.get(index);
                            item.setRemainingTime(busInfo.arrmsg);
                            item.setNon_step(busInfo.busType);
                            index++;
                        }
                    }
                }
                //문제점: onPostExcute를 실행하지 않는 경우: 경로 목록에 버스가 없는 경우 아래 코드 수행 안 해
                getSupportFragmentManager().beginTransaction().detach(fragmentAll).attach(fragmentAll).commit();
                transaction.replace(R.id.showPathframe, fragmentAll);//.commitAllowingStateLoss();

                //departure.setText("ars: " + arsIdInfo.size() + ", arr: " + busStationInfo.size());
            }
        }

    }
}

