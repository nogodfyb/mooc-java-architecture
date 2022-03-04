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

# 分词与内置分词器

## 内置分词器

```http
POST  http://192.168.248.128:9200/_analyze
Content-Type: application/json

{
  "analyzer": "standard",
  "text": "我在github学习"
}
```

```json
{
  "tokens": [
    {
      "token": "我",
      "start_offset": 0,
      "end_offset": 1,
      "type": "<IDEOGRAPHIC>",
      "position": 0
    },
    {
      "token": "在",
      "start_offset": 1,
      "end_offset": 2,
      "type": "<IDEOGRAPHIC>",
      "position": 1
    },
    {
      "token": "github",
      "start_offset": 2,
      "end_offset": 8,
      "type": "<ALPHANUM>",
      "position": 2
    },
    {
      "token": "学",
      "start_offset": 8,
      "end_offset": 9,
      "type": "<IDEOGRAPHIC>",
      "position": 3
    },
    {
      "token": "习",
      "start_offset": 9,
      "end_offset": 10,
      "type": "<IDEOGRAPHIC>",
      "position": 4
    }
  ]
}
```

中文分词不友好。

## 中文分词器

```shell
sudo unzip elasticsearch-analysis-ik-7.17.0.zip -d /usr/share/elasticsearch/plugins/ik
```

然后重启就安装完成。

### ik_max_word

```http
###分词
POST  http://192.168.248.128:9200/_analyze
Content-Type: application/json

{
  "analyzer": "ik_max_word",
  "text": "我在github学习"
}
```

```json
{
  "tokens": [
    {
      "token": "我",
      "start_offset": 0,
      "end_offset": 1,
      "type": "CN_CHAR",
      "position": 0
    },
    {
      "token": "在",
      "start_offset": 1,
      "end_offset": 2,
      "type": "CN_CHAR",
      "position": 1
    },
    {
      "token": "github",
      "start_offset": 2,
      "end_offset": 8,
      "type": "ENGLISH",
      "position": 2
    },
    {
      "token": "学习",
      "start_offset": 8,
      "end_offset": 10,
      "type": "CN_WORD",
      "position": 3
    }
  ]
}{
  "tokens": [
    {
      "token": "我",
      "start_offset": 0,
      "end_offset": 1,
      "type": "CN_CHAR",
      "position": 0
    },
    {
      "token": "在",
      "start_offset": 1,
      "end_offset": 2,
      "type": "CN_CHAR",
      "position": 1
    },
    {
      "token": "github",
      "start_offset": 2,
      "end_offset": 8,
      "type": "ENGLISH",
      "position": 2
    },
    {
      "token": "学习",
      "start_offset": 8,
      "end_offset": 10,
      "type": "CN_WORD",
      "position": 3
    }
  ]
}
```

### ik_smart

ik_max_word: 会将文本做最细粒度的拆分，比如会将“中华人民共和国国歌”拆分为“中华人民共和国,中华人民,中华,华人,人民共和国,人民,人,民,共和国,共和,和,国国,国歌”，会穷尽各种可能的组合，适合 Term Query；

ik_smart: 会做最粗粒度的拆分，比如会将“中华人民共和国国歌”拆分为“中华人民共和国,国歌”，适合 Phrase 查询。

### 自定义中文词库

# DSL搜索

## 创建映射指定分词器

```http
POST http://192.168.248.128:9200/book/_mapping
Content-Type: application/json

{
  "properties": {
    "bookId": {
      "type": "long"
    },
    "score": {
      "type": "float"
    },
    "scorerCount": {
      "type": "long"
    },
    "title": {
      "type": "text",
      "analyzer": "ik_max_word"
    },
    "author": {
      "type": "text",
      "analyzer": "ik_max_word"
    },
    "countWord": {
      "type": "long"
    }
  }
}


```

## 自定义词典

```
仙
仙逆
诛仙
```



## 入门语法

```http
###单独一个字段的查询
GET http://192.168.248.128:9200/book/_search?q=title:仙
```

```http
###复合字段查询
GET http://192.168.248.128:9200/book/_search?q=title:仙&q=score:7.7
```



```http
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "match": {
      "title": "仙"
    }
  }
}
```

## 查询所有

```http
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "match_all": {
    }
  }
}
```

类比

```sql
select * from book
```

## 查询某些字段

```http
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "match_all": {
    }
  },
  "_source": ["title","author"]
}
```

类比

```sql
select title , author from book
```

## 查询分页

```http
### 分页
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "match_all": {
    }
  },
  "_source": [
    "title",
    "author"
  ],
  "from": 0,
  "size": 2
}
```

