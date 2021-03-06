# 使用nginx搭建三个tomcat集群

```conf
user www-data;
worker_processes auto;
pid /run/nginx.pid;
include /etc/nginx/modules-enabled/*.conf;

events {
	worker_connections 768;
	# multi_accept on;
}

http {

	##
	# Basic Settings
	##

	sendfile on;
	tcp_nopush on;
	tcp_nodelay on;
	keepalive_timeout 65;
	types_hash_max_size 2048;
	# server_tokens off;

	# server_names_hash_bucket_size 64;
	# server_name_in_redirect off;

	include /etc/nginx/mime.types;
	default_type application/octet-stream;

	##
	# SSL Settings
	##

	ssl_protocols TLSv1 TLSv1.1 TLSv1.2 TLSv1.3; # Dropping SSLv3, ref: POODLE
	ssl_prefer_server_ciphers on;

	##
	# Logging Settings
	##

	access_log /var/log/nginx/access.log;
	error_log /var/log/nginx/error.log;

	##
	# Gzip Settings
	##

	gzip on;

	# gzip_vary on;
	# gzip_proxied any;
	# gzip_comp_level 6;
	# gzip_buffers 16 8k;
	# gzip_http_version 1.1;
	# gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;

	##
	# Virtual Host Configs
	##

	include /etc/nginx/conf.d/*.conf;
	include /etc/nginx/sites-enabled/*;


	#配置upstream
	upstream nginx-cluster {
		server 192.168.19.56:8080 weight=1;
		server 192.168.19.56:8081 weight=1;
		server 192.168.19.56:8082 weight=1;
	}

	#配置server节点
	server {
		listen       80;
		server_name  192.168.248.128;
		location / {
			proxy_pass http://nginx-cluster/;
		}
	}
}


#mail {
#	# See sample authentication script at:
#	# http://wiki.nginx.org/ImapAuthenticateWithApachePhpScript
# 
#	# auth_http localhost/auth.php;
#	# pop3_capabilities "TOP" "USER";
#	# imap_capabilities "IMAP4rev1" "UIDPLUS";
# 
#	server {
#		listen     localhost:110;
#		protocol   pop3;
#		proxy      on;
#	}
# 
#	server {
#		listen     localhost:143;
#		protocol   imap;
#		proxy      on;
#	}
#}

```

## upstream的指令参数

### max_conns

限制到代理服务器的最大同时活动连接数。 默认值为零，表示没有限制。 如果服务器组不驻留在共享内存中，则限制适用于每个工作进程。

### slow_start

设置服务器将其权重从零恢复到标称值的时间，当不健康的服务器变得健康时，或者当服务器在一段时间被认为不可用后变得可用时。 默认值为零，即禁用慢启动。

### down

将服务器标记为永久不可用。

### backup

将服务器标记为备份服务器。 当主服务器不可用时，它将传递请求。

### max_fails和fail_timeout

```
	upstream nginx-cluster {
		server 192.168.19.59:8080 max_fails=2 fail_timeout=10s;
		server 192.168.19.59:8081 max_fails=2 fail_timeout=10s;
		server 192.168.19.59:8082 max_fails=2 fail_timeout=10s;
	}
```

如上配置表明如果后端节点10秒内出现2次不可用情况，判定节点不可用。判定不可用后10秒内请求不会转发到此节点，直到10秒后重新检测节点健康情况。

## 支持keep alive长连接

https://skyao.gitbooks.io/learning-nginx/content/documentation/keep_alive.html

## 负载均衡

### ip_hash

指定集群使用的负载均衡算法，基于客户端IP地址将请求分派给服务器

### 一致性hash

使用一致性哈希的好处在于，增减集群的缓存服务器时，只有少量的缓存会失效，回源量较小。简单来说就是增减服务器之后，通过hash算法命中的具体上游服务器中的节点，对于大多数IP（用户）来说保持不变。

### URL_HASH

### LEAST_CONN

上游服务器的节点中，采用当前连接数最少的节点去命中。

# 浏览器缓存

# 高可用

## keepalived初体验---双机主备

