package com.paul.simpletools.classbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.paul.simpletools.classbox.activity.AuthActivity;
import com.paul.simpletools.classbox.listener.OnSuperAuthAdapter;
import com.paul.simpletools.classbox.listener.OnSuperAuthListener;
import com.paul.simpletools.classbox.model.SuperProfile;
import com.paul.simpletools.classbox.model.SuperResult;
import com.paul.simpletools.classbox.utils.SuperParser;
import com.paul.simpletools.classbox.utils.SuperUtils;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 封装了扫码的API，可以省略掉输入账号的过程
 * Created by Liu ZhuangFei on 2018/8/21.
 */
public class SuperBox {

    private Context context;
    private OnSuperAuthListener onSuperAuthListener;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private String url;

    public SuperBox(@NonNull Context context){
        sp=context.getSharedPreferences(AuthActivity.SAVE_SHARED_NAME,context.MODE_PRIVATE);
        editor=sp.edit();
        this.context=context;
    }

    public OnSuperAuthListener getOnSuperAuthListener() {
        return onSuperAuthListener;
    }

    public void request(String url,OnSuperAuthListener onSuperAuthListener){
        if(onSuperAuthListener==null) onSuperAuthListener=new OnSuperAuthAdapter();
        else this.onSuperAuthListener=onSuperAuthListener;

        this.url=url;

        String numberEncrypt=sp.getString(AuthActivity.SAVE_NUMBER,null);
        String passwordEncrypt=sp.getString(AuthActivity.SAVE_PASSWORD,null);
        if(numberEncrypt!=null&&passwordEncrypt!=null){
            login(numberEncrypt,passwordEncrypt);
        }else{
            getOnSuperAuthListener().onError("用户名或密码为空");
        }
    }

    private void clearLocalData(){
        editor.putString(AuthActivity.SAVE_NUMBER,null);
        editor.putString(AuthActivity.SAVE_PASSWORD,null);
        editor.commit();
    }

    private void login(final String numberEncrypt, final String passwordEncrypt) {
        SuperBoxRequest.loginForSuper(context, numberEncrypt, passwordEncrypt,
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody body=response.body();
                        try {
                            String result=body.string();
                            SuperProfile profile= SuperParser.parseLoginResult(result);
                            if(profile==null){
                               getOnSuperAuthListener().onError("对登录的返回结果解析过程中出现问题");
                                clearLocalData();
                            }else if(!profile.isSuccess()){
                                getOnSuperAuthListener().onError(""+profile.getErrMsg());
                                clearLocalData();
                            }else{
                                getOnSuperAuthListener().onLoginSuccess(profile);
                                scanCode();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            getOnSuperAuthListener().onException(e);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        getOnSuperAuthListener().onException(t);
                    }
                });
    }

    public void scanCode() {
        if(TextUtils.isEmpty(url)){
            getOnSuperAuthListener().onError("url is null");
            return;
        }
        String[] params= SuperUtils.parseUrl(url);
        if(params==null||params.length<3){
            getOnSuperAuthListener().onError("url格式错误");
            return;
        }

        SuperBoxRequest.scanCodeForSuper(context, params[0], params[1],
                params[2], new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            String result = response.body().string();
                            SuperResult scanResult=SuperParser.parseScanResult(result);
                            if(!scanResult.isSuccess()){
                                getOnSuperAuthListener().onError(scanResult.getErrMsg());
                            }else{
                                getOnSuperAuthListener().onScanSuccess(scanResult.getLessons());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            getOnSuperAuthListener().onException(e);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                        getOnSuperAuthListener().onException(t);
                    }
                });
    }
}
