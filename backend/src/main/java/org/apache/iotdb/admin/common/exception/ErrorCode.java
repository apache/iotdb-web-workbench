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

public class ErrorCode {

  // Connection
  public static final String ALIAS_REPEAT = "CONN-0001";
  public static final String ALIAS_REPEAT_MSG = "Connection alias already exists";

  public static final String INSERT_CONN_FAIL = "CONN-0002";
  public static final String INSERT_CONN_FAIL_MSG = "Failed to update or create connection";

  public static final String DELETE_CONN_FAIL = "CONN-0003";
  public static final String DELETE_CONN_FAIL_MSG = "Failed to delete connection";

  public static final String NO_CONN = "CONN-0004";
  public static final String NO_CONN_MSG = "The connection doesn't exist";

  public static final String GET_CONN_FAIL = "CONN-0005";
  public static final String GET_CONN_FAIL_MSG = "Failed to get connection";

  public static final String CHECK_FAIL = "CONN-0006";
  public static final String CHECK_FAIL_MSG =
      "The connection doesn't exist or the account doesn't have permissions";

  public static final String TEST_CONN_FAIL = "CONN-0007";
  public static final String TEST_CONN_FAIL_MSG = "Unreachable connection";

  public static final String TIME_OUT = "CONN-0009";
  public static final String TIME_OUT_MSG = "Connection timeout";

  public static final String TEST_CONN_FAIL_PWD = "CONN-0010";
  public static final String TEST_CONN_FAIL_PWD_MSG = "The user name or password is incorrect";

  // Login
  public static final String LOGIN_FAIL_USER = "USER-0001";
  public static final String LOGIN_FAIL_USER_MSG = "The user does not exist";

  public static final String USER_EXIST = "USER-0002";
  public static final String USER_EXIST_MSG = "The user name already exists";

  public static final String LOGIN_FAIL_PWD = "USER-0003";
  public static final String LOGIN_FAIL_PWD_MSG = "Login failed, password is incorrect";

  public static final String INSERT_USER_FAIL = "USER-0004";
  public static final String INSERT_USER_FAIL_MSG = "Failed to create user";

  public static final String WRONG_USER_PARAM = "USER-0005";
  public static final String WRONG_USER_PARAM_MSG = "Please enter a valid user name and password";

  public static final String DELETE_USER_FAIL = "USER-0006";
  public static final String DELETE_USER_FAIL_MSG = "Failed to delete user";

  public static final String NO_USER = "USER-0007";
  public static final String NO_USER_MSG = "User not specified";

  public static final String USER_AUTH_FAIL = "USER-0008";
  public static final String USER_AUTH_FAIL_MSG = "Validation fails, the users are inconsistent";

  public static final String TOKEN_ERR = "USER-0009";
  public static final String TOKEN_ERR_MSG = "No login or token is invalid";

  public static final String GET_TOKEN_FAIL = "USER-0010";
  public static final String GET_TOKEN_FAIL_MSG = "Failed to get token";

  public static final String SET_JWT_FAIL = "USER-0011";
  public static final String SET_JWT_FAIL_MSG = "JWT encoding or decoding failed";

  // CSV
  public static final String FILE_NAME_ILLEGAL = "CSV-0001";
  public static final String FILE_NAME_ILLEGAL_MSG = "The file must be a CSV file";

  public static final String UPLOAD_FILE_FAIL = "CSV-0002";
  public static final String UPLOAD_FILE_FAIL_MSG = "Failed to upload the CSV file";

  public static final String FILE_FIRST_LINE_ILLEGAL = "CSV-0003";
  public static final String FILE_FIRST_LINE_ILLEGAL_MSG =
      "The CSV file format is incorrect.Please check line 1";

  public static final String FILE_LINE_ILLEGAL = "CSV-0004";
  public static final String FILE_LINE_ILLEGAL_MSG = "CSV file format error, illegal line:";

  public static final String FILE_FORMAT_ILLEGAL = "CSV-0005";
  public static final String FILE_FORMAT_ILLEGAL_MSG =
      "CSV file format error. The number of columns in the data row cannot be smaller than that in the first row";

  public static final String FILE_TIME_ILLEGAL = "CSV-0006";
  public static final String FILE_TIME_ILLEGAL_MSG =
      "The timestamp format of the CSV file is incorrect";

