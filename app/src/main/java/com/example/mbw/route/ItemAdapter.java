package com.example.mbw.route;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbw.R;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<Route> data;  //adapter는 viewholder로 변경할 data를 가지고 있는다

    private static int TYPE_BUS = 1;
    private static int TYPE_SUB = 2;
    public ItemAdapter(ArrayList<Item> list) {
        //this.data = list;
    }

    @Override   //adapter는 아이템마다 viewholder를 만드는 방법을 정의해야 한다
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == TYPE_BUS) { // for bus layout
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bus, viewGroup, false);
            return new BusViewHolder(view);

        } else { // for subway layout
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sub, viewGroup, false);
            return new SubViewHolder(view);
        }
    }

    @Override
    public int getItemCount() { //전체 아이템 개수
        return (null != data ? data.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        /*if (TextUtils.isEmpty(data.get(position).getBusNum())) {
            return TYPE_BUS;

        } else {
            return TYPE_SUB;
        }*/
        return TYPE_BUS;
    }

    @Override   //adapter는 viewholder에 data를 전달해 주어야 한다.
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        /*if (getItemViewType(position) == TYPE_BUS) {
            ((BusViewHolder) viewHolder).setBusDetails(data.get(position));
        } else {
            ((SubViewHolder) viewHolder).setSubDetails(data.get(position));
        }*/
    }

    public class BusViewHolder extends RecyclerView.ViewHolder {


        protected TextView stationName, remainingTime, stationNo, busNum;
        protected ImageView busType;

        public BusViewHolder(@NonNull View view) {//constructor임
            //findViewById로 변수 정의
            //대충 이해가 갔는데 유동적으로 어떻게 표시하는지 모르겠음
            //긍까 지하철이랑 버스랑 어떻게 될지 모르는 상황. view 틀을 어떻게 만들어두고 어떻게 띄우지?
            //공통: 남은 시간, 역 이름; 지하철: 노선번호이미지, 버스: 버스번호, 버스타입, 저상여부, 정류장 번호
            //stationName, remainingTime,  subLineImage, busNum, busType, stationNo
            super(view);
            this.stationName = view.findViewById(R.id.busStationText);  //
            this.remainingTime = view.findViewById(R.id.busRemaining);
            this.stationNo = view.findViewById(R.id.stationId); //stationID
            this.busNum = view.findViewById(R.id.busNum); //busNo
            this.busType = view.findViewById(R.id.busType); //type
        }
        private void setBusDetails(Item item) {
            stationName.setText(item.getStationName());
            remainingTime.setText(item.getRemainingTime());
            busNum.setText(item.getBusNum());
            stationNo.setText(item.getStationNo());
            switch (item.getBusType()){
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
            //this.subStation = view.findViewById(R.id.subStation); //stationName
            this.subRemaining = view.findViewById(R.id.subRemaining);
            this.subImage = view.findViewById(R.id.subImage);  //subwayCode
        }
        //오디세이로부터 받아오긴하는데 안드에서 띄워줄 필요는 없는 정보는 어떻게 처리하지
        //일단 Route가 갖고있어야는되지 않나
        private void setSubDetails(Route route) {
            /*subStation.setText(route.getSubStation());
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
            }*/
        }
    }

}
