package com.example.tccdemo.controller;

import com.example.tccdemo.service.PaymentServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class PaymentController {
    @Autowired
    private PaymentServcie paymentServcie;

    @RequestMapping("payment")
    public String payment(int userId, int orderId, BigDecimal amount) throws Exception {

        // 本地消息表版本
//        int result = paymentServcie.pament(userId, orderId, amount);

        // 消息队列版本
         int result = paymentServcie.pamentMQ(userId, orderId, amount);

        return "支付结果："+result;
    }
}