ubuntu下安装

```shell
sudo apt-get install keepalived
```

创建编辑配置文件

```shell
sudo vim /etc/keepalived/keepalived.conf
```

### 主节点

```conf
global_defs {
    # 路由id：当前安装keepalived节点主机的标识符，全局唯一
        router_id keep_server1
}
#计算机节点
vrrp_instance VI_1 {
    #表示的状态，当前主机为主节点，MASTER/BACKUP
    state MASTER
    #当前实例绑定的网卡名称
    interface ens33 
    # 主、备机的 virtual_router_id 必须相同
    virtual_router_id 51 
    # 主、备机取不同的优先级，主机值较大，备份机值较小
    priority 100 
    # 主备之间同步检查的时间间隔，默认1S
    advert_int 1
    # 认证授权的密码，防止非法节点的进入
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    # 虚拟IP
    virtual_ipaddress {
        192.168.248.50
    }
}
```

### 备份节点

```
global_defs {
    # 路由id：当前安装keepalived节点主机的标识符，全局唯一
        router_id keep_server2
}
#计算机节点
vrrp_instance VI_1 {
    #表示的状态，当前主机为主节点，MASTER/BACKUP
    state BACKUP
    #当前实例绑定的网卡名称
    interface ens33 
    # 主、备机的 virtual_router_id 必须相同
    virtual_router_id 51 
    # 主、备机取不同的优先级，主机值较大，备份机值较小
    priority 80 
    # 主备之间同步检查的时间间隔，默认1S
    advert_int 1
    # 认证授权的密码，防止非法节点的进入
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    # 虚拟IP
    virtual_ipaddress {
        192.168.248.50
    }
}
```

启动

```shell
sudo service keepalived start
```

查看keepalived启动状态

```shell
sudo systemctl status keepalived
```

查看ubuntu网卡信息

```shell
ip addr show ens33
```

```
2: ens33: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc fq_codel state UP group default qlen 1000
    link/ether 00:0c:29:17:9d:ba brd ff:ff:ff:ff:ff:ff
    altname enp2s1
    inet 192.168.248.130/24 brd 192.168.248.255 scope global dynamic noprefixroute ens33
       valid_lft 1715sec preferred_lft 1715sec
    inet 192.168.248.50/32 scope global ens33
       valid_lft forever preferred_lft forever
    inet6 fe80::d9a7:dec9:a0f4:8b23/64 scope link noprefixroute 
       valid_lft forever preferred_lft forever
```

停止keepalived

```shell
sudo service keepalived stop
```

### 配置脚本实现nginx挂机则挂掉keepalived

check_nginx_alive_or_not.sh

```shell
#!/bin/bash

A=`ps -C nginx --no-header |wc -l`
# 判断nginx是否宕机，如果宕机了，尝试重启
if [ $A -eq 0 ];then
  service nginx restart
  # 等待一小会再次检查nginx,如果没有启动成功,则停止keepalived，使其启动备用机
  sleep 3
  if [ `ps -C nginx --no-header |wc -l` -eq 0 ];then
  	killall keepalived
  fi
fi
```

更新keepalived.conf

```conf
global_defs {
    # 路由id：当前安装keepalived节点主机的标识符，全局唯一
        router_id keep_server1
}
vrrp_script check_nginx_alive {
    script "/etc/keepalived/check_nginx_alive_or_not.sh"
    interval 2 #（检测脚本执行的间隔）
    weight 10 # 如果脚本运行失败，则升级权重10
}
#计算机节点
vrrp_instance VI_1 {
    #表示的状态，当前主机为主节点，MASTER/BACKUP
    state MASTER
    #当前实例绑定的网卡名称
    interface ens33 
    # 主、备机的 virtual_router_id 必须相同
    virtual_router_id 51 
    # 主、备机取不同的优先级，主机值较大，备份机值较小
    priority 100 
    # 主备之间同步检查的时间间隔，默认1S
    advert_int 1
    # 认证授权的密码，防止非法节点的进入
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    track_script{
      check_nginx_alive
    }
    # 虚拟IP
    virtual_ipaddress {
        192.168.248.50
    }
}
```

