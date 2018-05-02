package com.nuaakx.istest2;

import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class locmap extends AppCompatActivity {

    private lsmap map;
    final Handler handler = new Handler()
    {
        @Override
        //重写handleMessage方法,根据msg中what的值判断是否执行后续操作
        public void handleMessage(Message msg)
        {
            switch (msg.what) {
                case 0x3: {
                    String json = msg.getData().getString("inf");
                    try {
                        JSONObject jsonObj = new JSONObject(json);
                        if (jsonObj.has("ip")) {
                            String id = jsonObj.optString("id");
                            String temip = jsonObj.optString("ip");
                            int temport = jsonObj.optInt("port");
                            int x = jsonObj.optInt("x");
                            int y = jsonObj.optInt("y");
                            map.bitmapX = x * 3;
                            map.bitmapY = y * 3;
                            map.invalidate();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(locmap.this, e.getMessage()
                                , Toast.LENGTH_LONG).show();
                    }
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
        setContentView(R.layout.activity_locmap);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        FrameLayout fram = (FrameLayout)findViewById(R.id.map_all);
        map = new lsmap(locmap.this);
//        map.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                map.bitmapX = event.getX() ;
//                map.bitmapY = event.getY();
//                map.invalidate();
//                return true;
//            }
//        });
        fram.addView(map);
        LocMesHandleCo.addhandle(handler);
        ActivityCollector.addActivity(this);

    }

    @Override
    protected void onDestroy(){

        ActivityCollector.removeActivity(this);
        LocMesHandleCo.removehandle(handler);
        super.onDestroy();
    }

}
