package org.apache.iotdb.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.iotdb.admin.mapper.GroupMapper;
import org.apache.iotdb.admin.model.entity.Group;
import org.apache.iotdb.admin.service.GroupService;
import org.springframework.stereotype.Service;

/**
 * @anthor fyx 2021/6/16
 */
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {
}
