package org.DUT.utils;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;

public class Constants {
    public static String USER_STYLE = "userStyle";  //用户文字主题
    public static String SYS_STYLE = "sysStyle";  //用户文字主题
    public static String SECRE_TSTYLE = "secretStyle";  //用户文字主题
    public static String SYS_TALK = "SystemManager:";
    public static String SYS_ERROR = "系统消息：获取与服务器的初始连接时发生异常，请联系管理员或更新软件！！！";
    public static JTextPane charArea;

    static {
        charArea = ChatArea.getInstance();
        //设置用户文字可视化主题
        Style USERSTYLE= charArea.addStyle(USER_STYLE, null);
        StyleConstants.setForeground(USERSTYLE, Color.BLACK); // 设置文本颜色
        StyleConstants.setFontSize(USERSTYLE, 12); // 设置字体大小
        StyleConstants.setBold(USERSTYLE, false); // 设置文本加粗
        //设置私密消息
        Style SECRETSTYLE= charArea.addStyle(SECRE_TSTYLE, null);
        StyleConstants.setForeground(SECRETSTYLE, Color.RED); // 设置文本颜色
        StyleConstants.setFontSize(SECRETSTYLE, 12); // 设置字体大小
        StyleConstants.setBold(SECRETSTYLE, true); // 设置文本加粗
        //设置系统消息可视化主题
        Style SYSSTYLE= charArea.addStyle(SYS_STYLE, null);
        StyleConstants.setForeground(SYSSTYLE, Color.BLUE); // 设置文本颜色
        StyleConstants.setFontSize(SYSSTYLE, 12); // 设置字体大小
        StyleConstants.setBold(SYSSTYLE, true); // 设置文本加粗
    }
}
