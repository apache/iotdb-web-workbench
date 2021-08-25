package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.entity.User;

public interface UserService {
    User login(String name, String password) throws BaseException;

    void insert(User user) throws BaseException;

    void delete(Integer userId) throws BaseException;
}
