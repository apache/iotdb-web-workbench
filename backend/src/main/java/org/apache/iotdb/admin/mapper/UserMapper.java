package org.apache.iotdb.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.iotdb.admin.model.entity.User;
import org.springframework.stereotype.Component;

/**
 * @anthor fyx 2021/5/24
 */

@Component
public interface UserMapper extends BaseMapper<User> {

}
