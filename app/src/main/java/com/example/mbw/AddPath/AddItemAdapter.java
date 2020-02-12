package com.example.mbw.AddPath;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;


import com.example.mbw.AddPath.AddItem;
import com.example.mbw.R;

import java.util.ArrayList;

public class AddItemAdapter extends RecyclerView.Adapter<ViewHolder> {


    private ArrayList<AddItem> AddPath = null;

    public AddItemAdapter(ArrayList<AddItem> list) {
        AddPath = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view =null;
        RecyclerView.ViewHolder viewHolder = null;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add,parent,false);
        viewHolder = new addViewHolder(view);

        return viewHolder;
    }

    public class addViewHolder extends RecyclerView.ViewHolder{

        TextView startStation;
        TextView endStation;
        TextView busNum;

        ImageView startImage;
        ImageView endImage;
        ImageView busType;

        LinearLayout busInfo;

        public addViewHolder(@NonNull final View addView){
            super(addView);
            startStation = addView.findViewById(R.id.startStation);
            endStation = addView.findViewById(R.id.endStation);
            busNum = addView.findViewById(R.id.busNum);
            startImage = addView.findViewById(R.id.startImage);
            endImage = addView.findViewById(R.id.endImage);
            busType = addView.findViewById(R.id.busType);
            busInfo = addView.findViewById(R.id.busInfo);
        }

        public void setAddDetails(AddItem addItem){

            startStation.setText(addItem.getStart());
            endStation.setText(addItem.getEnd());

            if(addItem.getBusType()!=null) {
                busInfo.setVisibility(View.VISIBLE);
                busNum.setText(addItem.getNum());
                startImage.setImageResource(R.drawable.bus);
                endImage.setImageResource(R.drawable.fin);
                switch (addItem.getBusType()) {
                    case "간선":
                        busType.setImageResource(R.drawable.blue_bus);
                        break;
                    case "지선":
                        busType.setImageResource(R.drawable.green_bus);
                        break;
                    case "마을":
                        busType.setImageResource(R.drawable.town_bus);
                        break;
                }
            }
            else{
                busInfo.setVisibility(View.GONE);
                endImage.setImageResource(R.drawable.circle);
                switch(addItem.getNum()){
                    case "1":
                        startImage.setImageResource(R.drawable.line1);
                        endImage.setColorFilter(R.color.line1);
                        break;
                    case "2":
                        startImage.setImageResource(R.drawable.line2);
                        endImage.setColorFilter(R.color.line2);
                        break;
                    case "3":
                        startImage.setImageResource(R.drawable.line3);
                        endImage.setColorFilter(R.color.line3);
                        break;
                    case "4":
                        startImage.setImageResource(R.drawable.line4);
                        endImage.setColorFilter(R.color.line4);
                        break;
                    case "5":
                        startImage.setImageResource(R.drawable.line5);
                        endImage.setColorFilter(R.color.line5);
                        break;
                    case "6":
                        startImage.setImageResource(R.drawable.line6);
                        endImage.setColorFilter(R.color.line6);
                        break;
                    case "7":
                        startImage.setImageResource(R.drawable.line7);
                        endImage.setColorFilter(R.color.line7);
                        break;
                    case "8":
                        startImage.setImageResource(R.drawable.line8);
                        endImage.setColorFilter(R.color.line8);
                        break;
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ((addViewHolder)holder).setAddDetails(AddPath.get(position));
    }

    @Override
    public int getItemCount() {
        return (null != AddPath ? AddPath.size() : 0);
    }

}
