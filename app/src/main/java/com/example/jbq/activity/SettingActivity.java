package com.example.jbq.activity;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jbq.R;
import com.example.jbq.frame.BaseActivity;
import com.example.jbq.service.IPedometerService;
import com.example.jbq.service.PedometerService;
import com.example.jbq.util.LogWriter;
import com.example.jbq.util.Settings;
import com.example.jbq.util.Utils;

public class SettingActivity extends BaseActivity {

    private android.widget.RelativeLayout titleLy;
    private android.widget.TextView leftText;
    private ImageView leftImg;
    private ImageView titleImage;
    private android.widget.TextView title;
    private android.widget.TextView rightText;
    private ImageView rightImg;
    private ListView listView;
    private SettingListAdapter adapter = new SettingListAdapter();
    ;

    static class ViewHolder {
        TextView title;
        TextView desc;
    }

    public class SettingListAdapter extends BaseAdapter {
        private String[] listTitle = {"设置步长", "设置体重", "传感器灵敏度", "传感器采样时间"};
        private Settings setting = null;

        SettingListAdapter() {
            this.setting = new Settings(SettingActivity.this);
        }

        @Override
        public int getCount() {
            return listTitle.length;
        }

        @Override
        public Object getItem(int position) {
            if (listTitle != null && position < getCount()) {
                return listTitle[position];
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private IPedometerService remoteService;
        private ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                remoteService = IPedometerService.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                remoteService = null;
            }
        };

        private void sensitiveClick()
        {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingActivity.this, AlertDialog.THEME_HOLO_LIGHT);
            mBuilder.setItems(R.array.sensitive_array, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    setting.setSensitivity(Settings.SENSITIVE_ARRAY[which]);
                    if (adapter != null)
                    {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            mBuilder.setTitle("设置传感器灵敏度");
            mBuilder.create().show();
        }

        private void setInterval() {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingActivity.this, AlertDialog.THEME_HOLO_LIGHT);
            mBuilder.setItems(R.array.interval_array, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    setting.setInterval(Settings.INTERBAL_ARRAY[which]);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            mBuilder.setTitle("设置传感器采样间隔");
            mBuilder.create().show();
        }


        private void stepClick(float pStepLen) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingActivity.this, AlertDialog.THEME_HOLO_LIGHT);
            mBuilder.setTitle("设置步长");
            View mView = View.inflate(SettingActivity.this, R.layout.view_dialog_input, null);
            final EditText mInput = (EditText) mView.findViewById(R.id.input);
            mInput.setText(String.valueOf(pStepLen));
            mBuilder.setView(mView);
            mBuilder.setNegativeButton("取消", null);
            mBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String val = mInput.getText().toString();
                    if (val != null && val.length() > 0) {
                        float len = Float.parseFloat(val);
                        setting.setStepLength(len);
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(SettingActivity.this, "请输入正确的参数!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mBuilder.create().show();
        }


        private void weightClick(float pBodyWeight)
        {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingActivity.this, AlertDialog.THEME_HOLO_LIGHT);
            mBuilder.setTitle("设置体重");
            View mView = View.inflate(SettingActivity.this, R.layout.view_dialog_input, null);
            final EditText mInput = (EditText) mView.findViewById(R.id.input);
            mInput.setText(String.valueOf(pBodyWeight));
            mBuilder.setView(mView);
            mBuilder.setNegativeButton("取消", null);
            mBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    String val = mInput.getText().toString();
                    if (val != null && val.length() > 0)
                    {
                        float bodyWeight = Float.parseFloat(val);
                        setting.setBodyWeight(bodyWeight);
                        if (adapter != null)
                        {
                            adapter.notifyDataSetChanged();
                        }
                    }
                    else
                    {
                        Toast.makeText(SettingActivity.this, "请输入正确的参数!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mBuilder.create().show();
        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = View.inflate(SettingActivity.this, R.layout.item_setting, null);
            viewHolder.title = convertView.findViewById(R.id.title);
            viewHolder.desc = convertView.findViewById(R.id.item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(listTitle[position]);
        switch (position) {
            case 0: {
                float stepLen = setting.getSetpLength();
                viewHolder.desc.setText(String.format("计算距离和消耗的热量: %s", stepLen));
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("FUCC K", "onClick: FUCK");
                        stepClick(stepLen);
                    }
                });
            }
            break;
            case 1: {
                final float bodyWeight = setting.getBodyWeight();
                viewHolder.desc.setText(String.format("体重: %s", bodyWeight));
                convertView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        weightClick(bodyWeight);
                    }
                });
            }
            break;
            case 2: {
                double sensitivity = setting.getSensitivity();
                viewHolder.desc.setText(String.format("灵敏度: %s", sensitivity));
                convertView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        sensitiveClick();
                    }
                });
            }
            break;
            case 3: {
                int interval = setting.getInterval();
                viewHolder.desc.setText(String.format("时间间隔: %s", Utils.getFormatVal(interval, "#.00")));
                convertView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        setInterval();
                    }
                });
            }
            break;
            default: {
                LogWriter.d("Posotion " + position);
            }
        }
        return convertView;
    }

}


    @Override
    protected void onInitVariable() {

    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_setting);
        initView();
    }

    @Override
    protected void onRequestData() {
        Intent serviceIntent = new Intent(this, PedometerService.class);
        if (!Utils.isServiceRunning(this, PedometerService.class.getName())) {
            startService(serviceIntent);
        } else {
            serviceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }

    private void initView() {
        titleLy = findViewById(R.id.titleLy);
        leftText = findViewById(R.id.leftText);
        leftImg = findViewById(R.id.leftImg);
        titleImage = findViewById(R.id.titleImage);
        title = findViewById(R.id.title);
        rightText = findViewById(R.id.rightText);
        rightImg = findViewById(R.id.rightImg);
        listView = findViewById(R.id.listView);
        leftImg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });
        title.setText("设置");
        listView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
