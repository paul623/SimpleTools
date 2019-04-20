package com.paul.simpletools.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import com.paul.simpletools.R;
public class MainFragmentActivity extends FragmentActivity {

    private BottomNavigationView bottomNavigationView;
    private  Fragment1Activity fragment1;
    private Fragment2Activity fragment2;
    private Fragment3Activity fragment3;
    private Fragment[] fragments;
    private int lastfragment;//用于记录上个选择的Fragment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);
        initFragment();
    }
    //初始化fragment和fragment数组
    private void initFragment()
    {

        fragment1 = new Fragment1Activity();
        fragment2 = new Fragment2Activity();
        fragment3=new Fragment3Activity();
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
                    }
                    return true;
                }
                case R.id.navigation_note:
                {
                    if(lastfragment!=1)
                    {
                        switchFragment(lastfragment,1);
                        lastfragment=1;
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
        transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
        if(fragments[index].isAdded()==false)
        {
            transaction.add(R.id.mainview,fragments[index]);

        }
        transaction.show(fragments[index]).commitAllowingStateLoss();

    }




}
