package com.paul.simpletools.dataBase;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/4/11 16：49
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class MySupport {
    public static String CONFIG_DATA="config_data";//本地存储，存储配置信息
    public static String CONFIG_HIDELESOONS="config_hide_nweek_lessons";
    public static String CONFIG_MAXNUMBERS="config_max_numbers";
    public static String CONFIG_SHOWTIME ="config_showtime";
    public static String CONFIG_HIDEWEEKEND="config_hide_weekend";
    public static String CONFIG_TUISONG="config_tuisong";
    public static String CONFIG_TUUISONG_HOUR="config_tuisong_hour";
    public static String CONFIG_TUUISONG_MINUTE="config_tuisong_minute";
    public static String LOCAL_COURSE="local_course";//本地存储，存储课程信息
    public static String LOCAL_COURSE_DATABASE="local_course_data_base";
    public static String LOCAL_FRAGMENT2="local_fragment2";//本地存储，负责记录头像、每日一句等信息
    public static String appkey="5c2fcdf15f7879bd65b8d9a9e4d26c89";
    public static String conact_qq="mqqwpa://im/chat?chat_type=wpa&uin=" + "2499761614";//跳转QQ
    public static String CONFIG_BG="config_background";
    public static String CONFIG_HEAD="config_head";
    public static int  REQUEST_IMAGE_CAPTURE = 1;
    public static int  REQUEST_TAKE_PHOTO = 1;
    public static final String UpdateURL="https://www.coolapk.com/apk/229807";
    public static final String DATE_LOCALDATE="date_localdate";
    public static final String REQUEST_WORDS="http://open.iciba.com/dsapi/";
    public static final String REQUEST_WORDS2="https://v1.hitokoto.cn/";
    public static final String REQUEST_STATUS="request_status";
    public static final String LOCAL_TOMATO="local_tamato";
    public static final String LOCAL_WORDS="local_words";
    public static final String LOCAL_WORDS_DATE="lcoal_words_date";
    public static final String LOCAL_CURWEEK="local_cur_week";
    private static final String TAG = "PhotoTestActivity";

    public static final String SD_APP_DIR_NAME = "SimpleTools"; //存储程序在外部SD卡上的根目录的名字
    public static final String PHOTO_DIR_NAME = "photo";    //存储照片在根目录下的文件夹名字
    public static final String PHOTO_CORP_DIR_NAME="Background";
    public static final int PHOTO_RESULT_CODE = 100;        //标志符，图片的结果码，判断是哪一个Intent
    public static final int VOICE_RESULT_CODE = 101;        //标志符，音频的结果码，判断是哪一个Intent
    public static final int VIDEO_RESULT_CODE = 102;        //标志符，视频的结果码，判断是哪一个Intent
    public static final String[] REQUEST_STATUS_SELECTORS = new String[]{"双语","随机","动漫","漫画","游戏","小说","原创","网络","其他"};
    public static final String CHOOSE_TERM_STATUS="choose_term_status";
    public static final String[] DEFAULT_COURSE_STARTTIME = new String[]{"8:00", "8:55", "10:00", "10:55","14:00", "14:55", "16:00", "16:55","17:50", "19:00", "19:55","20:50"};
    public static final Integer REQUEST_ALUBUM_VIEWPHOTO=108;
    public static final Integer REQUEST_ALUBUM_SELECTPHOTO=109;
    public static final String[] RRuleConstantByWeek={"FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=MO;","FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=TU;",
            "FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=WE;", "FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=TH;","FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=FR;",
            "FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=SA;","FREQ=WEEKLY;INTERVAL=1;WKST=MO;BYDAY=SU;"};
    public static final String[] RRuleCOnstantByDay={"FREQ=WEEKLY;INTERVAL=2;WKST=MO;BYDAY=MO;","FREQ=WEEKLY;INTERVAL=2;WKST=MO;BYDAY=TU;",
            "FREQ=WEEKLY;INTERVAL=2;WKST=MO;BYDAY=WE;", "FREQ=WEEKLY;INTERVAL=2;WKST=MO;BYDAY=TH;","FREQ=WEEKLY;INTERVAL=2;WKST=MO;BYDAY=FR;",
            "FREQ=WEEKLY;INTERVAL=2;WKST=MO;BYDAY=SA;","FREQ=WEEKLY;INTERVAL=2;WKST=MO;BYDAY=SU;"};
    public static final String CALENDAR_STATUS="laboratory_calendar_status";
    public static final String DATA_COURSE_STARTTIME="data_course_starttime";//时间表存储名称
    public static final String COURSE_STARTTIME="course_starttime";//用户自行设置存储课程开始时间
}
