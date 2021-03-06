package com.example.mbw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mbw.AddPath.ReportActivity;
import com.example.mbw.Map.HttpHandler;
import com.example.mbw.route.DetailItem;
import com.example.mbw.route.DetailItemAdapter;
import com.example.mbw.route.Item;
import com.example.mbw.route.RouteAdapter;
import com.example.mbw.showPath.ShowPathActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import java.util.ArrayList;
import java.util.List;



public class DetailPathActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "DetailPathActivity";
    private GoogleMap mMap;


    private String url;
    private List<LatLng> polyLineList;

    private List<List<LatLng>> LatLngList = new ArrayList<>();
    private CustomSheetBehavior mBottomSheetBehavior;
    private int lastSheetState;
    private boolean userIsChangingSheetState = false;

    private ArrayList<DetailItem> detailItemList;

    private ArrayList<transitMapData> BusSubList; //시작, 끝, 시작, 끝.....

    //TMapView tMapView;
    //TMapPoint tMapPointStart = new TMapPoint(37.545316, 126.964883); // 숙명여대 37.5463644,126.9626424
    //TMapPoint tMapPointEnd = new TMapPoint(37.545248, 126.97191); // 숙대입구역

    TextView startTextView, endTextView, timeTextView, transitCntTextView, walkTimeTextView, payTextView;
    int totalTime;
    int totalPay;
    int transitCount;
    int totalWalkTime;
    String startPoint, endPoint;
    double startLati, startLongi, endLati, endLongi;
    ListView passListView;
    Document doc = null;

    boolean isFiltered;
    String arsId;
    int subwayCodeNo;
    int wayCodeNo;
    String busNum;
    String arrvMsg1, arrvMsg2;
    float multiple;


    public Bitmap resizeBitmap(String drawableName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(),getResources().getIdentifier(drawableName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng start0 = new LatLng(startLati, startLongi);
        mMap.addMarker(new MarkerOptions().position(start0).title("출발지").icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("start",210,210))));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start0,15));
        // Add a marker in Sydney and move the camera
        mMap.setMinZoomPreference(12);
        LatLng end0 = new LatLng(endLati, endLongi);
        mMap.addMarker(new MarkerOptions().position(end0).title("도착지").icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("end",210,210))));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(end0));

        for(int i=0; i<BusSubList.size(); i++){
            transitMapData nowItem = BusSubList.get(i);
            int nowLineNo = nowItem.getLineNo();
            int nowMode = nowItem.getTransitMode();
            double nowStartLati = nowItem.getStart().latitude;
            double nowStartLongi = nowItem.getStart().longitude;
            double nowEndLati = nowItem.getEnd().latitude;
            double nowEndLongi = nowItem.getEnd().longitude;

            if(nowMode == 1) { //지하철
                url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + nowStartLati + "," + nowStartLongi + "&destination=" + nowEndLati + "," + nowEndLongi + "&mode=transit&transit_mode=subway&key="+getString(R.string.google_key);
            }
            else { //버스
                url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + nowStartLati + "," + nowStartLongi + "&destination=" + nowEndLongi + "," + nowEndLati + "&mode=transit&transit_mode=bus&key="+getString(R.string.google_key);
            }

            new GetPaths().execute();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(int i=0; i<LatLngList.size(); i++){
            mMap.addPolyline(new PolylineOptions()
                    .color(getResources().getColor(R.color.busblue)) // Line color.
                    .width(10) // Line width.
                    .clickable(false) // Able to click or not.
                    .addAll(LatLngList.get(i)));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_path);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = new CustomSheetBehavior(bottomSheet, this);
        startTextView = findViewById(R.id.startTextView);
        endTextView = findViewById(R.id.endTextView);
        timeTextView = findViewById(R.id.timeTextView);
        walkTimeTextView = findViewById(R.id.walkTimeTextView);
        transitCntTextView = findViewById(R.id.transitCntTextView);
        payTextView = findViewById(R.id.payTextView);
        BusSubList= new ArrayList<transitMapData>();
        //passListView = findViewById(R.id.passListView);

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

        Double speed = Double.parseDouble(pathInfo[7]);
        String multipleStr = String.format("%.2f", 4.3 / speed);
        multiple = Float.parseFloat(multipleStr);

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

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        RecyclerView recyclerView = findViewById(R.id.detailPath_rv) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        // 리사이클러뷰에 DetailItemAdapter 객체 지정.
        detailItemList = new ArrayList<DetailItem>();
        LatLng start = new LatLng(startLongi, startLati);
        LatLng end = new LatLng(endLongi, endLati);


        detailItemList.add(new DetailItem(start, 0, startPoint)); // 시작점
        parseJson(obj);
        detailItemList.add(new DetailItem(end, 40, endPoint)); // 끝점

        DetailItemAdapter adapter = new DetailItemAdapter(detailItemList) ;
        recyclerView.setAdapter(adapter) ;

        //지도 부분
        //LinearLayout linearLayoutTmap = findViewById(R.id.linearLayoutTmap);
        //tMapView = new TMapView(this);
        //tMapView.setSKTMapApiKey("l7xxbcd1d4f9f0984e8b99466490a2b372b7");
        //linearLayoutTmap.addView( tMapView );

        //tMapView.setIconVisibility(true);//현재위치로 표시될 아이콘을 표시할지 여부를 설정
        //setGps();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1); //위치권한 탐색 허용 관련 내용
            }
            return;
        }

        //tMapView.setZoomLevel(16);
        //tMapView.setTrackingMode(true);
        //tMapView.setCenterPoint(startLongi, startLati);
