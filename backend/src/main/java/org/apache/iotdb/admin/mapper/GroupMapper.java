package org.apache.iotdb.admin.mapper;

import org.apache.iotdb.admin.model.entity.StorageGroup;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;

@Component
public interface GroupMapper extends BaseMapper<StorageGroup> {}
