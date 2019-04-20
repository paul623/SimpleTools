package com.paul.simpletools.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paul.simpletools.R;
import com.paul.simpletools.dataBase.MySupport;

import java.util.Timer;
import java.util.TimerTask;


public class Fragment3Activity extends Fragment implements View.OnClickListener {
    //定义TextView对象
    private TextView tv,show_time;
    //定义Buttn对象
    private Button btn_start;
    //声明两个整型数值,分别代表分、秒
    private int MINUTE = 24;
    private int SECOND = 60;
    boolean flag,tag;
    Timer timer;
    TimerTask timerTask;
    SharedPreferences sp;
    int x;
    //引入处理机制
    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){
                timeCount(); //跳转到timeCount()方法
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment3, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sp=getActivity().getSharedPreferences(MySupport.LOCAL_COURSE,Context.MODE_PRIVATE);
        x=sp.getInt(MySupport.LOCAL_TOMATO,0);
        indexView(); //跳转到indexView()

    }
    /**
     *初始化方法
     */
    private void indexView(){
        //添加动画效果
        LinearLayout linearLayout =getActivity().findViewById(R.id.LinearLayout1);
       /* Animation animation= AnimationUtils.loadAnimation(getContext(),R.anim.welcome);
        linearLayout.startAnimation(animation);*/

        btn_start = getView().findViewById(R.id.btn_start);  //匹配Button控件
        show_time=getView().findViewById(R.id.tomoto_showtime);
        show_time.setText("您累计得到了"+x+"个番茄");
        tv = getView().findViewById(R.id.tomoto_time); //匹配TextView控件
        tv.setText("25:00"); //设置TextView文本内容
        btn_start.setOnClickListener(this); //为btn_start按钮注册监听器
        flag=false;
        tag=false;
        tv.setTextSize(80);
    }

    /**
     * 计时器方法
     */
    private void calcTimer(){
        //声明定时器
        timer = new Timer(){};
        //声明定时任务
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what=1;
                myHandler.sendMessage(msg);
            }
        };
        //启动定时器
        timer.schedule(timerTask, 1000, 1000);

    }

    /**
     * 时间倒计方法
     */
    private void timeCount(){
        if(MINUTE>=0){
            SECOND--;
            String minute = String.valueOf(MINUTE);
            String second = String.valueOf(SECOND);

            if(SECOND>=0){
                if(SECOND<10){
                    second = "0"+second;
                }
                if(MINUTE<10){
                    minute = "0"+minute;
                }
                tv.setText(minute+":"+second);
            }else{
                SECOND = 60;
                MINUTE--;
            }

        }else{
            tv.setTextSize(50);
            tv.setText("您种植了一个健康的番茄~");
            btn_start.setText("继续~");
            tag=true;

        }
    } //timeCount()

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.btn_start){
            if(flag==false)
            {
                calcTimer(); //跳转到calcTimer()方法
                tv.setTextSize(80);
                btn_start.setText("停止");
                flag=true;
            }
            else
            {
                tv.setText("25:00"); //设置TextView文本内容
                tv.setTextSize(80);
                btn_start.setText("开始种番茄！");
                flag=false;
                timer.cancel();
                timer=null;
                timerTask.cancel();
                timerTask=null;
                MINUTE = 24;
                SECOND=60;
                if(tag)
                {
                    tag=false;
                    sp=getActivity().getSharedPreferences(MySupport.LOCAL_COURSE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp.edit();
                    x=x+1;
                    show_time.setText("您累计得到了"+x+"个番茄");
                    editor.putInt(MySupport.LOCAL_TOMATO,x);
                    editor.apply();
                }
            }

        }
    }
}