  public static final String FILE_IO_FAIL = "CSV-0007";
  public static final String FILE_IO_FAIL_MSG = "File read or write failed due to:";

  public static final String IMPORT_CSV_FAIL = "CSV-0008";
  public static final String IMPORT_CSV_FAIL_MSG = "Failed to import CSV file because:";

  public static final String CREATE_FILE_FAIL = "CSV-0009";
  public static final String CREATE_FILE_FAIL_MSG = "Failed to create file";

  public static final String EXPORT_CSV_FAIL = "CSV-0010";
  public static final String EXPORT_CSV_FAIL_MSG = "Failed to export the CSV file because:";

  public static final String FILE_NOT_FOUND = "CSV-0011";
  public static final String FILE_NOT_FOUND_MSG = "File not found";

  // IoTDB
  public static final String INSERT_TS_FAIL = "IOTDB-0001";
  public static final String INSERT_TS_FAIL_MSG = "Failed to create measurement";

  public static final String DELETE_TS_FAIL = "IOTDB-0002";
  public static final String DELETE_TS_FAIL_MSG = "Failed to delete measurement";

  public static final String DB_DATATYPE_WRONG = "IOTDB-0004";
  public static final String DB_DATATYPE_WRONG_MSG = "The data type of measurement is wrong";

  public static final String GET_SESSION_FAIL = "IOTDB-0006";
  public static final String GET_SESSION_FAIL_MSG = "Failed to connect to IoTDB database";

  public static final String CLOSE_DBCONN_FAIL = "IOTDB-0007";
  public static final String CLOSE_DBCONN_FAIL_MSG = "Failed to close connection";

  public static final String SQL_EP = "IOTDB-0008";
  public static final String SQL_EP_MSG = "No privileges or SQL errors";

  public static final String WRONG_DB_PARAM = "IOTDB-0011";
  public static final String WRONG_DB_PARAM_MSG = "The input parameter is invalid";

  public static final String GET_SQL_ONE_VALUE_FAIL = "IOTDB-0013";
  public static final String GET_SQL_ONE_VALUE_FAIL_MSG = "Failed to get data value";

  public static final String SET_TTL_FAIL = "IOTDB-0014";
  public static final String SET_TTL_FAIL_MSG = "Failed to set the TTL.";

  public static final String GET_SQL_ONE_COLUMN_FAIL = "IOTDB-0016";
  public static final String GET_SQL_ONE_COLUMN_FAIL_MSG = "Failed to get the data list";

  public static final String GET_MSM_FAIL = "IOTDB-0019";
  public static final String GET_MSM_FAIL_MSG = "Failed to get the measurement information";

  public static final String SET_GROUP_FAIL = "IOTDB-0022";
  public static final String SET_GROUP_FAIL_MSG = "Failed to create storage group";

  public static final String SET_GROUP_FAIL_EXISTS = "IOTDB-0095";
  public static final String SET_GROUP_FAIL__EXISTS_MSG =
      "Failed to create storage group, the storage group already exists";

  public static final String DELETE_GROUP_FAIL = "IOTDB-0023";
  public static final String DELETE_GROUP_FAIL_MSG = "Failed to delete storage group";

  public static final String DELETE_DB_USER_FAIL = "IOTDB-0024";
  public static final String DELETE_DB_USER_FAIL_MSG = "Failed to delete IoTDB user";

  public static final String DELETE_DB_ROLE_FAIL = "IOTDB-0025";
  public static final String DELETE_DB_ROLE_FAIL_MSG = "Failed to delete IoTDB role";

  public static final String SET_DB_USER_FAIL = "IOTDB-0026";
  public static final String SET_DB_USER_FAIL_MSG = "Failed to create IoTDB user";

  public static final String SET_DB_ROLE_FAIL = "IOTDB-0027";
  public static final String SET_DB_ROLE_FAIL_MSG = "Failed to create IoTDB role";

  public static final String NO_TYPE = "IOTDB-0028";
  public static final String NO_TYPE_MSG = "Privileges type does not exist";

  public static final String GET_RECORD_FAIL = "IOTDB-0033";
  public static final String GET_RECORD_FAIL_MSG = "Failed to get the measurement record";

