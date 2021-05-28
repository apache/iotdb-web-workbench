package org.apache.iotdb.admin.service.impl;

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
    public List<String> getAllStorageGroups(Connection connection) {
        java.sql.Connection conn = getConnection(connection);
        String sql = "show storage group";
        List<String> users = customExecuteQuery(conn, sql);
        closeConnection(conn);
        return users;
    }


    @Override
    public void saveStorageGroup(Connection connection,String groupName) {
        java.sql.Connection conn = getConnection(connection);
        String sql = "set storage group to " + groupName;
        customExecute(conn,sql);
        closeConnection(conn);
    }


    @Override
    public void deleteStorageGroup(Connection connection, String groupName) {
        java.sql.Connection conn = getConnection(connection);
        String sql = "delete storage group " + groupName;
        customExecute(conn,sql);
        closeConnection(conn);
    }

    @Override
    public List<String> getDevicesByGroup(Connection connection, String groupName) {
        java.sql.Connection conn = getConnection(connection);
        String sql = "show devices " + groupName;
        List<String> devices = customExecuteQuery(conn, sql);
        closeConnection(conn);
        return devices;
    }

    @Override
    public List<String> getMeasurementsByDevice(Connection connection, String deviceName) {
        java.sql.Connection conn = getConnection(connection);
        String sql = "show child paths " + deviceName;
        List<String> measurements = customExecuteQuery(conn, sql);
        closeConnection(conn);
        return measurements;
    }

    @Override
    public List<String> getIotDBUserList(Connection connection ) {
        java.sql.Connection conn = getConnection(connection);
        String sql = "list user";
        List<String> users = customExecuteQuery(conn, sql);
        closeConnection(conn);
        return users;
    }

    @Override
    public List<String> getIotDBRoleList(Connection connection) {
        java.sql.Connection conn = getConnection(connection);
        String sql = "list role";
        List<String> roles = customExecuteQuery(conn, sql);
        closeConnection(conn);
        return roles;
    }

    @Override
    public IotDBUserVO getIotDBUser(Connection connection, String userName) {
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
    public void deleteIotDBUser(Connection connection, String userName) {
        java.sql.Connection conn = getConnection(connection);
        String sql = "drop user " + userName;
        customExecute(conn,sql);
        closeConnection(conn);
    }

    @Override
    public void deleteIotDBRole(Connection connection, String roleName) {
        java.sql.Connection conn = getConnection(connection);
        String sql = "drop role " + roleName;
        customExecute(conn,sql);
        closeConnection(conn);
    }


    public static java.sql.Connection getConnection(Connection connection) {
        String driver = "org.apache.iotdb.jdbc.IoTDBDriver";
        String url = "jdbc:iotdb://"+connection.getHost()+":"+connection.getPort()+"/";
        String username = connection.getUsername();
        String password = connection.getPassword();
        java.sql.Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private void closeConnection(java.sql.Connection conn) {
        try {
            if(conn != null){
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void customExecute(java.sql.Connection conn, String sql) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if( preparedStatement != null){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private List<String> customExecuteQuery(java.sql.Connection conn, String sql) {
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
            e.printStackTrace();
        } finally {
            if(resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(statement != null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private <T> List<T> customExecuteQuery(Class<T> clazz,java.sql.Connection conn, String sql) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } finally {
            if(resultSet != null){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(statement != null){
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
