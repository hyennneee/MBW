package com.example.mbw.route;

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
import com.example.mbw.showPath.ShowPathActivity;

import java.util.ArrayList;

//Item => Route, SubItem=> Item
public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private ArrayList<Route> routeList;
    public static JsonObject detailPathData;
    private OnItemClickListener mListener;

    public RouteAdapter(ArrayList<Route> routeList, OnItemClickListener onItemClickListener) {
        this.routeList = routeList;
        this.mListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_recycler_view, viewGroup, false);
        return new RouteViewHolder(view, mListener);
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

        if(h != 0)
            total = "" + h + "시간 ";
        if(m != 0)
            total += "" + m + "분";

        routeViewHolder.totalTime.setText(total);
        routeViewHolder.walkingTime.setText("도보 " + route.getWalkingTime() + "분");
        if(route.getCost() == 0)
            routeViewHolder.cost.setText("");
        else
            routeViewHolder.cost.setText(route.getCost() + "원");
        switch (route.getGroup()){
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

    class RouteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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