package org.DUT.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.DUT.userStat;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class texasPlayer implements Cloneable{
    private String id;
    private userStat user;  //对应游戏外的用户
    private int currentBet;  //当前轮次的下注
    private int currentGameBet;  //当前游戏的下注
    private boolean acted; // 表示玩家是否已进行操作（下注、跟注、加注、弃牌）
    private boolean folded; // 表示玩家是否已弃牌
    private boolean leaved;  //是否离开牌局
    private int money;  //当前身上的积分
    private int no;   //牌局中的序号
    private List<String> hand=new ArrayList<>();  //手牌
    private String cardLevel;  //手牌等级
    private pokerStatics statics;  //个人的统计信息
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    public texasPlayer(String id) {
        this.id = id;
        this.currentBet = 0;
        this.acted = false;
        this.folded = false;
        this.money=3000;
    }
    public texasPlayer(userStat user) {
        this.id = user.getId();
        this.user=user;
        this.currentBet = 0;
        this.currentGameBet=0;
        this.acted = false;
        this.folded = false;
        this.money=0;
        leaved=false;
    }

    public boolean hasActed() {
        return acted;
    }
    /*
    重开一局
     */
    public void resetGame(){
        this.currentBet = 0;
        this.currentGameBet=0;
        this.acted = false;
        this.folded = false;
        if(this.money==0){
            //this.acted = true;
            this.folded = true;//如果初始资金为0，则自动弃牌
        }
    }
    /*
    新的下注轮次，重置下注金额
     */
    public void resetTurn(){
        this.acted=false;
        this.currentBet = 0;
    }
    public void bet(int amount) {
        this.currentBet += amount;
        this.currentGameBet+=amount;
        this.money-=amount;
        this.acted = true;
    }


    public void act(int currentHighestBet) {
        // 示例代码：假设玩家总是跟注到当前最高下注金额
        if(currentHighestBet- this.currentBet>this.money){
            bet(this.money);
        }
        else{
            int amountToCall = currentHighestBet - this.currentBet;
            bet(amountToCall);
        }
    }
    public void addMoney(int num){
        this.money+=num;
    }

    public void resetAction() {
        this.acted = false;
    }

    public boolean isFolded() {
        return folded;
    }

    public void fold() {
        this.folded = true;
    }

    public int getCurrentBet() {
        return currentBet;
    }
}
