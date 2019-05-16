package com.paul.simpletools.Tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.paul.simpletools.Fragment.CourseActivity;
import com.paul.simpletools.R;
import com.paul.simpletools.classbox.activity.AuthActivity;
import com.paul.simpletools.classbox.model.SuperResult;
import com.paul.simpletools.classbox.utils.SuperUtils;
import com.paul.simpletools.dataBase.MySubject;
import com.paul.simpletools.dataBase.MySupport;
import com.paul.simpletools.dataBase.TermData;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class TermsManageActivity extends Activity {

    ListView listView;
    Button button;
    List<TermData> termDatas;
    List<String> termsName;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_manage);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)//透明状态栏
        {
            Window window = this.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(Color.parseColor("#FFFFFF"));
        }
        LitePal.initialize(this);
        listView=findViewById(R.id.lv_terms);
        button=findViewById(R.id.btn_terms);
        termDatas=LitePal.findAll(TermData.class);
        termsName=new ArrayList<>();
        for(TermData item:termDatas)
        {
            termsName.add(item.getTermName());
        }
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1
                ,termsName);//新建并配置ArrayAapeter
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(TermsManageActivity.this);
                normalDialog.setIcon(R.mipmap.ic_launcher);
                normalDialog.setTitle("警告");
                normalDialog.setMessage("确认删除"+termDatas.get(position).getTermName()+"吗？删除后不可恢复！");
                normalDialog.setPositiveButton("确认",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences sharedPreferences=getSharedPreferences(MySupport.LOCAL_COURSE,MODE_PRIVATE);
                                Log.d("Terms",sharedPreferences.getString(MySupport.CHOOSE_TERM_STATUS,"85484"));
                                if(termsName.get(position).equals(sharedPreferences.getString(MySupport.CHOOSE_TERM_STATUS,"")))
                                {
                                    Toasty.error(TermsManageActivity.this,"不可删除当前课表！",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    List<TermData> aa=LitePal.where("termName=?",termDatas.get(position).getTermName()).find(TermData.class);
                                    aa.get(0).delete();
                                    termsName.remove(position);
                                    List<MySubject> lessons=LitePal.where("term=?",termDatas.get(position).getTermName()).find(MySubject.class);
                                    for(MySubject item:lessons)
                                    {
                                        item.delete();
                                    }
                                    Toasty.success(TermsManageActivity.this,"删除成功！",Toast.LENGTH_SHORT).show();
                                   finish();
                                }

                            }
                        });
                normalDialog.setNegativeButton("手滑了~",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toasty.info(TermsManageActivity.this,"吓死我了···",Toast.LENGTH_SHORT).show();
                            }
                        });
                normalDialog.show();
                return true;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(TermsManageActivity.this, AuthActivity.class);
                intent.putExtra(AuthActivity.FLAG_TYPE, AuthActivity.TYPE_IMPORT);
                startActivityForResult(intent,11);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==11)
        {
            Log.d("TermsManage","执行了判断");
            if(resultCode == AuthActivity.RESULT_STATUS&&data!=null) {
                SuperResult scanResult = SuperUtils.getResult(data);
                if (scanResult.isSuccess()) {
                    List<MySubject> mySubjects = CourseActivity.changeLesson(scanResult.getLessons());
                    for (MySubject item : mySubjects) {
                        item.save();
                    }
                    termsName.add(mySubjects.get(0).getTerm());
                    Intent intent=new Intent();
                    intent.putExtra(MySupport.CHOOSE_TERM_STATUS,mySubjects.get(0).getTerm());
                    TermsManageActivity.this.setResult(2233,intent);
                    Log.d("TermsManage","执行啦！！！！");
                    finish();
                }

            }
            else if(resultCode==130&&data!=null)
            {
                SuperResult scanResult = SuperUtils.getResult(data);
                if (scanResult.isSuccess()) {
                    List<MySubject> mySubjects = CourseActivity.changeLesson(scanResult.getLessons());
                    List<MySubject> lessons=LitePal.where("term=?",mySubjects.get(0).getTerm()).find(MySubject.class);
                    for(MySubject item:lessons)
                    {
                        item.delete();
                    }
                    for (MySubject item : mySubjects) {
                        item.save();
                    }
                    termsName.add(mySubjects.get(0).getTerm());
                    Log.d("TermsManage","执行啦！！！！");
                    finish();
                }
            }

        }


    }
}
