package com.example.jbq.service;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.example.jbq.bean.PedometerBean;

/**
 * 数据过滤和采样
 */
public class PedometerListener implements SensorEventListener {
    //当前步数
    private int currentSteps = 0;

    public void setCurrentSteps(int currentSteps) {
        this.currentSteps = currentSteps;
    }

    //灵敏度
    private float sensitivity = 30;
    //采样时间
    private long mLimit = 300;
    //最后保存的数值
    private float mLastValue;
    //放大
    private float mScale = -4.0f;
    //偏移
    private float offset = 240;
    //采样时间
    private long start = 0;
    private long end = 0;
    //加速度方向
    private float mLastDirection;
    //记录数值
    private float mLastExtremes[][] = new float[2][1];
    //最后一次变化量
    private float mLastDiff;
    //是否匹配
    private int mLastMatch;
    //取样数量
    private final static int AVG_NUM = 3;

    private PedometerBean data;

    public PedometerListener(PedometerBean data) {
        this.data = data;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.w("FFFF", "onSensorChanged: FUCKFFFFFFFFFFFFFFFFFFFfffffffffffffffffffffffffffffffffffFFFFFFFFFFFFFFFFFFFFFFFFF");
        Sensor sensor = event.sensor;
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float sum = 0;
                for (int i = 0; i < AVG_NUM; i++) {
                    float vector = offset + event.values[i] * mScale;
                    sum += vector;
                }
                float avg = sum / AVG_NUM;
                //判断方向
                float dir = Float.compare(avg, mLastValue);

                //反向
                if (dir == -mLastDirection) {
                    int extType = (dir > 0 ? 0 : 1);
                    mLastExtremes[extType][0] = mLastValue;
                    float diff = Math.abs(mLastExtremes[extType][0] = mLastExtremes[1 - extType][0]);
                    //大于灵敏度认为有效
                    if (diff > sensitivity) {
                        //是否足够大
                        boolean isLargeAsPrevious = diff > (mLastDiff * 2 / 3);
                        //不能太大
                        boolean isPreviousLargeEnough = mLastDiff > (diff / 3);
                        //方向判断
                        boolean isNotContra = (mLastMatch != 1 - extType);
                        if (isLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                            //一次有效数据
                            end = System.currentTimeMillis();
                            if (end - start > mLimit) {
                                currentSteps++;
                                mLastMatch = extType;
                                start = end;
                                mLastDiff = diff;
                                if (data != null) {
                                    data.setStepCount(currentSteps);
                                    data.setLastStepTime(System.currentTimeMillis());
                                }
                            } else {
                                mLastDiff = sensitivity;

                            }
                        } else {
                            mLastMatch = -1;
                            mLastDiff = sensitivity;
                        }
                    }
                }
                mLastDirection = dir;
                mLastValue = avg;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