## term与match

```http
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "term": {
      "title": "诛仙"
    }
  },
  "_source": [
    "title",
    "author"
  ]
}
```

```json
    "hits": [
      {
        "_index": "book",
        "_type": "_doc",
        "_id": "6",
        "_score": 1.951421,
        "_source": {
          "author": "萧鼎",
          "title": "诛仙"
        }
      }
    ]
```



```http
###
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "match": {
      "title": "诛仙"
    }
  },
  "_source": [
    "title",
    "author"
  ]
}
```

```json
"hits": [
      {
        "_index": "book",
        "_type": "_doc",
        "_id": "6",
        "_score": 3.3464973,
        "_source": {
          "author": "萧鼎",
          "title": "诛仙"
        }
      },
      {
        "_index": "book",
        "_type": "_doc",
        "_id": "18",
        "_score": 1.3950763,
        "_source": {
          "author": "陈风笑",
          "title": "官仙"
        }
      },
      {
        "_index": "book",
        "_type": "_doc",
        "_id": "7",
        "_score": 1.1220688,
        "_source": {
          "author": "说梦者",
          "title": "许仙志"
        }
      },
      {
        "_index": "book",
        "_type": "_doc",
        "_id": "10",
        "_score": 1.0498221,
        "_source": {
          "author": "减肥专家",
          "title": "幽冥仙途"
        }
      },
      {
        "_index": "book",
        "_type": "_doc",
        "_id": "15",
        "_score": 1.0498221,
        "_source": {
          "author": "耳根",
          "title": "仙逆"
        }
      },
      {
        "_index": "book",
        "_type": "_doc",
        "_id": "0",
        "_score": 0.9238435,
        "_source": {
          "author": "忘语",
          "title": "凡人修仙传"
        }
      }
    ]
```

## terms

```http
### 搜索多个关键字
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "terms": {
      "title": ["诛仙","仙逆"]
    }
  },
  "_source": [
    "title",
    "author"
  ]
}
```

## match_phrase

```http
### 搜索多个关键字 多个关键字都必须包含在所搜索的字段中
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "match_phrase": {
      "title": {
        "query": "杨戬 人生"
      }
    }
  },
  "_source": [
    "title",
    "author"
  ]
}
```

杨戬必须要排在人生之前。

```http
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "match_phrase": {
      "title": {
        "query": "人生 杨戬",
        "slop": 3
      }
    }
  },
  "_source": [
    "title",
    "author"
  ]
}
```

slop代表中间可以跳过的字符。

## operator

```http
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "match": {
      "title": {
        "query": "诛仙门派",
        "operator": "or"
      }
    }
  },
  "_source": [
    "title",
    "author"
  ]
}
```

```json
"hits": [
      {
        "_index": "book",
        "_type": "_doc",
        "_id": "6",
        "_score": 3.3464973,
        "_source": {
          "author": "萧鼎",
          "title": "诛仙"
        }
      },
      {
        "_index": "book",
        "_type": "_doc",
        "_id": "18",
        "_score": 1.3950763,
        "_source": {
          "author": "陈风笑",
          "title": "官仙"
        }
      },
      {
        "_index": "book",
        "_type": "_doc",
        "_id": "8",
        "_score": 1.2715712,
        "_source": {
          "author": "齐可休",
          "title": "修真门派掌门路"
        }
      },
      {
        "_index": "book",
        "_type": "_doc",
        "_id": "7",
        "_score": 1.1220688,
        "_source": {
          "author": "说梦者",
          "title": "许仙志"
        }
      },
      {
        "_index": "book",
        "_type": "_doc",
        "_id": "10",
        "_score": 1.0498221,
        "_source": {
          "author": "减肥专家",
          "title": "幽冥仙途"
        }
      },
      {
        "_index": "book",
        "_type": "_doc",
        "_id": "15",
        "_score": 1.0498221,
        "_source": {
          "author": "耳根",
          "title": "仙逆"
        }
      },
      {
        "_index": "book",
        "_type": "_doc",
        "_id": "0",
        "_score": 0.9238435,
        "_source": {
          "author": "忘语",
          "title": "凡人修仙传"
        }
      }
    ]
```

## minimum_should_match

```http
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "match": {
      "title": {
        "query": "诛仙门派",
        "minimum_should_match": "50%"
      }
    }
  },
  "_source": [
    "title",
    "author"
  ]
}
```

这就是一个匹配度，百分比。

```http
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "match": {
      "title": {
        "query": "诛仙门派",
        "minimum_should_match": "2"
      }
    }
  },
  "_source": [
    "title",
    "author"
  ]
}
```

