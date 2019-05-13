package com.paul.simpletools.Tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.paul.simpletools.R;
import com.paul.simpletools.dataBase.MySubject;
import com.zhuangfei.timetable.model.Schedule;

import org.litepal.LitePal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class EditCourseActivity extends Activity {
    private SuperTextView weekselect,lessonspicker,headerbar;
    private String start,end;
    private NumberPicker np_start,np_end;
    private String lesson_start,lesson_end;
    private NumberPicker lesson_np_start,lesson_np_end;
    private List<Schedule> schedules;
    private EditText name,teaher,location;
    private Integer date;//星期几
    private List<Integer> weeks=new ArrayList<>();
    private Boolean even_number_status,signal_number_status;
    private Integer start_lessons,step_lessons;
    private String term;//当前学期
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);
        LitePal.initialize(this);
        weekselect=findViewById(R.id.stv_weekselect);
        lessonspicker=findViewById(R.id.stv_lessonselect);
        schedules = (List<Schedule>) getIntent().getSerializableExtra("SuperLessons");
        date = getIntent().getIntExtra("课", 0);
        term=getIntent().getStringExtra("学期");
        init();
    }
    void init()
    {
        headerbar=findViewById(R.id.st_head);
        name=findViewById(R.id.et_lesson_name);
        name.setText(schedules.get(date).getName());
        teaher=findViewById(R.id.et_lesson_teacher);
        teaher.setText(schedules.get(date).getTeacher());
        location=findViewById(R.id.et_lesson_location);
        location.setText(schedules.get(date).getRoom());
        weekselect.setLeftString(schedules.get(date).getWeekList().toString());
        lessonspicker.setLeftString("周"+schedules.get(date).getDay()+" "+schedules.get(date).getStart()+"-"+(schedules.get(date).getStart()+schedules.get(date).getStep()-1)+"节");
        headerbar.setLeftTvClickListener(new SuperTextView.OnLeftTvClickListener() {
            @Override
            public void onClickListener() {
                finish();
            }
        });
        headerbar.setRightTvClickListener(new SuperTextView.OnRightTvClickListener() {
            @Override
            public void onClickListener() {
                save();
            }
        });
        weeks=schedules.get(date).getWeekList();
        even_number_status=signal_number_status=false;//单数偶数周设置
        start_lessons=schedules.get(date).getStart();
        step_lessons=schedules.get(date).getStep();
        weekselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySuperWeekSelectDialog();
            }
        });
        lessonspicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mySuperLessonSelectDialog();
            }
        });
    }
    //even_number_status 记录偶数
    //signal_number_status 记录奇数
    //start 开始 end 结束
    void mySuperWeekSelectDialog()
    {
        RadioButton btn_even,btn_signal;
        AlertDialog.Builder builder7 = new AlertDialog.Builder(
                EditCourseActivity.this);
        builder7.setTitle("周次选择");
        View view = LayoutInflater.from(EditCourseActivity.this).inflate(
                R.layout.weeklist_selector, null);
        np_start =view.findViewById(R.id.np_start);
        np_end = view.findViewById(R.id.np_end);
        np_start.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        np_end.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        btn_even=view.findViewById(R.id.rb_even);
        btn_signal=view.findViewById(R.id.rb_signal);
        np_start.setMinValue(schedules.get(date).getWeekList().get(0));
        np_start.setMaxValue(20);
        np_start.setWrapSelectorWheel(false);
        np_end.setMinValue(schedules.get(date).getWeekList().get(0)+1);
        np_end.setMaxValue(20);
        np_end.setWrapSelectorWheel(false);
        List<Integer> a=schedules.get(date).getWeekList();
        start=a.get(0)+"";
        end=a.get(a.size()-1)+"";
        np_end.setValue(a.get(a.size()-1));
        np_start.setValue(a.get(0));

        np_start.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                start = newVal+"";
                np_end.setMinValue(newVal+1);
                //np_end.setMaxValue(20);
            }
        });

        np_end.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                end = newVal+"";
            }
        });
        //设置数据源
        btn_even.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                even_number_status=isChecked;
                Log.d("我恨你","监听偶数到变化"+isChecked);
            }
        });
        btn_signal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                signal_number_status=isChecked;
                Log.d("我恨你","监听奇数到变化"+isChecked);
            }
        });
        builder7.setView(view);
        builder7.setPositiveButton("确定",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 传数据

                        if(even_number_status==signal_number_status)//说明没有选中，即全都要上
                        {
                            Log.d("我恨你","执行全部筛选");
                            weeks.clear();

                            for(int i=Integer.parseInt(start);i<=Integer.parseInt(end);i++)
                            {
                                weeks.add(i);
                            }
                            even_number_status=false;
                            signal_number_status=false;
                        }
                        else if(even_number_status)
                        {
                            Log.d("我恨你","执行偶数筛选");
                            weeks.clear();
                            for(int i=Integer.parseInt(start);i<=Integer.parseInt(end);i++)
                            {
                                if(i%2==0)
                                {
                                    weeks.add(i);
                                }
                            }
                            even_number_status=false;
                            signal_number_status=false;
                        }
                        else if(signal_number_status)
                        {
                            weeks.clear();
                            for(int i=Integer.parseInt(start);i<=Integer.parseInt(end);i++)
                            {
                                Log.d("我恨你","奇数进程");
                                if(i%2!=0)
                                {
                                    weeks.add(i);
                                }
                            }
                            even_number_status=false;
                            signal_number_status=false;
                        }
                        weekselect.setLeftString(weeks.toString());
                        Toasty.success(EditCourseActivity.this,"设置成功！",Toast.LENGTH_SHORT).show();
                    }
                });
        builder7.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 结束dialog

                    }
                });
        builder7.create();
        builder7.show();
    }
    //lesson_start 开始
    //lesson_end 结束
    //start_lesson 开始 step_lesson
    void mySuperLessonSelectDialog()
    {
        AlertDialog.Builder builder7 = new AlertDialog.Builder(
                EditCourseActivity.this);
        builder7.setTitle("节次选择");
        View view = LayoutInflater.from(EditCourseActivity.this).inflate(
                R.layout.lesson_selector, null);
        start_lessons=schedules.get(date).getStart();
        step_lessons=schedules.get(date).getStart()+schedules.get(date).getStep()-1;
        lesson_np_start = view.findViewById(R.id.lesson_np_start);
        lesson_np_end = view.findViewById(R.id.lesson_np_end);
        lesson_np_start.setMinValue(1);
        lesson_np_start.setMaxValue(12);
        lesson_np_start.setValue(start_lessons);
        lesson_np_start.setWrapSelectorWheel(false);
        lesson_start=start_lessons+"";
        lesson_end=start_lessons+step_lessons-1+"";
        lesson_np_start.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                lesson_start = newVal+"";
                lesson_np_end.setMinValue(newVal+1);
            }
        });

        lesson_np_end.setMinValue(2);
        lesson_np_end.setMaxValue(12);
        lesson_np_end.setValue(step_lessons);
        lesson_np_end.setWrapSelectorWheel(false);
        lesson_np_end.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        lesson_np_start.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        lesson_np_end.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                lesson_end = newVal+"";
            }
        });
        //设置数据源
        builder7.setView(view);
        builder7.setPositiveButton("确定",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 传数据
                        start_lessons=Integer.parseInt(lesson_start);
                        step_lessons=Integer.parseInt(lesson_end)-start_lessons+1;
                        String result="周"+schedules.get(date).getDay()+" 第"+start_lessons+"-"+lesson_end+"节";
                        lessonspicker.setLeftString(result);
                        Toasty.success(EditCourseActivity.this,"设置成功！",Toast.LENGTH_SHORT).show();
                    }
                });
        builder7.setNegativeButton("取消",
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 结束dialog

                    }
                });
        builder7.create();
        builder7.show();
    }
    //mTimetableView
    void save()
    {
        Intent intent=new Intent();
        boolean flag=false;
        List<MySubject> mySubjects=LitePal.where("term=? and name=?",term,schedules.get(date).getName())
                .find(MySubject.class);//一个学期应该没有相同的课吧，除非是一个专业的，好烦
        Log.d("编辑课程",mySubjects.size()+"大小");
        if(mySubjects.size()>=1)
        {
            for(MySubject item:mySubjects)
            {
                if(item.getDay()==schedules.get(date).getDay())
                {
                    flag=true;
                    item.setName(name.getText().toString());
                    item.setTeacher(teaher.getText().toString());
                    item.setWeekList(weeks);
                    item.setStart(start_lessons);
                    item.setRoom(location.getText().toString());
                    item.setStep(step_lessons);
                    item.save();
                    Log.d("测试添加课程","已执行保存");
                    break;
                }
            }

            if(flag)
            {
                Toasty.success(EditCourseActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toasty.error(EditCourseActivity.this,"保存失败！",Toast.LENGTH_SHORT).show();
            }
            intent.putExtra("result",flag);
            EditCourseActivity.this.setResult(RESULT_OK, intent);
            finish();
        }
        else
        {
            MySubject item=new MySubject();
            item.setName(name.getText().toString());
            item.setTeacher(teaher.getText().toString());
            item.setWeekList(weeks);
            item.setStart(start_lessons);
            item.setRoom(location.getText().toString());
            item.setStep(step_lessons);
            item.setTerm(term);
            item.setDay(schedules.get(date).getDay());
            Log.d("编辑课程","学期"+term);
            Log.d("编辑课程","节数"+step_lessons);
            Log.d("编辑课程","周"+schedules.get(date).getDay());
            Log.d("编辑课程","----------------------------------------");
            Boolean b=item.save();
            Log.d("编辑课程","保存状态"+b);
            intent=new Intent();
            intent.putExtra("result",true);
            EditCourseActivity.this.setResult(RESULT_OK, intent);
            Toasty.info(EditCourseActivity.this,"已添加！",Toast.LENGTH_SHORT).show();
            finish();
        }

    }
}
