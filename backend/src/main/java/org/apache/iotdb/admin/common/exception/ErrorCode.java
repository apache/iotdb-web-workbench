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

package org.apache.iotdb.admin.common.exception;

/** 错误码类 */
public class ErrorCode {

  // 连接相关
  public static final String ALIAS_REPEAT = "CONN-0001";
  public static final String ALIAS_REPEAT_MSG = "别名重复";

  public static final String INSERT_CONN_FAIL = "CONN-0002";
  public static final String INSERT_CONN_FAIL_MSG = "添加或更新连接失败";

  public static final String DELETE_CONN_FAIL = "CONN-0003";
  public static final String DELETE_CONN_FAIL_MSG = "删除连接失败";

  public static final String NO_CONN = "CONN-0004";
  public static final String NO_CONN_MSG = "连接不存在";

  public static final String GET_CONN_FAIL = "CONN-0005";
  public static final String GET_CONN_FAIL_MSG = "获取连接失败";

  public static final String CHECK_FAIL = "CONN-0006";
  public static final String CHECK_FAIL_MSG = "没有权限或连接不存在";

  public static final String TEST_CONN_FAIL = "CONN-0007";
  public static final String TEST_CONN_FAIL_MSG = "连接不可达或连接超时";

  public static final String TEST_CONN_WRONG = "CONN-0008";
  public static final String TEST_CONN_WRONG_MSG = "连接失败,主机输入不合法";

  public static final String TIME_OUT = "CONN-0009";
  public static final String TIME_OUT_MSG = "连接超时";

  public static final String TEST_CONN_FAIL_PWD = "CONN-0010";
  public static final String TEST_CONN_FAIL_PWD_MSG = "连接失败，用户名或密码错误";

  // 登录相关
  public static final String LOGIN_FAIL_USER = "USER-0001";
  public static final String LOGIN_FAIL_USER_MSG = "登录失败,用户不存在";

  public static final String USER_EXIST = "USER-0002";
  public static final String USER_EXIST_MSG = "用户已存在";

  public static final String LOGIN_FAIL_PWD = "USER-0003";
  public static final String LOGIN_FAIL_PWD_MSG = "登录失败,密码错误";

  public static final String INSERT_USER_FAIL = "USER-0004";
  public static final String INSERT_USER_FAIL_MSG = "插入失败";

  public static final String WRONG_USER_PARAM = "USER-0005";
  public static final String WRONG_USER_PARAM_MSG = "请输入合法的用户名和密码";

  public static final String DELETE_USER_FAIL = "USER-0006";
  public static final String DELETE_USER_FAIL_MSG = "删除用户失败";

  public static final String NO_USER = "USER-0007";
  public static final String NO_USER_MSG = "用户未指定";

  public static final String USER_AUTH_FAIL = "USER-0008";
  public static final String USER_AUTH_FAIL_MSG = "用户不一致,不能进行操作";

  public static final String TOKEN_ERR = "USER-0009";
  public static final String TOKEN_ERR_MSG = "请登录或token失效";

  public static final String GET_TOKEN_FAIL = "USER-0010";
  public static final String GET_TOKEN_FAIL_MSG = "获取token失败";

  public static final String SET_JWT_FAIL = "USER-0011";
  public static final String SET_JWT_FAIL_MSG = "JWT编解码失败";

  // 导入导出csv相关
  public static final String FILE_NAME_ILLEGAL = "CSV-0001";
  public static final String FILE_NAME_ILLEGAL_MSG = "文件必须以.csv结尾";

  public static final String UPLOAD_FILE_FAIL = "CSV-0002";
  public static final String UPLOAD_FILE_FAIL_MSG = "上传CSV文件失败";

  public static final String FILE_FIRST_LINE_ILLEGAL = "CSV-0003";
  public static final String FILE_FIRST_LINE_ILLEGAL_MSG = "CSV文件格式有误，请检查第1行";

  public static final String FILE_LINE_ILLEGAL = "CSV-0004";
  public static final String FILE_LINE_ILLEGAL_MSG = "CSV文件格式有误，非法行:";

  public static final String FILE_FORMAT_ILLEGAL = "CSV-0005";
  public static final String FILE_FORMAT_ILLEGAL_MSG = "CSV文件格式有误，数据行的列数不能小于第一行的列数";

  public static final String FILE_TIME_ILLEGAL = "CSV-0006";
  public static final String FILE_TIME_ILLEGAL_MSG = "CSV文件时间戳格式有误";

