package com.example.jbq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.jbq.R;
import com.example.jbq.frame.BaseActivity;


/**
 * 欢迎页
 */
public class WelcomeActivity extends BaseActivity {

    public static final int DELAY_MILLIS = 3000;
    private Handler handler;
    private Runnable jumpRunnable;

    @Override
    protected void onInitVariable() {
        handler = new Handler();
        jumpRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        };

    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_welcome);

    }

    @Override
    protected void onRequestData() {
        handler.postDelayed(jumpRunnable, DELAY_MILLIS);
    }
}
