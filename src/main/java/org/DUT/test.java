package org.DUT;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

    public static void main(String[] args) {
        JFrame frame = new JFrame("TextPane Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextPane textPane = new JTextPane();
        textPane.setEditable(false); // 设置为不可编辑，以免用户输入

        // 启用自动换行
        textPane.setEditorKit(new MyEditorKit());

        // 添加一些文本
        appendToPane(textPane, "test。", Color.BLACK);

        frame.add(new JScrollPane(textPane), BorderLayout.CENTER);
        frame.setSize(400, 300);
        frame.setVisible(true);
    }

    private static void appendToPane(JTextPane textPane, String text, Color color) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);

        int len = textPane.getDocument().getLength();
        textPane.setCaretPosition(len);
        textPane.setCharacterAttributes(aset, false);
        textPane.replaceSelection(text);
    }

    static class MyEditorKit extends StyledEditorKit {
        public ViewFactory getViewFactory() {
            return new WrapColumnFactory();
        }
    }

    static class WrapColumnFactory implements ViewFactory {
        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null && kind.equals(AbstractDocument.ContentElementName)) {
                return new WrapLabelView(elem);
            } else if (kind != null && kind.equals(AbstractDocument.ParagraphElementName)) {
                return new ParagraphView(elem);
            } else if (kind != null && kind.equals(AbstractDocument.SectionElementName)) {
                return new BoxView(elem, View.Y_AXIS);
            } else if (kind != null && kind.equals(StyleConstants.ComponentElementName)) {
                return new ComponentView(elem);
            } else if (kind != null && kind.equals(StyleConstants.IconElementName)) {
                return new IconView(elem);
            }
            return new LabelView(elem);
        }
    }

    static class WrapLabelView extends LabelView {
        public WrapLabelView(Element elem) {
            super(elem);
        }

        public float getMinimumSpan(int axis) {
            switch (axis) {
                case View.X_AXIS:
                    return 0;
                case View.Y_AXIS:
                    return super.getMinimumSpan(axis);
                default:
                    throw new IllegalArgumentException("Invalid axis: " + axis);
            }
        }
    }
}

