package com.aeneb.click2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class AlwaysTopServiceTouch extends Service {

    // 대칭클릭의 범위를 보여주는 View 이다
    private View mView_Range;
    private WindowManager mManager_Range;
    private WindowManager.LayoutParams mParams_Range;

    // 대칭클릭의 중심점
    private View mView_Center;
    private WindowManager mManager_Center;
    private WindowManager.LayoutParams mParams_Center;

    // 대칭클릭 드로어
    private View mView_Drawer;
    private WindowManager mManager_Drawer;
    private WindowManager.LayoutParams mParams_Drawer;

    // 대칭클릭 타겟
    private View mView_Target;
    private WindowManager mManager_Target;
    private WindowManager.LayoutParams mParams_Target;


    // 그냥 클릭
    private View mView_Click;
    private WindowManager mManager_Click;
    private WindowManager.LayoutParams mParams_Click;


    // 당긴 원과 대칭 원을 연결해주는 선
    Line mView_Line;
    private WindowManager mManager_Line;
    private WindowManager.LayoutParams mParams_Line;

    private float mTouchX, mTouchY;
    private int mViewX, mViewY;
    private int mViewDX, mViewDY;

    private boolean isMove = false;

    int x;
    int y;

    int viewWidth = 0;
    int viewHeight = 0;

    // Handler
    Handler handler;

    Vibrator vibrator;

    Symmetric symmetric;


    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();

        symmetric = new Symmetric();

        // Always On Top
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Range
        mView_Range = mInflater.inflate(R.layout.sy_range, null);

        mView_Range.setOnTouchListener(mViewTouchListener);

        mParams_Range = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams_Range.gravity = Gravity.CENTER | Gravity.CENTER;

        mManager_Range = (WindowManager) getSystemService(WINDOW_SERVICE);
        mManager_Range.addView(mView_Range, mParams_Range);


        // Center
        mView_Center = mInflater.inflate(R.layout.sy_center, null);

        mView_Center.setOnTouchListener(mViewTouchListener);

        mParams_Center = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams_Center.gravity = Gravity.TOP | Gravity.LEFT;

        mManager_Center = (WindowManager) getSystemService(WINDOW_SERVICE);
        mManager_Center.addView(mView_Center, mParams_Center);


        // 드로어
        mView_Drawer = mInflater.inflate(R.layout.sy_drawer, null);

        mView_Drawer.setOnTouchListener(mViewTouchListener);

        mParams_Drawer = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams_Drawer.gravity = Gravity.TOP | Gravity.LEFT;

        mManager_Drawer = (WindowManager) getSystemService(WINDOW_SERVICE);
        mManager_Drawer.addView(mView_Drawer, mParams_Drawer);


        // 타겟
        mView_Target = mInflater.inflate(R.layout.sy_target, null);

        mView_Target.setOnTouchListener(mViewTouchListener);

        mParams_Target = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams_Target.gravity = Gravity.TOP | Gravity.LEFT;

        mManager_Target = (WindowManager) getSystemService(WINDOW_SERVICE);
        mManager_Target.addView(mView_Target, mParams_Target);


        // 클릭
        mView_Click = mInflater.inflate(R.layout.sy_click, null);

        mView_Click.setOnTouchListener(mViewTouchListener);

        mParams_Click = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mParams_Click.gravity = Gravity.TOP | Gravity.LEFT;

        mManager_Click = (WindowManager) getSystemService(WINDOW_SERVICE);
        mManager_Click.addView(mView_Click, mParams_Click);

        // 안보이게 하기
        mView_Center.setVisibility(View.GONE);
        mView_Drawer.setVisibility(View.GONE);
        mView_Target.setVisibility(View.GONE);
        // mView_Click.setVisibility(View.GONE);

        // 라인
        mView_Line = new Line(this);

        mView_Line.setOnTouchListener(mViewTouchListener);

        mParams_Line = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        // mParams_Line.gravity = Gravity.CENTER | Gravity.CENTER;

        // 라인
        mManager_Line = (WindowManager) getSystemService(WINDOW_SERVICE);
        mManager_Line.addView(mView_Line, mParams_Line);

        mView_Line.setLine(0, 0, 0, 0);
        mView_Line.setVisibility(View.GONE);

        // 지연을 위한 핸들러
        handler = new Handler();

        // 진동을 위한 vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // 값 저장
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
    }

    // 세로화면이면 사용 불가능함
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                // 안보이게 바꾸기
                mView_Range.setVisibility(View.GONE);
                mView_Center.setVisibility(View.GONE);
                mView_Drawer.setVisibility(View.GONE);
                mView_Target.setVisibility(View.GONE);
                // mView_Click.setVisibility(View.GONE);
                mView_Line.setVisibility(View.GONE);
                break;

            case Configuration.ORIENTATION_PORTRAIT:
                // 보이게 바꾸기
                mView_Range.setVisibility(View.VISIBLE);
                break;

        }
    }

    boolean isSCUse = false;

    int xClick = 0;
    int yClick = 0;

    int lastX = 0;
    int lastY = 0;

    private OnTouchListener mViewTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, final MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    xClick = (int) (event.getRawX());
                    yClick = (int) (event.getRawY());

                    mTouchX = event.getRawX();
                    mTouchY = event.getRawY();

                    isSCUse = false;

                    mView_Target.setVisibility(View.GONE);

                    mParams_Target.x = 0;
                    mParams_Target.y = 0;
                    mManager_Target.updateViewLayout(mView_Target, mParams_Target);

                    mParams_Center.x = 0;
                    mParams_Center.y = 0;
                    mManager_Center.updateViewLayout(mView_Center, mParams_Center);

                    mParams_Drawer.x = 0;
                    mParams_Drawer.y = 0;
                    mManager_Drawer.updateViewLayout(mView_Drawer, mParams_Drawer);

                    mView_Line.setLine(0, 0, 0, 0);

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (isMove == false) {
                                mTouchX = event.getRawX();
                                mTouchY = event.getRawY();

                                mViewX = mParams_Center.x;
                                mViewY = mParams_Center.y;

                                mViewDX = mParams_Drawer.x;
                                mViewDY = mParams_Drawer.y;

                                x = (int) (event.getRawX());
                                y = (int) (event.getRawY());

                                mView_Center.setVisibility(View.VISIBLE);
                                mParams_Center.x = x - 75;
                                mParams_Center.y = y - 155;
                                mManager_Center.updateViewLayout(mView_Center, mParams_Center);

                                mParams_Center.x = x - 75;
                                mParams_Center.y = y - 155;
                                mManager_Center.updateViewLayout(mView_Center, mParams_Center);

                                mParams_Drawer.x = x - 75;
                                mParams_Drawer.y = y - 155;
                                mManager_Drawer.updateViewLayout(mView_Drawer, mParams_Drawer);

                                mView_Drawer.setVisibility(View.VISIBLE);
                                mView_Target.setVisibility(View.VISIBLE);
                                mView_Line.setVisibility(View.VISIBLE);

                                vibrator.vibrate(10);
                                isSCUse = true;
                            }
                        }
                    }, 800);
                    break;
                case MotionEvent.ACTION_UP:
                    editor.putString("x", mParams_Target.x + "");
                    editor.putString("y", mParams_Target.y + "");
                    editor.apply();

                    handler.removeMessages(0);
                    // 안보이게 바꾸기
                    mView_Center.setVisibility(View.GONE);
                    mView_Drawer.setVisibility(View.GONE);
                    mView_Target.setVisibility(View.GONE);
                    mView_Line.setVisibility(View.GONE);

                    mParams_Center.x = 0;
                    mParams_Center.y = 0;
                    mManager_Center.updateViewLayout(mView_Center, mParams_Center);

                    mParams_Drawer.x = 0;
                    mParams_Drawer.y = 0;
                    mManager_Drawer.updateViewLayout(mView_Drawer, mParams_Drawer);

                    mView_Line.setLine(0, 0, 0, 0);

                    if (isSCUse == false) {
                        mView_Range.setVisibility(View.GONE);
                        mView_Click.setVisibility(View.GONE);

                        mParams_Click.x = (int) event.getRawX() - 75;
                        mParams_Click.y = (int) event.getRawY() - 155;
                        mManager_Click.updateViewLayout(mView_Click, mParams_Click);

                        editor.putString("x", mParams_Click.x + "");
                        editor.putString("y", mParams_Click.y + "");
                        editor.apply();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mView_Range.setVisibility(View.VISIBLE);
                            }
                        }, 1500);
                    }

                    isSCUse = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    isMove = true;

                    int xd = (int) (event.getRawX() - mTouchX);
                    int yd = (int) (event.getRawY() - mTouchY);

                    final int num = 30;

                    if ((xd > -num && xd < num) && (yd > -num && yd < num)) {
                        isMove = false;
                    } else {
                        isMove = true;
                    }

                    if (isSCUse == false && isMove == true) {
                        mView_Range.setVisibility(View.GONE);

                        editor.putString("x", mParams_Click.x + "");
                        editor.putString("y", mParams_Click.y + "");
                        editor.apply();

                        break;
                    } else {
                        mParams_Drawer.x = x + xd - 120;
                        mParams_Drawer.y = y + yd - 200;
                        mManager_Drawer.updateViewLayout(mView_Drawer, mParams_Drawer);

                        mParams_Target.x = x - (2 * xd) - 120;
                        mParams_Target.y = y - (2 * yd) - 200;
                        mManager_Target.updateViewLayout(mView_Target, mParams_Target);

                        mView_Line.setLine(x + xd - 60, y + yd - 110, x - (2 * xd) - 60, y - (2 * yd) - 110);
                    }
                    break;
            }
            return true;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView_Range != null) {
            mManager_Range.removeView(mView_Range);
            mView_Range = null;
        }

        if (mView_Center != null) {
            mManager_Center.removeView(mView_Center);
            mView_Center = null;
        }

        if (mView_Drawer != null) {
            mManager_Drawer.removeView(mView_Drawer);
            mView_Drawer = null;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    // 화면 크기 가져오기
    public void getScreenSize(int width, int height) {
        viewWidth = width;
        viewHeight = height;
    }

    // 원과 원 사이를 연결하는 선을 만드는 class
    public class Line extends View {
        int screenHeight = 0;
        int screenWidth = 0;
        int viewHeight = 0;
        int viewWidth = 0;

        // 휴대폰 화면 크기 구하기
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // 폰 화면 크기
            this.screenWidth = MeasureSpec.getSize(widthMeasureSpec);
            this.screenHeight = MeasureSpec.getSize(heightMeasureSpec);

            // 화면에 그려질 키보드 크기
            this.viewWidth = this.screenWidth;
            this.viewHeight = this.screenHeight;

            setMeasuredDimension(viewWidth, viewHeight);

            getScreenSize(viewWidth, viewHeight);
        }

        int a;
        int b;
        int c;
        int d;

        // setLine - 각 원의 x, y를 저장하고 화면을 갱신한다
        public void setLine(int p_x, int p_y, int s_x, int s_y) {
            a = p_x;
            b = p_y;
            c = s_x;
            d = s_y;

            invalidate();
        }

        public Line(Context context) {
            super(context);
        }

        // onDraw
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint Pnt = new Paint();

            // 화면 그리기
            Pnt.setColor(Color.parseColor("#3F51B5"));
            Pnt.setStrokeWidth(8);
            canvas.drawLine(a, b, c, d, Pnt);
        }
    }
}