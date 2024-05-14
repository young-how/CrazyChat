package org.DUT;

import org.DUT.utils.ChatArea;
import org.DUT.utils.Constants;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class reciver extends Thread{
    //private JTextArea messageWin;  //旧版本
    private JTextPane messageWin;
    private StyledDocument doc;
    private String ip;
    private String port;
    private String topic_name;
    private String group_id;
    private String user_id;  //用户id
    private String id=UUID.randomUUID().toString();  //ID号
    private KafkaConsumer<String, String> kafkaConsumer; //消费者
    private HashSet<String> control_order=new HashSet<>();  //控制命令
    private String Date= LocalDate.now().toString();  //今天的日期
    private HashMap<String,Integer> insert_index=new HashMap<>();  //文本插入索引
    static ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 3, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(5));
    public reciver(Object win,Properties init_param){
        ip=init_param.getProperty("server_ip");
        port=init_param.getProperty("server_port");
        topic_name=init_param.getProperty("topic");
        messageWin=(JTextPane)win;
        doc= messageWin.getStyledDocument();  //获取文档类型
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
        //StyledDocument doc=messageWin.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), message, style);
        }
        catch (BadLocationException e){
            e.printStackTrace();
        }
    }
    private void insertMessage(String message, int idx,String id,Style style) {
        //带偏移量和id的消息插入
        //StyledDocument doc=messageWin.getStyledDocument();
        try {
            int str_len=message.length();  //消息的长度
            doc.insertString(idx, message, style);
            idx+=str_len;
            insert_index.put(id,idx);  //重构对应id的字符插入点
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
            //将待插入点的map也消除掉
            insert_index.clear();
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
                    Pattern pattern_info=Pattern.compile(regex_info, Pattern.DOTALL);  //匹配系统消息，包含换行字符
                    Matcher info=pattern_info.matcher(order);
                    if(info.find()){
                        //用红色标记高亮显示
                        String usr_info=info.group(1);  //提取出专门给该用户
                        //处理消息中的断点信息
                        if(usr_info.contains("/cutpoint_id:")){
                            //处理断点信息
                            String regex="/cutpoint_id:(.*)&&";  //提取id号码
                            Pattern pr=Pattern.compile(regex);
                            Matcher mr=pr.matcher(usr_info);  //匹配
                            if(mr.find()){
                                String content=messageWin.getText();  //保存消息
                                String content_id= mr.group(1);//截取到内容的id
                                String regex_content="@@(.*)/cutpoint_id:";
                                if(!insert_index.containsKey(content_id)){
                                    //没有匹配到插入点
                                    //StyledDocument doc = messageWin.getStyledDocument();
                                    int insert_ind=doc.getLength(); //最末端的标记
                                    insert_index.put(content_id,insert_ind);  //保存插入点的标记
                                }
                                Pattern p2=Pattern.compile(regex_content, Pattern.DOTALL);
                                Matcher m2=p2.matcher(order);  //匹配
                                if(m2.find()){
                                    //匹配到内容
                                    String info_content=m2.group(1);
                                    int idx=insert_index.get(content_id);  //获取插入点
                                    insertMessage(info_content,idx,content_id,messageWin.getStyle(Constants.SECRE_TSTYLE));  //在对应的插入点插入消息
                                }
                                appendMessage(content);   //保存消息
                            }
                            return "";//丢弃了原来的消息
                        }
                        insertMessage(usr_info+"\n",messageWin.getStyle(Constants.SECRE_TSTYLE));
                    }

                }

            }
            order=""; //丢弃消息
        }
        else if(order.contains("/WithHtmlContent:")){
            //包含html内容
            String[] re=order.split("/WithHtmlContent:");
            ChatArea.appendHtmlString(re[1]);
            scrollTextAreaToBottom();
            return "";
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
    private static void scrollTextAreaToBottom() {
        Runnable task=()->{
            try {
                sleep(200);
                int maximumValue = Constants.ChatPanel.getVerticalScrollBar().getMaximum();
                Constants.ChatPanel.getVerticalScrollBar().setValue(maximumValue);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        try{
            pool.execute(task);
        }
        catch (RejectedExecutionException e){
            e.printStackTrace();
        }

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
                    String infos=control_order_process(record.value());
                    if(!infos.equals("")){
                        //messageWin.append(info);
                        Style myStyle = infos.startsWith(Constants.SYS_TALK)?
                                messageWin.getStyle(Constants.SYS_STYLE):messageWin.getStyle(Constants.USER_STYLE);
                        insertMessage(infos,myStyle);
                        scrollTextAreaToBottom();  //如果没有处理特殊情况，则刷新
                    }
                    Thread.sleep(20); // 线程休眠1秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
