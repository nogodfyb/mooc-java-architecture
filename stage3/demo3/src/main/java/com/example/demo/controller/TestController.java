package com.example.demo.controller;

import com.example.demo.component.RabbitSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fyb
 * @since 2022/3/4
 */

@RestController
public class TestController {

    @Autowired
    private RabbitSender rabbitSender;


    @GetMapping("test")
    public void test() throws Exception {

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("number", "12345");
        properties.put("send_time", LocalDateTime.now().toString());
        rabbitSender.send("Hello Rabbirmq For springboot!", properties);

    }


}
