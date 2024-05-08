package org.DUT;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import org.DUT.utils.ChatArea;
import org.DUT.utils.Constants;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.Properties;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.concurrent.*;

@Data
public class ChatClient extends JFrame {
    //private JTextArea chatArea;  //老版本，不支持富文本
    private JTextPane chatArea;  //新版本，支持富文本和图片
    private JTextField inputField;
    private JTextField name_input;
    @Value("${message.initName}")
    private String username;
    private Sender sender;
    private reciver rec;
    private httpRequestor requestor;  //请求发送器
    private boolean minmum=false;
    private String ip;   //当前客户端ip地址
    private String server_ip;  //="192.168.0.12";  //服务器ip地址
    private String server_port;   //="9092";  //服务器端口
    private String topic;   //="chatroom";  //消息话题
    private userStat user=new userStat();  //当前客户端的用户信息
    // 状态显示的标签
    JPanel statusPanel;
    private JLabel send_num=new JLabel("-");  //消息发送数目
    private JLabel score=new JLabel("-");  //分数
    private JLabel level=new JLabel("-");  //等级
    private JLabel rank=new JLabel("-");   //积分排名
    private JLabel active_num=new JLabel("-");   //活跃人数
    private String Date= LocalDate.now().toString();  //今天的日期
    ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 3, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(5));

    public Properties readConfig() throws IOException {
        Properties props = new Properties();
        System.out.println("读取配置");
        InputStream fis = this.getClass().getClassLoader().getResourceAsStream("config.properties");
//        System.out.println(filepath);
//        InputStream fis = new FileInputStream(resourceAsStream);  //idea开发专用
        props.load(fis);
        fis.close();
        System.out.println("读取配置成功");
        return props;
    }
    public void postConstruct() {
        //可视化组件外的功能组件设置
        //设置ip地址
        try {
            ip=InetAddress.getLocalHost().getHostAddress().toString();
            System.out.println("设置ip");
        } catch (UnknownHostException e) {
            ip="0.0.0.0";  //未知ip
            throw new RuntimeException(e);
        }
        Properties config= null;
        try {
            config = readConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server_ip= config.getProperty("server.kafka.ip");
        server_port=config.getProperty("server.kafka.port");
        topic=config.getProperty("message.topic");
        username=config.getProperty("message.initName");
        System.out.println(server_ip);
        //发送器和接收器的属性
        Properties message_properties = new Properties();
        message_properties.setProperty("server_ip",server_ip);
        message_properties.setProperty("server_port",server_port);
        message_properties.setProperty("topic",topic);
        message_properties.setProperty("user_id",user.getId());
        //添加发送器
        sender=new Sender(message_properties);
        rec=new reciver(chatArea,message_properties);
        rec.start();
        name_input.setText(ip);
        //添加http发送器
        try{
            requestor=new httpRequestor(config,statusPanel,user);  //初始化
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        user=requestor.getNewest_user();  //获取请求器中得到的最新用户
    }

    public ChatClient(){
        setTitle("CrazyChat");
        setSize(400, 300);
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
        if(System.getProperty("os.name").startsWith("Mac")){
            setLocation(screenSize.width - getWidth()-20, screenSize.height - getHeight()-100);
        }else{
            setLocation(screenSize.width - getWidth(), screenSize.height - getHeight()-50);        }
         // 添加关闭按钮
        JButton closeButton = new JButton("*");
        closeButton.setPreferredSize(new Dimension(40, 17)); // 设置高度为 30
        JButton minButton = new JButton("-");
        minButton.setPreferredSize(new Dimension(40, 17)); // 设置高度为 30
        JButton cleanButton = new JButton("~");  //清屏
        cleanButton.setPreferredSize(new Dimension(40, 17)); // 设置高度为 30
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String context=chatArea.getText();  //获取文本信息
                rec.appendMessage(context);   //保存聊天框
                dispose(); // 关闭窗口
                System.exit(0); // 结束程序
            }
        });
        cleanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendMessage("/clean");
                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                }
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
                    setSize(400, 25);
                    setLocation(screenSize.width - getWidth(), screenSize.height - getHeight()-50);
                    minmum=true;
                }
            }
        });


