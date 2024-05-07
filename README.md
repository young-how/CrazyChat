# CrazyChat  ![](https://img.shields.io/badge/license-MIT-blue) ![](https://img.shields.io/badge/npm-v1.0.1-blue) ![](https://img.shields.io/badge/circleci-passing-brightgreen)

![](https://github.com/young-how/CrazyChat/blob/master/doc/cover.png)

CrazyChat是一个适用于局域网/公网的公屏聊天软件，内置了多种支持用户互动的游戏。具备隐蔽性和匿名性，适合上班/工位摸鱼。
+ 正式性： 一看就知道这个库是认真写的
+ 优雅性： 给人以美好的视觉感受，和其他黑白两色的readMe区分开来
+ 装笔性：  有利于向他人展示自己代码的美好的一面

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
