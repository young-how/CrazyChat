package org.DUT.UI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.DUT.example.poker;
import org.DUT.pojo.pokerDesk;
import org.DUT.pojo.pokerStatics;
import org.DUT.pojo.texasPlayer;
import org.DUT.userStat;
import org.DUT.utils.Constants;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * @projectName: CrazyChat
 * @package: org.DUT.UI
 * @className: pokerPanel
 * @author: younghow
 * @description: TODO
 * @date: 2024/5/19 14:36
 * @version: 1.0
 */
@Data
public class pokerPanel extends JFrame{
    private JPanel globalInfoPanel;
    private JPanel playersPanel;
    private JPanel actionPanel;
    private JPanel actionPanel2;
    private Timer timer;
    private RestTemplate restTemplate = new RestTemplate();  //请求发送器
    private ObjectMapper objectMapper = new ObjectMapper();  //Json映射为class
    private volatile pokerDesk nearest_deskInfo; //最近的牌局信息
    private volatile JSpinner raiseAmountSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 100));  //加注信息
    private volatile JSpinner joinMoney = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 100));  //加入房间所带筹码
    GridBagConstraints gbc= new GridBagConstraints();
    private ThreadPoolExecutor pool =new ThreadPoolExecutor(2,3,1L, TimeUnit.SECONDS,new LinkedBlockingQueue<>(3));
    public pokerPanel() {
        //标准化设置窗体大小
        setTitle("Texas Poker Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //参考主任务框体进行设置
        setSize(Constants.WIDTH_texasPoker, Constants.HEIGHT_imageWin);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // 将窗体设置为半透明
        setUndecorated(true); // 隐藏边框
        setBackground(new Color(255, 255, 255, 0)); // 设置背景颜色为半透明黑色
        setOpacity((float)1);
        //设置窗口始终置顶
        setAlwaysOnTop(true);
        // 设置窗体位置为右下角
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //根据系统设置摆放位置
        setLocation(screenSize.width - getWidth()+Constants.LOCATION_X_ADD-Constants.WIDTH, screenSize.height - getHeight()+Constants.LOCATION_Y_ADD);


        //setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setLayout(new BorderLayout());
        //添加布局
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Global Info Panel
        globalInfoPanel = new JPanel();
        //globalInfoPanel.setLayout(new GridLayout(4, 4));
        globalInfoPanel.setLayout(new GridBagLayout());  //新布局
        //GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 4, 8); // Add some padding
        add(globalInfoPanel, BorderLayout.NORTH);

        // 动作栏 Panel
        actionPanel = new JPanel();
        actionPanel2 = new JPanel();
        actionPanel.setLayout(new FlowLayout());
        actionPanel2.setLayout(new FlowLayout());
        //actionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        addGlobalInfoPanelComponents();
        add(actionPanel);
        add(actionPanel2);
        // Players Panel
        playersPanel = new JPanel();
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(playersPanel);
        add(scrollPane, BorderLayout.CENTER);


        try{
            updateInfo();
            updateGUI();
        }catch ( JsonProcessingException e){
            e.printStackTrace();
        }

    }
    private void addGlobalInfoPanelComponents() {
        actionPanel.add(new JLabel("兑换筹码:"));
        actionPanel.add(joinMoney); //加入房间所带的金额
        //加入房间
        JButton join = new JButton("兑换");
        join.addActionListener(e -> System.exit(0));
        addButtonStyle(join,actionPanel);

        // Exit Button
        JButton exitButton = new JButton("退出");
        exitButton.addActionListener(e -> System.exit(0));
        addButtonStyle(exitButton,actionPanel);

        // Fold Button
        JButton foldButton = new JButton("弃牌");
        addButtonStyle(foldButton,actionPanel2);

        // Call Button
        JButton callButton = new JButton("跟注");
        //callButton.addActionListener(e -> call());
        addButtonStyle(callButton,actionPanel2);

        // Raise Button
        JButton raiseButton = new JButton("加注");
        //raiseButton.addActionListener(e -> raise());
        addButtonStyle(raiseButton,actionPanel2);
        // Spinner for Raise Amount
        actionPanel2.add(raiseAmountSpinner); //加注的金额
    }
    public void addButtonStyle(JButton button,JPanel jp){
        button.setPreferredSize(new Dimension(60, 17));
        button.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        jp.add(button);
    }
    public HttpEntity<userStat> Class2Json(userStat user){
        // 将 User 对象封装成 JSON，并设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<userStat> request = new HttpEntity<>(user, headers);
        return request;
    }
    public void updateInfo() throws JsonProcessingException {
        Runnable task=()->{
            //向服务器发送请求，开一个线程等待
            while(true){
                try{
                    userStat user=new userStat();
                    HttpEntity<userStat> request=Class2Json(user);
                    // 发送 POST 请求到服务器
                    String serverUrl = "http://localhost:1025/texasPoker/getDeskInfo";
                    String responseBody= restTemplate.postForObject(serverUrl, request, String.class);
                    //JSONObject jsonObject = new JSONObject(responseBody);
                    pokerDesk info=objectMapper.readValue(responseBody, pokerDesk.class);
                    //updateGUI(info);
                    setNearest_deskInfo(info);
                    sleep(20);
                } catch (RuntimeException | InterruptedException e){
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    throw new RuntimeException(e);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }finally {
                    continue;
                }

            }

        };
        try{
            pool.execute(task);
        }
        catch (RuntimeException e){
            e.printStackTrace();
            pool.execute(task);
        }
    }

    private void updateGUI() {
        Runnable task=()->{
            while(true){
                globalInfoPanel.removeAll();
                playersPanel.removeAll();
                pokerDesk gameInfo=getNearest_deskInfo();
                if(gameInfo==null) continue;
                gbc.insets = new Insets(0, 0, 4, 8); // Add some padding
                // 第一行
                setGbc(gbc,0,0,1);
                globalInfoPanel.add(new JLabel("奖池: " + gameInfo.getPot()),gbc);
                setGbc(gbc,1,0,1);
                globalInfoPanel.add(new JLabel("回合: " + gameInfo.getRound()),gbc);
                setGbc(gbc,2,0,1);
                globalInfoPanel.add(new JLabel("正在操作: " + gameInfo.getCurrentUser_id()),gbc);
                String status;
                if(gameInfo.getStarted()){
                    status="已开始";
                }else{
                    status="待开始";
                }
                setGbc(gbc,3,0,1);
                globalInfoPanel.add(new JLabel("状态: " + status),gbc);
                //第2行
                setGbc(gbc,4,0,1);
                globalInfoPanel.add(new JLabel("你的筹码: " + gameInfo.getMoney()),gbc);
                setGbc(gbc,5,0,1);
                globalInfoPanel.add(new JLabel("你的编号: " + gameInfo.getOwn_id()),gbc);
                setGbc(gbc,6,0,1);
                globalInfoPanel.add(new JLabel("当前下注: " + gameInfo.getCurrentHighestBet()),gbc);
                //第3行
                setGbc(gbc,0,1,1);
                globalInfoPanel.add(new JLabel("你的手牌:"),gbc);
                setGbc(gbc,1,1,3);
                globalInfoPanel.add(new JLabel(formCards(gameInfo.getHadCards())),gbc);
                //第4行
                setGbc(gbc,0,2,1);
                globalInfoPanel.add(new JLabel("场面牌:"),gbc);
                setGbc(gbc,1,2,3);
                globalInfoPanel.add(new JLabel(formCards(gameInfo.getBoardCards())),gbc);



                // Update player info
                for (texasPlayer player : gameInfo.getUsers()) {
                    JPanel playerPanel = new JPanel();
                    //playerPanel.setLayout(new GridLayout(0, 2));
                    playerPanel.setLayout(new GridBagLayout());  //新布局
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(0, 0, 8, 8); // Add some padding
                    TitledBorder border =null;
                    if(player.getNo()==gameInfo.getCurrentUser_id()){
                        border = BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(Color.BLUE),
                                "Player " + player.getNo()+"     ID: "+player.getId());
                        border.setTitleColor(Color.BLUE);
                    }
                    else{
                        border = BorderFactory.createTitledBorder("Player " + player.getNo()+"     ID: "+player.getId());
                    }
                    playerPanel.setBorder(border);
                    //第一行
                    setGbc(gbc,0,0,1);
                    playerPanel.add(new JLabel("昵称: "),gbc);
                    setGbc(gbc,1,0,1);
                    playerPanel.add(new JLabel(player.getUser().getName()),gbc);
                    setGbc(gbc,2,0,1);
                    playerPanel.add(new JLabel("筹码: "),gbc);
                    setGbc(gbc,3,0,1);
                    playerPanel.add(new JLabel(String.valueOf(player.getMoney())),gbc);
                    setGbc(gbc,4,0,1);
                    playerPanel.add(new JLabel("下注筹码: "),gbc);
                    setGbc(gbc,5,0,1);
                    playerPanel.add(new JLabel(String.valueOf(player.getCurrentBet())),gbc);
                    setGbc(gbc,6,0,1);
                    playerPanel.add(new JLabel("状态: "),gbc);
                    setGbc(gbc,7,0,1);
                    if(player.isLeaved()){
                        playerPanel.add(new JLabel("离开"),gbc);
                    }else if(player.isFolded()){
                        playerPanel.add(new JLabel("弃牌"),gbc);
                    }else if(player.hasActed()){
                        playerPanel.add(new JLabel("已下注"),gbc);
                    }else{
                        playerPanel.add(new JLabel("未下注"),gbc);
                    }
                    //第2行
                    setGbc(gbc,0,1,1);
                    playerPanel.add(new JLabel("称号: " ),gbc);
                    setGbc(gbc,1,1,7);
                    playerPanel.add(new JLabel(getTitle(player)),gbc);  //获取称号

                    //第3行
                    setGbc(gbc,0,2,1);
                    playerPanel.add(new JLabel("手牌: "),gbc);
                    setGbc(gbc,1,2,3);
                    playerPanel.add(new JLabel(formCards(player.getHand())),gbc);
                    setGbc(gbc,4,2,1);
                    playerPanel.add(new JLabel("牌型: "),gbc);
                    setGbc(gbc,5,2,3);
                    playerPanel.add(new JLabel(player.getCardLevel()),gbc);
                    //第4行
                    pokerStatics statics=player.getStatics();
                    setGbc(gbc,0,3,1);
                    playerPanel.add(new JLabel("游戏局数: "),gbc);
                    setGbc(gbc,1,3,1);
                    playerPanel.add(new JLabel(String.valueOf(statics.getGameNum())),gbc);
                    setGbc(gbc,2,3,1);
                    playerPanel.add(new JLabel("胜场: "),gbc);
                    setGbc(gbc,3,3,1);
                    playerPanel.add(new JLabel(String.valueOf(statics.getWinNum())),gbc);
                    setGbc(gbc,4,3,1);
                    playerPanel.add(new JLabel("胜率: " ),gbc);
                    setGbc(gbc,5,3,1);
                    playerPanel.add(new JLabel(String.format("%.2f",statics.getWinRate())),gbc);
                    setGbc(gbc,6,3,1);
                    playerPanel.add(new JLabel("赢得积分: " ),gbc);
                    setGbc(gbc,7,3,1);
                    playerPanel.add(new JLabel(String.valueOf(statics.getGainMoney())),gbc);
                    //第5行
                    setGbc(gbc,0,4,1);
                    playerPanel.add(new JLabel("幸运值: " ),gbc);
                    setGbc(gbc,1,4,1);
                    playerPanel.add(new JLabel(String.format("%.2f",statics.getLuckyValue())),gbc);
                    setGbc(gbc,2,4,1);
                    playerPanel.add(new JLabel("幸运值排名: " ),gbc);
                    setGbc(gbc,3,4,1);
                    playerPanel.add(new JLabel(String.valueOf(statics.getRankLucky())),gbc);
                    setGbc(gbc,4,4,1);
                    playerPanel.add(new JLabel("胜场排名: " ),gbc);
                    setGbc(gbc,5,4,1);
                    playerPanel.add(new JLabel(String.valueOf(statics.getRankWinNum())),gbc);
                    setGbc(gbc,6,4,1);
                    playerPanel.add(new JLabel("赚取积分排名: " ),gbc);
                    setGbc(gbc,7,4,1);
                    playerPanel.add(new JLabel(String.valueOf(statics.getRankMoney())),gbc);
                    //第6行
                    setGbc(gbc,0,5,1);
                    playerPanel.add(new JLabel("牌型统计: "),gbc);  //牌型统计
                    setGbc(gbc,1,5,7);
                    playerPanel.add(new JLabel(getCardLevelStatics(player)),gbc);


                    // Add more player info if needed
                    playersPanel.add(playerPanel);
                }

                globalInfoPanel.revalidate();
                globalInfoPanel.repaint();
                actionPanel.revalidate();
                actionPanel.repaint();
                playersPanel.revalidate();
                playersPanel.repaint();
                try {
                    sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        pool.execute(task);
    }
    public void setGbc(GridBagConstraints gbc,int x,int y,int gridWith){
        gbc.gridx=x;
        gbc.gridy=y;
        gbc.gridwidth=gridWith;
        gbc.fill=GridBagConstraints.HORIZONTAL;
    }
    public String getCardLevelStatics(texasPlayer player){
        StringBuilder re=new StringBuilder();
        pokerStatics statics=player.getStatics();
        re.append(String.format("高牌[%d] ",statics.getHighCardNum()));
        re.append(String.format("一对[%d] ",statics.getPairNum()));
        re.append(String.format("两对[%d] ",statics.getTwoPairNum()));
        re.append(String.format("三条[%d] ",statics.getThreeOfKindNum()));
        re.append(String.format("顺子[%d] ",statics.getStraightNum()));
        re.append(String.format("同花[%d] ",statics.getFlushNum()));
        re.append(String.format("葫芦[%d] ",statics.getFullHouseNum()));
        re.append(String.format("四条[%d] ",statics.getFourOfKindNum()));
        re.append(String.format("同花顺[%d] ",statics.getStraightFlushNum()));
        return re.toString();
    }
    /*
     * @param player:
     * @return String
     * @author younghow
     * @description 生成玩家的显示称号
     * @date younghow younghow
     */
    public String getTitle(texasPlayer player){
        StringBuilder titles=new StringBuilder("<html>");
        pokerStatics statics=player.getStatics();
        if(statics.getRankMoney()==1&&statics.getRankWinNum()==1){
            //胜场+赢钱数最高
            titles.append(getLabelTile("牌场皇者",5));
        }
        if(statics.getRankLucky()==1){
            //幸运值最高
            if(statics.getGainMoney()<0){
                //幸运值最高，但是现在还是欠钱
                titles.append(getLabelTile("生不逢时",4));
            }else{
                titles.append(getLabelTile("幸运之神",4));
            }
        }
        if(statics.getStraightFlushNum()>0){
            titles.append(getLabelTile("同花顺",5));  //打出了同花顺
        }
        if(statics.getWinNum()>100){
            titles.append(getLabelTile("胜利者",4));  //获取百胜
        }
        if(statics.getGainMoney()>100000){
            titles.append(getLabelTile("财富之巅",5));  //赢得10w积分
        }else if(statics.getGainMoney()>50000){
            titles.append(getLabelTile("钻石豪杰",4));  //赢得5w积分
        }else if(statics.getGainMoney()>20000){
            titles.append(getLabelTile("财富巨人",3));  //赢得5000积分
        } else if(statics.getGainMoney()>10000){
            titles.append(getLabelTile("积分霸主",2));  //赢得10000积分
        }else if(statics.getGainMoney()>=0){
            titles.append(getLabelTile("牌局新手",2));  //初始积分
        }
        else if(statics.getGainMoney()>5000){
            titles.append(getLabelTile("筹码大亨",1));  //赢得5000积分
        }else if(statics.getGainMoney()<-1000000){
            titles.append(getLabelTile("什么败家玩意儿",5));  //累积亏损1000000
        } else if(statics.getGainMoney()<-50000){
            titles.append(getLabelTile("贵在坚持",3));  //累积亏损50000
        } else if(statics.getGainMoney()<-1000){
            titles.append(getLabelTile("贫困潦倒",2));  //累积亏损1000
        }
        titles.append("</html>");
        return titles.toString();
    }
    /*
     * @param title:
     * @param level:
     * @return String
     * @author younghow
     * @description 根据称号和对应的等级生成相应颜色的称号html标签
     * @date younghow younghow
     */
    public String getLabelTile(String title,int level){
        StringBuilder re=new StringBuilder();
        String color="black";
        if(level==5){
            //五级称号
            color="red";
        }else if(level==4){
            //4级称号
            color="orange";
        }
        else if(level==3){
            //3级称号
            color="blue";
        }
        else if(level==2){
            //2级称号
            color="green";
        }
        re.append("<span style='color: "+color+";'> &nbsp;");  //设置称号的颜色
        re.append(title);
        re.append("</span> &nbsp;");
        return re.toString();
    }
    /*
     * @param :
     * @return String
     * @author younghow
     * @description 将扑克牌的花色进行翻译
     * @date younghow younghow
     */
    public String transferCard(String card){
        String sym="";
        String num="";
        String[] sp=card.split("-");
        Integer num_=Integer.valueOf(sp[1]);
        if(num_.equals(13)){
            num="A";
        }
        else if(num_.equals(12)){
            num="K";
        }
        else if(num_.equals(11)){
            num="Q";
        }
        else if(num_.equals(10)){
            num="J";
        }
        else{
            num=String.valueOf(num_+1);
        }
        if(sp[0].contains("hearts")) sym="\u2665";  //♥
        else if(sp[0].contains("spades")) sym="\u2660";  //♠
        else if(sp[0].contains("diamonds")) sym="\u2666";  //♦
        else if(sp[0].contains("clubs")) sym="\u2663";  //♣
        return sym+num;
    }
    public String formCards(java.util.List<String> Cards){
        if(Cards.size()==0) return "";
        StringBuilder re=new StringBuilder("<html>");
        for(String card:Cards){
            String transfer=transferCard(card);
            if(transfer.contains("\u2663")||transfer.contains("\u2660")){
                re.append("<span style='color: black;'> &nbsp;");
                re.append(transfer);
                re.append("</span> &nbsp;");
            }
            else{
                re.append("<span style='color: red;'> &nbsp;");
                re.append(transfer);
                re.append("</span> &nbsp;");
            }
        }
        re.append("</html>");
        return re.toString();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            pokerPanel pokerGUI = new pokerPanel();
            pokerGUI.setVisible(true);
        });
    }
}
