package com.paul.simpletools.Ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.allen.library.SuperTextView;

import java.util.Calendar;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/5/7 16：45
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class MySuperDialog {

    private String date="";
    private String time="";
    private String term="";
    public  String showDatePickerDialog(Context context)
    {

        Calendar calendar=Calendar.getInstance();
        new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    date=year+"."+month+"."+dayOfMonth;
                }
            },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
        return date;
    }
    public  String showTimePickerDialog(Context context)
    {
        Calendar c = Calendar.getInstance();
        // 创建一个TimePickerDialog实例，并把它显示出来
        new TimePickerDialog(context,
                // 绑定监听器
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view,
                                          int hourOfDay, int minute) {
                        time=hourOfDay+":"+minute;
                    }
                }
                // 设置初始时间
                , c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                // true表示采用24小时制
                true).show();
        return time;
    }

    public String showTermPickerDiaglog(Context context,double height,double width)
    {

        return term;
    }
}
