
# 1. 本系统分为3个模块
## 1.1 一个是公共模块common
## 1.2 另一个模块是user模块
## 1.3 tools模块，主要生成entity mapper service controller
主要利用mabitisPlus3.x生成

common模块未来主要放一些公共的内容
user模块主要做shiro登录、验证码登录、单点登录、用户权限缓存到redis等关于用户的信息
# 3. 端口
8801开始都是提供者
8851开始都是消费者
其中8801的提供者对应的消费者是8851，同理8802-------8852
