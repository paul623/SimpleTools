package com.paul.simpletools.Tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/4/20 18：04
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class AlertService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    public static final int NOTIFICATION_ID=1;

    @Override
    public void onCreate() {


        // 创建通知管理器的实例
        final NotificationManager nm=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification n=new Notification(); // 创建通知的实例

        //两种设置声音的方法
        n.sound= Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "20");
        // n.sound=Uri.parse("file:///sdcard/alarm.mp3");
        nm.notify(NOTIFICATION_ID, n);  // 发布通知以显示在状态栏中


    }

    public PendingIntent getDefalutIntent(int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }

}
