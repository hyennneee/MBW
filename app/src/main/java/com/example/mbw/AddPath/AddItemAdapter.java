package com.example.mbw.AddPath;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;


import com.example.mbw.R;

import java.util.ArrayList;

public class AddItemAdapter extends RecyclerView.Adapter<ViewHolder> {


    private ArrayList<AddItem> AddPath = null;

    private Context context;
    //in the Constructor, pass the context in the parametres

    public AddItemAdapter(ArrayList<AddItem> list, Context context) {
        AddPath = list;
        this.context = context;

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

        View lineView;
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
            lineView = addView.findViewById(R.id.view5);
        }

        public void setAddDetails(AddItem addItem){

            startStation.setText(addItem.getStart());
            endStation.setText(addItem.getEnd());

            if(addItem.getBusType()!=null) {
                busInfo.setVisibility(View.VISIBLE);
                busNum.setText(addItem.getNum());
                startImage.setImageResource(0);
                startImage.setImageResource(R.drawable.bus1);
                endImage.setImageResource(R.drawable.busend);
                lineView.setBackgroundColor(context.getResources().getColor(R.color.busblue));

                switch (addItem.getBusType()) {
                    case "11":
                        busType.setImageResource(R.drawable.blue_bus);
                        break;
                    case "12":
                        busType.setImageResource(R.drawable.green_bus);
                        break;
                    case "3":
                        busType.setImageResource(R.drawable.town_bus);
                        break;
                }
            }
            else{
                busInfo.setVisibility(View.GONE);
                endImage.setImageResource(R.drawable.circle);

                GradientDrawable endCircle = (GradientDrawable)endImage.getBackground();
                //GradientDrawable line = (GradientDrawable)lineView.getBackground();

                switch(addItem.getNum()){
                    case "01호선":
                        startImage.setImageResource(R.drawable.line1);
                        endCircle.setColor(context.getResources().getColor(R.color.line1));
                        lineView.setBackgroundColor(context.getResources().getColor(R.color.line1));
                        break;
                    case "02호선":
                        startImage.setImageResource(R.drawable.line2);
                        endCircle.setColor(context.getResources().getColor(R.color.line2));
                        lineView.setBackgroundColor(context.getResources().getColor(R.color.line2));

                        break;
                    case "03호선":
                        startImage.setImageResource(R.drawable.line3);
                        endCircle.setColor(context.getResources().getColor(R.color.line3));
                        lineView.setBackgroundColor(context.getResources().getColor(R.color.line3));

                        break;
                    case "04호선":
                        startImage.setImageResource(R.drawable.line4);
                        endCircle.setColor(context.getResources().getColor(R.color.line4));
                        lineView.setBackgroundColor(context.getResources().getColor(R.color.line4));
                        break;
                    case "05호선":
                        startImage.setImageResource(R.drawable.line5);
                        endCircle.setColor(context.getResources().getColor(R.color.line5));
                        lineView.setBackgroundColor(context.getResources().getColor(R.color.line5));
                        break;
                    case "06호선":
                        startImage.setImageResource(R.drawable.line6);
                        endCircle.setColor(context.getResources().getColor(R.color.line6));
                        lineView.setBackgroundColor(context.getResources().getColor(R.color.line6));
                        break;
                    case "07호선":
                        startImage.setImageResource(R.drawable.line7);
                        endCircle.setColor(context.getResources().getColor(R.color.line7));
                        lineView.setBackgroundColor(context.getResources().getColor(R.color.line7));
                        break;
                    case "08호선":
                        startImage.setImageResource(R.drawable.line8);
                        endCircle.setColor(context.getResources().getColor(R.color.line8));
                        lineView.setBackgroundColor(context.getResources().getColor(R.color.line8));
                        break;

                }
                endImage.setImageResource(R.drawable.circle);
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
