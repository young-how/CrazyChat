package org.DUT;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class createTopic {
    public static void main(String[] args) {
        // Kafka 服务器地址
        String bootstrapServers = "10.7.8.7:9092";

        // 设置 Kafka 管理员客户端属性
        Properties properties = new Properties();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // 创建 Kafka 管理员客户端
        try (AdminClient adminClient = AdminClient.create(properties)) {
            // 创建话题的配置
            NewTopic newTopic = new NewTopic("test", 3, (short) 1);

            // 创建话题并等待创建完成
            adminClient.createTopics(Collections.singleton(newTopic)).all().get();
            System.out.println("Topic created successfully");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
