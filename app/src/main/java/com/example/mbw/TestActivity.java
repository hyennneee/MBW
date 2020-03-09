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

        String jsonString = getIntent().getStringExtra("DETAIL_PATH");
        tv.setText(jsonString);
        /*JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject)jsonParser.parse(jsonString);*/
    }
}
