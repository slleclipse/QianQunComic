package com.example.qianxun.qianxuncomic.utils;

import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by feng on 2017/2/21.
 */

public class Infor_communication {
    private static HttpClient mHttpClient = null;
    private static final String CHARSET = HTTP.UTF_8;
    String url=null;
    JSONObject jsonObject = null;
    public Infor_communication(String action , JSONObject obj ){
        url = action;
        jsonObject = obj;
    }
    public Infor_communication(String action ){
        url = action;
    }

    public   JSONObject getInfo() throws JSONException{
        String responseStr = "";
        JSONObject result= new JSONObject();
        try {

            // 不加这一段则会报错，错误信息为android.os.networkonmainthreadexception
            // 在Android2.2以后必须添加以下代码
            // 本应用采用的Android4.0
            // 设置线程的策略
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .permitAll() // 此处为允许磁盘读写和网络访问
                    .penaltyLog() // 打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
                    .build());

            HttpPost httpRequest = new HttpPost(url);
            //下面开始跟服务器传递数据，使用JSONObject
            StringEntity se = new StringEntity(jsonObject.toString());
            httpRequest.setEntity(se);

            mHttpClient = getSafeHttpClient();

            HttpResponse httpResponse = mHttpClient.execute(httpRequest);
            final int ret = httpResponse.getStatusLine().getStatusCode();
            if (ret == 200) {
                //得到一个应答的字符串，这也是一个json格式的字符串
                responseStr = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);

            } else {
                responseStr = "-1";
            }
            result = new JSONObject(responseStr);

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    public   byte[] getDownload(){

        try {

            // 不加这一段则会报错，错误信息为android.os.networkonmainthreadexception
            // 在Android2.2以后必须添加以下代码
            // 本应用采用的Android4.0
            // 设置线程的策略
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .permitAll() // 此处为允许磁盘读写和网络访问
                    .penaltyLog() // 打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
                    .build());

            HttpPost httpRequest = new HttpPost(url);

            mHttpClient = getSafeHttpClient();

            HttpResponse httpResponse = mHttpClient.execute(httpRequest);
            final int ret = httpResponse.getStatusLine().getStatusCode();
            if (ret == 200) {
                //得到一个应答的字符串，这也是一个json格式的字符串
                Log.i("url",url);
                return EntityUtils.toByteArray(httpResponse.getEntity());

            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    public   JSONArray getResult() throws JSONException{
        String responseStr = "";
        JSONArray result= new JSONArray();
        try {

            // 不加这一段则会报错，错误信息为android.os.networkonmainthreadexception
            // 在Android2.2以后必须添加以下代码
            // 本应用采用的Android4.0
            // 设置线程的策略
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .permitAll() // 此处为允许磁盘读写和网络访问
                    .penaltyLog() // 打印logcat，当然也可以定位到dropbox，通过文件保存相应的log
                    .build());

            HttpPost httpRequest = new HttpPost(url);
            httpRequest.addHeader("Content-Type","application/json;charset=utf-8");
            //下面开始跟服务器传递数据，使用JSONObject
            StringEntity se = new StringEntity(jsonObject.toString(),"utf-8");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
            httpRequest.setEntity(se);

            mHttpClient = getSafeHttpClient();

            HttpResponse httpResponse = mHttpClient.execute(httpRequest);
            final int ret = httpResponse.getStatusLine().getStatusCode();
            if (ret == 200) {
                //得到一个应答的字符串，这也是一个json格式的字符串
                responseStr = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);

            } else {
                responseStr = "-1";
            }
            result = new JSONArray(responseStr);

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }


    public static synchronized HttpClient getSafeHttpClient(){
        if(mHttpClient == null){
            HttpParams params = new BasicHttpParams();
            //设置基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, CHARSET);
            HttpProtocolParams.setUseExpectContinue(params, true);
            //超时设置
			/*从连接池中取连接的超时时间*/
            ConnManagerParams.setTimeout(params, 10000);
			/*连接超时*/
            HttpConnectionParams.setConnectionTimeout(params, 10000);
			/*请求超时*/
            HttpConnectionParams.setSoTimeout(params, 300000);
            //设置HttpClient支持HTTp和HTTPS两种模式
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
            //使用线程安全的连接管理来创建HttpClient
            ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
            mHttpClient = new DefaultHttpClient(conMgr, params);
        }
        return mHttpClient;
    }

}
