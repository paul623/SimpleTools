package com.paul.simpletools.Ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import com.paul.simpletools.Fragment.CourseActivity;
import com.paul.simpletools.R;
import com.paul.simpletools.Tools.LessonsHelper;
import com.paul.simpletools.Tools.toolsHelper;
import com.paul.simpletools.dataBase.MySubject;
import com.paul.simpletools.dataBase.MySupport;

import static android.content.ContentValues.TAG;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/5/7 23：56
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ViewRemoteService(this, intent);
    }

    //实现一个ViewRemoteService再其中进行adapter的一些操作
    private class ViewRemoteService implements RemoteViewsService.RemoteViewsFactory {

        private Context context;
        private int currentWeek;
        private static final int OVER = 0;
        private static final int PROCESSING = 1;
        private static final int NOTSTART = 2;
        private static final int READY = 3;
        private List<MySubject> courses = new ArrayList<>();

        ViewRemoteService(Context context, Intent intent) {
            this.context = context;
        }

        @Override
        public void onCreate() {
            updateList();
        }

        @Override
        public void onDataSetChanged() {
            updateList();
        }

        @Override
        public void onDestroy() {
            courses.clear();
            Log.d(TAG, "onDestroy");
        }

        @Override
        public int getCount() {
            return courses.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.course_list_widget);
            Intent intent = new Intent(context, CourseActivity.class);
            views.setOnClickFillInIntent(R.id.widget_list_item, intent);
            if(courses.size()==0&&position==0)
            {
                //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.course_list_widget);
                views.setTextViewText(R.id.name, "今天没有课呀~");
                return views;
            }
            else {
                MySubject curpostion = courses.get(position);
                String jc = curpostion.getStart() + "~" + (curpostion.getStart() + curpostion.getStep() - 1);
                //RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.course_list_widget);
                views.setTextViewText(R.id.name, curpostion.getName());
                views.setTextViewText(R.id.info, curpostion.getTeacher() + " " + curpostion.getRoom());
                views.setTextViewText(R.id.time, jc);
                //是否开启点击小部件跳转到主界面

                //显示是否显示上课状态
                if (true) {
                    views.setViewVisibility(R.id.status, View.GONE);
                } else {
                    views.setViewVisibility(R.id.status, View.VISIBLE);
                    switch (PROCESSING) {
                        case OVER:
                            views.setTextViewText(R.id.status, "已结束");
                            views.setTextColor(R.id.status, Color.parseColor("#666666"));
                            break;
                        case PROCESSING:
                            views.setTextViewText(R.id.status, "正在上课");
                            views.setTextColor(R.id.status, Color.parseColor("#3de1ad"));
                            break;
                        case NOTSTART:
                            views.setTextViewText(R.id.status, "未开始");
                            views.setTextColor(R.id.status, Color.parseColor("#e9e7ef"));
                            break;
                        case READY:
                            views.setTextViewText(R.id.status, "即将开始");
                            views.setTextColor(R.id.status, Color.parseColor("#ffa400"));
                            break;
                    }
                }
                return views;
            }
            //Log.d(TAG, "status: " + getStatus(jc));

        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


        private void updateList() {
            courses.clear();
            LitePal.initialize(context);
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

            currentWeek = sp.getInt(MySupport.LOCAL_CURWEEK,1);
            if (!all.isEmpty()) {
                List<MySubject> lala=LessonsHelper.getHaveSubjectsWithDay(all,currentWeek, toolsHelper.getTodayWeek());
                //if(lala.size()>2)
                    //lala=LessonsHelper.selectSubjectToShow(lala);
               courses.addAll(lala);
            }
        }

    }

}
