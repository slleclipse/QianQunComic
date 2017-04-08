package com.example.qianxun.qianxuncomic.fragment;


import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qianxun.qianxuncomic.DownloadActivity;
import com.example.qianxun.qianxuncomic.MainActivity;
import com.example.qianxun.qianxuncomic.R;
import com.example.qianxun.qianxuncomic.model.Comic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private List<Map<String, Object>> mData;
    private ArrayList<String> chapter_num;
    private View rootView = null;
    private SwipeRefreshLayout lay_fresh;
    private ListView listView;
    private ArrayList<Integer> downloadChapter;
    private CallBack callBack=null;
    AppAdapter adapter;
    public DownloadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_download, container, false);
        initBase();
        return rootView;
    }
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        callBack=(DownloadActivity)activity;
    }

    private void initBase() {
        Bundle args=getArguments();
        chapter_num =  args.getStringArrayList("chapter_num");
        listView = (ListView)rootView.findViewById(R.id.list);
        lay_fresh = (SwipeRefreshLayout) this.rootView.findViewById(R.id.lay_refresh);
        lay_fresh.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        lay_fresh.setOnRefreshListener(this);
        mData = getData();
        adapter = new AppAdapter();
        listView.setAdapter(adapter);

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
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "第一话");
        //map.put("img", R.drawable.i1);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "第一话");
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "第一话");
        list.add(map);

        return list;
    }



    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return chapter_num.size();
        }

        @Override
        public ApplicationInfo getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            // menu type count
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            // current menu type
            return position % 3;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(),
                        R.layout.list_item, null);
                convertView.setOnClickListener(new ClickListener(convertView,position));
                new AppAdapter.ViewHolder(convertView);
            }
            // holder.img.setBackgroundResource((Integer)mData.get(position).get("img"));

            ViewHolder holder = (ViewHolder) convertView.getTag();
            ApplicationInfo item = getItem(position);
            //  holder.iv_icon.setImageDrawable(item.loadIcon(getPackageManager()));
            holder.tv_name.setText(chapter_num.get(position));
            return convertView;
        }

        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;

            public ViewHolder(View view) {
                iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                tv_name = (TextView) view.findViewById(R.id.tv_name);
                view.setTag(this);
            }
        }
        class ClickListener implements View.OnClickListener {
            View view;
            int position;
            ClickListener(View view,int position){
                this.view = view;
                this.position = position;
            }
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();



                Drawable background =v.getBackground();
                ColorDrawable colorDrawable = (ColorDrawable)background;
                if(colorDrawable.getColor() == getResources().getColor(R.color.white)){
                    v.setBackgroundColor(getResources().getColor(R.color.placeholder_light_color));
                    bundle.putInt("position",position);
                }else if(colorDrawable.getColor() == getResources().getColor(R.color.placeholder_light_color)){
                    v.setBackgroundColor(getResources().getColor(R.color.white));
                    bundle.putInt("position",-position);
                }
                callBack.call(bundle);
            }
        }
    }
}
