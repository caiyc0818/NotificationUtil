package com.bcinfo.notificationutils.notification;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.bcinfo.notificationutils.utils.StringUtils;

/**
 * 全局alertDialog
 *
 * @author cyc
 */
@SuppressLint("Registered")
public class NotificationAlertDialogActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        showAlertDialog();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        showAlertDialog();
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(StringUtils.isEmpty(getIntent().getStringExtra("notificationTitle"))
                ? "在沃" : getIntent().getStringExtra("notificationTitle"))
                .setMessage(StringUtils.isEmpty(getIntent().getStringExtra("notificationMessage"))
                        ? "" : getIntent().getStringExtra("notificationMessage"))
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("查看详情", (dialog, which) -> dialog.dismiss())
                .setOnDismissListener(dialog -> finish());
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }
}