  public static final String NO_SQL = "IOTDB-0034";
  public static final String NO_SQL_MSG = "The query script has no SQL statements";

  public static final String UPDATE_GROUP_INFO_FAIL = "IOTDB-0035";
  public static final String UPDATE_GROUP_INFO_FAIL_MSG =
      "Failed to update the storage group information";

  public static final String NO_GROUP_INFO = "IOTDB-0036";
  public static final String NO_GROUP_INFO_MSG = "The storage group information doesn't exist";

  public static final String NO_DEVICE_INFO = "IOTDB-0038";
  public static final String NO_DEVICE_INFO_MSG = "The entity information doesn't exist";

  public static final String UPDATE_PWD_FAIL = "IOTDB-0039";
  public static final String UPDATE_PWD_FAIL_MSG = "Failed to change the account password";

  public static final String NO_QUERY = "IOTDB-0040";
  public static final String NO_QUERY_MSG = "The query does not exist or has ended";

  public static final String NO_PRI_SET_TTL = "IOTDB-0042";
  public static final String NO_PRI_SET_TTL_MSG = "No privilege to set the TTL";

  public static final String NO_PRI_CREATE_TIMESERIES = "IOTDB-0043";
  public static final String NO_PRI_CREATE_TIMESERIES_MSG =
      "No privilege to create the measurement";

  public static final String NO_PRI_READ_TIMESERIES = "IOTDB-0044";
  public static final String NO_PRI_READ_TIMESERIES_MSG = "No privilege to read the measurement";

  public static final String NO_PRI_DELETE_TIMESERIES = "IOTDB-0045";
  public static final String NO_PRI_DELETE_TIMESERIES_MSG =
      "No privilege to delete the measurement";

  public static final String CONN_REFUSED = "IOTDB-0046";
  public static final String CONN_REFUSED_MSG = "Connecting to IoTDB error";

  public static final String WRONG_USER = "IOTDB-0047";
  public static final String WRONG_USER_MSG =
      "The user name or password of connection is incorrect";

  public static final String NO_PRI_SET_GROUP = "IOTDB-0048";
  public static final String NO_PRI_SET_GROUP_MSG = "No privilege to set the storage group";

  public static final String NO_PRI_DELETE_GROUP = "IOTDB-0048";
  public static final String NO_PRI_DELETE_GROUP_MSG = "No privilege to delete the storage group";

  public static final String TTL_WRONG = "IOTDB-0050";
  public static final String TTL_WRONG_MSG = "The TTL must be greater than or equal to 0";

  public static final String NO_PRI_TIMESERIES_DATA = "IOTDB-0051";
  public static final String NO_PRI_TIMESERIES_DATA_MSG =
      "No privilege to read the measurement data";

  public static final String NO_SUP_CONTAIN_ROOT = "IOTDB-0052";
  public static final String NO_SUP_CONTAIN_ROOT_MSG =
      "Root must and can only be the beginning of a path";

  public static final String GET_DATA_FAIL = "IOTDB-0053";
  public static final String GET_DATA_FAIL_MSG = "Failed to get measurement data";

  public static final String UPDATE_DATA_FAIL = "IOTDB-0054";
  public static final String UPDATE_DATA_FAIL_MSG = "Failed to update measurement data";

  public static final String DELETE_DATA_FAIL = "IOTDB-0055";
  public static final String DELETE_DATA_FAIL_MSG = "Failed to delete measurement data";

  public static final String GET_DATA_COUNT_FAIL = "IOTDB-0056";
  public static final String GET_DATA_COUNT_FAIL_MSG = "Failed to obtain data statistics";

  public static final String DB_COMPRESSION_WRONG = "IOTDB-0057";
  public static final String DB_COMPRESSION_WRONG_MSG =
      "The compression type is passed in incorrectly";

  public static final String DB_ENCODING_WRONG = "IOTDB-0058";
  public static final String DB_ENCODING_WRONG_MSG = "The encoding type is passed in incorrectly";

  public static final String UPSERT_ALIAS_FAIL = "IOTDB-0059";
  public static final String UPSERT_ALIAS_FAIL_MSG =
      "Failed to set the alias. Please check whether the alias already exists under the current entity";

  public static final String UPSERT_TAGS_FAIL = "IOTDB-0060";
  public static final String UPSERT_TAGS_FAIL_MSG = "Failed to set the tags";

