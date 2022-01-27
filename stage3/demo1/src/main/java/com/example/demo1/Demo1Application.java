package com.example.demo1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
//@EnableRedisHttpSession
public class Demo1Application {


    public static void main(String[] args) {

        SpringApplication.run(Demo1Application.class, args);

    }


}
