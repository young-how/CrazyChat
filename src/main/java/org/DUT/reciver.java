package org.DUT;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.swing.*;
import java.time.Duration;
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
            //清屏操作
            messageWin.setText("");
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
                    Thread.sleep(1000); // 线程休眠1秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
