package com.example.mbw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mbw.route.DetailItem;
import com.example.mbw.route.DetailItemAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailPathActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    private static final String TAG = "DetailPathActivity";


    private CustomSheetBehavior mBottomSheetBehavior;
    private int lastSheetState;
    private boolean userIsChangingSheetState = false;

    private ArrayList<DetailItem> detailItemList;

    TMapView tMapView;
    TMapPoint tMapPointStart = new TMapPoint(37.545316, 126.964883); // 숙명여대 37.5463644,126.9626424
    TMapPoint tMapPointEnd = new TMapPoint(37.545248, 126.97191); // 숙대입구역

    TextView startTextView, endTextView, timeTextView, transitCntTextView, walkTimeTextView, payTextView;
    int totalTime;
    int totalPay;
    int transitCount;
    int totalWalkTime;
    String startPoint, endPoint;
    double startLati, startLongi, endLati, endLongi;

    @Override
    public void onLocationChange(Location location){
        tMapView.setLocationPoint(location.getLongitude(), location.getLatitude());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_path);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = new CustomSheetBehavior(bottomSheet, this);
        startTextView = findViewById(R.id.startTextView);
        endTextView = findViewById(R.id.endTextView);
        timeTextView = findViewById(R.id.timeTextView);
        walkTimeTextView = findViewById(R.id.walkTimeTextView);
        transitCntTextView = findViewById(R.id.transitCntTextView);
        payTextView = findViewById(R.id.payTextView);

        /*FragmenatAll에서 클릭된 경로 상세 정보*/
        String pathInfo[] = getIntent().getStringArrayExtra("DETAIL_PATH");
        startPoint = pathInfo[1];
        endPoint = pathInfo[2];
        startTextView.setText(startPoint);
        endTextView.setText(endPoint);

        startLongi = Double.parseDouble(pathInfo[3]);
        startLati = Double.parseDouble(pathInfo[4]);
        endLongi = Double.parseDouble(pathInfo[5]);
        endLati = Double.parseDouble(pathInfo[6]);
        String jsonString = pathInfo[0];
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject)jsonParser.parse(jsonString);
        JSONObject obj = null;

        try {
            obj = new JSONObject(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("clicked path json", jsonObject.toString());
        // RecyclerView 지정
        // RecyclerView recyclerView = findViewById(R.id.detailPath_rv) ;
        // recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        // Test => DetailItem 배열 넣어서 test 해보기

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        RecyclerView recyclerView = findViewById(R.id.detailPath_rv) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;


        // 리사이클러뷰에 DetailItemAdapter 객체 지정.
        detailItemList = new ArrayList<DetailItem>();    //mArrayList의 내용을 채워야돼
        LatLng start = new LatLng(startLongi, startLati);
        LatLng end = new LatLng(endLongi, endLati);


        detailItemList.add(new DetailItem(start, 0, startPoint)); // 시작점

        parseJson(obj);

        detailItemList.add(new DetailItem(end, 40, endPoint)); // 끝점

        //detailItemList.add(new DetailItem(start, end, 3, 10, 320));


        DetailItemAdapter adapter = new DetailItemAdapter(detailItemList) ;
        recyclerView.setAdapter(adapter) ;

        //지도 부분
        LinearLayout linearLayoutTmap = findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("l7xxbcd1d4f9f0984e8b99466490a2b372b7");
        linearLayoutTmap.addView( tMapView );

        tMapView.setIconVisibility(true);//현재위치로 표시될 아이콘을 표시할지 여부를 설정

        setGps();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }
            return;
        }

        tMapView.setZoomLevel(18);
        tMapView.setTrackingMode(true);

        //경로 부분
        TMapPolyLine polyLine = new TMapPolyLine();
        PathAsync pathAsync = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
            pathAsync = new PathAsync();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            pathAsync.execute(polyLine);
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                tMapView.setLocationPoint(longitude, latitude);
                tMapView.setCenterPoint(longitude, latitude);
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };


    public void setGps() {
        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                1000, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);
    }

    // 도보 경로 표시 위해서 (from Tmap)
    class PathAsync extends AsyncTask<TMapPolyLine, Void, TMapPolyLine> {
        @Override
        protected TMapPolyLine doInBackground(TMapPolyLine... tMapPolyLines) {
            TMapPolyLine tMapPolyLine = tMapPolyLines[0];
            try {
                tMapPolyLine = new TMapData().findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, tMapPointStart, tMapPointEnd);
                tMapPolyLine.setLineColor(Color.GRAY);
                tMapPolyLine.setLineWidth(5);

            }catch(Exception e) {
                e.printStackTrace();
                Log.e("error",e.getMessage());
            }
            return tMapPolyLine;
        }

        @Override
        protected void onPostExecute(TMapPolyLine tMapPolyLine) {
            super.onPostExecute(tMapPolyLine);
            tMapView.addTMapPolyLine("Line1", tMapPolyLine);
        }
    }

    /*JSONObject parsing 해서 보여주기*/
    private void parseJson(JSONObject obj) {
        try{
            totalTime = obj.getInt("totalTime");
            timeTextView.setText(totalTime+"분");
            totalPay = obj.getInt("totalPay");
            payTextView.setText(totalPay+"원");
            totalWalkTime = obj.getInt("totalWalkTime");
            walkTimeTextView.setText("도보 "+totalWalkTime+"분");
            transitCount = obj.getInt("transitCount");
            transitCntTextView.setText("환승 "+transitCount+"분");

            JSONArray subPathArray = obj.getJSONArray("subPath");
            int subPathCnt = subPathArray.length();
            for(int i=0; i<subPathCnt; i++){
                JSONObject subObj = subPathArray.getJSONObject(i);
                int trafficType= subObj.getInt("trafficType");
                switch(trafficType){
                    case 1://지하철
                        Log.i("지하철", "1");

                        break;

                    case 2: //버스
                        Log.i("버스", "2");
                        int sectionTime = subObj.getInt("sectionTime");
                        int stationCount = subObj.getInt("stationCount");
                        JSONArray laneArray = subObj.getJSONArray("lane");
                        JSONObject laneObj = laneArray.getJSONObject(0);
                        String busNo = laneObj.getString("busNo");
                        int type = laneObj.getInt("type");
                        //int laneArrayCnt = laneArray.length();
                        /*
                        for(int j=0; i<laneArrayCnt; i++){
                            JSONObject nowObj = laneArray.getJSONObject(i);
                            busNo = nowObj.getString("busNo");
                            type = nowObj.getInt("type");
                        }
*/
                        String startName = subObj.getString("startName");
                        LatLng start = new LatLng( subObj.getDouble("startY"), subObj.getDouble("startX"));
                        String endName = subObj.getString("endName");
                        LatLng end = new LatLng( subObj.getDouble("endY"), subObj.getDouble("endX"));
                        int startID = subObj.getInt("startID");
                        int endID = subObj.getInt("endID");


                        detailItemList.add(new DetailItem(start, end, 20, startName, endName, String.valueOf(startID), null, type, -1, busNo, null, null, null, sectionTime, stationCount));
//public DetailItem(LatLng start, LatLng end, int imageType, String spotName, String spotName2, String wayNum, String wayNum2, int busType1, int busType2,
//                      String busNum1, String busNum2, String remaining1, String remaining2, int time, int passedStop)

                        break;
                    case 3: //도보
                        Log.i("도보", "3");
                        int distance = subObj.getInt("distance");
                        int sectionTime3 = subObj.getInt("sectionTime");
                        detailItemList.add(new DetailItem(new LatLng(0,0), new LatLng(0,0), 30, sectionTime3, distance));

                        break;


                }
            }
        }catch(JSONException e){
            e.printStackTrace();
        }


    }

}