package com.example.mbw.showPath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mbw.DB.DBHelper;
import com.example.mbw.DB.RouteDB;
import com.example.mbw.DB.RouteDBHelper;
import com.example.mbw.AddPath.AddPathActivity;
import com.example.mbw.DB.SpeedDBHelper;
import com.example.mbw.MainActivity;
import com.example.mbw.MyPage.data.PostResponse;
import com.example.mbw.R;
import com.example.mbw.network.RetrofitClient;
import com.example.mbw.network.ServiceApi;
import com.example.mbw.pathData.PathResponse;
import com.example.mbw.route.Item;
import com.example.mbw.route.Route;
import com.example.mbw.route.RouteAdapter;
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

class BusInfo{
    int a, index;
    String arsID, busNum;
    public BusInfo(int i, int j, String arsID, String busNum){
        this.a = i;
        this.index = j;
        this.arsID = arsID;
        this.busNum = busNum;
    }
}

public class ShowPathActivity extends AppCompatActivity {
    private static final String TAG = "ShowPathActivity";
    TextView departure, destination;

    private FragmentAll fragmentAll;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private TextView all, bus, sub;
    private View first, second, third;

    private int searchType = 0, FLAG = 0, AUTOCOMPLETE_REQUEST_CODE = 1;//, totalWalk, cost, totalTime;
    private Intent searchIntent = null;
    Document doc;
    DBHelper MyDB;
    RouteDBHelper routeDBHelper;
    SpeedDBHelper speedDBHelper;

    String token;
    String departureName, destinationName;

    //variables to measure time
    boolean isFiltered = false;
    ServiceApi service;
    static JsonArray pathArray;
    //public static JsonArray pathArray;
    ArrayList<BusInfo> busInfoList;

    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
    static ArrayList<Route> routeArrayList;
    private String sx, sy, ex, ey;
    int bus_num = 0, sub_num = 0;
    int bus_called = 0, sub_called = 0;
    boolean bus_fin = false, sub_fin = false;

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

        first = findViewById(R.id.allLine);
        second = findViewById(R.id.busLine);
        third = findViewById(R.id.subLine);

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        MyDB = new DBHelper(this);
        routeDBHelper = new RouteDBHelper(this);
        speedDBHelper = new SpeedDBHelper(this);

