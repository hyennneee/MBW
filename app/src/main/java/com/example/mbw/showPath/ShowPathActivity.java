package com.example.mbw.showPath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mbw.MainActivity;
import com.example.mbw.R;
import com.example.mbw.network.RetrofitClient;
import com.example.mbw.network.ServiceApi;
import com.example.mbw.pathData.PathResponse;
import com.example.mbw.pathData.Position;
import com.example.mbw.route.Item;
import com.example.mbw.route.Route;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private static final String TAG = "ShowPathActivity";
    TextView departure, destination;

    private FragmentAll fragmentAll;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private TextView all, bus, sub, busNsub;
    private View first, second, third, fourth;

    private int searchType = 0, FLAG = 0, AUTOCOMPLETE_REQUEST_CODE = 1;//, totalWalk, cost, totalTime;
    private Intent searchIntent = null;
    Document doc;


    //variables to measure time
    Vector<String> arsIdInfo;
    Vector<BusInfo> busInfo;
    Vector<ArrivalBusInfo> arrBusInfo;
    Vector<SubInfo> subInfo;
    Vector<String> subRemainingInfo;
    public static Vector<String> routeHistory;
    int numOfBus, numOfBusCalled, numOfSub, numOfSubCalled, numOfCalled, pathArrayCount, posSize;
    boolean isFiltered = false, changeRoute = false;
    ServiceApi service;
    Position lastPos;
    static JsonArray pathArray;
    //public static JsonArray pathArray;

    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
    static ArrayList<Route> routeArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_path);
        departure = findViewById(R.id.departureText);
        destination = findViewById(R.id.destinationText);

        fragmentAll = new FragmentAll();

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
        departure.setText(strings[0]);
        destination.setText(strings[1]);
        routeHistory = new Vector<>();
        routeHistory.add(strings[0]);
        routeHistory.add(strings[1]);
        service = RetrofitClient.getClient().create(ServiceApi.class);
        getLastPosition();
        /*departure.setText("" + lastPos.getSX() + ", " + lastPos.getSY());
        destination.setText("" + lastPos.getEX() + ", " + lastPos.getEY());*/
        startSearchPath(lastPos);
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
                routeHistory.add(destTmp);
                routeHistory.add(deptTmp);

                //위도, 경도 바뀌게
                double SX, SY, EX, EY;
                getLastPosition();
                Position pos;
                SX = lastPos.getEX();
                SY = lastPos.getEY();
                EX = lastPos.getSX();
                EY = lastPos.getSY();
                pos = new Position(SX, SY, EX, EY);
                MainActivity.positions.add(pos);

                //길찾기 실행
                startSearchPath(pos);
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

    //google maps에 위치 검색
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Position pos = new Position();
                double x, y;
                x = place.getLatLng().longitude;
                y = place.getLatLng().latitude;
                getLastPosition();
                //
                if (FLAG == 1) {    //출발지 검색
                    pos.setSX(x);
                    pos.setSY(y);
                    pos.setEX(lastPos.getEX());
                    pos.setEY(lastPos.getEY());
                    MainActivity.positions.add(pos);
                    departure.setText(place.getName());
                } else if (FLAG == 2) {
                    pos.setSX(lastPos.getSX());
                    pos.setSY(lastPos.getSY());
                    pos.setEX(x);
                    pos.setEY(y);
                    MainActivity.positions.add(pos);
                    destination.setText(place.getName());
                }
                //길찾기 실행
                routeHistory.add(departure.getText().toString());
                routeHistory.add(destination.getText().toString());

                MainActivity.positions.add(pos);
                startSearchPath(pos);

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
        getLastPosition();

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
                lastPos.setType(searchType);
                startSearchPath(lastPos);
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
                lastPos.setType(searchType);
                startSearchPath(lastPos);
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
                lastPos.setType(searchType);
                startSearchPath(lastPos);
                break;
        }
    }

    public void onBackClick(View v) {
        finish();
    }

    private void startSearchPath(Position data) {
        double sx, sy, ex, ey;
        int type;
        sx = data.getSX();
        sy = data.getSY();
        ex = data.getEX();
        ey = data.getEY();
        type = data.getType();
        Log.i(TAG, "sx: " + sx + ", sy: " + sy + ", ex: " + ex + ", ey: " + ey + ", type: " + type);
        //departure.setText("" + sx + ", " + sy);
        //destination.setText("" + ex + ", " + ey + ", " + type);
        service.searchPath(sx, sy, ex, ey, type).enqueue(new Callback<PathResponse>() {
            @Override
            public void onResponse(Call<PathResponse> call, Response<PathResponse> response) {
                PathResponse result = response.body();
                if (response.isSuccessful()) {
                    JsonElement pathResult = result.getData();
                    //transportation을 sub thread로 빼기
                    TransportAsyncTask transport = new TransportAsyncTask(pathResult);
                    transport.execute();
                    //transportation(pathResult);
                } else {
                    try {
                        Gson gson = new Gson();
                        result = gson.fromJson(response.errorBody().string(), PathResponse.class);
                        Toast.makeText(ShowPathActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PathResponse> call, Throwable t) {
                Toast.makeText(ShowPathActivity.this, "오류 발생", Toast.LENGTH_SHORT).show();
                Log.e("오류 발생", t.getMessage());
            }
        });
    }



    /*public void OdsayAPi(Double startlng, Double startlat, Double endlng, Double endlat) {
        //searchType = i; // 이동방법: 0 모두(all, subNbus) 1 지하철(sub) 2 버스(bus)
        ODsayService odsayService;
        odsayService = ODsayService.init(getApplicationContext(), getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);
        // 서버 통신
        odsayService.requestSearchPubTransPath(Double.toString(startlng), Double.toString(startlat), Double.toString(endlng), Double.toString(endlat), "1", "0", Integer.toString(searchType), onResultCallbackListener);
    }*/


    public void transportation(JsonElement jsonElement){
    }
    // 이동방법 파싱
    public class TransportAsyncTask extends AsyncTask<Void, Void, Boolean>{
        JsonElement jsonElement;
        ImageView progressBar = findViewById(R.id.loading_image);
        public TransportAsyncTask(JsonElement jsonElement) {
            this.jsonElement = jsonElement;
        }
        @Override
        protected void onPreExecute() { //progress bar 실행
            super.onPreExecute();
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
            progressBar.setAnimation(animation);
            progressBar.startAnimation(animation);
        }

        @Override
        protected void onPostExecute(Boolean s) {   //progress bar 종료, fragment all에 정보 띄우기
            super.onPostExecute(s);

            //do In Background에서 실행시킨 다른 thread들이 모두 종료된 후에 아래 코드가 실행되는지는 잘 모르겠음
            transaction.replace(R.id.showPathframe, fragmentAll);
            progressBar.clearAnimation();
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {   //transport function
            //transport
            routeArrayList = new ArrayList<>();
            arsIdInfo = new Vector<>();
            busInfo = new Vector<>();
            arrBusInfo = new Vector<>();
            subInfo = new Vector<>();
            subRemainingInfo = new Vector<>();
            numOfBus = numOfBusCalled = numOfSub = numOfSubCalled = numOfCalled = 0;
            try {
                JsonObject result = jsonElement.getAsJsonObject();
                pathArray = result.get("path").getAsJsonArray();

                // pathArray 안의 경로 개수
                pathArrayCount = pathArray.size();
                //doInBackground
                for (int a = 0; a < pathArrayCount; a++) { //경로 개수만큼
                    int totalWalk, cost, totalTime, group;
                    JsonObject pathArrayDetailOBJ = pathArray.get(a).getAsJsonObject();
                    totalWalk = 0;

                    //한 경로에 대해 한 itemArrayList 가짐
                    ArrayList<Item> itemArrayList = new ArrayList<Item>();

// 경로 타입 1 지하철 2 버스 3 지하철 + 버스
                    int pathType = pathArrayDetailOBJ.get("pathType").getAsInt();
                    if (searchType == 0 || searchType ==  pathType) {   //검색 타입과 일치하는 것만 출력
                        JsonObject infoOBJ = pathArrayDetailOBJ;
                        group = infoOBJ.get("group").getAsInt();
                        if(group == 1)
                            cost = infoOBJ.get("totalPay").getAsInt(); // 요금
                        else{
                            cost = 0;
                        }
                        totalTime = infoOBJ.get("totalTime").getAsInt(); // 소요시간
                        String finalStation = infoOBJ.get("lastEndStation").getAsString(); // 도착 정거장
                        //한 세부 경로에 대해서만 필요
                        int bus = infoOBJ.get("busTransitCount").getAsInt();
                        numOfBus += bus;
                        int sub = infoOBJ.get("subwayTransitCount").getAsInt();
                        int total = bus + sub;

                        // 세부경로 디테일
                        JsonArray subPathArray = pathArrayDetailOBJ.get("subPath").getAsJsonArray();
                        int subPathArraycount = subPathArray.size();
                        boolean is_first = true;
                        for (int b = 0; b < subPathArraycount; b++) {   //한 경로당

                            int subLine, busType, wayCode;
                            String stationName, non_step1, remainingTime1, arsID;

                            JsonObject subPathOBJ = subPathArray.get(b).getAsJsonObject();
                            int Type = subPathOBJ.get("trafficType").getAsInt(); // 이동방법
                            if (Type == 1 || Type == 2) {   //1: 지하철, 2: 버스, 3: 도보
                                Vector<String> busNum = new Vector<>();
                                stationName = subPathOBJ.get("startName").getAsString(); // 출발지
                                JsonArray laneObj = subPathOBJ.get("lane").getAsJsonArray();
                                remainingTime1 = non_step1 = arsID = "";
                                if (Type == 1) { // 지하철
                                    wayCode = subPathOBJ.get("wayCode").getAsInt(); //상행, 하행(1, 2)
                                    subLine = laneObj.get(0).getAsJsonObject().get("subwayCode").getAsInt(); // 지하철 정보(몇호선)
                                    busNum.add("");
                                    busType = -1;
                                    if (is_first) {   //첫 타자면
                                        //첫 번째 지하철에 대해서만 xml호출할거임
                                        numOfSub++;
                                        subInfo.add(new SubInfo(wayCode, subLine));
                                        String subStation = stationName;
                                        String[] stationArray = {"아차산(어린이대공원후문)", "안암(고대병원앞)", "올림픽공원(한국체대)", "월드컵경기장(성산)", "대흥(서강대앞)", "공릉(서울산업대입구)" ,"총신대입구(이수)", "숭실대입구(살피재)", "군자(능동)", "천호(풍납토성)", "굽은다리(강동구민회관앞)", "남한산성입구(성남법원, 검찰청)", "오목교(목동운동장앞)", "몽촌토성(평화의문)", "신촌(경의.중앙선)", "증산(명지대앞)", "월곡(동덕여대)", "어린이대공원(세종대)", "상도(중앙대앞)", "신정(은행정)", "광나루(장신대)", "천호(풍납토성)", "새절(신사)", "상월곡(한국과학기술연구원)", "화랑대(서울여대입구)", "응암순환(상선)", "군자(능동)", "쌍용(나사렛대)"
                                        };
                                        for(int i = 0; i < stationArray.length; i++){
                                            if(stationArray[i].contains(stationName))
                                                subStation = stationArray[i];
                                        }
                                        executeSubXML(subStation);
                                    }
                                    if(!stationName.endsWith("역"))
                                        stationName += "역";
                                } else { // 버스
                                    //arsID 필요하기 때문에 버스 전체 개수 필요
                                    //numOfBus++;

                                    //여러개일 수도 있음
                                    for(int i = 0; i < laneObj.size(); i++) {
                                        busNum.add(laneObj.get(i).getAsJsonObject().get("busNo").getAsString()); // 버스번호정보
                                    }
                                    int stationID = subPathOBJ.get("startID").getAsInt(); // 버스정류소번호 -> 버스정류장 세부정보 조회에 사용
                                    busInfo.add(new BusInfo(busNum.get(0), stationID));
                                    busType = laneObj.get(0).getAsJsonObject().get("type").getAsInt();
                                    //OdsayAPi(stationID);
                                    //OdsayApi(stationID)에서 getStation하기 전에 밑에 코드 실행되는듯
                                    subLine = 0;
                                    arsID = subPathOBJ.get("busArsID").getAsString();
                                    getStationInfo(arsID);
                                }
                                //stationNo: layout에 띄워줄 정류장 번호, busID: 버스노선조회하면 나오는거(ex 500)

                                Item item = new Item(stationName, remainingTime1, busNum, arsID, subLine, busType, non_step1, is_first);
                                itemArrayList.add((item));
                                is_first = false;
                            } else {    //도보
                                totalWalk += subPathOBJ.get("sectionTime").getAsInt();
                            }
                        }
                        int subLine = -1, busType = 0;
                        if (itemArrayList.get(total - 1).getSubLine() == 0){  //마지막 수단 == 버스
                            finalStation += "정류장";
                            busType = 1;
                        }
                        else{
                            if(!finalStation.endsWith("역"))
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
                        Route route = new Route(totalTime, totalWalk, cost, group, itemArrayList);
                        routeArrayList.add(route);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private void executeSubXML(String subStation){
        String rss = "http://swopenapi.seoul.go.kr/api/subway/6c73727a4c70616e36336e6d707076/xml/realtimeStationArrival/1/5/" + subStation;  // RSS URL 구성
        GetSubXMLTask subXMLTask = new GetSubXMLTask(ShowPathActivity.this);
        subXMLTask.execute(rss);
    }

    private void getStationInfo(String arsID) {
        //busStationInfo
            arsIdInfo.add(arsID);
            //남은 시간, 저상버스 여부 : 사실 이건 맨 처음 버스에 대해서만 해도 되는데
            String rss;
            if (isFiltered)
                rss = "http://ws.bus.go.kr/api/rest/stationinfo/getLowStationByUid?ServiceKey=A5%2BhqLkSjuKIqcYXSgmPaQ8lZU%2FU4ygMfBqxJ7rQG%2Fs4j1TV1troG0srDXSfN99HJOqX6Mmqdw3zmEdZLfODXQ%3D%3D&arsId=" + arsID;
            else
                rss = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?ServiceKey=A5%2BhqLkSjuKIqcYXSgmPaQ8lZU%2FU4ygMfBqxJ7rQG%2Fs4j1TV1troG0srDXSfN99HJOqX6Mmqdw3zmEdZLfODXQ%3D%3D&arsId=" + arsID;  // RSS URL 구성
            GetXMLTask task = new GetXMLTask(this);
            task.execute(rss);

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
                arrBusInfo.add(new ArrivalBusInfo("저상버스 정보가 없습니다", "-1"));
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
                                String arrmsg = arrivalBusInfo.arrmsg;
                                String str[] = arrmsg.split("분");
                                try {   //남은 시간이 숫자일 경우
                                    if (Integer.parseInt(str[0]) >= 5) //10분 이상
                                        item.setRemainingTime(str[0] + "분");
                                    else
                                        item.setRemainingTime(arrmsg);
                                }
                                catch(NumberFormatException e) {    //문자일 경우
                                    item.setRemainingTime(arrmsg);
                                }
                                item.setNon_step(arrivalBusInfo.busType);
                            }
                            index++;
                        }
                    }
                }

                //subway는 맨 위에 있는 경우만 xml 불러오니까 busCalled == bus면 마지막 버스까지 다 불러온거
                //원래 여기서 fragment 전환
                getSupportFragmentManager().beginTransaction().detach(fragmentAll).attach(fragmentAll).commit();
                //transaction.replace(R.id.showPathframe, fragmentAll);
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
                    //transaction.replace(R.id.showPathframe, fragmentAll);//.commitAllowingStateLoss();
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
     public void getLastPosition(){
        posSize = MainActivity.positions.size();
        lastPos = MainActivity.positions.get(posSize - 1);
     }
}

