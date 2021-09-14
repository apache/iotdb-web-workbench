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

package org.apache.iotdb.admin.demo;

import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.session.SessionDataSet;
import org.apache.iotdb.tsfile.file.metadata.enums.CompressionType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding;
import org.apache.iotdb.tsfile.read.common.Field;
import org.apache.iotdb.tsfile.read.common.RowRecord;

import java.util.ArrayList;
import java.util.List;

/** 原生api demo代码 */
public class NativeAPI {

  public static void main(String[] args)
      throws IoTDBConnectionException, StatementExecutionException {
    // 建立连接
    Session session = new Session("localhost", 6667, "root", "root");
    session.open();
    //        SessionPool sessionPool = new SessionPool("localhost", 6667,"root","root",3);
    //        sessionPool.insertRecord();
    // 创建存储组
    session.setStorageGroup("root.fyx");
    // 创建时间序列
    /**
     * path:层级路径 TSDataType数据类型:六种 支持编码方式 boolean PLAIN,RLE int32 PLAIN,RLE,TS_2DIFF,GORILLA int64
     * PLAIN,RLE,TS_2DIFF,GORILLA float PLAIN,RLE,TS_2DIFF,GORILLA double PLAIN,RLE,TS_2DIFF,GORILLA
     * text(字符串) PLAIN
     *
     * <p>TSEncoding编码方式： PLAIN编码：不编码 压缩效率高 空间存储效率低 二阶差分编码(TS_2DIFF):适合单调序列数据 不适合编码波动较大的数据
     * 游程编码(RLE):比较适合整数值连续出现的序列 GORILLA编码：无损编码，它比较适合编码前后值比较接近的数值序列 定频数据编码(REGULAR):仅适用于整型 允许数据缺失
     *
     * <p>CompressionType压缩方式： UMCOMPRESSED SNAPPY LZ4 GZIP SDT PAA PLA
     */
    session.createTimeseries(
        "root.fyx.cq.dev.temp",
        TSDataType.FLOAT,
        TSEncoding.RLE,
        CompressionType.SNAPPY,
        null,
        null,
        null,
        null);
    // 创建多个时间序列
    //        session.createMultiTimeseries();
    // 插入数据
    List<String> measurements = new ArrayList<>();
    List<TSDataType> types = new ArrayList<>();
    measurements.add("temp");
    types.add(TSDataType.FLOAT);
    for (long time = 0; time < 10; time++) {
      List<Object> values = new ArrayList<>();
      values.add(time * 6.6f);
      // 如果不加type,服务器会做类型推断，会有额外耗时
      session.insertRecord("root.fyx.cq.dev", time, measurements, types, values);
    }
    // 执行非查询语句的sql
    session.executeNonQueryStatement(
        "insert into root.fyx.cq.dev(timestamp,temp) values(now(),66.66)");
    // 执行查询语句的sql
    SessionDataSet sessionDataSet = session.executeQueryStatement("select * from root.fyx.cq.dev");
    // 数据处理
    int fetchSize = sessionDataSet.getFetchSize();
    List<String> columnNames = sessionDataSet.getColumnNames();
    List<TSDataType> columnTypes = sessionDataSet.getColumnTypes();
    System.out.println(columnNames);
    System.out.println(columnTypes);
    if (fetchSize > 0) {
      while (sessionDataSet.hasNext()) {
        RowRecord next = sessionDataSet.next();
        List<Field> fields = next.getFields();
        // 查询结果第一个为时间戳
        long timestamp = next.getTimestamp();
        System.out.print(timestamp + "\t");
        for (int i = 0; i < fields.size(); i++) {
          Field field = fields.get(i);
          // 这里的需要按照类型获取
          System.out.print(field.getObjectValue(field.getDataType()));
        }
        System.out.println();
      }
    }
    sessionDataSet.closeOperationHandle();
    // 删除时间序列
    session.deleteTimeseries("root.fyx.cq.dev.temp");
    // 删除存储组
    session.deleteStorageGroup("root.fyx");
    session.close();
  }
}
