package com.nuaakx.istest2;


import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.content.Context;
/**
 * Created by viczsx on 2018/4/30.
 */

public class lsmap extends View {
    //定义相关变量,依次是妹子显示位置的X,Y坐标
    public float bitmapX;
    public float bitmapY;
    public lsmap(Context context) {
        super(context);
        //设置妹子的起始坐标
        bitmapX = 0;
        bitmapY = 0;
    }

    //重写View类的onDraw()方法
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //创建,并且实例化Paint的对象
        Paint paint = new Paint();

        //绘制圆
        canvas.drawCircle(bitmapX,bitmapY,10,paint);
        //判断图片是否回收,木有回收的话强制收回图片

    }

}
