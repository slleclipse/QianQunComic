package com.example.qianxun.qianxuncomic.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.qianxun.qianxuncomic.CatalogActivity;
import com.example.qianxun.qianxuncomic.MainActivity;
import com.example.qianxun.qianxuncomic.R;
import com.example.qianxun.qianxuncomic.ReadingActivity;
import com.example.qianxun.qianxuncomic.ResultActivity;
import com.example.qianxun.qianxuncomic.constant.Constant;
import com.example.qianxun.qianxuncomic.utils.Infor_communication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryFragment extends Fragment {

    private GridView gridview;
    private List<Map<String, Object>> mdata;
    private AppAdapter adapter;
    // 图片封装为一个数组
    private int[] icon = { R.drawable.love, R.drawable.campus,
            R.drawable.adventure, R.drawable.magic, R.drawable.terror,
            R.drawable.funny, R.drawable.beatuty, R.drawable.detective,
            R.drawable.action };
    private String[] iconName = { "恋爱", "校园", "冒险", "魔幻", "恐怖", "搞笑", "唯美",
            "侦探", "动作"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        gridview =(GridView) view.findViewById(R.id.category_view);
        mdata =  getData();
        adapter = new AppAdapter();
        gridview.setAdapter(adapter);
        return view;
    }

    public List<Map<String, Object>> getData(){
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(int i=0;i<icon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("title", iconName[i]);
            list.add(map);
        }

        return list;
    }
    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mdata.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(),
                        R.layout.category_item, null);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("searchContent",iconName[position]);
                            Log.i("searchContent","http://m.tuku.cc/comic/search?word="+iconName[position]+"/");
                            Infor_communication infor_communication = new Infor_communication("http://"+ Constant.ip+"/qianxun_background/cartoon/search",jsonObject);
                            JSONArray jsonArray = infor_communication.getResult();
                            Intent searchIntent = new Intent(getContext(),ResultActivity.class);
                            Log.i("searchResult",jsonArray.toString());
                            searchIntent.putExtra("searchResult",jsonArray.toString());
                            startActivity(searchIntent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                new ViewHolder(convertView);
            }
            // holder.img.setBackgroundResource((Integer)mData.get(position).get("img"));

            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.iv_icon.setImageResource((int) mdata.get(position).get("image"));
            holder.tv_name.setText((String)mdata.get(position).get("title"));
            return convertView;
        }

        class ViewHolder {
            ImageView iv_icon;
            TextView tv_name;

            public ViewHolder(View view) {
                iv_icon = (ImageView) view.findViewById(R.id.image);
                tv_name = (TextView) view.findViewById(R.id.text);
                view.setTag(this);
            }
        }
    }


}
