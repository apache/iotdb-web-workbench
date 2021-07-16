package org.apache.iotdb.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.mapper.UserMapper;
import org.apache.iotdb.admin.model.entity.User;
import org.apache.iotdb.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
