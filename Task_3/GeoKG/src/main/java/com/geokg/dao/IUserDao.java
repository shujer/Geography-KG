package com.geokg.dao;

import com.geokg.pojo.User;

public interface IUserDao {
    User selectUser(long id);
}
