package org.apache.iotdb.admin.service.impl;

import org.apache.iotdb.admin.mapper.DemoUserMapper;
import org.apache.iotdb.admin.model.entity.DemoUser;
import org.apache.iotdb.admin.service.DemoUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 目的在提供编程的例子，你可以删除Demo开始的文件，不会报错。
 * <p>
 * 请注意extends和implements分别实现的是什么
 *
 * @author fanli
 */
@Service
public class DemoUserServiceImpl extends ServiceImpl<DemoUserMapper, DemoUser> implements DemoUserService {

    @Autowired
    private DemoUserMapper demoUserMapper;

    @Override
    public List<DemoUser> selectByXml() {
        return demoUserMapper.selectUserInfo();
    }
}
