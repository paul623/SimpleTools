package com.paul.simpletools.classbox.model;

import java.io.Serializable;

/**
 * Created by Liu ZhuangFei on 2018/9/7.
 */
public class SuperTerm implements Serializable{
    private int beginYear;
    private int term;


    public int getBeginYear() {
        return beginYear;
    }

    public void setBeginYear(int beginYear) {
        this.beginYear = beginYear;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }
}
