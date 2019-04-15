package com.paul.simpletools.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.paul.simpletools.R;
import com.paul.simpletools.dataBase.MySubject;
import com.zhuangfei.timetable.model.ScheduleColorPool;

import java.util.List;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/4/1 22：49
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class NonViewAdapter extends BaseAdapter {

    List<MySubject> schedules;
    Context context;
    LayoutInflater inflater;

    public NonViewAdapter(Context context, List<MySubject> schedules) {
        this.context = context;
        this.schedules = schedules;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return schedules.size();
    }

    @Override
    public Object getItem(int i) {
        return schedules.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View mView = null;
        ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_no, null);
            holder.nameTextView = convertView.findViewById(R.id.id_nonview_name);
            holder.colorTextView = convertView.findViewById(R.id.id_nonview_color);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MySubject schedule= (MySubject) getItem(i);
        ScheduleColorPool colorPool=new ScheduleColorPool(context);
        holder.nameTextView.setText(schedule.getName()+" "+schedule.getRoom()+" "+schedule.getTeacher());
        holder.colorTextView.setBackgroundColor(colorPool.getColorAuto(schedule.getColorRandom()));
        return convertView;
    }

    class ViewHolder {
        TextView nameTextView;
        TextView colorTextView;
    }
}
