package com.geokg.service.impl;
import com.geokg.dao.IUserDao;
import com.geokg.pojo.User;
import com.geokg.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("userService")
public class UserServiceImpl implements IUserService {

    @Resource
    private IUserDao userDao;

    public User selectUser(long userId) {
        return this.userDao.selectUser(userId);
    }

}