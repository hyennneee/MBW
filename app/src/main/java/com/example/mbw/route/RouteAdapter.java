package com.example.mbw.route;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.JsonObject;

import com.example.mbw.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//Item => Route, SubItem=> Item
public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private ArrayList<Route> routeList;
    public static JsonObject detailPathData;
    private OnItemClickListener mListener;
    private Handler handler = new Handler();
    private ArrayList<RecyclerView.ViewHolder> viewHoldersList;
    private ArrayList<Integer> positionList;
    Timer tmr;

    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (viewHoldersList) {
                for (RecyclerView.ViewHolder holder : viewHoldersList) {
                    ((RouteViewHolder) holder).updateTimeRemaining();
                }
            }
        }
    };

    public RouteAdapter(ArrayList<Route> routeList, OnItemClickListener onItemClickListener) {
        this.routeList = routeList;
        this.mListener = onItemClickListener;
        viewHoldersList = new ArrayList<>();
        positionList = new ArrayList<>();
        startUpdateTimer();
    }
    public void stopTimer(){
        this.tmr.cancel();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    private void startUpdateTimer() {
        tmr = new Timer();
        tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(updateRemainingTimeRunnable);
            }
        }, 1000, 1000);
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_recycler_view, viewGroup, false);
        return new RouteViewHolder(view, mListener);
    }

    /*public void setTime(int time, String state, RouteViewHolder routeViewHolder){
        ItemAdapter adapter = (ItemAdapter) routeViewHolder.rvItem.getAdapter();
        (ItemAdapter.BusViewHolder) adapter.getItemViewHolder();

        ((ItemAdapter.BusViewHolder) holder).updateTimeRemaining();
        if(time != 0){  //남은 시간이 숫자


        }
        else{   //남은 시간이 문자

        }
    }*/

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder routeViewHolder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(routeViewHolder, position, payloads);
        } else {
            Bundle bundle = (Bundle) payloads.get(0);
            if (bundle.size() != 0) {
                //ToDo: position == 0의 item의 시간을 update
                int time = bundle.getInt("time");
                String state = bundle.getString("currState");
                //setTime(time, state, routeViewHolder);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder routeViewHolder, int position) {
        Route route = routeList.get(position);
        //routeViewHolder가 Null이라고 함
        int time, h, m;
        time = route.getTotalTime();
        String total = "";
        h = time / 60;
        m = time % 60;

        if (h != 0)
            total = "" + h + "시간 ";
        if (m != 0)
            total += "" + m + "분";

        routeViewHolder.totalTime.setText(total);
        routeViewHolder.walkingTime.setText("도보 " + route.getWalkingTime() + "분");
        if (route.getCost() == 0)
            routeViewHolder.cost.setText("");
        else
            routeViewHolder.cost.setText(route.getCost() + "원");
        switch (route.getGroup()) {
            case 1:
                routeViewHolder.routeType.setImageResource(R.drawable.basic);
                break;
            case 2:
            case 3:
                routeViewHolder.routeType.setImageResource(R.drawable.user);
                break;
            case 4:
                routeViewHolder.routeType.setImageResource(R.drawable.detour);
                break;
        }
        synchronized (viewHoldersList) {
            //중복되는 viewHolder는 저장하지 않게
            for (int pos : positionList) {
                if (pos == position) //이미 저장된 viewholder면 return
                    return;
            }
            positionList.add(position);
            viewHoldersList.add(routeViewHolder);
        }

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

    public class RouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView totalTime, walkingTime, cost;
        private RecyclerView rvItem;
        private ImageView routeType;
        OnItemClickListener onItemClickListener;

        RouteViewHolder(View itemView, OnItemClickListener onItemClickListener) {    //layout에 보여주기
            super(itemView);
            rvItem = itemView.findViewById(R.id.rv_sub_item);
            totalTime = itemView.findViewById(R.id.totalTimeView);
            walkingTime = itemView.findViewById(R.id.walkTime);
            cost = itemView.findViewById(R.id.costView);
            routeType = itemView.findViewById(R.id.routeType);
            this.onItemClickListener = onItemClickListener;

            itemView.setOnClickListener(this);
            rvItem.setOnClickListener(this);
        }

        //notifyDataSetChanged를 통해 itemAdapter에 신호 보내고 각 item의 position이 0인 애들의 time 변환
        public void updateTimeRemaining(){
            ItemAdapter adapter = (ItemAdapter) rvItem.getAdapter();
            adapter.startUpdateTimer();
        }
        @Override
        public void onClick(View view){
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position) ;
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }
}