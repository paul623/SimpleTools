package com.paul.simpletools.Ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.allen.library.SuperButton;
import com.paul.simpletools.R;
import com.paul.simpletools.dataBase.MySupport;
import com.paul.simpletools.dataBase.TermData;
import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class LoopViewActivity extends Activity {

    LoopView loopview;
    SuperButton button1,button2;
    Integer choice=0;
    List<TermData> termDatas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loop_view);
        loopview = findViewById(R.id.loopview);
        button1=findViewById(R.id.loopview_postive);
        button2=findViewById(R.id.loopview_negative);
        ArrayList<String> list = new ArrayList();
        LitePal.initialize(this);
        termDatas=LitePal.findAll(TermData.class);
        if(termDatas.size()==0)
        {
            Toast.makeText(LoopViewActivity.this,"错误！本地数据库损坏，请清除数据后重试！",Toast.LENGTH_SHORT).show();
            finish();
        }
        for(TermData item:termDatas)
        {
            list.add(item.getTermName());
            Log.d("Loop：",item.getTermName());
        }
        loopview.setNotLoop();
        loopview.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                Log.d("选择的是","index"+index);
                choice=index;
            }
        });
        loopview.setItems(list);
        loopview.setInitPosition(2);
        loopview.setTextSize(18);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoopViewActivity.this,termDatas.get(choice).getTermName()+" 切换成功！",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.putExtra(MySupport.CHOOSE_TERM_STATUS,termDatas.get(choice).getTermName());
                LoopViewActivity.this.setResult(RESULT_OK, intent);
                finish();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
