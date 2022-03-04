package com.example.demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author fyb
 * @since 2022/3/4
 */
@Data
@Document(indexName = "student")
public class Student {


    @Id
    private Long stuId;

    @Field(store = true)
    private String name;

    @Field(store = true)
    private int age;


}
