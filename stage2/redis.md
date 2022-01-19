# 安装

```shell
sudo apt install redis-server
```

安装后会自启。

# 基本数据类型

## String

![](img\20.png)

## hash

![](img\21.png)

## list

![](img\22.png)

## SET

![](img\23.png)

set有去除重复元素的特性。

## ZSET

有序的Set。

![](img\24.png)

# 线程模型

![](img\25.png)

# 发布订阅

![](img\26.png)

## 订阅

```shell
# SUBSCRIBE 频道1 频道2 
SUBSCRIBE fybCh
```

```shell
# * 通配符 批量订阅
SUBSCRIBE fybCh*
```



## 发布

```shell
PUBLISH fybCh hello
```

# 持久化

## 快照（snapshotting）持久化（RDB）

快照持久化是 Redis 默认采用的持久化方式，在 Redis.conf 配置文件中默认有此下配置：

```
save 900 1           #在900秒(15分钟)之后，如果至少有1个key发生变化，Redis就会自动触发BGSAVE命令创建快照。

save 300 10          #在300秒(5分钟)之后，如果至少有10个key发生变化，Redis就会自动触发BGSAVE命令创建快照。

save 60 10000        #在60秒(1分钟)之后，如果至少有10000个key发生变化，Redis就会自动触发BGSAVE命令创建快照。
```

## AOF（append-only file）持久化

与快照持久化相比，AOF 持久化的实时性更好，因此已成为主流的持久化方案。

```conf
appendfsync always    #每次有数据修改发生时都会写入AOF文件,这样会严重降低Redis的速度
appendfsync everysec  #每秒钟同步一次，显示地将多个写命令同步到硬盘
appendfsync no        #让操作系统决定何时进行同步
```

## 混合持久化

默认开启了？

```
aof-use-rdb-preamble yes
```

# 主从

## 原理

![](img\27.png)

## 模式

![](img\28.png)

![](img\29.png)

![](img\30.png)

## 一主二从模式搭建

![](img\31.png)