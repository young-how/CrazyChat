package org.DUT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import javax.swing.*;
import java.awt.*;
import java.util.Properties;

public class httpRequestor {
    private String ip;
    private String port;
    private JPanel statusPanel;  //状态栏组件
    private RestTemplate restTemplate = new RestTemplate();  //请求发送器
    private ObjectMapper objectMapper = new ObjectMapper();  //Json映射为class
    httpRequestor(){

    }
    private void sendMessage(userStat user) throws JsonProcessingException {
        //向服务器发送请求，开一个线程等待
        Thread sendThread=new Thread(
                ()->{
                    try{
                        HttpEntity<userStat> request=Class2Json(user);
                        // 发送 POST 请求到服务器
                        String serverUrl = ip+":"+port+"/sendMessage";
                        String responseBody= restTemplate.postForObject(serverUrl, request, String.class);
                        //JSONObject jsonObject = new JSONObject(responseBody);
                        userStat response_user=objectMapper.readValue(responseBody, userStat.class);
                        updateStatusPanel(response_user);
                    }
                    catch (RuntimeException e){
                        System.out.println(e);
                    } catch (JsonMappingException e) {
                        throw new RuntimeException(e);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        sendThread.start();  //启动线程
    }
    public HttpEntity<userStat> Class2Json(userStat user){
        // 将 User 对象封装成 JSON，并设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<userStat> request = new HttpEntity<>(user, headers);
        return request;
    }
    public void updateStatusPanel(userStat user){
        //更新状态栏
        for (Component component : statusPanel.getComponents()) {
            if (component.getName().equals("send_num")) {
                ((JLabel)component).setText(String.valueOf(user.getMessage_num()));
            }
            else if(component.getName().equals("score")){
                ((JLabel)component).setText(String.valueOf(user.getScore()));
            }
            else if(component.getName().equals("level")){
                ((JLabel)component).setText(String.valueOf(user.getLevel()));
            }
        }
    }
}
