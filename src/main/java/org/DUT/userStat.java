package org.DUT;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
@Data
@AllArgsConstructor
public class userStat {
    private String id;  //标识用户，mac地址
    private String name;  //用户名
    private int message_num;  //发言数目
    private int score;  //积分
    private int level;  //等级
    private String title;  //称号
    private int win_game_num;
    private int game_num;
    private List<String> reward;
    private  boolean isActive;  //是否是活跃状态
    private  Long rank;  //积分排名
    private  Long activeNum;  //活跃人数
    public userStat(){
        //设置mac地址
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
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    id=sb.toString();
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
