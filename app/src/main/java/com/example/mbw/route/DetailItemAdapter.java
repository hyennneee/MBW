package com.example.mbw.route;

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

/*
알아야 하는 것 : 1, 2, 3, 4, 5 의 갯수, 순서
    1. 버스 : 정류장 이름, 버스 번호, 정류장 번호, 남은 시간 1, 혼잡도 1, 남은 시간 2, 혼잡도 2, 이동시간, 이동 정류장 개수, 저상버스 여부, 버스 타입
    2. 지하철_환승 : 역 이름, __행1, 남은시간1, __행2, 남은시간2, 빠른 환승 지점, 빠른 환승 도보시간, 빠른 환승 도보거리, 이동시간, 이동 역 개수,
       지하철_끝 : 역 이름, __행1, 남은시간1, __행2, 남은시간2, 빠른 하차 지점, 이동시간, 이동 역 개수
    3. 도보 : 이동시간, 이동거리
    4. 시작점 : 시작지점
    5. 도착점 : 도착지점
 */

public class DetailItemAdapter extends RecyclerView.Adapter<ViewHolder>{

    private static final int TYPE_COUNT = 5;
    private static final int LAYOUT_START = 0;
    private static final int LAYOUT_BUS = 1;
    private static final int LAYOUT_SUB = 2;
    //private static final int LAYOUT_SUB_END = 3;
    private static final int LAYOUT_WALK = 3;
    private static final int LAYOUT_END = 4;

    int     mItemViewType;

    private ArrayList<DetailItem> DetailPath = null;

    // 생성자에서 데이터 리스트 객체를 전달 받음.
    public DetailItemAdapter(ArrayList<DetailItem> list) {
        DetailPath = list;
    }

    public int getViewTypeCount(){
        return TYPE_COUNT;
    }

    public void setItemViewType(int viewType){
        mItemViewType = viewType;
    }

    @Override
    public int getItemViewType(int position){
        int type = DetailPath.get(position).getImageType();

        switch(type){
            case 0 :
                return LAYOUT_START;
            case 20 :
                return LAYOUT_BUS;
            case 30 :
                return LAYOUT_WALK;
            case 40 :
                return LAYOUT_END;
            default:
                return LAYOUT_SUB;
        }
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view =null;
        ViewHolder viewHolder = null;

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_start,parent,false);
        viewHolder = new ViewHolderStart(view);

