package com.example.mbw;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class MyAdapter {//extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    /*public static class BusViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPicture;


        BusViewHolder(@NonNull View view){
            super(view);
        }
    }

    public static class SubViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPicture;


        SubViewHolder(@NonNull View view){
            super(view);
        }
    }

    private ArrayList<PathInformation> foodInfoArrayList;
    MyAdapter(ArrayList<PathInformation> foodInfoArrayList){
        this.foodInfoArrayList = foodInfoArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.path_view, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder) holder;

        myViewHolder.ivPicture.setImageResource(foodInfoArrayList.get(position).drawableId);
        myViewHolder.tvPrice.setText(foodInfoArrayList.get(position).price);
    }

    @Override
    public int getItemCount() {
        return foodInfoArrayList.size();
    }*/
}
