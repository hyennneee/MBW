package com.example.mbw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Intent intent = null;
    EditText departure, destination;
    TextView toHome, toOffice;
    ImageView profile, swap, home, office, bookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        departure = findViewById(R.id.departureText);
        destination = findViewById(R.id.destinationText);

        /*toHome = findViewById(R.id.toHome);
        toOffice = findViewById(R.id.toOffice);

        profile = findViewById(R.id.profileView);
        swap = findViewById(R.id.swap);
        home = findViewById(R.id.houseButton);
        office = findViewById(R.id.officeButton);
        bookmark = findViewById(R.id.bookmarkButton);*/

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
                //swap 버튼 누르면 departure랑 destination "내용" 바뀌게
                String deptTmp = departure.getText().toString();
                String destTmp = destination.getText().toString();
                departure.setText(destTmp);
                destination.setText(deptTmp);
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

        }
    }

    public void onExample(View v){
        intent = new Intent(MainActivity.this, ShowPathActivity.class);
        startActivity(intent);
    }
}
