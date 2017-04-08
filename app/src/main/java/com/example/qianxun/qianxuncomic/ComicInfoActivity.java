package com.example.qianxun.qianxuncomic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qianxun.qianxuncomic.constant.Constant;
import com.example.qianxun.qianxuncomic.fragment.ComicInfoFragment;
import com.example.qianxun.qianxuncomic.model.Chapter;
import com.example.qianxun.qianxuncomic.model.Comic;
import com.example.qianxun.qianxuncomic.utils.Infor_communication;
import com.example.qianxun.qianxuncomic.widget.BottomBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComicInfoActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Button buttonText;
    private ImageView imageView1;
    private ImageView imageView2;
    private Comic comic;
    private Chapter chapter;
    String name;
    private ArrayList<String> chapter_num=null;
    private ArrayList<String> chapter_url=null;
    ComicInfoFragment comicInfoFragment;
    BottomBar bottomBar;
    boolean falg = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_info);
        Toolbar mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent intent = getIntent();
        name= (String)intent.getSerializableExtra("name");
//        int id=(int)intent.getSerializableExtra("id");
        chapter = new Chapter();
        String author= (String)intent.getSerializableExtra("author");
        String cover= (String)intent.getSerializableExtra("cover");
        final String introduce= (String)intent.getSerializableExtra("introduce");
        mtoolbar.setTitle(name);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        JSONObject img=new JSONObject();

        try {
            img.put("img",introduce);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(" 看漫画url:",introduce);
        Infor_communication com=new Infor_communication("http://"+ Constant.ip+"/qianxun_background/cartooninf/cartoonInfnext",img);
        try {
            JSONObject obj=(JSONObject)com.getInfo();
            Gson gson = new Gson();

            comic=gson.fromJson(obj.get("cartooninf").toString(),Comic.class);
            comic.setUrl(introduce);
            comic.setName(name);
            comic.setAuthor(author);
            comic.setCover(cover);
            chapter_num=gson.fromJson(obj.get("chapter_num").toString(),new  TypeToken<ArrayList<String>>(){}.getType());
            Collections.reverse(chapter_num);
            comic.setChapter(chapter_num.size());
            chapter_url=gson.fromJson(obj.get("chapter_url").toString(),new  TypeToken<ArrayList<String>>(){}.getType());
            Collections.reverse(chapter_url);
            Log.i(" chapter_num:",chapter_num.toString());
            Log.i(" chapter_url:",chapter_url.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        imageView1 =(ImageView)findViewById(R.id.image1);
        imageView1.setImageResource(R.drawable.collect);
        imageView1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)  {

                final SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                String count = sharedPreferences.getString("count", "null");
                if (!count.equals("null")) {
                    JSONObject collect = new JSONObject();
                    try {
                        collect.put("count", count);
                        collect.put("cartoonname", name);
                        collect.put("url",introduce);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.i("收藏漫画的name=",name);
                    if(!falg){
                        try {
                            JSONObject result = new Infor_communication("http://"+ Constant.ip+"/qianxun_background/user_cartoon/add", collect).getInfo();
                            String responstr = result.getString("res");

                            if (responstr.equals("success")) {
                                Toast.makeText(ComicInfoActivity.this, "收藏成功!", Toast.LENGTH_LONG).show();
                                imageView1.setImageResource(R.drawable.collected);
                                falg=true;
                            } else {
                                Toast.makeText(ComicInfoActivity.this, "收藏失败!", Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            JSONObject res = new Infor_communication("http://"+ Constant.ip+"/qianxun_background/user_cartoon/delete", collect).getInfo();
                            String restr = res.getString("result");
                            if (restr.equals("success")) {
                                Toast.makeText(ComicInfoActivity.this, "取消收藏成功!", Toast.LENGTH_LONG).show();
                                imageView1.setImageResource(R.drawable.waitcollect);
                                falg = false;
                            } else {
                                Toast.makeText(ComicInfoActivity.this, "取消收藏失败!", Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }

                }else{
                    Toast.makeText(ComicInfoActivity.this,"请先登录!",Toast.LENGTH_LONG).show();
                }
            }
        });


        imageView2 =(ImageView)findViewById(R.id.image2);
        imageView2.setImageResource(R.drawable.download);
        //漫画下载事件
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent downloadIntent = new Intent(ComicInfoActivity.this, DownloadActivity.class);
                downloadIntent.putExtra("chapter_num",chapter_num);
                downloadIntent.putStringArrayListExtra("chapter_url",chapter_url);
                Bundle bundle = new Bundle();
                bundle.putSerializable("comic",comic);
                downloadIntent.putExtra("comic",bundle);
                startActivity(downloadIntent);
            }
        });



        buttonText = (Button) findViewById(R.id.button_text);
        buttonText.setText(R.string.start_reading);
        buttonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent readingIntent = new Intent(ComicInfoActivity.this, ReadingActivity.class);
                readingIntent.putStringArrayListExtra("chapter_num",chapter_num);
                readingIntent.putStringArrayListExtra("chapter_url",chapter_url);
                Bundle bundle = new Bundle();
                bundle.putSerializable("comic",comic);
                readingIntent.putExtra("num",1);
                readingIntent.putExtra("name",name);
                readingIntent.putExtra("comic",bundle);
                startActivity(readingIntent);
            }
        });

//        Intent intent = getIntent();
//        String img = (String)intent.getSerializableExtra("image");


        comicInfoFragment = new ComicInfoFragment();
        Bundle args=new Bundle();
        //args.putInt("id",id);
        args.putString("name",name);
        args.putString("author",comic.getAuthor());
        args.putString("cover",cover);
        args.putString("introduce",comic.getIntroduce());
        comicInfoFragment.setArguments(args);
        comicInfoFragment.setChapter_num(chapter_num);
        comicInfoFragment.setChapter_url(chapter_url);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, comicInfoFragment);
        fragmentTransaction.commit();

    }
}
