package org.apache.iotdb.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.mapper.UserMapper;
import org.apache.iotdb.admin.model.entity.User;
import org.apache.iotdb.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @anthor fyx 2021/5/24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String name,String password) throws BaseException {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("name",name);
        queryWrapper.eq("password",password);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            return user;
        }else {
            throw new BaseException(2001,"登录失败,用户不存在或账号密码错误");
        }
    }
}
