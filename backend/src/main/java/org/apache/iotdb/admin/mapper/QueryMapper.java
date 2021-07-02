package org.apache.iotdb.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.iotdb.admin.model.entity.Query;
import org.springframework.stereotype.Component;

/**
 * @anthor fyx 2021/7/1
 */
@Component
public interface QueryMapper extends BaseMapper<Query> {
}
