package com.paul.simpletools.Fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.paul.simpletools.PhotoActivity;
import com.paul.simpletools.R;
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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import cn.bmob.v3.update.BmobUpdateAgent;

import static android.content.Context.MODE_PRIVATE;

public class Fragment1Activity extends Fragment implements View.OnClickListener {
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
    private List<MySubject> mySubjects;
    private LinearLayout linearLayout;

    //记录切换的周次，不一定是当前周
    public int target = -1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        return view;
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent)
    {
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
            String[] times = new String[]{
                    "8:00", "8:55", "10:00", "10:55",
                    "14:00", "14:55", "16:00", "16:55",
                    "17:50", "18:35", "19:00", "20:00"};
            OnSlideBuildAdapter listener = (OnSlideBuildAdapter) mTimetableView.onSlideBuildListener();
            listener.setTimes(times)
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
        mTimetableView.updateView();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        linearLayout=getView().findViewById(R.id.ly_classtable);
        super.onActivityCreated(savedInstanceState);
        takephotoButton=getView().findViewById(R.id.btn_photo);
        takephotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), PhotoActivity.class);
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
        String local_mySubjects=sp.getString("mySubjects"," ");
        value_curWeek=sp.getInt("curweek",1);
        if(sp.getString("local_day"," ").equals(" "))
        {
            //Toast.makeText(getActivity(),"哦吼",Toast.LENGTH_LONG).show();
            value_curWeek=toolsHelper.getWeekNumber(MySupport.DATE_LOCALDATE,0);
        }
        else
        {
            String local_date=sp.getString("local_day","");
            value_curWeek=toolsHelper.getWeekNumber(local_date,value_curWeek);
        }
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
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        EventBus.getDefault().register(this);//EventBus注册
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void getConfig()
    {
        SharedPreferences sp=getActivity().getSharedPreferences(MySupport.CONFIG_DATA,MODE_PRIVATE);
        mTimetableView.isShowWeekends(sp.getBoolean(MySupport.CONFIG_HIDEWEEKEND,false));
        mTimetableView.isShowNotCurWeek(sp.getBoolean(MySupport.CONFIG_HIDELESOONS,false));
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
        if(requestCode==REQUEST_IMPORT&&resultCode==AuthActivity.RESULT_STATUS)
        {
            SuperResult scanResult= SuperUtils.getResult(data);
            if(scanResult.isSuccess())
            {
                mySubjects=changeLesson(scanResult.getLessons());
                Gson gson=new Gson();
                String a=gson.toJson(changeLesson(scanResult.getLessons()));
                SharedPreferences sp=getActivity().getSharedPreferences(LOCAL_COURSE,MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putString("mySubjects",a);
                editor.commit();
                initTimetableView();//初始化课程表
            }
            else{
                Toast.makeText(getActivity(),scanResult.getErrMsg(),Toast.LENGTH_SHORT).show();
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
                        Intent intent=new Intent(getActivity(), ViewCourseActivity.class);
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
                        Toast.makeText(getActivity(),
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
                        Toast.makeText(getActivity(),
                                "点击了旗标:周" + (day + 1) + ",第" + start + "节",
                                Toast.LENGTH_SHORT).show();
                        /*Intent intent=new Intent(MainActivity.this, EditCourseActivity.class);
                        startActivity(intent);*/
                    }
                })
                .showView();
        getConfig();
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
        BmobUpdateAgent.update(getActivity());
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
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
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
        String []dds=new String[]{};
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
