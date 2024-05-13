package org.DUT.example;

import org.DUT.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ImageGallery extends JFrame {

    private JPanel galleryPanel;

    public ImageGallery() {
        setTitle("Image Gallery");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        galleryPanel = new JPanel(new GridLayout(0, 3)); // 3列的栅格布局
        JScrollPane scrollPane = new JScrollPane(galleryPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        //参考主任务框体进行设置
        setSize(Constants.WIDTH_imageWin, Constants.HEIGHT_imageWin);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // 将窗体设置为半透明
        setUndecorated(true); // 隐藏边框
        setBackground(new Color(255, 255, 255, 0)); // 设置背景颜色为半透明黑色
        setOpacity((float)0.8);
        //设置窗口始终置顶
        setAlwaysOnTop(true);
        // 设置窗体位置为右下角
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //根据系统设置摆放位置
        setLocation(screenSize.width - getWidth()+Constants.LOCATION_X_ADD-Constants.WIDTH, screenSize.height - getHeight()+Constants.LOCATION_Y_ADD);
        // 添加关闭按钮
//        JButton closeButton = new JButton("x");
//        closeButton.setPreferredSize(new Dimension(40, 17)); // 设置高度为 30
//        JButton minButton = new JButton("-");
//        minButton.setPreferredSize(new Dimension(40, 17)); // 设置高度为 30
//        JButton cleanButton = new JButton("~");  //清屏
//        cleanButton.setPreferredSize(new Dimension(40, 17)); // 设置高度为 30

        loadImagesFromFolder("C:\\Users\\young\\Pictures\\Screenshots");
    }

    private void loadImagesFromFolder(String folderPath) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        for (File file : files) {
            if (isImageFile(file)) {
                BufferedImage thumbnail = createThumbnail(file);
                ImageIcon icon = new ImageIcon(thumbnail);
                JLabel label = new JLabel(icon);
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // 在这里处理点击事件，例如向服务器发送请求并上传图片
                        System.out.println("Clicked on: " + file.getName());
                    }
                });
                galleryPanel.add(label);
            }
        }
    }

    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif");
    }

    private BufferedImage createThumbnail(File file) {
        try {
            BufferedImage originalImage = ImageIO.read(file);
            int width = 100; // 设置缩略图宽度
            int height = 100; // 设置缩略图高度
            BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = thumbnail.createGraphics();
            g2d.drawImage(originalImage, 0, 0, width, height, null);
            g2d.dispose();
            return thumbnail;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ImageGallery().setVisible(true);
        });
    }
}
