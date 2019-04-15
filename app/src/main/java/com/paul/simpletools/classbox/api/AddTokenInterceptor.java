package com.paul.simpletools.classbox.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Liu ZhuangFei on 2018/8/12.
 */

public class AddTokenInterceptor implements Interceptor {
    private static final String TAG = "AddTokenInterceptor";

    Context context;

    public AddTokenInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request=chain.request();
        Request.Builder builder = request.newBuilder();
        String token = context.getSharedPreferences("app_tokens", Context.MODE_PRIVATE).getString("token", null);
//        if(token!=null){
//            builder.addHeader("Authorization","Token token=D32ky9mO7EjK0ZxXM-MZ2w");
//        }

//        Log.d(TAG, "intercept: "+new NUtils().sign(builder.toString()));

        builder.addHeader("SIGNATURE", "673df012f0174765d0ceb4633d2c24519bd19597")
                .addHeader("Authorization", "Token token=D32ky9mO7EjK0ZxXM-MZ2w");
        return chain.proceed(builder.build());
    }
}