  public static final String FILE_IO_FAIL = "CSV-0007";
  public static final String FILE_IO_FAIL_MSG = "文件读取或写入失败，由于:";

  public static final String IMPORT_CSV_FAIL = "CSV-0008";
  public static final String IMPORT_CSV_FAIL_MSG = "导入csv文件失败，由于:";

  public static final String CREATE_FILE_FAIL = "CSV-0009";
  public static final String CREATE_FILE_FAIL_MSG = "创建文件失败";

  public static final String EXPORT_CSV_FAIL = "CSV-0010";
  public static final String EXPORT_CSV_FAIL_MSG = "导出csv文件失败，由于:";

  public static final String FILE_NOT_FOUND = "CSV-0011";
  public static final String FILE_NOT_FOUND_MSG = "文件未找到";

  // iotDB相关
  public static final String INSERT_TS_FAIL = "IOTDB-0001";
  public static final String INSERT_TS_FAIL_MSG = "插入时间序列失败";

  public static final String DELETE_TS_FAIL = "IOTDB-0002";
  public static final String DELETE_TS_FAIL_MSG = "删除时间序列失败";

  public static final String DB_BOOL_WRONG = "IOTDB-0003";
  public static final String DB_BOOL_WRONG_MSG = "Boolean值输入错误,0为false，1为true";

  public static final String DB_DATATYPE_WRONG = "IOTDB-0004";
  public static final String DB_DATATYPE_WRONG_MSG = "物理量数据类型传入错误";

  public static final String GET_DBCONN_FAIL = "IOTDB-005";
  public static final String GET_DBCONN_FAIL_MSG = "获取数据库连接失败";

  public static final String GET_SESSION_FAIL = "IOTDB-0006";
  public static final String GET_SESSION_FAIL_MSG = "连接IoTDB失败";

  public static final String CLOSE_DBCONN_FAIL = "IOTDB-0007";
  public static final String CLOSE_DBCONN_FAIL_MSG = "关闭IoTDB连接失败";

  public static final String SQL_EP = "IOTDB-0008";
  public static final String SQL_EP_MSG = "没有权限或sql异常";

  public static final String QUERY_FAIL = "IOTDB-0009";
  public static final String QUERY_FAIL_MSG = "sql查询失败";

  public static final String SQL_PARAM_WRONG = "IOTDB-0010";
  public static final String SQL_PARAM_WRONG_MSG = "sql参数不合法";

  public static final String WRONG_DB_PARAM = "IOTDB-0011";
  public static final String WRONG_DB_PARAM_MSG = "输入参数不合法";

  public static final String GET_USER_FAIL = "IOTDB-0012";
  public static final String GET_USER_FAIL_MSG = "获取用户信息失败";

  public static final String GET_SQL_ONE_VALUE_FAIL = "IOTDB-0013";
  public static final String GET_SQL_ONE_VALUE_FAIL_MSG = "获取值失败";

  public static final String SET_TTL_FAIL = "IOTDB-0014";
  public static final String SET_TTL_FAIL_MSG = "设置存活时间失败";

  public static final String DEL_TTL_FAIL = "IOTDB-0015";
  public static final String DEL_TTL_FAIL_MSG = "删除存活时间失败";

  public static final String GET_SQL_ONE_COLUMN_FAIL = "IOTDB-0016";
  public static final String GET_SQL_ONE_COLUMN_FAIL_MSG = "获取列表失败";

  public static final String GET_SQL_SET_FAIL = "IOTDB-0017";
  public static final String GET_SQL_SET_FAIL_MSG = "获取列表集合失败";

  public static final String INSERT_DEV_FAIL = "IOTDB-0018";
  public static final String INSERT_DEV_FAIL_MSG = "插入实体失败";

  public static final String GET_MSM_FAIL = "IOTDB-0019";
  public static final String GET_MSM_FAIL_MSG = "获取物理量信息失败";

  public static final String NO_SUCH_FIELD = "IOTDB-0020";
  public static final String NO_SUCH_FIELD_MSG = "返回集没此属性";

  public static final String GET_LAST_VALUE_FAIL = "IOTDB-0021";
  public static final String GET_LAST_VALUE_FAIL_MSG = "获取物理量最新值失败";

  public static final String SET_GROUP_FAIL = "IOTDB-0022";
  public static final String SET_GROUP_FAIL_MSG = "创建存储组失败";

  public static final String DELETE_GROUP_FAIL = "IOTDB-0023";
  public static final String DELETE_GROUP_FAIL_MSG = "删除存储组失败";

