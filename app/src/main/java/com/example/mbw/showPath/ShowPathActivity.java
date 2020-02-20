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
import android.widget.CompoundButton;
import android.widget.Switch;
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
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

class ArrivalBusInfo{
    String arrmsg, busType;
    public ArrivalBusInfo(String a, String b){
        arrmsg = a;
        busType = b;
    }
}
class BusInfo{
    String busNum;
    int stationID;
    public BusInfo(String b, int s){
        busNum = b;
        stationID = s;
    }
}
class SubInfo{
    int wayCode, subLine;
    public SubInfo(int w, int s){
        wayCode = w;
        subLine = s;
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
    private Intent searchIntent = null;
    Document doc;


    //variables to measure time
    Vector<String> arsIdInfo;
    Vector<BusInfo> busInfo;
    Vector<ArrivalBusInfo> arrBusInfo;
    Vector<SubInfo> subInfo;
    Vector<String> subRemainingInfo;
    //Vector<String> busNum;
    int numOfBus, numOfBusCalled, numOfSub, numOfSubCalled, numOfCalled, pathArrayCount;
    boolean isFiltered = false, created = false;

    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
    static ArrayList<Route> routeArrayList;
    ArrayList<Route> mRouteArrayList;
    String[] stationArray = {"아차산(어린이대공원후문)", "안암(고대병원앞)", "올림픽공원(한국체대)", "월드컵경기장(성산)", "대흥(서강대앞)", "공릉(서울산업대입구)" ,"총신대입구(이수)", "숭실대입구(살피재)", "군자(능동)", "천호(풍납토성)", "굽은다리(강동구민회관앞)", "남한산성입구(성남법원, 검찰청)", "오목교(목동운동장앞)", "몽촌토성(평화의문)", "신촌(경의.중앙선)", "증산(명지대앞)", "월곡(동덕여대)", "어린이대공원(세종대)", "상도(중앙대앞)", "신정(은행정)", "광나루(장신대)", "천호(풍납토성)", "새절(신사)", "상월곡(한국과학기술연구원)", "화랑대(서울여대입구)", "응암순환(상선)", "군자(능동)", "쌍용(나사렛대)"
    };

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

        //서버에서 나만의 경로 list 받아오기
        /*for(int i = 0; i < size; i++){
            mRouteArrayList.add(server.get(i));
        }*/
        OdsayAPi(longitude[0], latitude[0], longitude[1], latitude[1]);
    }

