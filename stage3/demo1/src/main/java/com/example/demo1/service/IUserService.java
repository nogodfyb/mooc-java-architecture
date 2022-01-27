package com.example.demo1.service;

import com.example.demo1.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fyb
 * @since 2022-01-27
 */
public interface IUserService extends IService<User> {

    User queryUserForLogin(String username, String password);
}
