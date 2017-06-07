package com.hzy.qqwry;

import java.io.UnsupportedEncodingException;

/**
 * QQWryAnd接口类库，通过该类调用jni接口
 *
 * @author HZY
 */
public class QQWryAnd {

    /**
     * 构造方法
     *
     * @param datPath dat文件所在路径
     */
    public QQWryAnd(String datPath) {
        this.jniOpen(datPath);
    }

    /**
     * @return QQwry版本信息
     */
    public String getVersion() {
        return getStr(jniGetVersionBytes());
    }

    /**
     * 获取IP地址
     *
     * @param ip String类型的IP字符串，形如"192.168.56.1"
     * @return ip归属地信息
     */
    public String getIpAddr(String ip) {
        return getStr(jniGetIpAddrBytes(ip));
    }

    /**
     * 获取IP范围信息
     *
     * @param ip
     * @return 该范围内的IP归属地相同
     */
    public String getIpRange(String ip) {
        return getStr(jniGetIpRangeBytes(ip));
    }

    /**
     * 获取IP记录数量
     *
     * @return
     */
    public int getIpCount() {
        return jniGetIpCount();
    }

    /**
     * 释放jni资源
     */
    public void close() {
        this.jniClose();
    }

    /**
     * 将byte数组转换成字符串
     *
     * @param array 输入
     * @return 字符串
     */
    private String getStr(byte[] array) {
        String str = "";
        try {
            str = new String(array, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    private native void jniOpen(String datPath);

    private native byte[] jniGetVersionBytes();

    private native byte[] jniGetIpAddrBytes(String ip);

    private native byte[] jniGetIpRangeBytes(String ip);

    private native int jniGetIpCount();

    private native void jniClose();

    static {
        System.loadLibrary("qqwry");
    }

}