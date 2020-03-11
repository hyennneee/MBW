package com.example.mbw.myPageFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mbw.DB.DBvalue;
import com.example.mbw.DB.PlaceDB;
import com.example.mbw.DB.PlaceDBHelper;
import com.example.mbw.DB.RouteDBHelper;
import com.example.mbw.MainActivity;
import com.example.mbw.MyPageActivity;
import com.example.mbw.R;
import com.example.mbw.showPath.ShowPathActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class FragmentPlace extends Fragment {

    TextView home, office, editHome, editOffice;
    PlaceDBHelper placeDBHelper;
    final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private int FLAG = 0;
    Intent searchIntent = null;
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
    String name;

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
        placeDBHelper = new PlaceDBHelper(view.getContext());
        ArrayList<PlaceDB> db = placeDBHelper.getAllPlaces();
        for(PlaceDB value : db){
            String text = value.getPlace();
            if(value.getName().equals("home"))
                home.setText(text);
            else office.setText(text);
        }
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
    public void onClickEdit(View v){
        //편집 버튼 눌렀을 때
        /*
        * google maps search 뜨게 해
        * 검색해서 받은 x, y, name DB에 저장
        * place=home or office로
        * setTextView
        * */
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
                String object, x, y;    //y, x
                name = place.getName();
                x = Double.toString(place.getLatLng().longitude);
                y = Double.toString(place.getLatLng().latitude);

                if(FLAG == 1) {   //home
                    object = "home";
                    home.setText(name);
                }
                else{
                    object = "office";
                    office.setText(name);
                }
                placeDBHelper.insertPlace(new PlaceDB(object, name, x, y));

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