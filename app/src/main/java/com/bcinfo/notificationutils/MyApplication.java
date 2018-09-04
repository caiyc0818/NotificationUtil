package com.bcinfo.notificationutils;

import android.app.Application;

import com.bcinfo.notificationutils.model.NotificationStateBean;

public class MyApplication extends Application {
    private static MyApplication mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static MyApplication getInstance() {
        return mInstance;
    }


    /**
     * 通知
     */

    private NotificationStateBean notificationStateBean;

    public NotificationStateBean getNotificationStateBean() {
        if (notificationStateBean == null) {
            notificationStateBean = new NotificationStateBean();
        }
        return notificationStateBean;
    }

    /**
     * shortCuts跳转的activity
     */

    private Class jumpActivity;

    public Class getJumpActivity() {
        return jumpActivity;
    }

    public void setJumpActivity(Class jumpActivity) {
        this.jumpActivity = jumpActivity;
    }
}
