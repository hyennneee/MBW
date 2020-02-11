package com.example.mbw.showPath;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbw.R;
import com.example.mbw.route.Route;
import com.example.mbw.route.RouteAdapter;

import java.util.ArrayList;

import static com.example.mbw.showPath.ShowPathActivity.routeArrayList;

public class FragmentBus extends Fragment {

    private ArrayList<Route> mArrayList;
    private RouteAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
 /*
        할 일:
        각 fragment(path, place, setting)에 recyclerview1을 깐다.
        recyclerView1에는 item1, 2, 3이 있음
        각 item에는 recyclerView2가 깔려있음
        recyclerView2에는 item1, 2, a가 있음(몇까지 있을지는 알 수 없음)
        각 item에 알맞은 데이터 입력해야됨(ex 버스, 지하철에 따라 입력 값 달라져 - RouteAdapter이용)
        데이터는 odsay에서 불러옴 -> 그럼 어디서 odsay를 호출해야될까? (MainActivity? RouteAdapter? Route? 각 Fragment? -> ShowPath에서 불러옴)

        1. odsay api 호출
        2. recycler view 정리
        3. route*/
        View v = inflater.inflate(R.layout.fragment_all, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_sub_item);


        //routeArrayList = new ArrayList<Route>();    //mArrayList의 내용을 채워야돼
        mAdapter = new RouteAdapter( routeArrayList);
        mRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        /*DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);*/


        //!지우면 안 됨!
        return v;
    }
}
