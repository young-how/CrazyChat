package org.DUT.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/*
牌桌信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class pokerDesk {
    @JsonProperty("boardCards")
    private List<String> boardCards=new ArrayList<>();  //场面牌
    @JsonProperty("hadCards")
    private List<String> hadCards=new ArrayList<>();  //手牌
    private Boolean started;  //游戏是否启动
    private Integer pot;   //当前奖池
    private Integer money;  //自己的筹码
    private Integer currentUser_id;   //当前操作的玩家序号
    private Integer own_id;   //玩家的序号
    @JsonProperty("users")
    private List<texasPlayer> users=new ArrayList<>();  //所有用户的信息
    private Integer round;  //当前的下注轮次
    private Integer currentHighestBet;  //当前最高下注金额
    private String systemInfo;  //系统消息
    private String winner;  //赢家信息
    private List<String> winner_cards=new ArrayList<>();  //赢家手牌
}
