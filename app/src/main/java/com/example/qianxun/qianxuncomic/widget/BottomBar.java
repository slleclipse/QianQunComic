package com.example.qianxun.qianxuncomic.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.qianxun.qianxuncomic.R;

/**
 * Created by sllEc_000 on 2017/2/17 0017.
 */

public class BottomBar extends FrameLayout{
    private Button buttonText;

    private ImageView imageView1;
    private ImageView imageView2;
    public BottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.bottom_bar, this);
        //titleText = (TextView) findViewById(R.id.title_text);
        imageView1 =(ImageView)findViewById(R.id.image1);
        imageView2 =(ImageView)findViewById(R.id.image2);
        buttonText = (Button) findViewById(R.id.button_text);
        buttonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) getContext()).finish();
            }
        });
    }

    public void setButtonText(String text) {
        buttonText.setText(text);
    }

    public void setImageView1(int id) {
        imageView1.setImageResource(id);
    }
    public void setImageView2(int id) {
        imageView2.setImageResource(id);
    }
}
