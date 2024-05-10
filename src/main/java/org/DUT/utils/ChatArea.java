package org.DUT.utils;

import javax.swing.*;

public class ChatArea {
    private static volatile JTextPane chatArea;

    private ChatArea() {

    }

    public static JTextPane getInstance() {
            if (null == chatArea) {
                // 模拟在创建对象之前做一些准备工作
                synchronized (ChatArea.class) {
                    if(null == chatArea) {
                        chatArea = new JTextPane();
                    }
                }
            }
        return chatArea;
    }
}

