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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentPath extends Fragment implements RecordAdapter.OnItemClickListener {
    static  private RecordAdapter mAdapter;
    static private RecyclerView mRecyclerView;
    RouteDBHelper routeDB;
    ArrayList<RouteDB> ArrayListrouteDB;
    ServiceApi service;
    boolean getData = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        service = RetrofitClient.getClient().create(ServiceApi.class);
        return inflater.inflate(R.layout.fragment_path, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        GetBookmarkTask bookmarkTask = new GetBookmarkTask(MainActivity.getToken());
        bookmarkTask.execute();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.routeBMView);
        //routeDB = new RouteDBHelper(view.getContext());
        //ArrayListrouteDB = routeDB.getAllRoutesBM();

    }

    private class GetBookmarkTask extends AsyncTask<Void, Void, Void> {
        private  String token;

        public GetBookmarkTask(String token) {
            this.token = token;
            ArrayListrouteDB = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            service.getFavoritePath(token).enqueue(new Callback<LocationResponse>() {
                @Override
                public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                    LocationResponse result = response.body();
                    boolean success = response.isSuccessful();
                    if(success) {
                        int code = result.getCode();
                        if (code == 200) {   //즐겨찾는 장소 조회 성공
                            JsonArray data = result.getData();
                            if(data != null) {
                                try {
                                    for (JsonElement jsonElement : data) {
                                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                                        String departure, destination, SX, SY, EX, EY;
                                        departure = jsonObject.get("startAddress").getAsString();
                                        destination = jsonObject.get("endAddress").getAsString();
                                        SX = jsonObject.get("SX").getAsString();
                                        SY = jsonObject.get("SY").getAsString();
                                        EX = jsonObject.get("EX").getAsString();
                                        EY = jsonObject.get("EY").getAsString();
                                        RouteDB routeDB = new RouteDB(departure, destination, SX, SY, EX, EY);
                                        ArrayListrouteDB.add(routeDB);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else{
                                Toast.makeText(getContext(), "에러 발생", Toast.LENGTH_SHORT).show();
                            }
                        }
                        getData = true;
                    }
                    else {
                        try {
                            setError(response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        getData = true;
                    }
                }
                @Override
                public void onFailure(Call<LocationResponse> call, Throwable t) {
                    getData = true;
                    Toast.makeText(getContext(), "통신 에러 발생", Toast.LENGTH_SHORT).show();
                    Log.e("통신 에러 발생", t.getMessage());
                }
            });
            while(true){
                if(getData)
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Toast.makeText(getContext(), "" + ArrayListrouteDB.size(), Toast.LENGTH_SHORT).show();
            if(ArrayListrouteDB.size() != 0) {

                mAdapter = new RecordAdapter(ArrayListrouteDB, FragmentPath.this);
                mRecyclerView.setAdapter(mAdapter);
                LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLinearLayoutManager);

                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                        mLinearLayoutManager.getOrientation());
                mRecyclerView.addItemDecoration(dividerItemDecoration);
            }


        }

    }

    public void setError(Response<LocationResponse> response) throws IOException {
        Gson gson = new Gson();

        LocationResponse result = gson.fromJson(response.errorBody().string(), LocationResponse.class);
        Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
    }


    public void onItemClick(int position){
        //아이템 클릭 이벤트 처리
    }
}