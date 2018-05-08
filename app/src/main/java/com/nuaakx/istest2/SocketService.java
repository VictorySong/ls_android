package com.nuaakx.istest2;


import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

import static android.content.ContentValues.TAG;

/**
 * Created by viczsx on 2018/5/2.
 */

public class SocketService extends IntentService {
    private final String TAG = "hehe";
    private final MyBinder binder = new MyBinder();
    private LinkedList<SatelliteInf> mData;
    private String ip;          //中控ip
    private int port;           //中控监听端口
    private String tcpmes;
    private JSONObject tcpjson;
    private boolean stop = false;
    private LocalBroadcastManager localBroadcastManager;
    public SocketService(){
        super("SocketService");
    }


    public class MyBinder extends Binder
    {
        public LinkedList<SatelliteInf> get_mData(){
            return mData;
        }
        public String get_tcpmess(){
            return tcpmes;
        }
        public JSONObject get_tcpjson(){
            return tcpjson;
        }
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Log.i(TAG, "工作线程");
    }

    @Override
    public IBinder onBind(Intent intent){
        super.onBind(intent);
        Log.i(TAG,"绑定");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "解绑!");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate(){
        Log.i(TAG, "oncreate被调用!");
        super.onCreate();

        localBroadcastManager = LocalBroadcastManager.getInstance(this.getBaseContext());
        mData = new LinkedList<SatelliteInf>();
        new Thread(){
            public void run(){
                while(!stop) {
                    try {
                        // 1.创建服务器端DatagramSocket，指定端口
                        DatagramSocket socket = new DatagramSocket(4567);
                        // 2.创建数据报，用于接收客户端发送的数据
                        byte[] data = new byte[1024];// 创建字节数组，指定接收的数据包的大小
                        DatagramPacket packet = new DatagramPacket(data, data.length);
                        Log.i(TAG,"udp监听中");
                        // 3.接收客户端发送的数据
                        socket.receive(packet);// 此方法在接收到数据报之前会一直阻塞
                        // 4.读取数据
                        String info = new String(data, 0, packet.getLength());
                        try {

                            while (!stop) {
                                JSONObject jsonObj = new JSONObject(info);
                                if (jsonObj.has("ip")) {
                                    ip = jsonObj.optString("ip");
                                    port = jsonObj.optInt("port");

                                    Log.i(TAG,"收到服务端udp消息");
                                    //1.创建客户端Socket，指定服务器地址和端口
                                    Socket tcpsocket = new Socket(ip, port);
                                    //3.连接后获取输入流，读取客户端信息
                                    Reader read = new InputStreamReader(tcpsocket.getInputStream());

                                    //发送身份信息
                                    Writer writer = new OutputStreamWriter(tcpsocket.getOutputStream());
                                    writer.write("phone");
                                    writer.flush();

                                    char chars[] = new char[1024];
                                    int len;
                                    while ((len = read.read(chars)) != -1 && !stop) {
                                        tcpmes = new String(chars, 0, len);
                                        tcpjson = new JSONObject(tcpmes);

                                        if(tcpjson.has("ip")){
                                            String id = tcpjson.optString("id");
                                            //查找已有列表中是否存在该ip和端口
                                            boolean t = false;
                                            int i = 0;
                                            for(i = 0;i<mData.size();i++){
                                                if(mData.get(i).getid().compareTo(id) == 0) {
                                                    t = true;
                                                    break;
                                                }
                                            }
                                            int x = tcpjson.optInt("x");
                                            int y = tcpjson.optInt("y");
                                            if(t){
                                                mData.get(i).setx((float) x);
                                                mData.get(i).sety((float) y);
                                            }else{
                                                int r = tcpjson.optInt("R");
                                                int g = tcpjson.optInt("G");
                                                int b = tcpjson.optInt("B");
                                                mData.add(0,new SatelliteInf(id,(float) x,(float) y,r,g,b,1));
                                            }
                                        }

                                        if(tcpjson.has("status")){
                                            int status = tcpjson.optInt("status");
                                            String id = tcpjson.optString("id");
                                            if(status==0){
                                                int i;
                                                for(i = 0;i<mData.size();i++){
                                                    if(mData.get(i).getid().compareTo(id) == 0) {
                                                        SatelliteInf tem = new SatelliteInf(mData.get(i));
                                                        tem.setStatus(0);
                                                        mData.remove(i);
                                                        mData.add(tem);
                                                        break;
                                                    }
                                                }
                                            }else{
                                                int i;
                                                for(i = 0;i<mData.size();i++){
                                                    if(mData.get(i).getid().compareTo(id) == 0) {
                                                        SatelliteInf tem = new SatelliteInf(mData.get(i));
                                                        tem.setStatus(1);
                                                        mData.remove(i);
                                                        mData.add(0,tem);
                                                        break;
                                                    }
                                                }
                                            }
//                                            localBroadcastManager.sendBroadcast(new Intent("com.nuaakx.istest2.SatelliteConnectStatus"));
                                        }
                                        Log.i(TAG,"收到服务端tcp信息");
                                        localBroadcastManager.sendBroadcast(new Intent("com.nuaakx.istest2.SatelliteInfChange"));
                                    }
                                    tcpsocket.close();
                                } else {
                                    socket.receive(packet);// 此方法在接收到数据报之前会一直阻塞
                                    // 4.读取数据
                                    info = new String(data, 0, packet.getLength());
                                }
                            }
                            socket.close();
                        } catch (JSONException e) {
                            Log.i(TAG,e.getMessage());
                        }
                    } catch (IOException e) {
                        Log.i(TAG,e.getMessage());
                    }
                }
            }
        }.start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onstartcommand被调用!");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "onRebind方法被调用!");
        super.onRebind(intent);
    }

    @Override
    public void setIntentRedelivery(boolean enabled) {
        super.setIntentRedelivery(enabled);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"服务结束");
        stop = true;
        super.onDestroy();
    }
}
