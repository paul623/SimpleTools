package com.paul.simpletools.Tools;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

import com.allen.library.SuperTextView;
import com.paul.simpletools.Adapter.LessonTimeSelectAdapter;
import com.paul.simpletools.R;
import com.paul.simpletools.dataBase.MySupport;



import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class LessonTimeSelectActivity extends Activity {

    private ArrayList<String> lesson = new ArrayList<>();
    private ArrayList<String> time = new ArrayList<>();
    private ListView mLv;
    private SuperTextView superTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_time_select);
        mLv = findViewById(R.id.lv_1);
        mLv.setDividerHeight(1);
        /*
         * 将对话框的大小按屏幕大小的百分比设置
         */
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)//透明状态栏
        {
            Window window = this.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(Color.parseColor("#FFFFFF"));
        }
        initData();
    }
    private void initData() {
        String[] lessons = {"第一节课", "第二节课", "第三节课", "第四节课", "第五节课", "第六节课", "第七节课", "第八节课",
                "第九节课", "第十节课", "第十一节课", "第十二节课",};
        for (int i = 0; i < lessons.length; i++) {
            lesson.add(lessons[i]);
        }
        String[] times = toolsHelper.getTimeTable(LessonTimeSelectActivity.this);
        if(times==null)
        {
            times= MySupport.DEFAULT_COURSE_STARTTIME;
        }
        for (int i = 0; i < times.length; i++) {
            time.add(times[i]);
        }
        final LessonTimeSelectAdapter adapter = new LessonTimeSelectAdapter(LessonTimeSelectActivity.this, lesson, time);
        mLv.setAdapter(adapter);
        superTextView = findViewById(R.id.lesson_time_title);
        superTextView.setRightImageViewClickListener(new SuperTextView.OnRightImageViewClickListener() {
            @Override
            public void onClickListener(ImageView imageView) {
                ArrayList<String> aaa=adapter.getTimes();
                toolsHelper.SaveTimeTable(aaa,LessonTimeSelectActivity.this);
                Toasty.success(LessonTimeSelectActivity.this,"设置成功！",Toasty.LENGTH_SHORT).show();
                Intent intent = new Intent();
                //把返回数据存入Intent
                intent.putExtra("result", "OK");
                //设置返回数据
                LessonTimeSelectActivity.this.setResult(RESULT_OK, intent);
                //关闭Activity
                finish();

            }
        });
        superTextView.setLeftImageViewClickListener(new SuperTextView.OnLeftImageViewClickListener() {
            @Override
            public void onClickListener(ImageView imageView) {
                finish();
            }
        });

    }
}
