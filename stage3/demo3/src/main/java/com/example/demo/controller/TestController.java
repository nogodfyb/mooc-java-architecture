package com.example.demo.controller;

import com.example.demo.component.RabbitSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fyb
 * @since 2022/3/4
 */

@RestController
public class TestController {

    @Autowired
    private RabbitSender rabbitSender;


    @GetMapping("test")
    public void test() {


    }


}
