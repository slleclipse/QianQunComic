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
import com.example.qianxun.qianxuncomic.model.Comic;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ComicInfoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private View rootView = null;//缓存Fragment view
    private InfoRecycleAdapter adapter;
    private SwipeRefreshLayout lay_fresh;
    private Comic comic;

    public void setChapter_url(ArrayList<String> chapter_url) {
        this.chapter_url = chapter_url;
    }

    public void setChapter_num(ArrayList<String> chapter_num) {
        this.chapter_num = chapter_num;
    }

    ArrayList<String> chapter_num=null;
    ArrayList<String> chapter_url=null;
    public static ComicInfoFragment newInstance() {
        ComicInfoFragment f = new ComicInfoFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_comic_info, container, false);
        initBase();
        return rootView;
    }

    private void initBase() {

        Bundle args=getArguments();
        String image = args.getString("cover");
        //int id=args.getInt("id");
        String author=args.getString("author");
        String name=args.getString("name");
        String introduce=args.getString("introduce");
        comic = new Comic();
        comic.setName(name);
        comic.setAuthor(author);
        comic.setIntroduce(introduce);
        comic.setCover(image);
        lay_fresh = (SwipeRefreshLayout) this.rootView.findViewById(R.id.lay_refresh);
        lay_fresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        lay_fresh.setOnRefreshListener(this);




        RecyclerView recyclerView = (RecyclerView) this.rootView.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 6, GridLayoutManager.VERTICAL, false));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        recyclerView.setAdapter(adapter = new InfoRecycleAdapter(getActivity(),comic,chapter_num,chapter_url));
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