诛仙门派经过分词之后，有两个词汇在搜索的条目中，就符合搜索条件。

诛仙门派分词：诛仙 仙 门派

## ids

```http
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "ids": {
      "type": "_doc",
      "values": [
        "1",
        "2",
        "3"
      ]
    }
  },
  "_source": [
    "title",
    "author"
  ]
}
```

## multi_match

```http
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "multi_match": {
      "query": "笑门派",
      "fields": ["title","author"]
    }
  },
  "_source": [
    "title",
    "author"
  ]
}
```

### 提高搜索字段的权重

```http
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "multi_match": {
      "query": "笑门派",
      "fields": ["title^10","author"]
    }
  },
  "_source": [
    "title",
    "author"
  ]
}
```

## 布尔

### 示例1

```http
###
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "bool": {
      "must": [
        {
          "multi_match": {
            "query": "笑仙",
            "fields": [
              "title^10",
              "author"
            ]
          }
        }
      ]
    }
  },
  "_source": [
    "title",
    "author"
  ]
}
```

### 示例2

```http
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "bool": {
      "must": [
        {
          "multi_match": {
            "query": "笑仙",
            "fields": [
              "title^10",
              "author"
            ]
          }
        },
        {
          "term": {
            "scorerCount": 1567
          }
        }
      ]
    }
  },
  "_source": [
    "title",
    "author",
    "scorerCount"
  ]
}
```

相当于AND

### 示例3

```http
###
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "bool": {
      "should": [
        {
          "multi_match": {
            "query": "笑仙",
            "fields": [
              "title^10",
              "author"
            ]
          }
        },
        {
          "term": {
            "scorerCount": 1567
          }
        }
      ]
    }
  },
  "_source": [
    "title",
    "author",
    "scorerCount"
  ]
}
```

相等于OR

### 示例4

```http
###
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "bool": {
      "must_not": [
        {
          "multi_match": {
            "query": "笑仙",
            "fields": [
              "title^10",
              "author"
            ]
          }
        },
        {
          "term": {
            "scorerCount": 1567
          }
        }
      ]
    }
  },
  "_source": [
    "title",
    "author",
    "scorerCount"
  ]
}
```

相当于  NOT IN

must ,should,must_not都可以任意组合。

### 示例5 加权

```http
###
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "bool": {
      "should": [
        {
          "match": {
            "title": {
              "query": "诛仙",
              "boost": 2
            }
          }
        },
        {
          "match": {
            "author": {
              "query": "水",
              "boost": 20
            }
          }
        }
      ]
    }
  },
  "_source": [
    "title",
    "author",
    "scorerCount"
  ]
}
```

## 过滤

```http
### 
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "match": {
      "title": "诛仙"
    }
  },
  "post_filter": {
    "range": {
      "scorerCount": {
        "gte": 1000,
        "lte": 2000
      }
    }
  },
  "_source": [
    "title",
    "author",
    "scorerCount"
  ]
}
```

## 排序

```http
###
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "match": {
      "title": "诛仙"
    }
  },
  "sort": [
    {
      "scorerCount": "desc"
    }
  ],
  "_source": [
    "title",
    "author",
    "scorerCount"
  ]
}
```

进行了分词的字段，默认不能进行排序。可以为字段增加附属属性来进行排序。

## 高亮

### 示例1

```http
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "match": {
      "title": "诛仙"
    }
  },
  "highlight": {
    "fields": {
      "title": {}
    }
  },
  "_source": [
    "title",
    "author",
    "scorerCount"
  ]
}
```

### 示例2

```http
###
POST http://192.168.248.128:9200/book/_doc/_search
Content-Type: application/json

{
  "query": {
    "match": {
      "title": "诛仙"
    }
  },
  "highlight": {
    "pre_tags": ["<span>"],
    "post_tags": ["</span>"],
    "fields": {
      "title": {}
    }
  },
  "_source": [
    "title",
    "author",
    "scorerCount"
  ]
}
```

# 深度分页

分页的深度有限制，《=10000。

index.max_result_window=10000是默认值，可以调整来规避这个限制。

# 滚动搜索

## 示例1

```http
POST http://192.168.248.128:9200/book/_search?scroll=1m
Content-Type: application/json

{
  "query": {
    "match_all": {
    }
  },
  "_source": [
    "title",
    "author"
  ],
  "sort": [
    "_doc"
  ],
  "size": 2
}
```

## 示例2

