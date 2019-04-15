package com.paul.simpletools.classbox.listener;

import com.paul.simpletools.classbox.model.SuperLesson;
import com.paul.simpletools.classbox.model.SuperProfile;

import java.util.List;

/**
 * Created by Liu ZhuangFei on 2018/8/21.
 */
public class OnSuperAuthAdapter implements OnSuperAuthListener {
    @Override
    public void onLoginSuccess(SuperProfile profile) {

    }

    @Override
    public void onScanSuccess(List<SuperLesson> lessons) {

    }

    @Override
    public void onError(String msg) {

    }

    @Override
    public void onException(Throwable t) {

    }

    @Override
    public void onComplete() {

    }
}
