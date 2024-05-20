# CrazyChat  ![](https://img.shields.io/badge/license-MIT-blue) ![](https://img.shields.io/badge/jdk-1.8%2F17-green) ![](https://img.shields.io/badge/Kafka-3.70-blue) ![](https://img.shields.io/badge/Redis-5.0.7-red) ![](https://img.shields.io/badge/Spring-6.1.5-lightgreen) ![](https://img.shields.io/badge/Ollama-V0.1.34-yellow)

CrazyChat是一个适用于局域网/公网的公屏聊天软件，内置了多种支持用户互动的游戏。具备隐蔽性和匿名性，适合上班划水+工位摸鱼。
+ 隐蔽性： 界面不起眼，支持一键清屏与对话框伸缩，仅接收实时消息，让摸鱼更具隐蔽性。
+ 社交性： 无标识匿名发言，让社恐同学敢于发言。服务端设置有指令系统、积分系统、游戏系统，让摸鱼更有乐趣。
+ 游戏系统： 服务器能接收公屏的指令并加入到公屏聊天，服务端能提供德州扑克、谁是卧底、猜数字、刮刮乐、每日签到、用户交易、积分排行榜、活跃用户统计等功能。为摸鱼提供打怪升级的渠道。
+ AI大模型支持: 接入了本地AI大模型接口。能更加便捷地辅助你的摸鱼与工作生活。
+ 支持实时消息推送：能够接收服务端的相应数据流并实施更新消息。
+ 支持私密消息： 服务端能够通过特定指令向公屏发送指定用户可见的私密消息，可用于文字交互游戏的角色分发。

## 开发环境
+ 客户端： jdk 1.8, maven.
+ 服务端： ubuntu 20.04.1 LTS, kafka_2.12-3.70, redis, jdk 17, Spring 6.15, maven, Ollama.

## 环境部署

服务端:

服务端项目链接https://github.com/young-how/CrazyChat_Server. 需要预先在服务器上安装好kafka和redis。需要根据自身的配置运行服务器项目，需要对application.properties配置文件进行修改，修改项将在下方介绍。

按自己需求配置kafka和redis服务器:
```
server.port=1025 
spring.application.name=CrazyChat
spring.main.allow-circular-references=true

#redis
spring.data.redis.host=10.7.8.7  
spring.data.redis.port=6379
spring.data.redis.password=ABCabc123

#Kafka
SystemMessage.ip=10.7.8.7
SystemMessage.port=9092
```
猜词游戏的配置：
```
#guess game
#随机数的生成范围(0~bound)
game.guess.bound=1000
#猜中数字的基础奖励
game.guess.basebonus=200
#每次猜词游戏消耗的积分数目
game.guess.money_per_time=15
```
调整刮刮乐游戏的配置:
```
#lottery game
#彩票初始积分奖池
game.lottery.base=600000
#彩票最大积分奖池
game.lottery.base_max=1000000
#单张彩票所需消耗积分
game.lottery.one_ticked=30
#一次最多购买的彩票数
game.lottery.max_ticked=10
#单张彩票增加的奖池积分
game.lottery.add_per_note=50
#1~6等奖的中奖概率
game.lottery.probability_1=0.00001
game.lottery.probability_2=0.00003
game.lottery.probability_3=0.001
game.lottery.probability_4=0.01
game.lottery.probability_5=0.1
game.lottery.probability_6=0.3
```
每日签到奖励配置：
```
#dailySign
#基础奖励
game.daiySign.base_reward=1000
#奖励因子1，适用于累积签到日数的加成
game.daiySign.factor_sign_count=3
#奖励因子2，适用于连续签到日数的加成
game.daiySign.factor_sign_continues=10
#最高奖励为基础奖励的n倍
game.daiySign.max_magnification=20
```
寻找卧底游戏的词典路径配置，根据自身服务器的路径进行配置:
```
#findSpy
game.findSpy.dictionary=/home/ubuntu/younghow/CrazyChatServer/src/main/resources/findSpy.csv
#卧底数目
game.findSpy.spyNum=1
#单局奖励积分
game.findSpy.rewardOneturn=100
#卧底胜利积分
game.findSpy.rewardWinnerSpy=1000
#平民胜利积分
game.findSpy.rewardWinnerPeople=500
```

上述部分仅kafka、redis和寻找卧底的词典文件需要根据自身配置进行修改，其他都可以采用默认值。配置完成后，在服务端启动SpringBoot项目即可开启crazy的摸鱼之路。
```
$ sudo mvn package
$ java -jar /your/project/path/target/CrazyChat-0.0.1-SNAPSHOT.jar
```

客户端：

修改config.properties配置文件:
```
#kafka-server,与服务端保持一致
server.kafka.ip=10.7.8.7
server.kafka.port=9092
#部署了CrazyChat_Server的服务器ip地址与端口号
server.CrazyChat_Server.ip=10.7.8.7
server.CrazyChat_Server.port=1025

#topic
message.topic=chatroom
message.initName=guest
```
修改完成后直接运行ChatClient即可打开客户端。

## 使用案例

### 客户端概述：

![](/doc/gui.png)

