package org.DUT;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatClient extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JTextField name_input;
    private String username="null";
    private Sender sender;
    private reciver rec;
    private boolean minmum=false;
    private String ip;   //当前客户端ip地址
    private String server_ip="10.7.8.7";  //服务器ip地址
    private String server_port="9092";  //服务器端口
    private String topic="chatroom";  //消息话题

    ExecutorService executor = Executors.newFixedThreadPool(5);
    Runnable send_task; //消息发送任务
    private static int mouseX, mouseY;
    private static int windowX, windowY;
    public boolean In_bound(int x,int y){
        if(x>=0&&y>=0) return true;
        return false;
    }
    public ChatClient() throws UnknownHostException {
        setTitle("CrazyChat");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // 将窗体设置为半透明
        setUndecorated(true); // 隐藏边框
        setBackground(new Color(255, 255, 255, 0)); // 设置背景颜色为半透明黑色
        setOpacity((float)0.5);
        //设置窗口始终置顶
        setAlwaysOnTop(true);
        // 设置窗体位置为右下角
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width - getWidth(), screenSize.height - getHeight()-50);
        // 添加鼠标监听器，监听退出和植入
//        addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseEntered(MouseEvent e) {
//                // 鼠标进入窗体
//                setOpacity((float)0.5);
//            }
//
//            @Override
//            public void mouseExited(MouseEvent e) {
//                // 鼠标离开窗体
//                setOpacity((float)0.01);
//            }
//        });
//        addMouseMotionListener(new MouseMotionAdapter() {
//            @Override
//            public void mouseMoved(MouseEvent e) {
//                // 获取鼠标当前位置
//                Point mousePoint = e.getPoint();
//                // 判断鼠标是否在窗口范围内
//                if (In_bound(mousePoint.x,mousePoint.y)) {
//                    // 鼠标在范围内
//                    setOpacity((float)0.5);
//                    System.out.println("Mouse moved outside the window.");
//                }
//                else{
//                    setOpacity((float)0.01);
//                }
//            }
//        });
        // 添加关闭按钮
        JButton closeButton = new JButton("X");
        JButton minButton = new JButton("-");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 关闭窗口
                System.exit(0); // 结束程序
            }
        });
        minButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(minmum){
                    setSize(400, 300);
                    setLocation(screenSize.width - getWidth(), screenSize.height - getHeight()-50);
                    minmum=false;
                }
                else{
                    setSize(400, 50);
                    setLocation(screenSize.width - getWidth(), screenSize.height - getHeight()-50);
                    minmum=true;
                }
            }
        });


        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true); // 设置自动换行


        inputField = new JTextField();
        name_input= new JTextField();
        name_input.setColumns(13);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendMessage();
                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        inputField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    try {
                        sendMessage();
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        JPanel titlePanel = new JPanel(new FlowLayout());
        titlePanel.add(new JLabel("CrazyChat"));
        titlePanel.add(new JLabel("         name："));
        titlePanel.add(name_input);
        titlePanel.add(minButton);
        titlePanel.add(closeButton);
        add(titlePanel, BorderLayout.NORTH);
        //设置ip地址
        try {
            ip=InetAddress.getLocalHost().getHostAddress().toString();
        } catch (UnknownHostException e) {
            ip="0.0.0.0";  //未知ip
            throw new RuntimeException(e);
        }
        //发送器和接收器的属性
        Properties message_properties = new Properties();
        message_properties.setProperty("server_ip",server_ip);
        message_properties.setProperty("server_port",server_port);
        message_properties.setProperty("topic",topic);
        //添加发送器
        sender=new Sender(message_properties);
        rec=new reciver(chatArea,message_properties);
        rec.start();
        name_input.setText(ip);
    }

    private void sendMessage() throws UnknownHostException {
        String message = inputField.getText();
        String name = name_input.getText();
        if(name==""){
            InetAddress localHost = InetAddress.getLocalHost();
            sender.setUserName(localHost.getHostAddress());  //将ip地址设置为用户名
        }
        else{
            sender.setUserName(name);
        }
        if(message.length()==0) return;
        if (!message.isEmpty()) {
            //chatArea.append(message + "\n");
            String message_info=message+ "\n";
            sender.send(message_info);
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new ChatClient().setVisible(true);
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public JTextArea getChatArea() {
        return chatArea;
    }

    public void setChatArea(JTextArea chatArea) {
        this.chatArea = chatArea;
    }

    public JTextField getInputField() {
        return inputField;
    }

    public void setInputField(JTextField inputField) {
        this.inputField = inputField;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public reciver getRec() {
        return rec;
    }

    public void setRec(reciver rec) {
        this.rec = rec;
    }
}
