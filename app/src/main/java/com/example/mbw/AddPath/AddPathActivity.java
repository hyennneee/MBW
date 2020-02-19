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
import com.example.mbw.route.DetailItem;

import java.util.ArrayList;

// 지하철 :
// 버스 :
//

public class AddPathActivity extends AppCompatActivity {


    private ArrayList<AddItem> addItemList = new ArrayList<AddItem>();
    Intent intent = null;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_path);

        recyclerView = findViewById(R.id.addPath_rv) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent)
    {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        if(requestCode == Code.requestCode && resultCode == Code.subresultCode) { // 지하철 경로 추가 되었을때
//            textView_name.setText(resultIntent.getStringExtra("name"));
            String selectedLine = resultIntent.getStringExtra("line");
            String startName = resultIntent.getStringExtra("start");
            String endName = resultIntent.getStringExtra("end");

            addItemList.add(new AddItem(selectedLine, startName, endName));
        }

        else if(requestCode == Code.requestCode && resultCode == Code.busresultCode) { // 버스 경로 추가 되었을때

        // TO DO : 버스 정보 받아서 추가하기
        }
        AddItemAdapter adapter = new AddItemAdapter(addItemList, this);
        recyclerView.setAdapter(adapter);

    }

    public void addSubItem(View v){
        intent = new Intent(this, AddSubActivity.class);
        startActivityForResult(intent, Code.requestCode);

    }

    public void addBusItem(View v){
        intent = new Intent(AddPathActivity.this, AddBusActivity.class);
        startActivity(intent);
    }




}