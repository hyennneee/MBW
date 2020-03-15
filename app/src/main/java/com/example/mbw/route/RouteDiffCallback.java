package com.example.mbw.route;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class RouteDiffCallback extends DiffUtil.Callback {
    private final List<Route> mOldRouteList;
    private final List<Route> mNewRouteList;

    public RouteDiffCallback(List<Route> oldRouteList, List<Route> newRouteList) {
        this.mOldRouteList = oldRouteList;
        this.mNewRouteList = newRouteList;
    }

    @Override
    public int getOldListSize() {
        return mOldRouteList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewRouteList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldRouteList.get(oldItemPosition).getId() == mNewRouteList.get(
                newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Route oldItem = mOldRouteList.get(oldItemPosition);
        final Route newItem = mNewRouteList.get(newItemPosition);

        return oldItem.getItemList().get(0).getTime() == newItem.getItemList().get(0).getTime();
    }

    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Route oldRoute = mOldRouteList.get(oldItemPosition);
        Route newRoute = mNewRouteList.get(newItemPosition);
        Bundle bundle = new Bundle();
        if (oldRoute.getItemList().get(0).getTime() != newRoute.getItemList().get(0).getTime()) {
            bundle.putInt("time", newRoute.getItemList().get(0).getTime());
            bundle.putString("currState", newRoute.getItemList().get(0).getRemainingTime());
        }
        return bundle;
    }
}