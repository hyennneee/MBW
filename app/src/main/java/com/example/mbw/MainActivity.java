package com.example.mbw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.AlphabeticIndex;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mbw.DB.DBHelper;
import com.example.mbw.DB.DBvalue;
import com.example.mbw.DB.PlaceDB;
import com.example.mbw.DB.PlaceDBHelper;
import com.example.mbw.DB.RecordAdapter;
import com.example.mbw.DB.RouteDBHelper;
import com.example.mbw.accountActivity.SignInActivity;
import com.example.mbw.myPageFragment.FragmentPath;
import com.example.mbw.pathData.Position;
import com.example.mbw.AddPath.AddPathActivity;
import com.example.mbw.AddPath.ReportActivity;
import com.example.mbw.showPath.ShowPathActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
public class MainActivity extends AppCompatActivity implements RecordAdapter.OnItemClickListener{
    Intent intent = null, searchIntent = null;
    TextView departure, destination;
    TextView toHome, toOffice, userName;
    ImageView profile, swap, home, office, bookmark;
    int AUTOCOMPLETE_REQUEST_CODE = 1, FLAG = 0;
    //double longitude[] = new double[2], latitude[] = new double[2];
    private LocationManager locationManager;
    String token = null, sx = "0", sy = "0", ex = "0", ey = "0", currLocation;
    PlaceDB homeDB, officeDB;
    // Set the fields to specify which types of place data to
    // return after the user has made a selection.
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
    DBHelper MyDB;
    RouteDBHelper routeDBHelper;
    PlaceDBHelper placeDBHelper;
    private ArrayList<DBvalue> mArrayList;
    private RecordAdapter mAdapter;
    static private RecyclerView mRecyclerView;
    private FusedLocationProviderClient fusedLocationClient;
    private FragmentPath fragmentPath;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        departure = findViewById(R.id.departureText);
        destination = findViewById(R.id.destinationText);
        userName = findViewById(R.id.userName);
        Places.initialize(getApplicationContext(), getString(R.string.google_key));

        //String info[] = getIntent().getStringArrayExtra("USER_INFO");
        //token = info[0];
        /*
        token = getIntent().getStringExtra("token");
        Log.i("token2", token);

        String name = getIntent().getStringExtra("userName");
        userName.setText(name);
*/

        String info[] = getIntent().getStringArrayExtra("USER_INFO");
        token = info[0];
        userName.setText(info[1]);

        MyDB = new DBHelper(this);
        placeDBHelper = new PlaceDBHelper(this);
        routeDBHelper = new RouteDBHelper(this);
        search();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        //에뮬레이터 돌릴 때 들어갈 내용
        //x경도 y 위도 (127, 36)//126.9513153, 37.496374

        sx = "126.9625327"; sy = "37.5464301";  //ex="127.0043575"; ey="37.5672437";
        /*String currentLoc = getCurrentAddress(new LatLng(Double.parseDouble(sy), Double.parseDouble(sx)));
        String sp[] = currentLoc.split(",");
        departure.setText("현위치: " + sp[0]);
        Intent intent = new Intent(MainActivity.this, ShowPathActivity.class);
        String data[] = {departure.getText().toString(), "국립중앙의료원", sx, sy, ex, ey};
        intent.putExtra("LOC_DATA", data);
        startActivity(intent);*/

        //실제 디바이스 돌릴 때 들어갈 내용
        /*fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            sx = Double.toString(location.getLongitude());
                            sy = Double.toString(location.getLatitude());
                            currLocation = getCurrentAddress(new LatLng(Double.parseDouble(sy), Double.parseDouble(sx)));
                            String sp[] = currLocation.split("국 ");
                            currLocation = sp[1];
                            departure.setText(currLocation);
                        }
                    }
                });
                */




        toHome = findViewById(R.id.toHome);
        toOffice = findViewById(R.id.toOffice);

