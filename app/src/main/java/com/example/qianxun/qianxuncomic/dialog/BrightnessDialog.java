package com.example.qianxun.qianxuncomic.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.example.qianxun.qianxuncomic.R;
import com.example.qianxun.qianxuncomic.ReadingActivity;
import com.flyco.animation.FlipEnter.FlipVerticalSwingEnter;
import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.flyco.dialog.widget.base.TopBaseDialog;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BrightnessDialog extends BottomBaseDialog<BrightnessDialog> {
    @InjectView(R.id.brightness) SeekBar brightness;
    public BrightnessDialog(Context context, View animateView) {
        super(context, animateView);
    }
    public BrightnessDialog(Context context) {
        super(context);
    }
    @Override
    public View onCreateView() {
        showAnim(new FlipVerticalSwingEnter());
        dismissAnim(null);
        View inflate = View.inflate(mContext, R.layout.dialog_seekbar, null);
        ButterKnife.inject(this,inflate);
        brightness.setMax(255);
        brightness.setProgress(getSystemBrightness());
        brightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                //setScreenBrightness((float)brightness.getProgress()/255);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
               // setScreenBrightness((float)brightness.getProgress()/255);
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub

                setScreenBrightness((float)progress/255);
            }
        });
        return inflate;
    }
    private void setScreenBrightness(float b){
        //取得window属性保存在layoutParams中
        WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
        Settings.System.putInt(getContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS_MODE,Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
      // int brightness = Settings.System.getInt(getContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,0);
        if(b == -1){
            layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        }else {
            layoutParams.screenBrightness = b;
            getActivity().getWindow().setAttributes(layoutParams);
           // Settings.System.putInt(getContext().getContentResolver(),Settings.System.SCREEN_BRIGHTNESS,brightness);
        }
    }
    private int getSystemBrightness() {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
              return systemBrightness;
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
    @Override
    public void setUiBeforShow() {

    }
}
