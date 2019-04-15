package com.paul.simpletools.dataBase;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/4/15 23：28
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class MessageEvent {
    private Boolean show_nweek_lesson;
    private Boolean show_weekend;
    private Boolean minnumber;
    private Boolean show_times;
    private String background;

    public MessageEvent(Boolean show_nweek_lesson,Boolean show_weekend,Boolean minnumber,Boolean show_times,String background)
    {
        this.show_nweek_lesson=show_nweek_lesson;
        this.show_times=show_times;
        this.minnumber=minnumber;
        this.show_weekend=show_weekend;
        this.background=background;
    }
    public MessageEvent()
    {
        show_weekend=show_nweek_lesson=minnumber=show_times=false;
        background="";
    }
    public void setShow_nweek_lesson(Boolean show_nweek_lesson)
    {
        this.show_nweek_lesson=show_nweek_lesson;
    }
    public Boolean getShow_nweek_lesson()
    {
        return show_nweek_lesson;
    }
    public void setMinnumber(Boolean minnumber)
    {
        this.minnumber=minnumber;
    }
    public Boolean getMinnumber()
    {
        return minnumber;
    }
    public void setShow_weekend(Boolean show_weekend)
    {
        this.show_weekend=show_weekend;
    }
    public Boolean getShow_weekend()
    {
        return show_weekend;
    }
    public void setShow_times(Boolean show_times)
    {
        this.show_times=show_times;
    }
    public Boolean getShow_times()
    {
        return show_times;
    }
    public String getBackground()
    {
        return background;
    }
    public void setBackground(String background)
    {
        this.background=background;
    }
}
