package com.paul.simpletools.Adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import com.paul.simpletools.R;

import java.util.ArrayList;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/6/5 17：46
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class LessonTimeSelectAdapter extends BaseAdapter {
    private Context mcontext;
    private ArrayList<String> lesson;
    private ArrayList<String> time;
    LayoutInflater mLayoutInflater;

    public LessonTimeSelectAdapter(Context context,ArrayList lesson, ArrayList time){
        this.mcontext = context;
        this.lesson = lesson;
        this.time = time;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 12;
    }

    @Override
    public Object getItem(int position) {
        return time.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.lesson_time_select_list_item,null);
        TextView mTv_lesson = view.findViewById(R.id.tv_lesson1);
        mTv_lesson.setText(lesson.get(position));
        final TextView mTv_time = view.findViewById(R.id.tv_time1);
        mTv_time.setText(time.get(position));
        mTv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(mcontext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String times=hourOfDay+":";
                        if(minute<10)
                        {
                            times=times+"0"+minute;
                        }
                        else
                        {
                            times=times+minute;
                        }
                        mTv_time.setText(times);
                        time.set(position,times);
                    }
                },Integer.parseInt(time.get(position).split(":")[0]),Integer.parseInt(time.get(position).split(":")[1]),true);
                timePickerDialog.show();
            }
        });
        return view;

    }
    public ArrayList<String> getTimes()
    {
        return time;
    }
}
