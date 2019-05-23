package com.paul.simpletools.LessonAlbum;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.allen.library.SuperTextView;
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
    private SuperTextView superTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_photo_select);
        Intent intent=getIntent();
        name=intent.getStringExtra("课程名称");
        Log.d(TAG,"课程名称"+name);
        initData();
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(Color.parseColor("#FFFFFF"));
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
        superTextView=findViewById(R.id.stv_photoselect);
        superTextView.setLeftImageViewClickListener(new SuperTextView.OnLeftImageViewClickListener() {
            @Override
            public void onClickListener(ImageView imageView) {
                finish();
            }
        });
        superTextView.setCenterString(name);
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
