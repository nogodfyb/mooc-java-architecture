package com.example.demo1;

import com.example.demo1.util.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @author fyb
 * @since 2022/1/25
 */
@RestController
public class Controller {

    @Autowired
    private RedisOperator redisOperator;

    public static final String REDIS_USER_TOKEN = "redis_user_token";


    /**
     * spring session的方式一是有学习成本
     * 二是不便于掌控,和其他语言写的服务共享session
     * 建议放弃这种耦合较大的会话方式
     */
    @GetMapping("/index/{param}")
    public String index(HttpServletRequest request, @PathVariable String param) {

        HttpSession session = request.getSession();
        session.setAttribute("userInfo", "userInfo:" + param);
        return "index";

    }


    @PostMapping("/login1")
    public String login() {


        String token = UUID.randomUUID().toString().trim();

        redisOperator.set(REDIS_USER_TOKEN + ":" + "userId---xxx",
                token);

        return "login1";

    }

    @PostMapping("api/getUser")
    public String getUser() {


        return "ok";
    }


}
