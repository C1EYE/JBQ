package com.example.jbq.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.example.jbq.bean.PedometerBean;
import com.example.jbq.bean.PedometerChartBean;
import com.example.jbq.db.DBHelper;
import com.example.jbq.frame.FrameApplication;
import com.example.jbq.util.ACache;
import com.example.jbq.util.Settings;
import com.example.jbq.util.Utils;

public class PedometerService extends Service {

    private SensorManager sensorManager;
    private PedometerBean pedometerBean;
    private PedometerListener pedometerListener;
    public static final int STATUS_NOT_RUN = 0;
    public static final int STATUS_RUNNING = 1;
    private int runStatus = STATUS_NOT_RUN;
    private Settings settings;
    private PedometerChartBean chartBean;
    private static final long SAVE_CHART_TIME = 60000L;
    private static Handler handler = new Handler();
    private Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            if (runStatus == STATUS_RUNNING) {
                if (handler != null && chartBean != null) {
                    handler.removeCallbacks(timeRunnable);
                    updateChartData();
                    handler.postDelayed(timeRunnable, SAVE_CHART_TIME);
                }
            }
        }
    };

    private IPedometerService.Stub iPedometerService = new IPedometerService.Stub() {
        @Override
        public int getStepsCount() throws RemoteException {
            if (pedometerBean != null) {
                return pedometerBean.getStepCount();
            }
            return 0;
        }

        @Override
        public void resetCount() throws RemoteException {
            if (pedometerBean != null) {
                pedometerBean.reset();
                saveData();
            }
            if (chartBean != null) {
                chartBean.reset();
                saveChartData();
            }
            if (pedometerListener != null) {
                pedometerListener.resetCurrentStep();
            }
        }

        @Override
        public void startSetpsCount() throws RemoteException {
            if (sensorManager != null && pedometerListener != null) {
                Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.registerListener(pedometerListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                pedometerBean.setStartTime(System.currentTimeMillis());
                //记录哪天数据
                pedometerBean.setDay(Utils.getTimestemByDay());
                runStatus = STATUS_RUNNING;
                //开始刷新数据
                handler.postDelayed(timeRunnable, SAVE_CHART_TIME);
            }
        }

        @Override
        public void stopSetpsCount() throws RemoteException {
            if (sensorManager != null && pedometerListener != null) {
                Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                sensorManager.unregisterListener(pedometerListener, sensor);
                runStatus = STATUS_NOT_RUN;
                handler.removeCallbacks(timeRunnable);
            }
        }

        @Override
        public float getCalorie() throws RemoteException {
            if (pedometerBean != null) {
                return getCalorieBySteps(pedometerBean.getStepCount());
            }
            return 0;
        }

        @Override
        public float getDistance() {
            try {
                if (pedometerBean != null) {
                    return getDistanceBySteps(pedometerBean.getStepCount());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        public void saveData() throws RemoteException {
            if (pedometerBean != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DBHelper dbHelper = new DBHelper(PedometerService.this, DBHelper.DB_NAME);
                        pedometerBean.setDistance(getDistance());
                        pedometerBean.setCalorie(getCalorieBySteps(pedometerBean.getStepCount()));
                        long time = pedometerBean.getLastStepTime() - pedometerBean.getStartTime();
                        if (time == 0) {
                            pedometerBean.setPace(0);
                            pedometerBean.setSpeed(0);
                        } else {
                            int pace = Math.round(60 * pedometerBean.getStepCount() / time);
                            pedometerBean.setPace(pace);
                            long speed = Math.round((pedometerBean.getDistance() / 1000) / (time / (3600.0)));
                            pedometerBean.setSpeed(speed);
                        }
                        dbHelper.writeToDatabase(pedometerBean);
                    }
                }).start();
            }
        }

        @Override
        public void setSensitivity(float sensitivity) throws RemoteException {
            if (settings != null) {
                settings.setSensitivity(sensitivity);
            }
            if (pedometerListener != null) {
                pedometerListener.setSENSITIVITY(sensitivity);
            }
        }

        @Override
        public float getSensitivity() throws RemoteException {
            if (settings != null) {
                return settings.getSensitivity();
            }
            return 0;
        }

        @Override
        public int getInterval() throws RemoteException {
            if (settings != null) {
                return settings.getInterval();
            }
            return 0;
        }

        @Override
        public void setInterval(int interval) throws RemoteException {
            if (settings != null) {
                settings.setInterval(interval);
            }
            if (pedometerListener != null) {
                pedometerListener.setmLimit(interval);
            }
        }

        @Override
        public long getStartTimestmp() throws RemoteException {
            if (pedometerBean != null) {
                return pedometerBean.getStartTime();
            }
            return 0;
        }

        @Override
        public int getServiceRunningStatus() throws RemoteException {
            return runStatus;
        }

        @Override
        public PedometerChartBean getChartData() throws RemoteException {
            return chartBean;
        }

    };

    public float getCalorieBySteps(int stepCount) {
        //步长
        float stepLen = settings.getSetpLength();
        //体重
        float bodyWeight = settings.getBodyWeight();
        float METRIC_WALKING_FACTOR = 0.708f;
        float METRIC_RUNNING_FACTOR = 1.02784823f;
        //卡路里计算公式
        return (bodyWeight * METRIC_RUNNING_FACTOR) * stepLen * stepCount / 100000.0f;
    }

    public float getDistanceBySteps(int stepCount) {
        float stepLen = settings.getSetpLength();
        return (float) ((stepCount * stepLen) / 100000.0f);
    }

    /**
     * 更新图标数据
     */
    private void updateChartData() {
        if (chartBean.getIndex() < 1440 - 1) {
            chartBean.setIndex(chartBean.getIndex() + 1);
            chartBean.getDataArray()[chartBean.getIndex()] = pedometerBean.getStepCount();
        }
    }

    private void saveChartData() {
        String jsonStr = Utils.objToJson(chartBean);
        ACache.get(FrameApplication.getInstance()).put("JsonChartData", jsonStr);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iPedometerService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        pedometerBean = new PedometerBean();
        pedometerListener = new PedometerListener(pedometerBean);
        settings = new Settings(this);
        chartBean = new PedometerChartBean();
    }
}
