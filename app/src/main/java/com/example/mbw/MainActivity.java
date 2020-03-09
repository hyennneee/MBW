package com.example.mbw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mbw.AddPath.AddPathActivity;
import com.example.mbw.AddPath.ReportActivity;
import com.example.mbw.showPath.ShowPathActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Intent intent = null, searchIntent = null;
    TextView departure, destination;
    TextView toHome, toOffice, exTV, exTV2;
    ImageView profile, swap, home, office, bookmark;
    int AUTOCOMPLETE_REQUEST_CODE = 1, FLAG = 0;
    double longitude[] = new double[2], latitude[] = new double[2];
    private LocationManager locationManager;
    // Set the fields to specify which types of place data to
    // return after the user has made a selection.
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        departure = findViewById(R.id.departureText);
        destination = findViewById(R.id.destinationText);
        exTV = findViewById(R.id.exampleTV);
        exTV2 = findViewById(R.id.exampleTV2);
        Places.initialize(getApplicationContext(), getString(R.string.google_key));


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        //에뮬레이터 돌릴 때 들어갈 내용
        exTV.setText("검색 기록 띄우기: A->B 이것도 recyclerView");
        latitude[0] = 37.5463644;
        longitude[0] = 126.9648311;
        //실제 디바이스 돌릴 때 들어갈 내용
        /*Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);*/


        /*toHome = findViewById(R.id.toHome);
        toOffice = findViewById(R.id.toOffice);

        profile = findViewById(R.id.profileView);
        swap = findViewById(R.id.swap);
        home = findViewById(R.id.houseButton);
        office = findViewById(R.id.officeButton);
        bookmark = findViewById(R.id.bookmarkButton);*/

    }

    //위치 바꼈을 때
    public void onLocationChanged(Location location){
        latitude[0] = location.getLatitude();
        longitude[0] = location.getLongitude();
        exTV.setText("lat: " + latitude[0] + "\nlong: " + longitude[0]);
    }

    public void onClickMain(View v){
        switch (v.getId()){
            case R.id.toHome:
            case R.id.houseButton:
                //집으로 누르면 목적지 집 위치로 바뀜
                //null이면 집 저장해달라는 메세지 띄우기
                break;
            case R.id.toOffice:
            case R.id.officeButton:
                //회사로 누르면 목적지 회사 위치로 바뀜
                //null이면 회사 저장해달라는 메세지 띄우기
                break;
            case R.id.swap:
                if(destination.getText().toString() != "") {
                    //swap 버튼 누르면 departure랑 destination "내용" 바뀌게
                    String deptTmp = departure.getText().toString();
                    String destTmp = destination.getText().toString();
                    departure.setText(destTmp);
                    destination.setText(deptTmp);

                    //위도 경도도 바뀌게
                    double lt, lg;
                    lt = latitude[0];
                    lg = longitude[0];
                    latitude[0] = latitude[1];
                    longitude[0] = longitude[1];
                    latitude[1] = lt;
                    longitude[1] = lg;

                    String[] str = new String[6];
                    str[0] = new Double(latitude[0]).toString();
                    str[1] = new Double(latitude[1]).toString();
                    str[2] = new Double(longitude[0]).toString();
                    str[3] = new Double(longitude[1]).toString();
                    str[4] = departure.getText().toString();
                    str[5] = destination.getText().toString();

                    intent = new Intent(MainActivity.this, ShowPathActivity.class);
                    intent.putExtra("LOC_DATA", str);
                    startActivity(intent);
                }
                break;
            case R.id.profileView:
                //profile사진 누르면 myPage로 이동
                intent = new Intent(MainActivity.this, MyPageActivity.class);
                startActivity(intent);
                break;
            case R.id.bookmarkButton:
                //즐찾 버튼 누르면 즐찾해놓은 경로 띄우기
                intent = new Intent(MainActivity.this, BookmarkActivity.class);
                startActivity(intent);
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
                if(FLAG == 1){
                    departure.setText(place.getName());
                    if (destination.getText() == "") {
                        return;
                    }
                }
                else if(FLAG == 2){
                    destination.setText(place.getName());}
                //길찾기 실행

                String[] str = new String[6];
                str[0] = new Double(latitude[0]).toString();
                str[1] = new Double(latitude[1]).toString();
                str[2] = new Double(longitude[0]).toString();
                str[3] = new Double(longitude[1]).toString();
                str[4] = departure.getText().toString();
                str[5] = destination.getText().toString();

                intent = new Intent(MainActivity.this, ShowPathActivity.class);
                intent.putExtra("LOC_DATA", str);
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
        startActivity(intent);
    }

    public void onExample3(View v){
        intent = new Intent(MainActivity.this, ReportActivity.class);
        startActivity(intent);
    }


}
