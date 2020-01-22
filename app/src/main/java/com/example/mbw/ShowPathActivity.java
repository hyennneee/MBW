package com.example.mbw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.example.mbw.showPathFragment.FragmentAll;
import com.example.mbw.showPathFragment.FragmentBus;
import com.example.mbw.showPathFragment.FragmentBusNSub;
import com.example.mbw.showPathFragment.FragmentSub;

public class ShowPathActivity extends AppCompatActivity {
    EditText departure, destination;

    private FragmentAll fragmentAll;
    private FragmentBus fragmentBus;
    private FragmentSub fragmentSub;
    private FragmentBusNSub fragmentBusNSub;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private TextView all, bus, sub, busNsub;
    private View first, second, third, fourth;
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

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.showPathframe, fragmentAll).commitAllowingStateLoss();
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
                break;
        }
    }

    public void showPathFragmentHandler(View v) {

        transaction = fragmentManager.beginTransaction();

        switch (v.getId()) {
            case R.id.showAll:
                transaction.replace(R.id.showPathframe, fragmentAll).commitAllowingStateLoss();
                all.setTextColor(Color.parseColor("#1abc9c"));
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
}
