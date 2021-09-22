package com.example.jbq.util;

import android.app.ActivityManager;
import android.content.Context;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Utils {
    public static long getTimestemByDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        String dateStr = sdf.format(d);
        try {
            Date date = sdf.parse(dateStr);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }



    public static String objToJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static String getFormatVal(double val) {
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(val);
    }
    public static String getFormatVal(double val,String str) {
        DecimalFormat format = new DecimalFormat(str);
        return format.format(val);
    }

    public static boolean isServiceRunning(Context context, String serviceName) {
        boolean isRunning = false;
        if (context == null || serviceName == null) {
            return isRunning;
        }
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : list) {
            if (runningServiceInfo.service.getClassName().trim().equals(runningServiceInfo.service.getClassName())) {
                isRunning = true;
                return isRunning;
            }
        }
        return isRunning;

    }
}
