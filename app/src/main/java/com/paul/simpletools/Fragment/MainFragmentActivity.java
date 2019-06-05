package com.paul.simpletools.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.paul.simpletools.R;
import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.feedback.PgyerFeedbackManager;
import com.pgyersdk.update.PgyUpdateManager;

public class MainFragmentActivity extends FragmentActivity {

    private BottomNavigationView bottomNavigationView;
    private CourseActivity fragment1;
    private MineActivity fragment2;
    private TomatoActivity fragment3;
    private Fragment[] fragments;
    private int lastfragment;//用于记录上个选择的Fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);
        initFragment();
        PgyCrashManager.register(); //注册
        // 采用摇一摇弹出 Activity 方式
        new PgyerFeedbackManager.PgyerFeedbackBuilder()
                .setDisplayType(PgyerFeedbackManager.TYPE.DIALOG_TYPE)
                .builder()
                .register();
        new PgyUpdateManager.Builder()
                .register();
    }
    //初始化fragment和fragment数组
    private void initFragment()
    {

        fragment1 = new CourseActivity();
        fragment2 = new MineActivity();
        fragment3=new TomatoActivity();
        fragments = new Fragment[]{fragment1,fragment3,fragment2};
        lastfragment=0;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainview,fragment1)
                .show(fragment1)
                .commit();
        bottomNavigationView=findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(changeFragment);
    }
    //判断选择的菜单
    private BottomNavigationView.OnNavigationItemSelectedListener changeFragment= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId())
            {
                case R.id.navigation_home:
                {
                    if(lastfragment!=0)
                    {
                        switchFragment(lastfragment,0);

                        lastfragment=0;


                    }
                    return true;
                }
                case R.id.navigation_notifications:
                {
                    if(lastfragment!=2)
                    {
                        switchFragment(lastfragment,2);
                        lastfragment=2;
                        /*if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)//透明状态栏
                        {
                            Window window =fragments[lastfragment].getActivity().getWindow();
                            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            //设置状态栏颜色
                            window.setStatusBarColor(Color.parseColor("#FFFFFF"));
                        }*/
                    }
                    return true;
                }
                case R.id.navigation_note:
                {
                    if(lastfragment!=1)
                    {
                        switchFragment(lastfragment,1);
                        lastfragment=1;
                        /*if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)//透明状态栏
                        {
                            Window window =fragments[lastfragment].getActivity().getWindow();
                            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            //设置状态栏颜色
                            window.setStatusBarColor(Color.parseColor("#ED5046"));
                        }*/
                    }
                    return true;
                }

            }
            return false;
        }
    };
    //切换Fragment
    private void switchFragment(int lastfragment,int index)
    {
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        /*transaction.setCustomAnimations(
                R.animator.fragment_slide_right_enter,R.animator.fragment_slide_left_exit,
                R.animator.fragment_slide_left_enter,R.animator.fragment_slide_right_exit
        );*/
        transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
        if(fragments[index].isAdded()==false)
        {
            transaction.add(R.id.mainview,fragments[index]);

        }
        //transaction.show(fragments[index]).commitAllowingStateLoss();
        //transaction.addToBackStack(getClass().getName());
        transaction.show(fragments[index]);
        transaction.commitAllowingStateLoss();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)//透明状态栏
        {
            if (fragments[index].getActivity() != null) {
                Log.d("透明状态栏","执行了");
                Window window = fragments[index].getActivity().getWindow();
                //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                //设置状态栏颜色

                switch (index) {
                    case 0:
                        window.setStatusBarColor(Color.parseColor("#FFFFFF"));
                        break;
                    case 1:
                        window.setStatusBarColor(Color.parseColor("#ED5046"));
                        break;
                    case 2:
                        window.setStatusBarColor(Color.parseColor("#FFFFFF"));
                        break;
                }
            }
        }

    }




}
