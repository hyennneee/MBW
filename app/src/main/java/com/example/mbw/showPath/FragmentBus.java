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

import com.example.mbw.R;
import com.example.mbw.route.Route;
import com.example.mbw.route.RouteAdapter;

import java.util.ArrayList;

public class FragmentBus extends Fragment {

    private ArrayList<Route> mArrayList;
    private RouteAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        RecyclerView mRecyclerView = (RecyclerView) getView().findViewById(R.id.recyclerview_main_list);
        mArrayList = new ArrayList<>();
        mAdapter = new RouteAdapter( mArrayList);
        mRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        return inflater.inflate(R.layout.fragment_bus, container, false);
    }
}