        switch(viewType){
            case 0 :
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_start,parent,false);
                viewHolder = new ViewHolderStart(view);
                break;
            case 1 :
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_bus,parent,false);
                viewHolder = new ViewHolderBus(view);
                break;
            case 2 :
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_subway_transfer,parent,false);
                viewHolder = new ViewHolderSub(view);
            case 3 :
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_walk,parent,false);
                viewHolder = new ViewHolderWalk(view);
                break;
            case 4 :
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_end,parent,false);
                viewHolder = new ViewHolderEnd(view);
                break;
        }
        return viewHolder;
    }

    // 뷰홀더 지정 0 : Start
    public class ViewHolderStart extends RecyclerView.ViewHolder {
        TextView startPoint; // 시작지점 이름
        ImageView startImage;

        public ViewHolderStart(@NonNull final View itemView) {
            super(itemView);
            startPoint = itemView.findViewById(R.id.startPoint);
            startImage = itemView.findViewById(R.id.startImage);
        }

        private void setStartDetails(DetailItem detailItem){
            startPoint.setText(detailItem.getSpotName());
            startImage.setImageResource(R.drawable.start);
        }
    }


    // 뷰홀더 지정 1 : Bus
    public class ViewHolderBus extends RecyclerView.ViewHolder {
        TextView busStation;
        TextView busNum;
        TextView stationId;
        TextView stationId2;
        TextView busRemaining1;
        TextView busRemaining2;
        TextView busTime;
        TextView stopNum;
        TextView endStop;

        //ImageView busImage;
        ImageView busType1; // 버스 하나만 보여준다고 가정

        public ViewHolderBus(@NonNull final View itemView) {
            super(itemView);
            busStation = itemView.findViewById(R.id.finalStationText);
            endStop = itemView.findViewById(R.id.endStop);
            busNum = itemView.findViewById(R.id.busNum);
            stationId = itemView.findViewById(R.id.stationId);
            stationId2 = itemView.findViewById(R.id.stationId2);
            busRemaining1 = itemView.findViewById(R.id.busRemaining1);
            busRemaining2 = itemView.findViewById(R.id.busRemaining2);
            busTime = itemView.findViewById(R.id.busTime);
            stopNum = itemView.findViewById(R.id.stopNum);
            //busImage = itemView.findViewById(R.id.busImage);
            busType1 = itemView.findViewById(R.id.busType);
        }

        private void setBusDetails(DetailItem detailItem){
            busStation.setText(detailItem.getSpotName());
            endStop.setText(detailItem.getSpotName2());
            busNum.setText(detailItem.getBusNum1());
            stationId.setText(detailItem.getWayNum());
            stationId2.setText(detailItem.getWayNum2());
            busRemaining1.setText(detailItem.getRemaining1());
            busRemaining2.setText(detailItem.getRemaining2());

            busTime.setText(detailItem.getTime()+"분");
            stopNum.setText(detailItem.getPassedStop()+"개 정류장 이동");

            // busImage.setImageResource(R.drawable.bus);

            // 간선, 지선, 마을
            switch (detailItem.getBusType1()){
                case 11:
                    busType1.setImageResource(R.drawable.blue_bus);
                    break;
                case 12:
                    busType1.setImageResource(R.drawable.green_bus);
                    break;
                case 3:
                    busType1.setImageResource(R.drawable.town_bus);
                    break;
            }
        }
    }

    // 뷰홀더 지정 2 : Subway
    public class ViewHolderSub extends RecyclerView.ViewHolder {
        ImageView subImage;
        TextView subStation;
        TextView endStop;
        TextView subMainDir;
        TextView subDirection1;
        TextView subDirection2;
        TextView subRemaining1;
        TextView subRemaining2;
        TextView fastPlatform;
        TextView subTime;
        TextView transExit;
        LinearLayout subTransWalk;

        ImageView endImage;
        // 환승일 때만 보여주기
        TextView walkTime;
        TextView walkDistance;

        public ViewHolderSub(@NonNull final View itemView) {
            super(itemView);
            subImage = itemView.findViewById(R.id.subImage);
            subStation = itemView.findViewById(R.id.subStation);
            endStop = itemView.findViewById(R.id.endStop);
            subMainDir = itemView.findViewById(R.id.subMainDir);
            subDirection1 = itemView.findViewById(R.id.subDirection1);
            subDirection2 = itemView.findViewById(R.id.subDirection2);
            subRemaining1 = itemView.findViewById(R.id.subRemaining1);
            subRemaining2 = itemView.findViewById(R.id.subRemaining2);
            transExit = itemView.findViewById(R.id.transExit);
            fastPlatform = itemView.findViewById(R.id.fastPlatform);
            subTime = itemView.findViewById(R.id.subTime);
            walkTime = itemView.findViewById(R.id.walkTime);
            walkDistance = itemView.findViewById(R.id.walkDistance);

            subTransWalk = itemView.findViewById(R.id.subTransWalk);
            endImage = itemView.findViewById(R.id.endImage);
        }

        private void setSubDetails(DetailItem detailItem){
            subStation.setText(detailItem.getSpotName());
            endStop.setText(detailItem.getSpotName2()); // 도착역 이름
            subMainDir.setText(detailItem.getWayNum()); // 방면
            subDirection1.setText(detailItem.getDirection1());  // 행
            subDirection2.setText(detailItem.getDirection2());
            subRemaining1.setText(detailItem.getRemaining1());  // 남은 시간
            subRemaining2.setText(detailItem.getRemaining2());
            fastPlatform.setText(detailItem. getFastPlatform());
            subTime.setText(detailItem.getTime());

            // case 나눠야 하는 것
            // 1. 환승 / 출구   2. 지하철 이미지
            // transExit : 0 - trans, 1 - exit
            if(detailItem.getTransExit()==0){ // 환승일 경우
                transExit.setText("빠른 환승");
                subTransWalk.setVisibility(View.VISIBLE);
                walkTime.setText("환승 도보" +detailItem.getTransTime()+detailItem.getTransDistance()+"m");
            }
            else {
                transExit.setText("빠른 하차");
                subTransWalk.setVisibility(View.GONE);
            }

            switch (detailItem.getImageType()){
                case 1:
                    subImage.setImageResource(R.drawable.line1);
                    endImage.setColorFilter(R.color.line1);
                    break;
                case 2:
                    subImage.setImageResource(R.drawable.line2);
                    endImage.setColorFilter(R.color.line2);
                    break;
                case 3:
                    subImage.setImageResource(R.drawable.line3);
                    endImage.setColorFilter(R.color.line3);
                    break;
                case 4:
                    subImage.setImageResource(R.drawable.line4);
                    endImage.setColorFilter(R.color.line4);
                    break;
                case 5:
                    subImage.setImageResource(R.drawable.line5);
                    endImage.setColorFilter(R.color.line5);
                    break;
                case 6:
                    subImage.setImageResource(R.drawable.line6);
                    endImage.setColorFilter(R.color.line6);
                    break;
                case 7:
                    subImage.setImageResource(R.drawable.line7);
                    endImage.setColorFilter(R.color.line7);
                    break;
                case 8:
                    subImage.setImageResource(R.drawable.line8);
                    endImage.setColorFilter(R.color.line8);
                    break;
            }
        }
    }

    // 뷰홀더 지정 3 : Walk
    public class ViewHolderWalk extends RecyclerView.ViewHolder {
        TextView walkTime; // 시작지점 이름
        TextView walkDistance;

        public ViewHolderWalk(@NonNull final View itemView) {
            super(itemView);
            walkTime = itemView.findViewById(R.id.walkTime);
            walkDistance = itemView.findViewById(R.id.walkDistance);
        }

        private void setWalkDetails(DetailItem detailItem){
            walkTime.setText("도보 "+detailItem.getTime()+"분");
            walkDistance.setText(detailItem.getWalkDistance()+"m 이동");
        }
    }

    // 뷰홀더 지정 4 : End
    public class ViewHolderEnd extends RecyclerView.ViewHolder {
        TextView endPoint; // 시작지점 이름
        ImageView endImage;

        public ViewHolderEnd(@NonNull final View itemView) {
            super(itemView);
            endPoint = itemView.findViewById(R.id.endPoint);
            endImage = itemView.findViewById(R.id.endImage);
        }

        private void setEndDetails(DetailItem detailItem){
            endPoint.setText(detailItem. getSpotName());
            endImage.setImageResource(R.drawable.end);
        }
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        switch(getItemViewType(position)){
            case LAYOUT_START:
                ((ViewHolderStart) holder).setStartDetails(DetailPath.get(position));
                break;
            case LAYOUT_BUS:
                ((ViewHolderBus) holder).setBusDetails(DetailPath.get(position));
                break;
            case LAYOUT_SUB:
                ((ViewHolderSub) holder).setSubDetails(DetailPath.get(position));
                break;
            case LAYOUT_WALK:
                ((ViewHolderWalk) holder).setWalkDetails(DetailPath.get(position));
                break;
            case LAYOUT_END:
                ((ViewHolderEnd) holder).setEndDetails(DetailPath.get(position));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return (null != DetailPath ? DetailPath.size() : 0);
    }
}
