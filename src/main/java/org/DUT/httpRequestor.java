package org.DUT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.DUT.UI.toolPanel;
import org.DUT.utils.Constants;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import javax.swing.*;
import java.awt.*;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Data
public class httpRequestor {
    private String ip;
    private String port;
    private JPanel statusPanel;  //状态栏组件
    private RestTemplate restTemplate = new RestTemplate();  //请求发送器
    private ObjectMapper objectMapper = new ObjectMapper();  //Json映射为class
    private userStat newest_user;   //user的最近状态
    private ThreadPoolExecutor pool =new ThreadPoolExecutor(5,10,1L, TimeUnit.SECONDS,new LinkedBlockingQueue<>(10));
    httpRequestor(Properties config,JPanel jp,userStat user) throws JsonProcessingException {
        ip= config.getProperty("server.CrazyChat_Server.ip");
        port= config.getProperty("server.CrazyChat_Server.port");
        statusPanel=jp;
        newest_user=user;
        request4status(user);  //向服务器发起请求
    }
    public void request4status(userStat user) throws JsonProcessingException {
        Runnable task=()->{
            newest_user=user;
            //向服务器发送请求，开一个线程等待
            try{
                HttpEntity<userStat> request=Class2Json(user);
                // 发送 POST 请求到服务器
                String serverUrl = "http://"+ip+":"+port+"/getState";
                String responseBody= restTemplate.postForObject(serverUrl, request, String.class);
                //JSONObject jsonObject = new JSONObject(responseBody);
                newest_user=objectMapper.readValue(responseBody, userStat.class);
                Constants.setUser(newest_user);
                updateStatusPanel(newest_user);
            }
            catch (RuntimeException e){
                System.out.println(e);
            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
        pool.execute(task);
    }
    public void sendMessage(userStat user) throws JsonProcessingException {
        Runnable task=()->{
            newest_user=user;
            //向服务器发送请求，开一个线程等待
            try{
                HttpEntity<userStat> request=Class2Json(user);
                // 发送 POST 请求到服务器
                String serverUrl = "http://"+ip+":"+port+"/sendMessage";
                String responseBody= restTemplate.postForObject(serverUrl, request, String.class);
                //JSONObject jsonObject = new JSONObject(responseBody);
                newest_user=objectMapper.readValue(responseBody, userStat.class);
                updateStatusPanel(newest_user);
            }
            catch (RuntimeException e){
                System.out.println(e);
            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
        pool.execute(task);
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
        ((JLabel)statusPanel.getComponents()[1]).setText(String.valueOf(user.getMessage_num()));  //设置消息数
        ((JLabel)statusPanel.getComponents()[3]).setText(String.valueOf(user.getScore()));  //设置消息数
        ((JLabel)statusPanel.getComponents()[5]).setText(String.valueOf(user.getLevel()));  //设置等级数
        ((JLabel)statusPanel.getComponents()[7]).setText(String.valueOf(user.getRank()));  //设置排名
        ((JLabel)statusPanel.getComponents()[9]).setText(String.valueOf(user.getActiveNum()));  //设置活跃人数
    }

    public static void main(String[] args) {

    }
}
