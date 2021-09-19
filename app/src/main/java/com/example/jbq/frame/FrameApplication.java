package com.example.jbq.frame;

import android.app.Activity;
import android.app.Application;
import android.util.Log;


import com.example.jbq.util.PrefsManager;

import java.util.ArrayList;
import java.util.List;

public class FrameApplication extends Application {
    private static final String TAG = "FrameApplication";
    private static ArrayList<Activity> activityList = new ArrayList<>();

    public List<Activity> getActivityList() {
        return activityList;
    }

    public static void addActivity(final Activity act) {
        if (act != null) {
            activityList.add(act);
        }
    }

    public static void removeActivity(final Activity activity) {
        if (activity != null && !activityList.isEmpty() && activityList.indexOf(activity) != -1) {
            activityList.remove(activity);
        }
    }

    public static void finishAll() {
        for (Activity activity : activityList) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static void exitApp() {
        try {
            finishAll();
        } catch (final Exception e) {
            Log.e(TAG, "exitApp: ?", e);
        } finally {
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
    private PrefsManager prefsManager;
    private static FrameApplication instance;

    public PrefsManager getPrefsManager() {
        return prefsManager;
    }

    public static FrameApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefsManager = new PrefsManager(this);
        instance = this;
    }
}
