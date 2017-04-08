package com.example.qianxun.qianxuncomic.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.example.qianxun.qianxuncomic.R;
import com.example.qianxun.qianxuncomic.constant.Constant;
import com.example.qianxun.qianxuncomic.dialog.ShareBottomDialog;
import com.example.qianxun.qianxuncomic.utils.ChangeCharset;
import com.example.qianxun.qianxuncomic.utils.Infor_communication;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * ViewPager实现的轮播图广告自定义视图，如京东首页的广告轮播图效果；
 * 既支持自动轮播页面也支持手势滑动切换页面
 *
 *
 */

public class ReadingSlide extends FrameLayout {

    // 使用universal-image-loader插件读取网络图片，需要工程导入universal-image-loader-1.8.6-with-sources.jar
//	private ImageLoader imageLoader = ImageLoader.getInstance();
//	private BitmapUtils bitmapUtils;
    //轮播图图片数量
    private final static int IMAGE_COUNT = 4;
    //自动轮播的时间间隔
    private final static int TIME_INTERVAL = 5;
    //自动轮播启用开关
    private final static boolean isAutoPlay = false;

    //跳转监听器
    private OnClickListener goListener;

    //自定义轮播图的资源
    private List<String> imageUrls;
    //    private int[] imageSrcs;
    //放轮播图片的ImageView 的list
    private List<ImageView> imageViewsList;
   // private List<ImageView> imageViewsList_copy;
    private ViewPager viewPager;
    //当前轮播页
    private int currentItem  = 0;
    //定时任务
    private ScheduledExecutorService scheduledExecutorService;
    private Toolbar mtoolbar;
    private static Context context;
    private ArrayList<String> chapter_num=null;
    private ArrayList<String> chapter_url=null;

    public void setMtoolbar(Toolbar mtoolbar) {
        this.mtoolbar = mtoolbar;
    }

    public void setNum(int num) {
        ReadingSlide.num = num;
    }

    private static int num=2;
    private  boolean temp=false;

    public void setName(String name) {
        this.name = name;
    }

    private String name="";
    public void setChapter_num(ArrayList<String> chapter_num) {
        this.chapter_num = chapter_num;
    }

