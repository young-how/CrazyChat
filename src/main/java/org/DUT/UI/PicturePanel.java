package org.DUT.UI;

import lombok.Data;
import org.DUT.httpRequestor;
import org.DUT.userStat;
import org.DUT.utils.Constants;
import org.DUT.utils.downloadUtils;
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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
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
    Set<String> file_set=new HashSet<>();

    public PicturePanel() {
        setTitle("Image Gallery");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        galleryPanel = new JPanel(new GridLayout(0, 3)); // 3列的栅格布局
        JScrollPane scrollPane = new JScrollPane(galleryPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        //添加布局
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        JButton chosePath = getjButton();
        add(chosePath);

        //参考主任务框体进行设置
        setSize(Constants.WIDTH_imageWin, Constants.HEIGHT_imageWin);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // 将窗体设置为半透明
        setUndecorated(true); // 隐藏边框
        setBackground(new Color(255, 255, 255, 0)); // 设置背景颜色为半透明黑色
        setOpacity((float)Constants.Opacity);
        //设置窗口始终置顶
        setAlwaysOnTop(true);
        // 设置窗体位置为右下角
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //根据系统设置摆放位置
        setLocation(screenSize.width - getWidth()+Constants.LOCATION_X_ADD-Constants.WIDTH, screenSize.height - getHeight()+Constants.LOCATION_Y_ADD);
        loadImagesFromFolder();
        setVisible(isVisible);

    }
    private static File showFileChooser(Desktop desktop) throws IOException {
        // 调用系统默认的文件资源管理器
        File selectedFile = null;
        File currentDir = new File(".");
        desktop.open(currentDir);

        // 等待用户选择文件或文件夹
        selectedFile = new File(currentDir.getAbsolutePath());
        return selectedFile;
    }
    private static JButton getjButton() {
        JButton chosePath=new JButton("选择图片资源文件夹");
        chosePath.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                // 设置文件选择对话框的外观为 Windows 风格
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    SwingUtilities.updateComponentTreeUI(fileChooser);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String selectedFolderPath = selectedFile.getAbsolutePath();
                    System.out.println("所选文件夹路径：" + selectedFolderPath);
                    Constants.setMediaPath(selectedFolderPath);
//                    try {
//                        downloadUtils.downloadFile("http://10.7.8.7:1025/asyncDownload",
//                                "testEmojiPackage-master.zip","C:\\Users\\young\\Downloads\\test",4);
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    } catch (InterruptedException ex) {
//                        ex.printStackTrace();
//                    } catch (ExecutionException ex) {
//                        ex.printStackTrace();
//                    }
                }
            }
        });
        chosePath.setAlignmentX(Component.CENTER_ALIGNMENT);
        return chosePath;
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
            //galleryPanel=new JPanel(new GridLayout(0, 3));
            //galleryPanel.removeAll();
            //revalidate();
            for (File file : files) {
                if (isImageFile(file)) {
                    if(file_set.contains(file.getPath())) continue;
                    file_set.add(file.getPath());
                    BufferedImage thumbnail = createThumbnail(file);
                    ImageIcon icon = new ImageIcon(thumbnail);
                    JLabel label = new JLabel(icon);
                    label.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            // 在这里处理点击事件，例如向服务器发送请求并上传图片
                            System.out.println("Clicked on: " + file.getName());
                            System.out.println(file.getAbsolutePath());
                            File output=adjustImage(file);  //调整图片大小
                            sendPic2Server(output);  //发送调整大小后的图片
                            output.delete();
                        }
                    });
                    galleryPanel.add(label);
                    galleryPanel.repaint();
                }
            }
        };
        executor.execute(task);

    }
    public File adjustImage(File inputFile){
        File outputFile = new File("temp_"+ UUID.randomUUID().toString()+".jpg");
        // 目标宽度
        // 目标宽度
        int targetWidth = 250;

        try {
            // 读取原始图片文件
            BufferedImage originalImage = ImageIO.read(inputFile);

            // 计算调整后的高度，保持高度比例
            int targetHeight = (int) (originalImage.getHeight() * (double) targetWidth / originalImage.getWidth());

            // 创建缩放后的图片
            Image scaledImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
            BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.drawImage(scaledImage, 0, 0, null);
            graphics2D.dispose();
            // 将调整后的图片保存到文件
            ImageIO.write(resizedImage, "jpg", outputFile);
            // 重命名调整后的图片为原始图片的文件名
            //outputFile.renameTo(inputFile);
            System.out.println("图片调整完成，并替换原始图片。");

        } catch (IOException e) {
            e.printStackTrace();
            return inputFile;
        }
        return  outputFile;
    }
    public void sendPic2Server(File file){
        //发送图片到服务器
        // 设置服务器地址和端口号
        String serverUrl = "http://"+Constants.SERVER_IP+":"+Constants.SERVER_PORT+"/sendFile";
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        // 创建请求体，包含文件内容
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));
        if(Constants.user!=null){
            body.add("user", Constants.user);
        }
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
            loadImagesFromFolder();  //重新载入图片
            this.setVisible(isVisible);
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
