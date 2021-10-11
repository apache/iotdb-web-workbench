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
import org.apache.iotdb.admin.model.dto.*;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.*;
import org.apache.iotdb.admin.service.IotDBService;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionDataSetWrapper;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.tsfile.file.metadata.enums.CompressionType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;
import org.apache.iotdb.tsfile.read.common.RowRecord;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class IotDBServiceImpl implements IotDBService {

  private static final Logger logger = LoggerFactory.getLogger(IotDBServiceImpl.class);

  private static final HashMap<String, Boolean> SPECIAL_PRIVILEGES = new HashMap();

  private static final String NO_NEED_PRIVILEGES = "SET_STORAGE_GROUP";

  private static final List<String> PRIVILEGES = new ArrayList<>();

  private static final Set<String> AUTHORITY_PRIVILEGES = new HashSet<>();

  private static final Set<String> DATA_PRIVILEGES = new HashSet<>();

  private static final HashMap<String, Boolean> QUERY_STOP = new HashMap<>();

  static {
    SPECIAL_PRIVILEGES.put("CREATE_TIMESERIES", true);
    SPECIAL_PRIVILEGES.put("INSERT_TIMESERIES", true);
    SPECIAL_PRIVILEGES.put("READ_TIMESERIES", true);
    SPECIAL_PRIVILEGES.put("DELETE_TIMESERIES", true);
  }

  static {
    PRIVILEGES.add("SET_STORAGE_GROUP");
    PRIVILEGES.add("CREATE_TIMESERIES");
    PRIVILEGES.add("INSERT_TIMESERIES");
    PRIVILEGES.add("READ_TIMESERIES");
    PRIVILEGES.add("DELETE_TIMESERIES");
    PRIVILEGES.add("CREATE_USER");
    PRIVILEGES.add("DELETE_USER");
    PRIVILEGES.add("MODIFY_PASSWORD");
    PRIVILEGES.add("LIST_USER");
    PRIVILEGES.add("GRANT_USER_PRIVILEGE");
    PRIVILEGES.add("REVOKE_USER_PRIVILEGE");
    PRIVILEGES.add("CREATE_FUNCTION");
    PRIVILEGES.add("DROP_FUNCTION");
    PRIVILEGES.add("CREATE_TRIGGER");
    PRIVILEGES.add("DROP_TRIGGER");
    PRIVILEGES.add("START_TRIGGER");
    PRIVILEGES.add("STOP_TRIGGER");
  }

  static {
    AUTHORITY_PRIVILEGES.add("CREATE_USER");
    AUTHORITY_PRIVILEGES.add("DELETE_USER");
    AUTHORITY_PRIVILEGES.add("MODIFY_PASSWORD");
    AUTHORITY_PRIVILEGES.add("LIST_USER");
    AUTHORITY_PRIVILEGES.add("GRANT_USER_PRIVILEGE");
    AUTHORITY_PRIVILEGES.add("REVOKE_USER_PRIVILEGE");
    AUTHORITY_PRIVILEGES.add("GRANT_USER_ROLE");
    AUTHORITY_PRIVILEGES.add("REVOKE_USER_ROLE");
    AUTHORITY_PRIVILEGES.add("CREATE_ROLE");
    AUTHORITY_PRIVILEGES.add("DELETE_ROLE");
    AUTHORITY_PRIVILEGES.add("LIST_ROLE");
    AUTHORITY_PRIVILEGES.add("GRANT_ROLE_PRIVILEGE");
    AUTHORITY_PRIVILEGES.add("REVOKE_ROLE_PRIVILEGE");
    AUTHORITY_PRIVILEGES.add("CREATE_FUNCTION");
    AUTHORITY_PRIVILEGES.add("DROP_FUNCTION");
    AUTHORITY_PRIVILEGES.add("CREATE_TRIGGER");
    AUTHORITY_PRIVILEGES.add("DROP_TRIGGER");
    AUTHORITY_PRIVILEGES.add("START_TRIGGER");
    AUTHORITY_PRIVILEGES.add("STOP_TRIGGER");
  }

  static {
    DATA_PRIVILEGES.add("SET_STORAGE_GROUP");
    DATA_PRIVILEGES.add("CREATE_TIMESERIES");
    DATA_PRIVILEGES.add("INSERT_TIMESERIES");
    DATA_PRIVILEGES.add("READ_TIMESERIES");
    DATA_PRIVILEGES.add("DELETE_TIMESERIES");
  }

  @Override
  public DataCountVO getDataCount(Connection connection) throws BaseException {
    SessionPool sessionPool = null;
    try {
      sessionPool = getSessionPool(connection);
      String groupCountStr = executeQueryOneValue(sessionPool, "count storage group");
      int groupCount = Integer.parseInt(groupCountStr);
      String deviceCountStr = executeQueryOneValue(sessionPool, "count devices");
      int deviceCount = Integer.parseInt(deviceCountStr);
      String measurementCountStr = executeQueryOneValue(sessionPool, "count timeseries");
      int measurementCount = Integer.parseInt(measurementCountStr);
      List<String> dataCountList = executeQueryOneLine(sessionPool, "select count(*) from root");
      int dataCount = 0;
      for (String dataCountStr : dataCountList) {
        dataCount += Integer.parseInt(dataCountStr);
      }
      DataCountVO dataCountVO = new DataCountVO();
      dataCountVO.setGroupCount(groupCount);
      dataCountVO.setDeviceCount(deviceCount);
      dataCountVO.setMeasurementCount(measurementCount);
      dataCountVO.setDataCount(dataCount);
      return dataCountVO;
    } catch (NumberFormatException e) {
      throw new BaseException(ErrorCode.GET_DATA_COUNT_FAIL, ErrorCode.GET_DATA_COUNT_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public DataModelVO getDataModel(Connection connection) throws BaseException {
    SessionPool sessionPool = null;
    try {
      sessionPool = getSessionPool(connection);
      DataModelVO root = new DataModelVO("root");
      assembleDataModel(root, "root", sessionPool);
      root.setGroupCount(getGroupCount(sessionPool));
      root.setPath("root");
      return root;
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  private void assembleDataModel(DataModelVO node, String prefixPath, SessionPool sessionPool)
      throws BaseException {
    Set<String> childrenNode = getChildrenNode(prefixPath, sessionPool);
    if (childrenNode == null) {
      return;
    }
    for (String child : childrenNode) {
      DataModelVO childNode = new DataModelVO(child);
      assembleDataModel(childNode, prefixPath + "." + child, sessionPool);
      setNodeInfo(childNode, sessionPool, prefixPath + "." + child);
      node.initNodeChildren().add(childNode);
    }
  }

  private Set<String> getChildrenNode(String prefixPath, SessionPool sessionPool)
      throws BaseException {
    String sql = "show storage group " + prefixPath;
    List<String> children = executeQueryOneColumn(sessionPool, sql);
    if (children.size() == 0 || (children.size() == 1 && children.get(0).equals(prefixPath))) {
      sql = "show timeseries " + prefixPath;
      children = executeQueryOneColumn(sessionPool, sql);
      if (children.size() == 0 || (children.size() == 1 && children.get(0).equals(prefixPath))) {
        return null;
      }
    }
    Set<String> childrenNode = new HashSet<>();
    for (String child : children) {
      child = StringUtils.removeStart(child, prefixPath + ".").split("\\.")[0];
      childrenNode.add(child);
    }
    return childrenNode;
  }

  private Integer getGroupCount(SessionPool sessionPool) throws BaseException {
    String sql = "count storage group";
    String value = executeQueryOneValue(sessionPool, sql);
    Integer count = Integer.valueOf(value);
    return count;
  }

  private Integer getDeviceCount(SessionPool sessionPool, String groupName) throws BaseException {
    String sql = "count devices " + groupName;
    String value = executeQueryOneValue(sessionPool, sql);
    Integer count = Integer.valueOf(value);
    return count;
  }

  private Integer getMeasurementsCount(SessionPool sessionPool, String deviceName)
      throws BaseException {
    String sql = "count timeseries " + deviceName;
    String value = executeQueryOneValue(sessionPool, sql);
    Integer count = Integer.valueOf(value);
    return count;
  }

  private boolean isGroup(SessionPool sessionPool, String path) throws BaseException {
    String sql = "show storage group " + path;
    List<String> groups = executeQueryOneColumn(sessionPool, sql);
    boolean isGroup = false;
    for (String group : groups) {
      if (group.equals(path)) {
        isGroup = true;
        break;
      }
    }
    return isGroup;
  }

  private boolean isDevice(SessionPool sessionPool, String path) throws BaseException {
    String sql = "show devices " + path;
    List<String> devices = executeQueryOneColumn(sessionPool, sql);
    boolean isDevice = false;
    for (String device : devices) {
      if (device.equals(path)) {
        isDevice = true;
        break;
      }
    }
    return isDevice;
  }

  private boolean isMeasurement(SessionPool sessionPool, String path) throws BaseException {
    String sql = "show timeseries " + path;
    List<String> measurements = executeQueryOneColumn(sessionPool, sql);
    boolean isMeasurement = false;
    for (String measurement : measurements) {
      if (measurement.equals(path)) {
        isMeasurement = true;
        break;
      }
    }
    return isMeasurement;
  }

  private void setNodeInfo(DataModelVO dataModelVO, SessionPool sessionPool, String path)
      throws BaseException {
    dataModelVO.setPath(path);
    if (isGroup(sessionPool, path)) {
      dataModelVO.setDeviceCount(getDeviceCount(sessionPool, path));
      dataModelVO.setIsGroup(true);
    }
    if (isDevice(sessionPool, path)) {
      dataModelVO.setMeasurementCount(getMeasurementsCount(sessionPool, path));
      dataModelVO.setIsDevice(true);
      return;
    }
    if (isMeasurement(sessionPool, path)) {
      DataInfo dataInfo = new DataInfo();
      dataInfo.setNewValue(getLastValue(sessionPool, path));
      dataInfo.setDataCount(getOneDataCount(sessionPool, path));
      dataInfo.setDataType(getDataType(sessionPool, path));
      dataModelVO.setDataInfo(dataInfo);
      dataModelVO.setIsMeasurement(true);
    }
  }

  private String getLastValue(SessionPool sessionPool, String timeseries) throws BaseException {
    int index = timeseries.lastIndexOf(".");
    String sql =
        "select last_value("
            + timeseries.substring(index + 1)
            + ") from "
            + timeseries.substring(0, index);
    String value = executeQueryOneValue(sessionPool, sql);
    return value;
  }

  private Integer getOneDataCount(SessionPool sessionPool, String timeseries) throws BaseException {
    int index = timeseries.lastIndexOf(".");
    String sql = "select count(*) from " + timeseries.substring(0, index);
    String countStr = executeQueryOneLine(sessionPool, sql, "count(" + timeseries + ")");
    return Integer.parseInt(countStr);
  }

  private String getDataType(SessionPool sessionPool, String timeseries) throws BaseException {
    String sql = "show timeseries " + timeseries;
    String dataType = executeQueryOneLine(sessionPool, sql, "dataType");
    return dataType;
  }

  @Override
  public List<String> getAllStorageGroups(Connection connection) throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    List<String> groups;
    try {
      String sql = "show storage group";
      groups = executeQueryOneColumn(sessionPool, sql);
      return groups;
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public List<NodeTreeVO> getGroupsNodeTree(Connection connection) throws BaseException {
    SessionPool sessionPool = null;
    try {
      sessionPool = getSessionPool(connection);
      return getGroupsNodeTree(sessionPool);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  private List<NodeTreeVO> getGroupsNodeTree(SessionPool sessionPool) throws BaseException {
    Set<String> firstLevelNodes = getChildrenNode("root", "storage group", sessionPool);
    if (firstLevelNodes == null || firstLevelNodes.size() == 0) {
      return null;
    }
    List<NodeTreeVO> groupNodeVOList = new ArrayList<>();
    for (String firstLevelNodeName : firstLevelNodes) {
      NodeTreeVO firstLevelNode = new NodeTreeVO(firstLevelNodeName);
      groupNodeVOList.add(firstLevelNode);
      assembleNodeTree(firstLevelNode, firstLevelNodeName, "storage group", sessionPool);
    }
    return groupNodeVOList;
  }

  private void assembleNodeTree(
      NodeTreeVO node, String prefixPath, String type, SessionPool sessionPool)
      throws BaseException {
    Set<String> childrenNode = getChildrenNode(prefixPath, type, sessionPool);
    if (childrenNode == null) {
      return;
    }
    for (String child : childrenNode) {
      NodeTreeVO childNode = new NodeTreeVO(child);
      assembleNodeTree(childNode, child, type, sessionPool);
      node.initChildren().add(childNode);
    }
  }

  private Set<String> getChildrenNode(String prefixPath, String type, SessionPool sessionPool)
      throws BaseException {
    String sql = "show " + type + " " + prefixPath;
    List<String> children = executeQueryOneColumn(sessionPool, sql);
    if (children.size() == 0 || (children.size() == 1 && children.get(0).equals(prefixPath))) {
      return null;
    }
    Set<String> childrenNode = new HashSet<>();
    for (String child : children) {
      if (child.equals(prefixPath)) {
        continue;
      }
      child = prefixPath + "." + StringUtils.removeStart(child, prefixPath + ".").split("\\.")[0];
      childrenNode.add(child);
    }
    return childrenNode;
  }

  @Override
  public void saveStorageGroup(Connection connection, String groupName) throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    try {
      sessionPool.setStorageGroup(groupName);
    } catch (StatementExecutionException e) {
      // 300为存储组重复或者其前/后路径上已经有存储组了
      if (e.getStatusCode() == 602) {
        throw new BaseException(ErrorCode.NO_PRI_SET_GROUP, ErrorCode.NO_PRI_SET_GROUP_MSG);
      }
      if (e.getStatusCode() == 300) {
        throw new BaseException(ErrorCode.SET_GROUP_FAIL, ErrorCode.SET_GROUP_FAIL_MSG);
      }
      logger.error(e.getMessage());
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.SET_GROUP_FAIL, ErrorCode.SET_GROUP_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public void deleteStorageGroup(Connection connection, String groupName) throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    try {
      sessionPool.deleteStorageGroup(groupName);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      if (e.getStatusCode() == 602) {
        throw new BaseException(ErrorCode.NO_PRI_DELETE_GROUP, ErrorCode.NO_PRI_DELETE_GROUP_MSG);
      }
      throw new BaseException(ErrorCode.DELETE_GROUP_FAIL, ErrorCode.DELETE_GROUP_FAIL_MSG);
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.DELETE_GROUP_FAIL, ErrorCode.DELETE_GROUP_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public CountDTO getDevicesByGroup(
      Connection connection, String groupName, Integer pageSize, Integer pageNum, String keyword)
      throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    String sql = "show devices " + groupName;
    SessionDataSetWrapper sessionDataSetWrapper = null;
    try {
      sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
      int batchSize = sessionDataSetWrapper.getBatchSize();
      List<String> values = new ArrayList<>();
      int count = 0;
      if (batchSize > 0) {
        while (sessionDataSetWrapper.hasNext()) {
          RowRecord rowRecord = sessionDataSetWrapper.next();
          List<org.apache.iotdb.tsfile.read.common.Field> fields = rowRecord.getFields();
          if (keyword != null || "".equals(keyword)) {
            String deviceName = fields.get(0).toString();
            if (deviceName.contains(keyword)) {
              count++;
            } else {
              continue;
            }
            if (count >= pageSize * (pageNum - 1) + 1 && count <= pageSize * pageNum) {
              values.add(fields.get(0).toString());
            }
          } else {
            count++;
            if (count >= pageSize * (pageNum - 1) + 1 && count <= pageSize * pageNum) {
              values.add(fields.get(0).toString());
            }
          }
        }
      }
      CountDTO countDTO = new CountDTO();
      countDTO.setObjects(values);
      countDTO.setTotalCount(count);
      Integer totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
      countDTO.setTotalPage(totalPage);
      return countDTO;
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(
          ErrorCode.GET_SQL_ONE_COLUMN_FAIL, ErrorCode.GET_SQL_ONE_COLUMN_FAIL_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(
          ErrorCode.GET_SQL_ONE_COLUMN_FAIL, ErrorCode.GET_SQL_ONE_COLUMN_FAIL_MSG);
    } finally {
      closeResultSet(sessionDataSetWrapper);
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public CountDTO getMeasurementsByDevice(
      Connection connection, String deviceName, Integer pageSize, Integer pageNum, String keyword)
      throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    String sql = "show timeseries " + deviceName;
    SessionDataSetWrapper sessionDataSetWrapper = null;
    try {
      sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
      List<MeasurementDTO> results = new ArrayList<>();
      int batchSize = sessionDataSetWrapper.getBatchSize();
      int count = 0;
      if (batchSize > 0) {
        while (sessionDataSetWrapper.hasNext()) {
          RowRecord rowRecord = sessionDataSetWrapper.next();
          List<org.apache.iotdb.tsfile.read.common.Field> fields = rowRecord.getFields();
          String measurementName = fields.get(0).toString();
          if (StringUtils.removeStart(measurementName, deviceName + ".").contains(".")) {
            continue;
          }
          if (keyword != null || "".equals(keyword)) {
            if (measurementName.contains(keyword)) {
              count++;
            } else {
              continue;
            }
            if (count >= pageSize * (pageNum - 1) + 1 && count <= pageSize * pageNum) {
              MeasurementDTO t = new MeasurementDTO();
              List<String> columnNames = sessionDataSetWrapper.getColumnNames();
              for (int i = 0; i < fields.size(); i++) {
                Field field =
                    MeasurementDTO.class.getDeclaredField(columnNames.get(i).replaceAll(" ", ""));
                field.setAccessible(true);
                field.set(t, fields.get(i).toString());
              }
              results.add(t);
            }
          } else {
            count++;
            if (count >= pageSize * (pageNum - 1) + 1 && count <= pageSize * pageNum) {
              MeasurementDTO t = new MeasurementDTO();
              List<String> columnNames = sessionDataSetWrapper.getColumnNames();
              for (int i = 0; i < fields.size(); i++) {
                Field field =
                    MeasurementDTO.class.getDeclaredField(columnNames.get(i).replaceAll(" ", ""));
                field.setAccessible(true);
                field.set(t, fields.get(i).toString());
              }
              results.add(t);
            }
          }
        }
      }
      CountDTO countDTO = new CountDTO();
      countDTO.setObjects(results);
      countDTO.setTotalCount(count);
      Integer totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
      countDTO.setTotalPage(totalPage);
      return countDTO;
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_MSM_FAIL, ErrorCode.GET_MSM_FAIL_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_MSM_FAIL, ErrorCode.GET_MSM_FAIL_MSG);
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_MSM_FAIL, ErrorCode.GET_MSM_FAIL_MSG);
    } finally {
      closeResultSet(sessionDataSetWrapper);
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public List<String> getIotDBUserList(Connection connection) throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    List<String> users;
    try {
      String sql = "list user";
      users = executeQueryOneColumn(sessionPool, sql);
      return users;
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public List<String> getIotDBRoleList(Connection connection) throws BaseException {
    SessionPool sessionPool = null;
    try {
      sessionPool = getSessionPool(connection);
      String sql = "list role";
      List<String> roles = executeQueryOneColumn(sessionPool, sql);
      sessionPool.close();
      return roles;
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public RoleVO getIotDBRoleInfo(Connection connection, String roleName) throws BaseException {
    SessionPool sessionPool = null;
    RoleVO roleVO = new RoleVO();
    try {
      sessionPool = getSessionPool(connection);
      String sql = "LIST ALL USER OF ROLE " + roleName;
      List<String> users = executeQueryOneColumn(sessionPool, sql);
      roleVO.setUserList(users);
      return roleVO;
    } catch (BaseException e) {
      throw new BaseException(ErrorCode.ROLE_GET_USERS_FAIL, ErrorCode.ROLE_GET_USERS_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public void deleteIotDBUser(Connection connection, String userName) throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    String sql = "drop user " + userName;
    try {
      sessionPool.executeNonQueryStatement(sql);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      if (e.getStatusCode() == 602) {
        throw new BaseException(ErrorCode.NO_PRI_DELETE_USER, ErrorCode.NO_PRI_DELETE_USER_MSG);
      } else {
        throw new BaseException(ErrorCode.DELETE_DB_USER_FAIL, ErrorCode.DELETE_DB_USER_FAIL_MSG);
      }
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.DELETE_DB_USER_FAIL, ErrorCode.DELETE_DB_USER_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public void deleteIotDBRole(Connection connection, String roleName) throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    String sql = "drop role " + roleName;
    try {
      sessionPool.executeNonQueryStatement(sql);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      if (e.getStatusCode() == 602) {
        throw new BaseException(ErrorCode.NO_PRI_DELETE_ROLE, ErrorCode.NO_PRI_DELETE_ROLE_MSG);
      } else {
        throw new BaseException(ErrorCode.DELETE_DB_ROLE_FAIL, ErrorCode.DELETE_DB_ROLE_FAIL_MSG);
      }
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.DELETE_DB_ROLE_FAIL, ErrorCode.DELETE_DB_ROLE_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public void setIotDBUser(Connection connection, IotDBUser iotDBUser) throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    String userName = iotDBUser.getUserName();
    String password = iotDBUser.getPassword();
    String sql = "create user " + userName + " '" + password + "'";
    try {
      sessionPool.executeNonQueryStatement(sql);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      if (e.getStatusCode() == 602) {
        throw new BaseException(ErrorCode.NO_PRI_CREATE_USER, ErrorCode.NO_PRI_CREATE_USER_MSG);
      } else {
        throw new BaseException(ErrorCode.SET_DB_USER_FAIL, ErrorCode.SET_DB_USER_FAIL_MSG);
      }
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.SET_DB_USER_FAIL, ErrorCode.SET_DB_USER_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public void setIotDBRole(Connection connection, IotDBRole iotDBRole) throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    String roleName = iotDBRole.getRoleName();
    String sql = "create role " + roleName;
    try {
      sessionPool.executeNonQueryStatement(sql);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      if (e.getStatusCode() == 602) {
        throw new BaseException(ErrorCode.NO_PRI_CREATE_ROLE, ErrorCode.NO_PRI_CREATE_ROLE_MSG);
      } else {
        throw new BaseException(ErrorCode.SET_DB_ROLE_FAIL, ErrorCode.SET_DB_ROLE_FAIL_MSG);
      }
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public UserRolesVO getRolesOfUser(Connection connection, String userName) throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    UserRolesVO userRolesVO = new UserRolesVO();
    if (userName.equals(connection.getUsername())) {
      userRolesVO.setPassword(connection.getPassword());
    } else {
      userRolesVO.setPassword(null);
    }
    String sql = "list all role of user " + userName;
    try {
      List<String> roleList = executeQueryOneColumn(sessionPool, sql);
      userRolesVO.setRoleList(roleList);
      return userRolesVO;
    } catch (BaseException e) {
      if (e.getErrorCode().equals(ErrorCode.NO_PRI_DO_THIS)) {
        throw new BaseException(ErrorCode.NO_PRI_LIST_ROLE, ErrorCode.NO_PRI_LIST_ROLE_MSG);
      } else {
        throw e;
      }
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public void userGrant(Connection connection, String userName, UserGrantDTO userGrantDTO)
      throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    List<String> roleList = userGrantDTO.getRoleList();
    List<String> cancelRoleList = userGrantDTO.getCancelRoleList();
    try {
      if (cancelRoleList != null && cancelRoleList.size() != 0) {
        for (String cancelRole : cancelRoleList) {
          revokeRole(sessionPool, userName, cancelRole);
        }
      }
      if (roleList != null && roleList.size() != 0) {
        for (String garntRole : roleList) {
          grantRole(sessionPool, userName, garntRole);
        }
      }
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public void roleGrant(Connection connection, String roleName, RoleGrantDTO roleGrantDTO)
      throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    List<String> userList = roleGrantDTO.getUserList();
    List<String> cancelUserList = roleGrantDTO.getCancelUserList();
    try {
      if (cancelUserList != null && cancelUserList.size() != 0) {
        for (String cancelUser : cancelUserList) {
          revokeRole(sessionPool, cancelUser, roleName);
        }
      }
      if (userList != null && userList.size() != 0) {
        for (String garntUser : userList) {
          grantRole(sessionPool, garntUser, roleName);
        }
      }
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  private void revokeRole(SessionPool sessionPool, String userName, String roleName)
      throws BaseException {
    String sql = "revoke " + roleName + " from " + userName;
    try {
      sessionPool.executeNonQueryStatement(sql);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      if (e.getStatusCode() == 602) {
        throw new BaseException(
            ErrorCode.NO_PRI_REVOKE_USER_ROLE, ErrorCode.NO_PRI_REVOKE_USER_ROLE_MSG);
      } else {
        throw new BaseException(ErrorCode.REVOKE_ROLE, ErrorCode.REVOKE_ROLE_MSG);
      }
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    }
  }

  private void grantRole(SessionPool sessionPool, String userName, String roleName)
      throws BaseException {
    String sql = "grant " + roleName + " to " + userName;
    try {
      sessionPool.executeNonQueryStatement(sql);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      if (e.getStatusCode() == 602) {
        throw new BaseException(
            ErrorCode.NO_PRI_GRANT_USER_ROLE, ErrorCode.NO_PRI_GRANT_USER_ROLE_MSG);
      } else {
        throw new BaseException(ErrorCode.GRANT_ROLE, ErrorCode.GRANT_ROLE_MSG);
      }
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    }
  }

  @Override
  public Set<String> getUserAuthorityPrivilege(Connection connection, String userName)
      throws BaseException {
    SessionPool sessionPool = null;
    SessionDataSetWrapper sessionDataSetWrapper = null;
    try {
      if ("root".equals(userName)) {
        return AUTHORITY_PRIVILEGES;
      }
      Set<String> privileges = new HashSet<>();
      List<String> rowInfos = new ArrayList<>();
      sessionPool = getSessionPool(connection);
      String sql = "list user privileges " + userName;
      sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
      while (sessionDataSetWrapper.hasNext()) {
        RowRecord next = sessionDataSetWrapper.next();
        List<org.apache.iotdb.tsfile.read.common.Field> fields = next.getFields();
        for (int i = 0; i < fields.size(); i++) {
          org.apache.iotdb.tsfile.read.common.Field field = fields.get(i);
          if (i == 0) {
            if (!"".equals(field.toString())) {
              break;
            }
          } else {
            rowInfos.add(field.toString());
          }
        }
      }
      privileges = switchRowInfosToAuthorityPrivileges(rowInfos);
      return privileges;
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(
          ErrorCode.GET_USER_PRIVILEGE_FAIL, ErrorCode.GET_USER_PRIVILEGE_FAIL_MSG);
    } finally {
      closeResultSet(sessionDataSetWrapper);
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public Set<String> getRoleAuthorityPrivilege(Connection connection, String roleName)
      throws BaseException {
    SessionPool sessionPool = null;
    try {
      Set<String> privileges = new HashSet<>();
      sessionPool = getSessionPool(connection);
      String sql = "list role privileges " + roleName;
      List<String> rowInfos = executeQueryOneColumn(sessionPool, sql);
      privileges = switchRowInfosToAuthorityPrivileges(rowInfos);
      return privileges;
    } catch (BaseException e) {
      if (e.getErrorCode().equals(ErrorCode.NO_PRI_DO_THIS)) {
        throw new BaseException(ErrorCode.NO_PRI_LIST_ROLE, ErrorCode.NO_PRI_LIST_ROLE_MSG);
      } else {
        throw new BaseException(
            ErrorCode.GET_ROLE_PRIVILEGE_FAIL, ErrorCode.GET_ROLE_PRIVILEGE_FAIL_MSG);
      }
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  private Set<String> switchRowInfosToAuthorityPrivileges(List<String> rowInfos) {
    Set<String> authorityPrivileges = new HashSet<>();
    for (String rowInfo : rowInfos) {
      String[] split = rowInfo.split("\\s:\\s");
      String[] privileges = split[1].split("\\s");
      for (String privilege : privileges) {
        if (AUTHORITY_PRIVILEGES.contains(privilege)) {
          authorityPrivileges.add(privilege);
        }
      }
    }
    return authorityPrivileges;
  }

  @Override
  public void upsertAuthorityPrivilege(
      Connection connection,
      String userName,
      AuthorityPrivilegeDTO authorityPrivilegeDTO,
      String userOrRole)
      throws BaseException {
    SessionPool sessionPool = null;
    try {
      sessionPool = getSessionPool(connection);
      List<String> cancelPrivileges = authorityPrivilegeDTO.getCancelPrivileges();
      if (cancelPrivileges != null) {
        checkAuthorityPrivilege(cancelPrivileges);
        for (String cancelPrivilege : cancelPrivileges) {
          upsertAuthorityPrivilege(sessionPool, "revoke", userOrRole, userName, cancelPrivilege);
        }
      }
      List<String> privileges = authorityPrivilegeDTO.getPrivileges();
      if (privileges != null) {
        checkAuthorityPrivilege(privileges);
        for (String privilege : privileges) {
          upsertAuthorityPrivilege(sessionPool, "grant", userOrRole, userName, privilege);
        }
      }
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  private void upsertAuthorityPrivilege(
      SessionPool sessionPool,
      String operationType,
      String userOrRole,
      String name,
      String privilegesStr)
      throws BaseException {
    String sql =
        operationType
            + " "
            + userOrRole
            + " "
            + name
            + " privileges '"
            + privilegesStr
            + "' on root";
    try {
      sessionPool.executeNonQueryStatement(sql);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    }
  }

  private void checkAuthorityPrivilege(List<String> privileges) throws BaseException {
    for (String privilege : privileges) {
      if (!AUTHORITY_PRIVILEGES.contains(privilege)) {
        throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
      }
    }
  }

  @Override
  public List<DataPrivilegeVO> getUserDataPrivilege(Connection connection, String userName)
      throws BaseException {
    if ("root".equalsIgnoreCase(userName)) {
      DataPrivilegeVO dataPrivilegeVO = new DataPrivilegeVO();
      dataPrivilegeVO.setType(0);
      dataPrivilegeVO.setPrivileges(new ArrayList<>(DATA_PRIVILEGES));
      return Arrays.asList(dataPrivilegeVO);
    }
    SessionPool sessionPool = null;
    SessionDataSetWrapper sessionDataSetWrapper = null;
    try {
      List<String> rowInfos = new ArrayList<>();
      sessionPool = getSessionPool(connection);
      String sql = "list user privileges " + userName;
      sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
      while (sessionDataSetWrapper.hasNext()) {
        RowRecord next = sessionDataSetWrapper.next();
        List<org.apache.iotdb.tsfile.read.common.Field> fields = next.getFields();
        for (int i = 0; i < fields.size(); i++) {
          org.apache.iotdb.tsfile.read.common.Field field = fields.get(i);
          if (i == 0) {
            if (!"".equals(field.toString())) {
              break;
            }
          } else {
            rowInfos.add(field.toString());
          }
        }
      }
      // rowInfos内容形式 "path : 权限1 权限2 权限3"
      List<DataPrivilegeVO> dataPrivilegeList =
          switchRowInfosToDataPrivileges(rowInfos, sessionPool);
      return dataPrivilegeList;
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(
          ErrorCode.GET_USER_PRIVILEGE_FAIL, ErrorCode.GET_USER_PRIVILEGE_FAIL_MSG);
    } finally {
      closeResultSet(sessionDataSetWrapper);
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public List<DataPrivilegeVO> getRoleDataPrivilege(Connection connection, String roleName)
      throws BaseException {
    SessionPool sessionPool = null;
    SessionDataSetWrapper sessionDataSetWrapper = null;
    try {
      sessionPool = getSessionPool(connection);
      String sql = "list role privileges " + roleName;
      List<String> rowInfos = executeQueryOneColumn(sessionPool, sql);

      // rowInfos内容形式 "path : 权限1 权限2 权限3"
      List<DataPrivilegeVO> dataPrivilegeList =
          switchRowInfosToDataPrivileges(rowInfos, sessionPool);
      return dataPrivilegeList;
    } catch (BaseException e) {
      logger.error(e.getMessage());
      throw new BaseException(
          ErrorCode.GET_ROLE_PRIVILEGE_FAIL, ErrorCode.GET_ROLE_PRIVILEGE_FAIL_MSG);
    } finally {
      closeResultSet(sessionDataSetWrapper);
      closeSessionPool(sessionPool);
    }
  }

  private List<DataPrivilegeVO> switchRowInfosToDataPrivileges(
      List<String> rowInfos, SessionPool sessionPool) throws BaseException {
    if (rowInfos == null || rowInfos.size() == 0) {
      return null;
    }
    List<String> paths = new ArrayList<>();
    List<List<String>> privilegesList = new ArrayList<>();
    List<DataPrivilegeVO> dataPrivilegeList = new ArrayList<>();
    for (String rowInfo : rowInfos) {
      String[] split = rowInfo.split("\\s:\\s");
      String[] allPrivileges = split[1].split("\\s");
      String path = split[0];
      if ("root".equals(path)) {
        handleRootPrivileges(allPrivileges, dataPrivilegeList);
        continue;
      }

      List<String> privileges = new ArrayList<>();
      for (String privilege : allPrivileges) {
        if (DATA_PRIVILEGES.contains(privilege)) {
          privileges.add(privilege);
        }
      }
      privilegesList.add(privileges);
      paths.add(path);
    }

    // Map中的String形式为"权限1 权限2 权限3.." List存储相同权限并集下的path路径
    Map<String, List<String>> privilegeOne = new HashMap<>();
    Map<String, List<String>> privilegeTwo = new HashMap<>();
    Map<String, List<String>> privilegeThree = new HashMap<>();
    for (int i = 0; i < paths.size(); i++) {
      String path = paths.get(i);
      String privilegesStr = String.join(" ", privilegesList.get(i));
      // 通过路径获取所属粒度
      int type = findType(sessionPool, path);
      if (type == 1) {
        // 判断相同的权限集合 放入同一list
        if (privilegeOne.containsKey(privilegesStr)) {
          List<String> pathList = privilegeOne.get(privilegesStr);
          pathList.add(path);
          continue;
        }
        ArrayList<String> newPaths = new ArrayList();
        newPaths.add(path);
        privilegeOne.put(privilegesStr, newPaths);
        continue;
      }
      if (type == 2) {
        if (privilegeTwo.containsKey(privilegesStr)) {
          List<String> pathList = privilegeTwo.get(privilegesStr);
          pathList.add(path);
          continue;
        }
        ArrayList<String> newStr = new ArrayList();
        newStr.add(path);
        privilegeTwo.put(privilegesStr, newStr);
        continue;
      }
      if (type == 3) {
        if (privilegeThree.containsKey(privilegesStr)) {
          List<String> pathList = privilegeThree.get(privilegesStr);
          pathList.add(path);
          continue;
        }
        ArrayList<String> newStr = new ArrayList();
        newStr.add(path);
        privilegeThree.put(privilegesStr, newStr);
      }
    }

    Set<String> oneKeys = privilegeOne.keySet();
    Set<String> twoKeys = privilegeTwo.keySet();
    Set<String> threeKeys = privilegeThree.keySet();
    List<String> allGroupPaths = executeQueryOneColumn(sessionPool, "show storage group");
    List<String> allDevicePaths = executeQueryOneColumn(sessionPool, "show devices");
    // 封装成PrivilegeInfo返回 字符串处理
    for (String oneKey : oneKeys) {
      DataPrivilegeVO dataPrivilegeVO = new DataPrivilegeVO();
      List<String> groupPaths = privilegeOne.get(oneKey);
      List<String> privilegesOne = Arrays.asList(oneKey.split(" "));

      // 展示数据
      dataPrivilegeVO.setType(1);
      dataPrivilegeVO.setPrivileges(privilegesOne);
      dataPrivilegeVO.setGroupPaths(groupPaths);
      dataPrivilegeVO.setAllGroupPaths(getGroupsNodeTree(sessionPool));
      dataPrivilegeList.add(dataPrivilegeVO);
    }
    for (String twoKey : twoKeys) {
      List<String> privilegesTwo = Arrays.asList(twoKey.split(" "));
      List<String> pathsTwo = privilegeTwo.get(twoKey);

      Map<String, List<String>> groupPathsToDevicePaths = new HashMap<>();
      for (String path : pathsTwo) {
        String groupPath = getSupPath(path, allGroupPaths);
        if (!groupPathsToDevicePaths.containsKey(groupPath)) {
          groupPathsToDevicePaths.put(groupPath, Stream.of(path).collect(Collectors.toList()));
        } else {
          groupPathsToDevicePaths.get(groupPath).add(path);
        }
      }
      for (Map.Entry<String, List<String>> entry : groupPathsToDevicePaths.entrySet()) {
        DataPrivilegeVO dataPrivilegeVO = new DataPrivilegeVO();
        String groupName = entry.getKey();
        dataPrivilegeVO.setType(2);
        dataPrivilegeVO.setPrivileges(privilegesTwo);
        dataPrivilegeVO.setGroupPaths(Arrays.asList(groupName));
        dataPrivilegeVO.setDevicePaths(entry.getValue());
        dataPrivilegeVO.setAllDevicePaths(getDeviceNodeTree(sessionPool, groupName));
        dataPrivilegeVO.setAllGroupPaths(switchListToNodeList(allGroupPaths));
        dataPrivilegeList.add(dataPrivilegeVO);
      }
    }
    for (String threeKey : threeKeys) {
      List<String> privilegesThree = Arrays.asList(threeKey.split(" "));
      List<String> pathsThree = privilegeThree.get(threeKey);

      Map<String, List<String>> devicePathsTotimeseriesPaths = new HashMap<>();
      for (String path : pathsThree) {
        String devicePath = getSupPath(path, allDevicePaths);
        if (!devicePathsTotimeseriesPaths.containsKey(devicePath)) {
          devicePathsTotimeseriesPaths.put(
              devicePath, Stream.of(path).collect(Collectors.toList()));
        } else {
          devicePathsTotimeseriesPaths.get(devicePath).add(path);
        }
      }
      for (Map.Entry<String, List<String>> entry : devicePathsTotimeseriesPaths.entrySet()) {
        DataPrivilegeVO dataPrivilegeVO = new DataPrivilegeVO();
        dataPrivilegeVO.setType(3);
        dataPrivilegeVO.setPrivileges(privilegesThree);
        String device = entry.getKey();
        String group = getSupPath(device, allGroupPaths);
        dataPrivilegeVO.setGroupPaths(Arrays.asList(group));
        dataPrivilegeVO.setDevicePaths(Arrays.asList(device));
        dataPrivilegeVO.setTimeseriesPaths(entry.getValue());
        dataPrivilegeVO.setAllGroupPaths(switchListToNodeList(allGroupPaths));
        String sql = "show devices " + group;
        List<String> devicePathsOfGroup = executeQueryOneColumn(sessionPool, sql);
        dataPrivilegeVO.setAllDevicePaths(switchListToNodeList(devicePathsOfGroup));
        sql = "show timeseries " + device;
        dataPrivilegeVO.setAllTimeseriesPaths(executeQueryOneColumn(sessionPool, sql));
        dataPrivilegeList.add(dataPrivilegeVO);
      }
    }
    return dataPrivilegeList;
  }

  private String getSupPath(String path, List<String> allSupPath) throws BaseException {
    for (String supPath : allSupPath) {
      if (path.contains(supPath) && StringUtils.removeStart(path, supPath).startsWith(".")) {
        return supPath;
      }
    }
    throw new BaseException("-1", "代码逻辑错误");
  }

  private List<NodeTreeVO> switchListToNodeList(List<String> list) {
    List<NodeTreeVO> nodeList = new ArrayList<>();
    for (String s : list) {
      nodeList.add(new NodeTreeVO(s));
    }
    return nodeList;
  }

  private void handleRootPrivileges(
      String[] allPrivileges, List<DataPrivilegeVO> dataPrivilegeList) {
    Set<String> rootPrivilegesSet = new HashSet<>();
    for (String privilege : allPrivileges) {
      if (DATA_PRIVILEGES.contains(privilege)) {
        rootPrivilegesSet.add(privilege);
      }
    }
    if (rootPrivilegesSet.size() > 0) {
      DataPrivilegeVO dataPrivilegeVO = new DataPrivilegeVO();
      dataPrivilegeVO.setPrivileges(new ArrayList<String>(rootPrivilegesSet));
      dataPrivilegeVO.setType(0);
      dataPrivilegeList.add(dataPrivilegeVO);
    }
  }

  @Override
  public void deleteTimeseries(Connection connection, String timeseriesName) throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    try {
      sessionPool.deleteTimeseries(timeseriesName);
    } catch (IoTDBConnectionException e) {
      throw new BaseException(ErrorCode.DELETE_TS_FAIL, ErrorCode.DELETE_TS_FAIL_MSG);
    } catch (StatementExecutionException e) {
      if (e.getStatusCode() == 602) {
        throw new BaseException(
            ErrorCode.NO_PRI_DELETE_TIMESERIES, ErrorCode.NO_PRI_DELETE_TIMESERIES_MSG);
      }
      throw new BaseException(ErrorCode.DELETE_TS_FAIL, ErrorCode.DELETE_TS_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public List<Integer> getDevicesCount(Connection connection, List<String> groupNames)
      throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    List<Integer> devicesCount = new ArrayList<>();
    try {
      for (String groupName : groupNames) {
        String sql = "count devices " + groupName;
        String value = executeQueryOneValue(sessionPool, sql);
        if (value == null) {
          devicesCount.add(0);
          continue;
        }
        Integer count = Integer.valueOf(value);
        devicesCount.add(count);
      }
      return devicesCount;
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public void saveGroupTtl(Connection connection, String groupName, long l) throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    String sql = "set ttl to " + groupName + " " + l;
    try {
      sessionPool.executeNonQueryStatement(sql);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      if (e.getStatusCode() == 602) {
        throw new BaseException(ErrorCode.NO_PRI_SET_TTL, ErrorCode.NO_PRI_SET_TTL_MSG);
      }
      throw new BaseException(ErrorCode.SET_TTL_FAIL, ErrorCode.SET_TTL_FAIL_MSG);
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.SET_TTL_FAIL, ErrorCode.SET_TTL_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public void cancelGroupTtl(Connection connection, String groupName) throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    String sql = "unset ttl to " + groupName;
    try {
      sessionPool.executeNonQueryStatement(sql);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public List<Integer> getTimeseriesCount(Connection connection, List<String> deviceNames)
      throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    List<Integer> lines = new ArrayList<>();
    try {
      for (String deviceName : deviceNames) {
        String sql = "count timeseries " + deviceName;
        String value = executeQueryOneValue(sessionPool, sql);
        if (value == null) {
          lines.add(0);
          continue;
        }
        Integer count = Integer.valueOf(value);
        lines.add(count);
      }
      return lines;
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public List<String> deleteTimeseriesByDevice(Connection connection, String deviceName)
      throws BaseException {
    SessionPool sessionPool = null;
    try {
      sessionPool = getSessionPool(connection);
      List<String> timeseriesList = getTimeseries(connection, deviceName);
      for (String timeseries : timeseriesList) {
        String sql = "delete timeseries " + timeseries;
        sessionPool.executeNonQueryStatement(sql);
      }
      return timeseriesList;
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      if (e.getStatusCode() == 602) {
        throw new BaseException(
            ErrorCode.NO_PRI_DELETE_TIMESERIES, ErrorCode.NO_PRI_DELETE_TIMESERIES_MSG);
      }
      throw new BaseException(ErrorCode.DELETE_TS_FAIL, ErrorCode.DELETE_TS_FAIL_MSG);
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.DELETE_TS_FAIL, ErrorCode.DELETE_TS_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public void createDeviceWithMeasurements(Connection connection, DeviceInfoDTO deviceInfoDTO)
      throws BaseException {
    SessionPool sessionPool = null;
    try {
      List<String> typesStr = new ArrayList<>();
      List<String> encodingsStr = new ArrayList<>();
      List<String> measurements = new ArrayList<>();
      List<String> compressionStr = new ArrayList<>();
      for (DeviceDTO deviceDTO : deviceInfoDTO.getDeviceDTOList()) {
        typesStr.add(deviceDTO.getDataType());
        encodingsStr.add(deviceDTO.getEncoding());
        measurements.add(deviceDTO.getTimeseries());
        compressionStr.add(deviceDTO.getCompression());
      }
      List<TSDataType> types = handleTypeStr(typesStr);
      List<TSEncoding> encodings = handleEncodingStr(encodingsStr);
      List<CompressionType> compressionTypes = handleCompressionStr(compressionStr);
      sessionPool = getSessionPool(connection);
      sessionPool.createMultiTimeseries(
          measurements, types, encodings, compressionTypes, null, null, null, null);
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.INSERT_DEV_FAIL, ErrorCode.INSERT_DEV_FAIL_MSG);
    } catch (StatementExecutionException e) {
      if (e.getMessage().contains("No permissions")) {
        throw new BaseException(
            ErrorCode.NO_PRI_CREATE_TIMESERIES, ErrorCode.NO_PRI_CREATE_TIMESERIES_MSG);
      }
      if (!e.getMessage().contains("PathAlreadyExistException")) {
        logger.error(e.getMessage());
        throw new BaseException(ErrorCode.INSERT_DEV_FAIL, ErrorCode.INSERT_DEV_FAIL_MSG);
      }
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public void upsertMeasurementAlias(Connection connection, List<DeviceDTO> deviceDTOList)
      throws BaseException {
    SessionPool sessionPool = null;
    try {
      sessionPool = getSessionPool(connection);
      for (DeviceDTO deviceDTO : deviceDTOList) {
        String alias = deviceDTO.getAlias();
        if (alias == null || "null".equals(alias) || StringUtils.isBlank(alias)) {
          continue;
        } else {
          sessionPool.executeNonQueryStatement(
              "alter timeseries " + deviceDTO.getTimeseries() + " upsert alias=" + alias);
        }
      }
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.UPSERT_ALIAS_FAIL, ErrorCode.UPSERT_ALIAS_FAIL_MSG);
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public void upsertMeasurementTags(Connection connection, List<DeviceDTO> deviceDTOList)
      throws BaseException {
    SessionPool sessionPool = null;
    try {
      sessionPool = getSessionPool(connection);
      for (DeviceDTO deviceDTO : deviceDTOList) {
        String tags =
            executeQueryOneLine(
                sessionPool, "show timeseries " + deviceDTO.getTimeseries(), "tags");
        if (!"null".equals(tags)) {
          String patternStr = "\"([^\"]+)\":";
          Pattern pattern = Pattern.compile(patternStr);
          Matcher matcher = pattern.matcher(tags);
          List<String> oldTags = new ArrayList<>();
          while (matcher.find()) {
            oldTags.add(matcher.group(1));
          }
          sessionPool.executeNonQueryStatement(
              "alter timeseries "
                  + deviceDTO.getTimeseries()
                  + " drop "
                  + String.join(",", oldTags));
        }
        List<List<String>> newTags = deviceDTO.getTags();
        if (newTags != null) {
          for (List<String> newTag : newTags) {
            if (newTag.size() != 2) {
              throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
            }
            sessionPool.executeNonQueryStatement(
                "alter timeseries "
                    + deviceDTO.getTimeseries()
                    + " add tags "
                    + newTag.get(0)
                    + "="
                    + newTag.get(1));
          }
        }
      }
    } catch (BaseException | StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.UPSERT_TAGS_FAIL, ErrorCode.UPSERT_TAGS_FAIL_MSG);
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public void upsertMeasurementAttributes(Connection connection, List<DeviceDTO> deviceDTOList)
      throws BaseException {
    SessionPool sessionPool = null;
    try {
      sessionPool = getSessionPool(connection);
      for (DeviceDTO deviceDTO : deviceDTOList) {
        String attributes =
            executeQueryOneLine(
                sessionPool, "show timeseries " + deviceDTO.getTimeseries(), "attributes");
        if (!"null".equals(attributes)) {
          String patternStr = "\"([^\"]+)\":";
          Pattern pattern = Pattern.compile(patternStr);
          Matcher matcher = pattern.matcher(attributes);
          List<String> oldAttributes = new ArrayList<>();
          while (matcher.find()) {
            oldAttributes.add(matcher.group(1));
          }
          sessionPool.executeNonQueryStatement(
              "alter timeseries "
                  + deviceDTO.getTimeseries()
                  + " drop "
                  + String.join(",", oldAttributes));
        }
        List<List<String>> newAttributes = deviceDTO.getAttributes();
        if (newAttributes != null) {
          for (List<String> newAttribute : newAttributes) {
            if (newAttribute.size() != 2) {
              throw new BaseException(ErrorCode.WRONG_DB_PARAM, ErrorCode.WRONG_DB_PARAM_MSG);
            }
            sessionPool.executeNonQueryStatement(
                "alter timeseries "
                    + deviceDTO.getTimeseries()
                    + " add attributes "
                    + newAttribute.get(0)
                    + "="
                    + newAttribute.get(1));
          }
        }
      }
    } catch (BaseException | StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(
          ErrorCode.UPSERT_ATTRIBUTES_FAIL, ErrorCode.UPSERT_ATTRIBUTES_FAIL_MSG);
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public Integer getOneDataCount(Connection connection, String deviceName, String measurementName)
      throws BaseException {
    SessionPool sessionPool = null;
    try {
      sessionPool = getSessionPool(connection);
      String sql = "select count(*) from " + deviceName;
      String countStr = executeQueryOneLine(sessionPool, sql, "count(" + measurementName + ")");
      return Integer.parseInt(countStr);
    } catch (BaseException e) {
      throw new BaseException(
          ErrorCode.GET_MEASUREMENT_DATA_COUNT_FAIL, ErrorCode.GET_MEASUREMENT_DATA_COUNT_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public String getLastMeasurementValue(Connection connection, String timeseries)
      throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    int index = timeseries.lastIndexOf(".");
    String sql =
        "select last_value("
            + timeseries.substring(index + 1)
            + ") from "
            + timeseries.substring(0, index);
    String value;
    try {
      value = executeQueryOneValue(sessionPool, sql);
      return value;
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public String getGroupTTL(Connection connection, String groupName) throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    try {
      String sql = "show ttl on " + groupName;
      String queryField = "ttl";
      String ttl = executeQueryOneLine(sessionPool, sql, queryField);
      return ttl;
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public List<String> getDevices(Connection connection, String groupName) throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    try {
      String sql = "show devices " + groupName;
      List<String> devicesName = executeQueryOneColumn(sessionPool, sql);
      return devicesName;
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public List<NodeTreeVO> getDeviceNodeTree(Connection connection, String groupName)
      throws BaseException {
    SessionPool sessionPool = null;
    try {
      sessionPool = getSessionPool(connection);
      return getDeviceNodeTree(sessionPool, groupName);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  private List<NodeTreeVO> getDeviceNodeTree(SessionPool sessionPool, String groupName)
      throws BaseException {
    Set<String> firstLevelNodes = getChildrenNode(groupName, "devices", sessionPool);
    if (firstLevelNodes == null || firstLevelNodes.size() == 0) {
      return null;
    }
    List<NodeTreeVO> groupNodeVOList = new ArrayList<>();
    for (String firstLevelNodeName : firstLevelNodes) {
      NodeTreeVO firstLevelNode = new NodeTreeVO(firstLevelNodeName);
      groupNodeVOList.add(firstLevelNode);
      assembleNodeTree(firstLevelNode, firstLevelNodeName, "devices", sessionPool);
    }
    return groupNodeVOList;
  }

  @Override
  public NodeTreeVO getDeviceList(Connection connection, String groupName) throws BaseException {
    SessionPool sessionPool = null;
    try {
      sessionPool = getSessionPool(connection);
      String sql = "show devices " + groupName;
      List<String> devices = executeQueryOneColumn(sessionPool, sql);
      String ancestryName = null;
      if (devices.size() == 0) {
        return null;
      } else if (groupName.equals(devices.get(0))) {
        ancestryName = groupName;
      }
      NodeTreeVO ancestry = new NodeTreeVO(ancestryName);
      assembleDeviceList(ancestry, groupName, sessionPool);
      return ancestry;
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  private void assembleDeviceList(NodeTreeVO node, String deviceName, SessionPool sessionPool)
      throws BaseException {
    List<String> descendants = findDescendants(deviceName, sessionPool);
    if (descendants.size() == 0) {
      return;
    }
    List<String> children = findChildren(descendants);
    for (String child : children) {
      NodeTreeVO childNode = new NodeTreeVO(child);
      node.initChildren().add(childNode);
      assembleDeviceList(childNode, child, sessionPool);
    }
  }

  private List<String> findChildren(List<String> descendants) {
    List<String> children = new ArrayList<>();
    for (int i = 0; i < descendants.size(); i++) {
      int tag = 0;
      for (int j = 0; j < descendants.size(); j++) {
        if (!descendants.get(i).equals(descendants.get(j))
            && descendants.get(i).contains(descendants.get(j))
            && StringUtils.removeStart(descendants.get(i), descendants.get(j)).startsWith(".")) {
          tag++;
        }
      }
      if (tag == 0) {
        children.add(descendants.get(i));
      }
    }
    return children;
  }

  private List<String> findDescendants(String deviceName, SessionPool sessionPool)
      throws BaseException {
    List<String> descendants = executeQueryOneColumn(sessionPool, "show devices " + deviceName);
    if (descendants.size() != 0 && deviceName.equals(descendants.get(0))) {
      descendants.remove(0);
    }
    return descendants;
  }

  @Override
  public Boolean deviceExist(Connection connection, String groupName, String deviceName)
      throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    try {
      String sql = "show devices " + groupName;
      List<String> devices = executeQueryOneColumn(sessionPool, sql);
      Boolean isExist = false;
      for (String device : devices) {
        if (deviceName.equals(device)) {
          isExist = true;
          break;
        }
      }
      return isExist;
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public List<String> getTimeseries(Connection connection, String deviceName) throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    String sql = "show timeseries " + deviceName;
    SqlResultVO sqlResultVO = executeQuery(sessionPool, sql, true);
    List<String> metaDataList = sqlResultVO.getMetaDataList();
    int index = -1;
    if (metaDataList != null) {
      for (int i = 0; i < metaDataList.size(); i++) {
        if ("timeseries".equalsIgnoreCase(metaDataList.get(i))) {
          index = i;
          break;
        }
      }
    }
    if (index == -1) {
      throw new BaseException(ErrorCode.SQL_EP, ErrorCode.SQL_EP_MSG);
    }
    List<List<String>> valueList = sqlResultVO.getValueList();
    List<String> timeseries = new ArrayList<>();
    for (List<String> list : valueList) {
      String measurementName = list.get(0);
      if (StringUtils.removeStart(measurementName, deviceName + ".").contains(".")) {
        continue;
      }
      timeseries.add(list.get(index));
    }
    return timeseries;
  }

  @Override
  public DataVO getDataByDevice(
      Connection connection,
      String deviceName,
      Integer pageSize,
      Integer pageNum,
      DataQueryDTO dataQueryDTO)
      throws BaseException {
    SessionPool sessionPool = null;
    List<String> measurementList = dataQueryDTO.getMeasurementList();
    List<String> newMeasurementList = new ArrayList<>();
    for (String measurement : measurementList) {
      newMeasurementList.add(StringUtils.removeStart(measurement, deviceName + "."));
    }

    String basicSql = "select " + String.join(",", newMeasurementList) + " from " + deviceName;
    String whereClause = getWhereClause(dataQueryDTO);
    String limitClause = " limit " + pageSize + " offset " + (pageNum - 1) * pageSize;
    String sql = basicSql + whereClause + limitClause;
    try {
      sessionPool = getSessionPool(connection);
      DataVO dataVO = getDataBySql(sql, sessionPool);
      Integer totalLine = getDataLineBySql(basicSql + whereClause, deviceName, sessionPool);
      dataVO.setTotalCount(totalLine);
      int totalPage = (totalLine + pageSize - 1) / pageSize;
      dataVO.setTotalPage(totalPage);
      return dataVO;
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  private String getWhereClause(DataQueryDTO dataQueryDTO) {
    Long startTime = null;
    if (dataQueryDTO.getStartTime() != null) {
      startTime = dataQueryDTO.getStartTime().getTime();
    }
    Long endTime = null;
    if (dataQueryDTO.getEndTime() != null) {
      endTime = dataQueryDTO.getEndTime().getTime();
    }

    String whereClause = "";
    if (startTime != null && endTime != null) {
      whereClause = " where time >= " + startTime + " and time <= " + endTime;
    } else if (startTime == null && endTime != null) {
      whereClause = " where time <= " + endTime;
    } else if (startTime != null && endTime == null) {
      whereClause = " where time >= " + startTime;
    }
    return whereClause;
  }

  private DataVO getDataBySql(String sql, SessionPool sessionPool) throws BaseException {
    SessionDataSetWrapper sessionDataSetWrapper = null;
    DataVO dataVO = new DataVO();
    List<List<String>> valueList = new ArrayList<>();
    try {
      sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
      List<String> columnTypes = sessionDataSetWrapper.getColumnTypes();
      dataVO.setTypeList(columnTypes);
      List<String> columnNames = sessionDataSetWrapper.getColumnNames();
      dataVO.setMetaDataList(columnNames);
      if ("Time".equals(columnNames.get(0))) {
        while (sessionDataSetWrapper.hasNext()) {
          List<String> lineValueList = new ArrayList<>();
          RowRecord rowRecord = sessionDataSetWrapper.next();
          long timestamp = rowRecord.getTimestamp();
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
          Date date = new Date(timestamp);
          String timeStr = simpleDateFormat.format(date);
          lineValueList.add(timeStr);
          for (org.apache.iotdb.tsfile.read.common.Field field : rowRecord.getFields()) {
            lineValueList.add(field.toString());
          }
          valueList.add(lineValueList);
        }
      } else {
        while (sessionDataSetWrapper.hasNext()) {
          List<String> lineValueList = new ArrayList<>();
          RowRecord rowRecord = sessionDataSetWrapper.next();
          for (org.apache.iotdb.tsfile.read.common.Field field : rowRecord.getFields()) {
            lineValueList.add(field.toString());
          }
          valueList.add(lineValueList);
        }
      }
      dataVO.setValueList(valueList);
      return dataVO;
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      if (e.getStatusCode() == 602) {
        throw new BaseException(
            ErrorCode.NO_PRI_TIMESERIES_DATA, ErrorCode.NO_PRI_TIMESERIES_DATA_MSG);
      } else {
        throw new BaseException(ErrorCode.GET_DATA_FAIL, ErrorCode.GET_DATA_FAIL_MSG);
      }
    } finally {
      closeResultSet(sessionDataSetWrapper);
    }
  }

  private Integer getDataLineBySql(String sql, String deviceName, SessionPool sessionPool)
      throws BaseException {
    SessionDataSetWrapper sessionDataSetWrapper = null;
    try {
      int lineCount = 0;
      sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
      while (sessionDataSetWrapper.hasNext()) {
        lineCount++;
      }
      return lineCount;
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      if (e.getStatusCode() == 602) {
        throw new BaseException(
            ErrorCode.NO_PRI_TIMESERIES_DATA, ErrorCode.NO_PRI_TIMESERIES_DATA_MSG);
      } else {
        throw new BaseException(ErrorCode.GET_DATA_FAIL, ErrorCode.GET_DATA_FAIL_MSG);
      }
    } finally {
      closeResultSet(sessionDataSetWrapper);
    }
  }

  @Override
  public void updateDataByDevice(
      Connection connection, String deviceName, DataUpdateDTO dataUpdateDTO) throws BaseException {
    SessionPool sessionPool = null;
    long timestamp = dataUpdateDTO.getTimestamp().getTime();
    List<String> measurementList = dataUpdateDTO.getMeasurementList();
    List<String> valueList = dataUpdateDTO.getValueList();
    List<String> newMeasurementList = new ArrayList<>();
    for (String measurement : measurementList) {
      newMeasurementList.add(StringUtils.removeStart(measurement, deviceName + "."));
    }
    try {
      sessionPool = getSessionPool(connection);
      sessionPool.insertRecord(deviceName, timestamp, newMeasurementList, valueList);
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.UPDATE_DATA_FAIL, ErrorCode.UPDATE_DATA_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public void deleteDataByDevice(
      Connection connection, String deviceName, DataDeleteDTO dataDeleteDTO) throws BaseException {
    SessionPool sessionPool = null;
    List<Date> timestampList = dataDeleteDTO.getTimestampList();
    List<String> timestampStrList = new ArrayList<>();
    for (Date date : timestampList) {
      timestampStrList.add(Long.toString(date.getTime()));
    }
    List<String> measurementList = dataDeleteDTO.getMeasurementList();
    try {
      sessionPool = getSessionPool(connection);
      for (String measurement : measurementList) {
        for (String timestamp : timestampStrList) {
          String sql = "delete from " + measurement + " where time=" + timestamp;
          sessionPool.executeNonQueryStatement(sql);
        }
      }
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.DELETE_DATA_FAIL, ErrorCode.DELETE_DATA_FAIL_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public void randomImport(
      Connection connection, String deviceName, RandomImportDTO randomImportDTO)
      throws BaseException {
    SessionPool sessionPool = null;
    int totalLine = randomImportDTO.getTotalLine();
    int stepSize = randomImportDTO.getStepSize();
    Long startTime = randomImportDTO.getStartTime().getTime();

    List<Long> times = new ArrayList<>();
    List<List<String>> measurementsList = new ArrayList<>();
    List<List<Object>> valuesList = new ArrayList<>();
    List<List<TSDataType>> typesList = new ArrayList<>();
    try {
      sessionPool = getSessionPool(connection);
      SessionDataSetWrapper sessionDataSetWrapper =
          sessionPool.executeQueryStatement("show timeseries " + deviceName);
      List<String> columnNames = sessionDataSetWrapper.getColumnNames();
      sessionDataSetWrapper.getColumnTypes();
      int timeseriesIndex = -1;
      int dataTypeIndex = -1;
      if (columnNames != null) {
        for (int i = 0; i < columnNames.size(); i++) {
          if ("timeseries".equalsIgnoreCase(columnNames.get(i))) {
            timeseriesIndex = i;
          }
          if ("dataType".equalsIgnoreCase(columnNames.get(i))) {
            dataTypeIndex = i;
          }
        }
      }
      if (timeseriesIndex == -1 || dataTypeIndex == -1) {
        logger.error(ErrorCode.RANDOM_IMPORT_DATA_FAIL_MSG);
        throw new BaseException(
            ErrorCode.RANDOM_IMPORT_DATA_FAIL, ErrorCode.RANDOM_IMPORT_DATA_FAIL_MSG);
      }

      List<String> measurements = new ArrayList<>();
      List<String> typesStr = new ArrayList<>();
      while (sessionDataSetWrapper.hasNext()) {
        RowRecord next = sessionDataSetWrapper.next();
        String timeseries = next.getFields().get(timeseriesIndex).toString();
        timeseries = StringUtils.removeStart(timeseries, deviceName + ".");
        if (timeseries.contains(".")) {
          continue;
        }
        measurements.add(timeseries);
        String dataType = next.getFields().get(dataTypeIndex).toString();
        typesStr.add(dataType);
      }
      if (measurements.size() == 0) {
        logger.error(ErrorCode.NO_MEASUREMENT_MSG);
        throw new BaseException(ErrorCode.NO_MEASUREMENT, ErrorCode.NO_MEASUREMENT_MSG);
      }

      List<TSDataType> types = handleTypeStr(typesStr);
      for (int i = 0; i < totalLine; i++) {
        typesList.add(types);
        measurementsList.add(measurements);

        List<Object> values = createRandomData(typesStr);
        valuesList.add(values);

        times.add(stepSize * i + startTime);
      }

      sessionPool.insertOneDeviceRecords(
          deviceName, times, measurementsList, typesList, valuesList, false);

    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.NO_MEASUREMENT, ErrorCode.NO_MEASUREMENT_MSG);
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  private List<Object> createRandomData(List<String> types) throws BaseException {
    Random random = new Random();
    List<Object> values = new ArrayList<>();
    for (String type : types) {
      switch (type) {
        case "BOOLEAN":
          values.add(random.nextBoolean());
          break;
        case "INT32":
          values.add(random.nextInt());
          break;
        case "INT64":
          values.add(random.nextLong());
          break;
        case "FLOAT":
          values.add(random.nextFloat());
          break;
        case "DOUBLE":
          values.add(random.nextDouble());
          break;
        case "TEXT":
          values.add(RandomStringUtils.randomAlphabetic(5));
          break;
        default:
          throw new BaseException(ErrorCode.DB_DATATYPE_WRONG, ErrorCode.DB_DATATYPE_WRONG_MSG);
      }
    }
    return values;
  }

  @Override
  public String getSqlForExport(String deviceName, DataQueryDTO dataQueryDTO) throws BaseException {
    List<String> measurementList = dataQueryDTO.getMeasurementList();
    List<String> newMeasurementList = new ArrayList<>();
    for (String measurement : measurementList) {
      newMeasurementList.add(StringUtils.removeStart(measurement, deviceName + "."));
    }
    String whereClause = getWhereClause(dataQueryDTO);
    String sql =
        "select " + String.join(",", newMeasurementList) + " from " + deviceName + whereClause;
    return sql;
  }

  @Override
  public void upsertDataPrivileges(
      Connection connection, String userOrRole, String name, PrivilegeInfoDTO privilegeInfoDTO)
      throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    // 授权
    List<String> privileges = privilegeInfoDTO.getPrivileges();
    if (notNullAndNotZero(privileges)) {
      grantOrRevoke("grant", userOrRole, privileges, name, privilegeInfoDTO, sessionPool);
    }
    // 取消授权
    List<String> cancelPrivileges = privilegeInfoDTO.getCancelPrivileges();
    if (notNullAndNotZero(cancelPrivileges)) {
      grantOrRevoke("revoke", userOrRole, cancelPrivileges, name, privilegeInfoDTO, sessionPool);
    }
    cancelPathPrivileges(name, userOrRole, privilegeInfoDTO, sessionPool);
    sessionPool.close();
  }

  private void cancelPathPrivileges(
      String name, String userOrRole, PrivilegeInfoDTO privilegeInfoDTO, SessionPool sessionPool) {
    Integer type = privilegeInfoDTO.getType();
    List<String> delDevicePaths = privilegeInfoDTO.getDelDevicePaths();
    List<String> delGroupPaths = privilegeInfoDTO.getDelGroupPaths();
    List<String> delTimeseriesPaths = privilegeInfoDTO.getDelTimeseriesPaths();
    switch (type) {
      case 1:
        if (notNullAndNotZero(delGroupPaths)) {
          for (String privilege : DATA_PRIVILEGES) {
            grantOrRevokePaths("revoke", userOrRole, name, privilege, delGroupPaths, sessionPool);
          }
        }
        break;
      case 2:
        if (notNullAndNotZero(delDevicePaths)) {
          for (String privilege : DATA_PRIVILEGES) {
            grantOrRevokePaths("revoke", userOrRole, name, privilege, delDevicePaths, sessionPool);
          }
        }
        break;
      case 3:
        if (notNullAndNotZero(delTimeseriesPaths)) {
          for (String privilege : DATA_PRIVILEGES) {
            grantOrRevokePaths(
                "revoke", userOrRole, name, privilege, delTimeseriesPaths, sessionPool);
          }
        }
        break;
    }
  }

  @Override
  public RecordVO getRecords(
      Connection connection, String deviceName, String timeseriesName, String dataType)
      throws BaseException {
    SessionPool sessionPool = null;
    SessionDataSetWrapper sessionDataSetWrapper = null;
    RecordVO recordVO = new RecordVO();
    List<Date> timeList = new ArrayList<>();
    List<String> valueList = new ArrayList<>();
    Map<String, Integer> textCount = new HashMap<>();
    String sql =
        "select "
            + StringUtils.removeStart(timeseriesName, deviceName + ".")
            + " from "
            + deviceName
            + " order by time desc limit 200 offset 0";
    try {
      sessionPool = getSessionPool(connection);
      sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
      while (sessionDataSetWrapper.hasNext()) {
        if ("TEXT".equals(dataType)) {
          RowRecord next = sessionDataSetWrapper.next();
          String text = next.getFields().get(0).toString();
          if (textCount.containsKey(text)) {
            textCount.put(text, textCount.get(text) + 1);
          } else {
            textCount.put(text, 1);
          }
        } else if (StringUtils.equalsAny(
            dataType, "INT32", "INT64", "BOOLEAN", "FLOAT", "DOUBLE")) {
          RowRecord next = sessionDataSetWrapper.next();
          Date date = new Date(next.getTimestamp());
          timeList.add(date);
          List<org.apache.iotdb.tsfile.read.common.Field> fields = next.getFields();
          valueList.add(fields.get(0).toString());
        } else {
          throw new BaseException(ErrorCode.DB_DATATYPE_WRONG, ErrorCode.DB_DATATYPE_WRONG_MSG);
        }
      }
      recordVO.setTimeList(timeList);
      recordVO.setValueList(valueList);
      recordVO.setTextCount(textCount);
      return recordVO;
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_RECORD_FAIL, ErrorCode.GET_RECORD_FAIL_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_RECORD_FAIL, ErrorCode.GET_RECORD_FAIL_MSG);
    } finally {
      closeResultSet(sessionDataSetWrapper);
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public List<SqlResultVO> queryAll(Connection connection, List<String> sqls, Long timestamp)
      throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    List<SqlResultVO> results;
    String id_plus_timestamp;
    try {
      results = new ArrayList<>();
      Integer id = connection.getId();
      id_plus_timestamp = id + ":" + timestamp;
      QUERY_STOP.put(id_plus_timestamp, true);
      for (String sql : sqls) {
        int firstSpaceIndex = sql.indexOf(" ");
        String judge = sql.substring(0, firstSpaceIndex);
        if ("show".equalsIgnoreCase(judge)
            || "count".equalsIgnoreCase(judge)
            || "list".equalsIgnoreCase(judge)
            || "select".equalsIgnoreCase(judge)) {
          SqlResultVO sqlResultVO = executeQuery(sessionPool, sql, false, id_plus_timestamp);
          results.add(sqlResultVO);
          continue;
        }
        try {
          if (QUERY_STOP.get(id_plus_timestamp)) {
            long start = System.currentTimeMillis();
            sessionPool.executeNonQueryStatement(sql);
            long end = System.currentTimeMillis();
            double time = (end - start + 0.0d) / 1000;
            String queryTime = time + "s";
            SqlResultVO sqlResultVO = new SqlResultVO();
            sqlResultVO.setQueryTime(queryTime);
            sqlResultVO.setLine(0L);
            results.add(sqlResultVO);
          }
        } catch (StatementExecutionException e) {
          logger.error(e.getMessage());
          throw new BaseException(
              ErrorCode.SQL_EP,
              ErrorCode.SQL_EP_MSG + ":" + sql + "执行出错,错误信息[" + e.getMessage() + "]");
        } catch (IoTDBConnectionException e) {
          logger.error(e.getMessage());
          throw new BaseException(
              ErrorCode.SQL_EP,
              ErrorCode.SQL_EP_MSG + ":" + sql + "执行出错,错误信息[" + e.getMessage() + "]");
        }
      }
    } finally {
      closeSessionPool(sessionPool);
    }
    QUERY_STOP.remove(id_plus_timestamp);
    return results;
  }

  @Override
  public void updatePwd(Connection connection, IotDBUser iotDBUser) throws BaseException {
    SessionPool sessionPool = getSessionPool(connection);
    String userName = iotDBUser.getUserName();
    String newPWD = iotDBUser.getPassword();
    String sql = "alter user " + userName + " set password '" + newPWD + "'";
    try {
      sessionPool.executeNonQueryStatement(sql);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.UPDATE_PWD_FAIL, ErrorCode.UPDATE_PWD_FAIL_MSG);
    } catch (IoTDBConnectionException e) {
      e.printStackTrace();
    } finally {
      closeSessionPool(sessionPool);
    }
  }

  @Override
  public void stopQuery(Integer serverId, Long timestamp) throws BaseException {
    String notStopKey = serverId + ":" + timestamp;
    if (QUERY_STOP.containsKey(notStopKey)) {
      QUERY_STOP.put(notStopKey, false);
      return;
    }
    throw new BaseException(ErrorCode.NO_QUERY, ErrorCode.NO_QUERY_MSG);
  }

  private void grantOrRevoke(
      String grantOrRevoke,
      String userOrRole,
      List<String> privileges,
      String name,
      PrivilegeInfoDTO privilegesInfo,
      SessionPool sessionPool)
      throws BaseException {
    Integer type = privilegesInfo.getType();
    for (String privilege : privileges) {
      if (type == 0) {
        grantOrRevokePaths(
            grantOrRevoke, userOrRole, name, privilege, Arrays.asList("root"), sessionPool);
      } else if (type == 1) {
        List<String> groupPaths = privilegesInfo.getGroupPaths();
        grantOrRevokePaths(grantOrRevoke, userOrRole, name, privilege, groupPaths, sessionPool);
      } else if (type == 2) {
        List<String> devicePaths = privilegesInfo.getDevicePaths();
        grantOrRevokePaths(grantOrRevoke, userOrRole, name, privilege, devicePaths, sessionPool);
      } else if (type == 3) {
        List<String> timeseriesPaths = privilegesInfo.getTimeseriesPaths();
        grantOrRevokePaths(
            grantOrRevoke, userOrRole, name, privilege, timeseriesPaths, sessionPool);
      } else {
        throw new BaseException(ErrorCode.NO_TYPE, ErrorCode.NO_TYPE_MSG);
      }
    }
  }

  private void grantOrRevokePaths(
      String grantOrRevoke,
      String userOrRole,
      String name,
      String privilege,
      List<String> paths,
      SessionPool sessionPool) {
    if (notNullAndNotZero(paths)) {
      for (String groupPath : paths) {
        String sql =
            grantOrRevoke
                + " "
                + userOrRole
                + " "
                + name
                + " privileges '"
                + privilege
                + "' on "
                + groupPath;
        try {
          sessionPool.executeNonQueryStatement(sql);
        } catch (StatementExecutionException e) {
          logger.error(e.getMessage());
        } catch (IoTDBConnectionException e) {
          logger.error(e.getMessage());
        }
      }
    }
  }

  /** 判断集合不为空且长度大于0 */
  private boolean notNullAndNotZero(List list) {
    if (list != null && list.size() > 0) {
      return true;
    }
    return false;
  }

  private List<String> executeQueryOneLine(SessionPool sessionPool, String sql)
      throws BaseException {
    SessionDataSetWrapper sessionDataSetWrapper = null;
    try {
      List<String> valueList = new ArrayList<>();
      sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
      if (sessionDataSetWrapper.hasNext()) {
        RowRecord rowRecord = sessionDataSetWrapper.next();
        List<org.apache.iotdb.tsfile.read.common.Field> fields = rowRecord.getFields();
        for (org.apache.iotdb.tsfile.read.common.Field field : fields) {
          valueList.add(field.toString());
        }
      }
      return valueList;
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.SQL_EP, ErrorCode.SQL_EP_MSG);
    } finally {
      closeResultSet(sessionDataSetWrapper);
    }
  }

  private String executeQueryOneLine(SessionPool sessionPool, String sql, String queryField)
      throws BaseException {
    SessionDataSetWrapper sessionDataSetWrapper = null;
    try {
      sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
      List<String> columnNames = sessionDataSetWrapper.getColumnNames();
      int index = -1;
      for (int i = 0; i < columnNames.size(); i++) {
        if (queryField.equals(columnNames.get(i))) {
          index = i;
        }
      }
      if (index == -1) {
        throw new BaseException(ErrorCode.SQL_EP, ErrorCode.SQL_EP_MSG);
      }
      int batchSize = sessionDataSetWrapper.getBatchSize();
      if (batchSize > 0) {
        if (sessionDataSetWrapper.hasNext()) {
          RowRecord rowRecord = sessionDataSetWrapper.next();
          return rowRecord.getFields().get(index).toString();
        }
      }
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.SQL_EP, ErrorCode.SQL_EP_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.SQL_EP, ErrorCode.SQL_EP_MSG);
    } finally {
      closeResultSet(sessionDataSetWrapper);
    }
    throw new BaseException(ErrorCode.SQL_EP, ErrorCode.SQL_EP_MSG);
  }

  private SqlResultVO executeQuery(SessionPool sessionPool, String sql, Boolean closePool)
      throws BaseException {
    SqlResultVO sqlResultVO = new SqlResultVO();
    List<List<String>> valuelist = new ArrayList<>();
    SessionDataSetWrapper sessionDataSetWrapper = null;
    try {
      sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
      long start = System.currentTimeMillis();
      List<String> columnNames = sessionDataSetWrapper.getColumnNames();
      sqlResultVO.setMetaDataList(columnNames);
      int batchSize = sessionDataSetWrapper.getBatchSize();
      // 记录行数
      long count = 0;
      if (batchSize > 0) {
        while (sessionDataSetWrapper.hasNext()) {
          List<String> strList = new ArrayList<>();
          RowRecord rowRecord = sessionDataSetWrapper.next();
          count++;
          for (org.apache.iotdb.tsfile.read.common.Field field : rowRecord.getFields()) {
            strList.add(field.toString());
          }
          valuelist.add(strList);
        }
        long end = System.currentTimeMillis();
        double time = (end - start + 0.0d) / 1000;
        String queryTime = time + "s";
        sqlResultVO.setQueryTime(queryTime);
        sqlResultVO.setLine(count);
        sqlResultVO.setValueList(valuelist);
      }
      return sqlResultVO;
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.SQL_EP, ErrorCode.SQL_EP_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.SQL_EP, ErrorCode.SQL_EP_MSG);
    } finally {
      closeResultSet(sessionDataSetWrapper);
      if (sessionPool != null && closePool) {
        sessionPool.close();
      }
    }
  }

  private SqlResultVO executeQuery(
      SessionPool sessionPool, String sql, Boolean closePool, String notStopKey)
      throws BaseException {
    SqlResultVO sqlResultVO = new SqlResultVO();
    List<List<String>> valuelist = new ArrayList<>();
    SessionDataSetWrapper sessionDataSetWrapper = null;
    boolean timeFlag = false;
    try {
      sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
      long start = System.currentTimeMillis();
      List<String> columnNames = sessionDataSetWrapper.getColumnNames();
      sqlResultVO.setMetaDataList(columnNames);
      if ("Time".equals(columnNames.get(0))) {
        timeFlag = true;
      }
      int batchSize = sessionDataSetWrapper.getBatchSize();
      // 记录行数
      long count = 0;
      if (batchSize > 0) {
        while (sessionDataSetWrapper.hasNext() && QUERY_STOP.get(notStopKey)) {
          List<String> strList = new ArrayList<>();
          RowRecord rowRecord = sessionDataSetWrapper.next();
          if (timeFlag) {
            long timestamp = rowRecord.getTimestamp();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date(timestamp);
            String timeStr = simpleDateFormat.format(date);
            strList.add(timeStr);
          }
          count++;
          for (org.apache.iotdb.tsfile.read.common.Field field : rowRecord.getFields()) {
            strList.add(field.toString());
          }
          valuelist.add(strList);
        }
        long end = System.currentTimeMillis();
        double time = (end - start + 0.0d) / 1000;
        String queryTime = time + "s";
        sqlResultVO.setQueryTime(queryTime);
        sqlResultVO.setLine(count);
      }
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.SQL_EP, ErrorCode.SQL_EP_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      if (e.getStatusCode() == 602) {
        throw new BaseException(
            ErrorCode.NO_PRI_TIMESERIES_DATA, ErrorCode.NO_PRI_TIMESERIES_DATA_MSG);
      }
      throw new BaseException(ErrorCode.SQL_EP, ErrorCode.SQL_EP_MSG);
    } finally {
      closeResultSet(sessionDataSetWrapper);
      if (sessionPool != null && closePool) {
        sessionPool.close();
      }
    }
    sqlResultVO.setValueList(valuelist);
    return sqlResultVO;
  }

  private <T> CountDTO executeQuery(
      Class<T> clazz,
      SessionPool sessionPool,
      String sql,
      Integer pageSize,
      Integer pageNum,
      String keyword)
      throws BaseException {
    SessionDataSetWrapper sessionDataSetWrapper = null;
    try {
      sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
      List<T> results = new ArrayList<>();
      int batchSize = sessionDataSetWrapper.getBatchSize();
      int count = 0;
      if (batchSize > 0) {
        while (sessionDataSetWrapper.hasNext()) {
          RowRecord rowRecord = sessionDataSetWrapper.next();
          count++;
          if (count >= pageSize * (pageNum - 1) + 1 && count <= pageSize * pageNum) {
            T t = clazz.newInstance();
            List<org.apache.iotdb.tsfile.read.common.Field> fields = rowRecord.getFields();
            List<String> columnNames = sessionDataSetWrapper.getColumnNames();
            for (int i = 0; i < fields.size(); i++) {
              Field field = clazz.getDeclaredField(columnNames.get(i).replaceAll(" ", ""));
              field.setAccessible(true);
              field.set(t, fields.get(i).toString());
            }
            results.add(t);
          }
        }
      }
      CountDTO countDTO = new CountDTO();
      countDTO.setObjects(results);
      countDTO.setTotalCount(count);
      Integer totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
      countDTO.setTotalPage(totalPage);
      return countDTO;
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_MSM_FAIL, ErrorCode.GET_MSM_FAIL_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_MSM_FAIL, ErrorCode.GET_MSM_FAIL_MSG);
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_MSM_FAIL, ErrorCode.GET_MSM_FAIL_MSG);
    } finally {
      closeResultSet(sessionDataSetWrapper);
    }
  }

  private String executeQueryOneValue(SessionPool sessionPool, String sql) throws BaseException {
    SessionDataSetWrapper sessionDataSetWrapper = null;
    try {
      sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
      int batchSize = sessionDataSetWrapper.getBatchSize();
      String value = null;
      if (batchSize > 0) {
        while (sessionDataSetWrapper.hasNext()) {
          RowRecord rowRecord = sessionDataSetWrapper.next();
          List<org.apache.iotdb.tsfile.read.common.Field> fields = rowRecord.getFields();
          value = fields.get(0).toString();
          break;
        }
      }
      return value;
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(
          ErrorCode.GET_SQL_ONE_VALUE_FAIL, ErrorCode.GET_SQL_ONE_VALUE_FAIL_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      if (e.getStatusCode() == 602 && sql != null && sql.contains("select")) {
        throw new BaseException(
            ErrorCode.NO_PRI_READ_TIMESERIES, ErrorCode.NO_PRI_READ_TIMESERIES_MSG);
      }
      throw new BaseException(
          ErrorCode.GET_SQL_ONE_VALUE_FAIL, ErrorCode.GET_SQL_ONE_VALUE_FAIL_MSG);
    } finally {
      closeResultSet(sessionDataSetWrapper);
    }
  }

  private CountDTO executeQueryOneColumn(
      SessionPool sessionPool, String sql, Integer pageSize, Integer pageNum) throws BaseException {
    SessionDataSetWrapper sessionDataSetWrapper = null;
    try {
      sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
      int batchSize = sessionDataSetWrapper.getBatchSize();
      List<String> values = new ArrayList<>();
      int count = 0;
      if (batchSize > 0) {
        while (sessionDataSetWrapper.hasNext()) {
          RowRecord rowRecord = sessionDataSetWrapper.next();
          count++;
          if (count >= pageSize * (pageNum - 1) + 1 && count <= pageSize * pageNum) {
            List<org.apache.iotdb.tsfile.read.common.Field> fields = rowRecord.getFields();
            values.add(fields.get(0).toString());
          }
        }
      }
      CountDTO countDTO = new CountDTO();
      countDTO.setObjects(values);
      countDTO.setTotalCount(count);
      Integer totalPage = count % pageSize == 0 ? count / pageSize : count / pageSize + 1;
      countDTO.setTotalPage(totalPage);
      return countDTO;
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(
          ErrorCode.GET_SQL_ONE_COLUMN_FAIL, ErrorCode.GET_SQL_ONE_COLUMN_FAIL_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      throw new BaseException(
          ErrorCode.GET_SQL_ONE_COLUMN_FAIL, ErrorCode.GET_SQL_ONE_COLUMN_FAIL_MSG);
    } finally {
      closeResultSet(sessionDataSetWrapper);
    }
  }

  private List<String> executeQueryOneColumn(SessionPool sessionPool, String sql)
      throws BaseException {
    SessionDataSetWrapper sessionDataSetWrapper = null;
    try {
      Callable call = () -> sessionPool.executeQueryStatement(sql);
      ExecutorService service = Executors.newFixedThreadPool(1);
      Future submit = service.submit(call);
      sessionDataSetWrapper = (SessionDataSetWrapper) submit.get(60, TimeUnit.SECONDS);
      int batchSize = sessionDataSetWrapper.getBatchSize();
      List<String> values = new ArrayList<>();
      if (batchSize > 0) {
        while (sessionDataSetWrapper.hasNext()) {
          RowRecord rowRecord = sessionDataSetWrapper.next();
          List<org.apache.iotdb.tsfile.read.common.Field> fields = rowRecord.getFields();
          values.add(fields.get(0).toString());
        }
      }
      return values;
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      if (e.getStatusCode() == 602) {
        throw new BaseException(ErrorCode.NO_PRI_DO_THIS, ErrorCode.NO_PRI_DO_THIS_MSG);
      } else {
        throw new BaseException(
            ErrorCode.GET_SQL_ONE_COLUMN_FAIL, ErrorCode.GET_SQL_ONE_COLUMN_FAIL_MSG);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
      throw new BaseException(ErrorCode.TIME_OUT, ErrorCode.TIME_OUT_MSG);
    } catch (ExecutionException e) {
      e.printStackTrace();
      if (e.getMessage().contains("600")) {
        throw new BaseException(ErrorCode.WRONG_USER, ErrorCode.WRONG_USER_MSG);
      }
      throw new BaseException(ErrorCode.CONN_REFUSED, ErrorCode.CONN_REFUSED_MSG);
    } catch (TimeoutException e) {
      logger.error(e.getMessage());
      throw new BaseException(ErrorCode.TIME_OUT, ErrorCode.TIME_OUT_MSG);
    } finally {
      closeResultSet(sessionDataSetWrapper);
    }
  }

  private int findType(SessionPool sessionPool, String path) throws BaseException {
    // 主要用于判断s路径是否已经不存在 iotdb存在已删除路径的权限还会展示出来的问题
    String sql = "count timeseries " + path;
    SessionDataSetWrapper sessionDataSetWrapper = null;
    try {
      sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
    } catch (IoTDBConnectionException e) {
      logger.error(e.getMessage());
      return -1;
    } catch (StatementExecutionException e) {
      logger.error(e.getMessage());
      return -1;
    } finally {
      if (sessionDataSetWrapper != null) {
        sessionDataSetWrapper.close();
      }
    }
    sql = "count storage group " + path;
    Integer isGroup = Integer.valueOf(executeQueryOneValue(sessionPool, sql));
    if (isGroup >= 1) {
      return 1;
    }
    sql = "count devices " + path;
    Integer isDevices = Integer.valueOf(executeQueryOneValue(sessionPool, sql));
    if (isDevices >= 1) {
      return 2;
    }
    return 3;
  }

  private List<TSEncoding> handleEncodingStr(List<String> encoding) throws BaseException {
    List<TSEncoding> list = new ArrayList<>();
    for (String s : encoding) {
      switch (s) {
        case "PLAIN":
          list.add(TSEncoding.PLAIN);
          break;
        case "PLAIN_DICTIONARY":
          list.add(TSEncoding.PLAIN_DICTIONARY);
          break;
        case "RLE":
          list.add(TSEncoding.RLE);
          break;
        case "DIFF":
          list.add(TSEncoding.DIFF);
          break;
        case "TS_2DIFF":
          list.add(TSEncoding.TS_2DIFF);
          break;
        case "BITMAP":
          list.add(TSEncoding.BITMAP);
          break;
        case "GORILLA_V1":
          list.add(TSEncoding.GORILLA_V1);
          break;
        case "REGULAR":
          list.add(TSEncoding.REGULAR);
          break;
        case "GORILLA":
          list.add(TSEncoding.GORILLA);
          break;
        default:
          throw new BaseException(ErrorCode.DB_ENCODING_WRONG, ErrorCode.DB_ENCODING_WRONG_MSG);
      }
    }
    return list;
  }

  private List<CompressionType> handleCompressionStr(List<String> compression)
      throws BaseException {
    List<CompressionType> list = new ArrayList<>();
    for (String s : compression) {
      switch (s) {
        case "UNCOMPRESSED":
          list.add(CompressionType.UNCOMPRESSED);
          break;
        case "SNAPPY":
          list.add(CompressionType.SNAPPY);
          break;
        case "GZIP":
          list.add(CompressionType.GZIP);
          break;
        case "LZ4":
          list.add(CompressionType.LZ4);
          break;
        case "LZO":
          list.add(CompressionType.LZO);
          break;
        case "PLA":
          list.add(CompressionType.PLA);
          break;
        case "PAA":
          list.add(CompressionType.PAA);
          break;
        case "SDT":
          list.add(CompressionType.SDT);
          break;
        default:
          throw new BaseException(
              ErrorCode.DB_COMPRESSION_WRONG, ErrorCode.DB_COMPRESSION_WRONG_MSG);
      }
    }
    return list;
  }

  private List<Object> handleValueStr(List<String> values, List<TSDataType> types)
      throws BaseException {
    List<Object> list = new ArrayList<>();
    for (int i = 0; i < types.size(); i++) {
      TSDataType type = types.get(i);
      if (type == TSDataType.BOOLEAN) {
        Integer booleanNum = Integer.valueOf(values.get(i));
        Boolean flag = null;
        if (booleanNum == 0) {
          flag = false;
        }
        if (booleanNum == 1) {
          flag = true;
        }
        if (flag != null) {
          list.add(flag);
          continue;
        }
        throw new BaseException(ErrorCode.DB_BOOL_WRONG, ErrorCode.DB_BOOL_WRONG_MSG);
      }
      if (type == TSDataType.INT32 || type == TSDataType.INT64) {
        Integer intNum = Integer.valueOf(values.get(i));
        list.add(intNum);
        continue;
      }
      if (type == TSDataType.FLOAT) {
        Float floatNum = Float.valueOf(values.get(i));
        list.add(floatNum);
        continue;
      }
      if (type == TSDataType.DOUBLE) {
        Double doubleNum = Double.valueOf(values.get(i));
        list.add(doubleNum);
        continue;
      }
      list.add(values.get(i));
    }
    return list;
  }

  private List<TSDataType> handleTypeStr(List<String> types) throws BaseException {
    List<TSDataType> list = new ArrayList<>();
    for (String type : types) {
      TSDataType tsDataType;
      switch (type) {
        case "BOOLEAN":
          tsDataType = TSDataType.BOOLEAN;
          break;
        case "INT32":
          tsDataType = TSDataType.INT32;
          break;
        case "INT64":
          tsDataType = TSDataType.INT64;
          break;
        case "FLOAT":
          tsDataType = TSDataType.FLOAT;
          break;
        case "DOUBLE":
          tsDataType = TSDataType.DOUBLE;
          break;
        case "TEXT":
          tsDataType = TSDataType.TEXT;
          break;
        default:
          throw new BaseException(ErrorCode.DB_DATATYPE_WRONG, ErrorCode.DB_DATATYPE_WRONG_MSG);
      }
      list.add(tsDataType);
    }
    return list;
  }

  public static SessionPool getSessionPool(Connection connection) throws BaseException {
    String host = connection.getHost();
    Integer port = connection.getPort();
    String username = connection.getUsername();
    String password = connection.getPassword();
    SessionPool sessionPool = null;
    try {
      sessionPool = new SessionPool(host, port, username, password, 3);
    } catch (Exception e) {
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    }
    return sessionPool;
  }

  private String handlerPrivilegeStrToSql(String privilege, String userName, String roleName) {
    int i = privilege.indexOf(":");
    String path = privilege.substring(0, i).trim();
    String[] privileges = privilege.substring(i + 1).trim().split(" ");
    int len = privileges.length;
    if (len == 0) {
      return null;
    }
    StringBuilder str = new StringBuilder();
    if (userName != null) {
      str.append("grant user " + userName + " privileges ");
    } else {
      str.append("grant role " + roleName + " privileges ");
    }
    for (int j = 0; i < len - 1; j++) {
      str.append("'" + privileges[j] + "',");
    }
    str.append("'" + privileges[len - 1] + "' on " + path);
    return str.toString();
  }

  private void closeSessionPool(SessionPool sessionPool) {
    if (sessionPool != null) {
      sessionPool.close();
    }
  }

  private void closeResultSet(SessionDataSetWrapper sessionDataSetWrapper) {
    if (sessionDataSetWrapper != null) {
      sessionDataSetWrapper.close();
    }
  }
}
