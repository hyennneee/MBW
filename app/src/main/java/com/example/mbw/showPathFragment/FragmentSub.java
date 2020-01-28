package com.example.mbw.showPathFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbw.R;
import com.example.mbw.route.Route;
import com.example.mbw.route.RouteAdapter;

import java.util.ArrayList;

public class FragmentSub extends Fragment {

    private ArrayList<Route> mArrayList;
    private RouteAdapter mAdapter;
    private int count = -1;
    //private TextView ex;
    //api에서 받아온 결과를 어떻게 arrayList에 넣지?


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView mRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview_main_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        //        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //        recyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        //ex = getView().findViewById(R.id.toOffice);

        mArrayList = new ArrayList<>();

        mAdapter = new RouteAdapter( mArrayList);
        mRecyclerView.setAdapter(mAdapter);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);



        Button buttonInsert = (Button) getView().findViewById(R.id.button_main_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count++;

                Route data = new Route("정류장 번호", "노선번호", "지하철역이름", 7, 1, 102000139, 4, true);// = new Route(count+"전체", count, "환승횟수" + count, "카드", "정류장", "버스번호", "역", count);

                //mArrayList.add(0, dict); //RecyclerView의 첫 줄에 삽입
                mArrayList.add(data); // RecyclerView의 마지막 줄에 삽입

                mAdapter.notifyDataSetChanged();             }
        });

        return inflater.inflate(R.layout.fragment_sub, container, false);

    }

}
