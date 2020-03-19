package com.example.mbw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mbw.DB.DBHelper;
import com.example.mbw.DB.PlaceDB;
import com.example.mbw.DB.PlaceDBHelper;
import com.example.mbw.DB.RecordAdapter;
import com.example.mbw.DB.RouteDB;
import com.example.mbw.DB.RouteDBHelper;
import com.example.mbw.MyPage.FragmentPath;
import com.example.mbw.MyPage.MyPageActivity;
import com.example.mbw.MyPage.data.FavoritePathResponse;
import com.example.mbw.MyPage.data.LocationResponse;
import com.example.mbw.network.RetrofitClient;
import com.example.mbw.network.ServiceApi;
import com.example.mbw.route.Route;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecordAdapter.OnItemClickListener{
    Intent intent = null, searchIntent = null;
    TextView departure, destination;
    TextView toHome, toOffice, userName;
    ImageView profile, swap, home, office, bookmark;
    int AUTOCOMPLETE_REQUEST_CODE = 1, FLAG = 0;
    //double longitude[] = new double[2], latitude[] = new double[2];
    private LocationManager locationManager;
    public static String token = null;
    String sx = "0", sy = "0", ex = "0", ey = "0", currLocation;
    PlaceDB homeDB, officeDB;
    // Set the fields to specify which types of place data to
    // return after the user has made a selection.
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
    DBHelper MyDB;
    RouteDBHelper routeDBHelper;
    ServiceApi service;
    boolean getData = false;
    boolean isBookmark = false;
    private ArrayList<RouteDB> mArrayList;
    private RecordAdapter mAdapter;
    static private RecyclerView mRecyclerView;
    private FusedLocationProviderClient fusedLocationClient;
    private FragmentPath fragmentPath;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private ArrayList<RouteDB> routeArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        departure = findViewById(R.id.departureText);
        destination = findViewById(R.id.destinationText);
        userName = findViewById(R.id.userName);
        Places.initialize(getApplicationContext(), getString(R.string.google_key));

        service = RetrofitClient.getClient().create(ServiceApi.class);

        String info[] = getIntent().getStringArrayExtra("USER_INFO");
        token = info[0];

        Log.i("Token", token);
        userName.setText(info[1]);

        MyDB = new DBHelper(this);
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


        //실제 디바이스 돌릴 때 들어갈 내용
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            sx = Double.toString(location.getLongitude());
                            sy = Double.toString(location.getLatitude());
                            currLocation = getCurrentAddress(new LatLng(Double.parseDouble(sy), Double.parseDouble(sx)));
                            String sp[] = currLocation.split("시");
                            currLocation = "서울" + sp[1];
                            departure.setText(currLocation);
                        }
                    }
                });

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

    static public String getToken(){
        return token;
    }
    public void search() {  //검색기록
        mRecyclerView = (RecyclerView) findViewById(R.id.searchView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mArrayList = MyDB.getAllRoutes();
        mAdapter = new RecordAdapter(mArrayList, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    //경로 검색 기록 && 즐겨찾는 경로 클릭했을 때 발생하는 이벤트
    public void onItemClick(int position){
        RouteDB db;
        String data[];
        String dept, dest;
        //검색기록
        if(!isBookmark) {
            db = mArrayList.get(position);
            data = db.getValue().toArray(new String[0]);
            dept = db.getDeparture();
            dest = db.getDestination();
            MyDB.deleteRoute(dept, dest);
        }
        else {//즐겨찾기
            db = routeArrayList.get(position);
            data = db.getValue().toArray(new String[0]);
            dept = db.getDeparture();
            dest = db.getDestination();
            MyDB.deleteRoute(dept, dest);
        }


        //검색 기록에 추가
        MyDB.insertRoute(db);

        Intent intent = new Intent(MainActivity.this, ShowPathActivity.class);
        intent.putExtra("LOC_DATA", data);
        intent.putExtra("token",token);
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
        GetLocationTask locationTask;
        switch (v.getId()){
            case R.id.toHome:
            case R.id.houseButton:
                //집으로 누르면 목적지 집 위치로 바뀜
                //null이면 집 저장해달라는 메세지 띄우기
                locationTask = new GetLocationTask(MainActivity.this, token, 1);
                locationTask.execute();
                break;
            case R.id.toOffice:
            case R.id.officeButton:
                //회사로 누르면 목적지 회사 위치로 바뀜
                //null이면 회사 저장해달라는 메세지 띄우기
                locationTask = new GetLocationTask(MainActivity.this, token, 2);
                locationTask.execute();
                break;
            case R.id.swap: //도착지 지정 안 된 상태에서 swap하면 안 됨
                if(destination.getText().toString() != "") {
                    //swap 버튼 누르면 departure랑 destination "내용" 바뀌게
                    String deptTmp = departure.getText().toString();
                    String destTmp = destination.getText().toString();
                    departure.setText(destTmp);
                    destination.setTextColor(Color.BLACK);
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
                //getFavoritePath(token);
                //mArrayList = routeDBHelper.getAllRoutesBM();
                GetBookmarkTask bookmarkTask = new GetBookmarkTask(MainActivity.this, token);
                bookmarkTask.execute();
                break;
            case R.id.departureText:
                FLAG = 1;
                searchPlace();
                break;
            case R.id.destinationText:
                FLAG = 2;
                searchPlace();
                break;
            case R.id.deleteAllButton:
                MyDB.deleteAll();
                search();
                Toast.makeText(this, "모든 검색기록이 삭제되었습니다", Toast.LENGTH_SHORT).show();
        }
    }

    private class GetBookmarkTask extends AsyncTask<Void, Void, Void> {
        private Activity context;
        private  String token;

        public GetBookmarkTask(Activity context, String token) {
            this.context = context;
            this.token = token;
            routeArrayList = new ArrayList<>();
            isBookmark = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            service.getFavoritePath(token).enqueue(new Callback<LocationResponse>() {
                @Override
                public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                    LocationResponse result = response.body();
                    boolean success = response.isSuccessful();
                    if(success) {
                        int code = result.getCode();
                        if (code == 200) {   //즐겨찾는 장소 조회 성공
                            JsonArray data = result.getData();
                            if(data != null) {
                                try {
                                    for (JsonElement jsonElement : data) {
                                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                                        String departure, destination, SX, SY, EX, EY;
                                        departure = jsonObject.get("startAddress").getAsString();
                                        destination = jsonObject.get("endAddress").getAsString();
                                        SX = jsonObject.get("SX").getAsString();
                                        SY = jsonObject.get("SY").getAsString();
                                        EX = jsonObject.get("EX").getAsString();
                                        EY = jsonObject.get("EY").getAsString();
                                        RouteDB routeDB = new RouteDB(departure, destination, SX, SY, EX, EY);
                                        routeArrayList.add(routeDB);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                Toast.makeText(MainActivity.this, "에러 발생", Toast.LENGTH_SHORT).show();
                            }
                        }
                        getData = true;
                    }
                    else {
                        try {
                            setError(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        getData = true;
                    }
                }
                @Override
                public void onFailure(Call<LocationResponse> call, Throwable t) {
                    getData = true;
                    Toast.makeText(MainActivity.this, "통신 에러 발생", Toast.LENGTH_SHORT).show();
                    Log.e("통신 에러 발생", t.getMessage());
                }
            });
            while(true){
                if(getData)
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(routeArrayList.size() != 0) {
                mAdapter = new RecordAdapter(routeArrayList, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(MainActivity.this);
                mRecyclerView.setLayoutManager(mLinearLayoutManager);
                mAdapter = new RecordAdapter(routeArrayList, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
            }
        }

    }

    private class GetLocationTask extends AsyncTask<Void, Void, Void> {
        private Activity context;
        private  String token;
        private int category;

        public GetLocationTask(Activity context, String token, int category) {
            this.context = context;
            this.token = token;
            this.category = category;
        }

        @Override
        protected Void doInBackground(Void... params) {
            service.getLocation(token, category).enqueue(new Callback<LocationResponse>() {
                @Override
                public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                    LocationResponse result = response.body();
                    boolean success = response.isSuccessful();
                    if (success) {
                        int code = result.getCode();
                        if (code == 200) {   //즐겨찾는 장소 조회 성공
                            JsonArray data = result.getData();
                            try {
                                JsonObject jsonObject = data.get(0).getAsJsonObject();
                                String address = jsonObject.get("address").getAsString();
                                int resultCategory = jsonObject.get("category").getAsInt();
                                String X = jsonObject.get("X").getAsString();
                                String Y = jsonObject.get("Y").getAsString();
                                if(resultCategory == 1)  //집
                                    homeDB = new PlaceDB(resultCategory, address, X, Y);
                                else{   //회사
                                    officeDB = new PlaceDB(resultCategory, address, X, Y);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    else {
                        try {
                            setError(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //Toast.makeText(MainActivity.this, "저장된 주소가 없습니다", Toast.LENGTH_SHORT).show();
                        //ToDo:
                        //등록된 주소 없을 때
                        //"주소를 등록해주세요"
                        //카테고리에 따라 homeDB || officeDB = null;"
                        //Toast.makeText(MainActivity.this, "통신 에러 발생", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<LocationResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "즐겨찾는 주소 통신 에러 발생", Toast.LENGTH_SHORT).show();
                    Log.e("즐겨찾는 주소 통신 에러 발생", t.getMessage());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (category == 1) {
                if (homeDB != null){
                    String dest = homeDB.getPlace();
                    String ex, ey;
                    ex = homeDB.getX();
                    ey = homeDB.getY();
                    //검색 기록에 추가
                    RouteDB db = new RouteDB(currLocation, dest, sx, sy, ex, ey);
                    MyDB.insertRoute(db);

                    String sendData[] = {currLocation, dest, sx, sy, ex, ey};
                    intent = new Intent(MainActivity.this, ShowPathActivity.class);
                    intent.putExtra("LOC_DATA", sendData);
                    search();
                    startActivity(intent);
                }
            }
            else{
                if(officeDB != null){
                    String dest = officeDB.getPlace();
                    String ex, ey;
                    ex = officeDB.getX(); ey = officeDB.getY();
                    RouteDB db = new RouteDB(currLocation, dest, sx, sy, ex, ey);
                    MyDB.insertRoute(db);

                    String sendData[] = {currLocation, dest, sx, sy, ex, ey};
                    intent = new Intent(MainActivity.this, ShowPathActivity.class);
                    intent.putExtra("LOC_DATA", sendData);
                    startActivity(intent);
                }
            }
        }

    }

    public void setError(Response<LocationResponse> response) throws IOException {
        Gson gson = new Gson();

        LocationResponse result = gson.fromJson(response.errorBody().string(), LocationResponse.class);
        Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
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
                    destination.setTextColor(Color.BLACK);
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

                RouteDB db = new RouteDB(arrayList);
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


}