package com.example.mbw.route;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        //View v = inflater.inflate(R.layout.fragment_all, container, false);
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_recycler_view, viewGroup, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder routeViewHolder, int i) {
        Route route = routeList.get(i);
        //routeViewHolder가 Null이라고 함
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
            //rvItem = itemView.findViewById(R.id.rv_sub_item);
            totalTime = itemView.findViewById(R.id.totalTimeView);
            walkingTime = itemView.findViewById(R.id.walkingTimeView);
            cost = itemView.findViewById(R.id.costView);
        }
    }
}