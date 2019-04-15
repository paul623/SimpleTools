package com.paul.simpletools.classbox.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.paul.simpletools.classbox.activity.AuthActivity;
import com.paul.simpletools.classbox.model.SuperResult;

import java.util.HashMap;
import java.util.Map;

/**
 * 超表工具类
 * Created by Liu ZhuangFei on 2018/8/12.
 */

public class SuperUtils {
    private static final String TAG = "SuperUtils";

    /**
     * 检测本地是否有已登录的超表用户
     * @param context
     * @return
     */
    public static boolean isHasLocalData(Context context){
        SharedPreferences sp=context.getSharedPreferences(AuthActivity.SAVE_SHARED_NAME,context.MODE_PRIVATE);
        String numberEncrypt=sp.getString(AuthActivity.SAVE_NUMBER,null);
        String passwordEncrypt=sp.getString(AuthActivity.SAVE_PASSWORD,null);
        if(numberEncrypt==null||passwordEncrypt==null){
            return false;
        }
        return true;
    }

    /**
     * 从Intent中取出课程集合
     * @param intent
     * @return
     */
    public static SuperResult getResult(Intent intent){
        SuperResult result= (SuperResult) intent.getExtras().getSerializable(AuthActivity.RESULT_OBJ);
        return result;
    }

    /**
     * 判断传入的链接是否为课表课程码链接
     * @param url
     * @return true：是，false：不是
     */
    public static boolean isSuperUrl(String url){
        if(TextUtils.isEmpty(url)) return false;
        String[] arr=parseUrl(url);
        if(arr==null||arr.length<3) return false;
        return true;
    }

    /**
     * 从url中解析出参数
     * @param url
     * @return
     */
    public static String[] parseUrl(String url) {
        if(TextUtils.isEmpty(url)) return null;
        int index=url.indexOf("?");
        if(index==-1) return null;
        url=url.substring(index+1);
        if(TextUtils.isEmpty(url)) return null;

        try{
            String[] arr=url.split("&");
            Map<String,String> params=new HashMap<>();
            for(int i=0;i<arr.length;i++){
                if(!TextUtils.isEmpty(arr[i])&&arr[i].indexOf("=")!=-1){
                    String[] arr2=arr[i].split("=");
                    if(arr2.length>=2){
                        params.put(arr2[0],arr2[1]);
                    }
                }
            }
            if(!params.containsKey("i")||!params.containsKey("y")||!params.containsKey("tm")){
                return null;
            }
            return new String[]{params.get("i"),params.get("y"),params.get("tm")};
        }catch (Exception e){
            return null;
        }
    }
}