/*
        //경로 부분
        TMapPolyLine polyLine = new TMapPolyLine();
        PathAsync pathAsync = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
            pathAsync = new PathAsync();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            pathAsync.execute(polyLine);
        }
 */
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                //tMapView.setLocationPoint(longitude, latitude);
                //tMapView.setCenterPoint(longitude, latitude);
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
    /*
        // 도보 경로 표시 위해서 (from Tmap)
        class PathAsync extends AsyncTask<TMapPolyLine, Void, TMapPolyLine> {
            @Override
            protected TMapPolyLine doInBackground(TMapPolyLine... tMapPolyLines) {
                TMapPolyLine tMapPolyLine = tMapPolyLines[0];
                try {
                   // tMapPolyLine = new TMapData().findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, tMapPointStart, tMapPointEnd);
                    //tMapPolyLine.setLineColor(Color.GRAY);
                    //tMapPolyLine.setLineWidth(5);
                }catch(Exception e) {
                    e.printStackTrace();
                    Log.e("error",e.getMessage());
                }
                return tMapPolyLine;
            }
            @Override
            protected void onPostExecute(TMapPolyLine tMapPolyLine) {
                super.onPostExecute(tMapPolyLine);
                //tMapView.addTMapPolyLine("Line1", tMapPolyLine);
            }
        }
    */
    /*JSONObject parsing 해서 보여주기*/
    private void parseJson(JSONObject obj) {
        try{
            totalTime = obj.getInt("totalTime");
            totalWalkTime = obj.getInt("totalWalkTime");

            totalTime -= totalWalkTime;
            totalTime += totalWalkTime*multiple;

            totalWalkTime = Math.round(multiple * totalWalkTime);

            int hour = totalTime/60;
            int minute = totalTime%60;
            String timeInfo;
            if(hour==0)
                timeInfo = minute+"분";
            else{
                if(minute==0)
                    timeInfo=  hour+"시간 ";
                else
                    timeInfo = hour+"시간 "+minute+"분";}


            timeTextView.setText(timeInfo);
            if(obj.isNull("totalPay"))
                totalPay = 1300;
            else
                totalPay = obj.getInt("totalPay");

            payTextView.setText(totalPay+"원");
            walkTimeTextView.setText("도보 "+totalWalkTime+"분");
            transitCount = obj.getInt("transitCount");
            transitCntTextView.setText("환승 "+transitCount+"회");
            JSONArray subPathArray = obj.getJSONArray("subPath");
            int subPathCnt = subPathArray.length();
            for(int i=0; i<subPathCnt; i++){
                JSONObject subObj = subPathArray.getJSONObject(i);
                int trafficType= subObj.getInt("trafficType");
                switch(trafficType){
                    case 1://지하철
                        Log.i("지하철", "1");
                        int sectionTime1 = subObj.getInt("sectionTime");
                        int stationCount1 = subObj.getInt("stationCount");
                        JSONArray laneArray1 = subObj.getJSONArray("lane");
                        JSONObject laneObj1 = laneArray1.getJSONObject(0);
                        int subwayCode = laneObj1.getInt("subwayCode");
                        subwayCodeNo = laneObj1.getInt("publicCode");

                        String startName1 = subObj.getString("startName");
                        LatLng start1 = new LatLng( subObj.getDouble("startY"), subObj.getDouble("startX"));
                        String endName1 = subObj.getString("endName");
                        LatLng end1 = new LatLng( subObj.getDouble("endY"), subObj.getDouble("endX"));
                        String door = subObj.getString("door"); // ex. 1-1
                        wayCodeNo = subObj.getInt("wayCode");
                        ArrayList<String> passStopArray = new ArrayList<>();

                        // map 표시 위해
                        BusSubList.add(new transitMapData(subwayCode,1, start1, end1));

                        JSONObject passStopObj = subObj.getJSONObject("passStopList");
                        JSONArray stationsArray = passStopObj.getJSONArray("stations");
                        int stationsCnt = stationsArray.length();
                        for(int j=0; j<stationsCnt; j++){
                            JSONObject nowStation = stationsArray.getJSONObject(j);
                            String stationName = nowStation.getString("stationName");
                            passStopArray.add(stationName);
                        }
                        Log.i("passSTOP", passStopArray.toString());

                        JSONObject secondObj = stationsArray.getJSONObject(1); // 다음역 Object
                        String direction = secondObj.getString("stationName");

                        JSONArray startElevatorArray = subObj.getJSONArray("startElevatorInfo");

                        JSONObject problemObj = startElevatorArray.getJSONObject(0);
                        String problem = problemObj.getString("problem");
                        Log.i("problem", problem);
                        JSONObject startElevatorObj = startElevatorArray.getJSONObject(1);
                        String startContent = startElevatorObj.getString("content");

                        if(subObj.has("endElevatorInfo")){
                            try {
                                Log.i("try","try");
                                JSONArray endElevatorArray = subObj.getJSONArray("endElevatorInfo");
                                JSONObject problemObj2 = endElevatorArray.getJSONObject(0);
                                String problem2 = problemObj2.getString("problem");
                                Log.i("problem",problem2);

                                JSONObject endElevatorObj = endElevatorArray.getJSONObject(1);
                                String endContent = endElevatorObj.getString("content");
                                detailItemList.add(new DetailItem(start1, end1, subwayCode, startName1, endName1, direction, null, null, arrvMsg1, arrvMsg2, sectionTime1,  stationCount1, startContent, endContent, passStopArray, problem, problem2));

                            } catch (JSONException e) {
                                Log.i("catch","catch");
                            }
                        }
                        else
                            detailItemList.add(new DetailItem(start1, end1, subwayCode, startName1, endName1, direction, null, null, arrvMsg1, arrvMsg2, sectionTime1,  stationCount1, startContent, null, passStopArray, problem, "null"));

                        break;

                    case 2: //버스
                        Log.i("버스", "2");
                        int sectionTime = subObj.getInt("sectionTime");
                        int stationCount = subObj.getInt("stationCount");
                        JSONArray laneArray = subObj.getJSONArray("lane");
                        JSONObject laneObj = laneArray.getJSONObject(0);
                        String busNo = laneObj.getString("busNo");
                        busNum = busNo;
                        int type = laneObj.getInt("type");
                        String startName = subObj.getString("startName");
                        LatLng start = new LatLng( subObj.getDouble("startY"), subObj.getDouble("startX"));
                        String endName = subObj.getString("endName");
                        LatLng end = new LatLng( subObj.getDouble("endY"), subObj.getDouble("endX"));

                        ArrayList<String> passStopArray2 = new ArrayList<>();   //

                        // map 표시 위해
                        BusSubList.add(new transitMapData(-1,2, start, end));

                        JSONObject passStopObj2 = subObj.getJSONObject("passStopList"); //
                        JSONArray stationsArray2 = passStopObj2.getJSONArray("stations");
                        //arsId = passStopObj2.getString("busArsID"); // arsId 받아오기 -> arsID랑

                        int stationsCnt2 = stationsArray2.length();
                        for(int j=0; j<stationsCnt2; j++){
                            JSONObject nowStation = stationsArray2.getJSONObject(j);
                            String stationName = nowStation.getString("stationName");
                            passStopArray2.add(stationName);
                        }
                        Log.i("passSTOP", passStopArray2.toString());   //

                        int startID = subObj.getInt("startID");
                        int endID = subObj.getInt("endID");
                        detailItemList.add(new DetailItem(start, end, 20, startName, endName, String.valueOf(startID), String.valueOf(endID), type, -1, busNo, null, null, null, sectionTime, stationCount, passStopArray2));
                        break;

                    case 3: //도보
                        Log.i("도보", "3");
                        int distance = subObj.getInt("distance");
                        int sectionTime3 = Math.round(subObj.getInt("sectionTime")*multiple);
                        if(sectionTime3!=0)
                            detailItemList.add(new DetailItem(new LatLng(0,0), new LatLng(0,0), 30, sectionTime3, distance));
                        break;
                }
            }
            Log.i("json string", obj.toString());
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    private class GetPaths extends AsyncTask<Integer, Integer, Integer> {
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Integer doInBackground(Integer... integers) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray path = jsonObj.getJSONArray("routes");
                    for (int i = 0; i < path.length(); i++) {
                        JSONObject route = path.getJSONObject(i);
                        JSONObject poly = route.getJSONObject("overview_polyline");
                        String polyline = poly.getString("points");
                        polyLineList = decodePoly(polyline);
                        LatLngList.add(polyLineList);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            Log.e(TAG, "polypoly : " + polyLineList);
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            Log.e(TAG, "onPostExecute : ");
        }
    }

    private List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public void onExample3(View v){
        Intent intent = new Intent(DetailPathActivity.this, ReportActivity.class);
        startActivity(intent);
    }

}
