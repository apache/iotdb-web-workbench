package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.entity.User;

/**
 * @anthor fyx 2021/5/24
 */
public interface UserService {
    User login(String name, String password) throws BaseException;
}