  public static final String DELETE_DB_USER_FAIL = "IOTDB-0024";
  public static final String DELETE_DB_USER_FAIL_MSG = "删除数据库用户失败";

  public static final String DELETE_DB_ROLE_FAIL = "IOTDB-0025";
  public static final String DELETE_DB_ROLE_FAIL_MSG = "删除数据库角色失败";

  public static final String SET_DB_USER_FAIL = "IOTDB-0026";
  public static final String SET_DB_USER_FAIL_MSG = "创建数据库用户失败";

  public static final String SET_DB_ROLE_FAIL = "IOTDB-0027";
  public static final String SET_DB_ROLE_FAIL_MSG = "创建数据库角色失败";

  public static final String NO_TYPE = "IOTDB-0028";
  public static final String NO_TYPE_MSG = "粒度类型不存在";

  public static final String PRIV_ROOT_FAIL = "IOTDB-0029";
  public static final String PRIV_ROOT_FAIL_MSG = "根路径权限操作失败";

  public static final String PRIV_GROUP_FAIL = "IOTDB-0030";
  public static final String PRIV_GROUP_FAIL_MSG = "组路径权限操作失败";

  public static final String PRIV_DEVICE_FAIL = "IOTDB-0031";
  public static final String PRIV_DEVICE_FAIL_MSG = "实体路径权限操作失败";

  public static final String PRIV_TIMESERIES_FAIL = "IOTDB-0032";
  public static final String PRIV_TIMESERIES_FAIL_MSG = "物理量路径权限操作失败";

  public static final String GET_RECORD_FAIL = "IOTDB-0033";
  public static final String GET_RECORD_FAIL_MSG = "获取物理量记录失败";

  public static final String NO_SQL = "IOTDB-0034";
  public static final String NO_SQL_MSG = "查询脚本没有sql语句";

  public static final String UPDATE_GROUP_INFO_FAIL = "IOTDB-0035";
  public static final String UPDATE_GROUP_INFO_FAIL_MSG = "更新存储组信息失败";

  public static final String NO_GROUP_INFO = "IOTDB-0036";
  public static final String NO_GROUP_INFO_MSG = "不存在存储组信息";

  public static final String NO_GROUP = "IOTDB-0037";
  public static final String NO_GROUP_MSG = "不存在存储组";

  public static final String NO_DEVICE_INFO = "IOTDB-0038";
  public static final String NO_DEVICE_INFO_MSG = "不存在设备信息";

  public static final String UPDATE_PWD_FAIL = "IOTDB-0039";
  public static final String UPDATE_PWD_FAIL_MSG = "修改账号密码失败";

  public static final String NO_QUERY = "IOTDB-0040";
  public static final String NO_QUERY_MSG = "不存在此查询或该查询已结束";

  public static final String NO_ALL_NUM_SEARCH = "IOTDB-0041";
  public static final String NO_ALL_NUM_SEARCH_MSG = "不支持纯数字查询";

  public static final String NO_PRI_SET_TTL = "IOTDB-0042";
  public static final String NO_PRI_SET_TTL_MSG = "没有权限设置存活时间";

  public static final String NO_PRI_CREATE_TIMESERIES = "IOTDB-0043";
  public static final String NO_PRI_CREATE_TIMESERIES_MSG = "没有权限创建物理量";

  public static final String NO_PRI_READ_TIMESERIES = "IOTDB-0044";
  public static final String NO_PRI_READ_TIMESERIES_MSG = "没有权限查看物理量";

  public static final String NO_PRI_DELETE_TIMESERIES = "IOTDB-0045";
  public static final String NO_PRI_DELETE_TIMESERIES_MSG = "没有权限删除物理量";

  public static final String CONN_REFUSED = "IOTDB-0046";
  public static final String CONN_REFUSED_MSG = "连接错误,检查输入";

  public static final String WRONG_USER = "IOTDB-0047";
  public static final String WRONG_USER_MSG = "连接用户名或密码错误";

  public static final String NO_PRI_SET_GROUP = "IOTDB-0048";
  public static final String NO_PRI_SET_GROUP_MSG = "没有权限操作存储组";

  public static final String NO_PRI_DELETE_GROUP = "IOTDB-0048";
  public static final String NO_PRI_DELETE_GROUP_MSG = "没有权限删除存储组";

  public static final String NO_SUPPORT_SQL = "IOTDB-0049";
  public static final String NO_SUPPORT_SQL_MSG = "不支持此sql执行";

