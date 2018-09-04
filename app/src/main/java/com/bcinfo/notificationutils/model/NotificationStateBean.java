package com.bcinfo.notificationutils.model;

/**
 * 最新处理通知temp
 *
 * @author cyc
 */
public class NotificationStateBean {

    private String pushMessage = "";
    private int notificationId = -1;
    private boolean isFromPushMessage = false;

    public NotificationStateBean() {
    }


    public NotificationStateBean(String pushMessage, boolean isFromPushMessage, int notificationId) {
        this.pushMessage = pushMessage;
        this.isFromPushMessage = isFromPushMessage;
        this.notificationId = notificationId;
    }

    public String getPushMessage() {
        return pushMessage;
    }

    public void setPushMessage(String pushMessage) {
        this.pushMessage = pushMessage;
    }

    public boolean getIsFromPushMessage() {
        return isFromPushMessage;
    }

    public void setIsFromPushMessage(boolean isFromPushMessage) {
        this.isFromPushMessage = isFromPushMessage;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
}