  public static final String UPSERT_ATTRIBUTES_FAIL = "IOTDB-0061";
  public static final String UPSERT_ATTRIBUTES_FAIL_MSG = "Failed to set the attributes";

  public static final String GET_MEASUREMENT_DATA_COUNT_FAIL = "IOTDB-0062";
  public static final String GET_MEASUREMENT_DATA_COUNT_FAIL_MSG =
      "Failed to get measurement data statistics";

  public static final String RANDOM_IMPORT_DATA_FAIL = "IOTDB-0063";
  public static final String RANDOM_IMPORT_DATA_FAIL_MSG =
      "Failed to import measurement data randomly";

  public static final String NO_MEASUREMENT = "IOTDB-0064";
  public static final String NO_MEASUREMENT_MSG = " No measurement, cannot insert data";

  public static final String ROLE_GET_USERS_FAIL = "IOTDB-0065";
  public static final String ROLE_GET_USERS_FAIL_MSG = "Failed to get the user list for the role";

  public static final String REVOKE_ROLE = "IOTDB-0066";
  public static final String REVOKE_ROLE_MSG = "Failed to revoke the role of user.";

  public static final String GRANT_ROLE = "IOTDB-0067";
  public static final String GRANT_ROLE_MSG = "Failed to grant role to the user";

  public static final String NO_PRI_DO_THIS = "IOTDB-0068";
  public static final String NO_PRI_DO_THIS_MSG = "No privilege to do this";

  public static final String NO_PRI_CREATE_USER = "IOTDB-0069";
  public static final String NO_PRI_CREATE_USER_MSG = "No privilege to create user";

  public static final String NO_PRI_DELETE_USER = "IOTDB-0070";
  public static final String NO_PRI_DELETE_USER_MSG = "No privilege to delete user";

  public static final String NO_PRI_CREATE_ROLE = "IOTDB-0071";
  public static final String NO_PRI_CREATE_ROLE_MSG = "No privilege to create role";

  public static final String NO_PRI_DELETE_ROLE = "IOTDB-0072";
  public static final String NO_PRI_DELETE_ROLE_MSG = "No privilege to delete role";

  public static final String NO_PRI_LIST_ROLE = "IOTDB-0073";
  public static final String NO_PRI_LIST_ROLE_MSG = "No privilege to list role";

  public static final String NO_PRI_GRANT_USER_ROLE = "IOTDB-0074";
  public static final String NO_PRI_GRANT_USER_ROLE_MSG = "No privilege to grant user role";

  public static final String NO_PRI_REVOKE_USER_ROLE = "IOTDB-0075";
  public static final String NO_PRI_REVOKE_USER_ROLE_MSG = "No privilege to revoke user role";

  public static final String NO_PRI_GRANT_PRIVILEGE = "IOTDB-0076";
  public static final String NO_PRI_GRANT_PRIVILEGE_MSG = "No privilege to authorize user or role";

  public static final String GET_USER_PRIVILEGE_FAIL = "IOTDB-0080";
  public static final String GET_USER_PRIVILEGE_FAIL_MSG =
      "Failed to get user privilege information";

  public static final String GET_ROLE_PRIVILEGE_FAIL = "IOTDB-0081";
  public static final String GET_ROLE_PRIVILEGE_FAIL_MSG =
      "Failed to get role privilege information";

  public static final String NOT_SUPPORT_ALL_DIGIT = "IOTDB-0082";
  public static final String NOT_SUPPORT_ALL_DIGIT_MSG = "The name cannot contain only digits";

  public static final String USER_NAME_EXISTS = "IOTDB-0083";
  public static final String USER_NAME_EXISTS_MSG = "The user name already exists";

  public static final String ROLE_NAME_EXISTS = "IOTDB-0083";
  public static final String ROLE_NAME_EXISTS_MSG = "The role name already exists";

  public static final String NO_SUP_CONTAIN_WORD = "IOTDB-0084";
  public static final String NO_SUP_CONTAIN_WORD_MSG =
      "As, null, or like cannot be used as a path name";

  public static final String MEASUREMENTS_NAME_CONTAIN_DOT = "IOTDB-0085";
  public static final String MEASUREMENTS_NAME_CONTAIN_DOT_MSG =
      "The measurement layer name cannot have multiple layers";

