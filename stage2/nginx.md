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

