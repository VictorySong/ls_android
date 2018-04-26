package com.nuaakx.istest2;

import android.content.ClipData;

/**
 * Created by Victory2 on 2018/4/7.
 */

public class Item {

    private String iName;
    private String iName1;

    public Item() {
    }

    public Item(String iName,String iName1){
        this.iName = iName;
        this.iName1 = iName1;
    }

    public String getiName1() {
        return iName1;
    }

    public String getiName() {
        return iName;
    }

    public void setiName1(String tem) {
        this.iName1 = tem;
    }

    public void setiName(String iName) {
        this.iName = iName;
    }
}