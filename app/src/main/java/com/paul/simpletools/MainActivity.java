package com.paul.simpletools;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paul.simpletools.Tools.EditCourseActivity;
import com.paul.simpletools.Tools.LessonsHelper;
import com.paul.simpletools.Tools.ViewCourseActivity;
import com.paul.simpletools.classbox.activity.AuthActivity;
import com.paul.simpletools.classbox.model.SuperLesson;
import com.paul.simpletools.classbox.model.SuperResult;
import com.paul.simpletools.classbox.utils.SuperUtils;
import com.paul.simpletools.dataBase.MySubject;
import com.paul.simpletools.dataBase.SubjectRepertory;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.zhuangfei.timetable.TimetableView;
import com.zhuangfei.timetable.listener.ISchedule;
import com.zhuangfei.timetable.listener.IWeekView;
import com.zhuangfei.timetable.listener.OnSlideBuildAdapter;
import com.zhuangfei.timetable.model.Schedule;
import com.zhuangfei.timetable.model.ScheduleSupport;
import com.zhuangfei.timetable.view.WeekView;

import org.json.JSONArray;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.update.BmobUpdateAgent;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    private static String appkey="5c2fcdf15f7879bd65b8d9a9e4d26c89";
    public static final int REQUEST_IMPORT=1;
    private static final String LOCAL_COURSE="local_course";
    private static final String UpdateURL="https://www.yuque.com/docs/share/3d3b7318-c4d4-4bf4-a46e-6591ed8eab7b";
    //控件
    private TimetableView mTimetableView;
    private WeekView mWeekView;
    private int value_curWeek,temp_curWeek;
    private Button takephotoButton;
    private LinearLayout layout;
    private TextView titleTextView;
    private List<MySubject> mySubjects;

    //记录切换的周次，不一定是当前周
    public int target = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takephotoButton=findViewById(R.id.btn_photo);
        takephotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,PhotoActivity.class);
                intent.putExtra("courseName",getLessons(value_curWeek));
                startActivity(intent);
            }
        });
        titleTextView = findViewById(R.id.id_title);
        layout = findViewById(R.id.id_layout);
        layout.setOnClickListener(this);
        mWeekView = findViewById(R.id.id_weekview);
        mTimetableView = findViewById(R.id.id_timetableView);
        //List<SuperLesson> superLesson=(List<SuperLesson>) getIntent().getSerializableExtra("SuperLessons");
        //mySubjects=changeLesson(superLesson);
        SharedPreferences sp=this.getSharedPreferences(LOCAL_COURSE,MODE_PRIVATE);
        String local_mySubjects=sp.getString("mySubjects"," ");
        value_curWeek=sp.getInt("curweek",1);
        temp_curWeek=value_curWeek;
        Request();
        Bmob.initialize(this, appkey);
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        if(local_mySubjects.equals(" ")){
            toAuth();
        }
        else {
            Gson gson=new Gson();
            mySubjects=gson.fromJson(local_mySubjects,new TypeToken<List<MySubject>>(){}.getType());
            initTimetableView();
        }
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)//透明状态栏
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void toAuth(){
        Intent intent=new Intent(this, AuthActivity.class);
        intent.putExtra(AuthActivity.FLAG_TYPE,AuthActivity.TYPE_IMPORT);
        startActivityForResult(intent,REQUEST_IMPORT);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==REQUEST_IMPORT&&resultCode==AuthActivity.RESULT_STATUS)
        {
            SuperResult scanResult= SuperUtils.getResult(data);
            if(scanResult.isSuccess())
            {
                mySubjects=changeLesson(scanResult.getLessons());
                Gson gson=new Gson();
                String a=gson.toJson(changeLesson(scanResult.getLessons()));
                SharedPreferences sp=this.getSharedPreferences(LOCAL_COURSE,MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("mySubjects",a);
                editor.commit();
                initTimetableView();//初始化课程表
            }
            else{
                Toast.makeText(this,scanResult.getErrMsg(),Toast.LENGTH_SHORT).show();
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
                .curTerm("大二下学期")
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
                        Intent intent=new Intent(MainActivity.this, ViewCourseActivity.class);
                        intent.putExtra("date",temp_curWeek);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("SuperLessons",(Serializable) scheduleList);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                })
                .callback(new ISchedule.OnItemLongClickListener() {
                    @Override
                    public void onLongClick(View v, int day, int start) {
                        Toast.makeText(MainActivity.this,
                                "长按:周" + day  + ",第" + start + "节",
                                Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MainActivity.this,
                                "点击了旗标:周" + (day + 1) + ",第" + start + "节",
                                Toast.LENGTH_SHORT).show();
                        /*Intent intent=new Intent(MainActivity.this, EditCourseActivity.class);
                        startActivity(intent);*/
                    }
                })
                .showView();
    }

    /**
     * 更新一下，防止因程序在后台时间过长（超过一天）而导致的日期或高亮不准确问题。
     */
    @Override
    protected void onStart() {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
                    SharedPreferences sharedPreferences=MainActivity.this.getSharedPreferences(LOCAL_COURSE,MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putInt("curweek",target+1);
                    editor.commit();
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
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
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
     * 删除课程
     * 内部使用集合维护课程数据，操作集合的方法来操作它即可
     * 最后更新一下视图（全局更新）
     */
    protected void deleteSubject() {
        int size = mTimetableView.dataSource().size();
        int pos = (int) (Math.random() * size);
        if (size > 0) {
            mTimetableView.dataSource().remove(pos);
            mTimetableView.updateView();
        }
    }

    /**
     * 添加课程
     * 内部使用集合维护课程数据，操作集合的方法来操作它即可
     * 最后更新一下视图（全局更新）
     */
    protected void addSubject() {
        List<Schedule> dataSource = mTimetableView.dataSource();
        int size = dataSource.size();
        if (size > 0) {
            Schedule schedule = dataSource.get(0);
            dataSource.add(schedule);
            mTimetableView.updateView();
        }
    }

    /**
     * 隐藏非本周课程
     * 修改了内容的显示，所以必须更新全部（性能不高）
     * 建议：在初始化时设置该属性
     * <p>
     * updateView()被调用后，会重新构建课程，课程会回到当前周
     */
    protected void hideNonThisWeek() {
        mTimetableView.isShowNotCurWeek(false).updateView();
    }

    /**
     * 显示非本周课程
     * 修改了内容的显示，所以必须更新全部（性能不高）
     * 建议：在初始化时设置该属性
     */
    protected void showNonThisWeek() {
        mTimetableView.isShowNotCurWeek(true).updateView();
    }

    /**
     * 设置侧边栏最大节次，只影响侧边栏的绘制，对课程内容无影响
     *
     * @param num
     */
    protected void setMaxItem(int num) {
        mTimetableView.maxSlideItem(num).updateSlideView();
    }

    /**
     * 显示时间
     * 设置侧边栏构建监听，TimeSlideAdapter是控件实现的可显示时间的侧边栏
     */
    protected void showTime() {
        String[] times = new String[]{
                "8:00", "8:55", "10:00", "10:55",
                "14:00", "14:55", "16:00", "16:55",
                "17:50", "18:35", "19:00", "20:00"
        };
        OnSlideBuildAdapter listener = (OnSlideBuildAdapter) mTimetableView.onSlideBuildListener();
        listener.setTimes(times)
                .setTimeTextColor(Color.BLACK);
        mTimetableView.updateSlideView();
    }

    /**
     * 隐藏时间
     * 将侧边栏监听置Null后，会默认使用默认的构建方法，即不显示时间
     * 只修改了侧边栏的属性，所以只更新侧边栏即可（性能高），没有必要更新全部（性能低）
     */
    protected void hideTime() {
        mTimetableView.callback((ISchedule.OnSlideBuildListener) null);
        mTimetableView.updateSlideView();
    }

    /**
     * 显示WeekView
     */
    protected void showWeekView() {
        mWeekView.isShow(true);
    }

    /**
     * 隐藏WeekView
     */
    protected void hideWeekView() {
        mWeekView.isShow(false);
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

    /**
     * 隐藏周末
     */
    private void hideWeekends() {
        mTimetableView.isShowWeekends(false).updateView();
    }

    /**
     * 显示周末
     */
    private void showWeekends() {
        mTimetableView.isShowWeekends(true).updateView();
    }
    private void clearLocalData()
    {
        SharedPreferences sp=this.getSharedPreferences(LOCAL_COURSE,MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.clear();
        editor.commit();
        Toast.makeText(this,"本地缓存清理完毕！请重启咩！",Toast.LENGTH_LONG).show();
    }
    private void upDate()
    {
        BmobUpdateAgent.update(this);
        Intent intent = new Intent(Intent.ACTION_VIEW);    //为Intent设置Action属性
        intent.setData(Uri.parse(UpdateURL)); //为Intent设置DATA属性
        startActivity(intent);
    }
    public List<MySubject> changeLesson(List<SuperLesson> lessons)
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
                mySubjects.add(new MySubject("大二下学期",name,room,teacher,integers,start,step,day,2,""));
            }
        }

        return mySubjects;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void Request ()
    {             //获取读写权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//版本判断
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
            }
        }
    }
    public String getLessons(Integer curweek)//获取当前时间对应时间段并返回课程
    {
        String courseName="错误百出";
        int curday;
        Calendar calendar=Calendar.getInstance();
        String[] times = new String[]{
                "8:00", "8:55", "10:00", "10:55",
                "14:00", "14:55", "16:00", "16:55",
                "17:50", "18:35", "19:00", "20:00"
        };
        SimpleDateFormat formatters = new SimpleDateFormat("HH:mm");
        Date curDates = new Date(System.currentTimeMillis());// 获取当前时间
        String strs = formatters.format(curDates);
        String[] dds = new String[] {};
        // 分取系统时间 小时分
        dds = strs.split(":");
        int dhs = Integer.parseInt(dds[0]);
        int dms = Integer.parseInt(dds[1]);
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
        int result=-1;
        for(int i=0;i<times.length-1;i++)
        {
            String [] stime=new String[]{};
            String [] etime=new String[]{};
            stime=times[i].split(":");
            etime=times[i+1].split(":");
            //开始时间
            int sth = Integer.parseInt(stime[0]);//小时
            int stm = Integer.parseInt(stime[1]);//分
            //结束时间
            int eth = Integer.parseInt(etime[0]);//小时
            int etm = Integer.parseInt(etime[1]);//分

            if (sth <= dhs && dhs <= eth)
            {
                if (sth == dhs && stm <= dms) {
                    result=i+1;
                    break;
                }
                else if (dhs == eth && etm >= dms) {
                    result=i+1;
                    break;
                }
            }
        }
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
}
