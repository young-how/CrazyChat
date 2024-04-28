package org.DUT;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Class2Json {
    public static void main(String[] args) throws Exception {
        // 创建一个 Java 对象
        userStat obj = new userStat();
        obj.setId("younghow");

        // 创建 ObjectMapper 对象
        ObjectMapper mapper = new ObjectMapper();

        // 将 Java 对象转换为 JSON 字符串
        String json = mapper.writeValueAsString(obj);

        // 打印 JSON 字符串
        System.out.println(json);
    }
}