## keepalived---双主热备

![](img\1.png)

### 主1

```
global_defs {
    # 路由id：当前安装keepalived节点主机的标识符，全局唯一
        router_id keep_server1
}
#计算机节点
vrrp_instance VI_1 {
    #表示的状态，当前主机为主节点，MASTER/BACKUP
    state MASTER
    #当前实例绑定的网卡名称
    interface ens33 
    # 主、备机的 virtual_router_id 必须相同
    virtual_router_id 51 
    # 主、备机取不同的优先级，主机值较大，备份机值较小
    priority 100 
    # 主备之间同步检查的时间间隔，默认1S
    advert_int 1
    # 认证授权的密码，防止非法节点的进入
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    # 虚拟IP
    virtual_ipaddress {
        192.168.248.50
    }
}
vrrp_instance VI_2 {
    state BACKUP
    interface ens33 
    virtual_router_id 52 
    priority 80 
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.248.51
    }
}
```



### 主2

```conf
global_defs {
    # 路由id：当前安装keepalived节点主机的标识符，全局唯一
        router_id keep_server2
}
#计算机节点
vrrp_instance VI_1 {
    #表示的状态，当前主机为主节点，MASTER/BACKUP
    state BACKUP
    #当前实例绑定的网卡名称
    interface ens33 
    # 主、备机的 virtual_router_id 必须相同
    virtual_router_id 51 
    # 主、备机取不同的优先级，主机值较大，备份机值较小
    priority 80 
    # 主备之间同步检查的时间间隔，默认1S
    advert_int 1
    # 认证授权的密码，防止非法节点的进入
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    # 虚拟IP
    virtual_ipaddress {
        192.168.248.50
    }
}
vrrp_instance VI_2 {
    state MASTER
    interface ens33 
    virtual_router_id 52 
    priority 100 
    advert_int 1
    authentication {
        auth_type PASS
        auth_pass 1111
    }
    virtual_ipaddress {
        192.168.248.51
    }
}
```

## LVS

![](img\2.png)

### NAT模式![](img\3.png)

### TUN模式

![](img\4.png)

### DR模式

![](img\5.png)

#### 配置LVS节点

##### 设置VIP（临时设置）

永久设置VIP的方法需要用到的时候再研究。

```shell
sudo ifconfig ens33:1 192.168.248.150 netmask 255.255.0.0
```

设置之前

![](img\6.png)

设置之后

![](img\7.png)

#### ipvsadm

安装

```shell
sudo apt-get install ipvsadm
```

#### 真实服务器配置VIP（本地回环）

##### real-server1:

配置前

![](img\8.png)

配置

```shell
ifconfig lo:0 192.168.248.150 netmask 255.255.255.255
```

配置后

![](img\9.png)

##### real-server2:

同real-server1的配置一样

#### 真实服务器配置ARP响应级别

![](img\10.png)

```shell
echo "1">/proc/sys/net/ipv4/conf/all/arp_ignore   这个命令显示权限不够，改用下面的命令

sudo bash -c 'echo 1 > /proc/sys/net/ipv4/conf/all/arp_ignore'
```

```shell
sudo bash -c 'echo 1 > /proc/sys/net/ipv4/conf/lo/arp_ignore' 
```

```shell
sudo bash -c 'echo 1 > /proc/sys/net/ipv4/conf/default/arp_ignore' 
```

这个配置一生效，最普通的判断方式就是192.168.248.150该ip ping不通了。

![](img\11.png)

```shell
echo "2">/proc/sys/net/ipv4/conf/all/arp_announce 

sudo bash -c 'echo 2 > /proc/sys/net/ipv4/conf/all/arp_announce'
```

```shell
sudo bash -c 'echo 2 > /proc/sys/net/ipv4/conf/lo/arp_announce'
```

```shell
sudo bash -c 'echo 2 > /proc/sys/net/ipv4/conf/default/arp_announce'
```

##### 路由配置(每台均要)

```shell
sudo route add -host 192.168.248.150 dev lo:0
```

