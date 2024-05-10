package org.DUT.UI;

import javax.swing.*;
import java.awt.*;

public class TitlePanel extends JPanel {
    public TitlePanel(){
        setLayout(new FlowLayout());
        //setBackground(new Color(128 ,138,135));
        // titlePanel.setBackground(new Color(128 ,138,135,0));
        setPreferredSize(new Dimension(getPreferredSize().width, 25));
        add(new JLabel("CrazyChat"));
        add(new JLabel("         Name："));
    }
    private static volatile TitlePanel titlePanel;
    public static TitlePanel getInstance() {
            if (null == titlePanel) {
                // 模拟在创建对象之前做一些准备工作
                synchronized (TitlePanel.class) {
                    if(null == titlePanel) {
                        titlePanel = new TitlePanel();
                    }
                }
            }
        return titlePanel;
    }
}