```http
POST http://192.168.248.128:9200/_search/scroll
Content-Type: application/json

{
  "scroll_id": "FGluY2x1ZGVfY29udGV4dF91dWlkDnF1ZXJ5VGhlbkZldGNoAxZwcEhCbXBDT1NHdVdUakx5LWwyd21RAAAAAAAAAIEWVFJJZEJ4NFJRS2lPcWIzdFhsNi1xdxZwcEhCbXBDT1NHdVdUakx5LWwyd21RAAAAAAAAAIIWVFJJZEJ4NFJRS2lPcWIzdFhsNi1xdxZwcEhCbXBDT1NHdVdUakx5LWwyd21RAAAAAAAAAIAWVFJJZEJ4NFJRS2lPcWIzdFhsNi1xdw==",
  "scroll": "1m"
}
```

# 批量操作

## 查询

```http
POST http://192.168.248.128:9200/book/_doc/_mget
Content-Type: application/json

{
  "ids": [
    "1",
    "2",
    "3"
  ]
}
```

```json
{
  "docs": [
    {
      "_index": "book",
      "_type": "_doc",
      "_id": "1",
      "_version": 1,
      "_seq_no": 0,
      "_primary_term": 1,
      "found": true,
      "_source": {
        "bookId": 7639,
        "score": 7.2,
        "scorerCount": 3206,
        "title": "大道争锋",
        "author": "误道者",
        "countWord": 7521900
      }
    },
    {
      "_index": "book",
      "_type": "_doc",
      "_id": "2",
      "_version": 1,
      "_seq_no": 1,
      "_primary_term": 1,
      "found": true,
      "_source": {
        "bookId": 121383,
        "score": 6.9,
        "scorerCount": 3203,
        "title": "极道天魔",
        "author": "滚开",
        "countWord": 3854700
      }
    },
    {
      "_index": "book",
      "_type": "_doc",
      "_id": "3",
      "_version": 1,
      "_seq_no": 2,
      "_primary_term": 1,
      "found": true,
      "_source": {
        "bookId": 6999,
        "score": 8.1,
        "scorerCount": 2681,
        "title": "灭运图录",
        "author": "爱潜水的乌贼",
        "countWord": 2846600
      }
    }
  ]
}

```

## bulk

### create

如果文档不存在，那么就创建它。存在会报错。发生异常报错不会影响其他操作。

Postman

```
POST http://192.168.248.128:9200/_bulk
Content-Type:application/json

{"create": {"_index": "book","_type": "_doc", "_id": "21"}}
{"bookId": "5566","title": "诛仙1"}
{"create": {"_index": "book","_type": "_doc", "_id": "22"}}
{"bookId": "556677","title": "诛仙2"}
// 最后一行要回车
```



```
POST http://192.168.248.128:9200/book/_doc/_bulk
Content-Type:application/json

{"create": {"_id": "21"}}
{"bookId": "5566","title": "诛仙1"}
{"create": {"_id": "22"}}
{"bookId": "556677","title": "诛仙2"}
// 最后一行要回车
```



### index

创建一个新文档或者替换一个现有的文档。

```
POST http://192.168.248.128:9200/book/_doc/_bulk
Content-Type:application/json

{"index": {"_id": "21"}}
{"bookId": "5566","title": "诛仙11"}
{"index": {"_id": "22"}}
{"bookId": "556677","title": "诛仙22"}
{"index": {"_id": "23"}}
{"bookId": "55667788","title": "诛仙33"}
```



### update

部分更新一个文档

### delete

删除一个文档

# 集群

## 集群基本概念

![](img\4.png)

## 搭建集群

用到时再学习并记录。

## 集群分片

![](img\5.png)

## 宕机测试

当某个节点宕机之后，分片及副本会在节点之间重新分配，保证整个集群数据的完整性。

## 集群脑裂

**什么是脑裂**

如果发生网络中断或者服务器宕机，那么集群会有可能被划分为两个部分，各自有自己的master来管理，那么这就是脑裂。

**脑裂解决方案**

master主节点要经过多个master节点共同选举后才能成为新的主节点。就跟班级里选班长一样，并不是你1个人能决定的，需要班里半数以上的人决定。

解决实现原理：半数以上的节点同意选举，节点方可成为新的master。

discovery.zen.minimum_master_nodes=(N/2)+1

N为集群的中master节点的数量，也就是那些 node.master=true 设置的那些服务器节点总数。

**ES 7.X**

在最新版7.x中， minimum_master_node 这个参数已经被移除了，这一块内容完全由es自身去管理，这样就避免了脑裂的问题，选举也会非常快。

## 集群中文档读写原理

![](img\6.png)

![](img\7.png)



# Logstash

数据同步。