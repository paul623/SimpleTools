package com.paul.simpletools.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.paul.simpletools.R;

import java.util.ArrayList;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/5/23 10：49
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class MyGridViewAdapter extends BaseAdapter {
    private Context context;
    //数据源
    private LayoutInflater mLayoutInflater;

    public MyGridViewAdapter(ArrayList image, Context context){
        this.context = context;
        //this.image = image;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        //行数
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = mLayoutInflater.inflate(R.layout.album_griditem, null);
        ImageView imageView = layout.findViewById(R.id.album_grid_item1);
        //MyImage image1 = image.get(position);
        //imageView.setImageResource();
        return layout;
    }
}
