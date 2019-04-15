package com.paul.simpletools.classbox;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.paul.simpletools.classbox.api.AddTokenInterceptor;
import com.paul.simpletools.classbox.api.BoxService;
import com.paul.simpletools.classbox.api.SuperService;
import com.paul.simpletools.classbox.api.AddCookiesInterceptor;
import com.paul.simpletools.classbox.api.ReceivedCookiesInterceptor;
import com.paul.simpletools.classbox.model.BoxCourseParams;
import com.paul.simpletools.classbox.model.BoxLoginParams;
import com.paul.simpletools.classbox.model.BoxScanParams;
import com.paul.simpletools.classbox.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

/**
 * Created by Liu ZhuangFei on 2018/8/9.
 */

public class SuperBoxRequest {
    //本地存储
    public static final String SHARED_NAME = "superbox_shared";
    public static final String KEY_COOKIE = "superbox_key_cookie";

    public static final String KEY_TOKEN = "superbox_key_token";

    //super url
    public static final String SUPER_BASE_URL = "http://120.55.151.61/";
    public static final String SUPER_LOGIN = "V2/StudentSkip/loginCheckV4.action";
    public static final String SUPER_GET = "V2/Course/getCourseTableFromServer.action";
    public static final String SUPER_SCAN = "V2/Course/getCourseTableBySuperId.action";

    //box url
    public static final String BOX_BASE_URL = "https://classbox2.kechenggezi.com/";
    public static final String BOX_DEVICE = "https://classbox2.kechenggezi.com/api/v1/users/9f6d1228-fb42-46fc-a7c2-14f802c5e646/update_device?request_time=1533949691";

    private static Retrofit superRetrofit;
    private static Retrofit boxRetrofit;

    /**
     * 获取Retrofit对象
     *
     * @param context
     * @return
     */
    public static Retrofit getSuperRetrofit(final Context context) {
        if (superRetrofit != null) return superRetrofit;
        Context appContext=context.getApplicationContext();
        OkHttpClient builder = new OkHttpClient.Builder()
                .addInterceptor(new AddCookiesInterceptor(appContext))
                .addInterceptor(new ReceivedCookiesInterceptor(appContext))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        superRetrofit = new Retrofit.Builder()
                .client(builder)
                .baseUrl(SUPER_BASE_URL)
                .build();
        return superRetrofit;
    }

    /**
     * 获取Retrofit对象
     *
     * @param context
     * @return
     */
    public static Retrofit getBoxRetrofit(final Context context) {
        if (boxRetrofit != null) return boxRetrofit;
        Context appContext=context.getApplicationContext();
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient builder = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new AddTokenInterceptor(appContext))
                .addInterceptor(new AddCookiesInterceptor(appContext))
                .addInterceptor(new ReceivedCookiesInterceptor(appContext))
//                .addInterceptor(loggingInterceptor)
                .build();

        boxRetrofit = new Retrofit.Builder()
                .client(builder)
                .baseUrl(BOX_BASE_URL)
                .build();
        return boxRetrofit;
    }

    /**
     * 登录超表
     *
     * @param context
     * @param account  账号 明文
     * @param password 密码 明文
     * @param callback 回调
     */
    public static void loginForSuper(Context context, String account, String password,
                                     Callback<ResponseBody> callback) {
        SuperService service = getSuperRetrofit(context).create(SuperService.class);
        Call<ResponseBody> call = service.login(account, password,
                "1", "21",
                "vivo", "9.4.1",
                "vivo+X5Pro+D", "false","", "pinyou");
        call.enqueue(callback);
    }

    public static void getCourseForSuper(Context context, String beginYear, String term,
                                         Callback<ResponseBody> callback) {

        SuperService service = getSuperRetrofit(context).create(SuperService.class);
        Call<ResponseBody> call = service.getCourse(beginYear, term,
                "1", "21",
                "vivo", "9.4.1",
                "vivo+X5Pro+D");
        call.enqueue(callback);
    }

    public static void scanCodeForSuper(Context context, String superId, String beginYear, String term,
                                        Callback<ResponseBody> callback) {

        SuperService service = getSuperRetrofit(context).create(SuperService.class);
        Call<ResponseBody> call = service.scanCode(superId, beginYear, term,
                "1", "21",
                "vivo", "9.4.1",
                "vivo+X5Pro+D");
        call.enqueue(callback);
    }

    public static void loginForBox(Context context, String number, String password,
                                   Callback<ResponseBody> callback) {
        BoxService service = getBoxRetrofit(context).create(BoxService.class);
        BoxLoginParams params = new BoxLoginParams();
        BoxLoginParams.UserBean bean = new BoxLoginParams.UserBean();
        bean.setPassword(password);
        bean.setMobile_number(number);
        params.setUser(bean);
        params.setDevice(new BoxLoginParams.DeviceBean());

        String jsonStr = new Gson().toJson(params);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), jsonStr);
        Call<ResponseBody> call = service.login(System.currentTimeMillis() + "", body);
        call.enqueue(callback);
    }

    public static void getCourseForBox(Context context, String guid, Callback<ResponseBody> callback) {
        BoxService service = getBoxRetrofit(context).create(BoxService.class);

        Map<String,String> map=new HashMap<>();
        map.put("device[agent]","Mozilla/5.0%20(Linux%20Android%205.1.1%20OPPO%20A33%20Build/LMY47V%20wv)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Version/4.0%20Chrome/42.0.2311.138%20Mobile%20Safari/537.36%20classbox2/10.2.2");
        map.put("device[app_version]","10.2.2");
        map.put("device[brand]","OPPO");
        map.put("device[channel]","local");
        map.put("device[client_id]","d2a914e230d5339e7edff25d7609b6af");
        map.put("device[network]","wifi");
        map.put("device[platform]","android");
        map.put("device[unit_type]","OPPO%20A33");
        map.put("request_time",""+System.currentTimeMillis());

        Call<ResponseBody> call = service.getCourse(guid,map);
        call.enqueue(callback);
    }

    public static void scanCodeForBox(Context context, String from_guid, Callback<ResponseBody> callback) {
        BoxService service = getBoxRetrofit(context).create(BoxService.class);
        BoxScanParams params=new BoxScanParams();
        params.setFrom_guid(from_guid);
        String jsonStr = new Gson().toJson(params);

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), jsonStr);

        Call<ResponseBody> call = service.scanCode(from_guid,"1535086479",body);
        call.enqueue(callback);
    }
}