  public static final String TTL_WRONG = "IOTDB-0050";
  public static final String TTL_WRONG_MSG = "存活时间必须大于等于0";

  public static final String NO_PRI_TIMESERIES_DATA = "IOTDB-0051";
  public static final String NO_PRI_TIMESERIES_DATA_MSG = "没有权限查看测点数据";

  public static final String NO_SUP_CONTAIN_ROOT = "IOTDB-0052";
  public static final String NO_SUP_CONTAIN_ROOT_MSG = "root必须且只能作为路径开头";

  public static final String GET_DATA_FAIL = "IOTDB-0053";
  public static final String GET_DATA_FAIL_MSG = "获取物理量数据失败";

  public static final String UPDATE_DATA_FAIL = "IOTDB-0054";
  public static final String UPDATE_DATA_FAIL_MSG = "编辑物理量数据失败";

  public static final String DELETE_DATA_FAIL = "IOTDB-0055";
  public static final String DELETE_DATA_FAIL_MSG = "删除物理量数据失败";

  public static final String GET_DATA_COUNT_FAIL = "IOTDB-0056";
  public static final String GET_DATA_COUNT_FAIL_MSG = "获取数据统计信息失败";

  public static final String DB_COMPRESSION_WRONG = "IOTDB-0057";
  public static final String DB_COMPRESSION_WRONG_MSG = "物理量压缩方式传入错误";

  public static final String DB_ENCODING_WRONG = "IOTDB-0058";
  public static final String DB_ENCODING_WRONG_MSG = "物理量编码方式传入错误";

  public static final String UPSERT_ALIAS_FAIL = "IOTDB-0059";
  public static final String UPSERT_ALIAS_FAIL_MSG = "设置别名失败，请检查当前实体下是否已存在该别名";

  public static final String UPSERT_TAGS_FAIL = "IOTDB-0060";
  public static final String UPSERT_TAGS_FAIL_MSG = "设置标签失败";

  public static final String UPSERT_ATTRIBUTES_FAIL = "IOTDB-0061";
  public static final String UPSERT_ATTRIBUTES_FAIL_MSG = "设置属性失败";

  public static final String GET_MEASUREMENT_DATA_COUNT_FAIL = "IOTDB-0062";
  public static final String GET_MEASUREMENT_DATA_COUNT_FAIL_MSG = "获取物理量数据统计信息失败";

  public static final String RANDOM_IMPORT_DATA_FAIL = "IOTDB-0063";
  public static final String RANDOM_IMPORT_DATA_FAIL_MSG = "随机导入物理量数据失败";

  public static final String NO_MEASUREMENT = "IOTDB-0064";
  public static final String NO_MEASUREMENT_MSG = "该实体下没有物理量，不能插入数据";

  public static final String ROLE_GET_USERS_FAIL = "IOTDB-0065";
  public static final String ROLE_GET_USERS_FAIL_MSG = "获取角色的用户列表失败";

  public static final String REVOKE_ROLE = "IOTDB-0066";
  public static final String REVOKE_ROLE_MSG = "撤销用户角色失败";

  public static final String GRANT_ROLE = "IOTDB-0067";
  public static final String GRANT_ROLE_MSG = "赋予用户角色失败";

  public static final String NO_PRI_DO_THIS = "IOTDB-0068";
  public static final String NO_PRI_DO_THIS_MSG = "没有权限进行该操作";

  public static final String NO_PRI_CREATE_USER = "IOTDB-0069";
  public static final String NO_PRI_CREATE_USER_MSG = "没有权限创建用户";

  public static final String NO_PRI_DELETE_USER = "IOTDB-0070";
  public static final String NO_PRI_DELETE_USER_MSG = "没有权限删除用户";

  public static final String NO_PRI_CREATE_ROLE = "IOTDB-0071";
  public static final String NO_PRI_CREATE_ROLE_MSG = "没有权限创建角色";

  public static final String NO_PRI_DELETE_ROLE = "IOTDB-0072";
  public static final String NO_PRI_DELETE_ROLE_MSG = "没有权限删除角色";

  public static final String NO_PRI_LIST_ROLE = "IOTDB-0073";
  public static final String NO_PRI_LIST_ROLE_MSG = "没有权限获取角色信息";

  public static final String NO_PRI_GRANT_USER_ROLE = "IOTDB-0074";
  public static final String NO_PRI_GRANT_USER_ROLE_MSG = "没有权限赋予用户角色";

