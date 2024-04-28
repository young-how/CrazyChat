package org.DUT;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Sender extends Thread{
    private String ip;
    private String port;
    private String topic_name;
    private String Command_line;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;  //用户名
    private KafkaProducer<String, String> kafkaProducer; //生产者
    ExecutorService executor = Executors.newFixedThreadPool(5);
    public Sender(Properties init_param){
        ip=init_param.getProperty("server_ip");
        port=init_param.getProperty("server_port");
        topic_name=init_param.getProperty("topic");
        userName=ip;
        Command_line="Command_line";  //命令队列
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ip+":"+port);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProducer = new KafkaProducer<>(properties);
    }
    public void send(String info){
        Runnable send_task=()->{
            ProducerRecord<String, String> record = new ProducerRecord<>(topic_name,  userName+": "+info);
            kafkaProducer.send(record);
        };
        executor.execute(send_task);
    }
    public void send(String info,userStat user){
        //带状态的消息发送，用于给服务器发送指令
        Runnable send_task=()->{
            ProducerRecord<String, String> record = new ProducerRecord<>(Command_line,  user.getId()+":"+info);  //给服务器发送，根据id发送指令
            kafkaProducer.send(record);
        };
        executor.execute(send_task);
    }
}
