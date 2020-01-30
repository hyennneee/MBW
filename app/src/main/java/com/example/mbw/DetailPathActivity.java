package com.example.mbw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

public class DetailPathActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    private static final String TAG = "DetailPathActivity";


    private BottomSheetBehavior mBottomSheetBehavior;
    private int lastSheetState;
    private boolean userIsChangingSheetState = false;

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
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        userIsChangingSheetState = true;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        //setSheetState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }

            private void onStateChange(int sheetState)
            {
                userIsChangingSheetState = false;
                mBottomSheetBehavior.setState(sheetState);
                lastSheetState = sheetState;
            }

            public void setSheetState(int state) {
                switch (state) {
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        onStateChange(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    }
                    case BottomSheetBehavior.STATE_HALF_EXPANDED: {
                        onStateChange(BottomSheetBehavior.STATE_HALF_EXPANDED);
                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        onStateChange(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                    }
                    case BottomSheetBehavior.STATE_HIDDEN: {
                        onStateChange(BottomSheetBehavior.STATE_HIDDEN);
                        break;
                    }
                    case BottomSheetBehavior.STATE_DRAGGING: {
                        userIsChangingSheetState = true;
                        break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_SETTLING && userIsChangingSheetState)
                {
                    if ((slideOffset > 0.5 && lastSheetState == BottomSheetBehavior.STATE_HALF_EXPANDED)
                            || (slideOffset >= 0.70 && lastSheetState == BottomSheetBehavior.STATE_COLLAPSED)) {
                        setSheetState(BottomSheetBehavior.STATE_EXPANDED);
                    } else if ((slideOffset > 0 && lastSheetState == BottomSheetBehavior.STATE_COLLAPSED)
                            || (slideOffset < 1 && lastSheetState == BottomSheetBehavior.STATE_EXPANDED)) {
                        setSheetState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                    } else if ((slideOffset < 0.5 && lastSheetState == BottomSheetBehavior.STATE_HALF_EXPANDED)
                            || (slideOffset <= 0.40 && lastSheetState == BottomSheetBehavior.STATE_EXPANDED)) {
                        setSheetState(BottomSheetBehavior.STATE_COLLAPSED);
                    } else {
                        setSheetState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                    userIsChangingSheetState = false;
                }
            }
        });

        //지도 부분
        LinearLayout linearLayoutTmap = findViewById(R.id.linearLayoutTmap);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("Tmap api key");
        linearLayoutTmap.addView( tMapView );

        tMapView.setIconVisibility(true);//현재위치로 표시될 아이콘을 표시할지 여부를 설정합니다.

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
        PathAsync pathAsync = new PathAsync();
        pathAsync.execute(polyLine);
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
