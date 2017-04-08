package com.example.qianxun.qianxuncomic;

import android.content.Intent;
import android.content.res.Configuration;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.qianxun.qianxuncomic.constant.Constant;
import com.example.qianxun.qianxuncomic.fragment.ReadingSlide;
import com.example.qianxun.qianxuncomic.fragment.SlideShowView;

import com.example.qianxun.qianxuncomic.model.Comic;

import com.example.qianxun.qianxuncomic.model.History;
import com.example.qianxun.qianxuncomic.utils.ChangeCharset;
import com.example.qianxun.qianxuncomic.utils.DatabaseHelper;
import com.example.qianxun.qianxuncomic.utils.Infor_communication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ReadingActivity extends AppCompatActivity {
    ReadingSlide slideShowView;
    List<String> imgs=null;
    public  int num=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        initView();
    }
    private void initView() {
        //存储到观看记录
        Toolbar mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent=getIntent();
        String name=intent.getStringExtra("name");
        Bundle bundle = intent.getBundleExtra("comic");
        Comic comic = (Comic)bundle.get("comic");
        History history = new History();
        history.setComic_name(name);
        history.setChapter_name("");
        DatabaseHelper databaseHelper = new DatabaseHelper(getBaseContext(),"QianXunComicDB");
        databaseHelper.addHistory(databaseHelper.getWritableDatabase(),history);
        if(databaseHelper.getComic(comic.getName()).getName() != comic.getName())
        databaseHelper.addComic(databaseHelper.getWritableDatabase(),comic);

        ArrayList<String> chapter_num=intent.getStringArrayListExtra("chapter_num");
        ArrayList<String> chapter_url=intent.getStringArrayListExtra("chapter_url");
        num=(int)intent.getSerializableExtra("num");
        mtoolbar.setTitle(name+"  "+chapter_num.get(chapter_num.size()-num));
        Log.i("reading url:",chapter_url.get(chapter_num.size()-num));
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //mtoolbar.setVisibility(View.INVISIBLE);
        JSONObject chapterUrl=new JSONObject();

        try {
            chapterUrl.put("chapterUrl",chapter_url.get(chapter_num.size()-num));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Infor_communication com=new Infor_communication("http://"+ Constant.ip+"/qianxun_background/cartooninf/cartoonReading",chapterUrl);
        try {
            JSONObject obj=(JSONObject)com.getInfo();
            Gson gson = new Gson();

            imgs=gson.fromJson(obj.get("imgs").toString(),new  TypeToken<ArrayList<String>>(){}.getType());
            //List<String> imgs_index=gson.fromJson(obj.get("imgs_index").toString(),new  TypeToken<ArrayList<String>>(){}.getType());

            Log.i(" imgs:",imgs.toString());
            ChangeCharset cc=new ChangeCharset();
            for(int i=0;i<imgs.size();i++){
                imgs.set(i,cc.chinese_to_utf8(imgs.get(i)));
            }
            Log.i(" utf-8:",imgs.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //String img = "http://pic16.nipic.com/20110921/7247268_215811562102_2.jpg"

        slideShowView = (ReadingSlide)findViewById(R.id.slideShowView);
        slideShowView.setImageSrcs(imgs);
        slideShowView.setChapter_num(chapter_num);
        slideShowView.setChapter_url(chapter_url);
        slideShowView.setNum(num+1);
        slideShowView.setMtoolbar(mtoolbar);
        slideShowView.setName(name);

    }
}
