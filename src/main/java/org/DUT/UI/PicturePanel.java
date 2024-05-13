package org.DUT.UI;

import lombok.Data;
import org.DUT.httpRequestor;
import org.DUT.utils.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PicturePanel extends JFrame {

    private JPanel galleryPanel;
    private boolean isVisible=false;
    private RestTemplate restTemplate=new RestTemplate();
    private HttpHeaders headers=new HttpHeaders();
    private ThreadPoolExecutor executor=new ThreadPoolExecutor(2, 3, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(5));
    String folderPath="";  //图片存放的路径

    public PicturePanel() {
        setTitle("Image Gallery");
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
        //folderPath=this.getClass().getResource("").getPath()+"/../";
        loadImagesFromFolder();
        setVisible(isVisible);

    }

    private void loadImagesFromFolder() {
        folderPath=Constants.mediaPath;  //客户端媒体库路径
        // 创建 File 对象
        File directory = new File(folderPath);
        // 检查路径是否存在
        if (!directory.exists()) {
            // 路径不存在，创建路径
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("路径已创建：" + folderPath);
            } else {
                System.out.println("无法创建路径：" + folderPath);
            }
        } else {
            System.out.println("路径已经存在：" + folderPath);
        }
        Runnable task=()->{
            if(folderPath.equals("")) return;  //如果是空，退出
            File folder = new File(folderPath);
            File[] files = folder.listFiles();
            //galleryPanel.removeAll();
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
                            System.out.println(file.getAbsolutePath());
                            sendPic2Server(file);
                        }
                    });
                    galleryPanel.add(label);
                }
            }
            //galleryPanel.repaint();
        };
        executor.execute(task);

    }
    public void sendPic2Server(File file){
        //发送图片到服务器
        // 设置服务器地址和端口号
        String serverUrl = "http://"+Constants.SERVER_IP+":"+Constants.SERVER_PORT+"/sendFile";
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        // 创建请求体，包含文件内容
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));
        // 创建 HTTP 实体，包含请求头和请求体
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        // 发送 POST 请求
        ResponseEntity<String> response = restTemplate.exchange(serverUrl, HttpMethod.POST, requestEntity, String.class);
        // 处理服务器响应结果
        if (response.getStatusCode() == HttpStatus.OK) {
            // 上传成功
            System.out.println("File uploaded successfully!");
        } else {
            // 上传失败
            System.out.println("Failed to upload file. Server response: " + response.getBody());
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
    /*
    切换显示情况
     */
    public void switchWin(){
        if(isVisible){
            isVisible=false;
            this.setVisible(isVisible);
        }
        else{
            isVisible=true;
            this.setVisible(isVisible);
            loadImagesFromFolder();  //重新载入图片
        }
    }
    public void setWinVisible(boolean flag){
        isVisible=flag;
        this.setVisible(isVisible);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new org.DUT.example.ImageGallery().setVisible(true);
        });
    }
    private static volatile PicturePanel picPanel;
    public static PicturePanel getInstance() {
        if (null == picPanel) {
            // 模拟在创建对象之前做一些准备工作
            synchronized (PicturePanel.class) {
                if(null == picPanel) {
                    picPanel = new PicturePanel();
                }
            }
        }
        return picPanel;
    }
}
