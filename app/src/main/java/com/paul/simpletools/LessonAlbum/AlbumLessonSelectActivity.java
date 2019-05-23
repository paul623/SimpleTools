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
import android.widget.ImageView;
import android.widget.ListView;

import com.allen.library.SuperTextView;
import com.paul.simpletools.Adapter.MyGridViewAdapter;
import com.paul.simpletools.Adapter.MyLessonListAdapter;
import com.paul.simpletools.R;
import com.paul.simpletools.Tools.AlbumHelper;
import com.paul.simpletools.dataBase.MyPhotoBean;
import com.paul.simpletools.dataBase.MySupport;

import java.util.ArrayList;
import java.util.List;

public class AlbumLessonSelectActivity extends AppCompatActivity {

    private ListView listView;
    private List<MyPhotoBean> myPhotoBean;
    private List<String> names;
    private SuperTextView superTextView;
    private MyLessonListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_lesson_select);
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
        superTextView=findViewById(R.id.stv_lessonselect);
        listView=findViewById(R.id.album_lessonselect_lv);
        myPhotoBean=new ArrayList<>();
        names=new ArrayList<>();
        myPhotoBean=AlbumHelper.getMyPhotoBean();
        names=AlbumHelper.getPhotoName(myPhotoBean);
        adapter=new MyLessonListAdapter(myPhotoBean,names,this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(AlbumLessonSelectActivity.this, AlbumPhotoSelectActivity.class);
                intent.putExtra("课程名称",names.get(position));
                startActivityForResult(intent, MySupport.REQUEST_ALUBUM_SELECTPHOTO);
            }
        });
        superTextView.setLeftImageViewClickListener(new SuperTextView.OnLeftImageViewClickListener() {
            @Override
            public void onClickListener(ImageView imageView) {
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(data!=null&&requestCode==MySupport.REQUEST_ALUBUM_SELECTPHOTO)
        {
            if(data.getStringExtra("result").equals("delete"))
            {
                myPhotoBean=AlbumHelper.getMyPhotoBean();
                names=AlbumHelper.getPhotoName(myPhotoBean);
                adapter.refreash(myPhotoBean,names);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
