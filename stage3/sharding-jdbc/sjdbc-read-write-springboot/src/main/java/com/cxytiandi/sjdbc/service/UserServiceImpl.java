package com.cxytiandi.sjdbc.service;

import com.cxytiandi.sjdbc.po.User;
import com.cxytiandi.sjdbc.repository.UserRepository;
import org.apache.shardingsphere.api.hint.HintManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> list() {
        // 强制路由主库
        HintManager.getInstance().setMasterRouteOnly();
        return userRepository.list();
    }

    @Override
    public List<User> listSlave() {

        return userRepository.list();

    }


    public Long add(User user) {
        return userRepository.addUser(user);
    }

}