    public void onClickShowPath(View v) {
        switch (v.getId()) {
            case R.id.star:
                //즐겨찾는 경로에 추가
                break;
            case R.id.swap:
                //swap 버튼 누르면 departure랑 destination 내용 바뀌게
                //fragmentAll로 초기화
                searchType = 0;
                String deptTmp = departure.getText().toString();
                String destTmp = destination.getText().toString();
                all.setTextColor(Color.parseColor("#1ABC9C"));
                bus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                sub.setTextColor(getResources().getColor(android.R.color.darker_gray));
                busNsub.setTextColor(getResources().getColor(android.R.color.darker_gray));

                first.setBackgroundColor(Color.parseColor("#1abc9c"));
                second.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                third.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                fourth.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                //transaction.replace(R.id.showPathframe, fragmentAll);//.commitAllowingStateLoss();
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
                OdsayAPi(longitude[0], latitude[0], longitude[1], latitude[1]);
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
                OdsayAPi(longitude[0], latitude[0], longitude[1], latitude[1]);

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

        //FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (v.getId()) {
            case R.id.showAll:
                searchType = 0;
                all.setTextColor(Color.parseColor("#1ABC9C"));
                bus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                sub.setTextColor(getResources().getColor(android.R.color.darker_gray));
                busNsub.setTextColor(getResources().getColor(android.R.color.darker_gray));

                first.setBackgroundColor(Color.parseColor("#1abc9c"));
                second.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                third.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                fourth.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                OdsayAPi(longitude[0], latitude[0], longitude[1], latitude[1]);
                break;
            case R.id.showBus:
                searchType = 2;

                all.setTextColor(getResources().getColor(android.R.color.darker_gray));
                bus.setTextColor(Color.parseColor("#1abc9c"));
                sub.setTextColor(getResources().getColor(android.R.color.darker_gray));
                busNsub.setTextColor(getResources().getColor(android.R.color.darker_gray));

                first.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                second.setBackgroundColor(Color.parseColor("#1abc9c"));
                third.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                fourth.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                OdsayAPi(longitude[0], latitude[0], longitude[1], latitude[1]);
                break;
            case R.id.showSub:
                searchType = 1;
                all.setTextColor(getResources().getColor(android.R.color.darker_gray));
                bus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                sub.setTextColor(Color.parseColor("#1abc9c"));
                busNsub.setTextColor(getResources().getColor(android.R.color.darker_gray));

                first.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                second.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                third.setBackgroundColor(Color.parseColor("#1abc9c"));
                fourth.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                OdsayAPi(longitude[0], latitude[0], longitude[1], latitude[1]);
                break;

            case R.id.showBusNSub:
                searchType = 3;
                all.setTextColor(getResources().getColor(android.R.color.darker_gray));
                bus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                sub.setTextColor(getResources().getColor(android.R.color.darker_gray));
                busNsub.setTextColor(Color.parseColor("#1abc9c"));

                first.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                second.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                third.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                fourth.setBackgroundColor(Color.parseColor("#1abc9c"));
                OdsayAPi(longitude[0], latitude[0], longitude[1], latitude[1]);
                break;
        }
    }

    public void onBackClick(View v) {
        finish();
    }

