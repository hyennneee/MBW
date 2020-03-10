package com.example.mbw.AddPath;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.mbw.MainActivity;
import com.example.mbw.R;
import com.example.mbw.addPathData.addPathResponse;
import com.example.mbw.problemArea.problemAreaData;
import com.example.mbw.problemArea.problemAreaResponse;

import com.example.mbw.network.RetrofitClient;
import com.example.mbw.network.ServiceApi;
import com.example.mbw.problemArea.problemAreaResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* 사용자 문제 구역 제보 -> 서버 json 전송*/
public class ReportActivity extends AppCompatActivity {

    ServiceApi service;
    DataBaseHelper DBHelper;
    String selectedLine = "01호선";
    List stationNameList = new ArrayList(); //seletedLine에 해당하는 역이름 리스트
    ArrayAdapter<String> adapter;

    ImageButton btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    ImageButton lastClicked ;
    AutoCompleteTextView stationName;
    ToggleButton tg1, tg2;
    LinearLayout directionLayout, exitLayout;
    EditText directionName, exitNum;
    String startId, endId;
    DataAdapter mDbHelper;

    // 서버에 전송
    JSONObject obj;
    int line = 0;
    String Name;
    int transfer;
    String nextStation;
    String exitNo;
    int problemType;
    String resultMessage;
    String problem = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        stationNameList = initLoadDB(selectedLine);
        // 리스트로 담고 있다가 보여주기
        stationName = findViewById(R.id.stationName);
        tg1 = findViewById(R.id.toggleButton3);
        tg2 = findViewById(R.id.toggleButton4);
        directionLayout = findViewById(R.id.directionLayout);
        directionName = findViewById(R.id.directionName);
        exitLayout = findViewById(R.id.exitLayout);
        exitNum = findViewById(R.id.exitNumber);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,stationNameList);
        stationName.setAdapter(adapter);

        service = RetrofitClient.getClient().create(ServiceApi.class);

        RadioGroup rg = (RadioGroup)findViewById(R.id.problemType);

        tg1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //toggleButton.setChecked(false);
                    Log.e("1 selected", "o");
                    transfer = 1;
                    directionLayout.setVisibility(View.VISIBLE);
                } else {
                    //toggleButton.setChecked(true);
                    transfer = 0;
                    directionLayout.setVisibility(View.INVISIBLE);

                }
            }
        });

        tg2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //toggleButton.setChecked(false);
                    Log.e("0 selected", "x");
                    transfer = 0;
                    directionLayout.setVisibility(View.INVISIBLE);
                } else {
                    transfer = 0;
                    directionLayout.setVisibility(View.INVISIBLE);
                }
            }
        });


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radioButton1:
                        problemType=1;
                        exitLayout.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radioButton2:
                        problemType=2;
                        exitLayout.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radioButton3:
                        problemType=3;
                        exitLayout.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.radioButton4:
                        problemType=4;
                        exitLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        problemType=0;
                        exitLayout.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        });


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
                        line=1;
                        btn1.setImageAlpha(255);    //255 : 불투명
                        break;
                    case R.id.imageButton2:
                        selectedLine="02호선";
                        line=2;
                        btn2.setImageAlpha(255);
                        break;
                    case R.id.imageButton3:
                        selectedLine="03호선";
                        line=3;
                        btn3.setImageAlpha(255);
                        break;
                    case R.id.imageButton4:
                        selectedLine="04호선";
                        line=4;
                        btn4.setImageAlpha(255);
                        break;
                    case R.id.imageButton5:
                        selectedLine="05호선";
                        line=5;
                        btn5.setImageAlpha(255);
                        break;
                    case R.id.imageButton6:
                        selectedLine="06호선";
                        line=6;
                        btn6.setImageAlpha(255);
                        break;
                    case R.id.imageButton7:
                        selectedLine="07호선";
                        line=7;
                        btn7.setImageAlpha(255);
                        break;
                    case R.id.imageButton8:
                        selectedLine="08호선";
                        line=8;
                        btn8.setImageAlpha(255);
                        break;
                    case R.id.imageButton9:
                        selectedLine="09호선";
                        line=9;
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

        mDbHelper = new DataAdapter(getApplicationContext());
        mDbHelper.createDatabase();
        mDbHelper.open();

        // db에 있는 값들을 model을 적용해서 넣는다.
        stationNameList = mDbHelper.getTableData(selectedLine);
        Log.i("stationList[0] : %s", stationNameList.get(0).toString());

        // db 닫기
        return stationNameList;
    }

    public void OnClickHandle(View view)
    {
        mDbHelper.close();
        Log.i("onclick시","Onclick 완료");
        Name = stationName.getText().toString();
        nextStation = directionName.getText().toString();
        makeReportJSON(); // json 파일 만들기
        sendProblemArea(new problemAreaData(line, Name, transfer, nextStation, exitNo, problemType, problem));
    }

    public void makeReportJSON(){
        obj = new JSONObject();
        try{
            obj.put("subwayCode", line);
            obj.put("stationName", Name);
            obj.put("transfer", transfer);

            if(transfer == 1) obj.put("nextStation", nextStation);
            else {nextStation = null; obj.put("nextStation",JSONObject.NULL);}

            if(problemType == 4){
                exitNo = exitNum.getText().toString();
                obj.put("endExitNo", exitNo);
            }
            else {exitNo = "전체"; obj.put("endExitNo", "전체");}

            obj.put("problemNo", problemType);

            switch (problemType){
                case 1:
                    problem = getString(R.string.problem1);
                    break;
                case 2:
                    problem = getString(R.string.problem2);
                    break;
                case 3:
                    problem = getString(R.string.problem3);
                    break;
                case 4:
                    problem = getString(R.string.problem4);
                    break;
            }

            obj.put("problem", problem);

        }catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("JSON Test", obj.toString());
    }

    private void sendProblemArea(problemAreaData data){
        service.reportProblem("application/json", data).enqueue(new Callback<problemAreaResponse>() {
            @Override
            public void onResponse(Call<problemAreaResponse> call, Response<problemAreaResponse> response) {
                problemAreaResponse result = response.body();

                boolean success = response.isSuccessful();
                Log.i("successfdggdf", success+"");

                if(success) {
                    int code = result.getCode();

                    if (code == 200) {   // 서버 전송 성공
                        try {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(ReportActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
                else {
                    try {
                        setError(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<problemAreaResponse> call, Throwable t) {
                Toast.makeText(ReportActivity.this, "경로 추가 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("위험 지역 제보하기", t.getMessage());

            }
        });
    }

    public void setError(Response<problemAreaResponse> response) throws IOException {
        Gson gson = new Gson();
        problemAreaResponse result = gson.fromJson(response.errorBody().string(), problemAreaResponse.class);
        int code = result.getCode();

        if (code == 500) {   // 서버 내부 오류
            Toast.makeText(ReportActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show(); //오류메시지 출력
            Log.i("500", result.getMessage());
        }
        else if(code == 400){  // 문제 제보 작성 실패, 파라미터 값 부족 (ex transfer 값 안 넣었을 때)
            Log.i("400", result.getMessage());
        }

        Toast.makeText(ReportActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show(); //오류메시지 출력
        Log.i("etc", result.getMessage());
    }


}
