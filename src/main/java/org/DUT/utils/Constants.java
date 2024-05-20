package org.DUT.utils;

import lombok.Data;
import org.DUT.UI.PicturePanel;
import org.DUT.UI.pokerPanel;
import org.DUT.UI.settingPanel;
import org.DUT.UI.toolPanel;
import org.DUT.pojo.configParam;
import org.DUT.userStat;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.InputStream;
import java.util.Properties;

public class Constants {
    public static volatile JScrollPane ChatPanel=null;  //聊天滑动栏
    public static volatile userStat user;  //用户信息
    public static volatile configParam cf=new configParam();  //配置文件
    public static String SERVER_IP;  //服务器IP
    public static String SERVER_PORT;  //服务器PORT
    public static String mediaPath="";  //媒体路径
    public static String USER_STYLE = "userStyle";  //用户文字主题
    public static String SYS_STYLE = "sysStyle";  //用户文字主题
    public static String SECRE_TSTYLE = "secretStyle";  //用户文字主题
    public static String SYS_TALK = "SystemManager:";
    public static String SYS_ERROR = "系统消息：获取与服务器的初始连接时发生异常，请联系管理员或更新软件！！！";
    public static JTextPane charArea;
    public static PicturePanel picturePanel;
    public  static toolPanel toolpanel;
    public static settingPanel settingpanel;
    public static pokerPanel poker;
    public static double Opacity=1.0;  //透明度

    public static int WIDTH = System.getProperty("os.name").startsWith("Mac")?450:400;
    public static int HEIGHT = System.getProperty("os.name").startsWith("Mac")?350:300;
    public static int WIDTH_imageWin = System.getProperty("os.name").startsWith("Mac")?450:350;
    public static int WIDTH_texasPoker = System.getProperty("os.name").startsWith("Mac")?450:500;
    public static int HEIGHT_imageWin = System.getProperty("os.name").startsWith("Mac")?350:300;
    public static int LOCATION_X_ADD = System.getProperty("os.name").startsWith("Mac")?-20:0;
    public static int LOCATION_Y_ADD = System.getProperty("os.name").startsWith("Mac")?-100:-50;
    public static int LOCATION_X_ADD_imageWin = System.getProperty("os.name").startsWith("Mac")?-20:0;
    public static int LOCATION_Y_ADD_imageWin = System.getProperty("os.name").startsWith("Mac")?-100:-50;

    static {
        user=new userStat();
        settingpanel=settingPanel.getInstance();
        charArea = ChatArea.getInstance();
        picturePanel=PicturePanel.getInstance();
        toolpanel=toolPanel.getInstance();
        poker=pokerPanel.getInstance();
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
        //读取配置文件
        Properties props = new Properties();
    }
    public static void setIP(String ip){
        SERVER_IP=ip;
    }
    public static void setPort(String port){
        SERVER_PORT=port;
    }
    public static void setMediaPath(String path){
        mediaPath=path;
    }
    public static void setChatPanel(JScrollPane pane){
         ChatPanel=pane;
    }
    public static void setUser(userStat usr){
        user=usr;
    }
}
