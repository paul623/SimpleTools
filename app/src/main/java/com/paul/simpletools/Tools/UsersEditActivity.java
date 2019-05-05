package com.paul.simpletools.Tools;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.paul.simpletools.R;
import com.paul.simpletools.dataBase.EveryDayBean;
import com.paul.simpletools.dataBase.MySubject;
import com.paul.simpletools.dataBase.MySupport;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class UsersEditActivity extends AppCompatActivity {
    private LinearLayout linearLayout;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_edit);
        LitePal.initialize(this);
        final List<String> words=new ArrayList<>();
        final List<EveryDayBean> everyDayBeans=LitePal.findAll(EveryDayBean.class);
        for(EveryDayBean item:everyDayBeans)
        {
            String a=item.getDate()+" "+item.getContent()+" "+item.getAuther();
            words.add(a);
        }
        setContentView(R.layout.activity_users_edit);
        listView = findViewById(R.id.lv_1);//在视图中找到ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,words);//新建并配置ArrayAapeter
        listView.setAdapter(adapter);
        linearLayout=findViewById(R.id.ly_user);
        SharedPreferences sp=getSharedPreferences(MySupport.LOCAL_COURSE,MODE_PRIVATE);
        String bgURL=sp.getString(MySupport.CONFIG_BG,"@");
        if(!bgURL.equals("@"))
        {
            Bitmap bitmap = BitmapFactory.decodeFile(bgURL);
            Drawable drawable =new BitmapDrawable(getResources(),bitmap);
            linearLayout.setBackground(drawable);
        }
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                ClipboardManager clipboard = (ClipboardManager)getSystemService(UsersEditActivity.CLIPBOARD_SERVICE);
                //创建ClipData对象
                //第一个参数只是一个标记，随便传入。
                //第二个参数是要复制到剪贴版的内容
                ClipData clip = ClipData.newPlainText("课表拍拍",words.get(position).toString() );
                //传入clipdata对象.
                clipboard.setPrimaryClip(clip);
                Toast.makeText(UsersEditActivity.this,"内容已复制到剪贴板！",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)//透明状态栏
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
