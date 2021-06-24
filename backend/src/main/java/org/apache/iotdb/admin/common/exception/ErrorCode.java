package org.apache.iotdb.admin.common.exception;

/**
 * 错误码类
 */
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

    public static final String Token_Err = "USER-0009";
    public static final String Token_Err_MSG = "请登录或token失效";

    // iotDB相关
    public static final String INSERT_TS_FAIL = "IOTDB-0001";
    public static final String INSERT_TS_FAIL_MSG = "插入时间序列失败";

    public static final String DELETE_TS_FAIL = "IOTDB-0002";
    public static final String DELETE_TS_FAIL_MSG = "删除时间序列失败";

    public static final String DB_BOOL_WRONG = "IOTDB-0003";
    public static final String DB_BOOL_WRONG_MSG = "Boolean值输入错误,0为false，1为true";

    public static final String DB_DATATYPE_WRONG = "IOTDB-0004";
    public static final String DB_DATATYPE_WRONG_MSG = "TSDataType类型传入错误";

    public static final String GET_DBCONN_FAIL = "IOTDB-005";
    public static final String GET_DBCONN_FAIL_MSG = "获取数据库连接失败";

    public static final String GET_SESSION_FAIL = "IOTDB-0006";
    public static final String GET_SESSION_FAIL_MSG = "获取session失败";

    public static final String CLOSE_DBCONN_FAIL = "IOTDB-0007";
    public static final String CLOSE_DBCONN_FAIL_MSG = "关闭连接失败";

    public static final String SQL_EP = "IOTDB-0008";
    public static final String SQL_EP_MSG = "sql异常";

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
    public static final String SET_TTL_FAIL_MSG = "设置ttl失败";

    public static final String DEL_TTL_FAIL = "IOTDB-0015";
    public static final String DEL_TTL_FAIL_MSG = "删除ttl失败";

    public static final String GET_SQL_ONE_COLUMN_FAIL = "IOTDB-0016";
    public static final String GET_SQL_ONE_COLUMN_FAIL_MSG = "获取列表失败";

    public static final String GET_SQL_SET_FAIL = "IOTDB-0017";
    public static final String GET_SQL_SET_FAIL_MSG = "获取列表集合失败";

    // 存储组表相关
    public static final String INSERT_GROUP_INFO_FAIL = "GROUP-0001";
    public static final String INSERT_GROUP_INFO_FAIL_MSG = "插入存储组信息失败";

    public static final String DELETE_GROUP_INFO_FAIL = "GROUP-0002";
    public static final String DELETE_GROUP_INFO_FAIL_MSG = "删除存储组信息失败";

    // 设备表相关
    public static final String DELETE_DEVICE_INFO_FAIL = "DEV-0001";
    public static final String DELETE_DEVICE_INFO_FAIL_MSG = "删除设备信息失败";

    public static final String SET_DEVICE_INFO_FAIL = "DEV-0002";
    public static final String SET_DEVICE_INFO_FAIL_MSG = "插入设备信息失败";

    // 测点表相关
    public static final String DELETE_MEASUREMENT_INFO_FAIL = "MEASU-0001";
    public static final String DELETE_MEASUREMENT_INFO_FAIL_MSG = "删除测点信息失败";

    public static final String SET_MEASUREMENT_INFO_FAIL = "MEASU-0002";
    public static final String SET_MEASUREMENT_INFO_FAIL_MSG = "插入测点信息失败";

    // 参数校验相关
    public static final String WRONG_PARAM = "PARAM-0001";


}
