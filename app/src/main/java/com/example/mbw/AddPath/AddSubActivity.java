package com.example.mbw.AddPath;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import android.widget.ImageButton;

import com.example.mbw.R;

import java.util.ArrayList;
import java.util.List;

public class AddSubActivity extends AppCompatActivity {

    DataBaseHelper DBHelper;
    String selectedLine="01호선";
    List stationNameList = new ArrayList(); // seletedLine에 해당하는 역이름 리스트
    ArrayAdapter<String> adapter;

    ImageButton btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    ImageButton lastClicked ;
    AutoCompleteTextView startEditText, endEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sub);
        stationNameList = initLoadDB(selectedLine);
        // 리스트로 담고 있다가 보여주기
        startEditText = findViewById(R.id.startEditText);
        endEditText = findViewById(R.id.endEditText);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,stationNameList);
        startEditText.setAdapter(adapter);
        endEditText.setAdapter(adapter);

        btn1 = findViewById(R.id.imageButton1);
        btn2 = findViewById(R.id.imageButton2);
        btn3 = findViewById(R.id.imageButton3);
        btn4 = findViewById(R.id.imageButton4);
        btn5 = findViewById(R.id.imageButton5);
        btn6 = findViewById(R.id.imageButton6);
        btn7 = findViewById(R.id.imageButton7);
        btn8 = findViewById(R.id.imageButton8);
        btn9 = findViewById(R.id.imageButton9);

        lastClicked = btn1;
        btn1.setImageAlpha(50);
        btn2.setImageAlpha(50);
        btn3.setImageAlpha(50);
        btn4.setImageAlpha(50);
        btn5.setImageAlpha(50);
        btn6.setImageAlpha(50);
        btn7.setImageAlpha(50);
        btn8.setImageAlpha(50);
        btn9.setImageAlpha(50);


        ImageButton.OnClickListener onClickListener = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastClicked.setImageAlpha(50);
                switch (v.getId()){
                    case R.id.imageButton1:
                        selectedLine="01호선";
                        btn1.setImageAlpha(255);    //255 : 불투명
                        break;
                    case R.id.imageButton2:
                        selectedLine="02호선";
                        btn2.setImageAlpha(255);
                        break;
                    case R.id.imageButton3:
                        selectedLine="03호선";
                        btn3.setImageAlpha(255);
                        break;
                    case R.id.imageButton4:
                        selectedLine="04호선";
                        btn4.setImageAlpha(255);
                        break;
                    case R.id.imageButton5:
                        selectedLine="05호선";
                        btn5.setImageAlpha(255);
                        break;
                    case R.id.imageButton6:
                        selectedLine="06호선";
                        btn6.setImageAlpha(255);
                        break;
                    case R.id.imageButton7:
                        selectedLine="07호선";
                        btn7.setImageAlpha(255);
                        break;
                    case R.id.imageButton8:
                        selectedLine="08호선";
                        btn8.setImageAlpha(255);
                        break;
                    case R.id.imageButton9:
                        selectedLine="09호선";
                        btn9.setImageAlpha(255);
                        break;
                }
                lastClicked = findViewById(v.getId());
                stationNameList = initLoadDB(selectedLine);
                // 역이름 리스트 업데이트 위해서
                new AdapterHelper().update((ArrayAdapter)adapter, new ArrayList<Object>(stationNameList));
                adapter.notifyDataSetChanged();

            }
        };

        btn1.setOnClickListener(onClickListener);
        btn2.setOnClickListener(onClickListener);
        btn3.setOnClickListener(onClickListener);
        btn4.setOnClickListener(onClickListener);
        btn5.setOnClickListener(onClickListener);
        btn6.setOnClickListener(onClickListener);
        btn7.setOnClickListener(onClickListener);
        btn8.setOnClickListener(onClickListener);
        btn9.setOnClickListener(onClickListener);

    }


    public class AdapterHelper {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public void update(ArrayAdapter arrayAdapter, ArrayList<Object> listOfObject){
            arrayAdapter.clear();
            for (Object object : listOfObject){
                arrayAdapter.add(object);
            }
        }
    }

    public List initLoadDB(String selectedLine) {
        List stationNameList = new ArrayList();

        DataAdapter mDbHelper = new DataAdapter(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();

        // db에 있는 값들을 model을 적용해서 넣는다.
        stationNameList = mDbHelper.getTableData(selectedLine);
        Log.i("stationList[0] : %s", stationNameList.get(0).toString());

        // db 닫기
        mDbHelper.close();

        return stationNameList;
    }

    public void OnClickHandle(View view)
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("line", selectedLine);
        resultIntent.putExtra("start", startEditText.getText().toString());
        resultIntent.putExtra("end", endEditText.getText().toString());

        setResult(Code.subresultCode, resultIntent);
        finish();
    }


}
