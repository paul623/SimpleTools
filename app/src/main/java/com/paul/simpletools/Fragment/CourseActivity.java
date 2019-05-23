package com.paul.simpletools.Fragment;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.paul.simpletools.CourseListWidget;
import com.paul.simpletools.LessonAlbum.AlbumLessonSelectActivity;
import com.paul.simpletools.PhotoTestActivity;
import com.paul.simpletools.R;
import com.paul.simpletools.Tools.AlarmReceiver;
import com.paul.simpletools.Tools.EditCourseActivity;
import com.paul.simpletools.Tools.LessonsHelper;
import com.paul.simpletools.Tools.ViewCourseActivity;
import com.paul.simpletools.Tools.toolsHelper;
import com.paul.simpletools.classbox.activity.AuthActivity;
import com.paul.simpletools.classbox.model.SuperLesson;
import com.paul.simpletools.classbox.model.SuperResult;
import com.paul.simpletools.classbox.utils.SuperUtils;
import com.paul.simpletools.dataBase.MessageEvent;
import com.paul.simpletools.dataBase.MySubject;
import com.paul.simpletools.dataBase.MySupport;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.view.WeekView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class CourseActivity extends Fragment implements View.OnClickListener {
    public static final int REQUEST_IMPORT=1;
    private static final String LOCAL_COURSE="local_course";
    private static final String UpdateURL="https://www.yuque.com/docs/share/3d3b7318-c4d4-4bf4-a46e-6591ed8eab7b";
    //控件
    private TimetableView mTimetableView;
    private WeekView mWeekView;
    private int value_curWeek,temp_curWeek;
    private ImageButton takephotoButton;
    private LinearLayout layout;
    private TextView titleTextView;
    private List<MySubject> mySubjects=new ArrayList<>();
    private LinearLayout linearLayout;
    private Boolean cantuisong;
    private Integer tuisong_hour,tuisong_minute;
    private List<Schedule> clickSchedule=new ArrayList<>();//记录点击的shedule
    private SuperTextView headicon;
    //记录切换的周次，不一定是当前周
    public int target = -1;
    String termData="";
    private Integer countClick=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        return view;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent)
    {
        cantuisong=messageEvent.getTuisong();
        mTimetableView.isShowWeekends((messageEvent.getShow_weekend()));
        mTimetableView.isShowNotCurWeek(messageEvent.getShow_nweek_lesson());
        if(messageEvent.getMinnumber())
        {
            mTimetableView.maxSlideItem(12);
        }
        else
        {
            mTimetableView.maxSlideItem(10);
        }
        if(messageEvent.getShow_times())
        {
            OnSlideBuildAdapter listener = (OnSlideBuildAdapter) mTimetableView.onSlideBuildListener();
            listener.setTimes(MySupport.DEFAULT_COURSE_STARTTIME)
                    .setTimeTextColor(Color.BLACK);
        }
        else
        {
            mTimetableView.callback((ISchedule.OnSlideBuildListener) null);
        }
        if(!messageEvent.getBackground().equals(""))
        {
            Bitmap bitmap = BitmapFactory.decodeFile(messageEvent.getBackground());
            Drawable drawable =new BitmapDrawable(getResources(),bitmap);
            linearLayout.setBackground(drawable);

        }
        if(messageEvent.getMySubjects()!=null)
        {
            mWeekView.source(messageEvent.getMySubjects());
            mTimetableView.source(messageEvent.getMySubjects());
            mWeekView.updateView();
        }
        mTimetableView.updateView();
        tuisong_hour=messageEvent.getHour();
        tuisong_minute=messageEvent.getMinute();
        setReminder(false);
        setReminder(cantuisong);

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        linearLayout=getView().findViewById(R.id.ly_classtable);
        super.onActivityCreated(savedInstanceState);
        LitePal.initialize(getContext());//初始化dataBase
        headicon=getView().findViewById(R.id.course_head_icon);
        headicon.setLeftImageViewClickListener(new SuperTextView.OnLeftImageViewClickListener() {
            @Override
            public void onClickListener(ImageView imageView) {
                Intent intent=new Intent(getActivity(), AlbumLessonSelectActivity.class);
                startActivity(intent);
            }
        });
        setHeadIcon();//同步头像
        takephotoButton=getView().findViewById(R.id.btn_photo);
        takephotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), PhotoTestActivity.class);
                intent.putExtra("courseName",getLessons(value_curWeek));
                startActivity(intent);
            }
        });
        titleTextView = getView().findViewById(R.id.id_title);
        layout = getView().findViewById(R.id.id_layout);
        layout.setOnClickListener(this);
        mWeekView = getView().findViewById(R.id.id_weekview);
        mTimetableView = getView().findViewById(R.id.id_timetableView);

        //List<SuperLesson> superLesson=(List<SuperLesson>) getIntent().getSerializableExtra("SuperLessons");
        //mySubjects=changeLesson(superLesson);
        SharedPreferences sp=getActivity().getSharedPreferences(LOCAL_COURSE,MODE_PRIVATE);
        termData=sp.getString(MySupport.CHOOSE_TERM_STATUS,"@@");
        Log.d("CourseActivity","term:"+termData);
        Boolean local_mySubjects=sp.getBoolean(MySupport.LOCAL_COURSE_DATABASE,false);//标记是否已经存储
        value_curWeek=sp.getInt("curweek",1);
        if(sp.getString("local_day"," ").equals(" "))
        {
            //Toast.makeText(getActivity(),"哦吼",Toast.LENGTH_LONG).show();
            String date=sp.getString(MySupport.DATE_LOCALDATE,"2019-02-18");
            value_curWeek=toolsHelper.getWeekNumber(date,0);

        }
        else
        {
            String local_date=sp.getString("local_day","");
            value_curWeek=toolsHelper.getWeekNumber(local_date,value_curWeek);
        }
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt(MySupport.LOCAL_CURWEEK,value_curWeek);
        editor.apply();
        if(sp.getString(MySupport.CONFIG_BG," ").equals(" "))
        {
            //Toast.makeText(getActivity(),"正常！",Toast.LENGTH_SHORT).show();

        }
        else
        {
            Bitmap bitmap = BitmapFactory.decodeFile(sp.getString(MySupport.CONFIG_BG,"@"));
            Drawable drawable =new BitmapDrawable(getResources(),bitmap);
            linearLayout.setBackground(drawable);
        }
        temp_curWeek=value_curWeek;
        Request();
        if(!local_mySubjects){
            toAuth();
        }
        else {
            //Gson gson=new Gson();
            //mySubjects=gson.fromJson(local_mySubjects,new TypeToken<List<MySubject>>(){}.getType());
            if(!termData.equals("@@"))
            {
                mySubjects=LitePal.where("term=?",termData).find(MySubject.class);
                Log.d("CourseActivity","正在匹配！！！！");
                Log.d("CourseActivity",mySubjects.size()+"个");
            }
           else
            {
                mySubjects=LitePal.findAll(MySubject.class);
                termData=mySubjects.get(0).getTerm();
                editor.putString(MySupport.CHOOSE_TERM_STATUS,termData);
                editor.apply();
            }
            initTimetableView();
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)//透明状态栏
        {
            Window window = getActivity().getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(Color.parseColor("#FFFFFF"));
        }
        EventBus.getDefault().register(this);//EventBus注册
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences(MySupport.CONFIG_DATA,MODE_PRIVATE);
        cantuisong=sharedPreferences.getBoolean(MySupport.CONFIG_TUISONG,false);
        tuisong_hour=sharedPreferences.getInt(MySupport.CONFIG_TUUISONG_HOUR,7);
        tuisong_minute=sharedPreferences.getInt(MySupport.CONFIG_TUUISONG_MINUTE,0);
        setReminder(cantuisong);
        SharedPreferences.Editor editor1=sp.edit();
        Log.d("CourseActivity",termData);
        editor1.putString(MySupport.CHOOSE_TERM_STATUS,termData);
        editor1.apply();
    }
    private void setReminder(boolean b) {
        Calendar mCalendar;
        //得到日历实例，主要是为了下面的获取时间
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        //获取当前毫秒值
        long systemTime = System.currentTimeMillis();

        //是设置日历的时间，主要是让日历的年月日和当前同步
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然可能个别手机会有8个小时的时间差
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        //设置在几点提醒  设置的为7点
        mCalendar.set(Calendar.HOUR_OF_DAY, tuisong_hour);
        //设置在几分提醒  设置的为00分
        mCalendar.set(Calendar.MINUTE, tuisong_minute);
        //下面这两个看字面意思也知道
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);
        //上面设置的就是7点0分的时间点
        //获取上面设置的7点0分的毫秒值
        long selectTime = mCalendar.getTimeInMillis();

        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if(systemTime > selectTime) {
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        AlarmManager am= (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        // 创建将执行广播的PendingIntent
        PendingIntent pi= PendingIntent.getBroadcast(getActivity(), 0, new Intent(getActivity(), AlarmReceiver.class), 0);
        if(b){
            //am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pi);
            am.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), (1000 * 60 * 60 * 24), pi);
        }
        else{
            // cancel current alarm
            am.cancel(pi);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void getConfig()
    {
        SharedPreferences sp=getActivity().getSharedPreferences(MySupport.CONFIG_DATA,MODE_PRIVATE);
        mTimetableView.isShowWeekends(sp.getBoolean(MySupport.CONFIG_HIDEWEEKEND,true));
        mTimetableView.isShowNotCurWeek(sp.getBoolean(MySupport.CONFIG_HIDELESOONS,false));
        cantuisong=sp.getBoolean(MySupport.CONFIG_TUISONG,false);
        if(sp.getBoolean(MySupport.CONFIG_MAXNUMBERS,false))
        {
            mTimetableView.maxSlideItem(12);
        }
        else
        {
            mTimetableView.maxSlideItem(10);
        }
        if(sp.getBoolean(MySupport.CONFIG_SHOWTIME,false)) {
            String[] times = new String[]{
                    "8:00", "8:55", "10:00", "10:55",
                    "14:00", "14:55", "16:00", "16:55",
                    "17:50", "18:35", "19:00", "20:00"};
            OnSlideBuildAdapter listener = (OnSlideBuildAdapter) mTimetableView.onSlideBuildListener();
            listener.setTimes(times)
                    .setTimeTextColor(Color.BLACK);
        }else
        {
            mTimetableView.callback((ISchedule.OnSlideBuildListener) null);
            mTimetableView.updateSlideView();
        }
        mTimetableView.updateView();
    }
    public void toAuth(){
        Intent intent=new Intent(getActivity(), AuthActivity.class);
        intent.putExtra(AuthActivity.FLAG_TYPE,AuthActivity.TYPE_IMPORT);
        startActivityForResult(intent,REQUEST_IMPORT);
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        Log.d("CourseActivity",requestCode+"");
        if(requestCode==REQUEST_IMPORT&&resultCode==AuthActivity.RESULT_STATUS)
        {
            SuperResult scanResult= SuperUtils.getResult(data);
            if(scanResult.isSuccess())
            {
                mySubjects=changeLesson(scanResult.getLessons());
                /*Gson gson=new Gson();
                String a=gson.toJson(changeLesson(scanResult.getLessons()));
                SharedPreferences sp=getActivity().getSharedPreferences(LOCAL_COURSE,MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("mySubjects",a);
                editor.commit();*/
                for(MySubject item:mySubjects)
                {
                    item.save();
                }
                SharedPreferences sp=getActivity().getSharedPreferences(LOCAL_COURSE,MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putBoolean(MySupport.LOCAL_COURSE_DATABASE,true);
                editor.putString(MySupport.CHOOSE_TERM_STATUS,mySubjects.get(0).getTerm());
                editor.apply();
                initTimetableView();//初始化课程表

            }

            else{
                Toasty.error(getActivity(),scanResult.getErrMsg(),Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode==22&&data!=null)
        {
            Log.d("CourseActivity","执行了初始化操作");
            Log.d("CourseActivity",data.getExtras().getBoolean("resultView")+"");
            Log.d("CourseActivity",data.getExtras().getBoolean("resultEdit")+"");
            int loc,index;
            if (temp_curWeek % 2 == 0&&clickSchedule.size()>1) {
                index = 1;
            }
            else
            {
                index = 0;
            }
            if(data.getExtras().getBoolean("resultView"))
            {

                Log.d("CourActivity","已经刷新了布局");
                //List<MySubject> re=LitePal.where("term=?",termData).find(MySubject.class);

                loc=mTimetableView.dataSource().indexOf(clickSchedule.get(index));
                if(loc==-1)
                {
                    Toasty.error(getContext(),"Oooops！出错了,请重启本应用",Toast.LENGTH_SHORT).show();
                }
                mTimetableView.dataSource().remove(loc);
                mTimetableView.updateView();
            }
            else if(data.getExtras().getBoolean("resultEdit"))
            {

                int address=mTimetableView.dataSource().indexOf(clickSchedule.get(index));
                Log.d("mTimetableView",index+"");
                if(address==-1)
                {
                    /*List<MySubject> mm=LitePal.where("term=?",termData).find(MySubject.class);
                    mTimetableView.dataSource().clear();
                    mTimetableView.source(mm);*/
                }
                else
                {
                    Log.d("修改课程","执行了修改");
                    List<MySubject> mm=LitePal.where("term=?",termData).find(MySubject.class);
                    mTimetableView.dataSource().clear();
                    mTimetableView.source(mm);
                }
               mTimetableView.updateView();
            }
        }
        else if(requestCode==23&&data!=null)
        {
            Log.d("添加课程","执行了外层");
            Log.d("添加课程","状态"+data.getExtras().getBoolean("result"));
            if(data.getExtras().getBoolean("result"))
            {
                Log.d("添加课程","刷新");
                List<MySubject> mm=LitePal.where("term=?",termData).find(MySubject.class);
                mTimetableView.dataSource().clear();
                mTimetableView.source(mm);
                mTimetableView.updateView();
            }
        }
    }

    /**
     * 初始化课程控件
     */
    private void initTimetableView() {
        //获取控件
        /*mWeekView = findViewById(R.id.id_weekview);
        mTimetableView = findViewById(R.id.id_timetableView);*/

        //设置周次选择属性mySubjects
        mWeekView.source(mySubjects)//在这里放你封装好的List<mySubjects>
                .curWeek(value_curWeek)
                .callback(new IWeekView.OnWeekItemClickedListener() {
                    @Override
                    public void onWeekClicked(int week) {
                        int cur = mTimetableView.curWeek();
                        //更新切换后的日期，从当前周cur->切换的周week
                        mTimetableView.onDateBuildListener()
                                .onUpdateDate(cur, week);
                        mTimetableView.changeWeekOnly(week);
                    }
                })
                .callback(new IWeekView.OnWeekLeftClickedListener() {
                    @Override
                    public void onWeekLeftClicked() {
                        onWeekLeftLayoutClicked();
                    }
                })
                .isShow(false)//设置隐藏，默认显示
                .showView();

        mTimetableView.source(mySubjects)
                .curWeek(value_curWeek)
                .curTerm(mySubjects.get(0).getTerm())
                .maxSlideItem(12)
                .monthWidthDp(40)
                //透明度
                //日期栏0.1f、侧边栏0.1f，周次选择栏0.6f
                //透明度范围为0->1，0为全透明，1为不透明
                .alpha(0.6f, 0.6f, 0.8f)
                .callback(new ISchedule.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, List<Schedule> scheduleList) {
                        //display(scheduleList);
                        clickSchedule=scheduleList;
                        Log.d("mTimetableView","监听到"+clickSchedule.get(0).getName());
                        Intent intent=new Intent(getActivity(), ViewCourseActivity.class);
                        intent.putExtra("date",temp_curWeek);
                        intent.putExtra("term",mySubjects.get(0).getTerm());
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("SuperLessons",(Serializable) scheduleList);
                        Log.d("mTimetableView","监听到"+clickSchedule.get(0).getName()+scheduleList.get(0).getWeekList().toString());
                        intent.putExtras(bundle);
                        startActivityForResult(intent,22);
                    }
                })
                .callback(new ISchedule.OnItemLongClickListener() {
                    @Override
                    public void onLongClick(View v, int day, int start) {
                        /*Toasty.info(getActivity(),
                                "长按:周" + day  + ",第" + start + "节",
                                Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(getActivity(), EditCourseActivity.class));
                        /*Intent intent=new Intent(getActivity(), ViewCourseActivity.class);
                        intent.putExtra("day",day);
                        intent.putExtra("start",start);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("SuperLessons",(Serializable) scheduleList);
                        intent.putExtras(bundle);
                        startActivity(intent);*/
                    }
                })
                .callback(new ISchedule.OnWeekChangedListener() {
                    @Override
                    public void onWeekChanged(int curWeek) {
                        titleTextView.setText("第" + curWeek + "周");
                        temp_curWeek=curWeek;
                    }
                })
                //旗标布局点击监听
                .callback(new ISchedule.OnFlaglayoutClickListener() {
                    @Override
                    public void onFlaglayoutClick(int day, int start) {
                        mTimetableView.hideFlaglayout();
                        /*Toasty.info(getActivity(),
                                "点击了旗标:周" + (day + 1) + ",第" + start + "节",
                                Toast.LENGTH_SHORT).show();*/
                        Intent intent=new Intent(getContext(), EditCourseActivity.class);
                        //schedules = (List<Schedule>) getIntent().getSerializableExtra("SuperLessons");
                        //date = getIntent().getIntExtra("课", 0);
                        //term=getIntent().getStringExtra("学期");
                        Schedule schedule=initSchedule(day+1,start);
                        List<Schedule> scheduleList=new ArrayList<>();
                        scheduleList.add(schedule);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("SuperLessons",(Serializable) scheduleList);
                        intent.putExtras(bundle);
                        intent.putExtra("课",0);
                        intent.putExtra("学期",mySubjects.get(0).getTerm());
                        startActivityForResult(intent,23);
                    }
                })
                .showView();
        getConfig();
    }

    private Schedule initSchedule(int curDay,int start_lesson)
    {
        Schedule schedule=new Schedule();
        schedule.setDay(curDay);
        schedule.setRoom("");
        schedule.setName("");
        List<Integer> week=new ArrayList<>();
        for(int i=1;i<=temp_curWeek;i++)
        {
            week.add(i);
        }
        schedule.setWeekList(week);
        schedule.setStart(start_lesson);
        schedule.setStep(2);
        return schedule;
    }
    /**
     * 更新一下，防止因程序在后台时间过长（超过一天）而导致的日期或高亮不准确问题。
     */
    @Override
    public void onStart() {
        super.onStart();
        mTimetableView.onDateBuildListener()
                .onHighLight();
    }

    /**
     * 周次选择布局的左侧被点击时回调<br/>
     * 对话框修改当前周次
     */
    protected void onWeekLeftLayoutClicked() {
        final String items[] = new String[20];
        int itemCount = mWeekView.itemCount();
        for (int i = 0; i < itemCount; i++) {
            items[i] = "第" + (i + 1) + "周";
        }
        target = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("设置当前周");
        builder.setSingleChoiceItems(items, mTimetableView.curWeek() - 1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        target = i;
                    }
                });
        builder.setPositiveButton("设置为当前周", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (target != -1) {
                    mWeekView.curWeek(target + 1).updateView();
                    SharedPreferences sharedPreferences=getActivity().getSharedPreferences(LOCAL_COURSE,MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putInt("curweek",target+1);
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                    Date date=new Date();
                    editor.putString("local_day",sdf.format(date));
                    editor.apply();
                    mTimetableView.changeWeekForce(target + 1);
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    /**
     * 显示内容
     *
     * @param beans
     */
    protected void display(List<Schedule> beans) {
        String str = "";
        for (Schedule bean : beans) {
            str += "课程名称:"+bean.getName() + " 地点:"+bean.getRoom()+" 老师:"+bean.getTeacher()+"\n";
        }
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_layout:
                //如果周次选择已经显示了，那么将它隐藏，更新课程、日期
                //否则，显示
                if (mWeekView.isShowing()) {
                    mWeekView.isShow(false);
                    titleTextView.setTextColor(getResources().getColor(R.color.app_course_textcolor_blue));
                    int cur = mTimetableView.curWeek();
                    mTimetableView.onDateBuildListener()
                            .onUpdateDate(cur, cur);
                    mTimetableView.changeWeekOnly(cur);
                } else {
                    mWeekView.isShow(true);
                    titleTextView.setTextColor(getResources().getColor(R.color.app_red));
                }
                break;
        }
    }



    /**
     * 设置月份宽度
     */
    private void setMonthWidth() {
        mTimetableView.monthWidthDp(50).updateView();
    }

    /**
     * 设置月份宽度,默认40dp
     */
    private void resetMonthWidth() {
        mTimetableView.monthWidthDp(40).updateView();
    }

    private void upDate()
    {
        //BmobUpdateAgent.update(getActivity());
        Intent intent = new Intent(Intent.ACTION_VIEW);    //为Intent设置Action属性
        intent.setData(Uri.parse(UpdateURL)); //为Intent设置DATA属性
        startActivity(intent);
    }
    public static List<MySubject> changeLesson(List<SuperLesson> lessons)
    {
        List<MySubject> mySubjects=new ArrayList<>();
        if(lessons==null)
        {
            return null;
        }
        else
        {
            for(SuperLesson superLesson:lessons)
            {
                String name=superLesson.getName();
                String room=superLesson.getLocale();
                String teacher=superLesson.getTeacher();
                int start=superLesson.getSectionstart();
                int step=superLesson.getSectionend()-start+1;
                int day=superLesson.getDay();
                String arr[]=superLesson.getPeriod().split(" ");
                List<Integer> integers=new ArrayList<>();
                for(int i=0;i<arr.length;i++)
                {
                    integers.add(Integer.valueOf(arr[i]));
                }
                //String name,  String room, String teacher, List<Integer> weekList, int start, int step, int day, String term

                mySubjects.add(new MySubject(name,room,teacher,integers,start,step,day,superLesson.getSemester()));
            }
        }

        return mySubjects;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void Request ()
    {             //获取读写权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//版本判断
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
            }
        }

    }
    public String getLessons(Integer curweek)//获取当前时间对应时间段并返回课程
    {
        String courseName="";
        int curday;
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat formatters = new SimpleDateFormat("HH:mm");
        Date curDates = new Date(System.currentTimeMillis());// 获取当前时间
        String strs = formatters.format(curDates);
        String []dds=new String[]{};
        // 分取系统时间 小时分
        dds = strs.split(":");
        int dhs = Integer.parseInt(dds[0]);
        int dms = Integer.parseInt(dds[1]);
        Log.d("课表",dhs+":"+dms);
        Date today = new Date();
        Calendar c=Calendar.getInstance();
        c.setTime(today);
        int weekday=c.get(Calendar.DAY_OF_WEEK);
        if(weekday==1)//1代表星期日
        {
            curday=7;
        }
        else
        {
            curday=weekday-1;
        }
        Log.d("课表","curday:"+curday);
        int result=-1;
        for(int i=0;i<MySupport.DEFAULT_COURSE_STARTTIME.length-1;i++)
        {
            String [] stime=new String[]{};
            String [] etime=new String[]{};
            stime=MySupport.DEFAULT_COURSE_STARTTIME[i].split(":");
            etime=MySupport.DEFAULT_COURSE_STARTTIME[i+1].split(":");
            //开始时间
            int sth = Integer.parseInt(stime[0]);//小时
            int stm = Integer.parseInt(stime[1]);//分
            //结束时间
            int eth = Integer.parseInt(etime[0]);//小时
            int etm = Integer.parseInt(etime[1]);//分

            if (sth == dhs&&stm<=dms) {
                result=i+1;
                break;
            }
            else if (dhs == eth&&etm >= dms)
            {
                result=i+1;
                break;
            }
            else if(dhs>sth&&dhs<eth)
            {
                result=i+1;
                break;
            }

        }
        Log.d("课表","result="+result+"");
        List<MySubject> tempSubject= LessonsHelper.getHaveSubjectsWithDay(mySubjects,curweek,curday);
        for(MySubject item:tempSubject)
        {
            if(result>=item.getStart()&&result<=(item.getStep()+item.getStart()-1))
            {
                courseName=item.getName();
                break;
            }
        }
        return courseName;

    }
    /*public static Bitmap createCircleImage(Bitmap source) {
        int length = source.getWidth() < source.getHeight() ? source.getWidth() : source.getHeight();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle(length / 2, length / 2, length / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }*/
    private void setHeadIcon()
    {
        SharedPreferences sp = getActivity().getSharedPreferences(MySupport.LOCAL_FRAGMENT2, MODE_PRIVATE);
        if (!sp.getString(MySupport.CONFIG_HEAD, " ").equals(" ")) {
            Bitmap bitmap = BitmapFactory.decodeFile(sp.getString(MySupport.CONFIG_HEAD, " "));
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            headicon.setLeftIcon(drawable);

        }
    }
}
