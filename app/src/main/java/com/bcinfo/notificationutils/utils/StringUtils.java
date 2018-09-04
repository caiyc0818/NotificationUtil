package com.bcinfo.notificationutils.utils;

import android.annotation.SuppressLint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeBlock;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <一句话功能简述> <功能详细描述>
 *
 * @author l00194296
 * @version [版本号, 2012-10-13]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public final class StringUtils {
    private final static int STRING_IP = 0;

    private final static int STRING_PORT = 1;

    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    /**
     * 判断字符串是否为空
     *
     * @param param
     * @return
     */
    public static final boolean isEmpty(String param) {
        return null == param || 0 == param.length() || "".equals(param.trim());
    }

    /***
     * 是否为字母
     *
     * @param str
     * @return
     */
    private static boolean isLetter(String str) {
        return str.matches("[A-Za-z]+");
    }

    /***
     * 是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        return str.matches("[0-9]+");
    }

    /**
     * 得到全拼或简拼
     *
     * @param strs 字符串数组
     * @param type 全拼还是简拼
     * @return
     */
    public String getString(String[] strs, int type) {
        String[] newStrs = new String[strs.length];
        int j = 0;
        for (int i = 0; i < strs.length; i++) {
            String firstLetter = strs[i].substring(0, 1);
            if (isLetter(firstLetter)) {
                // type=0 out jp
                if (type == 0) {
                    newStrs[j] = firstLetter;
                }
                // type=1 out qp
                else {
                    newStrs[j] = strs[i] + " ";
                }
                j++;
            }
        }
        StringBuffer sb = new StringBuffer();
        for (int k = 0; k < newStrs.length; k++) {
            if (newStrs[k] != null) {
                sb.append(newStrs[k]);
            }
        }
        return sb.toString();
    }

    public static String parseMoblie(String mobile) {
        if (null == mobile)
            return null;
        return mobile.replace("-", "");
    }

    /**
     * 获取IP地址 <功能详细描述>
     *
     * @param ipAndPort
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String parseIP(String ipAndPort) {
        return parseAddress(ipAndPort, STRING_IP);
    }

    /**
     * 获取端口号 <功能详细描述>
     *
     * @param ipAndPort
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String parsePort(String ipAndPort) {
        return parseAddress(ipAndPort, STRING_PORT);
    }

    /**
     * 解析Xml文件中的节点内容（只获取结点内数据） <功能详细描述>
     *
     * @param src  :源字符串
     * @param node :节点
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String parseNode(String src, String node) {
        if (StringUtils.isEmpty(src) || StringUtils.isEmpty(node)) {
            return "";
        }
        int begin = src.indexOf("<" + node + ">");
        int end = src.indexOf("</" + node + ">");

        if (begin == -1 || end == -1) {
            return "";
        }

        return src.substring(begin + node.length() + 2, end).trim();
    }

    public static String parseLastNode(String src, String node) {
        if (StringUtils.isEmpty(src) || StringUtils.isEmpty(node)) {
            return "";
        }
        int begin = src.lastIndexOf("<" + node + ">");
        int end = src.lastIndexOf("</" + node + ">");

        if (begin == -1 || end == -1) {
            return "";
        }

        return src.substring(begin + node.length() + 2, end).trim();
    }

    /**
     * 从XML中解析整个元素值 如这样一个xml <?xml version=\1.0\?> <contact id="123" age="23"
     * code="1"> <uri>sip:+8675558010098@10.0.2.15:10500</uri>
     * 从中解析出来的结果为：<contact id="123" age="23" code="1">
     *
     * @param src
     * @param element
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String parseElementWithAttr(String src, String element) {
        if (isEmpty(src) || isEmpty(element)) {
            return null;
        }

        int begin = src.indexOf("<" + element);
        int end = src.indexOf(">", begin);

        if (begin == -1 || end == -1 || end < begin) {
            return null;
        }

        return src.substring(begin, end + 1);
    }

    /**
     * 解析XML中的节点值，并转换为INT值 <功能详细描述>
     *
     * @param src
     * @param node
     * @return：若为Integer.MIN_VALUE，则表示转化失败
     * @see [类、类#方法、类#成员]
     */
    public static int parseInt(String src, String node) {
        String number = parseNode(src, node);
        int ret = Integer.MIN_VALUE;

        if (validateInteger(number)) {
            ret = Integer.valueOf(number);
        }

        return ret;
    }

    public static int parseLastInt(String src, String node) {
        String number = parseLastNode(src, node);
        int ret = Integer.MIN_VALUE;

        if (validateInteger(number)) {
            ret = Integer.valueOf(number);
        }

        return ret;
    }

    /**
     * 从字符串中解析属性(慎用) 从<contact id="123" age="23" code="1">中解析id等属性
     *
     * @param src
     * @param attribute
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String parseAttribute(String src, String attribute) {
        if (StringUtils.isEmpty(src) || StringUtils.isEmpty(attribute)) {
            return "";
        }

        int pos = -1;
        int start = -1;
        String ret = "";

        pos = src.indexOf(attribute);
        if (-1 != pos) {
            start = pos + attribute.length() + 2;

            pos = src.indexOf("\"", start);
            if (-1 != pos) {
                ret = src.substring(start, pos);
            }
        }

        return ret;
    }

    /**
     * 从字符串中解析属性，返回整数 <功能详细描述>
     *
     * @param src
     * @param attribute
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static int parseAttributeInt(String src, String attribute) {
        String number = parseAttribute(src, attribute);
        int ret = Integer.MIN_VALUE;

        if (validateInteger(number)) {
            ret = Integer.valueOf(number);
        }

        return ret;
    }

    /**
     * 解析UDP消息中的sno 消息为JSON编码
     *
     * @param body
     * @return
     */
    public static int parseUdpSno(String body) {
        int sno = -1;

        if (isEmpty(body)) {
            return sno;
        }

        String pattern = "\"sno\":";
        int pos = body.indexOf(pattern);
        if (pos == -1) {
            return sno;
        }

        pos += pattern.length();

        int end = body.indexOf(",", pos);
        if (end == -1) {
            end = body.indexOf("}", pos);

            if (end == -1) {
                return sno;
            }
        }

        String snoString = body.substring(pos, end);

        if (validateInteger(snoString)) {
            sno = Integer.valueOf(snoString);
        }

        return sno;
    }

    /**
     * 检测一个字符串是否全为数字组成 <功能详细描述>
     *
     * @param str
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean validateInteger(String str) {
        if (isEmpty(str)) {
            return false;
        }

        int sz = str.length();
        int i = 0;
        if (str.startsWith("-")) {
            i = 1;
        }
        for (; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static String getCDataString(String src) {
        if (StringUtils.isEmpty(src) || StringUtils.isEmpty("<![CDATA[")
                || StringUtils.isEmpty("]]")) {
            return null;
        }
        int begin = src.indexOf("<![CDATA[");
        int end = src.lastIndexOf("]]");
        if ((begin == -1) || (end == -1)) {
            return null;
        }
        String returnString = src.substring(begin + "<![CDATA[".length(), end)
                .trim();

        return returnString;
    }

    /**
     * 解析IP地址和端口的辅助函数 <功能详细描述>
     *
     * @param ipAndPort
     * @param flag
     * @return
     * @see [类、类#方法、类#成员]
     */
    private static String parseAddress(String ipAndPort, int flag) {
        if ((null == ipAndPort) || ("".equals(ipAndPort.trim()))) {
            return null;
        }

        int end = ipAndPort.indexOf(":");

        if (end <= 0) {
            return null;
        }

        String temp = null;

        switch (flag) {
            case STRING_IP:
                temp = ipAndPort.substring(0, end);
                break;

            case STRING_PORT:
                temp = ipAndPort.substring(end + 1);
                break;

            default:
                break;
        }

        return temp;
    }

    /**
     * 从流中读取字符串 <功能详细描述>
     *
     * @param inputStream
     * @return
     * @throws IOException
     * @see [类、类#方法、类#成员]
     */
    public static String getStringFromStream(InputStream inputStream) {
        if (null == inputStream) {
            return null;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                inputStream));
        String returnString = null;
        try {
            int b;
            char[] buf = new char[1024];
            StringBuffer sb = new StringBuffer();
            while ((b = br.read(buf)) != -1) {
                sb.append(buf, 0, b);
            }
            returnString = sb.toString();
            sb = null;
            buf = null;

            inputStream.close();
            br.close();
        } catch (IOException e) {
            return null;
        }

        return returnString;
    }

    /**
     * 从string中去除trimString <功能详细描述>
     *
     * @param srcString : 源字符串
     * @param subString ：要删除的字符串
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String subString(String srcString, String subString) {
        if (StringUtils.isEmpty(srcString) || StringUtils.isEmpty(subString)
                || subString.length() >= srcString.length()) {
            return null;
        }

        int start = srcString.indexOf(subString) + subString.length();
        return srcString.substring(start);
    }

    /**
     * 实现输入字数的字数的格式设定 <功能详细描述>
     *
     * @param count : 当前已输入的个数
     * @param total : 总数
     * @return: 返回拼接好的字符串
     * @see [类、类#方法、类#成员]
     */
    public static String countText(int count, int total) {
        StringBuffer str = new StringBuffer();
        str.append("(");
        str.append(String.valueOf(count));
        str.append("/");
        str.append(String.valueOf(total));
        str.append(")");
        return new String(str);
    }

    /**
     * 将字节数组转换为String
     *
     * @param b byte[]
     * @return String
     */
    public static String bytesToString(byte[] b) {
        StringBuffer result = new StringBuffer("");
        int length = b.length;
        for (int i = 0; i < length; i++) {
            result.append((char) (b[i] & 0xff));
        }
        return result.toString();
    }

    public static String toString(String str) {
        if (null == str || str.length() == 0) {
            return "";
        } else {
            str = replace(str, "\n", "");
            str = replace(str, "\r", "");
            str = replace(str, "\r\n", "");
            return str.trim();
        }
    }

    /**
     * 过滤指定字符串
     *
     * @param string
     * @param oldString
     * @param newString
     * @return
     */
    public static final String replace(String string, String oldString,
                                       String newString) {
        if (string == null) {
            return null;
        }

        if (newString == null) {
            return string;
        }
        int i = 0;
        if ((i = string.indexOf(oldString, i)) >= 0) {
            char string2[] = string.toCharArray();
            char newString2[] = newString.toCharArray();
            int oLength = oldString.length();
            StringBuffer buf = new StringBuffer(string2.length);
            buf.append(string2, 0, i).append(newString2);
            i += oLength;
            int j;
            for (j = i; (i = string.indexOf(oldString, i)) > 0; j = i) {
                buf.append(string2, j, i - j).append(newString2);
                i += oLength;
            }

            buf.append(string2, j, string2.length - j);
            return buf.toString();
        } else {
            return string;
        }
    }

    /**
     * 获取消息的id
     *
     * @return: 消息的id
     * @see getMessageID
     */
    public static String getMessageID() {
        String msgId = "UE";
        long time = System.currentTimeMillis();

        String temp = "";
        Random ran = new Random(time);

        do {
            temp = ran.nextInt(1000000) + "";
        } while (temp.length() < 6);

        msgId += time;
        msgId += temp;

        return msgId;
    }

    /**
     * 过滤特殊字符
     *
     * @param str
     * @return
     * @throws PatternSyntaxException
     * @see isSpecialChar
     */
    public static boolean isSpecialChar(String str)
            throws PatternSyntaxException {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        return Pattern.compile(regEx).matcher(str).find();
    }

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}");
        Matcher m = p.matcher(mobiles);
        return m.find();
    }

    public static String getNumberOperator(String phoneNumber) {
        String operator = "未知运营商";

        String expression = "^1(34[0-8]|(3[5-9]|47|5[0-2]|57[124]|5[89]|8[2378])\\d)\\d{7}$";
        String expression2 = "^1(3[0-2]|45|5[56]|8[56])\\d{8}$";
        String expression3 = "^1(33|53|8[09])\\d{8}$";

        CharSequence inputStr = phoneNumber;

        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);

        Pattern pattern2 = Pattern.compile(expression2);
        Matcher matcher2 = pattern2.matcher(inputStr);

        Pattern pattern3 = Pattern.compile(expression3);
        Matcher matcher3 = pattern3.matcher(inputStr);

        if (matcher.matches()) {
            operator = "中国移动";
        } else if (matcher2.matches()) {
            operator = "中国联通";
        } else if (matcher3.matches()) {
            operator = "中国电信";
        }

        return operator;
    }

    /**
     * 将String转化为数组 <功能详细描述>
     *
     * @param data
     * @return: 转化后的数组
     * @see [类、类#方法、类#成员]
     */
    public static String[] string2Array(String data) {
        String[] result = null;
        if (data != null) {
            result = data.split("&");
        }
        return result;
    }

    /**
     * 将数组转化为有指定分割副（$）的字符串 <功能详细描述>
     *
     * @param data
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String array2String(String[] data) {
        StringBuilder result = new StringBuilder();
        if (data != null) {
            for (int i = 0, n = data.length; i < n; i++) {
                result.append(data[i]);
                result.append("&");
            }
        } else {
            return null;
        }
        return result.toString();
    }

    /**
     * 将数组转化为有指定分割副（$）的字符串 <功能详细描述>
     *
     * @param data
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String arrayList2String(ArrayList<String> data) {
        StringBuilder result = new StringBuilder();
        if (data != null) {
            for (int i = 0, n = data.size(); i < n; i++) {
                result.append(data.get(i));
                result.append("&");
            }
        } else {
            return null;
        }
        return result.toString();
    }

    /**
     * 处理全角标点符号和字体字符
     *
     * @param hex : 源数据
     * @return
     * @see replaceQuanjiao
     */
    public static String replaceQuanjiao(String hex) {
        // 需要替换为空串的字符
        String regKey = "(\\\\f[0-9] )|(\\\\f[0-9])|(\\\\cf[0-9][0-9] )|(\\\\cf[0-9][0-9])|(\\\\cf[0-9] )|"
                + "(\\\\cf[0-9])|(\\\\fs[0-9][0-9] )|(\\\\fs[0-9][0-9])|(\\\\fs[0-9] )|(\\\\fs[0-9])|"
                + "(\\\\hightlight[0-9] )|(\\\\hightlight[0-9])|(\\\\ulnone)|(\\\\ul)|(\\\\i0)|(\\\\i)|(\\r\\n)|(\\\\ltrpar)|(\\\\lang2052)|(\\\\b0)|(\\\\b)|";

        Matcher matcher = Pattern.compile(regKey).matcher(hex);
        hex = matcher.replaceAll("");
        /**
         * 这边是需要替换的全角标点符号字符 这个全角标点替换 貌似是有问题的
         */
        hex = hex.replace("\\ldblquote", "“").replace("\\rdblquote", "”")
                .replace("\\lquote", "‘").replace("\\rquote", "’")
                .replace("\\emdash", "-").replace("\\line", "\r\n")
                .replace("\\par", "\r\n").replace("\\tab", "  ")
                .replace("\\{", "{").replace("\\}", "}");
        return hex;
    }

    /**
     * utf-8 转换成 unicode
     *
     * @param inStr
     * @return
     */
    public static String utf8ToUnicode(String inStr) {
        char[] myBuffer = inStr.toCharArray();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inStr.length(); i++) {
            UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
            if (ub == UnicodeBlock.BASIC_LATIN) {
                // 英文及数字等
                sb.append(myBuffer[i]);
            } else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                // 全角半角字符
                int j = (int) myBuffer[i] - 65248;
                sb.append((char) j);
            } else {
                // 汉字
                short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u" + hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * unicode 转换成 utf-8
     *
     * @param theString
     * @return
     */
    public static String unicodeToUtf8(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }

                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }

        return outBuffer.toString();
    }

    /**
     * 判断请求是否成功
     *
     * @param resultCode : 一个请求对应的响应的状态码
     * @return true:成功，fasle:失败
     * @see isSuccess
     */
    public static boolean isSuccess(String resultCode) {
        if (resultCode.equals("200") || resultCode.equals("201")
                || resultCode.equals("202")) {
            return true;
        }

        return false;
    }

    /**
     * 判断请求是否成功 <功能详细描述>
     *
     * @param resultCode
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isSuccess(int resultCode) {
        if ((200 == resultCode) || (201 == resultCode) || (202 == resultCode)) {
            return true;
        }

        return false;
    }

    /**
     * 返回响应结果 <功能详细描述>
     *
     * @param resultCode
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static int getResultCode(int resultCode) {
        int ret = -1;

        if (isSuccess(resultCode)) {
            ret = 0;
        } else {
            ret = -1;
        }

        return ret;
    }

    public static int getResultCode2(int resultCode) {
        int ret = -1;

        // 410 含敏感词
        if (resultCode == 410) {
            ret = 1;
        } else if (isSuccess(resultCode)) {
            ret = 0;
        } else {
            ret = -1;
        }

        return ret;
    }

    /**
     * 过滤输入条件 <功能详细描述>
     *
     * @param condition % 转义方式为：\% _ 转义方式为：\_ \ 转义方式为：\\
     * @return: 条件不合法，返回NULL
     * @see [类、类#方法、类#成员]
     */
    public static String filterQueryCondition(String condition) {
        String ret = "";

        /**
         * 将字符中的全角转为半角
         */
        ret = toDBC(condition);

        ret = ret.replace("\\", "\\\\\\");
        ret = ret.replaceAll("%", "\\\\%");
        ret = ret.replaceAll("_", "\\\\_");

        return ret;
    }

    /**
     * 全角转半角
     *
     * @param input
     * @return
     */
    private static String toDBC(String input) {
        if (isEmpty(input)) {
            return "";
        }

        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }
        String returnString = new String(c);

        return returnString;
    }

    /**
     * MD5加密 需要编码转换
     *
     * @param data
     * @return
     * @throws IOException
     */
    public static byte[] encryptMD5(String data) {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] temp = data.getBytes("UTF-8");
            bytes = md.digest(temp);
        } catch (GeneralSecurityException gse) {
            gse.printStackTrace();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }
        return bytes;
    }

    public static String MD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            return toHex(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHex(byte[] bytes) {

        final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i=0; i<bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }

    /**
     * 数字转换，保留设定的小数点位数
     *
     * @param s
     * @return
     */
    public static String formatDecimalFloat(float flo, int maximum) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(maximum);
        nf.setMinimumFractionDigits(maximum);
        return nf.format(flo);
    }

    public static String formatDecimalFloat(double flo, int maximum) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(maximum);
        nf.setMinimumFractionDigits(maximum);
        return nf.format(flo);
    }

    /**
     * 转换为大写
     *
     * @param bytes
     * @return
     */
    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();

        for (int i = 0; i < bytes.length; ++i) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }

        return sign.toString();
    }

    /**
     * 将参数进行UTF-8编码
     *
     * @param agentId
     * @return accountString
     */
    public static String encodeAccount2UTF8(String agentId) {
        // 将参数进行编码，agentId可以为中文，且计算签名时也是使用编码之后的
        String accountString = "";

        try {
            accountString = URLEncoder.encode(agentId, "UTF-8");
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }

        return accountString;
    }

    public static String formatToUrl(String str) {
        return str.replace("\"", "%22").replace("{", "%7B").replace("}", "%7D")
                .replace(":", "%3A").replace(",", "%2C").replace(" ", "%20")
                .replace("/", "%2F").replace("[", "%5B").replace("]", "%5D");
    }

    public static String paramFormatToUrl(String str) {
        return str.replace("\"", "%22").replace("{", "%7B").replace("}", "%7D")
                .replace(":", "%3A").replace(",", "%2C").replace(" ", "%20")
                .replace("/", "%2F").replace("[", "%5B").replace("]", "%5D")
                .replace("=", "%3D").replace("+", "%2B").replace("@", "%40")
                .replace("#", "%23").replace("&", "%26").replace("^", "%5e");
    }

    @SuppressLint("NewApi")
    public static Boolean matchIp(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        return false;
    }

    /**
     * 转字符串为十六进制编码
     *
     * @param s
     * @return
     */
    public static String toHexString(String s) {
        if (s == null) {
            return "";
        }

        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    // public static String formatString(String str) {
    // DecimalFormat df = new DecimalFormat("0.00");
    // return df.format(Float.parseFloat(str));
    // }
    //
    // public static String formatPercent(String str) {
    // NumberFormat nf = NumberFormat.getPercentInstance();
    // nf.setMaximumFractionDigits(2);
    // return nf.format(Float.parseFloat(str));
    // }

    /**
     * 数字转换，保留设定的小数点位数
     *
     * @param s
     * @return
     */
    // public static String formatDecimal(String str, int maximum) {
    // NumberFormat nf = NumberFormat.getNumberInstance();
    // nf.setMaximumFractionDigits(maximum);
    // nf.setMinimumFractionDigits(maximum);
    // return nf.format(Float.parseFloat(str));
    // }

    /***
     * 年卡有限期转换成progressBar进度条
     *
     * @param day
     * @param totalProgress
     * @return
     */
    public static int getProgress(int day, int totalProgress) {
        return day * totalProgress / 365;
    }

    /**
     * @param distance
     * @return
     * @Create at ：2014-10-22下午5:31:26
     * @author Hunk 805015788@qq.com
     * @Description：返回多少千米
     */
    public static String getDistance(int distance) {
        if (0 == distance % 1000) {
            return String.format("%d千米", distance / 1000);
        }
        return distance > 1000 ? String.format("%.2f千米", distance / 1000.0f)
                : String.format("%d米", distance);
    }

    /**
     * @param duration
     * @return
     * @Create at ：2014-10-22下午5:36:03
     * @author Hunk 805015788@qq.com
     * @Description：返回x小时x分钟
     */
    public static String getDuration(int duration) {
        int second = duration / 60 % 60;
        int hour = duration / 3600;
        if (0 == second) {
            return String.format("%d小时", hour);
        }
        return hour > 0 ? String.format("%d小时%d分钟", hour, second) : String
                .format("%d分钟", second);
    }

    /**
     * is null or its length is 0 or it is made by space
     * <p>
     * <pre>
     * isBlank(null) = true;
     * isBlank(&quot;&quot;) = true;
     * isBlank(&quot;  &quot;) = true;
     * isBlank(&quot;a&quot;) = false;
     * isBlank(&quot;a &quot;) = false;
     * isBlank(&quot; a&quot;) = false;
     * isBlank(&quot;a b&quot;) = false;
     * </pre>
     *
     * @param str
     * @return if string is null or its size is 0 or it is made by space, return
     * true, else return false.
     */
    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }

    /**
     * null string to empty string
     * <p>
     * <pre>
     * nullStrToEmpty(null) = &quot;&quot;;
     * nullStrToEmpty(&quot;&quot;) = &quot;&quot;;
     * nullStrToEmpty(&quot;aa&quot;) = &quot;aa&quot;;
     * </pre>
     *
     * @param str
     * @return
     */
    public static String nullStrToEmpty(String str) {
        return (str == null ? "" : str);
    }

    /**
     * capitalize first letter
     * <p>
     * <pre>
     * capitalizeFirstLetter(null)     =   null;
     * capitalizeFirstLetter("")       =   "";
     * capitalizeFirstLetter("2ab")    =   "2ab"
     * capitalizeFirstLetter("a")      =   "A"
     * capitalizeFirstLetter("ab")     =   "Ab"
     * capitalizeFirstLetter("Abc")    =   "Abc"
     * </pre>
     *
     * @param str
     * @return
     */
    public static String capitalizeFirstLetter(String str) {
        if (isEmpty(str)) {
            return str;
        }

        char c = str.charAt(0);
        return (!Character.isLetter(c) || Character.isUpperCase(c)) ? str
                : new StringBuilder(str.length())
                .append(Character.toUpperCase(c))
                .append(str.substring(1)).toString();
    }

    /**
     * encoded in utf-8
     * <p>
     * <pre>
     * utf8Encode(null)        =   null
     * utf8Encode("")          =   "";
     * utf8Encode("aa")        =   "aa";
     * utf8Encode("啊啊啊啊")   = "%E5%95%8A%E5%95%8A%E5%95%8A%E5%95%8A";
     * </pre>
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException if an error occurs
     */
    public static String utf8Encode(String str) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(
                        "UnsupportedEncodingException occurred. ", e);
            }
        }
        return str;
    }

    /**
     * encoded in utf-8, if exception, return defultReturn
     *
     * @param str
     * @param defultReturn
     * @return
     */
    public static String utf8Encode(String str, String defultReturn) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return defultReturn;
            }
        }
        return str;
    }

    /**
     * get innerHtml from href
     * <p>
     * <pre>
     * getHrefInnerHtml(null)                                  = ""
     * getHrefInnerHtml("")                                    = ""
     * getHrefInnerHtml("mp3")                                 = "mp3";
     * getHrefInnerHtml("&lt;a innerHtml&lt;/a&gt;")                    = "&lt;a innerHtml&lt;/a&gt;";
     * getHrefInnerHtml("&lt;a&gt;innerHtml&lt;/a&gt;")                    = "innerHtml";
     * getHrefInnerHtml("&lt;a&lt;a&gt;innerHtml&lt;/a&gt;")                    = "innerHtml";
     * getHrefInnerHtml("&lt;a href="baidu.com"&gt;innerHtml&lt;/a&gt;")               = "innerHtml";
     * getHrefInnerHtml("&lt;a href="baidu.com" title="baidu"&gt;innerHtml&lt;/a&gt;") = "innerHtml";
     * getHrefInnerHtml("   &lt;a&gt;innerHtml&lt;/a&gt;  ")                           = "innerHtml";
     * getHrefInnerHtml("&lt;a&gt;innerHtml&lt;/a&gt;&lt;/a&gt;")                      = "innerHtml";
     * getHrefInnerHtml("jack&lt;a&gt;innerHtml&lt;/a&gt;&lt;/a&gt;")                  = "innerHtml";
     * getHrefInnerHtml("&lt;a&gt;innerHtml1&lt;/a&gt;&lt;a&gt;innerHtml2&lt;/a&gt;")        = "innerHtml2";
     * </pre>
     *
     * @param href
     * @return <ul>
     * <li>if href is null, return ""</li>
     * <li>if not match regx, return source</li>
     * <li>return the last string that match regx</li>
     * </ul>
     */
    public static String getHrefInnerHtml(String href) {
        if (isEmpty(href)) {
            return "";
        }

        String hrefReg = ".*<[\\s]*a[\\s]*.*>(.+?)<[\\s]*/a[\\s]*>.*";
        Pattern hrefPattern = Pattern
                .compile(hrefReg, Pattern.CASE_INSENSITIVE);
        Matcher hrefMatcher = hrefPattern.matcher(href);
        if (hrefMatcher.matches()) {
            return hrefMatcher.group(1);
        }
        return href;
    }

    /**
     * process special char in html
     * <p>
     * <pre>
     * htmlEscapeCharsToString(null) = null;
     * htmlEscapeCharsToString("") = "";
     * htmlEscapeCharsToString("mp3") = "mp3";
     * htmlEscapeCharsToString("mp3&lt;") = "mp3<";
     * htmlEscapeCharsToString("mp3&gt;") = "mp3\>";
     * htmlEscapeCharsToString("mp3&amp;mp4") = "mp3&mp4";
     * htmlEscapeCharsToString("mp3&quot;mp4") = "mp3\"mp4";
     * htmlEscapeCharsToString("mp3&lt;&gt;&amp;&quot;mp4") = "mp3\<\>&\"mp4";
     * </pre>
     *
     * @param source
     * @return
     */
    public static String htmlEscapeCharsToString(String source) {
        return StringUtils.isEmpty(source) ? source : source
                .replaceAll("&lt;", "<").replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&").replaceAll("&quot;", "\"");
    }

    /**
     * transform half width char to full width char
     * <p>
     * <pre>
     * fullWidthToHalfWidth(null) = null;
     * fullWidthToHalfWidth("") = "";
     * fullWidthToHalfWidth(new String(new char[] {12288})) = " ";
     * fullWidthToHalfWidth("！＂＃＄％＆) = "!\"#$%&";
     * </pre>
     *
     * @param s
     * @return
     */
    public static String fullWidthToHalfWidth(String s) {
        if (isEmpty(s)) {
            return s;
        }

        char[] source = s.toCharArray();
        for (int i = 0; i < source.length; i++) {
            if (source[i] == 12288) {
                source[i] = ' ';
                // } else if (source[i] == 12290) {
                // source[i] = '.';
            } else if (source[i] >= 65281 && source[i] <= 65374) {
                source[i] = (char) (source[i] - 65248);
            } else {
                source[i] = source[i];
            }
        }
        return new String(source);
    }

    /**
     * transform full width char to half width char
     * <p>
     * <pre>
     * halfWidthToFullWidth(null) = null;
     * halfWidthToFullWidth("") = "";
     * halfWidthToFullWidth(" ") = new String(new char[] {12288});
     * halfWidthToFullWidth("!\"#$%&) = "！＂＃＄％＆";
     * </pre>
     *
     * @param s
     * @return
     */
    public static String halfWidthToFullWidth(String s) {
        if (isEmpty(s)) {
            return s;
        }

        char[] source = s.toCharArray();
        for (int i = 0; i < source.length; i++) {
            if (source[i] == ' ') {
                source[i] = (char) 12288;
                // } else if (source[i] == '.') {
                // source[i] = (char)12290;
            } else if (source[i] >= 33 && source[i] <= 126) {
                source[i] = (char) (source[i] + 65248);
            } else {
                source[i] = source[i];
            }
        }
        return new String(source);
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    /***
     * 判断是不是一个合法的号码
     *
     * @param str
     * @return
     */
    public static boolean isMobileNumber(String number) {
        return number.replace("-", "").matches("[0-9]+")
                && (number.length() == 7 || number.length() == 8 || number.length() == 9 || number.length() == 10 || number.length() == 11 || number.length() == 12 || number.length() == 13);
    }

    /**
     * 用户输入是否为手机号码的方法
     */
    public static boolean verifyIsPhone(String phone) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * 隐藏号码
     *
     * @param str
     * @return
     */
    public static String getSecretStr(String str) {
        if (isEmpty(str)) {
            return str;
        }
        if (str.length() >= 11) {
            //手机号码隐藏后七位
            if (StringUtils.verifyIsPhone(str)) {
                String strMiddle = str.substring(str.length() - 7, str.length());
                str = str.replace(strMiddle, "*******");
            } else {
                //固定号码隐藏后四位
                String strMiddle = str.substring(str.length() - 4, str.length());
                str = str.replace(strMiddle, "****");
            }
        } else {
            if (str.length() >= 7) {
                //固定号码隐藏后四位
                String strMiddle = str.substring(str.length() - 4, str.length());
                str = str.replace(strMiddle, "****");
            }
        }
        return str;
    }

    /**
     * 隐藏号码
     *
     * @param str
     * @return
     */
    public static String getSecretPhone(String str) {
        if (isEmpty(str)) {
            return str;
        }
        if (str.length() <= 7) {
            return str;
        }
        if (str.length() >= 11) {
            //手机号码隐藏后八位
            if (StringUtils.verifyIsPhone(str)) {
                String strMiddle = str.substring(str.length() - 8, str.length());
                str = str.replace(strMiddle, "********");
            } else {
                //固定号码隐藏后四位
                String strMiddle = str.substring(str.length() - 4, str.length());
                str = str.replace(strMiddle, "****");
            }
        }
        return str;
    }


    /**
     * 隐藏身份证号码
     *
     * @param idNo
     * @return
     */
    public static String getIdNo(String idNo) {
        if (isEmpty(idNo)) {
            return idNo;
        }
        if (idNo.length() < 18) {
            return idNo;
        }
        String newString = "";
        if (idNo.length() >= 18) {
            char[] cc = idNo.toCharArray();
            for (int i = 0; i < cc.length; i++) {
                if (i > 5 && i < cc.length - 4) {
                    newString += "*";
                } else {
                    newString += cc[i];
                }
            }
        }
        return newString;
    }

    /***
     * 护照号隐藏
     *
     * @param passportNo
     * @return
     */
    public static String getPassportNo(String passportNo) {
        if (isEmpty(passportNo)) {
            return passportNo;
        }
        if (passportNo.length() <= 4) {
            return passportNo;
        }
        String newString = "";
        char[] cc = passportNo.toCharArray();
        if (passportNo.length() > 4) {
            for (int i = 0; i < cc.length; i++) {
                if (i > 3 && i <= 7) {
                    newString += "*";
                } else {
                    newString += cc[i];
                }
            }
        }
        return newString;
    }


    /**
     * 转换字符串第一个字符为大写
     *
     * @param str String
     * @return String
     */
    public static String getStrByUpperFirstChar(String str) {
        try {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        } catch (Exception e) {
            return "";
        }

    }

    public static String getNumStr(String str) {
        String reg = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(str);
        return mat.replaceAll("");
    }

    /**
     * 过滤设置的特殊符号
     */
    private static String filterSymbol(String str) throws Exception {
        String regEx = "[`~!@#$%^&*()+=|{}:;\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        return Pattern.compile(regEx).matcher(str).replaceAll("").trim();
    }

    /**
     * 过滤设置的特殊符号
     */
    private static String filterLetter(String str) throws Exception {
        return str.replaceAll("[a-zA-Z]", "");
    }

    /**
     * 获取字符串中的数字
     */
    public static String getNumbers(String str) {
        try {
            return filterLetter(filterSymbol(getNumStr(str)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getNumStrOnly(String str) {
        String tempStr = "";
        if (str.contains("-")) {
            return "0";
        } else {
            for (int i = 0; i < str.length(); i++) {
                String temp = "" + str.charAt(i);
                if (isNumber(temp) || ".".equals(temp)) {
                    tempStr += temp;
                }
            }
            return tempStr;
        }
    }

    /**
     * 格式化float
     *
     * @param num    需要格式化的数字
     * @param format 保留几位小数形如".00"
     * @return 格式化后的字符串
     */
    public static String formatFloat(Float num, String format) {
        DecimalFormat decimalFormat;
        if (StringUtils.isEmpty(format))
            decimalFormat = new DecimalFormat("0.00");
        else
            decimalFormat = new DecimalFormat(format);
        return decimalFormat.format(num);
    }

}