        profile = findViewById(R.id.profileView);
        swap = findViewById(R.id.swap);
        home = findViewById(R.id.houseButton);
        office = findViewById(R.id.officeButton);
        bookmark = findViewById(R.id.bookmarkButton);


        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();


    }
    public void search() {
        mRecyclerView = (RecyclerView) findViewById(R.id.searchView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mArrayList = MyDB.getAllRoutes();
        mAdapter = new RecordAdapter(mArrayList, this);
        mRecyclerView.setAdapter(mAdapter);
    }
    public void onItemClick(int position){
        //아이템 클릭 이벤트 처리
        //DB에서 해당 내용 삭제, 추가
        //ShowPathActivity에 좌표, 출발지, 도착지 전달
        DBvalue db = mArrayList.get(position);
        String data[] = db.getValue().toArray(new String[0]);
        String dept, dest;
        dept = db.getDeparture();
        dest = db.getDestination();
        MyDB.deleteRoute(dept, dest);
        MyDB.insertRoute(db);

        Intent intent = new Intent(MainActivity.this, ShowPathActivity.class);
        intent.putExtra("LOC_DATA", data);
        startActivity(intent);
    }

    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }

    public void onClickMain(View v){
        ArrayList<PlaceDB> pdb;
        switch (v.getId()){
           case R.id.toHome:
            case R.id.houseButton:
                //집으로 누르면 목적지 집 위치로 바뀜
                //null이면 집 저장해달라는 메세지 띄우기
                pdb = placeDBHelper.getAllPlaces();
                for(PlaceDB place : pdb){
                    if(place.getName().equals("home"))
                        homeDB = place;
                    else
                        officeDB = place;
                }
                if(homeDB == null)
                    return;
                else{
                    String dest = homeDB.getPlace();
                    String ex, ey;
                    ex = homeDB.getX(); ey = homeDB.getY();
                    DBvalue db = new DBvalue(currLocation, dest, sx, sy, ex, ey);
                    MyDB.insertRoute(db);

                    String sendData[] = {currLocation, dest, sx, sy, ex, ey};
                    intent = new Intent(MainActivity.this, ShowPathActivity.class);
                    intent.putExtra("LOC_DATA", sendData);
                    startActivity(intent);
                }
                break;
            case R.id.toOffice:
            case R.id.officeButton:
                //회사로 누르면 목적지 회사 위치로 바뀜
                //null이면 회사 저장해달라는 메세지 띄우기
                pdb = placeDBHelper.getAllPlaces();
                for(PlaceDB place : pdb){
                    if(place.getName().equals("home"))
                        homeDB = place;
                    else
                        officeDB = place;
                }
                if(officeDB == null)
                    return;
                else{
                    String dest = officeDB.getPlace();
                    String ex, ey;
                    ex = officeDB.getX(); ey = officeDB.getY();
                    DBvalue db = new DBvalue(currLocation, dest, sx, sy, ex, ey);
                    MyDB.insertRoute(db);

                    String sendData[] = {currLocation, dest, sx, sy, ex, ey};
                    intent = new Intent(MainActivity.this, ShowPathActivity.class);
                    intent.putExtra("LOC_DATA", sendData);
                    startActivity(intent);
                }
                break;
            case R.id.swap: //도착지 지정 안 된 상태에서 swap하면 안 됨
                if(destination.getText().toString() != "") {
                    //swap 버튼 누르면 departure랑 destination "내용" 바뀌게
                    String deptTmp = departure.getText().toString();
                    String destTmp = destination.getText().toString();
                    departure.setText(destTmp);
                    destination.setText(deptTmp);

                    //위도 경도도 바뀌게
                    String tmpSx, tmpSy;
                    tmpSx = sx; tmpSy = sy;
                    sx = ex; sy = ey;
                    ex = tmpSx; ey = tmpSy;
                }
                break;
            case R.id.profileView:
                //profile사진 누르면 myPage로 이동
                String name = userName.getText().toString();
                intent = new Intent(MainActivity.this, MyPageActivity.class);
                intent.putExtra("NAME", name);
                startActivity(intent);
                break;
            case R.id.bookmarkButton:
                //즐찾 버튼 누르면 즐찾해놓은 경로 띄우기
                mArrayList = routeDBHelper.getAllRoutesBM();
                mAdapter = new RecordAdapter(mArrayList, this);
                mRecyclerView.setAdapter(mAdapter);
                break;
            case R.id.departureText:
                FLAG = 1;
                searchPlace();
                break;
            case R.id.destinationText:
                FLAG = 2;
                searchPlace();
                break;
        }
    }

    public void searchPlace(){
        searchIntent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(searchIntent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                String lat, lng;
                lat = Double.toString(place.getLatLng().latitude);
                lng = Double.toString(place.getLatLng().longitude);

                if(FLAG == 1){  //search for departure
                    sx = lng;
                    sy = lat;
                    departure.setText(place.getName());
                }
                else if(FLAG == 2){
                    ex = lng;
                    ey = lat;
                    destination.setText(place.getName());
                }

                //출발지 또는 목적지 입력 안 됨
                if (departure.getText().toString().isEmpty() || destination.getText().toString().isEmpty()) {
                    return;
                }
                //길찾기 실행

                String[] sendData = new String[6];
                sendData[0] = departure.getText().toString();
                sendData[1] = destination.getText().toString();
                sendData[2] = sx; sendData[3] = sy; sendData[4] = ex; sendData[5] = ey;

                ArrayList<String> arrayList = new ArrayList<>();
                for(String temp : sendData){
                    arrayList.add(temp);
                }

                DBvalue db = new DBvalue(arrayList);
                MyDB.insertRoute(db);
                intent = new Intent(MainActivity.this, ShowPathActivity.class);
                intent.putExtra("LOC_DATA", sendData);
                startActivity(intent);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                //Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    public void onExample(View v){
        intent = new Intent(MainActivity.this, DetailPathActivity.class);
        startActivity(intent);
    }

    public void onExample2(View v){
        intent = new Intent(MainActivity.this, AddPathActivity.class);
        intent.putExtra("token", token);
        startActivity(intent);
    }

    public void onExample3(View v){
        intent = new Intent(MainActivity.this, ReportActivity.class);
        startActivity(intent);
    }


}