//        chatArea = new JTextArea();
//        chatArea.setEditable(false);
//        chatArea.setLineWrap(true); // 设置自动换行

        //新版本聊天框
        chatArea= Constants.charArea;
        chatArea.setEditable(false); // 设置为不可编辑，以免用户输入
        Font defaultFont = new Font("Arial", Font.PLAIN, 12);
        chatArea.setFont(defaultFont);


        inputField = new JTextField();
        name_input= new JTextField();
        name_input.setColumns(10);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendMessage();
                } catch (UnknownHostException ex) {
                    throw new RuntimeException(ex);
                } catch (JsonProcessingException ex) {
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
                    } catch (JsonProcessingException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        //容器布局设置
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setPreferredSize(new Dimension(inputPanel.getPreferredSize().width, 25));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

//        setLayout(new BorderLayout());

        //顶部容器栏
        JPanel titlePanel = new JPanel(new FlowLayout());
        titlePanel.setPreferredSize(new Dimension(titlePanel.getPreferredSize().width, 25));
        titlePanel.add(new JLabel("CrazyChat"));
        titlePanel.add(new JLabel("         name："));
        titlePanel.add(name_input);
        titlePanel.add(cleanButton);
        titlePanel.add(minButton);
        titlePanel.add(closeButton);

        //状态显示栏
        statusPanel = new JPanel(new FlowLayout());
        //statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusPanel.setPreferredSize(new Dimension(statusPanel.getPreferredSize().width, 17));
        statusPanel.add(new JLabel("发言数: "));
        send_num.setPreferredSize(new Dimension(30, 17));
        statusPanel.add(send_num);
        statusPanel.add(new JLabel("积分: "));
        score.setPreferredSize(new Dimension(30, 17));
        statusPanel.add(score);
        statusPanel.add(new JLabel("等级: "));
        level.setPreferredSize(new Dimension(30, 17));
        statusPanel.add(level);
        statusPanel.add(new JLabel("排名: "));
        rank.setPreferredSize(new Dimension(30, 17));
        statusPanel.add(rank);
        statusPanel.add(new JLabel("活跃人数: "));
        active_num.setPreferredSize(new Dimension(30, 17));
        statusPanel.add(active_num);
        //文本框显示区域
        JScrollPane ChatPanel=new JScrollPane(chatArea);
        ChatPanel.setPreferredSize(new Dimension(ChatPanel.getPreferredSize().width, 370));

        //添加布局
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
//        add(titlePanel, BorderLayout.PAGE_START);
//        add(statusPanel,BorderLayout.PAGE_END);
//        add(ChatPanel, BorderLayout.CENTER);
//        add(inputPanel, BorderLayout.SOUTH);
         //设置面板填充横向窗口
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ChatPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // 将面板添加到窗口中
        add(titlePanel);
        add(statusPanel);
        add(ChatPanel);
        add(inputPanel);
        //System.out.println(11111);
        try{
            postConstruct();  //构造gui组件之外的配置
        }
        catch (RuntimeException e){
            //chatArea.append("系统消息：获取与服务器的初始连接时发生异常，请联系管理员或更新软件！！！");
            appendMessage(Constants.SYS_ERROR,chatArea.getStyle(Constants.SYS_STYLE));
        }
    }
    private void appendMessage(String message,Style style) {
        StyledDocument doc=chatArea.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), message, style);
        }
        catch (BadLocationException e){
            e.printStackTrace();
        }
    }

    private void sendMessage() throws UnknownHostException, JsonProcessingException {
        String message = inputField.getText();
        //判别是否可能有特殊字符
        if(message.contains("#")){
            sender.send(message,user);
        }
//        if(message.contains("@")){
//            //仅服务器可见的发言
//            sender.send(message,user);
//            return;
//        }
        String name = name_input.getText();
        if(name==""){
            InetAddress localHost = InetAddress.getLocalHost();
            sender.setUserName(localHost.getHostAddress());  //将ip地址设置为用户名
        }
        else{
            user.setName(name);
            sender.setUserName(name);
        }
        if(message.length()==0) return;
        if (!message.isEmpty()) {
            //chatArea.append(message + "\n");
            //appendMessage(message + "\n",userStyle);
            String message_info=message+ "\n";
            sender.send(message_info);
            inputField.setText("");
        }
        Runnable httpTask=()->
        {
            try {
                requestor.sendMessage(user); //向服务器发送请求
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            user=requestor.getNewest_user();  //获取最新的对象
        };
        pool.execute(httpTask);  //并发请求服务器
        repaint();
    }
    //带控制信息的信息发送
    private void sendMessage(String control_order) throws UnknownHostException {
        String name = name_input.getText();
        if(name==""){
            InetAddress localHost = InetAddress.getLocalHost();
            sender.setUserName(localHost.getHostAddress());  //将ip地址设置为用户名
        }
        else{
            sender.setUserName(name);
        }
        String message_info=control_order;
        sender.send(message_info);
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    ChatClient win=new ChatClient();
                    win.setVisible(true);
                } catch (RuntimeException e){
                    System.out.println(e);
                }
            }
        });
    }

}
