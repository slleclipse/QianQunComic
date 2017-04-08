package com.example.qianxun.qianxuncomic.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qianxun.qianxuncomic.R;
import com.example.qianxun.qianxuncomic.constant.Constant;
import com.example.qianxun.qianxuncomic.model.Comic;
import com.example.qianxun.qianxuncomic.utils.Infor_communication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HotFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{


    private View rootView = null;//缓存Fragment view
    private HotRecycleAdapter adapter;
    private SwipeRefreshLayout lay_fresh;
    private List<Comic> cartoonInfLists;
    public static HotFragment newInstance() {
        HotFragment f = new HotFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_hot, container, false);
        initBase();
        return rootView;
    }

    private void initBase() {
        lay_fresh = (SwipeRefreshLayout) this.rootView.findViewById(R.id.lay_refresh);
        lay_fresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        lay_fresh.setOnRefreshListener(this);
        Infor_communication com=new Infor_communication("http://"+ Constant.ip+"/qianxun_background/cartooninf/cartoonInfList2",new JSONObject());
        try {
            JSONObject obj=(JSONObject)com.getInfo();
            //  JSONArray array=new JSONArray();
            Gson gson = new Gson();
            cartoonInfLists=gson.fromJson(obj.get("result").toString(),new TypeToken<List<Comic>>(){}.getType());
            //Log.i(" cartoonInflists:",cartoonInfLists.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RecyclerView recyclerView = (RecyclerView) this.rootView.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 6, GridLayoutManager.VERTICAL, false));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        recyclerView.setAdapter(adapter = new HotRecycleAdapter(getActivity(),cartoonInfLists));
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lay_fresh.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }
        }, 1000);
    }

}
