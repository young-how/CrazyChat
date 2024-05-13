package org.DUT.utils;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;

public class ChatArea {
    private static volatile JTextPane chatArea;
    private static volatile HTMLEditorKit htmlEditorKit;  //HTML支持的工具包
    private static volatile HTMLDocument doc;  //HTML文本内容支持
    private ChatArea() {

    }

    public static JTextPane getInstance() {
            if (null == chatArea) {
                // 模拟在创建对象之前做一些准备工作
                synchronized (ChatArea.class) {
                    if(null == chatArea) {
                        chatArea = new JTextPane();
                        // 添加 HTML 文档支持
                        htmlEditorKit = new HTMLEditorKit();
                        chatArea.setEditorKit(htmlEditorKit);  //添加html工具包
                        doc = new HTMLDocument();
                        chatArea.setDocument(doc);
                    }
                }
            }
        return chatArea;
    }
    /*
    插入html文本信息
     */
    public static void appendHtmlString(String html){
        // 插入 HTML 文本
        try {
            htmlEditorKit.insertHTML(doc, doc.getLength(), html, 0, 0, null);
        } catch (BadLocationException | IOException e) {
            e.printStackTrace();
        }
    }
}

