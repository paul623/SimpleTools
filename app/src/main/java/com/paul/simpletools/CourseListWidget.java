package com.paul.simpletools;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RemoteViews;

import com.paul.simpletools.Fragment.CourseActivity;
import com.paul.simpletools.Ui.WidgetService;
import com.paul.simpletools.dataBase.MySubject;
import com.paul.simpletools.dataBase.MySupport;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

/**
 * Implementation of App Widget functionality.
 */
public class CourseListWidget extends AppWidgetProvider {

    private static final String UPDATE_ACTION = "WIDGET_UPDATE";
    private static final String UPDATE_ACTION_NO_COURSES = "WIDGET_UPDATE_NO_COURSES";
    private boolean noCourse = false;
    private List<MySubject> todaylessons;
    private static final String TAG = "Widget";
    private String choose_term;
    private void updateAppWidget(@NonNull Context context, @NonNull AppWidgetManager appWidgetManager, int appWidgetId) {

        LitePal.initialize(context);
        SharedPreferences sp=context.getSharedPreferences(MySupport.LOCAL_COURSE,Context.MODE_PRIVATE);
        choose_term=sp.getString(MySupport.CHOOSE_TERM_STATUS,"");
        if(choose_term.equals(""))
        {
            todaylessons=LitePal.findAll(MySubject.class);//这种情况基本不存在吧（确信）
        }
        else {
            todaylessons=LitePal.where("term=?",choose_term).find(MySubject.class);
        }
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_course_list);
        SimpleDateFormat weekFormat = new SimpleDateFormat("E", Locale.CHINA);
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.course_list);
        views.setTextViewText(R.id.week_day, "星期" + weekFormat.format(new Date()).replace("周", ""));
        views.setTextViewText(R.id.week, "第" + sp.getInt(MySupport.LOCAL_CURWEEK,1) + "周");
        views.setRemoteAdapter(R.id.course_list, new Intent(context, WidgetService.class));
        Log.d("CourseListWidget嘿嘿",sp.getInt(MySupport.LOCAL_CURWEEK,1)+"");
        //判断是否开启点击跳转主界面功能
        if (true) {
            Intent intent = new Intent(context, CourseActivity.class);
            Log.d("CourseListWidget嘿嘿","应该执行了跳转才对");
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.header, pendingIntent);
            views.setPendingIntentTemplate(R.id.course_list, pendingIntent);
            views.setPendingIntentTemplate(R.id.status, pendingIntent);
        }

        //判断是否隐藏日期和星期
        /*if (mRepository.getSwitchOption("widget_date")) {
            views.setViewVisibility(R.id.header, View.GONE);
        } else {*/
            views.setViewVisibility(R.id.header, View.VISIBLE);
       // }

        //判断今天有没有课
        if (noCourse) {
            views.setViewVisibility(R.id.course_list, View.INVISIBLE);
        } else {
            views.setViewVisibility(R.id.course_list, View.VISIBLE);
        }
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        Log.d(TAG, "onUpdate : 小部件已更新 ");
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive intent: " + intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context.getPackageName(), getClass().getName());
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case UPDATE_ACTION:
                    noCourse = false;
                    onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(thisWidget));
                    break;
                case UPDATE_ACTION_NO_COURSES:
                    noCourse = true;
                    onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(thisWidget));

            }
        }
        super.onReceive(context, intent);
    }


}