    public void OdsayAPi(Double startlng, Double startlat, Double endlng, Double endlat) {
        //searchType = i; // 이동방법: 0 모두(all, subNbus) 1 지하철(sub) 2 버스(bus)
        ODsayService odsayService;
        odsayService = ODsayService.init(getApplicationContext(), getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);
        // 서버 통신
        odsayService.requestSearchPubTransPath(Double.toString(startlng), Double.toString(startlat), Double.toString(endlng), Double.toString(endlat), "1", "0", Integer.toString(searchType), onResultCallbackListener);
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
        arsIdInfo = new Vector<>();
        busInfo = new Vector<>();
        arrBusInfo = new Vector<>();
        subInfo = new Vector<>();
        subRemainingInfo = new Vector<>();
         numOfBus = numOfBusCalled = numOfSub = numOfSubCalled = numOfCalled = 0;
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

// 경로 타입 1 지하철 2 버스 3 지하철 + 버스
                int pathType = pathArrayDetailOBJ.getInt("pathType");
                if (searchType == 0 || searchType ==  pathType) {   //검색 타입과 일치하는 것만 출력
                    JSONObject infoOBJ = pathArrayDetailOBJ.getJSONObject("info");
                    cost = infoOBJ.getInt("payment"); // 요금
                    totalTime = infoOBJ.getInt("totalTime"); // 소요시간
                    String finalStation = infoOBJ.getString("lastEndStation"); // 도착 정거장
                    //한 세부 경로에 대해서만 필요
                    int bus = infoOBJ.getInt("busTransitCount");
                    numOfBus += bus;
                    int sub = infoOBJ.getInt("subwayTransitCount");
                    int total = bus + sub;

                    // 세부경로 디테일
                    JSONArray subPathArray = pathArrayDetailOBJ.getJSONArray("subPath");
                    int subPathArraycount = subPathArray.length();
                    boolean is_first = true;
                    for (int b = 0; b < subPathArraycount; b++) {   //한 경로당

                        int subLine, busType, wayCode;
                        String stationName, non_step1, remainingTime1, arsID;

                        JSONObject subPathOBJ = subPathArray.getJSONObject(b);
                        int Type = subPathOBJ.getInt("trafficType"); // 이동방법
                        if (Type == 1 || Type == 2) {   //1: 지하철, 2: 버스, 3: 도보
                            Vector<String> busNum = new Vector<>();
                            stationName = subPathOBJ.getString("startName"); // 출발지
                            JSONArray laneObj = subPathOBJ.getJSONArray("lane");
                            remainingTime1 = non_step1 = arsID = "";
                            if (Type == 1) { // 지하철
                                wayCode = subPathOBJ.getInt("wayCode"); //상행, 하행(1, 2)
                                subLine = laneObj.getJSONObject(0).getInt("subwayCode"); // 지하철 정보(몇호선)
                                busNum.add("");
                                busType = -1;
                                if (is_first) {   //첫 타자면
                                    //첫 번째 지하철에 대해서만 xml호출할거임
                                    numOfSub++;
                                    subInfo.add(new SubInfo(wayCode, subLine));
                                    String subStation = stationName;
                                    for(int i = 0; i < stationArray.length; i++){
                                        if(stationArray[i].contains(stationName))
                                            subStation = stationArray[i];
                                    }
                                    String rss = "http://swopenapi.seoul.go.kr/api/subway/6c73727a4c70616e36336e6d707076/xml/realtimeStationArrival/1/5/" + subStation;  // RSS URL 구성
                                    GetSubXMLTask subXMLTask = new GetSubXMLTask(this);
                                    subXMLTask.execute(rss);
                                }
                                stationName += "역";
                            } else { // 버스
                                //arsID 필요하기 때문에 버스 전체 개수 필요
                                //numOfBus++;

                                //여러개일 수도 있음
                                for(int i = 0; i < laneObj.length(); i++) {
                                    busNum.add(laneObj.getJSONObject(i).getString("busNo")); // 버스번호정보
                                }
                                int stationID = subPathOBJ.getInt("startID"); // 버스정류소번호 -> 버스정류장 세부정보 조회에 사용
                                busInfo.add(new BusInfo(busNum.get(0), stationID));
                                busType = laneObj.getJSONObject(0).getInt("type");
                                //OdsayAPi(stationID);
                                //!!!!!이거 해야돼!!!!!
                                //OdsayApi(stationID)에서 getStation하기 전에 밑에 코드 실행되는듯
                                subLine = 0;
                                /*if (is_first) {   //첫 타자면
                                }*/
                            }
                            //stationNo: layout에 띄워줄 정류장 번호, busID: 버스노선조회하면 나오는거(ex 500)

                            Item item = new Item(stationName, remainingTime1, busNum, arsID, subLine, busType, non_step1, is_first);
                            itemArrayList.add((item));
                            is_first = false;
                        } else {    //도보
                            totalWalk += subPathOBJ.getInt("sectionTime");
                        }
                    }
                    int subLine = -1, busType = 0;
                    if (itemArrayList.get(total - 1).getSubLine() == 0){  //마지막 수단 == 버스
                        finalStation += "정류장";
                        busType = 1;
                    }
                    else{
                        finalStation += "역";
                        subLine = itemArrayList.get(total - 1).getSubLine();
                    }
                    Vector<String> tmpBusNum = new Vector<>();
                    tmpBusNum.add("");
                    Item lastItem = new Item(finalStation, "", tmpBusNum, "", subLine, busType, "", false);
                    itemArrayList.add(lastItem);
                    totalTime += totalWalk;
                    totalWalk *= 2; //도보 시간에 보통 사람들의 2배 가량 소요된다 가정
                    //Route: totalTime, walkingTime, cost
                    Route route = new Route(totalTime, totalWalk, cost, itemArrayList);
                    routeArrayList.add(route);
                }
            }
            for(int i = 0; i < busInfo.size(); i++){
                OdsayAPi(busInfo.get(i).stationID);
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
            String[] array = arsID.split("-");
            arsID = "";
            for(int i = 0; i < array.length; i++){
                arsID += array[i];
            }
            arsIdInfo.add(arsID);
            //남은 시간, 저상버스 여부 : 사실 이건 맨 처음 버스에 대해서만 해도 되는데
            String rss;
            if(isFiltered)
                rss = "http://ws.bus.go.kr/api/rest/stationinfo/getLowStationByUid?ServiceKey=A5%2BhqLkSjuKIqcYXSgmPaQ8lZU%2FU4ygMfBqxJ7rQG%2Fs4j1TV1troG0srDXSfN99HJOqX6Mmqdw3zmEdZLfODXQ%3D%3D&arsId=" + arsID;
            else
                rss = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?ServiceKey=A5%2BhqLkSjuKIqcYXSgmPaQ8lZU%2FU4ygMfBqxJ7rQG%2Fs4j1TV1troG0srDXSfN99HJOqX6Mmqdw3zmEdZLfODXQ%3D%3D&arsId=" + arsID;  // RSS URL 구성
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

            NodeList nodeList = doc.getElementsByTagName("itemList");
            boolean found = false;
            String busNum = busInfo.get(numOfBusCalled).busNum;
            for(int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;

                NodeList rtNmList = fstElmnt.getElementsByTagName("rtNm");
                Element rtNmElement = (Element) rtNmList.item(0);
                rtNmList = rtNmElement.getChildNodes();
                String rtNm = ((Node) rtNmList.item(0)).getNodeValue();
                if(rtNm.equals(busNum)){
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
                    String[] array = arrmsg1.split("\\[");
                    arrmsg1 = array[0];

                    arrBusInfo.add(new ArrivalBusInfo(arrmsg1, busType1));
                    //arrBusInfo.add(new ArrivalBusInfo(arrmsg1, busType1)); //모든 버스들에 대한 정보 들어있음
                    found = true;
                    break;
                }
            }
            numOfBusCalled++;
            numOfCalled++;

            if(!found){
                arrBusInfo.add(new ArrivalBusInfo("null", "-1"));
            }

            //버스 정보 다 불러옴 -> 버스 개수만큼 setData
            if(numOfBus == numOfBusCalled) {
                int index = 0;  //busStationInfo index 셈
                for (int i = 0; i < pathArrayCount; i++) {
                    ArrayList<Item> items = routeArrayList.get(i).getItemList();
                    for(int j = 0; j < items.size(); j++) {
                        Item item = items.get(j);
                        if (item.getSubLine() == 0) {   //버스
                            item.setArsID(arsIdInfo.get(index));
                            if(item.isFirst()) {    //첫타자인 경우만
                                ArrivalBusInfo arrivalBusInfo = arrBusInfo.get(index);
                                item.setRemainingTime(arrivalBusInfo.arrmsg);
                                item.setNon_step(arrivalBusInfo.busType);
                            }
                            index++;
                        }
                    }
                }

                //subway는 맨 위에 있는 경우만 xml 불러오니까 busCalled == bus면 마지막 버스까지 다 불러온거
                getSupportFragmentManager().beginTransaction().detach(fragmentAll).attach(fragmentAll).commit();
                transaction.replace(R.id.showPathframe, fragmentAll);
            }
        }

    }
    private class GetSubXMLTask extends AsyncTask<String, Void, Document> {
        private Activity context;

        public GetSubXMLTask(Activity context) {
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

            NodeList nodeList = doc.getElementsByTagName("row");

            String wayCode;
            int subLine = subInfo.get(numOfSubCalled).subLine;
            int tmpWayCode = subInfo.get(numOfSubCalled).wayCode;

            if(subLine == 2){
                if(tmpWayCode == 1)
                    wayCode = "내선";
                else
                    wayCode = "외선";
            }
            else{
                if(tmpWayCode == 1)
                    wayCode = "상행";
                else
                    wayCode = "하행";
            }

            boolean found = false;
            for(int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;

                NodeList subwayIdList = fstElmnt.getElementsByTagName("subwayId");
                Element subwayIdElement = (Element) subwayIdList.item(0);
                subwayIdList = subwayIdElement.getChildNodes();
                String tmpId = ((Node) subwayIdList.item(0)).getNodeValue();
                int subwayId = Integer.parseInt(tmpId) % 10;

                NodeList updnLineList = fstElmnt.getElementsByTagName("updnLine");
                Element updnLineElement = (Element) updnLineList.item(0);
                updnLineList = updnLineElement.getChildNodes();
                String updnLine = ((Node) updnLineList.item(0)).getNodeValue();

                if(subwayId == subLine && updnLine.equals(wayCode)){//arvlMsg3
                    NodeList arvlMsg2List = fstElmnt.getElementsByTagName("arvlMsg2");
                    Element arvlMsg2Element = (Element) arvlMsg2List.item(0);
                    arvlMsg2List = arvlMsg2Element.getChildNodes();
                    String arvlMsg2 = ((Node) arvlMsg2List.item(0)).getNodeValue();

                    NodeList arvlMsg3List = fstElmnt.getElementsByTagName("arvlMsg3");
                    Element arvlMsg3Element = (Element) arvlMsg3List.item(0);
                    arvlMsg3List = arvlMsg3Element.getChildNodes();
                    String arvlMsg3 = ((Node) arvlMsg3List.item(0)).getNodeValue();

                    found = true;
                    subRemainingInfo.add(arvlMsg2 + ", " + arvlMsg3);
                    break;
                }
            }

            if(!found){
                subRemainingInfo.add("정보를 찾지 못했습니다");
            }

            numOfSubCalled++;
            numOfCalled++;

            //자하철로만 이루어져 있을 때 어떻게 할거임
            if(numOfSub == numOfSubCalled) {
                int index = 0;  //전체 지하철 수
                for (int i = 0; i < pathArrayCount; i++) {  //전체 경로 개수
                    ArrayList<Item> items = routeArrayList.get(i).getItemList();
                    if(items.get(0).getBusType() == -1){
                        Item item = items.get(0);
                        item.setRemainingTime(subRemainingInfo.get(index++));
                    }
                }

                if(numOfBus == numOfBusCalled) {
                    getSupportFragmentManager().beginTransaction().detach(fragmentAll).attach(fragmentAll).commit();  //transaction = getSupportFragmentManager().beginTransaction()
                    transaction.replace(R.id.showPathframe, fragmentAll);//.commitAllowingStateLoss();
                }
            }
        }

    }
        public void onFilterChecked(View v) {
        boolean checked = ((Switch) v).isChecked();
            numOfBusCalled = 0;
            numOfCalled = numOfSubCalled;   //버스 정보만 다시 불러오면 되니까
            arrBusInfo = new Vector<>();
            String rss;
            if(checked){
                isFiltered = true;
                for(int i = 0; i < arsIdInfo.size(); i++){
                    String arsID = arsIdInfo.get(i);
                    rss = "http://ws.bus.go.kr/api/rest/stationinfo/getLowStationByUid?ServiceKey=A5%2BhqLkSjuKIqcYXSgmPaQ8lZU%2FU4ygMfBqxJ7rQG%2Fs4j1TV1troG0srDXSfN99HJOqX6Mmqdw3zmEdZLfODXQ%3D%3D&arsId=" + arsID;

                    GetXMLTask task = new GetXMLTask(this);
                    task.execute(rss);

                }

            }
            else{
                isFiltered = false;
                for(int i = 0; i < arsIdInfo.size(); i++){
                    String arsID = arsIdInfo.get(i);
                    rss = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?ServiceKey=A5%2BhqLkSjuKIqcYXSgmPaQ8lZU%2FU4ygMfBqxJ7rQG%2Fs4j1TV1troG0srDXSfN99HJOqX6Mmqdw3zmEdZLfODXQ%3D%3D&arsId=" + arsID;

                    GetXMLTask task = new GetXMLTask(this);
                    task.execute(rss);

                }
            }
        }

}

