package com.paul.simpletools.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.paul.simpletools.R;
import com.paul.simpletools.Tools.AlbumHelper;
import com.paul.simpletools.dataBase.MyPhotoBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/5/23 10：57
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class MyLessonListAdapter extends BaseAdapter {
    private Context context;
    private List<MyPhotoBean> myPhotoBean;
    private List<String> names;
    private LayoutInflater mLayoutInflater;

    public MyLessonListAdapter(List<MyPhotoBean> myPhotoBean, List<String> names, Context context){
        this.context = context;
        this.myPhotoBean = myPhotoBean;
        this.names = names;
        mLayoutInflater = LayoutInflater.from(context);
    }
    public void refreash(List<MyPhotoBean> myPhotoBean, List<String> names)
    {
        this.myPhotoBean.clear();
        this.names.clear();
        this.myPhotoBean.addAll(myPhotoBean);
        this.names.addAll(names);
    }
    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View layout = mLayoutInflater.inflate(R.layout.album_lesson_listitem,null);
        ImageView imageview = layout.findViewById(R.id.album_lesson_iv);
        List<MyPhotoBean> myPhotoBeans=AlbumHelper.splitWithName(myPhotoBean,names.get(position));
        File file=new File(myPhotoBeans.get(0).getPhotoPath());
        Glide.with(context).load(file).into(imageview);
        TextView textview = layout.findViewById(R.id.album_lesson_tv);
        textview.setText(names.get(position));
        return layout;
    }
}
