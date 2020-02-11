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

import com.example.mbw.route.DetailItem;
import com.example.mbw.route.DetailItemAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

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

        // RecyclerView 지정
        // RecyclerView recyclerView = findViewById(R.id.detailPath_rv) ;
        // recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        // Test => DetailItem 배열 넣어서 test 해보기

        ArrayList<String> list = new ArrayList<>();
        for (int i=0; i<2; i++) {
            list.add(String.format("TEXT %d", i)) ;
        }

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        RecyclerView recyclerView = findViewById(R.id.detailPath_rv) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;


        // 리사이클러뷰에 DetailItemAdapter 객체 지정.
        detailItemList = new ArrayList<DetailItem>();    //mArrayList의 내용을 채워야돼
        LatLng start = new LatLng(127, 35);
        LatLng end = new LatLng(127.001, 35.001);

        detailItemList.add(new DetailItem(start, 0, "숙명여자대학교"));
        detailItemList.add(new DetailItem(start, end, 3, 10, 320));

        // detailPathItem 추가
        // ex. 시작 - 도보 - 지하철 - 버스 - 도보 - 도착

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

}