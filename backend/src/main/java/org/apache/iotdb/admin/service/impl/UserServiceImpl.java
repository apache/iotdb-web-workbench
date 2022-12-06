/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.admin.service.impl;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.mapper.UserMapper;
import org.apache.iotdb.admin.model.entity.User;
import org.apache.iotdb.admin.service.UserService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

  @Autowired private UserMapper userMapper;

  @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Override
  public User login(String name, String password) throws BaseException {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("name", name);
    User user = userMapper.selectOne(queryWrapper);
    if (user != null) {
      if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
        throw new BaseException(ErrorCode.LOGIN_FAIL_PWD, ErrorCode.LOGIN_FAIL_PWD_MSG);
      }
      return user;
    } else {
      throw new BaseException(ErrorCode.LOGIN_FAIL_USER, ErrorCode.LOGIN_FAIL_USER_MSG);
    }
  }

  @Override
  public void insert(User user) throws BaseException {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("name", user.getName());
    User existUser = userMapper.selectOne(queryWrapper);
    if (existUser == null) {
      user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
      try {
        userMapper.insert(user);
      } catch (Exception e) {
        throw new BaseException(ErrorCode.INSERT_USER_FAIL, ErrorCode.INSERT_USER_FAIL_MSG);
      }
    } else {
      throw new BaseException(ErrorCode.USER_EXIST, ErrorCode.USER_EXIST_MSG);
    }
  }

  @Override
  public void delete(Integer userId) throws BaseException {
    try {
      userMapper.deleteById(userId);
    } catch (Exception e) {
      throw new BaseException(ErrorCode.DELETE_USER_FAIL, ErrorCode.DELETE_USER_FAIL_MSG);
    }
  }
}
