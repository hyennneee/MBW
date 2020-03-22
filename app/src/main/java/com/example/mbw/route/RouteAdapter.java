package com.example.mbw.route;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbw.MainActivity;
import com.example.mbw.MyPage.data.PostResponse;
import com.example.mbw.network.RetrofitClient;
import com.example.mbw.network.ServiceApi;
import com.example.mbw.showPath.LikeNum;
import com.example.mbw.showPath.ShowPathActivity;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.example.mbw.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    ServiceApi service;

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
        service = RetrofitClient.getClient().create(ServiceApi.class);

        startUpdateTimer();
    }
    public void stopTimer(){
        if(tmr != null)
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

        int group = route.getGroup();
        switch (group) {
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
            case 5:
                routeViewHolder.routeType.setImageResource(R.drawable.danger);
                break;
        }

        if(group == 2 || group == 3){
            int likeNum = route.getLikedNum();
            routeViewHolder.likedNum.setText("" + likeNum);
        }
        else{
            routeViewHolder.likeBtn.setVisibility(View.INVISIBLE);
            //routeViewHolder.likeBtn.setClickable(false);
            routeViewHolder.likedNum.setVisibility(View.INVISIBLE);
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

    public class RouteViewHolder extends RecyclerView.ViewHolder{
        private TextView totalTime, walkingTime, cost, likedNum, rvBtn;
        private RecyclerView rvItem;
        private ImageView routeType, likeBtn;
        OnItemClickListener onItemClickListener;
        View v;

        RouteViewHolder(View itemView, OnItemClickListener onItemClickListener) {    //layout에 보여주기
            super(itemView);
            v = itemView;
            rvItem = itemView.findViewById(R.id.rv_sub_item);
            totalTime = itemView.findViewById(R.id.totalTimeView);
            walkingTime = itemView.findViewById(R.id.walkTime);
            cost = itemView.findViewById(R.id.costView);
            routeType = itemView.findViewById(R.id.routeType);
            likedNum = itemView.findViewById(R.id.likedNumber);
            likeBtn = itemView.findViewById(R.id.likeButton);
            rvBtn = itemView.findViewById(R.id.rvButton);
            this.onItemClickListener = onItemClickListener;
            rvBtn.bringToFront();


            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String token = MainActivity.getToken();
                    LikeNum likeNum = new LikeNum(routeList.get(getAdapterPosition()).getMyPathIdx(), 1);
                    pressLike(v, token, likeNum, getAdapterPosition());
                    Log.e("좋아요 버튼 클릭", "" + routeList.get(getAdapterPosition()).getMyPathIdx());
                }
            });

            rvBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    mListener.onItemClick(getAdapterPosition());
                }
            });

            //rvItem.setOnClickListener(this);
            //itemView.setOnClickListener(this);
        }
        /*
        *
        @Override
        public void onClick(View view){
            onItemClickListener.onItemClick(getAdapterPosition());
        }*/

        //MainActivity.getToken();
        public void pressLike(View v, String token, LikeNum likeNum, int position) {

            service.setLikeNum("application/json", token, likeNum).enqueue(new Callback<PostResponse>() {
                @Override
                public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                    PostResponse result = response.body();
                    boolean success = response.isSuccessful();
                    if(success) {
                        int code = result.getCode();
                        if (code == 200) {   //좋아요 누르기 성공
                            int liked = routeList.get(position).getLikedNum();
                            routeList.get(position).setLikedNum(++liked);
                            Toast.makeText(v.getContext(), "좋아요 누르기 성공", Toast.LENGTH_SHORT).show();
                            likedNum.setText("" + liked);
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
                public void onFailure(Call<PostResponse> call, Throwable t) {
                    Toast.makeText(v.getContext(), "좋아요 에러 발생", Toast.LENGTH_SHORT).show();
                    Log.e("좋아요 에러 발생", t.getMessage());
                }
            });
        }
        public void setError(Response<PostResponse> response) throws IOException {
            Gson gson = new Gson();
            PostResponse result = gson.fromJson(response.errorBody().string(), PostResponse.class);
            Toast.makeText(itemView.getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //notifyDataSetChanged를 통해 itemAdapter에 신호 보내고 각 item의 position이 0인 애들의 time 변환
        public void updateTimeRemaining(){
            ItemAdapter adapter = (ItemAdapter) rvItem.getAdapter();
            adapter.startUpdateTimer();
        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position) ;
    }
}