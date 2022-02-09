# 核心术语

![](3.png)

![](img\1.png)

# 集群原理

![](img\2.png)

# 倒排索引

![](img\3.png)

# 常用API



## 创建索引

```http
PUT http://192.168.248.128:9200/index_demo
Content-Type: application/json

{
    "settings": {
        "index": {
            "number_of_shards": "5",
            "number_of_replicas": "0"
        }
    }
}
```

## 删除索引

```http
DELETE http://192.168.248.128:9200/index_demo
```

## 创建包含映射的索引

```http
PUT http://192.168.248.128:9200/index_mapping
Content-Type: application/json

{
  "mappings": {
    "properties": {
      "realname": {
        "type": "text",
        "index": true
      },
      "username": {
        "type": "keyword",
        "index": false
      }
    }
  }
}
```

## 分词

```http
GET http://192.168.248.128:9200/index_mapping/_analyze
Content-Type: application/json

{
  "field": "realname",
  "text": "fyb is good"
}
```

执行第一次

```json
{
  "tokens": [
    {
      "token": "fyb",
      "start_offset": 0,
      "end_offset": 3,
      "type": "<ALPHANUM>",
      "position": 0
    },
    {
      "token": "is",
      "start_offset": 4,
      "end_offset": 6,
      "type": "<ALPHANUM>",
      "position": 1
    },
    {
      "token": "good",
      "start_offset": 7,
      "end_offset": 11,
      "type": "<ALPHANUM>",
      "position": 2
    }
  ]
}
```

执行第二次返回JSON与第一次相同。

## 修改映射（新增数据类型）

```http
POST http://192.168.248.128:9200/index_mapping/_mapping
Content-Type: application/json

{
  "properties": {
    "id": {
      "type": "long"
    },
    "age": {
      "type": "integer"
    }
  }
}
```

## 添加文档与自动映射

```http
POST http://192.168.248.128:9200/my_doc/_doc/2
Content-Type: application/json

{
  "id": 1002,
  "name": "imooc-2",
  "desc": "imooc is very fashion,慕课网非常时尚"
}
```

添加文档的操作，顺便自动为索引创建了映射。

## 文档的删除与修改

删除

```http
DELETE http://192.168.248.128:9200/my_doc/_doc/2
```

局部修改

```http
POST http://192.168.248.128:9200/my_doc/_doc/2/_update
Content-Type: application/json

{
  "doc": {
    "name": "xxx"
  }
}
```

全量修改

```http
PUT http://192.168.248.128:9200/my_doc/_doc/2
Content-Type: application/json

{
  "id": 1002,
  "name": "xxx",
  "desc": "慕课网很强大",
  "create": "2022-02-08"
}
```

## 文档查询

根据ID查询

```http
GET http://192.168.248.128:9200/my_doc/_doc/2
```

查询所有

```http
GET http://192.168.248.128:9200/my_doc/_doc/_search
```

定制化查询

```http
GET http://192.168.248.128:9200/my_doc/_doc/2?_source=id,name
```

```json
{
  "_index": "my_doc",
  "_type": "_doc",
  "_id": "2",
  "_version": 9,
  "_seq_no": 14,
  "_primary_term": 1,
  "found": true,
  "_source": {
    "name": "xxx",
    "id": 1002
  }
}
```

查询文档是否存在

```http
HEAD http://192.168.248.128:9200/my_doc/_doc/2?_source=id,name

Response code: 200 (OK);
```

## 文档乐观锁控制

```http
POST http://192.168.248.128:9200/my_doc/_doc/2?if_seq_no=15&if_primary_term=1
Content-Type: application/json

{
  "name": "xxx_XXXX"
}
```

