package com.paul.simpletools.Tools;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.paul.simpletools.R;
import com.paul.simpletools.classbox.model.SuperLesson;
import com.paul.simpletools.dataBase.MySubject;
import com.zhuangfei.timetable.model.Schedule;

import org.litepal.LitePal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ViewCourseActivity extends Activity {
    SuperTextView mcourse, mteacher, mlocation, mdate, mtime, mbtn1, mbtn2;
    Integer cur_week;
    List<Schedule> superLesson;
    private Boolean flag=false;
    private Integer date;
    private String term;//学期
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LitePal.initialize(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_course);
        superLesson = (List<Schedule>) getIntent().getSerializableExtra("SuperLessons");
        date = getIntent().getIntExtra("date", 0);
        term=getIntent().getStringExtra("term");
        Log.d("最后的测试","查看课程信息-学期： "+term);
        if (date % 2 == 0&&superLesson.size()>1) {
            cur_week = 1;
        }
        else
        {
            cur_week = 0;
        }
        initView();
        setView(superLesson);

        //display(superLesson);
    }

    private void initView() {
        mcourse = findViewById(R.id.stv_view_course);
        mlocation = findViewById(R.id.stv_view_location);
        mteacher = findViewById(R.id.stv_view_teacher);
        mdate = findViewById(R.id.stv_view_date);
        mtime = findViewById(R.id.stv_view_time);
        mbtn1 = findViewById(R.id.btn_view_setting);
        mbtn2 = findViewById(R.id.btn_view_delete);
        mbtn1.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                Intent intent=new Intent(ViewCourseActivity.this, EditCourseActivity.class);
                Bundle bundle=new Bundle();
                intent.putExtra("课",cur_week);
                bundle.putSerializable("SuperLessons",(Serializable) superLesson);
                intent.putExtra("学期",term);
                intent.putExtras(bundle);
                startActivityForResult(intent,10086);
            }
        });
        mbtn2.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                //删除函数，目前不会
                final AlertDialog.Builder normalDialog =
                        new android.app.AlertDialog.Builder(ViewCourseActivity.this,R.style.AlertDialog);
                normalDialog.setIcon(R.mipmap.ic_launcher);
                normalDialog.setTitle("警告");
                //normalDialog.setView()

                normalDialog.setMessage("确认删除本课程？删除后不可恢复！");
                normalDialog.setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete();
                            }


                        });
                normalDialog.setNegativeButton("手滑了~",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getContext(),"吓死我了···",Toast.LENGTH_SHORT).show();
                                Toasty.error(ViewCourseActivity.this, "吓死我了···", Toast.LENGTH_SHORT, true).show();
                            }
                        });
                normalDialog.show();
            }

        });
    }

    private void setView(List<Schedule> superLesson) {
        int endDay=superLesson.get(cur_week).getStart()+superLesson.get(cur_week).getStep()-1;
        mcourse.setLeftString(superLesson.get(cur_week).getName());
        mlocation.setLeftString(superLesson.get(cur_week).getRoom());
        mteacher.setLeftString(superLesson.get(cur_week).getTeacher());
        mdate.setLeftString(superLesson.get(cur_week).getWeekList().toString());
        mtime.setLeftString("周"+superLesson.get(cur_week).getDay()+" 第"+superLesson.get(cur_week).getStart()+"~"+endDay+"节");

    }
    private void delete()
    {
        List<MySubject> mySubjects=LitePal.where("term=? and name=?",term,superLesson.get(cur_week).getName())
               .find(MySubject.class);
        Log.d("朱俊瑞","curweek"+superLesson.get(cur_week).getDay());
        boolean fl=false;
        if(mySubjects.size()==0||mySubjects.size()>1)
        {
            Log.d("朱俊瑞","执行外层");
            for(MySubject item:mySubjects)
            {
                if(item.getDay()==superLesson.get(cur_week).getDay())
                {
                    Log.d("朱俊瑞","执行我了");
                    item.delete();
                    Intent intent=new Intent();
                    intent.putExtra("resultView",true);
                    ViewCourseActivity.this.setResult(RESULT_OK, intent);

                    fl=true;
                    break;
                }
            }
            if(fl)
            {
                Toasty.success(ViewCourseActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toasty.error(ViewCourseActivity.this,"删除失败，数据库异常！",Toast.LENGTH_SHORT).show();
            }

            Intent intent=new Intent();
            intent.putExtra("resultView",fl);
            ViewCourseActivity.this.setResult(RESULT_OK, intent);
            finish();
        }
        else
        {
            mySubjects.get(0).delete();
            Intent intent=new Intent();
            intent.putExtra("resultView",true);
            Toasty.success(ViewCourseActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
            ViewCourseActivity.this.setResult(RESULT_OK, intent);
            finish();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent=new Intent();
        if(data!=null)
        {
            flag=data.getExtras().getBoolean("result");
            /*Schedule superLessonA = (Schedule) getIntent().getSerializableExtra("SuperLessons");
            Bundle bundle=new Bundle();
            bundle.putSerializable("SuperLessons",(Serializable) superLessonA);
            intent.putExtras(bundle);*/
        }


        intent.putExtra("resultEdit",flag);
        ViewCourseActivity.this.setResult(RESULT_OK, intent);
        finish();

    }
}
