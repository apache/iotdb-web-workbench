package org.apache.iotdb.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.iotdb.admin.model.entity.Query;
import org.springframework.stereotype.Component;

@Component
public interface QueryMapper extends BaseMapper<Query> {
}
