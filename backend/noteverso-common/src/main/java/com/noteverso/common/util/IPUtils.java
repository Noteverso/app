package com.noteverso.common.util;

import com.noteverso.common.exceptions.BaseException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPUtils {
    public static String getHostAddress() {
        InetAddress inetAddress;

        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new BaseException("获取IP地址失败");
        }

        String ipAddress = inetAddress.getHostAddress();

        // 多个代理服务器的情况下，第一个为真实IP地址，多个IP逗号分隔
        // "***.***.***.***" IP 长度为 15
        if (null != ipAddress && ipAddress.length() > 15) {
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }

        return ipAddress;
    }

    public static Long getHostAddressWithLong() {
        String ipAddress = getHostAddress();
        String[] ipAddressArray = ipAddress.split("\\.");
        return Long.parseLong(ipAddressArray[3]);
    }

    public static void main(String args[]) {
        System.out.println(getHostAddress());
        System.out.println(getHostAddressWithLong());
        System.out.println(~(-1L << 17));
    }
}
