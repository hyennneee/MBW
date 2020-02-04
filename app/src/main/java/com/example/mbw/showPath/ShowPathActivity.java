package com.example.mbw.showPath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mbw.R;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


public class ShowPathActivity extends AppCompatActivity {
    TextView departure, destination;

    private FragmentAll fragmentAll;
    private FragmentBus fragmentBus;
    private FragmentSub fragmentSub;
    private FragmentBusNSub fragmentBusNSub;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private TextView all, bus, sub, busNsub, exTV;
    private View first, second, third, fourth;
    public static JSONObject jsonObject;

    private int searchType, FLAG = 0, AUTOCOMPLETE_REQUEST_CODE = 1;;
    private double longitude[] = new double[2], latitude[] = new double[2];
    private Intent searchIntent = null;

    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);


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

        exTV = findViewById(R.id.fragA);

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.showPathframe, fragmentAll).commitAllowingStateLoss();
        String []strings;
        strings = getIntent().getStringArrayExtra("LOC_DATA");
        latitude[0] = Double.parseDouble(strings[0]);
        latitude[1] = Double.parseDouble(strings[1]);
        longitude[0] = Double.parseDouble(strings[2]);
        longitude[1] = Double.parseDouble(strings[3]);
        departure.setText(strings[4]);
        destination.setText(strings[5]);

        OdsayAPi(longitude[0], latitude[0], longitude[1], latitude[1], 0);
    }

    public void onClickShowPath(View v){
        switch (v.getId()){
            case R.id.star:
                //즐겨찾는 경로에 추가
                break;
            case R.id.swap:
                //swap 버튼 누르면 departure랑 destination "내용" 바뀌게
                String deptTmp = departure.getText().toString();
                String destTmp = destination.getText().toString();
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
                OdsayAPi(longitude[0], latitude[0], longitude[1], latitude[1], 0);
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
                }
                else if(FLAG == 2){
                    destination.setText(place.getName());}
                //길찾기 실행
                OdsayAPi(longitude[0], latitude[0], longitude[1], latitude[1], 0);

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

        transaction = fragmentManager.beginTransaction();

        switch (v.getId()) {
            case R.id.showAll:
                transaction.replace(R.id.showPathframe, fragmentAll).commitAllowingStateLoss();
                all.setTextColor(Color.parseColor("#1ABC9C"));
                bus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                sub.setTextColor(getResources().getColor(android.R.color.darker_gray));
                busNsub.setTextColor(getResources().getColor(android.R.color.darker_gray));

                first.setBackgroundColor(Color.parseColor("#1abc9c"));
                second.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                third.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                fourth.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                break;
            case R.id.showBus:
                transaction.replace(R.id.showPathframe, fragmentBus).commitAllowingStateLoss();

                all.setTextColor(getResources().getColor(android.R.color.darker_gray));
                bus.setTextColor(Color.parseColor("#1abc9c"));
                sub.setTextColor(getResources().getColor(android.R.color.darker_gray));
                busNsub.setTextColor(getResources().getColor(android.R.color.darker_gray));

                first.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                second.setBackgroundColor(Color.parseColor("#1abc9c"));
                third.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                fourth.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                break;
            case R.id.showSub:
                transaction.replace(R.id.showPathframe, fragmentSub).commitAllowingStateLoss();
                all.setTextColor(getResources().getColor(android.R.color.darker_gray));
                bus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                sub.setTextColor(Color.parseColor("#1abc9c"));
                busNsub.setTextColor(getResources().getColor(android.R.color.darker_gray));

                first.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                second.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                third.setBackgroundColor(Color.parseColor("#1abc9c"));
                fourth.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                break;

            case R.id.showBusNSub:
                transaction.replace(R.id.showPathframe, fragmentBusNSub).commitAllowingStateLoss();
                all.setTextColor(getResources().getColor(android.R.color.darker_gray));
                bus.setTextColor(getResources().getColor(android.R.color.darker_gray));
                sub.setTextColor(getResources().getColor(android.R.color.darker_gray));
                busNsub.setTextColor(Color.parseColor("#1abc9c"));

                first.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                second.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                third.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                fourth.setBackgroundColor(Color.parseColor("#1abc9c"));
                break;
        }
    }

    public void onBackClick(View v){
        finish();
    }

    public void OdsayAPi(Double startlng, Double startlat, Double endlng, Double endlat, int i) {
        searchType = i; // 이동방법: 0 모두(all, subNbus) 1 지하철(sub) 2 버스(bus)
        String type = new Integer(i).toString();
        ODsayService odsayService;
        odsayService = ODsayService.init(getApplicationContext(), Resources.getSystem().getString(R.string.odsay_key));
        odsayService.setReadTimeout(5000);
        odsayService.setConnectionTimeout(5000);
// 서버 통신
        odsayService.requestSearchPubTransPath(Double.toString(startlng), Double.toString(startlat), Double.toString(endlng),Double.toString(endlat), "1", type, "0", onResultCallbackListener);
    }

    private OnResultCallbackListener onResultCallbackListener = new OnResultCallbackListener() {
        // 호출 성공시 데이터 들어옴
        @Override
        public void onSuccess(ODsayData odsayData, API api) {

            // API Value 는 API 호출 메소드 명을 따라갑니다.
            if (api == API.SEARCH_PUB_TRANS_PATH) {
                //이 코드가 없어서 null pointer exception error
                jsonObject = odsayData.getJson();

                //transportation(jsonObject);
                //Log.d("Station name : %s", stationName);
            }

        }
        // 호출 실패 시 실행
        @Override
        public void onError(int i, String errorMessage, API api) {
            if (api == API.BUS_STATION_INFO) {
                Log.i("SearchAPi",errorMessage);}
        }
    };

}
