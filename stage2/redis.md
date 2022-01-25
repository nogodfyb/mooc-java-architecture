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

## 一主二从模式搭建(读写分离)

![](img\31.png)

### 从库

```conf
replicaof 192.168.248.128 6379
#暴露在公网一定要密码
masterauth <master-password>
#默认配置 从库只读
replica-read-only yes
```

![](img\32.png)

### 主库

可以不做特殊配置，因为默认的就是主库。

# 无磁盘化复制

WARNING: DISKLESS REPLICATION IS EXPERIMENTAL CURRENTLY

```
#默认是关闭的
repl-diskless-sync no
```

# 缓存过期机制

1. **惰性删除** ：只会在取出 key 的时候才对数据进行过期检查。这样对 CPU 最友好，但是可能会造成太多过期 key 没有被删除。
2. **定期删除** ： 每隔一段时间抽取一批 key 执行删除过期 key 操作。并且，Redis 底层会通过限制删除操作执行的时长和频率来减少删除操作对 CPU 时间的影响。

# 内存淘汰管理机制（缓存过期机制的补充）

Redis 提供 6 种数据淘汰策略：

1. **volatile-lru（least recently used）**：从已设置过期时间的数据集（server.db[i].expires）中挑选最近最少使用的数据淘汰
2. **volatile-ttl**：从已设置过期时间的数据集（server.db[i].expires）中挑选将要过期的数据淘汰
3. **volatile-random**：从已设置过期时间的数据集（server.db[i].expires）中任意选择数据淘汰
4. **allkeys-lru（least recently used）**：当内存不足以容纳新写入数据时，在键空间中，移除最近最少使用的 key（这个是最常用的）
5. **allkeys-random**：从数据集（server.db[i].dict）中任意选择数据淘汰
6. **no-eviction**：禁止驱逐数据，也就是说当内存不足以容纳新写入数据时，新写入操作会报错。这个应该没人使用吧！

4.0 版本后增加以下两种：

1. **volatile-lfu（least frequently used）**：从已设置过期时间的数据集（server.db[i].expires）中挑选最不经常使用的数据淘汰
2. **allkeys-lfu（least frequently used）**：当内存不足以容纳新写入数据时，在键空间中，移除最不经常使用的 key

# 哨兵模式

![](img\33.png)

ubuntu需要安装

```shell
sudo apt install redis-sentinel
```

编辑sentinel.conf

```conf
# 注释掉
# bind 127.0.0.1 ::1
# 
sentinel monitor mymaster 192.168.248.128 6379 2
```

重启

```shell
sudo service redis-sentinel restart
```

与springboot 整合

```properties
spring.redis.database= 0
spring.redis.sentinel.master= mymaster
spring.redis.sentinel.nodes= 192.168.248.130:26379,192.168.248.128:26379,192.168.248.131:26379
```

## 疑问

表面上看起来是整合成功了，主库挂掉了，会重新在从库中选举产生新的主库。

但是怎么证明读写分离是OK的呢？如果不OK，该怎么做呢，这个问题留待以后水平提高了来证明或处理。

## 弊端

主节点挂掉时，可能会导致一两秒内的数据没有同步到从库，所以造成了缓存数据丢失。

# 三主三从集群模式

![](img\34.png)

redis1-6:redis.conf

```
#开启集群模式
cluster-enabled yes
#redis 自己维护的配置文件
cluster-config-file nodes-6379.conf
#超时时间
cluster-node-timeout 5000
#开启aof
appendonly yes
```

创建集群命令：

```shell
redis-cli --cluster create ip1:6379 ip2:6379 ip3:6379 ip4:6379 ip5:6379 ip6:6379 --cluster-replicas 1

redis-cli --cluster create 192.168.248.134:6379 192.168.248.135:6379 192.168.248.136:6379 192.168.248.137:6379 192.168.248.138:6379 192.168.248.139:6379 --cluster-replicas 1
```

![](img\37.png)

检查集群信息

```shell
redis-cli --cluster check 192.168.248.134:6379
```

![](img\38.png)

## 槽节点

![](img\35.png)

![](img\36.png)

## 登录集群：

```shell
redis-cli -c -p 6379 -h 192.168.248.134
```

![](img\39.png)

# 缓存穿透

## 解决方案

### 缓存无效KEY

```java
public Object getObjectInclNullById(Integer id) {
    // 从缓存中获取数据
    Object cacheValue = cache.get(id);
    // 缓存为空
    if (cacheValue == null) {
        // 从数据库中获取
        Object storageValue = storage.get(key);
        // 缓存空对象
        cache.set(key, storageValue);
        // 如果存储数据为空，需要设置一个过期时间(300秒)
        if (storageValue == null) {
            // 必须设置过期时间，否则有被攻击的风险
            cache.expire(key, 60 * 5);
        }
        return storageValue;
    }
    return cacheValue;
}
```

### 布隆过滤器

布隆过滤器在方案一(缓存无效KEY)之前，再加一层布隆过滤器过滤，分布式系统推荐使用redis的插件RedisBloom。

# 缓存雪崩

## 解决方案

### 针对Redis服务不可用的情况

1. 采用 Redis 集群，避免单机出现问题整个缓存服务都没办法使用。
2. 限流，避免同时处理大量的请求。

### 针对热点缓存失效的情况

1. 设置不同的失效时间比如随机设置缓存的失效时间。
2. 缓存永不失效。

# 批量查询优化

## 方案一

```java
ArrayList<String> keys = new ArrayList<>();

keys.add("sys_dict:dfn_ft_machine_status");
keys.add("sys_dict:dfn_ft_material_type");
keys.add("sys_dict:shimo_pkg");

List<String> list = stringRedisTemplate.opsForValue().multiGet(keys);
```

打开日志观察，如果采用遍历keys的方法来每个get，不仅从Java 代码本身来看效率低，消耗更多内存和时间，而且还有更频繁的以下日志出现。

```
2022-01-25 10:18:56.283 DEBUG 7120 --- [nio-8080-exec-1] o.s.d.redis.core.RedisConnectionUtils    : Opening RedisConnection
2022-01-25 10:18:56.754 DEBUG 7120 --- [nio-8080-exec-1] o.s.d.redis.core.RedisConnectionUtils    : Closing Redis Connection.
```

## 方案二（管道）

暂时未记录，等有更实用的使用场景再记录。