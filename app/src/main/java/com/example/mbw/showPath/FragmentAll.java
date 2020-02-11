package com.example.mbw.showPath;

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

import com.example.mbw.route.RouteAdapter;

import com.example.mbw.R;

import static com.example.mbw.showPath.ShowPathActivity.routeArrayList;

public class FragmentAll extends Fragment {
    private RouteAdapter mAdapter;

    //1-지하철, 2-버스, 3-버스+지하철
    //api에서 받아온 결과를 어떻게 arrayList에 넣지?


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_all, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_sub_item);

        //routeArrayList = new ArrayList<Route>();    //mArrayList의 내용을 채워야돼
        mAdapter = new RouteAdapter( routeArrayList);
        mRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }
}
