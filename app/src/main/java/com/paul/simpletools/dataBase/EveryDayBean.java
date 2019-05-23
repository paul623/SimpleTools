package com.paul.simpletools.dataBase;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/4/20 10：33
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class EveryDayBean  extends LitePalSupport {
    private String content;
    private String auther;
    private String date;
    public EveryDayBean(String content,String auther,String date)
    {
        this.content=content;
        this.auther=auther;
        this.date=date;
    }
    public String getAuther() {
        return auther;
    }

    public void setAuther(String auther) {
        this.auther = auther;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
