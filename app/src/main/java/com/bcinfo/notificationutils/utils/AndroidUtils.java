/**
 * 文 件 名:  AndroidUtils.java
 * 版    权:  Huawei Technologies Co., Ltd. Copyright YYYY-YYYY,  All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  sKF61027
 * 修改时间:  2011-12-27
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.bcinfo.notificationutils.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;
import android.support.annotation.RequiresApi;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import com.bcinfo.notificationutils.MyApplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * android 自身工具类
 *
 * @author sKF61027
 * @version [版本号, 2011-12-27]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class AndroidUtils {
    /*
     * 原始数据库联系人缓存
     */
    public static final String HIGH_LIGHT_HEAD = "<font color=\"#e20111\">";

    public static final String HIGH_LIGHT_TAIL = "</font>";

    static Toast toast;

    /**
     * 调用系统电话接口
     *
     * @param context
     * @param phoneNumber
     */
    public static void dialerPhone(Context context, String phoneNumber) {
        if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
            Intent intent = new Intent();
            Uri uri = Uri.parse("tel:" + phoneNumber);
            intent.setAction(Intent.ACTION_CALL);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }


    /**
     * 启动系统电话拨号界面
     *
     * @param context
     * @param phoneNumber
     */
    public static void dialerPhoneActivity(Context context, String phoneNumber) {
        if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
            Intent intent = new Intent();
            Uri uri = Uri.parse("tel:" + phoneNumber);
            intent.setAction(Intent.ACTION_DIAL);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 调用系统的联系人编辑
     *
     * @param context
     * @param mRawId
     */
    public static void editContact(Context context, int mRawId) {
        if (mRawId > -1) {
            Uri mUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, mRawId);
            Intent editIntent = new Intent(Intent.ACTION_EDIT, mUri);
            editIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(editIntent);
        }
    }

    /**
     * 发送短信
     *
     * @param context
     * @param phoneNumber
     * @param str
     */
    public static void sendMsg(Context context, String phoneNumber, String str) {
        SmsManager sms = SmsManager.getDefault();

        Intent sendIntent = new Intent("SENT_SMS_ACTION");
        PendingIntent sendPI = PendingIntent.getBroadcast(context, 0,
                sendIntent, 0);
        Intent deliverIntent = new Intent("DELIVERED_SMS_ACTION");
        PendingIntent deliverPI = PendingIntent.getBroadcast(context, 0,
                deliverIntent, 0);
        if (str.getBytes().length > 140) {
            ArrayList<String> msgList = sms.divideMessage(str);
            for (String msg : msgList) {
                sms.sendTextMessage(phoneNumber, null, msg, sendPI, deliverPI);
            }
        } else {
            sms.sendTextMessage(phoneNumber, null, str, sendPI, deliverPI);
        }
    }

    /**
     * 实现文本复制功能 add by wangqianzhou
     *
     * @param content
     */
    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    /**
     * 实现粘贴功能 add by wangqianzhou
     *
     * @param context
     * @return
     */
    public static String paste(Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }

    /**
     * 获取SmsMessage消息
     *
     * @param intent
     * @return
     */
    public final static SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");

        byte[][] pduObjs = new byte[messages.length][];

        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }

        byte[][] pdus = new byte[pduObjs.length][];

        int pduCount = pdus.length;

        SmsMessage[] msgs = new SmsMessage[pduCount];

        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];

            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }

    /**
     * 关闭游标
     *
     * @param cursor
     */
    public static void closeCursor(Cursor cursor) {
        // 判定游标是否为空
        if (null != cursor) {
            // 判定游标是否已经关闭
            if (!cursor.isClosed()) {
                // 关闭游标
                cursor.close();
            }
            // 游标置空
            cursor = null;
        }
    }

    /**
     * 去电话号码头部
     *
     * @param number : 号码
     * @return number (Fail: null)
     */
    public static String deleteHead(String number) {
        String ret = number;

        if ((null == number) || ("".equals(number.trim()))) {
            return ret;
        }

        // TODO
        String countryCode = "";// Global.getInstance().getLoginInfo().getCountryCode();
        if (StringUtils.isEmpty(countryCode)) {
            return ret;
        }

        int startIndex = number.indexOf(countryCode);

        if (startIndex != -1) {
            startIndex += countryCode.length();
            ret = number.substring(startIndex);
        }

        return ret;
    }

    /**
     * 获得手机的IP地址
     *
     * @return String string
     * @see [类、类#方法、类#成员]
     */
    public static String getLocalIPAddress() {
        String address = "";

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        address = inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("ip", ex.toString());
        }
        return address;
    }

    /**
     * 判断是否为wifi网络
     *
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    /**
     * 是否联网
     *
     * @param context
     * @return
     */
    public static boolean isNetWork(Context context) {
        ConnectivityManager cManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            // 联网
            return true;
        } else {
            // 未联网
            return false;
        }
    }

    // Android验证email地址的函数
    static boolean isValidAddress(String address) {
        // Note: Some email provider may violate the standard, so here we only
        // check that
        // address consists of two part that are separated by '@', and domain
        // part contains
        // at least one '.'.
        int len = address.length();
        int firstAt = address.indexOf('@');
        int lastAt = address.lastIndexOf('@');
        int firstDot = address.indexOf('.', lastAt + 1);
        int lastDot = address.lastIndexOf('.');
        return firstAt > 0 && firstAt == lastAt && lastAt + 1 < firstDot
                && firstDot <= lastDot && lastDot < len - 1;
    }

    /**
     * 获取SIM卡联系人信息
     */
    public void getSIMContacts(Context context) {
        ContentResolver resolver = context.getContentResolver();

        /*
         * 获取Sims卡联系人
         */
        Uri uri = Uri.parse("content://icc/adn");

        /*
         * 获取库Phon表字段
         */
        String[] PHONES_PROJECTION = new String[]{Phone.DISPLAY_NAME,
                Phone.NUMBER, Photo.PHOTO_ID, Phone._ID};

        Cursor cursor = resolver
                .query(uri, PHONES_PROJECTION, null, null, null);

        if (cursor == null) {
            return;
        }

        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                /*
                 * 得到手机号码
                 */
                String phone = cursor.getString(cursor
                        .getColumnIndex(Phone.NUMBER));
                /*
                 * 当手机号码为空的或者为空字段 跳过当前循环
                 */
                if (!StringUtils.isEmpty(phone)) {
                    /*
                     * 得到联系人名称
                     */
                    cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
                }
            }
        }

        closeCursor(cursor);
    }

    /**
     * sourceStr是否等于 constraint + ***
     *
     * @param sourceStr
     * @param constraint
     * @return
     */
    public static boolean isExist(String sourceStr, String constraint) {
        String regex = "";
        String reg = "(.*\\b)?" + constraint;
        regex = "^" + reg + ".*$";

        return Pattern.compile(regex).matcher(sourceStr).matches();
    }

    /**
     * Html格式化输入字符串 <功能详细描述>
     *
     * @param head
     * @param body
     * @param end
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String highLight(String head, String body, String end) {
        head = (null != head) ? head : "";
        body = (null != body && !"".equals(body)) ? HIGH_LIGHT_HEAD + body
                + HIGH_LIGHT_TAIL : "";
        end = (null != end) ? end : "";
        return (head + body + end);
    }

    @SuppressWarnings("unused")
    public static int[] quanpinMatch(String key, String code, String head,
                                     String match) {
        /*
         * 返回高亮显示的开始和结束位置
         */
        int[] rets = new int[]{-1, -1};

        /*
         * 将 head字段以","为标识,拆分成String数组
         */
        String[] heads = head.split(",");
        if (heads == null) {
            return rets;
        }
        /*
         * 将 match字段以","为标识,拆分成String数组
         */
        String[] matchs = match.split(",");
        if (matchs == null) {
            return rets;
        }
        /*
         * 定义个数为姓名长度的Boolean数组,记录名字中各个字是否需要高亮 // 未使用字段
         */

        boolean[] boolOfMatch = new boolean[heads[0].length()];

        /*
         * 定义key中首字母在head中出现的位置 //未使用字段
         */
        // boolean[] boolOfIndex = new boolean[heads[0].length()];

        /*
         * 定义一个List拼音数组
         */
        List<String[]> matchList = new ArrayList<String[]>();

        /*
         * 将match字段完全拆分成拼音数组
         */
        for (int i = 0; i < matchs.length; i++) {
            matchList.add(matchs[i].split("_"));
        }

        /*
         * 如果当前code字段中包含key字符串
         */
        if (code.contains(key)) {
            /*
             * 将当前code字段以","为标识,拆分成多音字数组
             */
            String[] codes = code.split(",");

            /*
             * 循环,次数为code拆分后的数组长度(姓名的一种拼音组合)
             */
            for (int i = 0; i < codes.length; i++) {
                /*
                 * 如果当前拼音组合中包含key字段
                 */
                if (codes[i].contains(key)) {
                    /*
                     * 当前拼音组合的首字母字符串
                     */
                    String headtmp = heads[i];
                    /*
                     * 当前拼音组合中汉字对应的拼音字符串数组
                     */
                    String[] matchtmp = matchList.get(i);
                    /*
                     * 姓名中包括多少个汉字字符
                     */
                    int lengthOfHead = headtmp.length();
                    /*
                     * 循环,次数为姓名的长度
                     */
                    a:
                    for (int j = 0; j < lengthOfHead; j++) {
                        /*
                         * 如果key的首字母等于当前循环中汉字的首字母
                         */
                        if (headtmp.charAt(j) == key.charAt(0)) {
                            // boolOfIndex[j] = true;

                            /*
                             * 当前汉字中拼音的个数
                             */
                            int lengthOfOneCode = matchtmp[j].length();
                            /*
                             * key中拼音个数
                             */
                            int lengthOfKey = key.length();

                            /*
                             * 如果key的长度小于等于当前汉字对应拼音的长度
                             */
                            if (lengthOfKey <= lengthOfOneCode) {
                                /*
                                 * key的长度小于等于匹配拼音的长度时 如果匹配拼音中包含key 则此匹配拼音高亮
                                 * Exp:单于(32698) key = (32) 时,"单"字高亮
                                 *
                                 *
                                 * 循环,次数为当前汉字对应拼音的长度
                                 */
                                b:
                                for (int k = 0; k < lengthOfOneCode; k++) {
                                    /*
                                     * 当前汉字对应拼音中前K个字符串
                                     */
                                    String tmpk = matchtmp[j].substring(0,
                                            k + 1);

                                    /*
                                     * 如果当前汉字对应拼音中前K个字符串和key相等
                                     */
                                    if (tmpk.equals(key)) {

                                        /*
                                         * 当前汉字高亮
                                         */
                                        boolOfMatch[j] = true;

                                        rets = new int[]{j, j};
                                        /*
                                         * 匹配结束,终止匹配循环
                                         */
                                        break a;
                                    }
                                }
                            } else {
                                /*
                                 * key的长度大于匹配拼音的长度
                                 * 从匹配拼音开始加key的长度结束包含的全部拼音必须完全包含key 拼音对应汉字高亮
                                 * 否则全部不高亮 Exp:单于(32698) key =
                                 * 单于(326998)时,"单于"高亮 Exp:单于(32698) key =
                                 * 单于(3269989)时,"单于" 不高亮
                                 */

                                /*
                                 * 当前汉字对应拼音
                                 */
                                StringBuffer tmpk = new StringBuffer(
                                        matchtmp[j]);

                                /*
                                 * 当前汉字之后一个汉字的拼音在数组中的位置
                                 */
                                int ki = j;

                                while (tmpk.length() < lengthOfKey
                                        && ++ki < matchtmp.length) {
                                    tmpk.append(matchtmp[ki]);
                                }

                                /*
                                 * 可以与key对应的,以key首字母开始的姓名中汉字对应拼音的长度
                                 * Exp:code=单于于(3269898); key=(989) 则: tmpk =
                                 * 9898, lengthOfTmpk = 4
                                 */
                                int lengthOfTmpk = tmpk.length();

                                /*
                                 * 循环,次数为当前匹配到的拼音组合的长度
                                 */
                                for (int k = 0; k < lengthOfTmpk; k++) {

                                    /*
                                     * 将当前组合分解为单个拼音字符累加的组合方式 Exp: 9898 ->
                                     * 9,98,989,9898
                                     */
                                    String tmpki = tmpk.toString().substring(0,
                                            k + 1);

                                    /*
                                     * 如果当前的组合方式中完全包含key字符串,则匹配成功
                                     */
                                    if (tmpki.equals(key)) {
                                        rets = new int[]{j, ki};

                                        /*
                                         * 判断key对应的汉字个数
                                         */
                                        for (int l = j; l <= ki; l++) {
                                            boolOfMatch[l] = true;
                                        }
                                        /*
                                         * 匹配成功,推出当前循环
                                         */
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return rets;
    }

    public static boolean extractDatabase(Context context) {
        boolean flag = false;
        File path = new File("/data/data/" + context.getPackageName()
                + "/databases/");
        String file = "/data/data/" + context.getPackageName()
                + "/databases/hanzi2code.db";

        if (!path.exists()) {
            flag = path.mkdir();
            if (flag == false) {
                return false;
            }
        }

        if (new File(file).exists()) {
            return true;
        }

        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getAssets().open("hanzi2code.db");
            fos = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int count = 0;

            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
                fos.flush();
            }
        } catch (Exception e) {
            Log.e("AndroidUtils", e.getMessage());
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (IOException e) {
                Log.e("AndroidUtils", e.getMessage());
            }

            try {
                if (null != fos) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.e("AndroidUtils", e.getMessage());
            }

        }

        return true;
    }

    /**
     * 判定输入字符串是否以汉字，数字，字母组成
     *
     * @param str
     * @return
     */
    public static String checkCode(String str) {
        StringBuffer sb = new StringBuffer();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (c < 40680 && c > 19967) {
                sb.append(str.charAt(i));
            } else if (c < 123 && c > 96) {
                sb.append(str.charAt(i));
            } else if (c < 91 && c > 64) {
                sb.append(str.charAt(i));
            } else if (c < 58 && c > 47) {
                sb.append(str.charAt(i));
            }
        }

        return sb.toString();
    }

    /**
     * 弹出提示窗口
     *
     * @param context
     * @param message
     * @param length
     */
    public static void showToast(Context context, String message, int length) {
        Toast.makeText(context, message, length).show();
    }

    /**
     * 同时多次点击只展示一个toast（最新的那个）
     *
     * @param id 要显示内容的id
     * @see [类、类#方法、类#成员]
     */
    public static synchronized void showMsgByToast(int id) {
        // if(toast==null)
        // {
        // toast=Toast.makeText(UserProfile.getInstance().getContext(),
        // UserProfile.getInstance().getContext().getResources().getString(id),
        // Toast.LENGTH_SHORT);
        // }
        // else
        // {
        // toast.setText(UserProfile.getInstance().getContext().getResources().getString(id));
        // }
        // toast.show();
    }

    /**
     * 显示在中间的Toast（无背景） <功能详细描述>
     *
     * @param resourceId
     * @see [类、类#方法、类#成员]
     */
    public static void showToast(int resourceId) {
        // Toast toast = new Toast(UserProfile.getInstance().getContext());
        // toast.setGravity(Gravity.CENTER, 0, 0);
        // TextView text = new TextView(UserProfile.getInstance().getContext());
        // text.setText(resourceId);
        // toast.setView(text);
        // toast.setDuration(Toast.LENGTH_LONG);
        // toast.show();
    }

    /**
     * 去除软件盘
     *
     * @param context
     */
    @SuppressWarnings("static-access")
    public static void hideSoftInput(Context context) {
        InputMethodManager m = (InputMethodManager) context
                .getSystemService(context.INPUT_METHOD_SERVICE);
        if (null != m && null != ((Activity) context).getCurrentFocus()) {
            m.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 判断有无网络
     *
     * @return
     */
    public static boolean hasNetwork(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != manager) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (null != info) {
                if (info.isRoaming() || info.isConnected()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 获取设备UUID
     */
    private static UUID uuid = null;

    public static synchronized UUID getDeviceUUID(Context context) {
        if (context == null) {
            return null;
        }

        if (uuid == null) {
            try {
                TelephonyManager tm = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                String id = tm.getDeviceId();
                if (id != null) {
                    uuid = UUID.nameUUIDFromBytes(id.getBytes());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return uuid;
    }

    /**
     * 执行Linux命令，并返回执行结果。
     */
    public static String exec(String[] args) {
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();

            int read = -1;

            process = processBuilder.start();

            errIs = process.getErrorStream();

            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }

            baos.write('\n');

            inIs = process.getInputStream();

            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }

            byte[] data = baos.toByteArray();

            result = new String(data);
        } catch (IOException e) {
            Log.e("AndroidUtils", e.getMessage());
        } catch (Exception e) {
            Log.e("AndroidUtils", e.getMessage());
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }

                if (inIs != null) {
                    inIs.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                Log.d("VCLIENTTOOL", "exec linux command error");
            }

            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    public static String unicode2UTF(String code) {
        int length = code.length();

        char[] cc = code.toCharArray();

        String ret = "";

        for (int i = 0; i < length; i++) {

            char c = cc[i];

            String str = Integer.toBinaryString(c);

            int len = str.length();

            if (c > 0x00000000 && c < 0x0000007F) {
                ret += c;
            } else if (c > 0x00000080 && c < 0x000007FF) {
                String tmp1 = str.substring(0, len - 6);

                String add1 = "110";

                while ((add1 + tmp1).length() < 8) {
                    add1 += "0";
                }

                tmp1 = add1 + tmp1;

                ret += (char) Integer.parseInt(tmp1, 2);

                ret += (char) Integer
                        .parseInt("10" + str.substring(len - 6), 2);
            } else if (c > 0x00000800 && c < 0x0000FFFF) {
                String tmp1 = str.substring(0, len - 12);

                String add1 = "1110";

                while ((add1 + tmp1).length() < 8) {
                    add1 += "0";
                }

                tmp1 = add1 + tmp1;

                ret += (char) Integer.parseInt(tmp1, 2);

                ret += (char) Integer.parseInt(
                        "10" + str.substring(len - 12, len - 6), 2);

                ret += (char) Integer
                        .parseInt("10" + str.substring(len - 6), 2);

            } else if (c > 0x00010000 && c < 0x0010FFFF) {
                String tmp1 = str.substring(0, len - 18);

                String add1 = "1110";

                while ((add1 + tmp1).length() < 8) {
                    add1 += "0";
                }

                tmp1 = add1 + tmp1;

                ret += (char) Integer.parseInt(tmp1, 2);

                ret += (char) Integer.parseInt(
                        "10" + str.substring(len - 18, len - 12), 2);

                ret += (char) Integer.parseInt(
                        "10" + str.substring(len - 12, len - 6), 2);

                ret += (char) Integer
                        .parseInt("10" + str.substring(len - 6), 2);

            }
        }
        return ret;
    }

    /**
     * 判断一个手机号码是否在指定的号码段内 <功能详细描述>
     *
     * @param numberList 指定的号码段
     * @param number     需要检查的号码
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isUsefulNumber(List<String> numberList, String number) {
        boolean ret = false;

        if (StringUtils.isEmpty(number) || null == numberList) {
            return ret;
        }

        if (numberList.size() == 0 || number.length() <= 1) {
            return ret;
        }

        if (number.startsWith("0")) {
            number = number.substring(1);
        }

        for (int i = 0, n = numberList.size(); i < n; i++) {
            if (number.startsWith(numberList.get(i))) {
                ret = true;
                break;
            }
        }

        return ret;
    }

    /**
     * 获取非零的区号
     *
     * @param areaCode 指定的区号
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getAreaCode(String areaCode) {
        if (areaCode.startsWith("0")) {
            areaCode = areaCode.substring(1);
        }
        return areaCode;
    }

    /**
     * 获取非零的手机号(如013977885210-->13977885210)
     *
     * @param number
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getPhoneNumber(String number) {
        if (number.startsWith("0")) {
            number = number.substring(1);
        }
        return number;
    }

    public static int generateLocalUdpPort(int portBase) {
        int TIMES_LIMIT = 10;
        int count = 0;
        int resp = -1;
        int port = portBase;
        while ((resp == -1) && (port < Integer.MAX_VALUE)) {
            if (count > TIMES_LIMIT) {
                return -1;
            }

            if (isLocalUdpPortFree(port)) {
                resp = port;
                break;
            } else {
                Log.d("generateLocalUdpPort", "try bind port:" + port
                        + " error, try again");
                port += 2;
                count++;
            }
        }

        return resp;
    }

    private static boolean isLocalUdpPortFree(int port) {
        boolean res = false;
        try {
            ServerSocket sock1 = new ServerSocket(port);
            sock1.close();
            res = true;
        } catch (IOException e) {
            Log.d("generateLocalUdpPort", "bind port error");
            res = false;
        }
        return res;
    }

    /**
     * 判断指定服务是否已启动 <功能详细描述>
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isServiceStarted(Context context, String serviceAction) {
        boolean ret = false;
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if (serviceName.getClassName().equals(serviceAction)) {
                if (serviceInfo.pid != 0) {
                    ret = true;
                } else {
                    ret = false;
                }
            }
        }

        return ret;
    }

    /**
     * 获取设备的mac地址
     *
     * @return
     */
    public static String getLocalMacAddress() {
        String mac = "";
        try {
            String path = "sys/class/net/eth0/address";
            FileInputStream fis_name = new FileInputStream(path);
            byte[] buffer_name = new byte[1024 * 8];
            int byteCount_name = fis_name.read(buffer_name);
            if (byteCount_name > 0) {
                mac = new String(buffer_name, 0, byteCount_name, "utf-8");
            }

            if (mac.length() == 0 || mac == null) {
                path = "sys/class/net/wlan0/address";
                FileInputStream fis = new FileInputStream(path);
                byte[] buffer = new byte[1024 * 8];
                int byteCount = fis.read(buffer);
                if (byteCount > 0) {
                    mac = new String(buffer, 0, byteCount, "utf-8");
                }
            }

            if (mac.length() == 0 || mac == null) {
                return "";
            }
        } catch (Exception io) {

        }
        return mac.trim();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName)
                && currentPackageName.equals(context.getPackageName())) {
            return true;
        }

        return false;
    }

    public static boolean isAppRunning(Context context) {
        // 获取ActivityManager
        ActivityManager mAm = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);
        // 获得当前运行的task
        List<ActivityManager.RunningTaskInfo> taskList = mAm
                .getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rti : taskList) {
            // 找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
            if (rti.topActivity.getPackageName().equals(
                    context.getPackageName())
                    && rti.baseActivity.getPackageName().equals(
                    context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回 ClassName app在前台运行。 2 app在运行,但不在前台 -1 app不在运行
     */
    public static String isAppRun(Context context) {
        ActivityManager mAm = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = mAm
                .getRunningTasks(100);
        for (int i = 0; i < taskList.size(); i++) {
            ComponentName topActivity = taskList.get(i).topActivity;
            ComponentName baseActivity = taskList.get(i).baseActivity;
            if (i == 0) {
                if (!TextUtils.isEmpty(topActivity.getPackageName())
                        && topActivity.getPackageName().equals(
                        context.getPackageName())) {
                    return topActivity.getClassName();
                }
            }
            // 找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
            if (topActivity.getPackageName().equals(context.getPackageName())
                    && baseActivity.getPackageName().equals(
                    context.getPackageName())) {
                return "2";
            }
        }
        return "-1";
    }

    public static boolean isAppOnForeground() {

        ActivityManager activityManager = (ActivityManager) MyApplication.getInstance().getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = MyApplication.getInstance().getApplicationContext().getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return Build.BRAND;

    }

    /**
     * 获取版本号
     * +build号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName + "." + info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取版本号
     */
    public static String getVersionNum(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取build号
     */
    public static String getVersionBuild(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionCode + "";
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 读取assets下的txt文件，返回utf-8 String
     *
     * @param context
     * @param fileName 不包括后缀
     * @return
     */
    public static String readAssetsTxt(Context context, String fileName) {
        try {
            //Return an AssetManager instance for your application's package
            InputStream is = context.getAssets().open(fileName + ".txt");
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert the buffer into a string.
            String text = new String(buffer, "utf-8");
            // Finally stick the string into the text view.
            return text;
        } catch (IOException e) {
            // Should never happen!
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
        return "{\n" +
                "    \"code\": \"300\",\n" +
                "    \"message\": \"读取错误\"\n" +
                "    \n" +
                " }";
    }


    /**
     * 是否使用代理(WiFi状态下的,避免被抓包)
     */
    public static boolean isWifiProxy(Context context) {
        final boolean isIcsOrLater = true;
        String proxyAddress;
        int proxyPort;
        if (isIcsOrLater) {
            proxyAddress = System.getProperty("http.proxyHost");
            String portstr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portstr != null ? portstr : "-1"));
            System.out.println(proxyAddress + "~");
            System.out.println("port = " + proxyPort);
        } else {
            proxyAddress = android.net.Proxy.getHost(context);
            proxyPort = android.net.Proxy.getPort(context);
            Log.e("address = ", proxyAddress + "~");
            Log.e("port = ", proxyPort + "~");
        }
        return (!TextUtils.isEmpty(proxyAddress)) && (proxyPort != -1);
    }


    /**
     * 是否正在使用VPN
     */
    public static boolean isVpnConnected() {
        try {
            Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
            if (niList != null) {
                for (NetworkInterface intf : Collections.list(niList)) {
                    if (!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
                        return true;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取任沃行manifest.xml中的appid
     * 获取钉钉分享appId
     * 获取微信分享appId
     *
     * @param context
     * @return
     */
    public static String getMetaDataString(Context context, String metaDataName) {
        String localAppId = "";
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            localAppId = appInfo.metaData.getString(metaDataName);
        } catch (PackageManager.NameNotFoundException var7) {
            var7.printStackTrace();
        }
        return localAppId;
    }

    /**
     * 获取省份编码id
     *
     * @param context
     * @return
     */
    public static int getMetaDataId(Context context, String metaDataName) {
        int provID = 999;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            provID = appInfo.metaData.getInt("ProvId");
        } catch (PackageManager.NameNotFoundException var7) {
            var7.printStackTrace();
        }
        return provID;
    }

    private static String LOG_TAG = "==";

    public static boolean isDeviceRooted() {
        if (checkDeviceDebuggable()) {
            return true;
        }//check buildTags
        if (checkSuperuserApk()) {
            return true;
        }//Superuser.apk
        //if (checkRootPathSU()){return true;}//find su in some path
        //if (checkRootWhichSU()){return true;}//find su use 'which'
        if (checkBusybox()) {
            return true;
        }//find su use 'which'
        if (checkAccessRootData()) {
            return true;
        }//find su use 'which'
        if (checkGetRootAuth()) {
            return true;
        }//exec su

        return false;
    }

    public static boolean checkDeviceDebuggable() {
        String buildTags = Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            Log.i(LOG_TAG, "buildTags=" + buildTags);
            return true;
        }
        return false;
    }

    public static boolean checkSuperuserApk() {
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                Log.i(LOG_TAG, "/system/app/Superuser.apk exist");
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean checkRootPathSU() {
        File f = null;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists()) {
                    Log.i(LOG_TAG, "find su in : " + kSuSearchPaths[i]);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkRootWhichSU() {
        String[] strCmd = new String[]{"/system/xbin/which", "su"};
        ArrayList<String> execResult = executeCommand(strCmd);
        if (execResult != null) {
            Log.i(LOG_TAG, "execResult=" + execResult.toString());
            return true;
        } else {
            Log.i(LOG_TAG, "execResult=null");
            return false;
        }
    }

    public static ArrayList<String> executeCommand(String[] shellCmd) {
        String line = null;
        ArrayList<String> fullResponse = new ArrayList<String>();
        Process localProcess = null;
        try {
            Log.i(LOG_TAG, "to shell exec which for find su :");
            localProcess = Runtime.getRuntime().exec(shellCmd);
        } catch (Exception e) {
            return null;
        }
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(localProcess.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
        try {
            while ((line = in.readLine()) != null) {
                Log.i(LOG_TAG, "–> Line received: " + line);
                fullResponse.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i(LOG_TAG, "–> Full response was: " + fullResponse);
        return fullResponse;
    }

    public static synchronized boolean checkGetRootAuth() {
        Process process = null;
        DataOutputStream os = null;
        try {
            Log.i(LOG_TAG, "to exec su");
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            Log.i(LOG_TAG, "exitValue=" + exitValue);
            if (exitValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.i(LOG_TAG, "Unexpected error - Here is what I know: "
                    + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized boolean checkAccessRootData() {
        try {
            Log.i(LOG_TAG, "to write /data");
            String fileContent = "test_ok";
            Boolean writeFlag = writeFile("/data/su_test", fileContent);
            if (writeFlag) {
                Log.i(LOG_TAG, "write ok");
            } else {
                Log.i(LOG_TAG, "write failed");
            }

            Log.i(LOG_TAG, "to read /data");
            String strRead = readFile("/data/su_test");
            Log.i(LOG_TAG, "strRead=" + strRead);
            if (fileContent.equals(strRead)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.i(LOG_TAG, "Unexpected error - Here is what I know: "
                    + e.getMessage());
            return false;
        }
    }

    //写文件
    public static Boolean writeFile(String fileName, String message) {
        try {
            FileOutputStream fout = new FileOutputStream(fileName);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
            fout.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //读文件
    public static String readFile(String fileName) {
        File file = new File(fileName);
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            while ((len = fis.read(bytes)) > 0) {
                bos.write(bytes, 0, len);
            }
            String result = new String(bos.toByteArray());
            Log.i(LOG_TAG, result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static synchronized boolean checkBusybox() {
        try {
            Log.i(LOG_TAG, "to exec busybox df");
            String[] strCmd = new String[]{"busybox", "df"};
            ArrayList<String> execResult = executeCommand(strCmd);
            if (execResult != null) {
                Log.i(LOG_TAG, "execResult=" + execResult.toString());
                return true;
            } else {
                Log.i(LOG_TAG, "execResult=null");
                return false;
            }
        } catch (Exception e) {
            Log.i(LOG_TAG, "Unexpected error - Here is what I know: "
                    + e.getMessage());
            return false;
        }
    }

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    //检测app通知是否展开
    public static boolean isNotificationEnabled(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String packageName = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class<?> appOpsClass = null;
        try {

            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method method = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
            Field notificationFieldValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (int) notificationFieldValue.get(Integer.class);
            return ((int) method.invoke(appOps, value, uid, packageName) == AppOpsManager.MODE_ALLOWED);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }
    //打开通知

    private static void toSetting(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));

        context.startActivity(localIntent);
    }


    //判断当前应用是否是debug状态
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }
}
