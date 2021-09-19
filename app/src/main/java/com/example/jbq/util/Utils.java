package com.example.jbq.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static float getCalorieBySteps(int stepCount) {
        //步长
        int stepLen = 50;
        //体重
        int bodyWeight = 70;
        float METRIC_WALKING_FACTOR = 0.708f;
        float METRIC_RUNNING_FACTOR = 1.02784823f;
        //卡路里计算公式
        return (bodyWeight * METRIC_RUNNING_FACTOR) * stepLen * stepCount / 100000.0f;
    }

    public static float getDistanceBySteps(int stepCount) {
        int stepLen = 50;
        return (float) ((stepCount * stepLen) / 100000.0f);
    }
}
