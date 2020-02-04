package com.example.mbw.showPath;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbw.R;
import com.example.mbw.route.Item;
import com.example.mbw.route.Route;
import com.example.mbw.route.RouteAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.mbw.showPath.ShowPathActivity.jsonObject;

//ShowPathActivity - odsayapi호출, Fragment에서 return
public class FragmentSub extends Fragment {

    private ArrayList<Route> routeArrayList;
    private RouteAdapter mAdapter;
    private int count = -1,searchType, subLine, busType, totalTime, totalWalk, cost, index;
    private String stationName, remainingTime,  busNum, stationNo, finalStation;
    private boolean non_step;

    //1-지하철, 2-버스, 3-버스+지하철
    //api에서 받아온 결과를 어떻게 arrayList에 넣지?


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView mRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview_main_list);

        transportation(jsonObject);
        routeArrayList = new ArrayList<Route>();    //mArrayList의 내용을 채워야돼
        mAdapter = new RouteAdapter( routeArrayList);
        mRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);



        return inflater.inflate(R.layout.fragment_sub, container, false);
    }



    // 이동방법 파싱
    private void transportation(JSONObject jsonObject) {   //여기서 mArrayList의 내용 채우기 - route 내용 채우기
        try{
            JSONObject result = jsonObject.getJSONObject("result");
            JSONArray pathArray = result.getJSONArray("path");
// pathArray 안의 경로 갯수
            int pathArrayCount = pathArray.length();

            for(int a = 0; a<pathArrayCount; a++) { //경로 개수만큼
                JSONObject pathArrayDetailOBJ = pathArray.getJSONObject(a);
                ArrayList<Item> itemArrayList = new ArrayList<Item>();
// 경로 타입 1 지하철 2 버스 3도보
                int pathType = pathArrayDetailOBJ.getInt("pathType");
                if( pathType == 1){ //지하철
                    JSONObject infoOBJ = pathArrayDetailOBJ.getJSONObject("info");
                    totalWalk = infoOBJ.getInt("totalWalk"); // 총 도보 이동거리
                    cost = infoOBJ.getInt("payment"); // 요금
                    totalTime = infoOBJ.getInt("totalTime"); // 소요시간
                    String mapObj = infoOBJ.getString("mapObj"); // 경로 디테일 조회 아이디
                    String firstStartStation = infoOBJ.getString("firstStartStation"); // 출발 정거장
                    String lastEndStation = infoOBJ.getString("lastEndStation"); // 도착 정거장

// 세부경로 디테일
                    JSONArray subPathArray = pathArrayDetailOBJ.getJSONArray("subPath");
                    int subPathArraycount = subPathArray.length();
// 반환 데이터 스트링으로

                    int busID=0;
                    /*for(int b = 0; b<subPathArraycount; b++){
                        JSONObject subPathOBJ = subPathArray.getJSONObject(b);
                        int Type = subPathOBJ.getInt("trafficType"); // 이동방법
                        switch (Type){
                            case 1:
                                routedetail += "지하철 ";
                                break;
                            case 2:
                                routedetail += "버스 ";
                                break;
                            default:
                                routedetail += "도보 ";
                                break;
                        }
// 버스 또는 지하철 이동시에만
                        if(Type == 1 || Type ==2){
                            String startName = subPathOBJ.getString("startName"); // 출발지
                            routedetail += startName+" 에서 ";
                            String endName = subPathOBJ.getString("endName"); // 도착지
                            routedetail += endName;
// 버스및 지하철 정보 가져옴 (정보가 많으므로 array로 가져오기)
                            JSONArray laneObj = subPathOBJ.getJSONArray("lane");
                            if(Type == 1 ){ // 지하철
                                String subwayName = laneObj.getJSONObject(0).getString("name"); // 지하철 정보(몇호선)
                                routedetail += subwayName+" 탑승 ";
                            }
                            if(Type == 2 ) { // 버스..
                                String busNo = laneObj.getJSONObject(0).getString("busNo"); // 버스번호정보
                                String busroute = " ["+busNo+ "] 번 탑승 ";
                                routedetail += busroute;
                                busID = laneObj.getJSONObject(0).getInt("busID"); // 버스정류장 id
                            }
                        }
                        int distance = subPathOBJ.getInt("distance"); // 이동길이
                        routedetail += "\n( "+Integer.toString(distance)+"m 이동. ";
                        int sectionTime = subPathOBJ.getInt("sectionTime"); // 이동시간
                        routedetail += Integer.toString(sectionTime)+"분 소요 )\n";
                        totalTime += sectionTime ;
////////////////////////////////////////////////////////////addlist 넣기!!! 한줄마다 listview설정하기
                        //String stationName, remainingTime,  busNum, stationNo, finalStation;
                        //int subLineImage, busType;
                        //boolean non_step;
                        Item item = new Item();
                        itemArrayList.add((item));

                    } // 세부경로 종료*/
                    //routedetail += "총" + Integer.toString(totalTime) + "분 소요\n " ;
// api 경로 좌표 요청
                    //OdsayAPiroute(mapObj);
// 화면에 버스 및 지하철 경로 출력
                   // Dialogview();
                    break;
                }

                //Route: totalTime, walkingTime, cost
                //Route route = new Route(, itemArrayList);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
