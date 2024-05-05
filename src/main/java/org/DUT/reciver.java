package org.DUT;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class reciver extends Thread{
    //private JTextArea messageWin;  //旧版本
    private JTextPane messageWin;
    private String ip;
    private String port;
    private String topic_name;
    private String group_id;
    private String user_id;  //用户id
    private String id=UUID.randomUUID().toString();  //ID号
    private KafkaConsumer<String, String> kafkaConsumer; //消费者
    private HashSet<String> control_order=new HashSet<>();  //控制命令
    private String Date= LocalDate.now().toString();  //今天的日期
    private Style userStyle;  //用户文字主题
    private Style sysStyle;  //用户文字主题
    public reciver(Object win,Properties init_param){
        ip=init_param.getProperty("server_ip");
        port=init_param.getProperty("server_port");
        topic_name=init_param.getProperty("topic");
        messageWin=(JTextPane)win;
        group_id=topic_name+id;
        user_id=init_param.getProperty("user_id");
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ip+":"+port);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, group_id);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        kafkaConsumer = new KafkaConsumer<>(properties);
        //加入控制台指令
        control_order.add("/clean");
        //设置用户文字可视化主题
        userStyle= messageWin.addStyle("userStyle", null);
        StyleConstants.setForeground(userStyle, Color.BLACK); // 设置文本颜色
        StyleConstants.setFontSize(userStyle, 12); // 设置字体大小
        StyleConstants.setBold(userStyle, false); // 设置文本加粗
        //设置系统消息可视化主题
        sysStyle= messageWin.addStyle("userStyle", null);
        StyleConstants.setForeground(sysStyle, Color.RED); // 设置文本颜色
        StyleConstants.setFontSize(sysStyle, 16); // 设置字体大小
        StyleConstants.setBold(sysStyle, true); // 设置文本加粗
    }
    /*
     * @param message:
    	 * @param style:
      * @return void
     * @author younghow
     * @description 为文本框插入指定样式的文本
     * @date younghow younghow
     */
    private void insertMessage(String message, Style style) {
        StyledDocument doc=messageWin.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), message, style);
        }
        catch (BadLocationException e){
            e.printStackTrace();
        }
    }
    //对控制命令进行处理
    public String control_order_process(String order){
        if(order.contains("/clean")){
            String content=messageWin.getText();  //保存消息
            appendMessage(content);   //保存消息
            //清屏操作
            messageWin.setText("");
            //将内容保存到文件中
        }
        else if(order.contains("/toUser:")){
            //发送给特定的用户
            String regex_userid="/toUser:(\\S+)@@";
            Pattern pattern=Pattern.compile(regex_userid);
            Matcher m=pattern.matcher(order);
            if(m.find()){
                String usr_id=m.group(1);  //提取出专门给该用户
                if(usr_id.equals(user_id)){
                    //如果提取的消息和本机id一致，则保留消息，否则丢弃
                    String regex_info="@@(.*)&&";  //系统消息的模板
                    Pattern pattern_info=Pattern.compile(regex_info);  //匹配系统消息
                    Matcher info=pattern_info.matcher(order);
                    if(info.find()){
                        //用红色标记高亮显示
                        String usr_info=info.group(1);  //提取出专门给该用户
                        insertMessage(usr_info+"\n",sysStyle);
                    }

                }

            }
            order=""; //丢弃消息
        }
        return order;
    }
    public void appendMessage(String text){
        String filename=Date+".txt";
        //追加消息
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            // 追加内容到文件
            writer.write(text);
            writer.newLine(); // 写入新行（可选）
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // 滚动 JTextArea 到底部
    private static void scrollTextAreaToBottom(JTextArea textArea) {
        textArea.setCaretPosition(textArea.getDocument().getLength());
//        if(textArea.getDocument().getLength()-textArea.getCaretPosition()<20){
//            textArea.setCaretPosition(textArea.getDocument().getLength());
//        }
        //
        // textArea.getCaretPosition();
    }
    /*
     * @param textPane:
      * @return void
     * @author younghow
     * @description 将JTextPane滑动到底部
     * @date younghow younghow
     */
    private void scrollJTextPaneToBottom(JTextPane textPane) {
        int height = textPane.getPreferredSize().height;
        textPane.setCaretPosition(height);
        try {
            Rectangle caretRect = textPane.modelToView(height);
            if (caretRect != null) {
                caretRect.height = textPane.getSize().height;
                textPane.scrollRectToVisible(caretRect);
            }
        }
        catch (BadLocationException e){
            e.printStackTrace();
        }


    }
    public void run(){
        String info;
        kafkaConsumer.subscribe(Arrays.asList(topic_name));
        while (true) {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(2));
            for (ConsumerRecord<String, String> record : consumerRecords) {
                try {
                    info=record.value();
                    info=control_order_process(info);
                    if(!info.equals("")){
                        //messageWin.append(info);
                        insertMessage(info,userStyle);
                    }
                    //scrollTextAreaToBottom(messageWin);   //自动下拉到最下方
                    //scrollJTextPaneToBottom(messageWin);

                    Thread.sleep(20); // 线程休眠1秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
