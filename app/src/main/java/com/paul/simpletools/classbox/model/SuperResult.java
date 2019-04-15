package com.paul.simpletools.classbox.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liu ZhuangFei on 2018/9/5.
 */
public class SuperResult implements Serializable{
    private List<SuperLesson> lessons;
    private String errMsg="未知错误";
    private boolean isSuccess=false;
    private SuperProfile profile;

    public void setProfile(SuperProfile profile) {
        this.profile = profile;
    }

    public SuperProfile getProfile() {
        return profile;
    }

    public SuperResult() {
        lessons=new ArrayList<>();
    }

    public List<SuperLesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<SuperLesson> lessons) {
        this.lessons = lessons;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
