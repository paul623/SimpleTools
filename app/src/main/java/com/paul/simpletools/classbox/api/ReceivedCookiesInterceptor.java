package com.paul.simpletools.classbox.api;

import android.content.Context;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by Liu ZhuangFei on 2018/8/12.
 */

public class ReceivedCookiesInterceptor implements Interceptor {

    Context context;

    public ReceivedCookiesInterceptor(Context context){
        this.context=context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        List<String> cookies = originalResponse.headers("Set-Cookie");
        String cookieStr = "";
        if (cookies != null && cookies.size() > 0) {
            for (int i = 0; i < cookies.size(); i++) {
                cookieStr += cookies.get(i);
            }
            context.getSharedPreferences("cookies_app",Context.MODE_PRIVATE).edit()
                    .putString("cookie", cookieStr)
                    .commit();
        }
        return originalResponse;
    }
}
