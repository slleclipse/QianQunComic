package com.example.qianxun.qianxuncomic;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.qianxun.qianxuncomic.fragment.CustomBitmapLoadCallBack;
import com.example.qianxun.qianxuncomic.model.Comic;
import com.example.qianxun.qianxuncomic.utils.Infor_communication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {
    private JSONArray mData;
    private Toolbar mtoolbar;
    private ListView listView;
    private AppAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initBase();
    }

    private void initBase() {
        try {
        Intent searchIntent = getIntent();
        String searchResult = (String) searchIntent.getSerializableExtra("searchResult");
        Log.i("searchResult",searchResult);
            Log.i("searchResult","84651327846513");
            mData = new JSONArray(searchResult);
            listView = (ListView)findViewById(R.id.list);
            mtoolbar = (Toolbar) findViewById(R.id.toolbar);
            mtoolbar.setTitle("结果");
            setSupportActionBar(mtoolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // mData = getData();
            adapter = new AppAdapter();
            listView.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "斗破苍穹");
        map.put("image", R.drawable.love);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "霸道总裁");
        map.put("image", R.drawable.campus);
        list.add(map);

        map = new HashMap<String, Object>();
        map.put("title", "砍头狂魔");
        map.put("image", R.drawable.terror);
        list.add(map);

        return list;
    }

    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mData.length();
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
            try {
               final String cover = (String) mData.getJSONObject(position).get("cover");
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(),
                        R.layout.list_item, null);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent infoIntent = new Intent(ResultActivity.this, ComicInfoActivity.class);

                        try {
                            infoIntent.putExtra("name",(String)mData.getJSONObject(position).get("name"));
                            infoIntent.putExtra("author","dasf");
                            infoIntent.putExtra("cover",cover);
                            infoIntent.putExtra("introduce",(String)mData.getJSONObject(position).get("url"));
                            startActivity(infoIntent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                new ViewHolder(convertView);
            }
            // holder.img.setBackgroundResource((Integer)mData.get(position).get("img"));

            ViewHolder holder = (ViewHolder) convertView.getTag();
                x.image().bind(holder.iv_icon, cover,new ImageOptions.Builder().build(),new CustomBitmapLoadCallBack(holder.iv_icon));
                holder.tv_name.setText((String)mData.getJSONObject(position).get("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
    }
}
