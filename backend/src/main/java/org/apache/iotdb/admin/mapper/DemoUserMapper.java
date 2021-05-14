package org.apache.iotdb.admin.mapper;

import org.apache.iotdb.admin.model.entity.DemoUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * 目的在提供编程的例子，你可以删除Demo开始的文件，不会报错。
 * <p>
 * 不需要写任何代码，就可以实现增删改查
 *
 * @author fanli
 */
public interface DemoUserMapper extends BaseMapper<DemoUser> {

    /**
     * 查询用户信息
     *
     * @param id 用户id
     * @return 用户基本信息
     */
    List<DemoUser> selectUserInfo();
}
