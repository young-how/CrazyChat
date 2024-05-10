package org.DUT.UI;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.DUT.ChatClient;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


public class InputField extends JTextField{
    private static volatile InputField inputField;
    private static List<String> messageList = new ArrayList<>();
    public static int cur_location = -1;
    private static final int MAX_LEN = 5;
    public static void getMessage(){
        int len = messageList.size();
        if(len==0) return;
        // 找到下一个元素的位置 rk 111
        String s = messageList.get(len - Math.abs(cur_location%len) - 1);
        InputField.getInstance().setText(s);
    }
    public void putMessage(String s){
        messageList.remove(s);
        int len = messageList.size();
        if(len == MAX_LEN){
            messageList.remove(0);
        }
        messageList.add(s);
    }
    public void cur_to_zero(){
        cur_location = -1;
    }
    public static InputField getInstance() {
            if (null == inputField) {
                // 模拟在创建对象之前做一些准备工作
                synchronized (TitlePanel.class) {
                    if(null == inputField) {
                        inputField = new InputField();
                        inputField.addKeyListener(new KeyListener() {
                            @Override
                            public void keyTyped(KeyEvent e) {

                            }

                            @Override
                            public void keyPressed(KeyEvent e) {
                                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                    try {
                                        ChatClient.getInstance().sendMessage();
                                    } catch (UnknownHostException | JsonProcessingException ex) {
                                        throw new RuntimeException(ex);
                                    }
                                }
                                if(e.getKeyCode() == KeyEvent.VK_UP){
                                    cur_location++;
                                    getMessage();
                                }
                                if(e.getKeyCode()==KeyEvent.VK_DOWN){
                                    cur_location--;
                                    getMessage();
                                }

                            }

                            @Override
                            public void keyReleased(KeyEvent e) {

                            }
                        });
                    }
                }
            }
        return inputField;
    }
}
