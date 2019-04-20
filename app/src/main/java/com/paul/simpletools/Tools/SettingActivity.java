package com.paul.simpletools.Tools;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.paul.simpletools.R;
import com.paul.simpletools.dataBase.MessageEvent;
import com.paul.simpletools.dataBase.MySubject;
import com.paul.simpletools.dataBase.MySupport;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Calendar;

public class SettingActivity extends AppCompatActivity {

    SuperTextView stv_1,stv_2,stv_3,stv_4,stv_5,stv_6,stv_7;
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
    /*private class mySwitchListener implements SuperTextView.OnSwitchCheckedChangeListener
    {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                switch (compoundButton.getId()){
                    case R.id.stv_1:
                        stv1=b;
                        Toast.makeText(SettingActivity.this,"选中1",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.stv_2:
                        stv2=b;
                        Toast.makeText(SettingActivity.this,"选中2",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.stv_3:
                        stv3=b;
                        Toast.makeText(SettingActivity.this,"选中3",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.stv_4:
                        stv4=b;
                        Toast.makeText(SettingActivity.this,"选中4",Toast.LENGTH_SHORT).show();
                        break;
                }
                saveSettings();
        }
    }*/
    /*private void saveSettings()
    {
        sp=this.getSharedPreferences(MySupport.CONFIG_DATA,MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putBoolean(MySupport.CONFIG_HIDELESOONS,stv1);
        editor.putBoolean(MySupport.CONFIG_HIDEWEEKEND,stv2);
        editor.putBoolean(MySupport.CONFIG_MAXNUMBERS,stv3);
        editor.putBoolean(MySupport.CONFIG_SHOWTIME,stv4);
        editor.commit();
    }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        /*super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1&& resultCode == Activity.RESULT_OK
                && data != null)
        {
            Uri selectedImage = data.getData();//返回的是uri


            String [] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(columnIndex);
            SharedPreferences sp=getSharedPreferences(MySupport.CONFIG_DATA,MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString(MySupport.CONFIG_BG,path);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            LinearLayout layout1=findViewById(R.id.ly_classtable);

            Drawable drawable =new BitmapDrawable(getResources(),bitmap);
            layout1.setBackground(drawable);
        }*/
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
}


