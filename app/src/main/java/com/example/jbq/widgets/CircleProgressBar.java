package com.example.jbq.widgets;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.view.View;

public class CircleProgressBar extends View {
    private int progress = 0;
    private int maxProgress = 100;

    private Paint pathPaint;
    private Paint fillPaint;

    private RectF oval;

    private int[] arcColors = {0xFF02C016,0xFF3DF346,0xFF40F1d5,0xFF02C016};
    //背景灰色
    private int pathColor = 0xFFF0EEDF;
    //边框灰色
    private int borderColor = 0xFFD2D1C4;
    private int pathWidth = 35;
    private int width;
    private int height;
    //圆半径
    private int radius = 120;

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public int getPathWidth() {
        return pathWidth;
    }

    public void setPathWidth(int pathWidth) {
        this.pathWidth = pathWidth;
    }

    public int getPathColor() {
        return pathColor;
    }

    public void setPathColor(int pathColor) {
        this.pathColor = pathColor;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    //梯度渲染
    private SweepGradient sweepGradient;
    private boolean reset = false;

    public CircleProgressBar(Context context) {
        super(context);
        //初始化绘制
        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setDither(true);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);

        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.STROKE);
        fillPaint.setDither(true);
        fillPaint.setStrokeJoin(Paint.Join.ROUND);
        oval = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
