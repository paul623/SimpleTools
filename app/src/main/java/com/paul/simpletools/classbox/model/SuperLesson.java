package com.paul.simpletools.classbox.model;

import com.zhuangfei.timetable.model.ScheduleEnable;

import java.io.Serializable;

/**
 * Created by Liu ZhuangFei on 2018/8/10.
 */

public class SuperLesson implements Serializable{

    //周几上
    private int day;

    //地点
    private String locale;

    //课程名
    private String name;

    //上课周次，以空格分隔
    private String period;

    //结束节次
    private int sectionend;

    //开始节次
    private int sectionstart;

    //学期
    private String semester;

    //教师
    private String teacher;

    private String schoolName;

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getSectionend() {
        return sectionend;
    }

    public void setSectionend(int sectionend) {
        this.sectionend = sectionend;
    }

    public int getSectionstart() {
        return sectionstart;
    }

    public void setSectionstart(int sectionstart) {
        this.sectionstart = sectionstart;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}
