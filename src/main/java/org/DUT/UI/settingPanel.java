package org.DUT.UI;

import org.DUT.example.ConfigEditor;
import org.DUT.utils.Constants;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
设置栏
 */
public class settingPanel extends JFrame {
    private Properties properties;
    private JPanel panel;
    private JButton saveButton;
    private boolean isVisible=false;
    private ThreadPoolExecutor executor=new ThreadPoolExecutor(2, 3, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(5));

    public settingPanel() {
        setTitle("Config Properties Editor");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        properties = new Properties();
        panel = new JPanel(new GridBagLayout());
        saveButton = new JButton("Save");

        loadProperties();
        createUI();

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProperties();
            }
        });

        add(new JScrollPane(panel), BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //添加布局
        //setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        //参考主任务框体进行设置
        setSize(Constants.WIDTH_imageWin, Constants.HEIGHT_imageWin);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // 将窗体设置为半透明
        setUndecorated(true); // 隐藏边框
        setBackground(new Color(255, 255, 255, 0)); // 设置背景颜色为半透明黑色
        setOpacity((float)0.8);
        //设置窗口始终置顶
        setAlwaysOnTop(true);
        // 设置窗体位置为右下角
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //根据系统设置摆放位置
        setLocation(screenSize.width - getWidth()+Constants.LOCATION_X_ADD-Constants.WIDTH, screenSize.height - getHeight()+Constants.LOCATION_Y_ADD);
        setVisible(isVisible);
    }
    /*
    切换显示情况
     */
    public void switchWin(){
        if(isVisible){
            isVisible=false;
            this.setVisible(isVisible);
        }
        else{
            isVisible=true;
            this.setVisible(isVisible);
        }
    }
    private void loadProperties() {
        try (InputStream input = new FileInputStream("config.properties")) {
            if (input == null) {
                JOptionPane.showMessageDialog(this, "Sorry, unable to find config.properties", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void createUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;
        for (String key : properties.stringPropertyNames()) {
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(new JLabel(key), gbc);

            gbc.gridx = 1;
            JTextField valueField = new JTextField(properties.getProperty(key), 20);
            valueField.setName(key);
            panel.add(valueField, gbc);
            row++;
        }
    }

    private void saveProperties() {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JTextField) {
                JTextField field = (JTextField) comp;
                String key = field.getName();
                String value = field.getText();
                properties.setProperty(key, value);
            }
        }

        try (OutputStream output = new FileOutputStream(getClass().getClassLoader().getResource("config.properties").getPath())) {
            properties.store(output, null);
            JOptionPane.showMessageDialog(this, "Properties saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving properties.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void setWinVisible(boolean flag){
        isVisible=flag;
        this.setVisible(isVisible);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new settingPanel().setVisible(true);
            }
        });
    }
    private static volatile settingPanel settingpanel;
    public static settingPanel getInstance() {
        if (null == settingpanel) {
            // 模拟在创建对象之前做一些准备工作
            synchronized (PicturePanel.class) {
                if(null == settingpanel) {
                    settingpanel = new settingPanel();
                }
            }
        }
        return settingpanel;
    }
}
