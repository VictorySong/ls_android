package com.nuaakx.istest2;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by viczsx on 2018/5/3.
 */

public class SatelliteInfAdapter extends BaseAdapter {
    private LinkedList<SatelliteInf> mData;
    private Context mContext;

    public SatelliteInfAdapter(LinkedList<SatelliteInf> mData, Context mContext) {
        this.mData = mData;
        Log.i("hh","SatelliteInfAdapter 初始化");
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_satelliteinf,parent,false);
        TextView t_id = (TextView) convertView.findViewById(R.id.s_id);
        TextView t_x = (TextView) convertView.findViewById(R.id.s_x);
        TextView t_y = (TextView) convertView.findViewById(R.id.s_y);
        t_id.setText(mData.get(position).getid());
        t_x.setText("x:"+mData.get(position).getx());
        t_y.setText("y:"+mData.get(position).gety());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tem = new Intent(mContext,locmap.class);
                Bundle bd = new Bundle();
                bd.putFloat("x",mData.get(position).getx());
                bd.putFloat("y",mData.get(position).gety());
                bd.putString("id",mData.get(position).getid());
                tem.putExtras(bd);
                mContext.startActivity(tem);
                Log.i("view","点击");
            }
        });
        return convertView;
    }
}