+ 界面固定在桌面的右下角（mac需要手动修改代码以调整布局），并悬浮在最上方。
+ "name"输入框为发言所显示的名称，可随意更改。
+ 输入框输入消息后点击"send"按钮或者回车即可发送消息。
+ 能根据服务器返回的信息对发言数目、积分、等级、排名、活跃人数进行可视化。
+ 点击"..."按钮可以清空所有用户的聊天区，并将聊天区的文本以.txt格式存放在项目路径中。轻松一键销毁摸鱼证据。

![](/doc/clean.gif)
+ 点击"-"按钮能收缩界面，进入隐藏形态，让摸鱼更有安全感。

![](/doc/hide.gif)

### 服务器概述:

服务器支持的指令列表:

| 指令名称          |                                                                                     描述                                                                                      |
|:--------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| #help         |                                                                            帮助列表，服务器会列出当前所有支持的命令                                                                             |
| #st #stat     |                                                                                  显示自身用户的信息                                                                                  |
| #rk           |                                                                                   显示用户排名                                                                                    |
| #lt           |                                                                                购买彩票，单次消耗30积分                                                                                |
| #gs n         |                                                                              猜词游戏，猜中数字n即可获得奖励                                                                               |
| #qd           |                                                         每日签到,随机生成一个(0-1)的随机数x，签到奖励=(1000+10*签到天数+30*连续签到天数)/(x+0.1)                                                         |
| #give         |                                                          赠予其他玩家积分，使用格式为#give id n。其中id为输入#stat后得到的唯一id，n为赠送的积分数目。                                                           |
| #join findSpy | 加入寻找卧底游戏的等待队列中，当人数集齐后输入#start开始游戏。游戏说明：游戏开始后系统会给每个玩家发一个红色字体的私密信息，包含编号和自己的词语，游戏开始后所有玩家将名称改为对应的号码。当所有玩家描述完后输入#vote n投票给n号玩家。票数最多的玩家将会被踢出游戏，无法进行投票。直到所有卧底被找出或者游戏人数中的平民玩家小于卧底人数 |
| #ask          |                                                                        向AI大模型提问，AI机器人会根据你的提问生成相应的回复                                                                         |
| #@            |                                                                         向大模型提问，大模型会私聊回复你，相应是实时数据流。                                                                          |

指令效果一览：

![](/doc/example%201.png)![](/doc/example%202.png)![](/doc/example%203.png)![](/doc/AI.gif) ![](/doc/AI2.gif)
## 更新计划
+ 预期加入德州扑克游戏功能.
+ 预期接入自然语言AI大模型，实现更加便捷的工作助手。
+ 预期添加图片/文件传输功能。

## 服务端额外环境部署（Ubuntu）
### Ollama大模型服务器部署(Ubuntu)
github项目链接https://github.com/ollama/ollama
下载Ollama
```
curl -fsSL https://ollama.com/install.sh | sh
```
下载模型权重文件并运行（以Mistral为例）
```
ollama run Mistral
```
更改服务器配置文件使之能够监听外部请求
```
$ sudo vi /etc/systemd/system/ollama.service
```
文件中的service下添加
```agsl
Environment="OLLAMA_HOST=0.0.0.0:11434"
```
重启Ollama
```agsl
systemctl restart ollama.service
```

## V1.0.4更新
客户端与服务器提供了德州扑克的游戏支持。
### 客户端
客户端提供了用于德州扑克游戏的图形化界面。能够将用户积分兑换为筹码，并能够对正在进行的牌局信息如奖池大小、下注轮次、当前正在下注的玩家、玩家信息进行可视化。支持了更加详细的用户信息可视化，能够对每个用户的下注状态、称号、局数、胜场、胜率、累积积分、手气、手气排名、胜场排名、积分排名、历史牌型进行可视化。信息存储由后端服务器的redis提供支持。
![](/doc/texasPoker.png)
![](/doc/texasPoker2.png)
### 服务端
服务端为德州扑克游戏提供的API如下(以localhost为例)：

1.获取牌局信息
```
http://localhost:1025/texasPoker/getDeskInfo
//requestBody: userStat。根据提供的用户信息返回经过处理的牌局视图，示例json字符串如下：
{
    "id": "00-FF-2D-4A-D2-C1",
    "name": "test",
    "message_num": 132,
    "score": 2320,
    "level": 2,
    "title": null,
    "win_game_num": 0,
    "game_num": 0,
    "reward": null,
    "rank": null,
    "active": true
}
```
2.加入牌局/购买筹码
```
http://localhost:1025/texasPoker/join
//param: money。  购买的筹码数量、或者加入牌局兑换的筹码数量
//requestBody: userStat。根据提供的用户信息返回经过处理的牌局视图，示例json字符串如略
```
3.退出牌局，并将筹码转换为积分
```
http://localhost:1025/texasPoker/exit
//requestBody: userStat。根据提供的用户信息返回经过处理的牌局视图，示例json字符串略
```
4.退出牌局，并将筹码转换为积分
```
http://localhost:1025/texasPoker/bet
//param: money。  下注的筹码数量
//param: fold。  是否弃牌，弃牌:true,不弃牌：false
//requestBody: userStat。根据提供的用户信息返回经过处理的牌局视图，示例json字符串略
```
