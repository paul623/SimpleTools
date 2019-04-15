package com.paul.simpletools.classbox.listener;

import com.paul.simpletools.classbox.model.SuperLesson;
import com.paul.simpletools.classbox.model.SuperProfile;

import java.util.List;

/**
 * 超表授权监听
 * Created by Liu ZhuangFei on 2018/8/21.
 */
public interface OnSuperAuthListener {
    /**
     * 登录成功
     * @param profile
     */
    void onLoginSuccess(SuperProfile profile);

    /**
     * 扫描成功
     * @param lessons
     */
    void onScanSuccess(List<SuperLesson> lessons);

    /**
     * 错误
     * @param msg
     */
    void onError(String msg);

    /**
     * 异常
     * @param t
     */
    void onException(Throwable t);

    void onComplete();
}
