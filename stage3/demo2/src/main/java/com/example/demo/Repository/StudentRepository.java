package com.example.demo.Repository;

import com.example.demo.entity.Student;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author fyb
 * @since 2022/3/4
 */
public interface StudentRepository extends ElasticsearchRepository<Student, String> {



}
