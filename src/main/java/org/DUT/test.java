package org.DUT;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class test {
    public static void main(String[] args) {
        try {
            // 获取本地网络接口列表
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            // 遍历网络接口列表
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                // 排除回环接口和虚拟接口
                if (networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }

                // 获取硬件地址（MAC 地址）
                byte[] mac = networkInterface.getHardwareAddress();

                // 如果硬件地址不为空，打印出来
                if (mac != null) {
                    System.out.print("MAC Address: ");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    System.out.println(sb.toString());
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
