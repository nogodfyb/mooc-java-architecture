package com.cxytiandi.sjdbc.service;

import java.util.List;

import com.cxytiandi.sjdbc.po.User;

public interface UserService {

	List<User> list();

	List<User> listSlave();

	Long add(User user);

}
