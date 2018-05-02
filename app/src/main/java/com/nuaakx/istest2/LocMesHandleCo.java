package com.nuaakx.istest2;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by viczsx on 2018/4/30.
 */

public class LocMesHandleCo {
    private static List<Handler> handlers = new ArrayList<Handler>();
    public  static void addhandle(Handler tem){
        handlers.add(tem);
    }

    public static void removehandle(Handler tem){
        handlers.remove(tem);
    }

    public static int getsize(){
        return handlers.size();
    }

    public static void sendmes(Message msg){

        for (Handler handler:handlers) {
            Message msg1 = new Message();
            msg1.copyFrom(msg);
            handler.sendMessage(msg1);
        }
    }

    public static void sendemptymes(int what){
        for (Handler handler:handlers) {
            handler.sendEmptyMessage(what);
        }
    }
}
