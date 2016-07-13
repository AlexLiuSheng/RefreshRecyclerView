package com.allenliu.refreshrecyclerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Allen Liu on 2016/7/11.
 */
public class DefaultHeaderView extends View {
    private float A;
    private float W;
    private float B;
    private float H;
    //周期
    private float T;
    private float[]ys;
    private Paint linePaint;
    //y=Asin(wx+b)+h;
    public DefaultHeaderView(Context context) {
        super(context);
        init();
    }

    public DefaultHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DefaultHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DefaultHeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        linePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(getResources().getColor(R.color.colorPrimary));
        linePaint.setStrokeWidth(2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        W= (float) (Math.PI*2*1/getMeasuredWidth());
        ys=new float[getMeasuredWidth()];
        changeB(0);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWave(canvas);

    }
    private void changeB(float b){
        B=b;
        for (int i = 0; i < getMeasuredWidth(); i++) {
            ys[i]= (float) (20*Math.sin(W*i+B)+getMeasuredHeight()*2/3);
        }
        invalidate();
    }
    private void drawWave(Canvas canvas){
        for (int i = 0; i < ys.length; i++) {
            canvas.drawLine(i,getMeasuredHeight()-ys[i],i,getMeasuredHeight(),linePaint);
            if(i==ys.length-1) {
                B=B+0.4f;
                changeB(B);
            }
        }
    }
}
