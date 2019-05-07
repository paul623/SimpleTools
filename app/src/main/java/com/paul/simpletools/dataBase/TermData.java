package com.paul.simpletools.dataBase;

import org.litepal.crud.LitePalSupport;

/**
 * 作者:created by 巴塞罗那的余晖 on 2019/5/7 19：23
 * 邮箱:zhubaoluo@outlook.com
 * 不会写BUG的程序猿不是好程序猿，嘤嘤嘤
 */
public class TermData extends LitePalSupport {
    private String termName;

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }
}