        String[] strings;
        strings = getIntent().getStringArrayExtra("LOC_DATA");
        departure.setText(strings[0]);
        destination.setText(strings[1]);
        sx = strings[2]; sy = strings[3]; ex = strings[4]; ey = strings[5];
        service = RetrofitClient.getClient().create(ServiceApi.class);
        startSearchPath(0);
        token = MainActivity.getToken();
        departureName = departure.getText().toString();
        destinationName = destination.getText().toString();
    }

    public void onExample2(View v){
        Intent intent = new Intent(ShowPathActivity.this, AddPathActivity.class);
        intent.putExtra("token", token);
        intent.putExtra("sx", Double.parseDouble(sx));
        intent.putExtra("sy", Double.parseDouble(sy));
        intent.putExtra("ex", Double.parseDouble(ex));
        intent.putExtra("ey", Double.parseDouble(ey));
        intent.putExtra("departureName", departureName);
        intent.putExtra("destinationName", destinationName);
        startActivity(intent);
    }

    public void onClickShowPath(View v) {
        String dest, dept;
        dept = departure.getText().toString();
        dest = destination.getText().toString();
        switch (v.getId()) {
            case R.id.star:
                //즐겨찾는 경로에 추가
                if(!dept.isEmpty() && !dest.isEmpty()) {
                    addFavoritePath(new RouteDB(dept, dest, sx, sy, ex, ey));
                }
                break;
            case R.id.swap:
                //swap 버튼 누르면 departure랑 destination 내용 바뀌게
                //fragmentAll로 초기화
                searchType = 0;
                dest = departure.getText().toString();
                dept = destination.getText().toString();
                all.setTextColor(Color.parseColor("#1ABC9C"));
                bus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                sub.setTextColor(getResources().getColor(android.R.color.darker_gray));

                first.setBackgroundColor(Color.parseColor("#1abc9c"));
                second.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                third.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                //transaction.replace(R.id.showPathframe, fragmentAll);//.commitAllowingStateLoss();
                departure.setText(dept);
                destination.setText(dest);

                //위도 경도도 바뀌게
                String tmpSx, tmpSy;
                tmpSx = sx; tmpSy = sy;
                sx = ex; sy = ey;
                ex = tmpSx; ey = tmpSy;

                //길찾기 실행
                startSearchPath(0);
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

    public void addFavoritePath(RouteDB routeDB){
        service.addFavoritePath("application/json", token, routeDB).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                PostResponse result = response.body();
                boolean success = response.isSuccessful();
                if(success) {
                    int code = result.getCode();
                    if (code == 200) {   //즐겨찾는 장소 등록 성공
                        Toast.makeText(ShowPathActivity.this, "즐겨찾는 경로에 추가되었습니다", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    try {
                        Gson gson = new Gson();
                        result = gson.fromJson(response.errorBody().string(), PostResponse.class);
                        Toast.makeText(ShowPathActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Toast.makeText(ShowPathActivity.this, "통신 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("통신 에러 발생", t.getMessage());
            }
        });
    }

    //google maps에 위치 검색
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                String x, y;
                x = Double.toString(place.getLatLng().longitude);
                y = Double.toString(place.getLatLng().latitude);
                if (FLAG == 1) {    //출발지 검색
                    sx = x;
                    sy = y;
                    departure.setText(place.getName());
                } else if (FLAG == 2) {
                    ex = x;
                    ey = y;
                    destination.setText(place.getName());
                }

                //길찾기 실행
                startSearchPath(0);

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

                first.setBackgroundColor(Color.parseColor("#1abc9c"));
                second.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                third.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                startSearchPath(searchType);
                break;
            case R.id.showBus:
                searchType = 2;

                all.setTextColor(getResources().getColor(android.R.color.darker_gray));
                bus.setTextColor(Color.parseColor("#1abc9c"));
                sub.setTextColor(getResources().getColor(android.R.color.darker_gray));

                first.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                second.setBackgroundColor(Color.parseColor("#1abc9c"));
                third.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                startSearchPath(searchType);
                break;
            case R.id.showSub:
                searchType = 1;
                all.setTextColor(getResources().getColor(android.R.color.darker_gray));
                bus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                sub.setTextColor(Color.parseColor("#1abc9c"));

                first.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                second.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                third.setBackgroundColor(Color.parseColor("#1abc9c"));
                startSearchPath(searchType);
                break;
        }
    }

    public void onBackClick(View v) {
        finish();
    }

    private void startSearchPath(int searchType) {
        int type;
        type = searchType;

        Bundle bundle = new Bundle();
        String dept, dest;
        dept = departure.getText().toString();
        dest = destination.getText().toString();
        String pathDetail[] = {dept, dest, sx, sy, ex, ey};
        bundle.putStringArray("PATH_INFO", pathDetail);
        fragmentAll.setArguments(bundle);

        //검색 기록에 추가
        RouteDB db = new RouteDB(dept, dest, sx, sy, ex, ey);
        MyDB.insertRoute(db);

        Log.i(TAG, "sx: " + sx + ", sy: " + sy + ", ex: " + ex + ", ey: " + ey + ", type: " + type);
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
            progressBar.startAnimation(animation);

            //변수 초기화
            routeArrayList = new ArrayList<>();
            busInfoList = new ArrayList<>();
            sub_fin = false; bus_fin = false;
            sub_num = 0; bus_num = 0;
            bus_called = 0; sub_called = 0;
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
            try {
                if(searchType == 1)
                    bus_fin = true;
                else if(searchType == 2)
                    sub_fin = true;

                JsonObject result = jsonElement.getAsJsonObject();
                pathArray = result.get("path").getAsJsonArray();

                // pathArray 안의 경로 개수
                int pathArrayCount = pathArray.size();

                for (int a = 0; a < pathArrayCount; a++) { //경로 개수만큼
                    int totalWalk, cost, totalTime, group, likedNum = 0, myPathIdx = 0;
                    JsonObject infoOBJ = pathArray.get(a).getAsJsonObject();
                    totalWalk = 0;

                    int pathType = infoOBJ.get("pathType").getAsInt();
                    if (searchType == 0 || searchType ==  pathType) {   //검색 타입과 일치하는 것만 출력
                        group = infoOBJ.get("group").getAsInt();
                        if(group == 1 || group == 4 || group == 5) {
                            cost = infoOBJ.get("totalPay").getAsInt();
                        }
                        else{
                            cost = 0;
                            if(group == 2 || group == 3){  //좋아요 수 확인
                                likedNum = infoOBJ.get("likeNum").getAsInt();
                                myPathIdx = infoOBJ.get("myPathIdx").getAsInt();
                            }
                        }
                        totalTime = infoOBJ.get("totalTime").getAsInt(); // 소요시간
                        String finalStation = infoOBJ.get("lastEndStation").getAsString(); // 도착 정거장
                        //한 세부 경로에 대해 대중교통 몇 번 이용
                        int bus = infoOBJ.get("busTransitCount").getAsInt();
                        int sub = infoOBJ.get("subwayTransitCount").getAsInt();
                        int total = bus + sub;

                        // 세부경로 디테일
                        JsonArray subPathArray = infoOBJ.get("subPath").getAsJsonArray();
                        int subPathArraycount = subPathArray.size();
                        boolean is_first = true;
                        int index = 0;
                        //한 경로에 대해 한 itemArrayList 가짐
                        ArrayList<Item> itemArrayList = new ArrayList<>();
                        Route route = new Route(cost, group, itemArrayList);
                        routeArrayList.add(route);

                        for (int b = 0; b < subPathArraycount; b++) {   //세부 경로
                            JsonObject subPathOBJ = subPathArray.get(b).getAsJsonObject();
                            int trafficType = subPathOBJ.get("trafficType").getAsInt(); // 이동방법
                            if (trafficType == 1 || trafficType == 2) {   //1: 지하철, 2: 버스, 3: 도보
                                Item item;
                                Vector<String> busNum = new Vector<>();
                                int subLine, busType, wayCode;
                                String stationName;
                                stationName = subPathOBJ.get("startName").getAsString(); // 출발지
                                JsonArray laneObj = subPathOBJ.get("lane").getAsJsonArray();

                                if (trafficType == 1) { // 지하철
                                    wayCode = subPathOBJ.get("wayCode").getAsInt(); //상행, 하행(1, 2)
                                    subLine = laneObj.get(0).getAsJsonObject().get("subwayCode").getAsInt(); // 지하철 정보(몇호선)
                                    busType = -1;   //지하철

                                    item = new Item(stationName, busNum, "", subLine, busType, is_first);
                                    itemArrayList.add(item);

                                    if(!stationName.endsWith("역"))
                                        stationName += "역";
                                    if (is_first) {   //첫 타자면
                                        sub_num++;
                                        is_first = false;
                                        int publicCode = laneObj.get(0).getAsJsonObject().get("publicCode").getAsInt(); //공공데이터포털
                                        item.setPublicCode(publicCode);
                                        //첫 번째 지하철에 대해서만 xml 호출할 것
                                        String subStation = stationName;
                                        String[] stationArray = {"아차산(어린이대공원후문)", "안암(고대병원앞)", "올림픽공원(한국체대)", "월드컵경기장(성산)", "대흥(서강대앞)", "공릉(서울산업대입구)" ,"총신대입구(이수)", "숭실대입구(살피재)", "군자(능동)", "천호(풍납토성)", "굽은다리(강동구민회관앞)", "남한산성입구(성남법원, 검찰청)", "오목교(목동운동장앞)", "몽촌토성(평화의문)", "신촌(경의.중앙선)", "증산(명지대앞)", "월곡(동덕여대)", "어린이대공원(세종대)", "상도(중앙대앞)", "신정(은행정)", "광나루(장신대)", "천호(풍납토성)", "새절(신사)", "상월곡(한국과학기술연구원)", "화랑대(서울여대입구)", "응암순환(상선)", "군자(능동)", "쌍용(나사렛대)"
                                        };
                                        for(int i = 0; i < stationArray.length; i++){
                                            if(stationArray[i].contains(stationName))
                                                subStation  = stationArray[i];
                                        }
                                        getSubInfo(a, index, subStation , wayCode, subLine, publicCode);    //어떤 역, 상행/ 하행, 몇호선, 공공데이터포털
                                    }
                                }
                                else { // 버스
                                    subLine = 0;    //버스
                                    //busNum 여러개일 수도
                                    for(int i = 0; i < laneObj.size(); i++)
                                        busNum.add(laneObj.get(i).getAsJsonObject().get("busNo").getAsString()); // 버스번호정보
                                    busType = laneObj.get(0).getAsJsonObject().get("type").getAsInt();
                                    //OdsayAPi(startID);
                                    //OdsayApi(stationID)에서 getStation하기 전에 밑에 코드 실행되는듯
                                    String arsID = subPathOBJ.get("busArsID").getAsString();
                                    item = new Item(stationName, busNum, arsID, subLine, busType, is_first);
                                    itemArrayList.add(item);
                                    if(is_first) {
                                        bus_num++;
                                        is_first = false;
                                        getBusInfo(a, index, arsID, busNum.get(0));
                                        busInfoList.add(new BusInfo(a, index, arsID, busNum.get(0)));
                                    }
                                }
                                //stationNo: layout에 띄워줄 정류장 번호
                                index++;
                            } else {    //도보
                                totalWalk += subPathOBJ.get("sectionTime").getAsInt();
                            }
                        }   //세부 정로 다 돌았음
                        //도착지 정보
                        int subLine = -1, busType = 0;
                        if (itemArrayList.get(total - 1).getSubLine() == 0){  //마지막 수단 == 버스
                            finalStation += "정류장";
                            busType = 1;
                        }
                        else{   //마지막 수단 == 지하철
                            if(!finalStation.endsWith("역"))
                                finalStation += "역";
                            subLine = itemArrayList.get(total - 1).getSubLine();
                        }
                        Vector<String> tmpBusNum = new Vector<>();
                        Item lastItem = new Item(finalStation, tmpBusNum, "", subLine, busType, false);
                        itemArrayList.add(lastItem);

                        Double speed = Double.parseDouble(speedDBHelper.getSpeed());
                        String multipleStr = String.format("%.2f", 4.3 / speed);
                        float multiple = Float.parseFloat(multipleStr);
                        int newWalkingTime = Math.round(multiple * totalWalk);
                        totalTime -= totalWalk;
                        totalTime += newWalkingTime;
                        route.setTotalTime(totalTime);
                        route.setWalkingTime(newWalkingTime);

                        if(group == 2 || group == 3){
                            route.setLikedNum(likedNum);
                            route.setMyPathIdx(myPathIdx);
                        }
                    }
                }

                //모든 경로 다 돌았음
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void getBusInfo(int a, int index, String arsID, String busNum) {
        String rss;
        if (isFiltered)
            rss = "http://ws.bus.go.kr/api/rest/stationinfo/getLowStationByUid?ServiceKey&arsId=" + arsID;
        else
            rss = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?ServiceKey&arsId=" + arsID;  // RSS URL 구성
        GetXMLTask task = new GetXMLTask(routeArrayList.get(a).getItemList().get(index), busNum);
        task.execute(rss);
    }
    private class GetXMLTask extends AsyncTask<String, Void, Void> {
        Item item;
        String busNum;

        public GetXMLTask(Item item, String busNum) {
            this.item = item;
            this.busNum = busNum;
        }

        @Override
        protected Void doInBackground(String... urls) {
            URL url;
            bus_called++;
            if(!busNum.equals("")) {
                try {
                    url = new URL(urls[0]);
                    DocumentBuilderFactory dbf = DocumentBuilderFactory
                            .newInstance();
                    DocumentBuilder db;

                    db = dbf.newDocumentBuilder();
                    doc = db.parse(new InputSource(url.openStream()));
                    doc.getDocumentElement().normalize();
                    busRemaining(doc, item, busNum);
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
                }
                return null;
            }
            else
                return null;
        }

        protected boolean busRemaining(Document doc, Item item, String busNum){
            NodeList nodeList = doc.getElementsByTagName("itemList");
            boolean found = false;
            String arrmsg = "";
            for(int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;

                NodeList rtNmList = fstElmnt.getElementsByTagName("rtNm");
                Element rtNmElement = (Element) rtNmList.item(0);
                rtNmList = rtNmElement.getChildNodes();
                String rtNm = ((Node) rtNmList.item(0)).getNodeValue();

                if(rtNm.contains(busNum)){  //해당 버스 찾음
                    NodeList arrmsg1List = fstElmnt.getElementsByTagName("arrmsg1");
                    Element arrmsg1Element = (Element) arrmsg1List.item(0);
                    arrmsg1List = arrmsg1Element.getChildNodes();
                    String arrmsg1 = ((Node) arrmsg1List.item(0)).getNodeValue();

                    String[] array = arrmsg1.split("\\[");
                    if(array[0].equals("")){    //[막차] 1분31초후[1번째 전]
                        String[] tmpArray = arrmsg1.split("] ");
                        arrmsg = tmpArray[1];
                    }
                    else
                        arrmsg = array[0];
                    found = true;
                    break;
                }
            }

            if(!found){
                if(isFiltered)
                    arrmsg = "저상버스 정보가 없습니다";
                else
                    arrmsg = "운행종료";
            }
            setRemainingTime(item, arrmsg);
            return true;
        }

        public void setRemainingTime(Item item, String arrmsg){
            String str[] = arrmsg.split("분");
            try {   //남은 시간이 숫자일 경우
                int time = Integer.parseInt(str[0]) * 60;
                if (Integer.parseInt(str[0]) < 10) {  //10분 미만
                    str = str[1].split("초");
                    time += Integer.parseInt(str[0]);
                }
                item.setTime(time);
                item.setRemainingTime("");
            } catch (NumberFormatException e) {    //문자일 경우
                item.setTime(0);
                item.setRemainingTime(arrmsg);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(bus_called == bus_num)
                bus_fin = true;
            if(sub_fin && bus_fin){
                RouteAdapter adapter = fragmentAll.getAdapter();
                if (adapter != null)
                    adapter.stopTimer();
                getSupportFragmentManager().beginTransaction().detach(fragmentAll).attach(fragmentAll).commit();
            }
        }
    }

    //지하철 정보
    private void getSubInfo(int a, int index, String subStation, int wayCode, int subLine, int publicCode){
        String rss = "http://swopenapi.seoul.go.kr/api/subway/6c73727a4c70616e36336e6d707076/xml/realtimeStationArrival/1/6/" + subStation;  // RSS URL 구성
        Item item = routeArrayList.get(a).getItemList().get(index);
        GetSubXMLTask subXMLTask = new GetSubXMLTask(item, wayCode, subLine, publicCode);
        subXMLTask.execute(rss);
    }

    private class GetSubXMLTask extends AsyncTask<String, Void, Void> {
        Item item;
        int wayCode, subLine, publicCode;

        public GetSubXMLTask(Item item, int wayCode, int subLine, int publicCode) {
            this.item = item;
            this.wayCode = wayCode;
            this.subLine = subLine;
            this.publicCode = publicCode;
        }

        @Override
        protected Void doInBackground(String... urls) {
            sub_called++;

            URL url;
            if(subLine != -1) {
                try {
                    url = new URL(urls[0]);
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db;

                    db = dbf.newDocumentBuilder();
                    doc = db.parse(new InputSource(url.openStream()));
                    doc.getDocumentElement().normalize();
                    subRemaining(doc, item, wayCode, subLine, publicCode);

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Parsing Error",
                            Toast.LENGTH_SHORT).show();
                }
            }

            subRemaining(doc, item, wayCode, subLine, publicCode);
            return null;
        }


        public boolean subRemaining(Document doc, Item item, int tmpWayCode, int subLine, int publicCode){

            NodeList nodeList = doc.getElementsByTagName("row");

            String wayCode;
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
                int subwayId = Integer.parseInt(tmpId);

                NodeList updnLineList = fstElmnt.getElementsByTagName("updnLine");
                Element updnLineElement = (Element) updnLineList.item(0);
                updnLineList = updnLineElement.getChildNodes();
                String updnLine = ((Node) updnLineList.item(0)).getNodeValue();

                if(subwayId == publicCode && updnLine.equals(wayCode)){//arvlMsg3
                    NodeList arvlMsg2List = fstElmnt.getElementsByTagName("arvlMsg2");
                    Element arvlMsg2Element = (Element) arvlMsg2List.item(0);
                    arvlMsg2List = arvlMsg2Element.getChildNodes();
                    String arvlMsg2 = ((Node) arvlMsg2List.item(0)).getNodeValue();

                    found = true;
                    setRemainingTime(arvlMsg2, item);
                    break;
                }
            }

            if(!found){
                item.setTime(0);
                item.setRemainingTime("운행종료");
            }

            return true;
        }

        public void setRemainingTime(String arrmsg, Item item){
            String min[] = arrmsg.split("분 ");
            try {   //남은 시간이 숫자일 경우
                int time = Integer.parseInt(min[0]) * 60;
                String sec[] = min[1].split("초 후 ");
                //초가 있을 경우
                if(sec.length == 2){
                    time += Integer.parseInt(sec[0]);
                }
                item.setTime(time);
            } catch (NumberFormatException e) {    //문자일 경우
                item.setRemainingTime(arrmsg);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(sub_called == sub_num)
                sub_fin = true;
            if(sub_fin && bus_fin){
                getSupportFragmentManager().beginTransaction().detach(fragmentAll).attach(fragmentAll).commit();  //transaction = getSupportFragmentManager().beginTransaction()
            }
        }
    }
    public void onFilterChecked(View v) {
        boolean checked = ((Switch) v).isChecked();
        bus_called = 0;
        String rss;
        if(checked){
            isFiltered = true;
            for(int i = 0; i < busInfoList.size(); i++){
                BusInfo busInfo = busInfoList.get(i);
                String arsID = busInfo.arsID;
                rss = "http://ws.bus.go.kr/api/rest/stationinfo/getLowStationByUid?ServiceKey=6qStrxuINKmwimpKepusWn2D0%2B%2FV%2FKifMCu5X8Po12nfWFFuC9vIK0Rrpv4EtwERm7%2FM9eZeGaOSvXvxabBsYg%3D%3D&arsId=" + arsID;

                int x, y;
                x = busInfo.a; y = busInfo.index;
                Item item = routeArrayList.get(x).getItemList().get(y);
                GetXMLTask task = new GetXMLTask(item, busInfo.busNum);
                task.execute(rss);

            }

        }
        else{
            isFiltered = false;
            for(int i = 0; i < busInfoList.size(); i++){
                BusInfo busInfo = busInfoList.get(i);
                String arsID = busInfo.arsID;
                rss = "http://ws.bus.go.kr/api/rest/stationinfo/getStationByUid?ServiceKey&arsId=" + arsID;

                int x, y;
                x = busInfo.a; y = busInfo.index;
                Item item = routeArrayList.get(x).getItemList().get(y);
                GetXMLTask task = new GetXMLTask(item, busInfo.busNum);
                task.execute(rss);
            }
        }
    }
}