package com.bcinfo.notificationutils.utils;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

import com.bcinfo.notificationutils.MyApplication;


public class ToastUtil {
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;

    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;

    static int horizontal = 0;// 水平

    static int vertical = 0;// 竖直

    public ToastUtil(Context context) {
    }

    public static void showShortToast(Context context, String str) {
        Toast toast = Toast.makeText(MyApplication.getInstance().getApplicationContext(), str, LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, horizontal, vertical);
        toast.show();
        //修改到3秒消失 2018.8.1 李双双
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 3000);
    }

    public static void showLongToast(Context context, String str) {
        Toast toast = Toast.makeText(MyApplication.getInstance().getApplicationContext(), str, LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, horizontal, vertical);
        toast.show();
        //修改到3秒消失 2018.8.1 李双双
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 3000);
    }
}
