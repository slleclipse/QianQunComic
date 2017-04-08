package com.example.qianxun.qianxuncomic.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qianxun.qianxuncomic.ComicInfoActivity;
import com.example.qianxun.qianxuncomic.R;
import com.example.qianxun.qianxuncomic.ReadingActivity;
import com.example.qianxun.qianxuncomic.model.Comic;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sllEc_000 on 2017/2/17 0017.
 */

public class InfoRecycleAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private Comic comic;

    //type
    public static final int TYPE_SLIDER = 0xff01;
    public static final int TYPE_TYPE_HEAD = 0xff02;
    public static final int TYPE_TYPE3 = 0xff03;
    public static final int TYPE_TYPE4 = 0xff04;
    ArrayList<String> chapter_num;
    ArrayList<String> chapter_url;
    public InfoRecycleAdapter(Context context, Comic comic, ArrayList<String> chapter_num, ArrayList<String> chapter_url) {
        this.context = context;
        this.comic = comic;
        this.chapter_num=chapter_num;
        this.chapter_url=chapter_url;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_TYPE_HEAD:
                return new HolderType2Head(LayoutInflater.from(parent.getContext()).inflate(R.layout.onerecycle_item_head, parent, false));
            case TYPE_TYPE3:
                return new HolderType3(LayoutInflater.from(parent.getContext()).inflate(R.layout.onerecycle_item_type3, parent, false));
            case TYPE_TYPE4:
                return new HolderType4(LayoutInflater.from(parent.getContext()).inflate(R.layout.onerecycle_item_episode, parent, false));
            default:
                Log.d("error","viewholder is null");
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HolderType4){
            bindType4((HolderType4) holder, position);
        }else if (holder instanceof HolderType2Head){
            bindType2Head((HolderType2Head) holder, position);
        }else if (holder instanceof HolderType3){
            bindType3((HolderType3) holder, position);
        }
    }

    @Override
    public int getItemCount() {
//        if(chapter_num.size()>20)
//            return 4+20;
//        else
            return 4+chapter_num.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_TYPE3;
        }else if(position == 1){
            return TYPE_TYPE_HEAD;
        }else if(position == 2){
            return TYPE_TYPE_HEAD;
        }else if(position == 3){
            return TYPE_TYPE_HEAD;
        }else{
            return TYPE_TYPE4;
        }
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    switch (type){
                        case TYPE_TYPE4:return 1;
                        case TYPE_TYPE_HEAD:return gridManager.getSpanCount();
                        case TYPE_TYPE3:
                            return gridManager.getSpanCount();
                        default:
                            return gridManager.getSpanCount();
                    }
                }
            });
        }
    }

    /////////////////////////////

    private void bindType4(InfoRecycleAdapter.HolderType4 holder, final int position){
        String img = "http://pic16.nipic.com/20110921/7247268_215811562102_2.jpg";
        String[] imgs= new String[]{img,img,img,img,img,img,img};
      //  holder.slideShowView.setImageUrls(imgs);
        //holder.slideShowView.startPlay();
        holder.episode.setText(chapter_num.get(position-4));
        holder.episode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent readingIntent = new Intent(context, ReadingActivity.class);
                readingIntent.putStringArrayListExtra("chapter_num",chapter_num);
                readingIntent.putStringArrayListExtra("chapter_url",chapter_url);
                Log.i(" num:",""+(position-5));
                readingIntent.putExtra("num",chapter_num.size()-1-(position-5));
                readingIntent.putExtra("name",comic.getName());
                context.startActivity(readingIntent);
            }
        });
    }

    private void bindType2Head(InfoRecycleAdapter.HolderType2Head holder, int position){
        if(position == 2){
            holder.title.setText(comic.getIntroduce());
        }else if(position == 1){
            holder.title.setText(R.string.introduction);
        }else{
            holder.title.setText(R.string.catalog);
        }
    }

    private void bindType3(InfoRecycleAdapter.HolderType3 holder, int position){
       String img = comic.getCover();
        holder.item_text_type3.setText("作者："+comic.getAuthor());
        x.image().bind(holder.item_img_type3, img,new ImageOptions.Builder().build(),new CustomBitmapLoadCallBack(holder.item_img_type3));
    }


    /////////////////////////////

    public static class HolderType4 extends RecyclerView.ViewHolder {
        public TextView episode;

        public HolderType4(View itemView) {
            super(itemView);
            episode = (TextView) itemView.findViewById(R.id.text_episode);
        }
    }

    public static class HolderType2Head extends RecyclerView.ViewHolder {
        public TextView title;
        public HolderType2Head(View itemView) {
            super(itemView);
            title =  (TextView) itemView.findViewById(R.id.title);
        }
    }
    public static class HolderType3 extends RecyclerView.ViewHolder {
        public ImageView item_img_type3;
        public TextView item_text_type3;
        public HolderType3(View itemView) {
            super(itemView);
            item_img_type3 = (ImageView) itemView.findViewById(R.id.item_img);
            item_text_type3=(TextView)itemView.findViewById(R.id.title_tv);
        }
    }
}
