package com.example.hanmaku.suwanemupotter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity{
    /** スレッドUI操作用ハンドラ */
    private Handler mHandler = new Handler();
    /** テキストオブジェクト */
    private Runnable updateView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MYDEBUG","APLスタート");
        setContentView(R.layout.activity_main);
        //Intent intent = new Intent(MainActivity.this, daiya.class);
        //startActivity(intent);
        updateView = new Runnable() {
            public void run() {
                finish();
                Intent intent=null;
                intent = new Intent(MainActivity.this,UserInput.class);
                startActivity(intent);
            }
        };
        mHandler.postDelayed(updateView, 2000);
    }
}
