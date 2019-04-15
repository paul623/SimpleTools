package com.paul.simpletools.classbox.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Liu ZhuangFei on 2018/8/12.
 */

public class AddCookiesInterceptor implements Interceptor {

    Context context;

    public AddCookiesInterceptor(Context context){
        this.context=context;
    }

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        String cookieStr = context.getSharedPreferences("cookies_app",Context.MODE_PRIVATE).getString("cookie","");
        if (!TextUtils.isEmpty(cookieStr)) {
            return chain.proceed(chain.request().newBuilder().header("Cookie", cookieStr).build());
        }
        return chain.proceed(chain.request());
    }
}
