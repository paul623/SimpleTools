package com.paul.simpletools.Tools;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.paul.simpletools.R;
import com.paul.simpletools.classbox.model.SuperLesson;
import com.paul.simpletools.dataBase.MySubject;
import com.zhuangfei.timetable.model.Schedule;

import java.util.ArrayList;
import java.util.List;

public class ViewCourseActivity extends Activity {
    SuperTextView mcourse, mteacher, mlocation, mdate, mtime, mbtn1, mbtn2;
    Integer cur_week;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course);
        List<Schedule> superLesson = (List<Schedule>) getIntent().getSerializableExtra("SuperLessons");
        Integer date = getIntent().getIntExtra("date", 0);
        if (date % 2 == 0&&superLesson.size()>1) {
            cur_week = 1;
        }
        else
        {
            cur_week = 0;
        }
        initView();
        setView(superLesson);
        //display(superLesson);
    }

    private void initView() {
        mcourse = findViewById(R.id.stv_view_course);
        mlocation = findViewById(R.id.stv_view_location);
        mteacher = findViewById(R.id.stv_view_teacher);
        mdate = findViewById(R.id.stv_view_date);
        mtime = findViewById(R.id.stv_view_time);
        /*mbtn1 = findViewById(R.id.btn_view_confirm);
        mbtn2 = findViewById(R.id.btn_view_delete);
        mbtn1.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                Intent intent = new Intent(ViewCourseActivity.this, EditCourseActivity.class);
                startActivity(intent);
            }
        });
        mbtn2.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                //删除函数，目前不会
            }
        });*/
    }

    private void setView(List<Schedule> superLesson) {
        int endDay=superLesson.get(cur_week).getStart()+superLesson.get(cur_week).getStep()-1;
        mcourse.setLeftString(superLesson.get(cur_week).getName());
        mlocation.setLeftString(superLesson.get(cur_week).getRoom());
        mteacher.setLeftString(superLesson.get(cur_week).getTeacher());
        mdate.setLeftString(superLesson.get(cur_week).getWeekList().toString());
        mtime.setLeftString("周"+superLesson.get(cur_week).getDay()+" 第"+superLesson.get(cur_week).getStart()+"~"+endDay+"节");

    }
}
