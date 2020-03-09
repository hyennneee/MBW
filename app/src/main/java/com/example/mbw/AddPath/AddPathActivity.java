package com.example.mbw.AddPath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mbw.AddPath.AddItem;
import com.example.mbw.DetailPathActivity;
import com.example.mbw.MainActivity;
import com.example.mbw.R;

import java.util.ArrayList;

public class AddPathActivity extends AppCompatActivity {


    private ArrayList<AddItem> addItemList;
    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_path);

        RecyclerView recyclerView = findViewById(R.id.addPath_rv) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

    }

    public void addSubItem(View v){
        intent = new Intent(AddPathActivity.this, AddSubActivity.class);
        startActivity(intent);
    }

    public void addBusItem(View v){
        intent = new Intent(AddPathActivity.this, AddBusActivity.class);
        startActivity(intent);
    }


}