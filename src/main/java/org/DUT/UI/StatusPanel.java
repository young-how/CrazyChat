package org.DUT.UI;

import org.DUT.utils.ChatArea;

import javax.swing.*;
import java.awt.*;

public class StatusPanel extends JPanel {
    private JLabel send_num=new JLabel("-");  //消息发送数目
    private JLabel score=new JLabel("-");  //分数
    private JLabel level=new JLabel("-");  //等级
    private JLabel rank=new JLabel("-");   //积分排名
    private JLabel active_num=new JLabel("-");   //活跃人数
    public StatusPanel(){
        setLayout(new FlowLayout());
        //setBackground(new Color(128 ,138,135));
        //statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        setPreferredSize(new Dimension(getPreferredSize().width, 25));
        add(new JLabel("发言数: "));
        send_num.setPreferredSize(new Dimension(30, 17));
        add(send_num);
        add(new JLabel("积分: "));
        score.setPreferredSize(new Dimension(30, 17));
        add(score);
        add(new JLabel("等级: "));
        level.setPreferredSize(new Dimension(30, 17));
        add(level);
        add(new JLabel("排名: "));
        rank.setPreferredSize(new Dimension(30, 17));
        add(rank);
        add(new JLabel("活跃人数: "));
        active_num.setPreferredSize(new Dimension(30, 17));
        add(active_num);
    }
    private static volatile JPanel statusPanel;
    public static JPanel getInstance() {
            if (null == statusPanel) {
                // 模拟在创建对象之前做一些准备工作
                synchronized (StatusPanel.class) {
                    if(null == statusPanel) {
                        statusPanel = new StatusPanel();
                    }
                }
            }
        return statusPanel;
    }
}
