package com.paul.simpletools.LessonAlbum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.paul.simpletools.Adapter.MyGridViewAdapter;
import com.paul.simpletools.R;
import com.paul.simpletools.Tools.AlbumHelper;
import com.paul.simpletools.dataBase.MyPhotoBean;
import com.paul.simpletools.dataBase.MySupport;

import java.util.ArrayList;
import java.util.List;

public class AlbumPhotoSelectActivity extends AppCompatActivity {
    private String name;
    private List<MyPhotoBean> myPhotoBeans;
    private GridView gridView;
    String TAG="AlbumPhotoSelectActivity";
    private MyGridViewAdapter adapter;
    private Integer count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_photo_select);
        Intent intent=getIntent();
        name=intent.getStringExtra("课程名称");
        Log.d(TAG,"课程名称"+name);
        initData();
    }
    void initData()
    {
        myPhotoBeans=AlbumHelper.getMyPhotoBean();
        myPhotoBeans= AlbumHelper.splitWithName(myPhotoBeans,name);
        count=myPhotoBeans.size();
        Log.d("监听数据刷新","count start"+count);
        gridView=findViewById(R.id.album_photoselect_gv);
        adapter=new MyGridViewAdapter(myPhotoBeans,this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(AlbumPhotoSelectActivity.this,AlbumViewPhotoActivity.class);
                intent.putExtra("照片地址",myPhotoBeans.get(position).getPhotoPath());
                startActivityForResult(intent, MySupport.REQUEST_ALUBUM_VIEWPHOTO);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(data!=null&&requestCode==MySupport.REQUEST_ALUBUM_VIEWPHOTO)
        {
            if(data.getStringExtra("result").equals("delete")) {
                Log.d("监听数据刷新", "执行刷新");
                myPhotoBeans = AlbumHelper.getMyPhotoBean();
                myPhotoBeans = AlbumHelper.splitWithName(myPhotoBeans, name);
                adapter.setMyPhotoBeans(myPhotoBeans);
                adapter.notifyDataSetChanged();
                count--;
            }
            Log.d("监听数据刷新","count end"+count);
            if(count==0)
            {
                Intent intent = new Intent();
                //把返回数据存入Intent
                intent.putExtra("result", "delete");
                //设置返回数据
                Log.d("监听数据刷新","应该执行了结束方法");
                AlbumPhotoSelectActivity.this.setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}
