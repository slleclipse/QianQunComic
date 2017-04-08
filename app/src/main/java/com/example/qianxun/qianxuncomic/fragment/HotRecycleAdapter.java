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
import com.example.qianxun.qianxuncomic.model.Comic;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sllEc_000 on 2017/2/16 0016.
 */

public class HotRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Comic> cartoonInfLists;

    //type
    public static final int TYPE_SLIDER = 0xff01;
    public static final int TYPE_TYPE2_HEAD = 0xff02;
    public static final int TYPE_TYPE2 = 0xff03;

    public HotRecycleAdapter(Context context,List<Comic> cartoonInfLists) {
        this.context = context;
        this.cartoonInfLists=cartoonInfLists;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_SLIDER:
                return new HolderSlider(LayoutInflater.from(parent.getContext()).inflate(R.layout.onerecycle_item_slider, parent, false));
            case TYPE_TYPE2_HEAD:
                return new HolderType2Head(LayoutInflater.from(parent.getContext()).inflate(R.layout.onerecycle_item_type2_head, parent, false));
            case TYPE_TYPE2:
                return new HolderType2(LayoutInflater.from(parent.getContext()).inflate(R.layout.onerecycle_item_type2, parent, false));
            default:
                Log.d("error","viewholder is null");
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HolderSlider){
            bindTypeSlider((HolderSlider) holder, position);
        }else if (holder instanceof HolderType2Head){
            bindType2Head((HolderType2Head) holder, position);
        }else if (holder instanceof HolderType2){
            bindType2((HolderType2) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return 15;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_SLIDER;
        }else if (position == 1){
            return TYPE_TYPE2_HEAD;
        }else if (2<=position && position <= 7){
            return TYPE_TYPE2;
        }else if (position == 8){
            return TYPE_TYPE2_HEAD;
        }else if (9<=position && position <= 14){
            return TYPE_TYPE2;
        }else{
            return TYPE_TYPE2;
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
                        case TYPE_SLIDER:
                            return gridManager.getSpanCount();
                        case TYPE_TYPE2_HEAD:
                            return gridManager.getSpanCount();
                        case TYPE_TYPE2:
                            return 2;
                        default:
                            return 3;
                    }
                }
            });
        }
    }

    /////////////////////////////

    private void bindTypeSlider(HolderSlider holder, int position){
        String img = "http://pic16.nipic.com/20110921/7247268_215811562102_2.jpg";
        String[] imgs= new String[]{"http://tkres.tukucc.com/cimg/2011/guhuozaiguoyu.jpg","http://tkres.tukucc.com/cimg/2013/12241.jpg","http://tkres.tukucc.com/images/upload/20161116/14792613645442.jpg","http://tkres.tukucc.com/images/upload/20151226/14511194473104.jpg","http://tkres.tukucc.com/images/upload/20160219/14558533784719.jpg"};
        holder.slideShowView.setImageUrls(imgs);
        holder.slideShowView.startPlay();
    }

    private void bindType2Head(HolderType2Head holder, int position){
    }

    private void bindType2(HolderType2 holder, int position){
        String img = "http://pica.nipic.com/2007-10-09/200710994020530_2.jpg";
       // x.image().bind(holder.item_img_type2, img,new ImageOptions.Builder().build(),new CustomBitmapLoadCallBack(holder.item_img_type2));
        final String img1=cartoonInfLists.get(position).getCover();
        final String name=cartoonInfLists.get(position).getName();
        final int id=cartoonInfLists.get(position).getComic_id();
        final String author=cartoonInfLists.get(position).getAuthor();
        final String introduce=cartoonInfLists.get(position).getIntroduce();
        holder.item_text_type2.setText(name);
        x.image().bind(holder.item_img_type2, img1,new ImageOptions.Builder().build(),new CustomBitmapLoadCallBack(holder.item_img_type2));
        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent infoIntent = new Intent(context, ComicInfoActivity.class);
                infoIntent.putExtra("id",id);
                infoIntent.putExtra("name",name);
                infoIntent.putExtra("author",author);
                infoIntent.putExtra("cover",img1);
                infoIntent.putExtra("introduce",introduce);
                context.startActivity(infoIntent);
            }
        });
    }


    /////////////////////////////

    public class HolderSlider extends RecyclerView.ViewHolder {
        public SlideShowView slideShowView;

        public HolderSlider(View itemView) {
            super(itemView);
            slideShowView = (SlideShowView) itemView.findViewById(R.id.slideShowView);
        }
    }

    public class HolderType2Head extends RecyclerView.ViewHolder {
        public HolderType2Head(View itemView) {
            super(itemView);
        }
    }
    public class HolderType2 extends RecyclerView.ViewHolder {
        public ImageView item_img_type2;
        public TextView item_text_type2;
        public HolderType2(View itemView) {
            super(itemView);
            item_img_type2 = (ImageView) itemView.findViewById(R.id.item_img_type2);
            item_text_type2=(TextView)itemView.findViewById(R.id.textView4);
        }
    }

}

