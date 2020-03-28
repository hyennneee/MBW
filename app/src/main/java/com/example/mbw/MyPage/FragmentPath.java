package com.example.mbw.MyPage;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbw.DB.RecordAdapter;
import com.example.mbw.DB.RouteDB;
import com.example.mbw.DB.RouteDBHelper;
import com.example.mbw.MainActivity;
import com.example.mbw.MyPage.data.LocationResponse;
import com.example.mbw.R;
import com.example.mbw.network.RetrofitClient;
import com.example.mbw.network.ServiceApi;

public class FragmentPath extends Fragment implements RecordAdapter.OnItemClickListener {
    static  private RecordAdapter mAdapter;
    static private RecyclerView mRecyclerView;

    ServiceApi service;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        service = RetrofitClient.getClient().create(ServiceApi.class);
        return inflater.inflate(R.layout.fragment_path, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.routeBMView);

        mAdapter = new RecordAdapter(MyPageActivity.ArrayListrouteDB, FragmentPath.this);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        //routeDB = new RouteDBHelper(view.getContext());
        //ArrayListrouteDB = routeDB.getAllRoutesBM();
    }




    public void onItemClick(int position){
        //아이템 클릭 이벤트 처리
    }
}