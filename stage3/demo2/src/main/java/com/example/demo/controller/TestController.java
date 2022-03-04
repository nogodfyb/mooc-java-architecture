package com.example.demo.controller;

import com.example.demo.Repository.StudentRepository;
import com.example.demo.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fyb
 * @since 2022/3/4
 */

@RestController
public class TestController {

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    @Autowired
    private StudentRepository studentRepository;


    @GetMapping("indexExists")
    public void indexExists() {


    }

    @GetMapping("createIndex")
    public void test() {


        Student student = new Student();
        student.setStuId(1L);
        student.setName("fyb");
        student.setAge(18);

        IndexQuery indexQuery = new IndexQueryBuilder().withObject(student).build();


    }


}