#### ipvsadm配置集群规则

查看

```shell
sudo ipvsadm -Ln
```

配置real-server的虚拟Ip

```shell
sudo ipvsadm -A -t 192.168.248.150:80 -s rr
```

![](img\12.png)

```shell
sudo ipvsadm -a -t 192.168.248.150:80 -r 192.168.248.130:80 -g
```

```shell
sudo ipvsadm -a -t 192.168.248.150:80 -r 192.168.248.131:80 -g
```

![](img\13.png)

查看状态验证DR模式

```shell
sudo ipvsadm -Ln --stats
```

![](img\14.png)

Outpkts 和 OutBytes都为0 证明DR模式启动成功。

LVS的持久化机制，连接在900s内都会，访问同一个real-server。

![](img\15.png)

### Keepalived+Lvs+Nginx

之前没有结合keepalived的时候配置了负载均衡规则

![](img\16.png)

清除负载均衡规则

```shell
sudo ipvsadm -C
```

![](img\17.png)

#### LVS+keepalived (1)

```conf
# Global Configuration
global_defs {
	router_id lvs1
}
 
# VRRP Configuration
vrrp_instance LVS {
	state MASTER
	interface ens33
	virtual_router_id 51
	priority 100
	advert_int 1
	authentication {
		auth_type PASS
		auth_pass 1111
	}
 
	virtual_ipaddress {
		192.168.248.150
	}
}
 
# 配置集群访问IP地址
virtual_server 192.168.248.150 80 {
        # 健康检查时间，单位：1s
		delay_loop 6
        # 配置负载均衡的算法，默认是轮询
		lb_algo rr
        # 设置LVS的模式
		lb_kind DR
        # 设置会话持久化的时间s
		persistence_timeout 5
        # 协议 -t
		protocol TCP
 
	# 负载均衡的真实服务器，也就是nginx节点的具体的真实ip地址
	real_server 192.168.248.130 80 {
        # 权重
		weight 1
        # 健康检查
        TCP_CHECK{
            # 检查的端口
            connect_port 80
            # 超时时间 s
            connect_timeout 2
            # 重试次数 次
            nb_get_retry 2
            # 间隔时间 s
            delay_before_retry 3
        }
	}
 
	real_server 192.168.248.131 80 {
		weight 1
        TCP_CHECK{
            connect_port 80
            connect_timeout 2
            nb_get_retry 2
            delay_before_retry 3
        }
	}
 
}
```

启动keepalived 之后

![](img\18.png)

为啥 persistence_timeout 5 设置未成功？（这个待研究）

![](img\19.png)

#### LVS+keepalived (2)

```conf
# Global Configuration
global_defs {
	router_id lvs2
}
 
# VRRP Configuration
vrrp_instance LVS {
	state BACKUP
	interface ens33
	virtual_router_id 51
	priority 80
	advert_int 1
	authentication {
		auth_type PASS
		auth_pass 1111
	}
 
	virtual_ipaddress {
		192.168.248.150
	}
}
 
# 配置集群访问IP地址
virtual_server 192.168.248.150 80 {
        # 健康检查时间，单位：1s
		delay_loop 6
        # 配置负载均衡的算法，默认是轮询
		lb_algo rr
        # 设置LVS的模式
		lb_kind DR
        # 设置会话持久化的时间s
		persistence_timeout 1
        # 协议 -t
		protocol TCP
 
	# 负载均衡的真实服务器，也就是nginx节点的具体的真实ip地址
	real_server 192.168.248.130 80 {
        # 权重
		weight 1
        # 健康检查
        TCP_CHECK{
            # 检查的端口
            connect_port 80
            # 超时时间 s
            connect_timeout 2
            # 重试次数 次
            nb_get_retry 2
            # 间隔时间 s
            delay_before_retry 3
        }
	}
 
	real_server 192.168.248.131 80 {
		weight 1
        TCP_CHECK{
            connect_port 80
            connect_timeout 2
            nb_get_retry 2
            delay_before_retry 3
        }
	}
 
}
```

