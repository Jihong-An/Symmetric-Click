package com.aeneb.click2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, AlwaysTopServiceTouch.class));

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("x", "0");
        editor.putString("y", "0");
        editor.apply();
    }

//    public void mStart(View v) {
//        startService(new Intent(this, AlwaysTopServiceTouch.class));
//    }
//
//    public void mStop(View v) {
//        stopService(new Intent(this, AlwaysTopServiceTouch.class));
//    }
}