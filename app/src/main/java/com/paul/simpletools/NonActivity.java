package com.paul.simpletools;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paul.simpletools.Adapter.NonViewAdapter;
import com.paul.simpletools.R;
import com.paul.simpletools.dataBase.MySubject;
import com.paul.simpletools.dataBase.SubjectRepertory;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.model.ScheduleSupport;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.zhuangfei.timetable.model.ScheduleSupport.isThisWeek;

public class NonActivity extends AppCompatActivity {
    private static final String LOCAL_COURSE="local_course";
    Button moreButton;
    List<MySubject> schedules;
    ListView listView;
    NonViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non);
        moreButton = findViewById(R.id.pop_non);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopmenu();
            }
        });

        schedules = new ArrayList<>();
        listView = findViewById(R.id.id_listview);
        adapter = new NonViewAdapter(this, schedules);
        listView.setAdapter(adapter);
        all();
    }

    /**
     * 显示弹出菜单
     */
    public void showPopmenu() {
        PopupMenu popup = new PopupMenu(this, moreButton);
        popup.getMenuInflater().inflate(R.menu.popmenu_nonview, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.top1:
                        all();
                        break;
                    case R.id.top2:
                        chooseDay();
                        break;
                    case R.id.top3:
                        //haveTimeWithMonday();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        popup.show();
    }

    public List<MySubject> getData() {
        List<MySubject> mySubjects;
        SharedPreferences sp=this.getSharedPreferences(LOCAL_COURSE,MODE_PRIVATE);
        String local_mySubjects=sp.getString("mySubjects"," ");
        Gson gson=new Gson();
        mySubjects=gson.fromJson(local_mySubjects,new TypeToken<List<MySubject>>(){}.getType());
        return mySubjects;
    }

    /**
     * 获取所有课程
     */
    protected void all() {
        schedules.clear();
        schedules.addAll(getData());
        adapter.notifyDataSetChanged();
    }
    private void chooseDay()
    {
        schedules.clear();
        schedules.addAll(getHaveSubjectsWithDay(getData(),7,2));
        adapter.notifyDataSetChanged();
    }
    public static List<MySubject> getHaveSubjectsWithDay(List<MySubject> scheduleList, int curWeek, int day) {
        List<MySubject> subjectBeanList = getAllSubjectsWithDay(scheduleList, day);
        List<MySubject> result = new ArrayList<>();
        for (MySubject bean : subjectBeanList) {
            if (isThisWeek(bean, curWeek)) {
                result.add(bean);
            }
        }
        return result;
    }
    public static boolean isThisWeek(MySubject subject, int cur_week) {
        List<Integer> weekList = subject.getWeekList();
        if (weekList.indexOf(cur_week) != -1) return true;
        return false;
    }
    public static List<MySubject> getAllSubjectsWithDay(List<MySubject> scheduleList, int day) {
        List<MySubject> subjectBeanList = splitSubjectWithDay(scheduleList)[day];
        return subjectBeanList;
    }
    public static List<MySubject>[] splitSubjectWithDay(List<MySubject> dataSource) {
        List<MySubject>[] data = new ArrayList[7];
        if (dataSource == null) return data;
        for (int i = 0; i < data.length; i++) {
            data[i] = new ArrayList<>();
        }
        for (int i = 0; i < dataSource.size(); i++) {
            MySubject bean = dataSource.get(i);
            if (bean.getDay() != -1)
                data[bean.getDay() - 1].add(bean);
        }
        sortList(data);
        return data;
    }
    public static void sortList(List<MySubject>[] data) {
        for (int i = 0; i < data.length; i++)
            sortList(data[i]);
    }
    public static void sortList(List<MySubject> data) {
        int min;
        MySubject tmp;
        for (int m = 0; m < data.size() - 1; m++) {
            min = m;
            for (int k = m + 1; k < data.size(); k++) {
                if (data.get(min).getStart() > data.get(k).getStart()) {
                    min = k;
                }
            }
            tmp = data.get(m);
            data.set(m, data.get(min));
            data.set(min, tmp);
        }
    }
}
