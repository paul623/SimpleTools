package com.paul.simpletools.Laboratory;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.kyle.calendarprovider.calendar.CalendarEvent;
import com.kyle.calendarprovider.calendar.CalendarProviderManager;
import com.paul.simpletools.LaunchActivity;
import com.paul.simpletools.R;
import com.paul.simpletools.classbox.model.BoxLoginParams;
import com.paul.simpletools.dataBase.EveryDayBean;
import com.paul.simpletools.dataBase.MySubject;
import com.paul.simpletools.dataBase.MySupport;
import com.paul.simpletools.dataBase.TermData;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class OutputToCalendar extends AppCompatActivity {
    private List<MySubject> mySubjects;
    private SuperTextView superTextView,stv_input,stv_delete;
    private String choose_term;
    private Integer currentWeekDays;
    private Boolean flag;
    private SharedPreferences sp;
    private Integer iCount = 0;
    private ProgressDialog pDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_output_to_calendar);
        LitePal.initialize(OutputToCalendar.this);
        init();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR,
                            Manifest.permission.READ_CALENDAR}, 1);
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)//透明状态栏
        {
            Window window = getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(Color.parseColor("#FFFFFF"));
        }
    }
    void init()
    {
        sp=getSharedPreferences(MySupport.LOCAL_COURSE, Context.MODE_PRIVATE);
        choose_term=sp.getString(MySupport.CHOOSE_TERM_STATUS,"");
        currentWeekDays=sp.getInt(MySupport.LOCAL_CURWEEK,0);
        flag=sp.getBoolean(MySupport.CALENDAR_STATUS,false);
        if(choose_term.equals(""))
        {
            mySubjects=LitePal.findAll(MySubject.class);//这种情况基本不存在吧（确信）
        }
        else {
            mySubjects=LitePal.where("term=?",choose_term).find(MySubject.class);
        }
        Log.d("导出到日历","导入项目数量:"+mySubjects.size());
        superTextView=findViewById(R.id.stv_outputtocalendar_title);
        stv_input=findViewById(R.id.stv_output);
        stv_delete=findViewById(R.id.stv_delete);
        superTextView.setLeftImageViewClickListener(new SuperTextView.OnLeftImageViewClickListener() {
            @Override
            public void onClickListener(ImageView imageView) {
                finish();
            }
        });
        stv_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });
        stv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag)
                showprocess();
                else
                {
                    Toasty.info(OutputToCalendar.this,"请先添加！",Toasty.LENGTH_SHORT).show();
                }
            }
        });

    }
    CalendarEvent setCalendarEvent(MySubject mySubject) {

        Calendar calendar = Calendar.getInstance();
        String name = mySubject.getName();
        String description = "第" + mySubject.getStart() + "~" + (mySubject.getStart() + mySubject.getStep() - 1) + "节" + " 老师:" + mySubject.getTeacher();
        String eventLocation = mySubject.getRoom();
        /*long start
        long end
        int advanceTime
        String rRule*/
        int goalWeek = mySubject.getDay();
        int curWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (curWeek == 1) {
            curWeek = 7;
        } else {
            curWeek = curWeek - 1;
        }
        int moveValue = goalWeek - curWeek;
        /*if (moveValue > 0) {
            moveValue--;

        } else {
            moveValue++;
        }*/
        String starttime = MySupport.DEFAULT_COURSE_STARTTIME[mySubject.getStart()-1];
        String result[] = starttime.split(":");
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(result[0]));
        calendar.set(Calendar.MINUTE, Integer.valueOf(result[1]));
        if (isDoubleWeekList(mySubject.getWeekList()))//偶数周
        {
            if (currentWeekDays % 2 == 0)//此时也是偶数周
            {
                long start = calendar.getTimeInMillis() + moveValue * 24 * 60 * 60 * 1000;
                long end = start + mySubject.getStep() * 45 * 60 * 1000 + (mySubject.getStep() - 1) * 10 * 60 * 1000;
                String rRule = MySupport.RRuleCOnstantByDay[goalWeek-1] + "COUNT=" + (mySubject.getWeekList().size() - 1 - getLocOfList(mySubject.getWeekList()));
                CalendarEvent calendarEvent = new CalendarEvent(name, description, eventLocation, start, end, 0, rRule);
                Log.d("偶数周","偶数:"+(mySubject.getWeekList().size() - 1 - getLocOfList(mySubject.getWeekList())));
                return calendarEvent;
            } else {
                long start = calendar.getTimeInMillis() + moveValue * 24 * 60 * 60 * 1000 + 7 * 24 * 60 * 60 * 1000;//下一周
                long end = start + mySubject.getStep() * 45 * 60 * 1000 + (mySubject.getStep() - 1) * 10 * 60 * 1000;
                String rRule = MySupport.RRuleCOnstantByDay[goalWeek-1] + "COUNT=" + (mySubject.getWeekList().size() - 1 - getMinLocOfList(mySubject.getWeekList()));
                CalendarEvent calendarEvent = new CalendarEvent(name, description, eventLocation, start, end, 0, rRule);
                Log.d("奇数周","偶数:"+(mySubject.getWeekList().size() - 1 - getMinLocOfList(mySubject.getWeekList())));
                return calendarEvent;
            }
        } else if (isSignalWeekList(mySubject.getWeekList())) {
            if (currentWeekDays % 2 != 0)//此时是奇数周
            {
                long start = calendar.getTimeInMillis() + moveValue * 24 * 60 * 60 * 1000;
                long end = start + mySubject.getStep() * 45 * 60 * 1000 + (mySubject.getStep() - 1) * 10 * 60 * 1000;
                String rRule = MySupport.RRuleCOnstantByDay[goalWeek-1] + "COUNT=" + (mySubject.getWeekList().size() - 1 - getLocOfList(mySubject.getWeekList()));
                CalendarEvent calendarEvent = new CalendarEvent(name, description, eventLocation, start, end, 0, rRule);
                Log.d("奇数周","奇数:"+(mySubject.getWeekList().size() - 1 - getLocOfList(mySubject.getWeekList())));
                return calendarEvent;
            } else {
                long start = calendar.getTimeInMillis() + moveValue * 24 * 60 * 60 * 1000 + 7 * 24 * 60 * 60 * 1000;//下一周
                long end = start + mySubject.getStep() * 45 * 60 * 1000 + (mySubject.getStep() - 1) * 10 * 60 * 1000;
                String rRule = MySupport.RRuleCOnstantByDay[goalWeek-1] + "COUNT=" + (mySubject.getWeekList().size() - 1 - getMinLocOfList(mySubject.getWeekList()));
                CalendarEvent calendarEvent = new CalendarEvent(name, description, eventLocation, start, end, 0, rRule);
                Log.d("偶数周","奇数:"+(mySubject.getWeekList().size() - 1 - getMinLocOfList(mySubject.getWeekList())));
                return calendarEvent;
            }
        } else {
            long start = calendar.getTimeInMillis() + moveValue * 24 * 60 * 60 * 1000;
            long end = start + mySubject.getStep() * 45 * 60 * 1000 + (mySubject.getStep() - 1) * 10 * 60 * 1000;
            String rRule = MySupport.RRuleConstantByWeek[goalWeek-1] + "COUNT=" + (mySubject.getWeekList().size() - 1 - getLocOfList(mySubject.getWeekList()));
            CalendarEvent calendarEvent = new CalendarEvent(name, description, eventLocation, start, end, 0, rRule);
            return calendarEvent;
        }
    }

    int getLocOfList(List<Integer> list)
    {
        int count=0;
        for(Integer integer:list)
        {
            if(integer==currentWeekDays)
            {
                return count;
            }
            count++;
        }
        return -1;
    }
    int getMinLocOfList(List<Integer> list)
    {
        for(int i=0;i<list.size();i++)
        {
            if(list.get(i)>currentWeekDays)
            {
                return i;
            }
        }
        return -1;
    }
    boolean isDoubleWeekList(List<Integer> list)
    {
        boolean flag=true;
        for(Integer integer:list)
        {
            if(integer%2!=0)
            {
                flag=false;
                break;
            }
        }
        return flag;
    }
    boolean isSignalWeekList(List<Integer> list)
    {
        boolean flag=true;
        for(Integer integer:list)
        {
            if(integer%2==0)
            {
                flag=false;
                break;
            }
        }
        return flag;
    }
    void showConfirmDialog()
    {
        final android.app.AlertDialog.Builder normalDialog =
                new android.app.AlertDialog.Builder(OutputToCalendar.this);
        normalDialog.setIcon(R.mipmap.ic_launcher);
        normalDialog.setTitle("说明");
        normalDialog.setMessage("鉴于作者水平有限，该功能不是太稳定，请遇到问题及时反馈！"+"\n"+"课程时间可能不太准确，请等待下一个版本更新设置~");
        normalDialog.setPositiveButton("确认导入",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!flag) {
                            int result = -1;
                            for (MySubject item : mySubjects) {
                                if (item.getWeekList().get(item.getWeekList().size() - 1) >= currentWeekDays) {
                                    result = CalendarProviderManager.addCalendarEvent(OutputToCalendar.this, setCalendarEvent(item));
                                    Log.d("导出到日历", "result" + result);

                                }
                            }
                            if (result == 0) {
                                Toasty.success(OutputToCalendar.this, "导出成功！", Toast.LENGTH_SHORT).show();
                                flag=true;
                                SharedPreferences.Editor editor=sp.edit();
                                editor.putBoolean(MySupport.CALENDAR_STATUS,flag);
                                editor.apply();
                            } else if (result == -1) {
                                Toasty.error(OutputToCalendar.this, "导出失败", Toast.LENGTH_SHORT).show();
                            } else if (result == -2) {
                                Toasty.error(OutputToCalendar.this, "没有权限", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toasty.info(OutputToCalendar.this,"请勿重复添加！",Toasty.LENGTH_SHORT).show();
                        }
                    }


                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Toast.makeText(getContext(),"吓死我了···",Toast.LENGTH_SHORT).show();
                        //Toasty.error(OutputToCalendar.this, "吓死我了···", Toast.LENGTH_SHORT, true).show();
                    }
                });
        normalDialog.show();
    }
    void showprocess() {

        pDialog = new ProgressDialog(OutputToCalendar.this);
        pDialog.setTitle("删除中");
        pDialog.setIcon(R.mipmap.ic_launcher);
        pDialog.setMessage("正在清理日历中已添加的课程···");
        pDialog.setCancelable(false);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.show();

        // 创建线程实例
        new Thread() {
            public void run() {

                long calID2 = CalendarProviderManager.obtainCalendarAccountID(OutputToCalendar.this);
                List<CalendarEvent> events2 = CalendarProviderManager.queryAccountEvent(OutputToCalendar.this, calID2);
                if (null != events2) {
                    if (events2.size() == 0) {
                        Toasty.info(OutputToCalendar.this, "没有事件可以删除", Toast.LENGTH_SHORT).show();
                    } else {
                        for(CalendarEvent item:events2) {
                            for(MySubject mySubject:mySubjects)
                            {
                                if(mySubject.getName().equals(item.getTitle()))
                                {
                                    CalendarProviderManager.deleteCalendarEvent(OutputToCalendar.this, item.getId());
                                    break;
                                }
                            }

                        }
                        flag=false;
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putBoolean(MySupport.CALENDAR_STATUS,flag);
                        editor.apply();
                    }
                } else {
                   // Toasty.error(OutputToCalendar.this, "查询失败", Toast.LENGTH_SHORT).show();
                }
                try {
                    while (iCount <= 50) {
                        // 由线程来控制进度。
                        pDialog.setProgress(iCount++);
                        Thread.sleep(50);
                    }
                    //Toast.makeText(getActivity(),"数据清理完毕！",Toast.LENGTH_SHORT).show();
                    pDialog.cancel();
                   // Toasty.success(OutputToCalendar.this,"删除成功！",Toasty.LENGTH_SHORT).show();

                } catch (InterruptedException e) {
                    pDialog.cancel();
                }
            }

        }.start();


    }
}
