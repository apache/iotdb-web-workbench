package org.apache.iotdb.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.iotdb.admin.model.entity.Connection;
import org.springframework.stereotype.Component;

/**
 * @anthor fyx 2021/5/25
 */
@Component
public interface ConnectionMapper extends BaseMapper<Connection> {
}
