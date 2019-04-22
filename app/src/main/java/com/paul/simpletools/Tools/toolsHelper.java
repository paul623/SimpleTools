package com.paul.simpletools.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/4/17 13：32
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class toolsHelper {

    public static int getWeekNumber(String local_day) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(local_day);
        Date date2 = new Date();
        return differentDays(date,date2);
    }
    public static int getWeekNumber(String local_date,int curWeek)
    {

        int weeknumbers=-1;
        int local_week,today_week;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(local_date);
            Date date2 = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            local_week = calendar.get(Calendar.DAY_OF_WEEK);
            if (local_week == 1)
            {
                local_week=7;
            }
            else
            {
                local_week=local_week-1;
            }
            calendar.setTime(date2);
            today_week=calendar.get(Calendar.DAY_OF_WEEK);
            if (today_week == 1)
            {
                today_week=7;
            }
            else
            {
                today_week=today_week-1;
            }
            weeknumbers=differentDays(date,date2);
            if(curWeek!=0)
            {
                weeknumbers=(weeknumbers+local_week-today_week)/7+curWeek;
            }
            else {
                weeknumbers=(weeknumbers+local_week-today_week)/7+1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return weeknumbers;
    }
    public  static int differentDays(Date date1, Date date2)
    {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1= cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if(year1 != year2)   //不同年
        {
            int timeDistance = 0 ;
            for(int i = year1 ; i < year2 ; i ++)
            {
                if(i%4==0 && i%100!=0 || i%400==0)    //闰年
                {
                    timeDistance += 366;
                }
                else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2-day1) ;
        }
        else    //相同年
        {
            //System.out.println("判断day2 - day1 : " + (day2-day1));
            return day2-day1;
        }
    }
    public  static int getTodayWeek()
    {
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
        return curday;
    }

}