  public static final String NO_PRI_REVOKE_USER_ROLE = "IOTDB-0075";
  public static final String NO_PRI_REVOKE_USER_ROLE_MSG = "没有权限撤销用户角色";

  public static final String NO_PRI_GRANT_PRIVILEGE = "IOTDB-0076";
  public static final String NO_PRI_GRANT_PRIVILEGE_MSG = "没有给用户或角色赋权的权限";

  public static final String GET_USER_PRIVILEGE_FAIL = "IOTDB-0080";
  public static final String GET_USER_PRIVILEGE_FAIL_MSG = "获取用户权限信息失败";

  public static final String GET_ROLE_PRIVILEGE_FAIL = "IOTDB-0081";
  public static final String GET_ROLE_PRIVILEGE_FAIL_MSG = "获取角色权限信息失败";

  public static final String NOT_SUPPORT_ALL_DIGIT = "IOTDB-0082";
  public static final String NOT_SUPPORT_ALL_DIGIT_MSG = "名称不能为纯数字";

  public static final String USER_NAME_EXISTS = "IOTDB-0083";
  public static final String USER_NAME_EXISTS_MSG = "用户名已存在";

  public static final String ROLE_NAME_EXISTS = "IOTDB-0083";
  public static final String ROLE_NAME_EXISTS_MSG = "角色名已存在";

  public static final String NO_SUP_CONTAIN_WORD = "IOTDB-0084";
  public static final String NO_SUP_CONTAIN_WORD_MSG = "as/null/like不能作为路径名";

  public static final String MEASUREMENTS_NAME_CONTAIN_DOT = "IOTDB-0085";
  public static final String MEASUREMENTS_NAME_CONTAIN_DOT_MSG = "物理量层名不能级联";

  public static final String MEASUREMENT_NAME_EQUALS_DEVICE = "IOTDB-0086";
  public static final String MEASUREMENT_NAME_EQUALS_DEVICE_MSG = "实体名不能和已有物理量重名";

  public static final String GET_DATA_PRIVILEGE_FAIL = "IOTDB-0087";
  public static final String GET_DATA_PRIVILEGE_FAIL_MSG = "获取数据管理权限信息失败";

  // 存储组表相关
  public static final String INSERT_GROUP_INFO_FAIL = "GROUP-0001";
  public static final String INSERT_GROUP_INFO_FAIL_MSG = "插入存储组信息失败";

  public static final String DELETE_GROUP_INFO_FAIL = "GROUP-0002";
  public static final String DELETE_GROUP_INFO_FAIL_MSG = "删除存储组信息失败";

  // 实体表相关
  public static final String DELETE_DEVICE_INFO_FAIL = "DEV-0001";
  public static final String DELETE_DEVICE_INFO_FAIL_MSG = "删除实体信息失败";

  public static final String SET_DEVICE_INFO_FAIL = "DEV-0002";
  public static final String SET_DEVICE_INFO_FAIL_MSG = "插入实体信息失败";

  // 物理量表相关
  public static final String DELETE_MEASUREMENT_INFO_FAIL = "MEASU-0001";
  public static final String DELETE_MEASUREMENT_INFO_FAIL_MSG = "删除物理量信息失败";

  public static final String SET_MEASUREMENT_INFO_FAIL = "MEASU-0002";
  public static final String SET_MEASUREMENT_INFO_FAIL_MSG = "插入物理量信息失败";

  public static final String GET_MSM_DES_FAIL = "MEASU-0003";
  public static final String GET_MSM_DES_FAIL_MSG = "获取物理量描述信息失败";

  // 查询表相关
  public static final String QUERY_EXIST = "QUERY-0001";
  public static final String QUERY_EXIST_MSG = "脚本名已存在";

  public static final String QUERY_NOT_EXIST = "QUERY-0002";
  public static final String QUERY_NOT_EXIST_MSG = "脚本不存在";

  // 角色表相关
  public static final String UPSERT_ROLE_INFO_FAIL = "ROLE-0001";
  public static final String UPSERT_ROLE_INFO_FAIL_MSG = "更新角色信息失败";

  public static final String DELETE_ROLE_INFO_FAIL = "ROLE-0002";
  public static final String DELETE_ROLE_INFO_FAIL_MSG = "删除角色信息失败";

  public static final String GET_ROLE_INFO_FAIL = "ROLE-0003";
  public static final String GET_ROLE_INFO_FAIL_MSG = "获取角色信息失败";

  // 参数校验相关
  public static final String WRONG_PARAM = "PARAM-0001";
}
