package org.apache.iotdb.admin.service.impl;

import com.alibaba.fastjson.JSON;
import net.bytebuddy.utility.JavaConstant;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.dto.IotDBRole;
import org.apache.iotdb.admin.model.dto.IotDBUser;
import org.apache.iotdb.admin.model.dto.Timeseries;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.IotDBUserVO;
import org.apache.iotdb.admin.model.vo.RoleWithPrivileges;
import org.apache.iotdb.admin.model.vo.SqlResultVO;
import org.apache.iotdb.admin.service.IotDBService;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.tsfile.encoding.encoder.TSEncodingBuilder;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.springframework.stereotype.Service;

import javax.lang.model.element.TypeElement;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @anthor fyx 2021/5/27
 */
@Service
public class IotDBServiceImpl implements IotDBService {

//    private static SessionPool sessionPool;
//    private static String host;
//    private static Integer port;
//    private static String username;
//    private static String password;

    @Override
    public List<String> getAllStorageGroups(Connection connection) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String sql = "show storage group";
        List<String> users = customExecuteQuery(conn, sql);
        closeConnection(conn);
        return users;
    }


    @Override
    public void saveStorageGroup(Connection connection, String groupName) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String sql = "set storage group to " + groupName;
        customExecute(conn, sql);
        closeConnection(conn);
    }


    @Override
    public void deleteStorageGroup(Connection connection, String groupName) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String sql = "delete storage group " + groupName;
        customExecute(conn, sql);
        closeConnection(conn);
    }

    @Override
    public List<String> getDevicesByGroup(Connection connection, String groupName) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String sql = "show devices " + groupName;
        List<String> devices = customExecuteQuery(conn, sql);
        closeConnection(conn);
        return devices;
    }

    @Override
    public List<String> getMeasurementsByDevice(Connection connection, String deviceName) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String sql = "show child paths " + deviceName;
        List<String> measurements = customExecuteQuery(conn, sql);
        closeConnection(conn);
        return measurements;
    }

    @Override
    public List<String> getIotDBUserList(Connection connection) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String sql = "list user";
        List<String> users = customExecuteQuery(conn, sql);
        closeConnection(conn);
        return users;
    }

    @Override
    public List<String> getIotDBRoleList(Connection connection) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String sql = "list role";
        List<String> roles = customExecuteQuery(conn, sql);
        closeConnection(conn);
        return roles;
    }

    @Override
    public IotDBUserVO getIotDBUser(Connection connection, String userName) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        IotDBUserVO iotDBUserVO = new IotDBUserVO();
        iotDBUserVO.setUserName(userName);
        String sql = "list user privileges " + userName;
        List<RoleWithPrivileges> roleWithPrivileges = customExecuteQuery(RoleWithPrivileges.class, conn, sql);
        closeConnection(conn);
        iotDBUserVO.setRoleWithPrivileges(roleWithPrivileges);
        return iotDBUserVO;
    }

    @Override
    public void deleteIotDBUser(Connection connection, String userName) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String sql = "drop user " + userName;
        customExecute(conn, sql);
        closeConnection(conn);
    }

    @Override
    public void deleteIotDBRole(Connection connection, String roleName) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String sql = "drop role " + roleName;
        customExecute(conn, sql);
        closeConnection(conn);
    }

    @Override
    public void setIotDBUser(Connection connection, IotDBUser iotDBUser) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String userName = iotDBUser.getUserName();
        String password = iotDBUser.getPassword();
        String sql = "create user " + userName + " '" + password + "'";
        customExecute(conn, sql);
        // 用户角色
        for (String role : iotDBUser.getRoles()) {
            sql = "grant " + role + " to " + userName;
            customExecute(conn, sql);
        }
        // 用户授权
        List<String> privileges = iotDBUser.getPrivileges();
        for (String privilege : privileges) {
            sql = handlerPrivilegeStrToSql(privilege, userName, null);
            if (sql != null) {
                customExecute(conn, sql);
            }
        }
        closeConnection(conn);
    }

    @Override
    public void setIotDBRole(Connection connection, IotDBRole iotDBRole) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String roleName = iotDBRole.getRoleName();
        String sql = "create role " + roleName;
        customExecute(conn, sql);
        List<String> privileges = iotDBRole.getPrivileges();
        for (String privilege : privileges) {
            sql = handlerPrivilegeStrToSql(privilege, null, roleName);
            if (sql != null) {
                customExecute(conn, sql);
            }
        }
        closeConnection(conn);
    }

    @Override
    public SqlResultVO query(Connection connection, String sql) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        SqlResultVO sqlResultVO = sqlQuery(conn, sql);
        closeConnection(conn);
        return sqlResultVO;
    }

    @Override
    public void insertTimeseries(Connection connection, String deviceName, Timeseries timeseries) throws BaseException {
        SessionPool session = getSession(connection);
        try {
            List<TSDataType> types = handleTypeStr(timeseries.getTypes());
            List<Object> values = handleValueStr(timeseries.getValues(),types);
            session.insertRecord(deviceName,timeseries.getTime(),timeseries.getMeasurements(),types,values);
        } catch (IoTDBConnectionException e) {
            throw new BaseException(3001, e.getMessage());
        } catch (StatementExecutionException e) {
            throw new BaseException(3001, e.getMessage());
        }finally {
            if(session != null){
                session.close();
            }
        }

    }

    private List<Object> handleValueStr(List<String> values, List<TSDataType> types) throws BaseException {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < types.size(); i++) {
            TSDataType type = types.get(i);
            if(type == TSDataType.BOOLEAN){
                Integer booleanNum = Integer.valueOf(values.get(i));
                Boolean flag = null;
                if(booleanNum == 0){
                    flag = false;
                }
                if(booleanNum == 1){
                    flag = true;
                }
                if(flag != null){
                    list.add(flag);
                    continue;
                }
                throw new BaseException(3003, "Boolean值输入错误,0为false，1为true");
            }
            if(type == TSDataType.INT32 || type == TSDataType.INT64){
                Integer intNum = Integer.valueOf(values.get(i));
                list.add(intNum);
                continue;
            }
            if(type == TSDataType.FLOAT){
                Float floatNum = Float.valueOf(values.get(i));
                list.add(floatNum);
                continue;
            }
            if(type == TSDataType.DOUBLE){
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
            switch (type){
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
                    throw new BaseException(3002,"TSDataType类型传入错误");
            }
            list.add(tsDataType);
        }
        return list;
    }


    public static java.sql.Connection getConnection(Connection connection) throws BaseException {
        String driver = "org.apache.iotdb.jdbc.IoTDBDriver";
        String url = "jdbc:iotdb://" + connection.getHost() + ":" + connection.getPort() + "/";
        String username = connection.getUsername();
        String password = connection.getPassword();
        java.sql.Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new BaseException(3001, e.getMessage());
        } catch (SQLException e) {
            throw new BaseException(3001, e.getMessage());
        }
        return conn;
    }

        public static SessionPool getSession(Connection connection) throws BaseException {
            String host = connection.getHost();
            Integer port = connection.getPort();
            String username = connection.getUsername();
            String password = connection.getPassword();
            SessionPool sessionPool = null;
            try {
                sessionPool = new SessionPool(host,port,username,password,3);
            } catch (Exception e) {
                throw new BaseException(3001, e.getMessage());
            }
            return sessionPool;
        }
