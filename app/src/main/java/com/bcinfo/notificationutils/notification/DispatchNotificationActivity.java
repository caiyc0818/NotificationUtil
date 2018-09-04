package com.bcinfo.notificationutils.notification;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bcinfo.notificationutils.MainActivity;
import com.bcinfo.notificationutils.MyApplication;
import com.bcinfo.notificationutils.utils.AndroidUtils;
import com.bcinfo.notificationutils.utils.Constants;
import com.bcinfo.notificationutils.utils.ToastUtil;

import java.util.List;

/**
 * 统一分发个推消息activity
 *
 * @author cyc
 */
@SuppressLint("Registered")
public class DispatchNotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //缓存要处理的消息
        MyApplication.getInstance().getNotificationStateBean().setIsFromPushMessage(true);
        MyApplication.getInstance().getNotificationStateBean().setPushMessage(intent.getStringExtra("pushMessage"));
        MyApplication.getInstance().getNotificationStateBean().setNotificationId(intent.getIntExtra("notificationId", -1));
        if (!Constants.isLogin) {
            ToastUtil.showShortToast(this, intent.getStringExtra("pushMessage"));
            jumpNextActivity();
        } else {
            ToastUtil.showShortToast(this, "消息处理完毕！");
            jumpNextActivity();
            NotificationManager nm = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                nm.cancel(MyApplication.getInstance().getNotificationStateBean().getNotificationId());
            }
            MyApplication.getInstance().getNotificationStateBean().setIsFromPushMessage(false);
            MyApplication.getInstance().getNotificationStateBean().setPushMessage("");
        }
    }

    /**
     * 跳转activity
     */
    private void jumpNextActivity() {
        if ("2".equals(AndroidUtils.isAppRun(this))) {
            //2 app在运行,但不在前台 返回前台后跳转
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                am.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
            }
            if (Constants.isLogin && Constants.IS_SHOW_ALERT) {
                startActivity(
                        new Intent(this, NotificationAlertDialogActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra("notificationMessage", MyApplication.getInstance().getNotificationStateBean().getPushMessage())
                                .putExtra("notificationTitle", ""));
                finish();
            } else {
                finish();
            }

        } else {
            if (isExistMainActivity(MainActivity.class)) {
                //app不在运行  重新启动
                Intent intentMain = new Intent(this, MainActivity.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentMain);
                finish();
            } else {
                // ClassName app在前台运行  直接跳转
                if (Constants.isLogin && Constants.IS_SHOW_ALERT) {
                    startActivity(
                            new Intent(DispatchNotificationActivity.this, NotificationAlertDialogActivity.class)
                                    .putExtra("notificationMessage", MyApplication.getInstance().getNotificationStateBean().getPushMessage())
                                    .putExtra("notificationTitle", "")
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                } else {
                    finish();
                }

            }
        }
    }




    //判断某一个类是否存在任务栈里面
    private boolean isExistMainActivity(Class<?> activity){
        Intent intent = new Intent(this, activity);
        ComponentName cmpName = intent.resolveActivity(getPackageManager());
        boolean flag = false;
        if (cmpName != null) { // 说明系统中存在这个activity
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(10);  //获取从栈顶开始往下查找的10个activity
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) { // 说明它已经启动了
                    flag = true;
                    break;  //跳出循环，优化效率
                }
            }
        }
        return flag;
    }
}
