package com.example.mbw.route;
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

    private ArrayList<Item> data;  //adapter는 viewholder로 변경할 data를 가지고 있는다

    private static int TYPE_BUS = 1;
    private static int TYPE_SUB = 2;
    private static int TYPE_FIN = 3;
    private View adapterView;
    public ItemAdapter(ArrayList<Item> list) {
        this.data = list;
    }

    @Override   //adapter는 아이템마다 viewholder를 만드는 방법을 정의해야 한다
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == TYPE_BUS) { // for bus layout
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_bus, viewGroup, false);
            return new BusViewHolder(view);

        } else if(viewType == TYPE_SUB){ // for subway layout
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sub, viewGroup, false);
            return new SubViewHolder(view);
        }
        else{ // for final station layout
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_final, viewGroup, false);
            return new FinViewHolder(view);
        }
    }

    @Override
    public int getItemCount() { //전체 아이템 개수
        return (null != data ? data.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).getSubLine() == 0) {    //subLine == 0
            return TYPE_BUS;
        } else if(data.get(position).getBusType() == -1){
            return TYPE_SUB;
        }
        else{
            return TYPE_FIN;
        }
    }

    @Override   //adapter는 viewholder에 data를 전달해 주어야 한다.
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == TYPE_BUS) {
            ((BusViewHolder) viewHolder).setBusDetails(data.get(position));
        } else if(getItemViewType(position) == TYPE_SUB){
            ((SubViewHolder) viewHolder).setSubDetails(data.get(position));
        }
        else{
            ((FinViewHolder) viewHolder).setFinDetails(data.get(position));
        }
    }

    public class BusViewHolder extends RecyclerView.ViewHolder {


        protected TextView stationName, busRemaining, stationNo, busNum, busOthers2, numOfOtherBus1, numOfOtherBus2;
        protected ImageView busType;

        public BusViewHolder(@NonNull View view) {//constructor임
            super(view);
            this.stationName = view.findViewById(R.id.finalStationText);  //
            this.busRemaining = view.findViewById(R.id.busRemaining);
            this.stationNo = view.findViewById(R.id.stationId); //arsID
            this.busNum = view.findViewById(R.id.busNum); //busNo
            this.busType = view.findViewById(R.id.busType); //type: 0-일반, 1-저상
            this.busOthers2 = view.findViewById(R.id.busOthers2);
            this.numOfOtherBus1 = view.findViewById(R.id.numOfOtherBus1);
            this.numOfOtherBus2 = view.findViewById(R.id.numOfOtherBus2);

            //저상 여부 어떻게 표시할까
        }
        private void setBusDetails(Item item) {
            stationName.setText(item.getStationName());
            String mainBus = item.getBusNum().get(0);
            int size = item.getBusNum().size();
            if(item.isFirst()) {   //첫 번째가 버스
                String getRemaining = item.getRemainingTime();
                busRemaining.setText(getRemaining);
                //busNum 2개 이상
                if(size > 1){
                    String subBus = item.getBusNum().get(1);
                    busOthers2.setTextSize(13);
                    if(item.getBusNum().size() > 2) {
                        subBus += ", " + item.getBusNum().get(2);
                        if(size > 3){
                            numOfOtherBus2.setTextSize(13);
                            numOfOtherBus2.setText(" 외 " + (size - 2) + "대");
                        }
                    }
                    busOthers2.setText(subBus);
                }
            }
            else{//others1
                if(size > 1) {
                    mainBus += ", " + item.getBusNum().get(1);
                    if (size > 2) {
                        numOfOtherBus1.setTextSize(13);
                        numOfOtherBus1.setText("외 " + (size - 2) + "대");
                    }
                }
            }
            busNum.setText(mainBus);

            stationNo.setText(item.getArsID());
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
        protected View subLine;

        public SubViewHolder(@NonNull View view) {//constructor임
            //findViewById로 변수 정의
            //대충 이해가 갔는데 유동적으로 어떻게 표시하는지 모르겠음
            //긍까 지하철이랑 버스랑 어떻게 될지 모르는 상황. view 틀을 어떻게 만들어두고 어떻게 띄우지?
            super(view);
            this.subStation = view.findViewById(R.id.subStation); //stationName
            this.subImage = view.findViewById(R.id.subImage);  //subwayCode
            this.subRemaining = view.findViewById(R.id.subRemaining);
            subLine = view.findViewById(R.id.subwayLine);
            adapterView = view;
        }
        //오디세이로부터 받아오긴하는데 안드에서 띄워줄 필요는 없는 정보는 어떻게 처리하지
        //일단 Route가 갖고있어야는되지 않나
        private void setSubDetails(Item item) {
            subStation.setText(item.getStationName());
            if(!item.getRemainingTime().equals("")) {
                subRemaining.setText(item.getRemainingTime());
                subRemaining.setTextSize(13);
            }
            switch (item.getSubLine()){   //subwayCode
                case 1:
                    subImage.setImageResource(R.drawable.line1);
                    subLine.setBackgroundColor(adapterView.getResources().getColor(R.color.line1));
                    break;
                case 2:
                    subImage.setImageResource(R.drawable.line2);
                    subLine.setBackgroundColor(adapterView.getResources().getColor(R.color.line2));
                    break;
                case 3:
                    subImage.setImageResource(R.drawable.line3);
                    subLine.setBackgroundColor(adapterView.getResources().getColor(R.color.line3));
                    break;
                case 4:
                    subImage.setImageResource(R.drawable.line4);
                    subLine.setBackgroundColor(adapterView.getResources().getColor(R.color.line4));
                    break;
                case 5:
                    subImage.setImageResource(R.drawable.line5);
                    subLine.setBackgroundColor(adapterView.getResources().getColor(R.color.line5));
                    break;
                case 6:
                    subImage.setImageResource(R.drawable.line6);
                    subLine.setBackgroundColor(adapterView.getResources().getColor(R.color.line6));
                    break;
                case 7:
                    subImage.setImageResource(R.drawable.line7);
                    subLine.setBackgroundColor(adapterView.getResources().getColor(R.color.line7));
                    break;
                case 8:
                    subImage.setImageResource(R.drawable.line8);
                    subLine.setBackgroundColor(adapterView.getResources().getColor(R.color.line8));
                    break;
                case 9:
                    subImage.setImageResource(R.drawable.line9);
                    subLine.setBackgroundColor(adapterView.getResources().getColor(R.color.line9));
                    break;
                case 100:
                    subImage.setImageResource(R.drawable.line_bundang);
                    subLine.setBackgroundColor(adapterView.getResources().getColor(R.color.line_bundang));
                    break;
                case 101:
                    subImage.setImageResource(R.drawable.line_airport);
                    subLine.setBackgroundColor(adapterView.getResources().getColor(R.color.line_airport));
                    break;
                case 104:
                    subImage.setImageResource(R.drawable.line_gyeongui);
                    subLine.setBackgroundColor(adapterView.getResources().getColor(R.color.line_gyeonui));
                    break;
                default:
                    subImage.setImageResource(R.drawable.line_default);
                    subLine.setBackgroundColor(adapterView.getResources().getColor(R.color.line_default));
                    break;
            }
        }
    }
    public class FinViewHolder extends RecyclerView.ViewHolder {


        protected TextView stationName;

        public FinViewHolder(@NonNull View view) {//constructor임
            super(view);
            this.stationName = view.findViewById(R.id.finalStationText); //stationName
        }
        private void setFinDetails(Item item) {
            stationName.setText(item.getStationName());
            if(item.getSubLine() == -1){    //버스

            }
            else{   //지하철

            }
        }
    }

}