  public static final String MEASUREMENT_NAME_EQUALS_DEVICE = "IOTDB-0086";
  public static final String MEASUREMENT_NAME_EQUALS_DEVICE_MSG =
      "The entity name cannot be the same as an existing measurement";

  public static final String GET_DATA_PRIVILEGE_FAIL = "IOTDB-0087";
  public static final String GET_DATA_PRIVILEGE_FAIL_MSG =
      "Failed to get data management privilege";

  public static final String NO_PRI_INSERT_DATA = "IOTDB-0088";
  public static final String NO_PRI_INSERT_DATA_MSG = "No privilege to insert data";

  public static final String NO_PRI_ALTER_MEASUREMENT = "IOTDB-0089";
  public static final String NO_PRI_ALTER_MEASUREMENT_MSG = "No privilege to update measurement";

  public static final String NO_SUP_WORD = "IOTDB-0090";
  public static final String NO_SUP_WORD_MSG = "The tag or attribute cannot be as, null, or like";

  public static final String NO_SUP_ALL_DIGIT = "IOTDB-0091";
  public static final String NO_SUP_ALL_DIGIT_MSG =
      "The keys of tags or attributes cannot be pure numbers";

  public static final String NO_SUP_ALIAS_WORD = "IOTDB-0092";
  public static final String NO_SUP_ALIAS_WORD_MSG =
      "Measurement alias cannot be as, like, or pure numbers";

  public static final String TTL_OVER = "IOTDB-0093";
  public static final String TTL_OVER_MSG = "The TTL is too long";

  public static final String MEASUREMENT_ALREADY_EXIST = "IOTDB-0094";
  public static final String MEASUREMENT_ALREADY_EXIST_MSG =
      "Failed to create a measurement. Check whether the measurement has the same name as an existing measurement or alias";

  // Storage group table
  public static final String INSERT_GROUP_INFO_FAIL = "GROUP-0001";
  public static final String INSERT_GROUP_INFO_FAIL_MSG =
      "Failed to insert storage group information";

  public static final String DELETE_GROUP_INFO_FAIL = "GROUP-0002";
  public static final String DELETE_GROUP_INFO_FAIL_MSG =
      "Failed to delete storage group information";

  // Entity table
  public static final String DELETE_DEVICE_INFO_FAIL = "DEV-0001";
  public static final String DELETE_DEVICE_INFO_FAIL_MSG = "Failed to delete entity information";

  public static final String SET_DEVICE_INFO_FAIL = "DEV-0002";
  public static final String SET_DEVICE_INFO_FAIL_MSG = "Failed to insert entity information";

  // Measurement table
  public static final String DELETE_MEASUREMENT_INFO_FAIL = "MEASU-0001";
  public static final String DELETE_MEASUREMENT_INFO_FAIL_MSG =
      "Failed to delete measurement information";

  public static final String SET_MEASUREMENT_INFO_FAIL = "MEASU-0002";
  public static final String SET_MEASUREMENT_INFO_FAIL_MSG =
      "Failed to insert measurement information";

  public static final String GET_MSM_DES_FAIL = "MEASU-0003";
  public static final String GET_MSM_DES_FAIL_MSG = "Failed to get measurement information";

  // query table
  public static final String QUERY_EXIST = "QUERY-0001";
  public static final String QUERY_EXIST_MSG = "The query script name already exists";

  public static final String QUERY_NOT_EXIST = "QUERY-0002";
  public static final String QUERY_NOT_EXIST_MSG = "The query script name doesn't exist";

  // role table
  public static final String UPSERT_ROLE_INFO_FAIL = "ROLE-0001";
  public static final String UPSERT_ROLE_INFO_FAIL_MSG = "Failed to upsert role information";

  public static final String DELETE_ROLE_INFO_FAIL = "ROLE-0002";
  public static final String DELETE_ROLE_INFO_FAIL_MSG = "Failed to delete role information";

  public static final String GET_ROLE_INFO_FAIL = "ROLE-0003";
  public static final String GET_ROLE_INFO_FAIL_MSG = "Failed to get role information";

  // Parameter verify
  public static final String WRONG_PARAM = "PARAM-0001";
}
