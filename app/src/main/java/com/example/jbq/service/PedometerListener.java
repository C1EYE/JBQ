package com.example.jbq.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.jbq.bean.PedometerBean;
import com.example.jbq.util.LogWriter;

/**
 * 数据过滤和采样
 */
public class PedometerListener implements SensorEventListener {
    private static final String TAG = "PedometerListener";
    public static int CURRENT_SETP = 0;
    public static float SENSITIVITY = 1; // SENSITIVITY灵敏度
    private static long mLimit = 300;//采样时间
    private float mLastValues[] = new float[3 * 2];//最后保存的数据
    private float mScale[] = new float[2];

    private float mYOffset;
    private static long end = 0;

    public static long getmLimit() {
        return mLimit;
    }

    public static float getSENSITIVITY() {
        return SENSITIVITY;
    }

    private static long start = 0;

    public static void setmLimit(long mLimit) {
        PedometerListener.mLimit = mLimit;
    }

    /**
     * 最后加速度方向
     */
    private float mLastDirections[] = new float[3 * 2];
    private float mLastExtremes[][] = {new float[3 * 2], new float[3 * 2]};
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;
    PedometerBean pedmoeterBean;
    public void resetCurrentStep() {
        CURRENT_SETP = 0;
    }

    /**
     * 传入上下文的构造函数
     *
     * @param context
     */
    public PedometerListener(Context context, PedometerBean pedmoeterBean) {
        super();
        int h = 480;
        mYOffset = h * 0.5f;//240
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
        this.pedmoeterBean = pedmoeterBean;
    }

    public static void setSENSITIVITY(float SENSITIVITY) {
        PedometerListener.SENSITIVITY = SENSITIVITY;
    }

    //当传感器检测到的数值发生变化时就会调用这个方法
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
//        Log.i(TAG, "onSensorChanged: -1");
        synchronized (this) {

            Log.i(TAG, "onSensorChanged: 0");
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER)//加速传感器
            {
//                Log.i(TAG, "onSensorChanged: 1");
                float vSum = 0;
                for (int i = 0; i < 3; i++) {
//                    final float v = mYOffset + event.values[i] * mScale[0];
                    float v = 240.0f + event.values[i] * 10.0f;
                    vSum += v;
                }
                int k = 0;
                float v = vSum / 3.0f;//记录三个轴向,传感器的平均值
                Log.i(TAG, "onSensorChanged: v"+v);

                float direction = (Float.compare(v, mLastValues[k]));
                if (direction == -mLastDirections[k]) {
//                    Log.i(TAG, "onSensorChanged: 2");
                    // Direction changed
                    int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
                    mLastExtremes[extType][k] = mLastValues[k];
                    float diff = Math.abs(mLastExtremes[extType][k]
                            - mLastExtremes[1 - extType][k]);
                    Log.i(TAG, "onSensorChanged:" + diff);
                    if (diff > SENSITIVITY) {
                        Log.i(TAG, "onSensorChanged: 3");
                        boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                        boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                        boolean isNotContra = (mLastMatch != 1 - extType);

                        if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough
                                && isNotContra) {
                            Log.i(TAG, "onSensorChanged: 4");
                            end = System.currentTimeMillis();
                            if (end - start > mLimit) {// 此时判断为走了一步
                                Log.i(TAG, "onSensorChanged: 有效的数据");
                                CURRENT_SETP++;
                                pedmoeterBean.setStepCount(CURRENT_SETP);
                                pedmoeterBean.setLastStepTime(System.currentTimeMillis());
                                mLastMatch = extType;
                                start = end;
                                LogWriter.d("Current count = " + CURRENT_SETP);
                            }
                        } else {
                            mLastMatch = -1;
                        }
                    }
                    mLastDiff[k] = diff;
                }
                mLastDirections[k] = direction;
                mLastValues[k] = v;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public PedometerListener(PedometerBean pedmoeterBean) {
        this.pedmoeterBean = pedmoeterBean;
    }
}