//    public static SessionPool getSession(Connection connection) throws BaseException {
//        if(sessionPool == null){
//            host = connection.getHost();
//            port = connection.getPort();
//            username = connection.getUsername();
//            password = connection.getPassword();
//            sessionPool = new SessionPool(host,port,username,password,3);
//            return sessionPool;
//        }
//        if(host == connection.getHost() && port.equals(connection.getPort()) && username == connection.getUsername() && password == connection.getPassword()){
//            return sessionPool;
//        }
//        sessionPool.close();
//        host = connection.getHost();
//        port = connection.getPort();
//        username = connection.getUsername();
//        password = connection.getPassword();
//        sessionPool = new SessionPool(host,port,username,password,3);
//        return sessionPool;
//    }

    private void closeConnection(java.sql.Connection conn) throws BaseException {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new BaseException(3001, e.getMessage());
        }
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

    private void customExecute(java.sql.Connection conn, String sql) throws BaseException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new BaseException(3001, e.getMessage());
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    throw new BaseException(3001, e.getMessage());
                }
            }
            closeConnection(conn);
        }
    }

    private List<String> customExecuteQuery(java.sql.Connection conn, String sql) throws BaseException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.prepareStatement(sql);
            resultSet = statement.executeQuery();
            int columnCount = resultSet.getMetaData().getColumnCount();
            List<String> list = new ArrayList<>();
            while (resultSet.next()) {
                for (int i = 0; i < columnCount; i++) {
                    list.add(resultSet.getString(i + 1));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new BaseException(3001, e.getMessage());
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    throw new BaseException(3001, e.getMessage());
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new BaseException(3001, e.getMessage());
                }
            }
            closeConnection(conn);
        }
    }

    private <T> List<T> customExecuteQuery(Class<T> clazz, java.sql.Connection conn, String sql) throws BaseException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.prepareStatement(sql);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<T> list = new ArrayList<>();
            while (resultSet.next()) {
                T t = clazz.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    Object value = resultSet.getObject(i + 1);
                    String columnName = metaData.getColumnLabel(i + 1);
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(t, value);
                }
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            throw new BaseException(3001, e.getMessage());
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    throw new BaseException(3001, e.getMessage());
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new BaseException(3001, e.getMessage());
                }
            }
            closeConnection(conn);
        }
    }

    private SqlResultVO sqlQuery(java.sql.Connection conn, String sql) throws BaseException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.prepareStatement(sql);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            SqlResultVO sqlResultVO = new SqlResultVO();
            List<String> metaDataList = new ArrayList<>();
            for (int i = 0; i < columnCount; i++) {
                metaDataList.add(metaData.getColumnLabel(i + 1));
            }
            sqlResultVO.setMetaDataList(metaDataList);
            List<List<String>> valuelist = new ArrayList<>();
            while (resultSet.next()) {
                List<String> strList = new ArrayList<>();
                for (int i = 0; i < columnCount; i++) {
                    strList.add(resultSet.getString(i + 1));
                }
                valuelist.add(strList);
            }
            sqlResultVO.setValueList(valuelist);
            return sqlResultVO;
        } catch (SQLException e) {
            throw new BaseException(3001, e.getMessage());
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    throw new BaseException(3001, e.getMessage());
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new BaseException(3001, e.getMessage());
                }
            }
            closeConnection(conn);
        }
    }
}