package org.DUT;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.swing.*;
import java.awt.*;

public class test extends JFrame {

    public test() {
        setTitle("Vertical Panels Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // 创建四个面板
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();

        // 设置面板的背景颜色
        panel1.setBackground(Color.RED);
        panel2.setBackground(Color.GREEN);
        panel3.setBackground(Color.BLUE);
        panel4.setBackground(Color.YELLOW);

        // 将面板添加到窗口中
        add(panel1);
        add(panel2);
        add(panel3);
        add(panel4);

        // 设置面板填充横向窗口
        panel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel4.setAlignmentX(Component.CENTER_ALIGNMENT);

        pack();
        setLocationRelativeTo(null); // 居中显示
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            test example = new test();
            example.setVisible(true);
        });
    }
}

