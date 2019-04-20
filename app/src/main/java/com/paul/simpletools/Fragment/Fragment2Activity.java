package com.paul.simpletools.Fragment;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.allen.library.SuperTextView;
import com.paul.simpletools.LaunchActivity;
import com.paul.simpletools.R;
import com.paul.simpletools.Tools.LoadInternet;
import com.paul.simpletools.Tools.SettingActivity;
import com.paul.simpletools.Tools.UsersEditActivity;
import com.paul.simpletools.Tools.toolsHelper;
import com.paul.simpletools.dataBase.MySupport;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


import static android.content.Context.MODE_PRIVATE;

public class Fragment2Activity extends Fragment {
    private SuperTextView stv_mine_users;
    private SuperTextView stv_mine_setting;
    private SuperTextView stv_clear_data;
    private SuperTextView stv_checkUpdate;
    private SuperTextView stv_contact_us;
    private SuperTextView stv_helper;
    private SuperTextView stv_header;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment2, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        stv_mine_users = getActivity().findViewById(R.id.stv_mine_users);
        stv_mine_setting = getActivity().findViewById(R.id.stv_mine_setting);
        stv_checkUpdate = getActivity().findViewById(R.id.stv_checkUpDate);
        stv_clear_data = getActivity().findViewById(R.id.stv_clearData);
        stv_helper = getActivity().findViewById(R.id.stv_helper);
        stv_contact_us = getActivity().findViewById(R.id.stv_contact);
        MyListener myListener = new MyListener();
        stv_mine_users.setOnClickListener(myListener);
        stv_mine_setting.setOnClickListener(myListener);
        stv_checkUpdate.setOnClickListener(myListener);
        stv_clear_data.setOnClickListener(myListener);
        stv_helper.setOnClickListener(myListener);
        stv_contact_us.setOnClickListener(myListener);
        stv_header = getActivity().findViewById(R.id.head_bar);
        stv_header.setLeftImageViewClickListener(new SuperTextView.OnLeftImageViewClickListener() {
            @Override
            public void onClickListener(ImageView imageView) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });
        SharedPreferences sp = getActivity().getSharedPreferences(MySupport.LOCAL_FRAGMENT2, MODE_PRIVATE);
        if (!sp.getString(MySupport.CONFIG_HEAD, " ").equals(" ")) {
            Bitmap bitmap = BitmapFactory.decodeFile(sp.getString(MySupport.CONFIG_HEAD, " "));
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            stv_header.setLeftIcon(drawable);

        }
        try {
            refreash();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        stv_header.setLeftString(sp.getString(MySupport.LOCAL_WORDS," "));

        handler=new Handler();
    }
    private void contactUs()
    {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(MySupport.conact_qq)));
        }catch (Exception e){
            Toast.makeText(getActivity(),"请检查是否安装QQ！",Toast.LENGTH_SHORT).show();
        }


    }
    private void accountSet()
    {
        Toast.makeText(getActivity(),"功能正在测试中！",Toast.LENGTH_SHORT).show();
    }
    private void helper()
    {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(getActivity());
        normalDialog.setIcon(R.mipmap.ic_launcher);
        normalDialog.setTitle("Hi，你好呀");
        normalDialog.setMessage("我是课表拍拍的作者 巴塞罗那的余晖"+"\n"+"欢迎你使用本软件~我会继续努力完善功能的！"+"\n"+"同时希望你们可以通过反馈按钮给我提建议哦！祝愉快");
        normalDialog.setPositiveButton("好的喵",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(),"嘻嘻(#^.^#)",Toast.LENGTH_SHORT).show();
                    }
                });
        normalDialog.show();
    }
    private void clearLocalData()
    {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(getActivity());
        normalDialog.setIcon(R.mipmap.ic_launcher);
        normalDialog.setTitle("停！确认删除本地数据吗？");
        normalDialog.setMessage("执行本操作后将清空所有本地数据，包括课表和配置信息，确认吗？");
        normalDialog.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sp=getActivity().getSharedPreferences(MySupport.LOCAL_COURSE,MODE_PRIVATE);
                        SharedPreferences.Editor editor=sp.edit();
                        editor.clear();
                        editor.apply();
                        SharedPreferences ss=getActivity().getSharedPreferences(MySupport.LOCAL_FRAGMENT2,MODE_PRIVATE);
                        SharedPreferences.Editor editor1=ss.edit();
                        editor1.clear();
                        editor1.apply();
                        Toast.makeText(getActivity(),"数据清理完毕！",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), LaunchActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });
        normalDialog.setNegativeButton("手滑了~",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(),"吓死我了···",Toast.LENGTH_SHORT).show();
                    }
                });
        normalDialog.show();

    }
    private void UpDate()
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);    //为Intent设置Action属性
        intent.setData(Uri.parse(MySupport.UpdateURL)); //为Intent设置DATA属性
        startActivity(intent);
    }

    private class MyListener implements SuperTextView.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.stv_mine_setting:
                    startActivity(new Intent(getActivity(), SettingActivity.class));
                    break;
                case R.id.stv_mine_users:
                    accountSet();
                    startActivity( new Intent(getActivity(), UsersEditActivity.class));
                    break;
                case R.id.stv_checkUpDate:
                    UpDate();
                    break;
                case R.id.stv_contact:
                    contactUs();
                    break;
                case R.id.stv_helper:
                    helper();
                    break;
                case R.id.stv_clearData:
                    clearLocalData();
                    break;

            }

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ContentResolver resolver = getActivity().getContentResolver();
        try {
            if(data!=null) {
                Uri originalUri = data.getData(); // 获得图片的uri
                MediaStore.Images.Media.getBitmap(resolver, originalUri);
                String[] proj = {MediaStore.Images.Media.DATA};
                @SuppressWarnings("deprecation")
                Cursor cursor = getActivity().managedQuery(originalUri, proj, null, null,
                        null);
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String path = cursor.getString(column_index);
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                Drawable drawable =new BitmapDrawable(getResources(),bitmap);
                stv_header.setLeftIcon(drawable);
                SharedPreferences sp = getActivity().getSharedPreferences(MySupport.LOCAL_FRAGMENT2, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(MySupport.CONFIG_HEAD, path);
                editor.apply();
            }
            else
            {
                Toast.makeText(getActivity(),"操作错误或已取消",Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            Log.e("TAG-->Error", e.toString());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void refreash() throws ParseException {
        SharedPreferences sp=getActivity().getSharedPreferences(MySupport.LOCAL_FRAGMENT2,MODE_PRIVATE);
        String everydaywords=sp.getString(MySupport.LOCAL_WORDS,"");

        if(!sp.getString(MySupport.LOCAL_WORDS_DATE,"@").equals("@")
            && toolsHelper.getWeekNumber(sp.getString(MySupport.LOCAL_WORDS_DATE,"2019-4-20"))==0)
            {
                //不需要进行操作
            }
        else
        {
            LoadInternet.sendRequestWithOkhttp(MySupport.REQUEST_WORDS, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                        Toast.makeText(getActivity(),"每日一句更新失败，错误码:"+e,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String result=response.body().string();
                    try {
                            JSONObject jsonObject=new JSONObject(result);
                            String content=jsonObject.optString("content");
                            String note=jsonObject.optString("note");
                            final String heihei=content+"\n"+note;
                            SharedPreferences s=getActivity().getSharedPreferences(MySupport.LOCAL_FRAGMENT2,MODE_PRIVATE);
                            SharedPreferences.Editor editor=s.edit();
                            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                            Date date=new Date();
                            editor.putString(MySupport.LOCAL_WORDS_DATE,sdf.format(date));
                            editor.putString(MySupport.LOCAL_WORDS,heihei);
                            editor.apply();
                            Runnable runnable=new Runnable() {
                                @Override
                                public void run() {
                                    stv_header.setLeftString(heihei);
                                }
                            };
                            handler.post(runnable);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }

        }

}


