package com.example.jbq.frame;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

public abstract class BaseActivity extends Activity {
    private static final String TAG = "BaseActivity";
    protected boolean isHideAppTitle = true;
    protected boolean isHideSysTitle = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.onInitVariable();
        if (this.isHideAppTitle) {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        if (this.isHideSysTitle) {
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
        this.onInitView(savedInstanceState);
        this.onRequestData();
        FrameApplication.addActivity(this);
        Log.d(TAG, "onCreate: " + getClass().getSimpleName());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        FrameApplication.removeActivity(this);
        super.onDestroy();
        Log.d(TAG, "onDestroy: " + getClass().getSimpleName());
    }

    protected abstract void onInitVariable();

    protected abstract void onInitView(final Bundle savedInstanceState);

    protected abstract void onRequestData();


}

