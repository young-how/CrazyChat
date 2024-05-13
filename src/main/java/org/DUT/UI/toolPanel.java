package org.DUT.UI;

import org.DUT.utils.Constants;
import scala.collection.immutable.Stream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class toolPanel extends JPanel{
    private JButton imageTool=new JButton("图片");  //消息发送数目
    private PicturePanel picturePanel;  //图片发送工具
    public toolPanel(){
        picturePanel= Constants.picturePanel;
        imageTool.setPreferredSize(new Dimension(60, 17));
        imageTool.setFont(new Font("微软雅黑", Font.PLAIN, 8));
        imageTool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(picturePanel!=null){
                    picturePanel.switchWin();
                }
            }
        });  //添加点击事件
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setPreferredSize(new Dimension(getPreferredSize().width, 13));
        add(imageTool);

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
