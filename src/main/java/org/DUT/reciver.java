package org.DUT;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class reciver extends Thread{
    private JTextArea messageWin;
    private String ip;
    private String port;
    private String topic_name;
    private String group_id;
    private String id=UUID.randomUUID().toString();  //ID号
    private KafkaConsumer<String, String> kafkaConsumer; //消费者
    private HashSet<String> control_order=new HashSet<>();  //控制命令
    private String Date= LocalDate.now().toString();  //今天的日期
    public reciver(JTextArea win,Properties init_param){
        ip=init_param.getProperty("server_ip");
        port=init_param.getProperty("server_port");
        topic_name=init_param.getProperty("topic");
        messageWin=win;
        group_id=topic_name+id;
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ip+":"+port);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, group_id);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        kafkaConsumer = new KafkaConsumer<>(properties);
        //加入控制台指令
        control_order.add("/clean");
    }
    //对控制命令进行处理
    public void control_order_process(String order){
        if(order.contains("/clean")){
            String content=messageWin.getText();  //保存消息
            appendMessage(content);   //保存消息
            //清屏操作
            messageWin.setText("");
            //将内容保存到文件中
        }
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
    public void run(){
        String info;
        kafkaConsumer.subscribe(Arrays.asList(topic_name));
        while (true) {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(2));
            for (ConsumerRecord<String, String> record : consumerRecords) {
                try {
                    info=record.value();
                    control_order_process(info);
                    messageWin.append(info);
                    Thread.sleep(200); // 线程休眠1秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
