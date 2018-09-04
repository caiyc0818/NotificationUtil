package com.bcinfo.notificationutils;

import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.bcinfo.notificationutils.notification.NotificationAlertDialogActivity;
import com.bcinfo.notificationutils.notification.NotificationUtils;
import com.bcinfo.notificationutils.utils.Constants;
import com.bcinfo.notificationutils.utils.ToastUtil;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Switch aSwitch = findViewById(R.id.switch1);
        Switch bSwitch = findViewById(R.id.switch2);
        Switch cSwitch = findViewById(R.id.switch3);
        Switch dSwitch = findViewById(R.id.switch4);


        aSwitch.setOnCheckedChangeListener(this);
        bSwitch.setOnCheckedChangeListener(this);
        cSwitch.setOnCheckedChangeListener(this);
        dSwitch.setOnCheckedChangeListener(this);


        findViewById(R.id.notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("我是一条测试通知", 0);
            }
        });


    }

    private void sendMessage(String data, int what) {
        if (!Constants.IS_SHOW_ALERT && !Constants.SHOW_NOTIFICATION) {
            ToastUtil.showShortToast(this, "请先打开通知或者打开弹窗");
        } else {
            if (Constants.IS_SHOW_ALERT) {
                Intent i = new Intent(this, NotificationAlertDialogActivity.class);
                i.putExtra("notificationMessage", data);
                i.putExtra("notificationTitle", "");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
            if (Constants.SHOW_NOTIFICATION) {
                Message msg = Message.obtain();
                msg.what = what;
                msg.obj = data;
                NotificationUtils.sendMessage(msg);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch1:
                Constants.IS_SHOW_ALERT = isChecked;
                break;
            case R.id.switch2:
                Constants.OPEN_HEADS_UP_NOTIFICATION = isChecked;
                break;
            case R.id.switch3:
                Constants.SHOW_NOTIFICATION = isChecked;
                break;
            case R.id.switch4:
                Constants.isLogin = isChecked;
                break;
            default:
                break;
        }
    }


}
