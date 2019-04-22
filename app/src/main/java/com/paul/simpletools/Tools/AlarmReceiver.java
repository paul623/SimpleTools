package com.paul.simpletools.Tools;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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

    //当BroadcastReceiver接收到Intent广播时调用。
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences=context.getSharedPreferences(MySupport.LOCAL_COURSE,Context.MODE_PRIVATE);
        int value_curWeek=sharedPreferences.getInt(MySupport.LOCAL_CURWEEK,1);
        int curday=toolsHelper.getTodayWeek();
        List<MySubject> subjects=new ArrayList<>();
        subjects.addAll(NonActivity.getHaveSubjectsWithDay(NonActivity.getData(context),value_curWeek,curday));
        manager = (NotificationManager)context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        //例如这个id就是你传过来的
        String id="simpletools";
        //MainActivity是你点击通知时想要跳转的Activity
        Intent playIntent = new Intent(context, MainFragmentActivity.class);
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
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,id);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            NotificationChannel channel1 = new NotificationChannel(id,"课表拍拍专用", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("用来通知课程等等");
            ///配置通知出现时的闪灯（如果 android 设备支持的话）
            channel1.enableLights(true);
            channel1.setLightColor(Color.WHITE);
            //配置通知出现时的震动（如果 android 设备支持的话）
            channel1.enableVibration(true);
            channel1.enableLights(true); //是否在桌面icon右上角展示小红点
            channel1.setLightColor(Color.RED); //小红点颜色
            channel1.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            channel1.setLockscreenVisibility(Notification.VISIBILITY_SECRET);//设置在锁屏界面上显示这条通知
            manager.createNotificationChannel(channel1);
        }
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
