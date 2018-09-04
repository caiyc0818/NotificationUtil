package com.bcinfo.notificationutils.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.v4.app.NotificationCompat;

import com.bcinfo.notificationutils.MyApplication;
import com.bcinfo.notificationutils.R;
import com.bcinfo.notificationutils.utils.Constants;

import java.util.Random;

import static android.app.Notification.VISIBILITY_PUBLIC;

/**
 * 设置通知栏
 *
 * @author cyc
 */
public class NotificationUtils {

    private static Intent intent = null;

    public static void sendMessage(Message msg) {
        if (Constants.OPEN_HEADS_UP_NOTIFICATION) {
            showHeadsUpNotification(msg);
        } else {
            showCommonNotification(msg);
        }
    }

    /**
     * 普通通知
     */

    private static void showCommonNotification(Message msg) {
        int id = new Random().nextInt();
        NotificationManager nm = (NotificationManager)
                MyApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        intent = new Intent();
        intent.putExtra("pushMessage", msg.obj.toString());
        intent.putExtra("notificationId", id);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(MyApplication.getInstance(), DispatchNotificationActivity.class);
        PendingIntent pi = PendingIntent.getActivity(MyApplication.getInstance(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification;
        notification = new NotificationCompat.Builder(MyApplication.getInstance(), "default")
                .setContentTitle("在沃")
                .setContentText(msg.obj.toString())
                .setSmallIcon(R.drawable.push_small)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pi)
                .setVisibility(VISIBILITY_PUBLIC)
                .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle("在沃").bigText(msg.obj.toString()))
                .setWhen(System.currentTimeMillis())
                .setLargeIcon(BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), R.mipmap.ic_launcher))
                .build();

        notification.flags = Notification.FLAG_AUTO_CANCEL;
        if (nm != null) {
            nm.notify(id, notification);

        }
    }

    /**
     * 悬挂通知
     */

    private static void showHeadsUpNotification(Message msg) {
        int id = new Random().nextInt();
        NotificationManager nm = (NotificationManager)
                MyApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        intent = new Intent();
        intent.putExtra("pushMessage", msg.obj.toString());
        intent.putExtra("notificationId", id);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(MyApplication.getInstance(), DispatchNotificationActivity.class);
        PendingIntent pi = PendingIntent.getActivity(MyApplication.getInstance(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Long time = System.currentTimeMillis();
        Notification notification = new NotificationCompat.Builder(MyApplication.getInstance(), "default")
                .setSmallIcon(R.drawable.push_small)
                .setLargeIcon(BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), R.mipmap.ic_launcher))
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setContentTitle("在沃")
                .setStyle(new NotificationCompat.BigPictureStyle().setBigContentTitle("在沃").setSummaryText(msg.obj.toString()).bigLargeIcon(BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), R.mipmap.ic_launcher))
                        .bigPicture(BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), R.mipmap.ic_launcher)))
                .setContentText(msg.obj.toString())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setVisibility(VISIBILITY_PUBLIC)
                .setWhen(time)
                .setContentIntent(pi)
                .setTicker("在沃")
                .setPriority(Notification.PRIORITY_HIGH)
                .build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        if (nm != null) {
            nm.notify(id, notification);
        }
    }

}
