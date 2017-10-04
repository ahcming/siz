# siz (Sex is Zero, 色即是空)

## 技术栈
* 使用Vert.X框架实现接口文档管理相关功能

## 项目模块说明
* web-open: 对外展示相关代码
* web-manage: 后台管理代码
* common: 公共代码

## 项目目标
### 接口测试用例
* 可以设置接口参数
* 可以对返回值进行断言
* 用例进行自动执行
* 最好用例能由测试维护,新增,执行

### 接口性能测试
* 对接口进行简单的多线程压测
* 输出测试报告

### 生成接口文档
* 接口参数说明文档
* 接口注意说明
* 接口用例文档

### 支持各种形式接口
- http/https
- RPC (kiev, dubbo, jRPC)
- Socket/Websocket