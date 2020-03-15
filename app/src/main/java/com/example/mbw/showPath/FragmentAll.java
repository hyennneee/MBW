package com.example.mbw.showPath;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mbw.DetailPathActivity;
import com.example.mbw.TestActivity;
import com.example.mbw.route.RouteAdapter;

import com.example.mbw.R;
import com.google.gson.JsonObject;

import static com.example.mbw.showPath.ShowPathActivity.routeArrayList;

public class FragmentAll extends Fragment implements RouteAdapter.OnItemClickListener{
   static  private RouteAdapter mAdapter;
    static private RecyclerView mRecyclerView;

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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_sub_item);
        mAdapter = new RouteAdapter( routeArrayList, this);
        mRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }
    public void onItemClick(int position){
        //아이템 클릭 이벤트 처리
        String tmp[] = getArguments().getStringArray("PATH_INFO");
        JsonObject jsonData = ShowPathActivity.pathArray.get(position).getAsJsonObject();
        String detailPathData[] = new String[7];
        detailPathData[0] = jsonData.toString();
        int i = 1;
        for( String data : tmp ) {
            detailPathData[i++] = data;
        }

        Intent intent = new Intent(getActivity(), DetailPathActivity.class);
        intent.putExtra("DETAIL_PATH", detailPathData);
        startActivity(intent);
    }

    public void onRemainingChanged(DiffUtil.DiffResult diffResult){
        diffResult.dispatchUpdatesTo(mAdapter);
    }

    public RouteAdapter getAdapter(){
        return this.mAdapter;
    }

}
