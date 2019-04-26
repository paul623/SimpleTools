package com.paul.simpletools.Tools;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paul.simpletools.Fragment.MainFragmentActivity;
import com.paul.simpletools.NonActivity;
import com.paul.simpletools.R;
import com.paul.simpletools.dataBase.MessageEvent;
import com.paul.simpletools.dataBase.MySubject;
import com.paul.simpletools.dataBase.MySupport;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    SuperTextView stv_1,stv_2,stv_3,stv_4,stv_5,stv_6,stv_7,stv_8;
    Boolean stv1,stv2,stv3,stv4,stv6;
    SharedPreferences sp;
    MessageEvent messageEvent;
    private int hour,minute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        messageEvent=new MessageEvent();
        InitEventBus();
        initSuperTextView();
    }
    @Override
    protected void onStop()
    {
        super.onStop();
        EventBus.getDefault().post(messageEvent);
    }
    private void initSuperTextView()
    {
        stv_1=findViewById(R.id.stv_1);//隐藏非本周课程
        stv_2=findViewById(R.id.stv_2);//隐藏周末
        stv_3=findViewById(R.id.stv_3);//最大节次
        stv_4=findViewById(R.id.stv_4);//显示节次时间
        stv_5=findViewById(R.id.stv_5);//背景设置
        stv_6=findViewById(R.id.stv_6);//推送设置
        stv_7=findViewById(R.id.stv_7);//推送时间设置
        stv_8=findViewById(R.id.stv_8);//推送设置
        sp=this.getSharedPreferences(MySupport.CONFIG_DATA,MODE_PRIVATE);
        hour=sp.getInt(MySupport.CONFIG_TUUISONG_HOUR,7);
        minute=sp.getInt(MySupport.CONFIG_TUUISONG_MINUTE,0);
        String out=INTime(hour,minute);
        stv_7.setRightBottomString(out);
        final SharedPreferences.Editor editor=sp.edit();
        stv_1.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                messageEvent.setShow_nweek_lesson(b);
                stv1=b;
                editor.putBoolean(MySupport.CONFIG_HIDELESOONS,stv1);
                editor.apply();
            }
        });
        stv_2.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                messageEvent.setShow_weekend(b);
                stv2=b;
                editor.putBoolean(MySupport.CONFIG_HIDEWEEKEND,stv2);
                editor.apply();
            }
        });
        stv_3.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                messageEvent.setMinnumber(b);
                stv3=b;
                editor.putBoolean(MySupport.CONFIG_MAXNUMBERS,stv3);
                editor.apply();
            }
        });
        stv_4.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                messageEvent.setShow_times(b);
                stv4=b;
                editor.putBoolean(MySupport.CONFIG_SHOWTIME,stv4);
                editor.apply();
            }
        });
        stv_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);

            }
        });
        stv_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar=Calendar.getInstance();
                hour = calendar.get(Calendar.HOUR);//获取小时
                minute = calendar.get(Calendar.MINUTE);//获取分钟
                new TimePickerDialog(SettingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    //实现监听方法
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour=i;
                        minute=i1;
                        Toast.makeText(SettingActivity.this,"推送时间已经设置为 "+hour+":"+minute,Toast.LENGTH_SHORT).show();
                        sp=SettingActivity.this.getSharedPreferences(MySupport.CONFIG_DATA,MODE_PRIVATE);
                        SharedPreferences.Editor editor1=sp.edit();
                        editor.putInt(MySupport.CONFIG_TUUISONG_HOUR,hour);
                        editor.putInt(MySupport.CONFIG_TUUISONG_MINUTE,minute);
                        editor.apply();
                        messageEvent.setHour(hour);
                        messageEvent.setMinute(minute);
                        String result=INTime(hour,minute);
                        stv_7.setRightBottomString(result);
                    }
                },hour,minute,true).show();//记得使用show才能显示！
            }
        });
        stv_6.setSwitchCheckedChangeListener(new SuperTextView.OnSwitchCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                messageEvent.setTuisong(b);
                stv6=b;
                editor.putBoolean(MySupport.CONFIG_TUISONG,stv6);
                editor.apply();
            }
        });
        stv_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        stv_1.setSwitchIsChecked(sp.getBoolean(MySupport.CONFIG_HIDELESOONS,false));
        stv_2.setSwitchIsChecked(sp.getBoolean(MySupport.CONFIG_HIDEWEEKEND,false));
        stv_3.setSwitchIsChecked(sp.getBoolean(MySupport.CONFIG_MAXNUMBERS,false));
        stv_4.setSwitchIsChecked(sp.getBoolean(MySupport.CONFIG_SHOWTIME,false));
        stv_6.setSwitchIsChecked(sp.getBoolean(MySupport.CONFIG_TUISONG,false));
    }
    void InitEventBus()
    {
        sp=this.getSharedPreferences(MySupport.CONFIG_DATA,MODE_PRIVATE);
        messageEvent.setHour(sp.getInt(MySupport.CONFIG_TUUISONG_HOUR,7));
        messageEvent.setMinute(sp.getInt(MySupport.CONFIG_TUUISONG_MINUTE,0));
        messageEvent.setTuisong(sp.getBoolean(MySupport.CONFIG_TUISONG,false));
        messageEvent.setBackground(sp.getString(MySupport.CONFIG_BG,""));
        messageEvent.setShow_times(sp.getBoolean(MySupport.CONFIG_SHOWTIME,false));
        messageEvent.setShow_weekend(sp.getBoolean(MySupport.CONFIG_HIDEWEEKEND,false));
        messageEvent.setShow_nweek_lesson(sp.getBoolean(MySupport.CONFIG_HIDELESOONS,false));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        ContentResolver resolver = getContentResolver();
        try {
                if(data!=null) {
                    Uri originalUri = data.getData(); // 获得图片的uri
                    MediaStore.Images.Media.getBitmap(resolver, originalUri);
                    String[] proj = {MediaStore.Images.Media.DATA};
                    @SuppressWarnings("deprecation")
                    Cursor cursor = managedQuery(originalUri, proj, null, null,
                            null);
                    int column_index = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String path = cursor.getString(column_index);
                    messageEvent.setBackground(path);
                    SharedPreferences sp = getSharedPreferences(MySupport.LOCAL_COURSE, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(MySupport.CONFIG_BG, path);
                    editor.apply();
                }
                else
                {
                    Toast.makeText(SettingActivity.this,"操作错误或已取消",Toast.LENGTH_SHORT).show();
                }

            } catch (IOException e) {
                Log.e("TAG-->Error", e.toString());
            }
        super.onActivityResult(requestCode, resultCode, data);
        }

    private String INTime(Integer hour,Integer minute)
    {
        String out="";
        if(hour<10)
        {
            out="0"+hour;
        }
        else
        {
            out=out+hour;
        }
        if(minute<10)
        {
            out=out+":"+"0"+minute;
        }
        else
        {
            out=out+":"+minute;
        }
        return out;
    }
    void sendMessage()
    {
        SharedPreferences sharedPreferences=getSharedPreferences(MySupport.LOCAL_COURSE,MODE_PRIVATE);
        int value_curWeek=sharedPreferences.getInt(MySupport.LOCAL_CURWEEK,1);
        int curday=toolsHelper.getTodayWeek();
        List<MySubject> subjects=new ArrayList<>();
        subjects.addAll(NonActivity.getHaveSubjectsWithDay(NonActivity.getData(SettingActivity.this),value_curWeek,curday));
        //Toast.makeText(context, "闹铃响了, 可以做点事情了~~", Toast.LENGTH_LONG).show();

        NotificationManager manager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        //例如这个id就是你传过来的
        String id="simpletools";
        //MainActivity是你点击通知时想要跳转的Activity
        Intent playIntent = new Intent(SettingActivity.this, MainFragmentActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(SettingActivity.this, 1, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String today_lessons="";
        for(MySubject mySubject:subjects)
        {
            today_lessons=today_lessons+mySubject.getName()+" 地点:"+mySubject.getRoom()+" 节数"+mySubject.getStart()+"~"+(mySubject.getStart()+mySubject.getStep()-1)+"\n";
        }
        if(today_lessons.equals(""))
        {
            today_lessons="太棒了！今天没有课~好好放松放松吧~";
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(SettingActivity.this,id);
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


