package com.example.mbw.DB;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbw.R;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {
    private ArrayList<RouteDB> recordList;
    private OnItemClickListener mListener;

    public RecordAdapter(ArrayList<RouteDB> recordList, OnItemClickListener onItemClickListener) {
        this.recordList = recordList;
        this.mListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_record, viewGroup, false);
        return new RecordViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder recordViewHolder, int position) {
        RouteDB route = recordList.get(position);
        //routeViewHolder가 Null이라고 함
        //ToDo: setText
        String dept, dest;
        dept = route.getDeparture();
        dest = route.getDestination();
        recordViewHolder.departure.setText(dept);
        recordViewHolder.destination.setText(dest);
    }

    // Create sub item view adapter

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    class RecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView departure, destination;
        OnItemClickListener onItemClickListener;

        RecordViewHolder(View itemView, OnItemClickListener onItemClickListener) {    //layout에 보여주기
            super(itemView);
            departure = itemView.findViewById(R.id.searchDeparture);
            destination = itemView.findViewById(R.id.searchDestination);

            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position) ;
    }

        // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }
}
