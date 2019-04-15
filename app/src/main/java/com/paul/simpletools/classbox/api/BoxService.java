package com.paul.simpletools.classbox.api;

import com.paul.simpletools.classbox.SuperBoxRequest;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Liu ZhuangFei on 2018/8/9.
 */

public interface BoxService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("api/v1/login")
    public Call<ResponseBody> login(@Query("request_time") String time, @Body RequestBody body);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("api/v1/users/{guid}/sync_all")
    public Call<ResponseBody> getCourse(@Path("guid") String guid,
                                        @QueryMap Map<String, String> params);

    @POST("api/v1/calendars/{calendar_guid}/copy")
    public abstract Call<ResponseBody> scanCode(
            @Path("calendar_guid") String paramString,
            @Query("request_time") String reqTime,
            @Body RequestBody paramRequestBody);
}
