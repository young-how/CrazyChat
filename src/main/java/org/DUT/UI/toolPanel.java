package org.DUT.UI;

import org.DUT.utils.Constants;
import scala.collection.immutable.Stream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class toolPanel extends JPanel{
    private JButton imageTool=new JButton("图片");  //消息发送数目
    private JButton mediaTool=new JButton("资源库");  //消息发送数目
    private JButton setTool=new JButton("设置");  //设置面板
    private JButton pokerTool=new JButton("德州扑克");  //设置面板
    private PicturePanel picturePanel;  //图片发送工具
    private settingPanel settingpanel;  //图片发送工具
    private pokerPanel poker;  //扑克牌
    public toolPanel(){
        picturePanel= Constants.picturePanel;
        settingpanel= Constants.settingpanel;
        poker= Constants.poker;
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setPreferredSize(new Dimension(getPreferredSize().width, 13));
        addButtonStyle(imageTool);  //添加按钮样式
        addButtonStyle(mediaTool);  //添加按钮样式
        addButtonStyle(setTool);  //添加按钮样式
        addButtonStyle(pokerTool);  //添加德州扑克
        imageTool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(picturePanel!=null){
                    picturePanel.switchWin();
                }else{
                    picturePanel=Constants.picturePanel;
                }
            }
        });  //添加点击事件
        setTool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(picturePanel!=null){
                    settingpanel.switchWin();
                } else{
                    settingpanel=Constants.settingpanel;
                }
            }
        });  //添加点击事件
        pokerTool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(poker!=null){
                    poker.switchWin();
                }
                else{
                    poker=Constants.poker;
                }
            }
        });  //添加点击事件

    }
    public void addButtonStyle(JButton button){
        button.setPreferredSize(new Dimension(60, 17));
        button.setFont(new Font("微软雅黑", Font.PLAIN, 8));
        add(button);
    }
    private static volatile toolPanel toolPanel;
    public static toolPanel getInstance() {
        if (null == toolPanel) {
            // 模拟在创建对象之前做一些准备工作
            synchronized (toolPanel.class) {
                if(null == toolPanel) {
                    toolPanel = new toolPanel();
                }
            }
        }
        return toolPanel;
    }
}
