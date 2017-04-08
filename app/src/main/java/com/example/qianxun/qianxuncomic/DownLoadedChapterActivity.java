package com.example.qianxun.qianxuncomic;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.qianxun.qianxuncomic.model.Chapter;
import com.example.qianxun.qianxuncomic.model.Comic;
import com.example.qianxun.qianxuncomic.utils.DatabaseHelper;

import java.util.List;

public class DownLoadedChapterActivity extends AppCompatActivity {
    private List<ApplicationInfo> mAppList;
    private AppAdapter mAdapter;
    private List<Chapter> listChapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_loaded_chapter);
        initBase();
    }
    private void initBase() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("下载章节");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAppList = getPackageManager().getInstalledApplications(0);

        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.listView);
        DatabaseHelper databaseHelper = new DatabaseHelper(getBaseContext(),"QianXunComicDB");

        Intent intent = getIntent();
        Log.i("comic_name", intent.getStringExtra("comic_name"));
        listChapter = databaseHelper.getDownloadComicChapter( intent.getStringExtra("comic_name"));
        mAdapter = new AppAdapter();
        listView.setAdapter(mAdapter);

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                switch (menu.getViewType()) {
                    case 0:
                        createMenu2(menu);
                        break;
                    case 1:
                        createMenu2(menu);
                        break;
                    case 2:
                        createMenu2(menu);
                        break;
                }
            }
            private void createMenu2(SwipeMenu menu) {
                SwipeMenuItem item2 = new SwipeMenuItem(
                        getApplicationContext());
                item2.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                item2.setWidth(dp2px(90));
                item2.setIcon(R.drawable.ic_action_discard);
                menu.addMenuItem(item2);
            }

        };
        // set creator
        listView.setMenuCreator(creator);

        // step 2. listener item click event

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // open
                        break;
                    case 1:
                        // delete
//					delete(item);
                        listChapter.remove(position);
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
    }
    class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listChapter.size();
        }

        @Override
        public Chapter getItem(int position) {
            return listChapter.get(position);
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
                convertView = View.inflate(getApplicationContext(),
                        R.layout.list_item, null);
                new ViewHolder(convertView);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();

            holder.tv_name.setText(listChapter.get(position).getChapter_name());
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

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
