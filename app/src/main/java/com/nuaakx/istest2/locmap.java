package com.nuaakx.istest2;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

public class locmap extends AppCompatActivity {

    private lsmap map;
    private String id;
    private Context mcontext;
    private FrameLayout fram;
    public SocketService.MyBinder binder;
    int WindowWidth ;
    int WindowHeight;
    float scale;
    int Width = 200;
    private SInfChangeBcReceiver localbcReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;
    private ServiceConnection conn = new ServiceConnection() {
        //Activity与Service断开连接时回调该方法
        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("------Service DisConnected-------");
        }
        //Activity与Service连接成功时回调该方法
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("------Service Connected-------");
            binder = (SocketService.MyBinder) service;
            if(id.compareTo("ALL") == 0) {
                LinkedList<SatelliteInf> t_mdata = binder.get_mData();
                for (int i = 0; i < t_mdata.size(); i++) {
                    t_mdata.get(i).setLsmap(new lsmap(mcontext));
                    map = t_mdata.get(i).getLsmap();
                    map.bitmapX = scale * t_mdata.get(i).getx();
                    map.bitmapY = scale * t_mdata.get(i).gety();
                    map.rr = t_mdata.get(i).getr();
                    map.gg = t_mdata.get(i).getg();
                    map.bb = t_mdata.get(i).getb();
                    fram.addView(map);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locmap);
        mcontext = this;
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        WindowWidth = wm.getDefaultDisplay().getWidth();
        WindowHeight = wm.getDefaultDisplay().getHeight();
        scale = WindowWidth/Width;
        //获取启动数据，是单个还是所有
        Intent tem = getIntent();
        Bundle bd = tem.getExtras();
        id = bd.getString("id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        fram = (FrameLayout)findViewById(R.id.map_all);
        if(id.compareTo("ALL")!=0){
            setTitle(id+"位置图");
            map = new lsmap(mcontext);
            map.bitmapX = bd.getFloat("x")*scale;
            map.bitmapY = bd.getFloat("y")*scale;
            map.rr = bd.getInt("R");
            map.gg = bd.getInt("G");
            map.bb = bd.getInt("B");
            fram.addView(map);
        }else{
            setTitle("所有卫星位置图");
        }
        //初始化广播接收者，设置过滤器
        localBroadcastManager = LocalBroadcastManager.getInstance(this.getBaseContext());
        localbcReceiver = new SInfChangeBcReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.nuaakx.istest2.SatelliteInfChange");
        localBroadcastManager.registerReceiver(localbcReceiver,intentFilter);
        //绑定服务
        final Intent it = new Intent();
        it.setAction("com.nuaakx.istest2.SocketService");
        it.setPackage("com.nuaakx.istest2");
        bindService(it, conn, Service.BIND_AUTO_CREATE);

        ActivityCollector.addActivity(this);

    }

    @Override
    protected void onDestroy(){

        ActivityCollector.removeActivity(this);
        //解除绑定service
        unbindService(conn);
        //取消注册广播
        localBroadcastManager.unregisterReceiver(localbcReceiver);
        super.onDestroy();
    }

    private class SInfChangeBcReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if(binder == null)
                return;
            JSONObject tem = binder.get_tcpjson();
            if(id.compareTo("ALL") != 0) {
                if (tem.optString("id").compareTo(id) == 0) {
                    map.bitmapX = (float) scale * tem.optInt("x");
                    map.bitmapY = (float) scale * tem.optInt("y");
                    map.invalidate();
                }
            }else {
                LinkedList<SatelliteInf> t_mdata = binder.get_mData();
                for(int i = 0;i<t_mdata.size(); i++){
                    if(null == t_mdata.get(i).getLsmap()) {
                        t_mdata.get(i).setLsmap(new lsmap(mcontext));
                        fram.addView(t_mdata.get(i).getLsmap());
                    }
                    map = t_mdata.get(i).getLsmap();
                    map.bitmapX =  scale * t_mdata.get(i).getx();
                    map.bitmapY = scale * t_mdata.get(i).gety();
                    map.invalidate();
                }
            }
        }
    }

}
