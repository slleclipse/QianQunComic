package com.example.qianxun.qianxuncomic;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qianxun.qianxuncomic.Service.DownloadService;
import com.example.qianxun.qianxuncomic.fragment.CallBack;
import com.example.qianxun.qianxuncomic.fragment.DownloadFragment;
import com.example.qianxun.qianxuncomic.model.Chapter;
import com.example.qianxun.qianxuncomic.model.Comic;
import com.example.qianxun.qianxuncomic.model.Download;
import com.example.qianxun.qianxuncomic.utils.DatabaseHelper;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadActivity extends AppCompatActivity implements CallBack {
    private List<Map<String, Object>> mData;
    private List<Map<String, Integer>> downloadChapter;
    private Comic comic;
    private ArrayList<String> chapter_num;
    private ArrayList<String> chapter_url;
    private ArrayList<String> downloaded_chapter_name;
    private ArrayList<String> downloaded_chapter_url;
    private Toolbar mtoolbar;
    private ListView listView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private DownloadFragment downloadFragment;


    private Button buttonText;
    private ImageView imageView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        downloadChapter = new ArrayList<>();
        listView = (ListView)findViewById(R.id.list);
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        mtoolbar.setTitle("下载");
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //获取传过来的信息
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("comic");
        comic = (Comic)bundle.get("comic");
        chapter_num = (ArrayList<String>)intent.getSerializableExtra("chapter_num");

        chapter_url = (ArrayList<String>)intent.getSerializableExtra("chapter_url");
        downloaded_chapter_name = new ArrayList<>();
        downloaded_chapter_url = new ArrayList<>();


        imageView2 =(ImageView)findViewById(R.id.image2);
        imageView2.setImageResource(R.drawable.select);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        downloadFragment = new DownloadFragment();
        Bundle args=new Bundle();
        args.putStringArrayList("chapter_num",chapter_num);
        downloadFragment.setArguments(args);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, downloadFragment);
        fragmentTransaction.commit();

        buttonText = (Button) findViewById(R.id.button_text);
        buttonText.setText(R.string.start_download);
        buttonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "正在下载",Toast.LENGTH_SHORT).show();
                DatabaseHelper databaseHelper = new DatabaseHelper(getBaseContext(),"QianXunComicDB");
                if(databaseHelper.getComic(comic.getName()).getName() != comic.getName())
                databaseHelper.addComic(databaseHelper.getWritableDatabase(),comic);


                for(int i=0; i<downloadChapter.size(); i++){
                    int chapterNum = downloadChapter.get(i).get("position");
                    Chapter chapter = new Chapter();
                    Download download = new Download();
                    download.setChapter_name(chapter_num.get(chapterNum));
                    downloaded_chapter_name.add(chapter_num.get(chapterNum));
                    downloaded_chapter_url.add(chapter_url.get(chapterNum));
                    download.setComic_name(comic.getName());
                    databaseHelper.addDownload(databaseHelper.getWritableDatabase(),download);
                }
                Intent intent=new Intent(DownloadActivity.this, DownloadService.class);

                intent.putExtra("comic_name",comic.getName());
                intent.putStringArrayListExtra("downloaded_chapter_name",downloaded_chapter_name);
                intent.putStringArrayListExtra("downloaded_chapter_url",downloaded_chapter_url);
                startService(intent);

            }
        });

    }

    @Override
    public void call(Bundle arg) {
        int position = arg.getInt("position");
        Log.i("position",Integer.toString(position));
        Map<String, Integer> map = new HashMap<String, Integer>();
        if(position >= 0) {
            map.put("position", position);
            downloadChapter.add(map);
        }else {
            downloadChapter.remove(-position);
        }
    }
}
