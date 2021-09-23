package com.example.jbq.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jbq.MainActivity;
import com.example.jbq.R;
import com.example.jbq.frame.BaseActivity;

public class LoginActivity extends BaseActivity {
    private android.widget.ImageView iv;
    private android.widget.LinearLayout number11;
    private android.widget.TextView tvNumber;
    private android.widget.EditText etNumber;
    private android.widget.LinearLayout password11;
    private android.widget.TextView tvPassword;
    private android.widget.EditText etPassword;
    private Button btnLogin;

    @Override
    protected void onInitVariable() {

    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
        initView();
        btnLogin.setOnClickListener(v -> {
            Editable name = etNumber.getText();
            Editable pass = etPassword.getText();
            //懒得搞了
            if(true){
                startActivity(new Intent(LoginActivity.this, AboutActivity.class));
            }else{
                android.app.AlertDialog dialog;
                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle("账号或密码不能为空")                 //设置对话框的标题
                        .setIcon(R.mipmap.ic_launcher)               //设置对话框标题图标
                        .setMessage("请输入账号和密码")                //设置对话框的提示信息
                        //添加"确定"按钮
                        .setPositiveButton("确定", (dialog1, which) -> {
                            dialog1.dismiss();                             //关闭对话框
                            //关闭MainActivity
                        })
                        //添加“取消”按钮
                        .setNegativeButton("取消", (dialog12, which) -> {
                            dialog12.dismiss();                             //关闭对话框
                        });
                dialog = builder.create();
                dialog.show();
            }


        });
    }

    @Override
    protected void onRequestData() {

    }

    private void initView() {
        iv = findViewById(R.id.iv);
        number11 = findViewById(R.id.number_11);
        tvNumber = findViewById(R.id.tv_number);
        etNumber = findViewById(R.id.et_number);
        password11 = findViewById(R.id.password_11);
        tvPassword = findViewById(R.id.tv_password);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
    }
}
