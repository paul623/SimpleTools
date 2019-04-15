package com.paul.simpletools.Tools;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.EditText;

import com.paul.simpletools.R;
import com.paul.simpletools.dataBase.MySubject;

public class EditCourseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);
        Drawable drawable1=getResources().getDrawable(R.drawable.icon_1);
        drawable1.setBounds(0,0,80,80);
        EditText et_1=findViewById(R.id.et_1);
        et_1.setCompoundDrawables(drawable1,null,null,null);
        Drawable drawable2=getResources().getDrawable(R.drawable.icon_2);
        drawable2.setBounds(0,0,80,80);
        EditText et_2=findViewById(R.id.et_2);
        et_2.setCompoundDrawables(drawable2,null,null,null);
    }

}
