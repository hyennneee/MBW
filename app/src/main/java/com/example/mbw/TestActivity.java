package com.example.mbw;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class TestActivity extends AppCompatActivity{
    TextView tv;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        tv = findViewById(R.id.testView);

        String jsonString[] = getIntent().getStringArrayExtra("DETAIL_PATH");
        String tmp = "";
        tv.setText("x: "+ jsonString[3] + ", y: " + jsonString[4] + ", x: " + jsonString[5] + ", y: " + jsonString[6]+"\n"+jsonString[0]);
        /*JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject)jsonParser.parse(jsonString);*/
    }
}
