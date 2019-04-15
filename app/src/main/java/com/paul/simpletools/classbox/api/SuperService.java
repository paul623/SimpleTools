package com.paul.simpletools.classbox.api;

import com.paul.simpletools.classbox.SuperBoxRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Liu ZhuangFei on 2018/8/9.
 */

public interface SuperService {

    @POST(SuperBoxRequest.SUPER_LOGIN)
    @FormUrlEncoded
    public Call<ResponseBody> login(@Field("account") String account,
                                    @Field("password") String password,
                                    @Field("platform") String platform,
                                    @Field("phoneVersion") String phoneVersion,
                                    @Field("phoneBrand") String phoneBrand,
                                    @Field("versionNumber") String versionNumber,
                                    @Field("phoneModel") String phoneModel,
                                    @Field("updateInfo") String updateInfo,
                                    @Field("deviceCode") String deviceCode,
                                    @Field("channel") String channel);

    @POST(SuperBoxRequest.SUPER_GET)
    @FormUrlEncoded
    public Call<ResponseBody> getCourse(@Field("beginYear") String beginYear,
                                        @Field("term") String term,
                                        @Field("platform") String platform,
                                        @Field("phoneVersion") String phoneVersion,
                                        @Field("phoneBrand") String phoneBrand,
                                        @Field("versionNumber") String versionNumber,
                                        @Field("phoneModel") String phoneModel);

    @POST(SuperBoxRequest.SUPER_SCAN)
    @FormUrlEncoded
    public Call<ResponseBody> scanCode(@Field("superId") String superId,
                                       @Field("beginYear") String beginYear,
                                       @Field("term") String term,
                                        @Field("platform") String platform,
                                        @Field("phoneVersion") String phoneVersion,
                                        @Field("phoneBrand") String phoneBrand,
                                        @Field("versionNumber") String versionNumber,
                                        @Field("phoneModel") String phoneModel);
}