    public void setChapter_url(ArrayList<String> chapter_url) {
        this.chapter_url = chapter_url;
    }
    GetListTask l;
    //Handler
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            viewPager.setCurrentItem(currentItem);
        }

    };

    public ReadingSlide(Context context) {
        this(context,null);
    }
    public ReadingSlide(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public ReadingSlide(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (!isInEditMode()) {
            this.context = context;
//	        this.bitmapUtils = BitmapHelp.getBitmapUtils();
//	        this.bitmapUtils.configDefaultLoadingImage(R.drawable.none);
//	    	this.bitmapUtils.configDefaultLoadFailedImage(R.drawable.none);

            initData();

            // 一步任务获取图片
            l=new GetListTask();
            l.execute("");
            if (isAutoPlay) {
                startPlay();
            }
        }
    }
    /**
     * 开始轮播图切换
     */
    public void startPlay(){
        if (scheduledExecutorService==null) {
            Log.i(" startplay()","ahaha");
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 4, 4, TimeUnit.SECONDS);
        }
    }
    /**
     * 停止轮播图切换
     */
    private void stopPlay(){
        scheduledExecutorService.shutdown();
    }
    /**
     * 初始化相关Data
     */
    private void initData(){
        imageViewsList = new ArrayList<ImageView>();
    }
    /**
     * 初始化Views等UI
     */
    private void initUI(Context context){
        if(imageUrls == null || imageUrls.size() == 0)
            return;
        if(!temp)
        LayoutInflater.from(context).inflate(R.layout.reading_slide, this, true);

        if(imageViewsList!=null){
            imageViewsList.clear();
        }
        // 热点个数与图片特殊相等
        for (int i = 0; i < imageUrls.size(); i++) {
            ImageView view =  new ImageView(context);
            Log.i("InitUi set tag:",imageUrls.get(i));
            view.setTag(imageUrls.get(i));
//        	if(i==0)//给一个默认图
//        		view.setBackgroundResource(R.drawable.defalt);
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//        	view.setScaleType(ScaleType.FIT_START);

            imageViewsList.add(view);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.leftMargin = 8;
            params.rightMargin = 8;
        }
        //////////////////////////

        if (imageViewsList.size()>0 && goListener!=null) {
            imageViewsList.get(imageViewsList.size()-1).setOnClickListener(goListener);
        }
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        viewPager.setFocusable(true);

        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setOnPageChangeListener(new MyPageChangeListener());

    }

    public void setOnGolistener(OnClickListener goListener) {
        this.goListener = goListener;
    }

    /**
     * 填充ViewPager的页面适配器
     *
     */
    private class MyPagerAdapter  extends PagerAdapter {

        @Override
        public void destroyItem(View container, int position, Object object) {
            // TODO Auto-generated method stub
          ((ViewPager)container).removeView((View)object);
         Log.i(" destory remoview",((ImageView)object).getTag().toString());
        //  ((ViewPager)container).removeView(imageViewsList.get(position));
            Log.i(" position-i:",""+position);
           // ((ViewPager)container).removeViewAt(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = imageViewsList.get(position);
            Log.i(" instantiateItem:",imageView.getTag().toString());

            if (!MyStrUtil.isEmpty(imageUrls)) {
                //TODO load img
                Log.i(" build:",imageUrls.toString());
                ImageOptions imageOptions = new ImageOptions.Builder()
//                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
//                .setRadius(DensityUtil.dip2px(5))
                        // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                        .setCrop(true)
                        // 加载中或错误图片的ScaleType
                        //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                        .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//                .setLoadingDrawableId(R.mipmap.ic_launcher)
//                .setFailureDrawableId(R.mipmap.ic_launcher)
                        .build();
                x.image().bind(imageView, imageView.getTag().toString(),imageOptions,new CustomBitmapLoadCallBack(imageView));
        			//bitmapUtils.display(imageView, imageView.getTag().toString());
            }
            //imageView.setBackgroundResource(R.color.white);
            imageView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                   // Toolbar mtoolbar = (Toolbar)findViewById(R.id.toolbar);
                   // mtoolbar.setBackgroundColor(R.color.dialog);
                 //   mtoolbar.setVisibility(View.VISIBLE);
                    ShareBottomDialog bottomdialog = new ShareBottomDialog(context);
                    bottomdialog.showAnim(new BounceTopEnter())
                            .show();

                }
            });
         /*   Log.i(" temp:",""+temp);
            if(temp)
            {
                container.removeAllViews();
                Log.i(" remove all view","");
                temp=false;
            }*/
          //  imageView.invalidate();
            container.addView(imageView);
          //  container.invalidate();

            return imageView;
        }


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return imageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }
        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

    }
    /**
     * ViewPager的监听器
     * 当ViewPager中页面的状态发生改变时调用
     *
     */
    private class MyPageChangeListener implements OnPageChangeListener{

        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
            switch (arg0) {
                case 1:// 手势滑动，空闲中
                    isAutoPlay = false;
                    break;
                case 2:// 界面切换中
                    isAutoPlay = true;
                    break;
                case 0:// 滑动结束，即切换完毕或者加载完毕
                        // 当前为最后一张，此时从右向左滑，则切换到第一张
                        if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !isAutoPlay) {
                            Log.i("page and reload data :",""+viewPager.getCurrentItem());

                            if(mtoolbar!=null) {
                                Log.i(" mtoolbar", ":null");
                                mtoolbar.setTitle(name+"  "+chapter_num.get(chapter_num.size() - num));
                            }
                            Log.i(" chapter_num",chapter_num.toString());
                            Log.i(" num:",""+num);
                            temp=true;
                            viewPager.removeAllViews();
                            destoryBitmaps();

                            Log.i("currentItem:",""+currentItem);
                            JSONObject chapterUrl = new JSONObject();


                                try {
                                    chapterUrl.put("chapterUrl", chapter_url.get(chapter_num.size() - num));
                                    num--;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Infor_communication com = new Infor_communication("http://"+ Constant.ip+"/qianxun_background/cartooninf/cartoonReading", chapterUrl);
                                try {
                                    JSONObject obj = (JSONObject) com.getInfo();
                                    Gson gson = new Gson();

                                    ArrayList<String> imgs = gson.fromJson(obj.get("imgs").toString(), new TypeToken<ArrayList<String>>() {
                                    }.getType());
                                    //List<String> imgs_index=gson.fromJson(obj.get("imgs_index").toString(),new  TypeToken<ArrayList<String>>(){}.getType());

                                    Log.i(" imageUrls:", imgs.toString());
                                    ChangeCharset cc = new ChangeCharset();
                                    imageUrls.clear();
                                    for (int i = 0; i < imgs.size(); i++) {
                                        imgs.set(i, cc.chinese_to_utf8(imgs.get(i)));
                                        imageUrls.add(imgs.get(i));
                                    }
                                    Log.i(" utf-8:", imageUrls.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            /*initUI(context);*/
                            l.cancel(true);
                            l=null;
                            l=new GetListTask();
                            l.execute("");
                           /* if (isAutoPlay) {
                                startPlay();
                            }*/

                            Log.i("page and reload 22:",""+viewPager.getCurrentItem());
                           // viewPager.getAdapter().notifyDataSetChanged();
                            //viewPager.invalidate();
                           // viewPager.refreshDrawableState();
                            //viewPager.setCurrentItem(0);
                        }
                        // 当前为第一张，此时从左向右滑，则切换到最后一张
                        else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                            viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                        }

                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int pos) {
            // TODO Auto-generated method stub
        }

    }

    /**
     *执行轮播图切换任务
     *
     */
    private class SlideShowTask implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            synchronized (viewPager) {
                currentItem = (currentItem+1)%imageViewsList.size();
                handler.obtainMessage().sendToTarget();
            }
        }

    }

    /**
     * 销毁ImageView资源，回收内存
     *
     */
    private void destoryBitmaps() {

        for (int i = 0; i < imageViewsList.size(); i++) {
            ImageView imageView = imageViewsList.get(i);
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                //解除drawable对view的引用
                Log.i(" 解除dreable:",imageView.getTag().toString());
                drawable.setCallback(null);
            }
        }
    }

    /**
     * 释放资源，在activity结束时请调用
     */
    public void destory() {
        stopPlay();
        destoryBitmaps();

    }

    public void setImageSrcs(List<String> imageSrcs) {
        if (MyStrUtil.isEmpty(imageSrcs)) {
            return;
        }
        imageUrls = new ArrayList<String>();
        for (int i = 0; i < imageSrcs.size(); i++) {
            imageUrls.add(imageSrcs.get(i));
        }
    }

    public void setImageUrls(List<String> imageSrcs) {
        this.imageUrls = imageUrls;
    }

    /**
     * 异步任务,获取数据
     *
     */
    class GetListTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                initUI(context);
            }
        }
    }

}