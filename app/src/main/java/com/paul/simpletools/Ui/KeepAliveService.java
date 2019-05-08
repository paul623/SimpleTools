package com.paul.simpletools.Ui;

import android.annotation.SuppressLint;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.paul.simpletools.Tools.LessonsHelper;
import com.paul.simpletools.Tools.toolsHelper;
import com.paul.simpletools.dataBase.MySubject;
import com.paul.simpletools.dataBase.MySupport;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/5/8 17：40
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class KeepAliveService extends JobService {
    private static final String TAG = "KeepAliveService";
    private List<MySubject> courses = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: 服务已启动");
        return Service.START_STICKY;
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "onStartJob: 已执行");
        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.schedule(getJobInfo());
        updateWidget();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "onStopJob: 已停止");
        return false;
    }

    private JobInfo getJobInfo() {
        JobInfo.Builder builder = new JobInfo.Builder(1, new ComponentName(this, KeepAliveService.class));
        builder.setMinimumLatency(60 * 1000);
        builder.setOverrideDeadline(240 * 1000);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        return builder.build();
    }

    @SuppressLint("WrongConstant")
    private void updateWidget() {
        courses.clear();
        LitePal.initialize(this);
        List<MySubject> all;
        SharedPreferences sp=getSharedPreferences(MySupport.LOCAL_COURSE,MODE_PRIVATE);
        String term=sp.getString(MySupport.CHOOSE_TERM_STATUS,"");
        if(term.equals(""))
        {
            all = LitePal.findAll(MySubject.class);
        }
        else
        {
            all=LitePal.where("term=?",term).find(MySubject.class);
        }
        int currentWeek = sp.getInt(MySupport.LOCAL_CURWEEK,1);
        if (!all.isEmpty()) {
            courses.addAll(LessonsHelper.getHaveSubjectsWithDay(all,currentWeek, toolsHelper.getTodayWeek()));
        }

        Intent intent;
        if (this.courses.isEmpty()) {
            intent = new Intent("WIDGET_UPDATE_NO_COURSES");
        } else {
            intent = new Intent("WIDGET_UPDATE");
        }
        intent.addFlags(0x01000000);
        sendBroadcast(intent);
        Log.d(TAG, "updateWidget: 小部件已更新");
    }

}
