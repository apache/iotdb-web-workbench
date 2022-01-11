/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.iotdb.admin.service.impl;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.mapper.GroupMapper;
import org.apache.iotdb.admin.model.dto.GroupDTO;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.entity.StorageGroup;
import org.apache.iotdb.admin.service.GroupService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, StorageGroup>
    implements GroupService {

  @Autowired private GroupMapper groupMapper;

  @Override
  public List<String> getGroupDescription(String host, List<String> groupNames)
      throws BaseException {
    List<String> descriptions = new ArrayList<>();
    for (String groupName : groupNames) {
      descriptions.add(getDescription(host, groupName));
    }
    return descriptions;
  }

  @Override
  public void setStorageGroupInfo(Connection connection, GroupDTO groupDTO) throws BaseException {
    QueryWrapper queryWrapper = new QueryWrapper();
    String host = connection.getHost();
    String groupName = groupDTO.getGroupName();
    String description = groupDTO.getDescription();
    queryWrapper.eq("host", host);
    queryWrapper.eq("group_name", groupName);
    StorageGroup storageGroup = groupMapper.selectOne(queryWrapper);
    if (storageGroup != null) {
      storageGroup.setDescription(description);
      int flag = groupMapper.updateById(storageGroup);
      if (flag <= 0) {
        throw new BaseException(
            ErrorCode.INSERT_GROUP_INFO_FAIL, ErrorCode.INSERT_GROUP_INFO_FAIL_MSG);
      }
      return;
    }
    String username = connection.getUsername();
    StorageGroup group = new StorageGroup();
    group.setCreateTime(System.currentTimeMillis());
    group.setCreator(username);
    group.setGroupName(groupName);
    group.setDescription(description);
    group.setHost(host);
    int flag = groupMapper.insert(group);
    if (flag <= 0) {
      throw new BaseException(
          ErrorCode.INSERT_GROUP_INFO_FAIL, ErrorCode.INSERT_GROUP_INFO_FAIL_MSG);
    }
  }

  @Override
  public boolean isExist(String host, String groupName) {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("host", host);
    queryWrapper.eq("group_name", groupName);
    StorageGroup group = groupMapper.selectOne(queryWrapper);
    if (group != null) {
      return true;
    }
    return false;
  }

  @Override
  public void deleteGroupInfo(String host, String groupName) throws BaseException {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("host", host);
    queryWrapper.eq("group_name", groupName);
    try {
      groupMapper.delete(queryWrapper);
    } catch (Exception e) {
      throw new BaseException(
          ErrorCode.DELETE_GROUP_INFO_FAIL, ErrorCode.DELETE_GROUP_INFO_FAIL_MSG);
    }
  }

  @Override
  public StorageGroup getGroupInfo(String host, String groupName) {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("host", host);
    queryWrapper.eq("group_name", groupName);
    return groupMapper.selectOne(queryWrapper);
  }

  @Override
  public void updateStorageGroupInfo(Connection connection, GroupDTO groupDTO)
      throws BaseException {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("host", connection.getHost());
    queryWrapper.eq("group_name", groupDTO.getGroupName());
    StorageGroup storageGroup = groupMapper.selectOne(queryWrapper);
    if (storageGroup != null) {
      storageGroup.setDescription(groupDTO.getDescription());
      int flag = groupMapper.updateById(storageGroup);
      if (flag <= 0) {
        throw new BaseException(
            ErrorCode.UPDATE_GROUP_INFO_FAIL, ErrorCode.UPDATE_GROUP_INFO_FAIL_MSG);
      }
      return;
    }
    throw new BaseException(ErrorCode.NO_GROUP_INFO, ErrorCode.NO_GROUP_INFO_MSG);
  }

  @Override
  public Integer getGroupId(String host, String groupName) {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("host", host);
    queryWrapper.eq("group_name", groupName);
    StorageGroup group = groupMapper.selectOne(queryWrapper);
    if (group != null) {
      return group.getId();
    }
    return null;
  }

  private String getDescription(String host, String groupName) throws BaseException {
    QueryWrapper queryWrapper = new QueryWrapper();
    queryWrapper.eq("host", host);
    queryWrapper.eq("group_name", groupName);
    StorageGroup storageGroup = groupMapper.selectOne(queryWrapper);
    if (storageGroup != null) {
      return storageGroup.getDescription();
    }
    return null;
  }
}
