package com.example.mbw;

import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class SubAdapter extends RecyclerView.Adapter<SubAdapter.SubViewHolder>{
    private ArrayList<Subway> mList;

    public class SubViewHolder extends RecyclerView.ViewHolder {
        protected TextView wholeTime, walkingTime, walkingTime2, transferNum, money, subStation;
        protected ImageView subLine;


        public SubViewHolder(View view) {
            super(view);
            this.wholeTime = (TextView) view.findViewById(R.id.wholeTime);
            this.walkingTime = (TextView) view.findViewById(R.id.walkingTimeView);
            this.walkingTime2 = (TextView) view.findViewById(R.id.walkingTimeView2);
            this.transferNum = (TextView) view.findViewById(R.id.transferNum);
            this.money = (TextView) view.findViewById(R.id.money);
            this.subLine = (ImageView) view.findViewById(R.id.subLine);
            this.subStation = (TextView) view.findViewById(R.id.subStation);
        }
    }


    public SubAdapter(ArrayList<Subway> list) {
        this.mList = list;
    }



    @Override
    public SubViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_list_place_search_route, viewGroup, false);

        SubViewHolder viewHolder = new SubViewHolder(view);

        return viewHolder;
    }




    @Override
    public void onBindViewHolder(@NonNull SubViewHolder viewholder, int position) {

        /*viewholder.id.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        viewholder.english.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        viewholder.korean.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        viewholder.id.setGravity(Gravity.CENTER);
        viewholder.english.setGravity(Gravity.CENTER);
        viewholder.korean.setGravity(Gravity.CENTER);



        viewholder.id.setText(mList.get(position).getId());
        viewholder.english.setText(mList.get(position).getEnglish());
        viewholder.korean.setText(mList.get(position).getKorean());*/
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}
