package org.apache.iotdb.admin.service.impl;

import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.service.IotDBService;
import org.apache.iotdb.session.pool.SessionPool;
import org.springframework.stereotype.Service;

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
//        sessionPool = getPool(connection);
        java.sql.Connection conn = getConnection(connection);
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.prepareStatement("show storage group");
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

    @Override
    public void saveStorageGroup(Connection connection,String groupName) {
        java.sql.Connection conn = getConnection(connection);
        String sql = "set storage group to " + groupName;
        customExecute(conn,sql);
    }


    @Override
    public void deleteStorageGroup(Connection connection, String groupName) {
        java.sql.Connection conn = getConnection(connection);
        String sql = "delete storage group " + groupName;
        customExecute(conn,sql);
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
}
