package com.example.qianxun.qianxuncomic.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.qianxun.qianxuncomic.R;
import com.example.qianxun.qianxuncomic.constant.Constant;
import com.example.qianxun.qianxuncomic.model.Chapter;
import com.example.qianxun.qianxuncomic.utils.ChangeCharset;
import com.example.qianxun.qianxuncomic.utils.DatabaseHelper;
import com.example.qianxun.qianxuncomic.utils.Infor_communication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class DownloadService extends Service {

    private String comic_name;
    private  String savePath;
    private ArrayList<String> downloaded_chapter_name;
    private ArrayList<String> downloaded_chapter_url;
    private final  String url ="http://pica.nipic.com/2007-10-09/200710994020530_2.jpg";
    private Bitmap bitmap;
    public DownloadService() {
    }


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if(msg.what==1)
            {
                stopSelf();//停止当前的Service
                Toast.makeText(getApplicationContext(), "下载完毕",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    public class MyThread implements Runnable{
       // String savePath;
       /* MyThread(String savePath){
            this.savePath = savePath;
        }*/
        @Override
        public void run() {
            Log.i("mian savePath",savePath);
            DatabaseHelper databaseHelper = new DatabaseHelper(getBaseContext(),"QianXunComicDB");
            Log.i("size",Integer.toString(downloaded_chapter_url.size()));
            for(int i=0; i<downloaded_chapter_url.size(); i++){
                Log.i("chapterurl",downloaded_chapter_url.get(i));
                List<String> pageUrlList = getPageUrl(downloaded_chapter_url.get(i));
                //将一章节中所有图片下载下来
                for(int j=0 ; j<pageUrlList.size(); j++) {
                    String fileName = getFileName();
                    Chapter chapter = new Chapter();
                    chapter.setChapter(i);
                    chapter.setChapter_name(downloaded_chapter_name.get(i));
                    chapter.setChapter_url(downloaded_chapter_url.get(i));
                    chapter.setPage_url(pageUrlList.get(j));
                    chapter.setPage_address(savePath+fileName);
                    databaseHelper.addChapter(databaseHelper.getWritableDatabase(),chapter);//存储章节信息
                    //下载漫画到本机
                    byte[] result = getImg(pageUrlList.get(j));
                    bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
                    saveFile(bitmap,fileName);
                }
            }
        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        //开启线程
        savePath = this.getFilesDir()+"/download/";
        comic_name = intent.getStringExtra("comic_name");
        downloaded_chapter_name = intent.getStringArrayListExtra("downloaded_chapter_name");
        downloaded_chapter_url = intent.getStringArrayListExtra("downloaded_chapter_url");
        new Thread(new MyThread()).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub

        return null;
    }
    //根据时间命名图片
    public String getFileName(){
        Random random = new Random();
        String fileName = String.valueOf(random.nextInt(Integer.MAX_VALUE));
        return  fileName+".jpg";
    }

    //获取章节图面URL
    public List<String> getPageUrl(String chapterUrl){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("chapterUrl",chapterUrl);
            Infor_communication infor_communication = new Infor_communication("http://"+ Constant.ip+"/qianxun_background/cartooninf/cartoonReading" ,jsonObject);

            JSONObject obj=(JSONObject)infor_communication.getInfo();
            Gson gson = new Gson();

            List<String> pageUrl=gson.fromJson(obj.get("imgs").toString(),new  TypeToken<ArrayList<String>>(){}.getType());

            Log.i(" imgs:",pageUrl.toString());
            ChangeCharset changeCharset=new ChangeCharset();
            for(int i=0;i<pageUrl.size();i++){
                pageUrl.set(i,changeCharset.chinese_to_utf8(pageUrl.get(i)));
            }
            return  pageUrl;
        } catch (JSONException e) {
            e.printStackTrace();
            return  null;
        }

    }

    //保存图片到本地
    public void saveFile(Bitmap bm, String imgName){
        String savePath =this.getFilesDir()+"/download/";
        File dirFile = new File(savePath);
        if(!dirFile.exists()){
            dirFile.mkdir();
        }

        File myFile = new File(savePath+imgName);
        try{
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myFile));
            bm.compress(Bitmap.CompressFormat.JPEG,80,bos);
            bos.flush();
            bos.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    //获取图片
    public byte[] getImg(String imgPath){
        try{
            URL url = new URL(imgPath);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5*1000);
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                return readStream(is);
            }
        }catch (MalformedURLException mue){
            //URL抛出的异常
            mue.printStackTrace();
        }catch (IOException ie){
            //HttpURLConnection抛出的异常。
            ie.printStackTrace();
        }
        return null;
    }
    //将图片流转化为数据
    public static byte[] readStream(InputStream inputStream){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len =0;
        try{
            while ((len = inputStream.read(buffer))!=-1){
                byteArrayOutputStream.write(buffer,0,len);
            }
            byteArrayOutputStream.close();
            inputStream.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }
}

