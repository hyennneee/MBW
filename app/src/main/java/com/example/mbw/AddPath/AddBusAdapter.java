package com.example.mbw.AddPath;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.mbw.R;
import com.example.mbw.route.ItemAdapter;

import java.util.ArrayList;

public class AddBusAdapter extends RecyclerView.Adapter<ViewHolder> {

    private ArrayList<AddBusItem> BusRoute = null; // 버스 경유 정류장 리스트

    public AddBusAdapter(ArrayList<AddBusItem> list){
        BusRoute = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view =null;
        ViewHolder viewHolder = null;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_bus,parent,false);
        viewHolder = new BusViewHolder(view);

        return viewHolder;
    }

    public class BusViewHolder extends RecyclerView.ViewHolder{

        TextView busStopName;
        TextView busStopId;
        ToggleButton toggleButton;
        ToggleButton toggleButton2;
        ImageView imageView;

        public BusViewHolder(@NonNull final View itemView){
            super(itemView);
            busStopName = itemView.findViewById(R.id.busStopName);
            busStopId = itemView.findViewById(R.id.busStopId);
            toggleButton = itemView.findViewById(R.id.toggleButton);
            toggleButton2 = itemView.findViewById(R.id.toggleButton2);
            imageView = itemView.findViewById(R.id.imageView);
        }

        private void setBusDetails(AddBusItem addBusItem) {
            busStopName.setText(addBusItem.getBusStopName());
            busStopId.setText(addBusItem.getBusStopId());
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position){
        ((BusViewHolder) holder).setBusDetails(BusRoute.get(position));
    }

    @Override
    public int getItemCount() {
        return (null != BusRoute ? BusRoute.size() : 0);
    }

}
