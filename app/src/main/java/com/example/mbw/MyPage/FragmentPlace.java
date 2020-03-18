package com.example.mbw.MyPage;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mbw.DB.PlaceDB;
import com.example.mbw.DB.PlaceDBHelper;
import com.example.mbw.MainActivity;
import com.example.mbw.MyPage.data.LocationResponse;
import com.example.mbw.MyPage.data.PostResponse;
import com.example.mbw.R;
import com.example.mbw.network.RetrofitClient;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.mbw.network.ServiceApi;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FragmentPlace extends Fragment {

    TextView home, office, editHome, editOffice;
    final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private int FLAG = 0;
    Intent searchIntent = null;
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
    String name, token, homeAddress = "", officeAddress = "";
    ServiceApi service;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_place, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        home = (TextView) view.findViewById(R.id.homeTV);
        office = (TextView) view.findViewById(R.id.officeTV);
        token = MainActivity.getToken();
        Log.i("FragToken", token);
        service = RetrofitClient.getClient().create(ServiceApi.class);
        home.setText(MyPageActivity.homeAddress);
        office.setText(MyPageActivity.officeAddress);

        //placeDBHelper = new PlaceDBHelper(view.getContext());
        //ArrayList<PlaceDB> db = placeDBHelper.getAllPlaces();
        /*for(PlaceDB value : db){
            String text = value.getPlace();
            if(value.getCategory() == 1)
                home.setText(text);
            else office.setText(text);
        }*/
        editHome = (TextView) view.findViewById(R.id.editHome);
        editOffice = (TextView) view.findViewById(R.id.editOffice);

        editHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLAG = 1;
                searchPlace(view);
            }
        });
        editOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FLAG = 2;
                searchPlace(view);
            }
        });
    }


    public void searchPlace(View v){
        searchIntent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(v.getContext());
        startActivityForResult(searchIntent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                int category = FLAG;
                String x, y;    //y, x
                name = place.getName();
                x = Double.toString(place.getLatLng().longitude);
                y = Double.toString(place.getLatLng().latitude);

                if(FLAG == 1) {   //home
                    home.setText(name);
                }
                else{
                    office.setText(name);
                }
                //ToDo: 서버와 통신
                String token = MainActivity.getToken();
                setLocation(token, new PlaceDB(category, name, x, y));
                //placeDBHelper.insertPlace(new PlaceDB(category, name, x, y));

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                //Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
    public void setLocation(String token, PlaceDB placeDB) {
        service.addLocation(token, placeDB).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                PostResponse result = response.body();
                boolean success = response.isSuccessful();
                if(success) {
                    int code = result.getCode();
                    if (code == 200) {   //즐겨찾는 장소 등록 성공
                        Toast.makeText(getContext(), "등록 성공!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    try {
                        Gson gson = new Gson();
                        result = gson.fromJson(response.errorBody().string(), PostResponse.class);
                        Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Toast.makeText(getContext(), "통신 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("통신 에러 발생", t.getMessage());
            }
        });
    }
}