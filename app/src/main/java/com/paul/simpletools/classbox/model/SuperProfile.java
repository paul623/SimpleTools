package com.paul.simpletools.classbox.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 超表个人信息：我只筛选了一些可能用得到的
 * Created by Liu ZhuangFei on 2018/8/10.
 */

public class SuperProfile implements Serializable{
    private boolean isSuccess=true;
    private String errMsg;
    private String academyName;
    private int beginYear;
    private int grade;
    private String nickName;
    private String schoolName;
    private int term;
    private List<SuperTerm> termList;

    public void setTermList(List<SuperTerm> termList) {
        this.termList = termList;
    }

    public List<SuperTerm> getTermList() {
        return termList;
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

    public String getErrMsg() {
        return errMsg;
    }

    public String getAcademyName() {
        return academyName;
    }

    public void setAcademyName(String academyName) {
        this.academyName = academyName;
    }

    public int getBeginYear() {
        return beginYear;
    }

    public void setBeginYear(int beginYear) {
        this.beginYear = beginYear;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }
}
