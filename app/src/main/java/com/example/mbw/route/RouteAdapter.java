package com.example.mbw.route;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbw.R;

import java.util.ArrayList;

//Item => Route, SubItem=> Item
public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private ArrayList<Route> routeList;


    public RouteAdapter(ArrayList<Route> routeList) {
        this.routeList = routeList;

    public class BusViewHolder extends RecyclerView.ViewHolder {


        protected TextView busStation, busNum, busStopId, busRemaining;
        protected ImageView busType;

        public BusViewHolder(@NonNull View view) {//constructor임
            //findViewById로 변수 정의
            //대충 이해가 갔는데 유동적으로 어떻게 표시하는지 모르겠음
            //긍까 지하철이랑 버스랑 어떻게 될지 모르는 상황. view 틀을 어떻게 만들어두고 어떻게 띄우지?
            super(view);
            this.busStation = view.findViewById(R.id.busStationText);  //firstStartStation
            this.busNum = view.findViewById(R.id.busNum);   //busNo
            this.busStopId = view.findViewById(R.id.stationId); //stationID
            this.busRemaining = view.findViewById(R.id.busRemaining); //
            this.busType = view.findViewById(R.id.busType); //type
        }
        private void setBusDetails(Route route) {
            busStation.setText(route.getBusStation());
            busNum.setText(route.getBusNum());
            busStopId.setText(route.getStationId());
            busRemaining.setText(route.getRemainingTime());
            switch (route.getBusType()){
                case 11:
                    busType.setImageResource(R.drawable.blue_bus);
                    break;
                case 12:
                    busType.setImageResource(R.drawable.green_bus);
                    break;
                case 3:
                    busType.setImageResource(R.drawable.town_bus);
                    break;
            }
        }
    }

    public class SubViewHolder extends RecyclerView.ViewHolder {


        protected TextView subStation, subRemaining;
        protected ImageView subImage;

        public SubViewHolder(@NonNull View view) {//constructor임
            //findViewById로 변수 정의
            //대충 이해가 갔는데 유동적으로 어떻게 표시하는지 모르겠음
            //긍까 지하철이랑 버스랑 어떻게 될지 모르는 상황. view 틀을 어떻게 만들어두고 어떻게 띄우지?
            super(view);
            /*
            this.subStation = view.findViewById(R.id.subStation); //stationName
            this.subRemaining = view.findViewById(R.id.subRemaining);
            this.subImage = view.findViewById(R.id.subImage);  //subwayCode

             */
        }
    //오디세이로부터 받아오긴하는데 안드에서 띄워줄 필요는 없는 정보는 어떻게 처리하지
        //일단 Route가 갖고있어야는되지 않나
        private void setSubDetails(Route route) {
            subStation.setText(route.getSubStation());
            subRemaining.setText(route.getRemainingTime());
            switch (route.getSubCode()){   //subwayCode
                case 1:
                    subImage.setImageResource(R.drawable.line1);
                    break;
                case 2:
                    subImage.setImageResource(R.drawable.line2);
                    break;
                case 3:
                    subImage.setImageResource(R.drawable.line3);
                    break;
                case 4:
                    subImage.setImageResource(R.drawable.line4);
                    break;
                case 5:
                    subImage.setImageResource(R.drawable.line5);
                    break;
                case 6:
                    subImage.setImageResource(R.drawable.line6);
                    break;
                case 7:
                    subImage.setImageResource(R.drawable.line7);
                    break;
                case 8:
                    subImage.setImageResource(R.drawable.line8);
                    break;
            }
        }
    }

    private ArrayList<Route> data;  //adapter는 viewholder로 변경할 data를 가지고 있는다

    

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_recycler_view, viewGroup, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder routeViewHolder, int i) {
        Route route = routeList.get(i);
        routeViewHolder.totalTime.setText(route.getTotalTime() + "분");
        routeViewHolder.walkingTime.setText("도보 " + route.getWalkingTime() + "분");
        routeViewHolder.cost.setText(route.getCost() + " 원");

        // Create layout manager with initial prefetch item count
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                routeViewHolder.rvItem.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(route.getItemList().size());

        // Create sub item view adapter
        ItemAdapter itemAdapter = new ItemAdapter(route.getItemList());

        routeViewHolder.rvItem.setLayoutManager(layoutManager);
        routeViewHolder.rvItem.setAdapter(itemAdapter);
        routeViewHolder.rvItem.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    class RouteViewHolder extends RecyclerView.ViewHolder {
        private TextView totalTime, walkingTime, cost;
        private RecyclerView rvItem;

        RouteViewHolder(View itemView) {    //layout에 보여주기
            super(itemView);
            rvItem = itemView.findViewById(R.id.rv_sub_item);
            totalTime = itemView.findViewById(R.id.totalTimeView);
            walkingTime = itemView.findViewById(R.id.walkingTimeView);
            cost = itemView.findViewById(R.id.costView);
        }
    }
}