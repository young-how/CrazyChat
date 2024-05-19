package org.DUT.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @projectName: CrazyChat_Service
 * @package: com.dlut.crazychat.pojo
 * @className: pokerStatics
 * @author: younghow
 * @description: TODO
 * @date: 2024/5/19 9:39
 * @version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class pokerStatics {
    private String userId;  //用户id
    private long gameNum;  //游戏场次
    private long winNum;  //胜利场次
    private double luckyValue;  //手气值，所有出牌牌型得分之和/场次
    private double winRate;  //胜率
    private long rankMoney;  //积分排行
    private long rankWinNum;  //胜场排行
    private long rankLucky;  //手气值排行
    private long gainMoney;  //赢取的积分数
    private long highCardNum;  //打出高牌的数目
    private long pairNum;  //对子的数目
    private long twoPairNum; //两队的数目
    private long threeOfKindNum; //三条
    private long straightNum;  //顺子
    private long flushNum;  //同花
    private long fullHouseNum;  //葫芦的数目
    private long fourOfKindNum;  //四条数目
    private long StraightFlushNum;  //同花顺数目
}
