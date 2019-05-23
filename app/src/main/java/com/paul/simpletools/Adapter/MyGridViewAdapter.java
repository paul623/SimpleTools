package com.paul.simpletools.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.paul.simpletools.R;
import com.paul.simpletools.dataBase.MyPhotoBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/5/23 10：49
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class MyGridViewAdapter extends BaseAdapter {
    private Context context;
    private List<MyPhotoBean> myPhotoBeans;
    //数据源
    private LayoutInflater mLayoutInflater;
    public MyGridViewAdapter(List<MyPhotoBean> myPhotoBeans, Context context){
        this.context = context;
        this.myPhotoBeans = myPhotoBeans;
        mLayoutInflater = LayoutInflater.from(context);
    }
    public void setMyPhotoBeans(List<MyPhotoBean> myPhotoBeans)
    {
        this.myPhotoBeans.clear();
        this.myPhotoBeans.addAll(myPhotoBeans);
    }
    @Override
    public int getCount() {
       return myPhotoBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return myPhotoBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = mLayoutInflater.inflate(R.layout.album_griditem, null);
        ImageView imageView = layout.findViewById(R.id.album_grid_item1);
        Log.d("课程名称","图片地址？"+myPhotoBeans.get(position).getPhotoPath());
        File file=new File(myPhotoBeans.get(position).getPhotoPath());
        Glide.with(context).load(file).into(imageView);
        return layout;
    }

}
