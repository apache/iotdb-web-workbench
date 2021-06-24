package org.apache.iotdb.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.mapper.GroupMapper;
import org.apache.iotdb.admin.model.dto.GroupDTO;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.entity.StorageGroup;
import org.apache.iotdb.admin.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @anthor fyx 2021/6/16
 */
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, StorageGroup> implements GroupService {


    @Autowired
    private GroupMapper groupMapper;

    @Override
    public List<String> getGroupDescription(Integer serverId, List<String> groupNames) throws BaseException {
        List<String> descriptions = new ArrayList<>();
        for (String groupName : groupNames) {
            descriptions.add(getDescription(serverId,groupName));
        }
        return descriptions;
    }

    @Override
    public void setStorageGroupInfo(Connection connection, GroupDTO groupDTO) throws BaseException {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("connection_id",connection.getId());
        queryWrapper.eq("group_name",groupDTO.getGroupName());
        StorageGroup storageGroup = groupMapper.selectOne(queryWrapper);
        if (storageGroup != null) {
            storageGroup.setDescription(groupDTO.getDescription());
            int flag = groupMapper.updateById(storageGroup);
            if (flag <= 0) {
                throw new BaseException(ErrorCode.INSERT_GROUP_INFO_FAIL,ErrorCode.INSERT_GROUP_INFO_FAIL_MSG);
            }
            return;
        }
        StorageGroup group = new StorageGroup();
        group.setConnectionId(connection.getId());
        group.setCreateTime(System.currentTimeMillis());
        group.setCreator(connection.getUsername());
        group.setGroupName(groupDTO.getGroupName());
        group.setDescription(groupDTO.getDescription());
        int flag = groupMapper.insert(group);
        if (flag <= 0) {
            throw new BaseException(ErrorCode.INSERT_GROUP_INFO_FAIL,ErrorCode.INSERT_GROUP_INFO_FAIL_MSG);
        }
    }

    @Override
    public boolean isExist(Integer serverId, String groupName) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("connection_id",serverId);
        queryWrapper.eq("group_name",groupName);
        StorageGroup group = groupMapper.selectOne(queryWrapper);
        if (group != null) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteGroupInfo(Integer serverId, String groupName) throws BaseException {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("connection_id",serverId);
        queryWrapper.eq("group_name",groupName);
        int flag = groupMapper.delete(queryWrapper);
        if (flag <= 0) {
            throw new BaseException(ErrorCode.DELETE_GROUP_INFO_FAIL,ErrorCode.DELETE_GROUP_INFO_FAIL_MSG);
        }
    }

    private String getDescription(Integer serverId, String groupName) throws BaseException {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("connection_id",serverId);
        queryWrapper.eq("group_name",groupName);
        StorageGroup storageGroup = groupMapper.selectOne(queryWrapper);
        if (storageGroup != null) {
            return storageGroup.getDescription();
        }
        return null;
    }
}
