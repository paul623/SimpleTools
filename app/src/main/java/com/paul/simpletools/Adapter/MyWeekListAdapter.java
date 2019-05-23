package com.paul.simpletools.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.paul.simpletools.R;

import java.util.ArrayList;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/5/23 11：03
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class MyWeekListAdapter extends BaseAdapter {
    private Context context;
    //ArrayList<MyImage> image;
    //ArrayList<String>  className;
    private LayoutInflater mLayoutInflater;

    public MyWeekListAdapter(ArrayList image, ArrayList className, Context context){
        this.context = context;
        //this.image = image;
        //this.className = className;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        //return image.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        //return image.get(position);
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        public TextView tv_1;
        public ImageView iv_1, iv_2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View layout = mLayoutInflater.inflate(R.layout.album_week_listitem,null);
        ImageView imageview = layout.findViewById(R.id.album_week_iv);
        //MyImage image1 = image.get(position);
        //imageview.setImageResource(image1.getimage());
        TextView textview = layout.findViewById(R.id.album_week_tv);
        //String classname = className.get(position);
        //textview.setText(classname);
        return layout;
    }
}
