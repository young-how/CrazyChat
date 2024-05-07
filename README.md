# CrazyChat  ![](https://img.shields.io/badge/license-MIT-blue) ![](https://img.shields.io/badge/npm-v1.0.1-blue) ![](https://img.shields.io/badge/circleci-passing-brightgreen)

![](https://github.com/young-how/CrazyChat/blob/master/doc/cover.png)

CrazyChat是一个适用于局域网/公网的公屏聊天软件，内置了多种支持用户互动的游戏。具备隐蔽性和匿名性，适合上班划水+工位摸鱼。
+ 隐蔽性： 界面不起眼，支持一键清屏与对话框伸缩，让摸鱼更具隐蔽性。
+ 社交性： 无标识匿名发言，让社恐同学敢于发言。服务端设置有指令系统、积分系统、游戏系统，让摸鱼更有乐趣。
+ 游戏系统： 服务器能接收公屏的指令并加入到公屏聊天，服务端能提供谁是卧底、猜数字、刮刮乐、每日签到、用户交易、积分排行榜、活跃用户统计等功能。为摸鱼提供打怪升级的渠道。
+ 支持私密消息： 服务端能够通过特定指令向公屏发送指定用户可见的私密消息，可用于文字交互游戏的角色分发。

## Installation
```
npm install water-js
```
## Examples
```js
const { water } = require('water-js')

function print() {
  const content = water(true);
  console.log(content);
}
```
output
```
// 输出
我就是来水的
```
## License
Water.js is MIT licensed.
