package com.example.qianxun.qianxuncomic.dialog;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.util.ArrayMap;
import android.view.View;
import android.widget.LinearLayout;

import com.example.qianxun.qianxuncomic.CatalogActivity;
import com.example.qianxun.qianxuncomic.ComicInfoActivity;
import com.example.qianxun.qianxuncomic.DownloadActivity;
import com.example.qianxun.qianxuncomic.R;
import com.example.qianxun.qianxuncomic.ReadingActivity;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.FlipEnter.FlipVerticalSwingEnter;
import com.flyco.dialog.widget.base.BottomBaseDialog;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ShareBottomDialog extends BottomBaseDialog<ShareBottomDialog> {
    @InjectView(R.id.brightness) LinearLayout brightness;
    @InjectView(R.id.screen_size) LinearLayout screenSize;
    @InjectView(R.id.catalog) LinearLayout catalog;
    @InjectView(R.id.download) LinearLayout download;

    public ShareBottomDialog(Context context, View animateView) {
        super(context, animateView);
    }

    public ShareBottomDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        showAnim(new FlipVerticalSwingEnter());
        dismissAnim(null);
        View inflate = View.inflate(mContext, R.layout.dialog_reading_top, null);
        ButterKnife.inject(this, inflate);

        return inflate;
    }

    @Override
    public void setUiBeforShow() {

        brightness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                BrightnessDialog bottomdialog = new BrightnessDialog(getContext());
                bottomdialog.showAnim(new BounceTopEnter())//
                        .show();

            }
        });
        screenSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if(getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                dismiss();
            }
        });
        catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent readingIntent = new Intent(getContext(), CatalogActivity.class);
                getContext().startActivity(readingIntent);
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent readingIntent = new Intent(getContext(), DownloadActivity.class);
                getContext().startActivity(readingIntent);
            }
        });
    }
    public static Activity getActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) { // 4.4 以下使用的是 HashMap
                activities = (HashMap) activitiesField.get(activityThread);
            }else{ // 4.4 以上使用的是 ArrayMap
                activities = (ArrayMap) activitiesField.get(activityThread);
            }
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused"); // 找到 paused 为 false 的activity
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
