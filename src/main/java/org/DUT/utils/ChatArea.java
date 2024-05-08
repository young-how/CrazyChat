package org.DUT.utils;

import javax.swing.*;

public class ChatArea {
    private static volatile JTextPane chatArea;

    private ChatArea() {

    }

    public static JTextPane getInstance() {
        try {
            if (null == chatArea) {
                // 模拟在创建对象之前做一些准备工作
                Thread.sleep(1000);
                synchronized (ChatArea.class) {
                    if(null == chatArea) {
                        chatArea = new JTextPane();
                    }
                }
            }
        } catch (InterruptedException e) {
            // TODO: handle exception
        }
        return chatArea;
    }
}

