package com.example.mbw.route;

import android.R.layout;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.mbw.DetailPathActivity;
import com.example.mbw.R;

import java.util.ArrayList;
import java.util.List;

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
/*
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_start,parent,false);
        viewHolder = new ViewHolderStart(view);
*/
        switch(viewType){
            case LAYOUT_START :
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_start,parent,false);
                viewHolder = new ViewHolderStart(view);
                break;
            case LAYOUT_BUS :
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_bus,parent,false);
                viewHolder = new ViewHolderBus(view);
                break;
            case LAYOUT_SUB :
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_subway_transfer,parent,false);
                viewHolder = new ViewHolderSub(view);
                break;
            case LAYOUT_WALK :
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_walk,parent,false);
                viewHolder = new ViewHolderWalk(view);
                break;
            case LAYOUT_END :
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
        //TextView busRemaining1;
        //TextView busRemaining2;
        TextView busTime;
        TextView stopNum;
        TextView endStop;
        ImageButton downArrow;
        RecyclerView passListView;

        //ImageView busImage;
        ImageView busType1; // 버스 하나만 보여준다고 가정

        public ViewHolderBus(@NonNull final View itemView) {
            super(itemView);
            busStation = itemView.findViewById(R.id.finalStationText);
            endStop = itemView.findViewById(R.id.endStop);
            busNum = itemView.findViewById(R.id.busNum);
            stationId = itemView.findViewById(R.id.stationId);
            stationId2 = itemView.findViewById(R.id.stationId2);
            //busRemaining1 = itemView.findViewById(R.id.busRemaining1);
            //busRemaining2 = itemView.findViewById(R.id.busRemaining2);
            busTime = itemView.findViewById(R.id.busTime);
            stopNum = itemView.findViewById(R.id.stopNum);
            //busImage = itemView.findViewById(R.id.busImage);
            busType1 = itemView.findViewById(R.id.busType);
            downArrow =itemView.findViewById(R.id.imageButton);
            passListView = itemView.findViewById(R.id.passListView);

            downArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(passListView.getVisibility() != View.VISIBLE){
                        passListView.setVisibility(View.VISIBLE);
                        downArrow.setImageResource(R.drawable.uparrow);
                    }
                    else {
                        passListView.setVisibility(View.GONE);
                        downArrow.setImageResource(R.drawable.downarrow);
                    }
                }
            });

        }

        private void setBusDetails(DetailItem detailItem){
            busStation.setText(detailItem.getSpotName());
            endStop.setText(detailItem.getSpotName2());
            busNum.setText(detailItem.getBusNum1());
            stationId.setText(detailItem.getWayNum());
            stationId2.setText(detailItem.getWayNum2());
            //busRemaining1.setText(detailItem.getRemaining1());
            //busRemaining2.setText(detailItem.getRemaining2());

            int hour = detailItem.getTime()/60;
            int minute = detailItem.getTime()%60;
            String timeInfo;
            if(hour==0)
                timeInfo = minute+"분";
            else
                timeInfo = hour+"시간 "+minute+"분";


            busTime.setText(timeInfo);
            stopNum.setText(detailItem.getPassedStop()+"개 정류장 이동");

            ArrayList<String> passStationList = detailItem.getPassStationArray();

            // 리사이클러뷰에 LinearLayoutManager 객체 지정.
            passListView.setLayoutManager(new LinearLayoutManager(itemView.getContext())) ;

            // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
            SimpleTextAdapter adapter = new SimpleTextAdapter(passStationList) ;
            passListView.setAdapter(adapter) ;


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
        //TextView subDirection1;
        //TextView subDirection2;
        TextView subRemaining1;
        TextView subRemaining2;
        TextView fastPlatform;
        TextView fastPlatform2;

        TextView subTime;
        TextView stationNum;

        ImageView endImage;
        TextView problemTextView;
        TextView problemTextView2;

        View v;

        RecyclerView passListView;

        public ViewHolderSub(@NonNull final View itemView) {
            super(itemView);
            subImage = itemView.findViewById(R.id.subImage);
            subStation = itemView.findViewById(R.id.subStation);
            endStop = itemView.findViewById(R.id.endStop);
            subMainDir = itemView.findViewById(R.id.subMainDir);
            //subDirection1 = itemView.findViewById(R.id.subDirection1);
            //subDirection2 = itemView.findViewById(R.id.subDirection2);
            subRemaining1 = itemView.findViewById(R.id.subRemaining1);
            subRemaining2 = itemView.findViewById(R.id.subRemaining2);
            stationNum = itemView.findViewById(R.id.stationNum);
            fastPlatform = itemView.findViewById(R.id.contextInfo1);
            fastPlatform2 = itemView.findViewById(R.id.contextInfo2);
            subTime = itemView.findViewById(R.id.subTime);
            endImage = itemView.findViewById(R.id.endImage);
            v = itemView.findViewById(R.id.view5);
            passListView = itemView.findViewById(R.id.passListView);
            ImageButton downArrow = itemView.findViewById(R.id.imageButton);
            problemTextView = itemView.findViewById(R.id.problemTextView);
            problemTextView2 = itemView.findViewById(R.id.problemTextView2);

            downArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(passListView.getVisibility() != View.VISIBLE){
                        passListView.setVisibility(View.VISIBLE);
                        downArrow.setImageResource(R.drawable.uparrow);
                    }
                    else {
                        passListView.setVisibility(View.GONE);
                        downArrow.setImageResource(R.drawable.downarrow);
                    }
                }
            });
        }

        private void setSubDetails(DetailItem detailItem){
            subStation.setText(detailItem.getSpotName());
            endStop.setText(detailItem.getSpotName2()); // 도착역 이름
            subMainDir.setText(detailItem.getWayNum()+" 방면"); // 방면
            //subDirection1.setText(detailItem.getDirection1());  // 행
            //subDirection2.setText(detailItem.getDirection2());
            subRemaining1.setText(detailItem.getRemaining1());  // 남은 시간
            subRemaining2.setText(detailItem.getRemaining2());
            fastPlatform.setText(detailItem. getContext1());
            fastPlatform2.setText(detailItem. getContext2());
            stationNum.setText(detailItem.getPassedStop()+"개 역 이동");
            String problem = detailItem.getProblem();
            String problem2 = detailItem.getProblem2();

            if(problem.equals("null")){
                problemTextView.setVisibility(View.GONE);}
            else{
                problemTextView.setText(detailItem.getProblem());}

            if(problem2.equals("null")){
                problemTextView2.setVisibility(View.GONE);}
            else{
                problemTextView2.setText(detailItem.getProblem2());}

            int hour = detailItem.getTime()/60;
            int minute = detailItem.getTime()%60;
            String timeInfo;

            if(hour==0)
                timeInfo = minute+"분";
            else
                timeInfo = hour+"시간 "+minute+"분";

            subTime.setText(timeInfo);

            ArrayList<String> passStationList = detailItem.getPassStationArray();

            // 리사이클러뷰에 LinearLayoutManager 객체 지정.
            passListView.setLayoutManager(new LinearLayoutManager(itemView.getContext())) ;

            // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
            SimpleTextAdapter adapter = new SimpleTextAdapter(passStationList) ;
            Log.i("ADAPTER stationLIst", passStationList.toString());
            passListView.setAdapter(adapter) ;


            switch (detailItem.getImageType()){
                case 1:
                    subImage.setImageResource(R.drawable.line1);
                    endImage.setColorFilter(R.color.line1);
                    v.setBackgroundColor(itemView.getResources().getColor(R.color.line1));
                    //background.setColor(itemView.getResources().getColor(R.color.line1));

                    break;
                case 2:
                    subImage.setImageResource(R.drawable.line2);
                    endImage.setColorFilter(R.color.line2);
                    v.setBackgroundColor(itemView.getResources().getColor(R.color.line2));
                    //background.setColor(itemView.getResources().getColor(R.color.line2));
                    break;
                case 3:
                    subImage.setImageResource(R.drawable.line3);
                    endImage.setColorFilter(R.color.line3);
                    v.setBackgroundColor(itemView.getResources().getColor(R.color.line3));
                    //background.setColor(itemView.getResources().getColor(R.color.line3));

                    break;
                case 4:
                    subImage.setImageResource(R.drawable.line4);
                    endImage.setColorFilter(R.color.line4);
                    v.setBackgroundColor(itemView.getResources().getColor(R.color.line4));
                    //background.setColor(itemView.getResources().getColor(R.color.line4));
                    break;
                case 5:
                    subImage.setImageResource(R.drawable.line5);
                    endImage.setColorFilter(R.color.line5);
                    v.setBackgroundColor(itemView.getResources().getColor(R.color.line5));
                    //background.setColor(itemView.getResources().getColor(R.color.line5));
                    break;
                case 6:
                    subImage.setImageResource(R.drawable.line6);
                    endImage.setColorFilter(R.color.line6);
                    v.setBackgroundColor(itemView.getResources().getColor(R.color.line6));
                    //background.setColor(itemView.getResources().getColor(R.color.line6));
                    break;
                case 7:
                    subImage.setImageResource(R.drawable.line7);
                    endImage.setColorFilter(R.color.line7);
                    v.setBackgroundColor(itemView.getResources().getColor(R.color.line7));
                    //background.setColor(itemView.getResources().getColor(R.color.line7));
                    break;
                case 8:
                    subImage.setImageResource(R.drawable.line8);
                    endImage.setColorFilter(R.color.line8);
                    v.setBackgroundColor(itemView.getResources().getColor(R.color.line8));
                    //background.setColor(itemView.getResources().getColor(R.color.line8));
                    break;
                case 104:
                    subImage.setImageResource(R.drawable.line_gyeongui);
                    endImage.setColorFilter(R.color.line_gyeonui);
                    v.setBackgroundColor(itemView.getResources().getColor(R.color.line_gyeonui));
                    break;
                case 100:
                    subImage.setImageResource(R.drawable.line_bundang);
                    endImage.setColorFilter(R.color.line_bundang);
                    v.setBackgroundColor(itemView.getResources().getColor(R.color.line_bundang));
                    break;
                case 101:
                    subImage.setImageResource(R.drawable.line_airport);
                    endImage.setColorFilter(R.color.line_airport);
                    v.setBackgroundColor(itemView.getResources().getColor(R.color.line_airport));
                    break;
                case 113:
                    subImage.setImageResource(R.drawable.line_ui);
                    endImage.setColorFilter(R.color.line_ui);
                    v.setBackgroundColor(itemView.getResources().getColor(R.color.line_ui));
                    break;
                default:
                    subImage.setImageResource(R.drawable.line_default);
                    endImage.setColorFilter(R.color.line_default);
                    v.setBackgroundColor(itemView.getResources().getColor(R.color.line_default));
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
        Log.i("position", position+"");
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
