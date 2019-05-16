package com.paul.simpletools.Tools;

import android.util.Log;

import com.paul.simpletools.dataBase.MySubject;
import com.zhuangfei.timetable.model.Schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/4/9 16：17
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class LessonsHelper {
    public static List<MySubject> selectSubjectToShow(List<MySubject> mySubjects)
    {
        List<MySubject> goals=new ArrayList<>();
        SimpleDateFormat formatters = new SimpleDateFormat("HH:mm");
        Date curDates=new Date(System.currentTimeMillis());
        String strs=formatters.format(curDates);
        String []dds=new String[]{};
        dds = strs.split(":");
        int dhs = Integer.parseInt(dds[0]);
        Log.d("进行筛选程序","执行");
        Log.d("进行筛选程序","当前时间:"+dhs);
        for(MySubject item:mySubjects)
        {
            if(item.getStart()<=4&&dhs<=12)
            {
                goals.add(item);
                Log.d("进行筛选程序","添加上午课程");
            }
            else if(item.getStart()>4&&dhs>12)
            {
                goals.add(item);
                Log.d("进行筛选程序","添加下午课程");
            }
        }
        Log.d("进行筛选程序","课程筛选后大小"+goals.size());
        return goals;

    }
    public static List<Schedule> changeToShedule(List<MySubject> mySubjects)
    {
        List<Schedule> schedules=new ArrayList<>();
        for(MySubject item:mySubjects)
        {
            Schedule schedule=new Schedule();
            schedule.setName(item.getName());
            schedule.setDay(item.getDay());
            schedule.setRoom(item.getRoom());
            schedule.setStart(item.getStart());
            schedule.setStep(item.getStep());
            schedule.setTeacher(item.getTeacher());
            schedule.setWeekList(item.getWeekList());
            schedules.addAll(schedules);
        }
        return schedules;
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
        List<MySubject> subjectBeanList = splitSubjectWithDay(scheduleList)[day-1];
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


