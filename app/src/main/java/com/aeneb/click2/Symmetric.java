package com.aeneb.click2;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 안드로이드 버전 7.0 이상부터 정상 작동합니다
 */
public class Symmetric extends AccessibilityService {
    FrameLayout mLayout;

    RelativeLayout center;

    AlwaysTopServiceTouch alwaysTopServiceTouch;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onServiceConnected() {
        // overlay & display 만들기
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayout = new FrameLayout(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.activity_start, mLayout);
        wm.addView(mLayout, lp);
        configureSwipeButton();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    private void configureSwipeButton() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TimerTask myTask = new TimerTask() {
                    public void run() {
                        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

                        int point_x = Integer.parseInt(pref.getString("x", "")) + 75;
                        int point_y = Integer.parseInt(pref.getString("y", "")) + 155;

                        if (Integer.parseInt(pref.getString("x", "")) == 0 || point_y == 0) {

                        } else {
                            // x좌표와 y좌표 클릭하기
                            Path swipePath = new Path();
                            swipePath.moveTo(point_x, point_y);
                            swipePath.lineTo(point_x, point_y);
                            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
                            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, 1));
                            dispatchGesture(gestureBuilder.build(), null, null);


                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("x", "0");
                            editor.putString("y", "0");
                            editor.apply();
                        }
                    }
                };
                Timer timer = new Timer();
                timer.schedule(myTask, 500, 500);
            }
        }, 100);
    }
}