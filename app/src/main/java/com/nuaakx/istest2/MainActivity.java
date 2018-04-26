package com.nuaakx.istest2;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private udpserver udp;      //寻找中控IP和端口
    private String ip;          //中控ip
    private int port;           //中控监听端口
    private tcpclient tcp;      //与中控tcp连接

    //列表
    private ArrayList<Group> gData = null;
    private ArrayList<ArrayList<Item>> iData = null;
    private ArrayList<Item> lData = null;
    private Context mContext;
    private ExpandableListView exlist_lol;
    private MyBaseExpandableListAdapter myAdapter = null;


    final Handler mainHandler = new Handler()
    {
        @Override
        //重写handleMessage方法,根据msg中what的值判断是否执行后续操作
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case 0x123: {
                    //获取udp信息
                    String json = msg.getData().getString("udp");
                    try {
                        JSONObject jsonObj = new JSONObject(json);
                        if (jsonObj.has("ip")) {
                            ip = jsonObj.optString("ip");
                            port = jsonObj.optInt("port");
                            String text = "中控IP：" + ip + "端口：" + port;
                            TextView tem = (TextView) findViewById(R.id.server);
                            tem.setText(text);
                            tcp.setip(ip);
                            tcp.setport(port);
                            tcp.start();

                        } else {
                            udp.start();
                        }
                    } catch (JSONException e) {
                        udp.start();
                    }
                    break;
                }
                case 0x1: {
                    //udp信息获取失败
                    Toast.makeText(MainActivity.this, msg.getData().getString("mis")
                            , Toast.LENGTH_LONG).show();
                    break;
                }
                case 0x2: {
                    //tcp出错
                    Toast.makeText(MainActivity.this, msg.getData().getString("inf")
                            , Toast.LENGTH_LONG).show();
                    break;
                }
                case 0x3: {
                    String json = msg.getData().getString("inf");
                    try{
                        JSONObject jsonObj = new JSONObject(json);
                        if(jsonObj.has("ip")){
                            String id = jsonObj.optString("id");
                            String temip = jsonObj.optString("ip");
                            int temport = jsonObj.optInt("port");
                            //查找已有列表中是否存在该ip和端口
                            boolean t = false;
                            int i = 0;
                            for(i = 0;i<gData.size();i++){
                                if(gData.get(i).getgName().compareTo(id) == 0) {
                                    t = true;
                                    break;
                                }
                            }

                            int x = jsonObj.optInt("x");
                            int y = jsonObj.optInt("y");
                            if(t){

                                iData.get(i).get(0).setiName("x:"+x);
                                iData.get(i).get(0).setiName1("y:"+y);
                                myAdapter.notifyDataSetChanged();
                            }else{
                                gData.add(new Group(id));
                                lData = new ArrayList<Item>();
                                lData.add(new Item("x:"+x,"y:"+y));
                                iData.add(lData);
                                myAdapter.notifyDataSetChanged();
//                                exlist_lol.setAdapter(myAdapter);
                            }

                        }
                    }catch (JSONException e){
                        Toast.makeText(MainActivity.this, e.getMessage()
                                , Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                case 0x4: {
                    Toast.makeText(MainActivity.this, "与中控断开"
                            , Toast.LENGTH_LONG).show();
                    TextView tem = (TextView) findViewById(R.id.server);
                    tem.setText("查询中控的ip地址及监听端口...");

                    udp = new udpserver(mainHandler);   //重新实例化
                    tcp = new tcpclient(mainHandler);
                    udp.start();
                    break;
                }
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        udp = new udpserver(mainHandler);
        udp.start();
        tcp = new tcpclient(mainHandler);

        mContext = MainActivity.this;
        exlist_lol = (ExpandableListView) findViewById(R.id.exlist_lol);


        //数据准备
        gData = new ArrayList<Group>();
        iData = new ArrayList<ArrayList<Item>>();
        myAdapter = new MyBaseExpandableListAdapter(gData,iData,mContext);
        exlist_lol.setAdapter(myAdapter);

    }

    //获取中控tcp监听端口
    public class udpserver extends Thread {
        private Handler h;
        public udpserver(Handler tem){
            h = tem;
        }

        @Override
        public void run() {
            try{
                udpget();
            }catch (IOException e){
                Message msg = new Message();
                msg.what = 0x1;
                Bundle tem2 = new Bundle();
                tem2.putString("mis",e.getMessage());
                msg.setData(tem2);
                h.sendMessage(msg);
            }
        }

        public void udpget() throws IOException{
            // 1.创建服务器端DatagramSocket，指定端口
            DatagramSocket socket = new DatagramSocket(4567);
            // 2.创建数据报，用于接收客户端发送的数据
            byte[] data = new byte[1024];// 创建字节数组，指定接收的数据包的大小
            DatagramPacket packet = new DatagramPacket(data, data.length);
            // 3.接收客户端发送的数据
            socket.receive(packet);// 此方法在接收到数据报之前会一直阻塞
            // 4.读取数据
            String info = new String(data, 0, packet.getLength());
            socket.close();
            Message msg = new Message();
            msg.what = 0x123;
            Bundle tem = new Bundle();
            tem.putString("udp",info);
            msg.setData(tem);
            h.sendMessage(msg);
        }
    }

    //发起连接并接收来自中控数据
    public class tcpclient extends Thread{
        private Handler h;
        private String ip;          //中控ip
        private int port;           //中控端口
        public tcpclient(Handler tem){
            h = tem;
        }

        @Override
        public void run(){
            try{
                tcpconnect();
            }catch (IOException e){
                Message msg = new Message();
                msg.what = 0x2;
                Bundle tem = new Bundle();
                tem.putString("inf",e.getMessage());
                msg.setData(tem);
                h.sendMessage(msg);
            }
        }
        //设置ip
        public void setip(String tem){
            ip = tem;
        }
        //设置端口
        public void setport(int tem){
            port = tem;
        }
        public void tcpconnect() throws IOException{
            //1.创建客户端Socket，指定服务器地址和端口
            Socket socket = new Socket(ip,port);
            //发送连接成功的信号
            Message msg = new Message();
            msg.what = 0x2;
            Bundle tem = new Bundle();
            tem.putString("inf","连接成功");
            msg.setData(tem);
            h.sendMessage(msg);

            //3.连接后获取输入流，读取客户端信息
            Reader read=new InputStreamReader(socket.getInputStream());

            //发送身份信息
            Writer writer = new OutputStreamWriter(socket.getOutputStream());
            writer.write("phone");
            writer.flush();

            char chars[] = new char[1024];
            int len;
            while((len=read.read(chars)) != -1) {
                String sb = new String(chars, 0, len);
                Message msg1 = new Message();
                msg1.what = 0x3;
                Bundle tem1 = new Bundle();
                tem1.putString("inf", sb);
                msg1.setData(tem1);
                h.sendMessage(msg1);
            }
            //发送中控断开信号
            h.sendEmptyMessage(0x4);
            socket.close();
        }
    }

}
