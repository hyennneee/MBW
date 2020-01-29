package com.example.mbw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.mbw.myPageFragment.FragmentPath;
import com.example.mbw.myPageFragment.FragmentPlace;
import com.example.mbw.myPageFragment.FragmentSetting;

public class MyPageActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentPath fragmentPath;
    private FragmentPlace fragmentPlace;
    private FragmentSetting fragmentSetting;
    private FragmentTransaction transaction;
    private FrameLayout fragment;
    private TextView path, place, setting;
    private View first, second, third;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        fragmentManager = getSupportFragmentManager();

        fragmentPath = new FragmentPath();
        fragmentPlace = new FragmentPlace();
        fragmentSetting = new FragmentSetting();

        path = findViewById(R.id.pathView);
        place = findViewById(R.id.placeView);
        setting = findViewById(R.id.settingView);

        first = findViewById(R.id.firstLine);
        second = findViewById(R.id.secondLine);
        third = findViewById(R.id.thirdLine);

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.myPageFrame, fragmentPath).commitAllowingStateLoss();
    }

    public void onClickMyPage(View v){

    }

    public void myPageFragmentHandler(View v) {

        transaction = fragmentManager.beginTransaction();

        switch (v.getId()) {
            case R.id.pathView:
                transaction.replace(R.id.myPageFrame, fragmentPath).commitAllowingStateLoss();
                path.setTextColor(Color.parseColor("#1abc9c"));
                place.setTextColor(getResources().getColor(android.R.color.darker_gray));
                setting.setTextColor(getResources().getColor(android.R.color.darker_gray));

                first.setBackgroundColor(Color.parseColor("#1abc9c"));
                second.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                third.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                break;
            case R.id.placeView:
                transaction.replace(R.id.myPageFrame, fragmentPlace).commitAllowingStateLoss();

                path.setTextColor(getResources().getColor(android.R.color.darker_gray));
                place.setTextColor(Color.parseColor("#1abc9c"));
                setting.setTextColor(getResources().getColor(android.R.color.darker_gray));

                first.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                second.setBackgroundColor(Color.parseColor("#1abc9c"));
                third.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                break;
            case R.id.settingView:
                transaction.replace(R.id.myPageFrame, fragmentSetting).commitAllowingStateLoss();
                path.setTextColor(getResources().getColor(android.R.color.darker_gray));
                place.setTextColor(getResources().getColor(android.R.color.darker_gray));
                setting.setTextColor(Color.parseColor("#1abc9c"));

                first.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                second.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                third.setBackgroundColor(Color.parseColor("#1abc9c"));
                break;
        }
    }

    public void onClickBack(View v){
        finish();
    }
}
