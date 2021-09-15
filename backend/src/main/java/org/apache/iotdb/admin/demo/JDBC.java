package org.apache.iotdb.admin.demo;

import java.sql.*;

/** JDBC demo代码 */
public class JDBC {
  public static void main(String[] args) throws SQLException {
    Connection connection = getConnection();
    if (connection == null) {
      System.out.println("get connection defeat");
      return;
    }
    Statement statement = connection.createStatement();
    // 创建存储组
    try {
      statement.execute("SET STORAGE GROUP TO root.demo");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    // 展示存储组
    statement.execute("SHOW STORAGE GROUP");
    outputResult(statement.getResultSet());
    // 创建时间序列
    // 不同的数据类型有不同的编码方式. 这里以INT32作为例子
    try {
      statement.execute("CREATE TIMESERIES root.demo.s0 WITH DATATYPE=INT32,ENCODING=RLE;");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    // 展示时间序列
    statement.execute("SHOW TIMESERIES root.demo");
    outputResult(statement.getResultSet());
    // 展示设备
    statement.execute("SHOW DEVICES");
    outputResult(statement.getResultSet());
    // 计算时间序列个数
    statement.execute("COUNT TIMESERIES root");
    outputResult(statement.getResultSet());
    // 计算给定级别的nodes个数
    statement.execute("COUNT NODES root LEVEL=3");
    outputResult(statement.getResultSet());
    // 计算时间序列以级别3分组的个数
    statement.execute("COUNT TIMESERIES root GROUP BY LEVEL=3");
    outputResult(statement.getResultSet());

    // 批量执行插入语句
    statement.addBatch("insert into root.demo(timestamp,s0) values(1,1);");
    statement.addBatch("insert into root.demo(timestamp,s0) values(1,1);");
    statement.addBatch("insert into root.demo(timestamp,s0) values(2,15);");
    statement.addBatch("insert into root.demo(timestamp,s0) values(2,17);");
    statement.addBatch("insert into root.demo(timestamp,s0) values(4,12);");
    statement.executeBatch();
    statement.clearBatch();

    // 查询语句
    String sql = "select * from root.demo";
    ResultSet resultSet = statement.executeQuery(sql);
    System.out.println("sql: " + sql);
    outputResult(resultSet);

    // 精确的查询语句
    sql = "select s0 from root.demo where time = 4;";
    resultSet = statement.executeQuery(sql);
    System.out.println("sql: " + sql);
    outputResult(resultSet);

    // 时间范围查询
    sql = "select s0 from root.demo where time >= 2 and time < 5;";
    resultSet = statement.executeQuery(sql);
    System.out.println("sql: " + sql);
    outputResult(resultSet);

    // 聚合查询
    sql = "select count(s0) from root.demo;";
    resultSet = statement.executeQuery(sql);
    System.out.println("sql: " + sql);
    outputResult(resultSet);

    // 删除时间序列
    statement.execute("delete timeseries root.demo.s0");

    // 关闭连接
    statement.close();
    connection.close();
  }

  public static Connection getConnection() {
    // JDBC driver name and database URL
    String driver = "org.apache.iotdb.jdbc.IoTDBDriver";
    String url = "jdbc:iotdb://127.0.0.1:6667/";

    // Database credentials
    String username = "root";
    String password = "root";

    Connection connection = null;
    try {
      Class.forName(driver);
      connection = DriverManager.getConnection(url, username, password);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return connection;
  }

  /** This is an example of outputting the results in the ResultSet */
  private static void outputResult(ResultSet resultSet) throws SQLException {
    if (resultSet != null) {
      System.out.println("--------------------------");
      final ResultSetMetaData metaData = resultSet.getMetaData();
      final int columnCount = metaData.getColumnCount();
      for (int i = 0; i < columnCount; i++) {
        System.out.print(metaData.getColumnLabel(i + 1) + " ");
      }
      System.out.println();
      while (resultSet.next()) {
        for (int i = 1; ; i++) {
          System.out.print(resultSet.getString(i));
          if (i < columnCount) {
            System.out.print(", ");
          } else {
            System.out.println();
            break;
          }
        }
      }
      System.out.println("--------------------------\n");
    }
  }
}
