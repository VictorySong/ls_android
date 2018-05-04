package com.nuaakx.istest2;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Context mContext;

    private ListView s_list = null;
    private LinkedList<SatelliteInf> mData = null;
    private SatelliteInfAdapter slistAdapter = null;

    public SocketService.MyBinder binder;
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
            mData = binder.get_mData();
            Log.i("hh",""+mData.size());
            slistAdapter = new SatelliteInfAdapter(mData,mContext);
            s_list.setAdapter(slistAdapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = MainActivity.this;
        ActivityCollector.addActivity(this);            //加入到活动activity 管理
        this.setTitle("卫星监测中..");
        s_list = (ListView)findViewById(R.id.ss_list);

        localBroadcastManager = LocalBroadcastManager.getInstance(this.getBaseContext());
        //初始化广播接收者，设置过滤器
        localbcReceiver = new SInfChangeBcReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.nuaakx.istest2.SatelliteInfChange");
        localBroadcastManager.registerReceiver(localbcReceiver,intentFilter);

        final Intent it = new Intent();
        it.setAction("com.nuaakx.istest2.SocketService");
        it.setPackage("com.nuaakx.istest2");
        bindService(it, conn, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        unbindService(conn);
        localBroadcastManager.unregisterReceiver(localbcReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainmenu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.ls_map_view) {
            Intent tem = new Intent(this,locmap.class);
            Bundle bd = new Bundle();
            bd.putString("id","ALL");
            tem.putExtras(bd);
            startActivity(tem);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SInfChangeBcReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            slistAdapter.notifyDataSetChanged();
        }
    }

}
