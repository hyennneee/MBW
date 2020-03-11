package com.example.mbw.myPageFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbw.DB.DBvalue;
import com.example.mbw.DB.RecordAdapter;
import com.example.mbw.DB.RouteDBHelper;
import com.example.mbw.R;

import java.util.ArrayList;

public class FragmentPath extends Fragment implements RecordAdapter.OnItemClickListener {
    static  private RecordAdapter mAdapter;
    static private RecyclerView mRecyclerView;
    RouteDBHelper routeDB;
    ArrayList<DBvalue> ArrayListrouteDB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_path, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.routeBMView);
        routeDB = new RouteDBHelper(view.getContext());
        ArrayListrouteDB = routeDB.getAllRoutesBM();
        mAdapter = new RecordAdapter( ArrayListrouteDB, this);
        mRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    public void onItemClick(int position){
        //아이템 클릭 이벤트 처리
    }
}