package com.paul.simpletools.Tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paul.simpletools.Fragment.MainFragmentActivity;
import com.paul.simpletools.NonActivity;
import com.paul.simpletools.R;
import com.paul.simpletools.dataBase.MySubject;
import com.paul.simpletools.dataBase.MySupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/4/20 18：06
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class AlarmReceiver extends BroadcastReceiver {
    private NotificationManager manager;
    private SharedPreferences sp;

    //当BroadcastReceiver接收到Intent广播时调用。
    @Override
    public void onReceive(Context context, Intent intent) {

        sp=context.getSharedPreferences(MySupport.LOCAL_COURSE,Context.MODE_PRIVATE);
        String local_mySubjects=sp.getString("mySubjects","");
        Gson gson=new Gson();
        List<MySubject> mySubjects=gson.fromJson(local_mySubjects,new TypeToken<List<MySubject>>(){}.getType());
        int value_curWeek=sp.getInt("curweek",1);
        Date today = new Date();
        Calendar c=Calendar.getInstance();
        c.setTime(today);
        int weekday=c.get(Calendar.DAY_OF_WEEK);
        int curday;
        if(weekday==1)//1代表星期日
        {
            curday=7;
        }
        else
        {
            curday=weekday-1;
        }
        List<MySubject> subjects=new ArrayList<>();
        subjects.addAll(NonActivity.getHaveSubjectsWithDay(mySubjects,value_curWeek,curday));
        //Toast.makeText(context, "闹铃响了, 可以做点事情了~~", Toast.LENGTH_LONG).show();

        manager = (NotificationManager)context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        //例如这个id就是你传过来的
        String id = intent.getStringExtra("id");
        id= "0";
        //MainActivity是你点击通知时想要跳转的Activity
        Intent playIntent = new Intent(context, MainFragmentActivity.class);
        playIntent.putExtra("id", id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String today_lessons="";
        for(MySubject mySubject:subjects)
        {
            today_lessons=today_lessons+mySubject.getName()+" 地点:"+mySubject.getRoom()+" 节数"+mySubject.getStart()+"~"+(mySubject.getStart()+mySubject.getStep()-1)+"\n";
        }
        if(today_lessons.equals(""))
        {
            today_lessons="太棒了！今天没有课~好好放松放松吧~";
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"default");
        builder .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationCompat.BigTextStyle bigTextStyle=new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("叮~每日提醒来啦")
                .setSummaryText("今天您有"+subjects.size()+"节课")
                .bigText(today_lessons);
        builder.setStyle(bigTextStyle);
        manager.notify(value_curWeek, builder.build());
    }

}
