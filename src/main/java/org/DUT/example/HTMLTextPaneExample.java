package org.DUT.example;

import org.DUT.utils.ChatArea;
import org.DUT.utils.Constants;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLTextPaneExample {
    public static void main(String[] args) throws BadLocationException {

        String html="<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Adjust Image Max Width</title>\n" +
                "    <style>\n" +
                "        .image-container {\n" +
                "            max-width: 200px; /* 设置图片容器的最大宽度 */\n" +
                "        }\n" +
                "        .image-container img {\n" +
                "            width: 50%; /* 图片宽度设置为父元素宽度的百分比，以适应不同尺寸的父元素 */\n" +
                "            height: auto; /* 保持图片宽高比 */\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"image-container\">\n" +
                "        <img src=\"https://www.baidu.com/img/PCfb_5bf082d29588c07f842ccde3f97243ea.png\" alt=\"Example Image\">\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
        // 创建 JFrame
        JFrame frame = new JFrame("JTextPane Insert HTML Text Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // 创建 JTextPane
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false); // 设置为不可编辑

        // 创建 HTML 文档
        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        HTMLDocument doc = new HTMLDocument();
        textPane.setEditorKit(htmlEditorKit);
        textPane.setDocument(doc);

        // 要插入的 HTML 文本
        String htmlText = "<html><body><h1>Hello, HTML!</h1><p>This is some HTML text.</p></body></html>";

        // 插入 HTML 文本
        try {
            htmlEditorKit.insertHTML(doc, doc.getLength(), html, 0, 0, null);
            Document doc_text=textPane.getDocument();
            doc_text.insertString(doc.getLength(),"\n测试案例",ChatArea.getInstance().getStyle(Constants.SECRE_TSTYLE));
            htmlEditorKit.insertHTML(doc, doc.getLength(), html, 0, 0, null);
        } catch (BadLocationException | IOException e) {
            e.printStackTrace();
        }

        // 将 JTextPane 添加到 JFrame 中
        frame.getContentPane().add(new JScrollPane(textPane), BorderLayout.CENTER);

        // 显示窗口
        frame.setVisible(true);
    }
}

