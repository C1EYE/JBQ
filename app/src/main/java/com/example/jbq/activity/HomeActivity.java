package com.example.jbq.activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.example.jbq.R;
import com.example.jbq.bean.PedometerChartBean;
import com.example.jbq.frame.BaseActivity;
import com.example.jbq.service.IPedometerService;
import com.example.jbq.service.PedometerService;
import com.example.jbq.util.LogWriter;
import com.example.jbq.util.Utils;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends BaseActivity {
    private static final String TAG = "HomeActivity";
    private android.widget.ImageView imageview;
    private android.widget.TextView daka;
    private android.widget.TextView min;
    private android.widget.TextView distance;
//    private com.example.jbq.widgets.CircleProgressBar circleBar;
    private android.widget.TextView stepCount;
    private android.widget.TextView targetText;
    private android.widget.Button reset;
    private android.widget.Button btnStart;
    private com.github.mikephil.charting.charts.BarChart chart1;


    private int status = -1;
    public static final int STATUS_NOT_RUNNING = 0;
    public static final int STATUS_RUNNING = 1;
    private boolean isRunning = false;
    private boolean isChartUpdate = false;
    public static final int MESSAGE_UPDATE_STEP_COUNT = 1000;
    public static final int MESSAGE_UPDATE_CHART_DATA = 2000;
    public static final int GET_DATA_TIME = 200;
    public static final long GET_CHART_DATA_TIME = 60000L;


    private IPedometerService remoteService;
    private PedometerChartBean chartBean;
    ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 4, 2000, TimeUnit.MILLISECONDS, new SynchronousQueue<>());



    @Override
    protected void onInitVariable() {

    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        initView();
    }

    private boolean bindService = false;

    @Override
    protected void onRequestData() {
        //检查服务是否运行
        Intent serviceIntent = serviceIntent = new Intent(this, PedometerService.class);
        if (Utils.isServiceRunning(this, PedometerService.class.getName())) {
            startService(serviceIntent);
        } else {
            serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        bindService = bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
        //init
        if (bindService && remoteService != null) {
            try {
                status = remoteService.getServiceRunningStatus();
                if (status == PedometerService.STATUS_NOT_RUN) {
                    btnStart.setText("启动");
                } else if (status == PedometerService.STATUS_RUNNING) {
                    btnStart.setText("停止");
                    isRunning = true;
                    isChartUpdate = true;
                    pool.execute(new StepRunnable());
                    pool.execute(new ChartRunnable());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }else{
            btnStart.setText("启动");
        }
    }

    private void initView() {
        imageview = findViewById(R.id.imageview);
        daka = findViewById(R.id.daka);
        min = findViewById(R.id.min);
        distance = findViewById(R.id.distance);
//        circleBar = findViewById(R.id.circle_bar);
        stepCount = findViewById(R.id.stepCount);
        targetText = findViewById(R.id.target_text);
        reset = findViewById(R.id.reset);
        btnStart = findViewById(R.id.btnStart);
        chart1 = findViewById(R.id.chart1);

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("确认重置");
                builder.setMessage("您的记录将要被清除，确定吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (remoteService != null) {
                            try {
                                remoteService.stopSetpsCount();
                                remoteService.resetCount();
                                chartBean = remoteService.getChartData();
                                updateChart(chartBean);
                                status = remoteService.getServiceRunningStatus();
                                if(status == PedometerService.STATUS_RUNNING){
                                    btnStart.setText("停止");
                                }else if(status == PedometerService.STATUS_NOT_RUN){
                                    btnStart.setText("启动");
                                }
                            } catch (RemoteException e) {
                                LogWriter.d(e.toString());
                            }
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", null);
                AlertDialog resetDlg = builder.create();
                resetDlg.show();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    status = remoteService.getServiceRunningStatus();
                } catch (RemoteException e) {
                    LogWriter.d(e.toString());
                }
                if (status == STATUS_RUNNING && remoteService != null) {
                    try {
                        remoteService.stopSetpsCount();
                        btnStart.setText("启动");
                        isRunning = false;
                        isChartUpdate = false;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else if (status == STATUS_NOT_RUNNING && remoteService != null) {
                    try {
                        remoteService.startSetpsCount();
                        btnStart.setText("停止");
                        isRunning = true;
                        isChartUpdate = true;
                        pool.execute(new StepRunnable());
                        pool.execute(new ChartRunnable());
                        chartBean = remoteService.getChartData();

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteService = IPedometerService.Stub.asInterface(service);
            try {
                status = remoteService.getServiceRunningStatus();
                if (status == STATUS_RUNNING) {
                    btnStart.setText("停止");
                    isChartUpdate = true;
                    isRunning = true;

                    chartBean = remoteService.getChartData();
                    updateChart(chartBean);
                    pool.execute(new StepRunnable());
                    pool.execute(new ChartRunnable());
                } else {
                    btnStart.setText("启动");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                LogWriter.d(e.toString());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //统计步数
    private class StepRunnable implements Runnable {
        @Override
        public void run() {
            while (isRunning) {
                try {
                    status = remoteService.getServiceRunningStatus();
                    if (status == STATUS_RUNNING) {
                        handler.removeMessages(MESSAGE_UPDATE_STEP_COUNT);
                        handler.sendEmptyMessage(MESSAGE_UPDATE_STEP_COUNT);
                        TimeUnit.MILLISECONDS.sleep(GET_DATA_TIME);
                    }
                } catch (RemoteException | InterruptedException e) {
                    LogWriter.d(e.toString());
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bindService){
            bindService = false;
            isRunning = false;
            isChartUpdate = false;
            unbindService(serviceConnection);
        }
    }

    private class ChartRunnable implements Runnable {

        @Override
        public void run() {
            while (isChartUpdate) {
                try {
                    chartBean = remoteService.getChartData();
                    handler.removeMessages(MESSAGE_UPDATE_CHART_DATA);
                    handler.sendEmptyMessage(MESSAGE_UPDATE_CHART_DATA);
                    TimeUnit.MILLISECONDS.sleep(GET_CHART_DATA_TIME);
                } catch (RemoteException | InterruptedException e) {
                    LogWriter.d(e.toString());
                }
            }
        }
    }

    public void updateStepCount() {
        if (remoteService != null) {
            int stepCountVal = 0;
            double calorieVal = 0;
            double distanceVal = 0;
            try {
                stepCountVal = remoteService.getStepsCount();
                calorieVal = remoteService.getCalorie();
                distanceVal = remoteService.getDistance();
            } catch (RemoteException e) {
                LogWriter.d(e.toString());
            }
            stepCount.setText(stepCountVal + "步");
            daka.setText(Utils.getFormatVal(calorieVal) + "卡");
            distance.setText(Utils.getFormatVal(distanceVal));
//            circleBar.setProgress(stepCountVal);
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_UPDATE_STEP_COUNT: {
                    updateStepCount();
                    break;
                }
                case MESSAGE_UPDATE_CHART_DATA: {
                    if (chartBean != null) {
                        updateChart(chartBean);
                    }
                    break;
                }
                default: {
                    LogWriter.d("Default = " + msg.what);
                }
            }
            super.handleMessage(msg);
        }
    };

    @SuppressLint("SetTextI18n")
    public void updateChart(PedometerChartBean bean) {
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals = new ArrayList<>();
        if (bean != null) {
            for (int i = 0; i < bean.getIndex(); i++) {
                xVals.add(i + "分s");
                int valY = bean.getDataArray()[i];
                yVals.add(new BarEntry(valY, i));
            }
            targetText.setText(bean.getIndex() + "分");
            min.setText(bean.getIndex()+"分");
            BarDataSet set1 = new BarDataSet(yVals, "所走的步数");
            set1.setBarSpacePercent(2f);
            ArrayList<BarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            //FIXME 重写这里
            BarData data = new BarData(xVals, dataSets);
            data.setValueTextSize(10f);
            chart1.setData(data);
            chart1.invalidate();
        }

    }


}
