package com.paul.simpletools;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.paul.simpletools.Fragment.MainFragmentActivity;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)//透明状态栏
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // 创建状态栏的管理实例
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // 激活状态栏设置
        tintManager.setStatusBarTintEnabled(true);
        // 激活导航栏设置
        tintManager.setNavigationBarTintEnabled(true);
        // 设置一个颜色给系统栏
        tintManager.setTintColor(Color.parseColor("#EE6AA7"));
        setContentView(R.layout.activity_launch);
        //Request();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //耗时任务
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //跳转至 MainActivity
                        //Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                        Intent intent = new Intent(LaunchActivity.this, MainFragmentActivity.class);
                        startActivity(intent);
                        //结束当前的 Activity
                        LaunchActivity.this.finish();
                    }
                });
            }
        }).start();
        /*Integer time = 1000;    //设置等待时间，单位为毫秒
        Handler handler = new Handler();
        //当计时结束时，跳转至主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                LaunchActivity.this.finish();
            }
        }, time);
    }*/

    }
}

