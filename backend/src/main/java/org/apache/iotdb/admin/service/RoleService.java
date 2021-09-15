package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.dto.IotDBRole;
import org.apache.iotdb.admin.model.entity.Role;

public interface RoleService {
  void upsertRoleInfo(String host, Integer port, IotDBRole iotDBRole) throws BaseException;

  void deleteRoleInfo(String host, Integer port, String roleName) throws BaseException;

  Role getRoleInfo(String host, Integer port, String roleName) throws BaseException;
}
