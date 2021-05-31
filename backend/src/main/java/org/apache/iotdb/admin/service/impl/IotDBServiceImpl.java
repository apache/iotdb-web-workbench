package org.apache.iotdb.admin.service.impl;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.dto.IotDBRole;
import org.apache.iotdb.admin.model.dto.IotDBUser;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.IotDBUserVO;
import org.apache.iotdb.admin.model.vo.RoleWithPrivileges;
import org.apache.iotdb.admin.service.IotDBService;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @anthor fyx 2021/5/27
 */
@Service
public class IotDBServiceImpl implements IotDBService {

    @Override
    public List<String> getAllStorageGroups(Connection connection) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String sql = "show storage group";
        List<String> users = customExecuteQuery(conn, sql);
        closeConnection(conn);
        return users;
    }


    @Override
    public void saveStorageGroup(Connection connection,String groupName) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String sql = "set storage group to " + groupName;
        customExecute(conn,sql);
        closeConnection(conn);
    }


    @Override
    public void deleteStorageGroup(Connection connection, String groupName) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String sql = "delete storage group " + groupName;
        customExecute(conn,sql);
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
    public List<String> getIotDBUserList(Connection connection ) throws BaseException {
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
        customExecute(conn,sql);
        closeConnection(conn);
    }

    @Override
    public void deleteIotDBRole(Connection connection, String roleName) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String sql = "drop role " + roleName;
        customExecute(conn,sql);
        closeConnection(conn);
    }

    @Override
    public void setIotDBUser(Connection connection, IotDBUser iotDBUser) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String userName = iotDBUser.getUserName();
        String password = iotDBUser.getPassword();
        String sql = "create user " + userName + " '" + password + "'";
        customExecute(conn,sql);
        //用户角色
        for (String role : iotDBUser.getRoles()) {
            sql = "grant " + role + " to " + userName;
            customExecute(conn,sql);
        }
        //用户授权
        List<String> privileges = iotDBUser.getPrivileges();
        for (String privilege : privileges) {
            sql = handlerPrivilegeStrToSql(privilege, userName,null);
            if(sql != null){
                customExecute(conn,sql);
            }
        }
        closeConnection(conn);
    }

    @Override
    public void setIotDBRole(Connection connection, IotDBRole iotDBRole) throws BaseException {
        java.sql.Connection conn = getConnection(connection);
        String roleName = iotDBRole.getRoleName();
        String sql = "create role " + roleName;
        customExecute(conn,sql);
        List<String> privileges = iotDBRole.getPrivileges();
        for (String privilege : privileges) {
            sql = handlerPrivilegeStrToSql(privilege, null,roleName);
            if(sql != null){
                customExecute(conn,sql);
            }
        }
        closeConnection(conn);
    }


    public static java.sql.Connection getConnection(Connection connection) throws BaseException {
        String driver = "org.apache.iotdb.jdbc.IoTDBDriver";
        String url = "jdbc:iotdb://"+connection.getHost()+":"+connection.getPort()+"/";
        String username = connection.getUsername();
        String password = connection.getPassword();
        java.sql.Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new BaseException(3001,e.getMessage());
        } catch (SQLException e) {
            throw new BaseException(3001,e.getMessage());
        }
        return conn;
    }

    private void closeConnection(java.sql.Connection conn) throws BaseException {
        try {
            if(conn != null){
                conn.close();
            }
        } catch (SQLException e) {
            throw new BaseException(3001,e.getMessage());
        }
    }

    private String handlerPrivilegeStrToSql(String privilege,String userName,String roleName) {
        int i = privilege.indexOf(":");
        String path = privilege.substring(0,i).trim();
        String[] privileges = privilege.substring(i+1).trim().split(" ");
        int len = privileges.length;
        if(len == 0){
            return null;
        }
        StringBuilder str = new StringBuilder();
        if(userName != null){
            str.append("grant user " + userName + " privileges ");
        }else{
            str.append("grant role " + roleName + " privileges ");
        }
        for (int j = 0; i < len - 1; j++) {
            str.append("'"+privileges[j]+"',");
        }
        str.append("'"+privileges[len - 1]+"' on " + path);
        return str.toString();
    }

    private void customExecute(java.sql.Connection conn, String sql) throws BaseException {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new BaseException(3001,e.getMessage());
        } finally {
            if( preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    throw new BaseException(3001,e.getMessage());
                }
            }
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
            while (resultSet.next()){
                for (int i = 0; i < columnCount; i++) {
                    list.add(resultSet.getString(i + 1));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new BaseException(3001,e.getMessage());
        } finally {
            if(resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    throw new BaseException(3001,e.getMessage());
                }
            }
            if(statement != null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new BaseException(3001,e.getMessage());
                }
            }
        }
    }

    private <T> List<T> customExecuteQuery(Class<T> clazz,java.sql.Connection conn, String sql) throws BaseException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.prepareStatement(sql);
            resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<T> list = new ArrayList<>();
            while (resultSet.next()){
                T t = clazz.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    Object value = resultSet.getObject(i + 1);
                    String columnName = metaData.getColumnLabel(i + 1);
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(t,value);
                }
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            throw new BaseException(3001,e.getMessage());
        } finally {
            if(resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    throw new BaseException(3001,e.getMessage());
                }
            }
            if(statement != null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    throw new BaseException(3001,e.getMessage());
                }
            }
        }
    }
}
