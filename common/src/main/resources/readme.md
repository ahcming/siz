### 架构图

Client          Server
 |                  |
 |                  |
 +------------------+
 |     api parse    |
 +------------------+
 |   <- netty ->    |
 +------------------+
 
 
 
## 版本演变
 
### 1.0: Client直接调用Server
- Client通过netty发送数据到Server
- Server解析Client过来的请求数据
- 把响应数据通过netty返回给Client
- Client解析响应数据

### 2.0: API使用接口定义及调用, Client直连Server

实现原理

- API通过Java interface定义
- api地址: 服务命名空间 + 接口的完全限定名, api参数: 方法调用时传递的参数, 通过动态代理生成接口信息调用, 对调用者完全屏蔽细节
- 在Client端通过接口调用完成API调用, 把数据发送到Server
- Server开发者真实的实现逻辑跟API地址进行bind
- Server收到数据过后, 解析出api地址, 调用真正的实现类, 并把返回值和异常返回给Client
- Client及Server之的数据协议支持可配置 (此版本支持PB格式, Object默认序列化方式)

### 完成RPC调用步骤
- 定义API interface, 参见: UserApi, User
- 公开API interface, 一般是把UserApi, User把打成serverXXX-stub.jar包, 服务提供方及服务调用方都要依赖serverXXX-stub.jar
- 服务提供方实现API: 参见: UserApiImpl, 
- API上线, 把服务实现代码部署到服务器上, 把服务地址(IP:port)公布出来, 参见ServerDemo
- 服务调用方请求API: 连接IP:port, 通过interface调用API, 参见ClientDemo

```shell

 Client                                                            Server
    |                                                                |
 UserApi                                                    UserApi(UserApiImpl)
    |                                                                |
    +>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>+
    |       API Invoke:  userApi.getUser(123456)                     |
    |       Build RpcRequest, decode request, encode response        |
    |       similarly send url: userService:getUser?id=123456        |
    +----------------------------------------------------------------+
    |       Receive RpcRequest: userService:getUser?id=123456        |
    |       encode request, Invoke: userApiImpl.getUser(123456)      |
    |       Wrapper result, Build RpcResponse, decode response       |
    +<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<+ 

```

### 2.1: 增加长连接+心跳机制

- 长连接: 由于RPC都是内部调用, TPS都比较高, 为减少TCP建立连接的次数, 用长连接比较合适
- 心跳机制设计: 由于服务端保持长连接是比较耗费服务器资源, 当Client与Server某一方挂掉时, 应及时释放掉长连接
- server与zk之间有心跳? 当Server挂掉, zk把server从服务列表中剔除; 当client连接到的Server不可响应, 异常策略(重试, let it crash). 心跳间隔?
- client与server之间心跳? 当Client挂掉, server断开与client之间的连接, 释放资源

### 3.0: 通过zk, 实现服务注册,治理等功能

### 3.1: 客户端负载均衡

### 3.2: 服务降级, 熔断机制

### 3.3: 配置中心, 热更新, 监制
