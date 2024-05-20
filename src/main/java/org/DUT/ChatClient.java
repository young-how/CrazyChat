package org.DUT;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.DUT.UI.*;
import org.DUT.utils.ChatArea;
import org.DUT.utils.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.DUT.Sender;
import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.ViewFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
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
    private PicturePanel picturePanel;  //图片发送工具包
    private pokerPanel poker;  //扑克牌
    private toolPanel toolpanel;   //工具栏
    private InputField inputField;
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
    private String Date= LocalDate.now().toString();  //今天的日期
    ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 3, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(5));

    public Properties readConfig() throws IOException {
        Properties props = new Properties();
        System.out.println("读取配置");
        //InputStream fis = this.getClass().getClassLoader().getResourceAsStream("config.properties");
        InputStream fis =new FileInputStream("config.properties");  //打包exe使用
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
        Constants.setIP(config.getProperty("server.CrazyChat_Server.ip"));
        Constants.setPort(config.getProperty("server.CrazyChat_Server.port"));
        if(config.getProperty("image.path")!=null){
            Constants.setMediaPath(config.getProperty("image.path"));
        }
        else{
            Constants.setMediaPath("/");
        }

        //Constants.setMediaPath(this.getClass().getClassLoader().getResource("pic").getPath()); //设置媒体路径
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
        setSize(Constants.WIDTH, Constants.HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // 将窗体设置为半透明
        setUndecorated(true); // 隐藏边框
        setBackground(new Color(255, 255, 255, 0)); // 设置背景颜色为半透明黑色
        setOpacity((float)Constants.Opacity);
        //设置窗口始终置顶
        setAlwaysOnTop(true);
        // 设置窗体位置为右下角
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //根据系统设置摆放位置
        setLocation(screenSize.width - getWidth()+Constants.LOCATION_X_ADD, screenSize.height - getHeight()+Constants.LOCATION_Y_ADD);
        // 添加关闭按钮
        JButton closeButton = new JButton("x");
        closeButton.setPreferredSize(new Dimension(40, 17)); // 设置高度为 30
        JButton minButton = new JButton("-");
        minButton.setPreferredSize(new Dimension(40, 17)); // 设置高度为 30
        JButton cleanButton = new JButton("~");  //清屏
        cleanButton.setPreferredSize(new Dimension(40, 17)); // 设置高度为 30
        closeButton.addActionListener(e -> {
            String context=chatArea.getText();  //获取文本信息
            rec.appendMessage(context);   //保存聊天框
            //save_config();
            dispose(); // 关闭窗口
            System.exit(0); // 结束程序
        });
        cleanButton.addActionListener(e -> {
            try {
                sendMessage("/clean");
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            }
        });
        minButton.addActionListener(e -> {
            if(minmum){
                setSize(Constants.WIDTH, Constants.HEIGHT);
                minmum=false;
                minButton.setText("-");
            }
            else{
                setSize(Constants.WIDTH, 25);
                minmum=true;
                minButton.setText("口");
                picturePanel.setWinVisible(false);  //缩小图片工具栏
                poker.setWinVisible(false); //隐藏德州扑克
                Constants.settingpanel.setWinVisible(false);
            }
            setLocation(screenSize.width - getWidth()+Constants.LOCATION_X_ADD, screenSize.height - getHeight()+Constants.LOCATION_Y_ADD);

        });


//        chatArea = new JTextArea();
//        chatArea.setEditable(false);
//        chatArea.setLineWrap(true); // 设置自动换行
        picturePanel=Constants.picturePanel;  //图片显示工具
        toolpanel=Constants.toolpanel;  //工具栏
        poker=Constants.poker;  //德州扑克
        //新版本聊天框
        chatArea= Constants.charArea;
        chatArea.setEditable(false); // 设置为不可编辑，以免用户输入
        Font defaultFont = new Font("Arial", Font.PLAIN, 12);
        chatArea.setFont(defaultFont);


        inputField = InputField.getInstance();
        name_input= new JTextField();
        name_input.setColumns(10);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            try {
                sendMessage();
            } catch (UnknownHostException ex) {
                throw new RuntimeException(ex);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        });
        //容器布局设置
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setPreferredSize(new Dimension(inputPanel.getPreferredSize().width, 25));
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
//        setLayout(new BorderLayout());
        //顶部容器栏
        JPanel titlePanel = TitlePanel.getInstance();
        titlePanel.add(name_input);
        titlePanel.add(cleanButton);
        titlePanel.add(minButton);
        titlePanel.add(closeButton);

        //状态显示栏
        statusPanel = StatusPanel.getInstance();
        //文本框显示区域
        JScrollPane ChatPanel=new JScrollPane(chatArea);
        ChatPanel.getVerticalScrollBar();
        ChatPanel.setPreferredSize(new Dimension(ChatPanel.getPreferredSize().width, 370));
        Constants.setChatPanel(ChatPanel);  //设置为全局变量

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
        add(toolpanel); //添加工具框
        add(inputPanel);
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

    public void sendMessage() throws UnknownHostException, JsonProcessingException {
        String message = inputField.getText();
        inputField.cur_to_zero();
        inputField.putMessage(message);
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
        inputField.cur_to_zero();
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

    private static volatile ChatClient chatClient;
    public static ChatClient getInstance() {
            if (null == chatClient) {
                // 模拟在创建对象之前做一些准备工作
                synchronized (ChatClient.class) {
                    if(null == chatClient) {
                        chatClient = new ChatClient();
                    }
                }
            }
        return chatClient;
    }
    /*
    对配置文件进行修改
     */
    public void save_config(){
        Properties props= null;
        try(OutputStream fos = new FileOutputStream("config.properties")){
            props = readConfig();
            System.out.println("保存配置");
            props.setProperty("image.path",Constants.mediaPath);   //保存媒体库路径
            props.store(fos, "Modified Configuration");
            fos.close();
            System.out.println("保存配置成功");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ChatClient win=ChatClient.getInstance();
                win.setVisible(true);
            } catch (RuntimeException e){
                System.out.println(e);
            }
        });
    }

}
