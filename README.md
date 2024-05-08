# CrazyChat  ![](https://img.shields.io/badge/license-MIT-blue) ![](https://img.shields.io/badge/npm-v1.0.1-blue) ![](https://img.shields.io/badge/circleci-passing-brightgreen)

![](https://github.com/young-how/CrazyChat/blob/master/doc/cover.png)

CrazyChat是一个适用于局域网/公网的公屏聊天软件，内置了多种支持用户互动的游戏。具备隐蔽性和匿名性，适合上班划水+工位摸鱼。
+ 隐蔽性： 界面不起眼，支持一键清屏与对话框伸缩，仅接收实时消息，让摸鱼更具隐蔽性。
+ 社交性： 无标识匿名发言，让社恐同学敢于发言。服务端设置有指令系统、积分系统、游戏系统，让摸鱼更有乐趣。
+ 游戏系统： 服务器能接收公屏的指令并加入到公屏聊天，服务端能提供谁是卧底、猜数字、刮刮乐、每日签到、用户交易、积分排行榜、活跃用户统计等功能。为摸鱼提供打怪升级的渠道。
+ 支持私密消息： 服务端能够通过特定指令向公屏发送指定用户可见的私密消息，可用于文字交互游戏的角色分发。

## 开发环境
+ 客户端： jdk 1.8, maven.
+ 服务端： ubuntu 20.04.1 LTS, kafka_2.12-3.70, redis, jdk 17, Spring 6.15, maven.

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

客户端概述：

## License
Water.js is MIT licensed.
