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
import org.apache.iotdb.admin.model.dto.QueryInfoDTO;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.*;
import org.apache.iotdb.admin.service.ConnectionService;
import org.apache.iotdb.admin.service.IotDBService;
import org.apache.iotdb.admin.service.MetricsService;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionDataSetWrapper;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.tsfile.read.common.RowRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetricsServiceImpl implements MetricsService {

  @Autowired ConnectionService connectionService;
  @Autowired IotDBService iotDBService;

  private static final Logger logger = LoggerFactory.getLogger(IotDBServiceImpl.class);

  public JVMMetricsListDataVO getCurrentThreadsCount(Connection connection) throws BaseException {
    String name = "JVM当前线程数";
    String metricType = "线程";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 1;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.threads.daemon\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.threads.live\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String s1 = values.get(2);
    String s2 = values.get(1);
    s1 = s1.substring(0, s1.indexOf('.'));
    s2 = s2.substring(0, s2.indexOf('.'));
    int totalThreadCount = Integer.parseInt(s1);
    int demoThreadCount = Integer.parseInt(s2);
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    String latestResult =
        "前台:"
            + (totalThreadCount - demoThreadCount)
            + "个，后台:"
            + demoThreadCount
            + "个，线程总数:"
            + totalThreadCount
            + "个";
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getCurrentDaemonThreadsCount(Connection connection)
      throws BaseException {
    String name = "当前daemon线程数";
    String metricType = "线程";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.threads.daemon\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String s1 = values.get(1);
    s1 = s1.substring(0, s1.indexOf('.'));
    int daemonThreadCount = Integer.parseInt(s1);
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    String latestResult = daemonThreadCount + "个";
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getPeakThreadsCount(Connection connection) throws BaseException {
    String name = "峰值线程数";
    String metricType = "线程";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.threads.peak\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String s1 = values.get(1);
    s1 = s1.substring(0, s1.indexOf('.'));
    int daemonThreadCount = Integer.parseInt(s1);
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    String latestResult = daemonThreadCount + "个";
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getVariableThreadsCount(Connection connection) throws BaseException {
    String name = "处于各种状态的线程数";
    String metricType = "线程";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.threads.states\".\"state=new\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.threads.states\".\"state=waiting\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.threads.states\".\"state=runnable\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.threads.states\".\"state=blocked\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.threads.states\".\"state=timed-waiting\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.threads.states\".\"state=terminated\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String s1 = values.get(1);
    String s2 = values.get(2);
    String s3 = values.get(3);
    String s4 = values.get(4);
    String s5 = values.get(5);
    String s6 = values.get(6);
    s1 = s1.substring(0, s1.indexOf('.'));
    s2 = s2.substring(0, s2.indexOf('.'));
    s3 = s3.substring(0, s3.indexOf('.'));
    s4 = s4.substring(0, s4.indexOf('.'));
    s5 = s5.substring(0, s5.indexOf('.'));
    s6 = s6.substring(0, s6.indexOf('.'));
    int newThreadCount = Integer.parseInt(s1);
    int waitingThreadCount = Integer.parseInt(s2);
    int runnableThreadCount = Integer.parseInt(s3);
    int blockedThreadCount = Integer.parseInt(s4);
    int timedWaitingThreadCount = Integer.parseInt(s5);
    int terminatedThreadCount = Integer.parseInt(s6);
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    String latestResult =
        "新建("
            + newThreadCount
            + ")、"
            + "可运行("
            + waitingThreadCount
            + ")、"
            + "运行("
            + runnableThreadCount
            + ")、"
            + "阻塞("
            + blockedThreadCount
            + ")、"
            + "休眠("
            + timedWaitingThreadCount
            + ")、"
            + "死亡("
            + terminatedThreadCount
            + ")";
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getYGCHappendCountAndCostTime(Connection connection)
      throws BaseException {
    String name = "YGC发生次数及总耗时";
    String metricType = "垃圾回收";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    SessionPool sessionPool = getSessionPool(connection);
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String countSQL =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.gc.pause_count\".\"action=end of minor GC\".\"cause=Allocation Failure\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.gc.pause_count\".\"action=end of minor GC\".\"cause=Metadata GC Threshold\" "
            + "order by time desc limit 1";
    String timeSQL =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.gc.pause_total\".\"action=end of minor GC\".\"cause=Allocation Failure\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.gc.pause_total\".\"action=end of minor GC\".\"cause=Metadata GC Threshold\" "
            + "order by time desc limit 1";
    List<String> countValues = executeQueryOneLine(sessionPool, countSQL);
    List<String> timeValues = executeQueryOneLine(sessionPool, timeSQL);
    long lastestTimeStamp = Long.parseLong(countValues.get(0));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    Integer detailAvailable = 2;
    String s1 = countValues.get(1);
    String s2 = countValues.get(2);
    s1 = s1.substring(0, s1.indexOf('.'));
    s2 = s2.substring(0, s2.indexOf('.'));
    int count = Integer.parseInt(s1) + Integer.parseInt(s2);
    double time =
        (Double.parseDouble(timeValues.get(1)) + Double.parseDouble(timeValues.get(2))) / 1000;
    String latestResult = count + "次 " + time + "s";
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getYGCMaxCostTimeAndReason(Connection connection)
      throws BaseException {
    String name = "YGC最大耗时及原因";
    String metricType = "垃圾回收";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    SessionPool sessionPool = getSessionPool(connection);
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String timeSQL =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.gc.pause_max\".\"action=end of minor GC\".\"cause=Metadata GC Threshold\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.gc.pause_max\".\"action=end of minor GC\".\"cause=Allocation Failure\" "
            + "order by time desc limit 1";
    List<String> timeValues = executeQueryOneLine(sessionPool, timeSQL);
    long lastestTimeStamp = Long.parseLong(timeValues.get(0));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    Integer detailAvailable = 2;
    String latestResult =
        Double.parseDouble(timeValues.get(1)) / 1000
            + "s(Metadata GC Threshold)、"
            + Double.parseDouble(timeValues.get(2)) / 1000
            + "s(Allocation Failure)";
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getFGCHappendCountAndCostTime(Connection connection)
      throws BaseException {
    String name = "FGC发生次数及总耗时";
    String metricType = "垃圾回收";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    SessionPool sessionPool = getSessionPool(connection);
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String countSQL =
        "select * from "
            +
            //            "root._metric.\"127.0.0.1:8086\".\"jvm.gc.pause_count\".\"action=end of
            // major GC\".\"cause=Allocation Failure\", " +
            "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.gc.pause_count\".\"action=end of major GC\".\"cause=Metadata GC Threshold\" "
            +
            //            "root._metric.\"127.0.0.1:8086\".\"jvm.gc.pause_count\".\"action=end of
            // major GC\".\"cause=Ergonomics\" " +
            "order by time desc limit 1";
    String timeSQL =
        "select * from "
            +
            //            "root._metric.\"127.0.0.1:8086\".\"jvm.gc.pause_total\".\"action=end of
            // major GC\".\"cause=Allocation Failure\", " +
            "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.gc.pause_total\".\"action=end of major GC\".\"cause=Metadata GC Threshold\" "
            +
            //            "root._metric.\"127.0.0.1:8086\".\"jvm.gc.pause_total\".\"action=end of
            // major GC\".\"cause=Ergonomics\" " +
            "order by time desc limit 1";
    List<String> countValues = executeQueryOneLine(sessionPool, countSQL);
    List<String> timeValues = executeQueryOneLine(sessionPool, timeSQL);
    long lastestTimeStamp = Long.parseLong(countValues.get(0));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    Integer detailAvailable = 2;
    // TODO: IOTDB BUG 等待修复
    String s1 = countValues.get(1).equals("null") ? "0.0" : countValues.get(1);
    //    String s2 = countValues.get(2).equals("null") ? "0.0" : countValues.get(2);
    //    String s3 = countValues.get(3).equals("null") ? "0.0" : countValues.get(3);

    s1 = s1.substring(0, s1.indexOf('.'));
    //    s2 = s2.substring(0, s2.indexOf('.'));
    //    s3 = s3.substring(0, s3.indexOf('.'));
    //    int count = Integer.parseInt(s1) + Integer.parseInt(s2) + Integer.parseInt(s3);
    int count = Integer.parseInt(s1);
    // TODO: IOTDB BUG 等待修复
    double d1 = timeValues.get(1).equals("null") ? 0.0 : Double.parseDouble(timeValues.get(1));
    //    double d2 = timeValues.get(2).equals("null")? 0.0 : Double.parseDouble(timeValues.get(2));
    //    double d3 = timeValues.get(3).equals("null")? 0.0 : Double.parseDouble(timeValues.get(3));
    //    double time = d1 + d2 + d3;
    double time = (d1) / 1000;
    String latestResult = count + "次 " + time + "s";
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getFGCMaxCostTimeAndReason(Connection connection)
      throws BaseException {
    String name = "FGC最大耗时及原因";
    String metricType = "垃圾回收";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    SessionPool sessionPool = getSessionPool(connection);
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String timeSQL =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.gc.pause_max\".\"action=end of major GC\".\"cause=Metadata GC Threshold\" "
            +
            //            "root._metric.\"127.0.0.1:8086\".\"jvm.gc.pause_max\".\"action=end of
            // major GC\".\"cause=Allocation Failure\", " +
            //            "root._metric.\"127.0.0.1:8086\".\"jvm.gc.pause_max\".\"action=end of
            // major GC\".\"cause=Ergonomics\" " +
            "order by time desc limit 1";
    List<String> timeValues = executeQueryOneLine(sessionPool, timeSQL);
    long lastestTimeStamp = Long.parseLong(timeValues.get(0));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    Integer detailAvailable = 2;
    // TODO: IOTDB BUG 等待修复
    String s1 = timeValues.get(1).equals("null") ? "0.0" : timeValues.get(1);
    //    String s2 = timeValues.get(2).equals("null")? "0.0" : timeValues.get(2);
    //    String s3 = timeValues.get(3).equals("null")? "0.0" : timeValues.get(3);
    //    String latestResult = timeValues.get(1)+"s(Metadata GC
    // Threshold)、"+timeValues.get(2)+"s(Allocation Failure)、"+timeValues.get(3)+"s(Ergonomics)";
    String latestResult = Double.parseDouble(timeValues.get(1)) / 1000 + "s(Metadata GC Threshold)";
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getGCCPUoverhead(Connection connection) throws BaseException {
    String name = "GC消耗CPU的比例";
    String metricType = "垃圾回收";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.gc.overhead\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    double percent = Double.parseDouble(values.get(1));
    BigDecimal b = new BigDecimal(percent);
    double percent1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    String latestResult = percent1 + "%";
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getGCPromoted(Connection connection) throws BaseException {
    String name = "从GC之前到GC之后老年代内存池大小正增长的累计";
    String metricType = "垃圾回收";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.gc.memory.promoted\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String latestResult = getNetFileSizeDescription((long) (Double.parseDouble(values.get(1))));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getMajorMemoryMaxValueEver(Connection connection)
      throws BaseException {
    String name = "老年代内存的历史最大值";
    String metricType = "垃圾回收";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.gc.max.data.size\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String latestResult = getNetFileSizeDescription((long) (Double.parseDouble(values.get(1))));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getMajorMemorySizeAfterGC(Connection connection)
      throws BaseException {
    String name = "GC之后老年代内存的大小";
    String metricType = "垃圾回收";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.gc.live.data.size\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String count = getNetFileSizeDescription((long) (Double.parseDouble(values.get(1))));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    String latestResult = count;
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getMinorMemorySizeAddedBetweentwoGC(Connection connection)
      throws BaseException {
    String name = "在一个GC之后到下一个GC之前年轻代增加的内存";
    String metricType = "垃圾回收";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.gc.memory.allocated\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String count = getNetFileSizeDescription((long) (Double.parseDouble(values.get(1))));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    String latestResult = count;
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getBufferUsed(Connection connection) throws BaseException {
    String name = "已经使用的缓冲区大小";
    String metricType = "内存";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.buffer.memory.used\".\"id=mapped\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.buffer.memory.used\".\"id=direct\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String count =
        getNetFileSizeDescription(
            (long) (Double.parseDouble(values.get(1)) + Double.parseDouble(values.get(2))));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    String latestResult = count;
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getMaxBuffer(Connection connection) throws BaseException {
    String name = "最大缓冲区大小";
    String metricType = "内存";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.buffer.total.capacity\".\"id=mapped\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.buffer.total.capacity\".\"id=direct\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String count =
        getNetFileSizeDescription(
            (long) (Double.parseDouble(values.get(1)) + Double.parseDouble(values.get(2))));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    String latestResult = count;
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getBufferCount(Connection connection) throws BaseException {
    String name = "当前缓冲区数量";
    String metricType = "内存";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.buffer.count\".\"id=mapped\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.buffer.count\".\"id=direct\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String s1 = values.get(1);
    String s2 = values.get(2);
    s1 = s1.substring(0, s1.indexOf('.'));
    s2 = s2.substring(0, s2.indexOf('.'));
    String latestResult = (Integer.parseInt(s1) + Integer.parseInt(s2)) + "个";
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getJVMCommittedMemorySize(Connection connection)
      throws BaseException {
    String name = "当前向JVM申请的内存大小";
    String metricType = "内存";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.committed\".\"area=nonheap\".\"id=Compressed Class Space\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.committed\".\"area=nonheap\".\"id=Code Cache\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.committed\".\"area=nonheap\".\"id=Metaspace\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.committed\".\"area=heap\".\"id=PS Old Gen\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.committed\".\"area=heap\".\"id=PS Eden Space\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.committed\".\"area=heap\".\"id=PS Survivor Space\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String count =
        getNetFileSizeDescription(
            (long)
                (Double.parseDouble(values.get(1))
                    + Double.parseDouble(values.get(2))
                    + Double.parseDouble(values.get(3))
                    + Double.parseDouble(values.get(4))
                    + Double.parseDouble(values.get(5))
                    + Double.parseDouble(values.get(6))));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    String latestResult = count;
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getJVMMemoryMaxSize(Connection connection) throws BaseException {
    String name = "JVM最大内存";
    String metricType = "内存";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.max\".\"area=nonheap\".\"id=Compressed Class Space\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.max\".\"area=nonheap\".\"id=Code Cache\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.max\".\"area=nonheap\".\"id=Metaspace\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.max\".\"area=heap\".\"id=PS Old Gen\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.max\".\"area=heap\".\"id=PS Eden Space\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.max\".\"area=heap\".\"id=PS Survivor Space\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String count =
        getNetFileSizeDescription(
            (long)
                (Double.parseDouble(values.get(1))
                    + Double.parseDouble(values.get(2))
                    + Double.parseDouble(values.get(3))
                    + Double.parseDouble(values.get(4))
                    + Double.parseDouble(values.get(5))
                    + Double.parseDouble(values.get(6))));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    String latestResult = count;
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getJVMMemoryUsedSize(Connection connection) throws BaseException {
    String name = "JVM已使用内存大小";
    String metricType = "内存";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.used\".\"area=nonheap\".\"id=Compressed Class Space\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.used\".\"area=nonheap\".\"id=Code Cache\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.used\".\"area=nonheap\".\"id=Metaspace\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.used\".\"area=heap\".\"id=PS Old Gen\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.used\".\"area=heap\".\"id=PS Eden Space\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.memory.used\".\"area=heap\".\"id=PS Survivor Space\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String count =
        getNetFileSizeDescription(
            (long)
                (Double.parseDouble(values.get(1))
                    + Double.parseDouble(values.get(2))
                    + Double.parseDouble(values.get(3))
                    + Double.parseDouble(values.get(4))
                    + Double.parseDouble(values.get(5))
                    + Double.parseDouble(values.get(6))));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    String latestResult = count;
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getJVMUnloadedClassesTotal(Connection connection)
      throws BaseException {
    String name = "JVM累计卸载的Class数量";
    String metricType = "Classes";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.classes.unloaded\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String s1 = values.get(1);
    s1 = s1.substring(0, s1.indexOf('.'));
    String latestResult = Integer.parseInt(s1) + "个";
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getJVMloadedClassesTotal(Connection connection) throws BaseException {
    String name = "JVM累计加载的Class数量";
    String metricType = "Classes";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.classes.loaded\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String s1 = values.get(1);
    s1 = s1.substring(0, s1.indexOf('.'));
    String latestResult = Integer.parseInt(s1) + "个";
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getJVMCompilationTime(Connection connection) throws BaseException {
    String name = "JVM耗费在编译上的时间";
    String metricType = "Classes";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"jvm.compilation.time\".\"compiler=HotSpot 64-Bit Tiered Compilers\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String latestResult = values.get(1) + "s";
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public MetricsListDataVO getCPUUsed(Connection connection) throws BaseException {
    String name = "CPU使用率";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"sys_cpu_load\".\"name=system\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String latestResult = values.get(1) + "%";
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getCPUCores(Connection connection) throws BaseException {
    String name = "CPU核数";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"sys_cpu_cores\".\"name=system\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String s1 = values.get(1);
    s1 = s1.substring(0, s1.indexOf('.'));
    String latestResult = s1 + "核";
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getCPUTime(Connection connection) throws BaseException {
    String name = "CPU Time";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"process_cpu_time\".\"name=process\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String timeStr = values.get(1);
    long count = Long.parseLong(timeStr.substring(timeStr.indexOf("E") + 1));
    double time = Double.parseDouble(timeStr.substring(0, timeStr.indexOf("E")));
    while (count > 0) {
      time *= 10;
      count--;
    }
    String latestResult = (float) (time / 1000000000) + "s";
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getTotalMem(Connection connection) throws BaseException {
    String name = "物理内存大小";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    String str = connection.getHost();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"sys_total_physical_memory_size\".\"name=system\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String latestResult = getNetFileSizeDescription((long) Double.parseDouble(values.get(1)));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getProcessRatio(Connection connection) throws BaseException {
    String name = "IoTDB进程内存占用比例";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"process_mem_ratio\".\"name=process\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    String latestResult = values.get(1) + "%";
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getDiskTotalSize(Connection connection) throws BaseException {
    String name = "磁盘总大小";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"sys_disk_total_space\".\"name=system\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String latestResult = getNetFileSizeDescription((long) Double.parseDouble(values.get(1)));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getDiskLoadSize(Connection connection) throws BaseException {
    // TODO: 假数据，等待iotdb增加该指标
    String name = "磁盘挂载";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"sys_disk_total_space\".\"name=system\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    //    String latestResult = getNetFileSizeDescription((long)Double.parseDouble(values.get(1)));
    String latestResult = "【假数据：指标暂缺】2G";
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getDiskAvailableSize(Connection connection) throws BaseException {
    String name = "磁盘剩余";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"sys_disk_free_space\".\"name=system\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String latestResult = getNetFileSizeDescription((long) Double.parseDouble(values.get(1)));
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getDiskIO(Connection connection) throws BaseException {
    String name = "磁盘IO吞吐";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"sys_disk_free_space\".\"name=system\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    //    String latestResult = getNetFileSizeDescription((long)Double.parseDouble(values.get(1)));
    String latestResult = "【假数据：指标暂缺】136K/s";
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getWalFileCountAndSize(Connection connection) throws BaseException {
    String name = "wal日志文件数量及大小";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"file_count\".\"name=wal\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"file_size\".\"name=wal\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String count = values.get(1);
    count = count.substring(0, count.indexOf('.'));
    String size = getNetFileSizeDescription((long) Double.parseDouble(values.get(2)));
    String latestResult = "数量：" + count + "；" + "大小：" + size;
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getSeqTSFileCountAndSize(Connection connection) throws BaseException {
    String name = "顺序TsFile文件数量及大小";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"file_count\".\"name=seq\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"file_size\".\"name=seq\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String count = values.get(1);
    count = count.substring(0, count.indexOf('.'));
    String size = getNetFileSizeDescription((long) Double.parseDouble(values.get(2)));
    String latestResult = "数量：" + count + "；" + "大小：" + size;
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getUnSeqTSFileCountAndSize(Connection connection) throws BaseException {
    String name = "乱序TsFile文件数量及大小";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"file_count\".\"name=unseq\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"file_size\".\"name=unseq\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String count = values.get(1);
    count = count.substring(0, count.indexOf('.'));
    String size = getNetFileSizeDescription((long) Double.parseDouble(values.get(2)));
    String latestResult = "数量：" + count + "；" + "大小：" + size;
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getWriteDelay(Connection connection) throws BaseException {
    String name = "写入延迟(最近一分钟)";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"file_count\".\"name=unseq\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"file_size\".\"name=unseq\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String count = values.get(1);
    count = count.substring(0, count.indexOf('.'));
    String size = getNetFileSizeDescription((long) Double.parseDouble(values.get(2)));
    String latestResult = "【假数据：指标暂缺】" + "90" + "%";
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getWriteSucceedCount(Connection connection) throws BaseException {
    String name = "查询成功次数(最近1分钟)";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"file_count\".\"name=unseq\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"file_size\".\"name=unseq\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String count = values.get(1);
    count = count.substring(0, count.indexOf('.'));
    String size = getNetFileSizeDescription((long) Double.parseDouble(values.get(2)));
    String latestResult = "【假数据：指标暂缺】" + "100" + "次";
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getWriteFailedCount(Connection connection) throws BaseException {
    String name = "查询失败次数(最近1分钟)";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"file_count\".\"name=unseq\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"file_size\".\"name=unseq\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String count = values.get(1);
    count = count.substring(0, count.indexOf('.'));
    String size = getNetFileSizeDescription((long) Double.parseDouble(values.get(2)));
    String latestResult = "【假数据：指标暂缺】" + "20" + "次";
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getWriteSucceedRatio(Connection connection) throws BaseException {
    String name = "查询成功率";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Integer detailAvailable = 0;
    int port = connection.getPort();
    // TODO bug 修复后删除
    if (port == 6668) {
      port = 8086;
    }
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"file_count\".\"name=unseq\", "
            + "root._metric.\"127.0.0.1:"
            + port
            + "\".\"file_size\".\"name=unseq\" "
            + "order by time desc limit 1";
    SessionPool sessionPool = getSessionPool(connection);
    List<String> values = executeQueryOneLine(sessionPool, sql);
    long lastestTimeStamp = Long.parseLong(values.get(0));
    String count = values.get(1);
    count = count.substring(0, count.indexOf('.'));
    String size = getNetFileSizeDescription((long) Double.parseDouble(values.get(2)));
    String latestResult = "【假数据：指标暂缺】" + "80" + "%";
    String latestScratchTime = simpleDateFormat.format(lastestTimeStamp);
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  @Override
  public List<MetricsListDataVO> getJVMMetricsDataList(Connection connection) throws BaseException {
    List<MetricsListDataVO> list = new ArrayList<>();
    list.add(getCurrentThreadsCount(connection));
    list.add(getCurrentDaemonThreadsCount(connection));
    list.add(getPeakThreadsCount(connection));
    list.add(getVariableThreadsCount(connection));
    list.add(getYGCHappendCountAndCostTime(connection));
    list.add(getYGCMaxCostTimeAndReason(connection));
    list.add(getFGCHappendCountAndCostTime(connection));
    list.add(getFGCMaxCostTimeAndReason(connection));
    list.add(getGCCPUoverhead(connection));
    list.add(getGCPromoted(connection));
    list.add(getMajorMemoryMaxValueEver(connection));
    list.add(getMajorMemorySizeAfterGC(connection));
    list.add(getMinorMemorySizeAddedBetweentwoGC(connection));
    list.add(getBufferUsed(connection));
    list.add(getMaxBuffer(connection));
    list.add(getBufferCount(connection));
    list.add(getJVMCommittedMemorySize(connection));
    list.add(getJVMMemoryMaxSize(connection));
    list.add(getJVMMemoryUsedSize(connection));
    list.add(getJVMUnloadedClassesTotal(connection));
    list.add(getJVMloadedClassesTotal(connection));
    list.add(getJVMCompilationTime(connection));
    return list;
  }

  @Override
  public List<MetricsListDataVO> getCPUMetricsDataList(Connection connection) throws BaseException {
    List<MetricsListDataVO> list = new ArrayList<>();
    list.add(getCPUCores(connection));
    list.add(getCPUUsed(connection));
    list.add(getCPUTime(connection));
    return list;
  }

  @Override
  public List<MetricsListDataVO> getMemMetricsDataList(Connection connection) throws BaseException {
    List<MetricsListDataVO> list = new ArrayList<>();
    list.add(getTotalMem(connection));
    list.add(getProcessRatio(connection));
    return list;
  }

  @Override
  public List<MetricsListDataVO> getDiskMetricsDataList(Connection connection)
      throws BaseException {
    List<MetricsListDataVO> list = new ArrayList<>();

    try {
      list.add(getDiskTotalSize(connection));
      list.add(getDiskLoadSize(connection));
      list.add(getDiskAvailableSize(connection));
      list.add(getDiskIO(connection));
      list.add(getWalFileCountAndSize(connection));
      list.add(getSeqTSFileCountAndSize(connection));
    } catch (Exception e) {
      e.printStackTrace();
      return list;
    }

    return list;
  }

  @Override
  public List<MetricsListDataVO> getWriteMetricsDataList(Connection connection)
      throws BaseException {
    List<MetricsListDataVO> list = new ArrayList<>();
    list.add(getWriteDelay(connection));
    list.add(getWriteSucceedCount(connection));
    list.add(getWriteFailedCount(connection));
    list.add(getWriteFailedCount(connection));
    list.add(getWriteSucceedRatio(connection));
    return list;
  }

  @Override
  public MetircsQueryClassificationVO getMetircsQueryClassification(Integer serverId) {
    // TODO：等待清华提供查询分类的接口
    List<QueryClassificationVO> fakeData = new ArrayList<>();
    for (int i = 0; i < 6; i++) {
      QueryClassificationVO queryClassificationVO = new QueryClassificationVO();
      queryClassificationVO.setId(i + 1);
      queryClassificationVO.setName("查询分类" + (i + 1));
      queryClassificationVO.setFlag(i % 2 == 0 ? 1 : 0);
      fakeData.add(queryClassificationVO);
    }
    MetircsQueryClassificationVO metircsQueryClassificationVO = new MetircsQueryClassificationVO();
    metircsQueryClassificationVO.setServerId(serverId);
    metircsQueryClassificationVO.setClassificationList(fakeData);
    return metircsQueryClassificationVO;
  }

  @Override
  public QueryInfoVO getQueryInfo(
      Integer serverId,
      Integer queryClassificationId,
      Integer pageSize,
      Integer pageNum,
      String filterString,
      String startTimeStr,
      String endTimeStr,
      Integer executionResult)
      throws BaseException {
    long startTime = Long.parseLong(startTimeStr);
    long endTime = Long.parseLong(endTimeStr);
    Connection connection = connectionService.getById(serverId);
    QueryInfoDTO queryInfoDTO =
        iotDBService.getQueryInfoListByQueryClassificationId(
            connection,
            queryClassificationId,
            pageSize,
            pageNum,
            filterString,
            startTime,
            endTime,
            executionResult);
    QueryInfoVO queryInfoVO = new QueryInfoVO();
    queryInfoVO.setQueryClassificationId(queryClassificationId);
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    Long latestRunningTime = queryInfoDTO.getLatestRunningTime();
    queryInfoVO.setLatestRunningTime(
        latestRunningTime == 0 ? null : simpleDateFormat.format(latestRunningTime));
    BeanUtils.copyProperties(queryInfoDTO, queryInfoVO);
    queryInfoVO.setServerId(serverId);
    return queryInfoVO;
  }

  @Override
  public MetricsDataCountVO getMetricsDataCount(Integer serverId) throws BaseException {
    Connection connection = connectionService.getById(serverId);
    DataCountVO dataCountVO = new DataCountVO();
    MetricsDataCountVO metricsDataCountVO = new MetricsDataCountVO();
    DataCountVO dataCount = new DataCountVO();
    try {
      dataCount = iotDBService.getDataCount(connection);
      metricsDataCountVO.setStatus(true);
    } catch (BaseException e) {
      metricsDataCountVO.setStatus(false);
    }
    metricsDataCountVO.setServerId(serverId);
    metricsDataCountVO.setUrl(connection.getHost());
    metricsDataCountVO.setPort(connection.getPort());
    BeanUtils.copyProperties(dataCount, metricsDataCountVO);
    metricsDataCountVO.setDataCount(dataCount.getDataCount());
    return metricsDataCountVO;
  }

  @Override
  public MetricsDataForListVO getMetricsDataForList(Integer serverId, Integer metricsType)
      throws BaseException {
    Connection connection = connectionService.getById(serverId);
    List<MetricsListDataVO> metricsDataList = null;
    // TODO：具体区分和判断等待清华提供方案和策略
    switch (metricsType) {
      case 0:
        metricsDataList = getJVMMetricsDataList(connection);
        break;
      case 1:
        metricsDataList = getCPUMetricsDataList(connection);
        break;
      case 2:
        metricsDataList = getMemMetricsDataList(connection);
        break;
      case 3:
        metricsDataList = getDiskMetricsDataList(connection);
        break;
      case 4:
        metricsDataList = getWriteMetricsDataList(connection);
        break;
    }
    MetricsDataForListVO metricsDataForListVO = new MetricsDataForListVO();
    metricsDataForListVO.setServerId(serverId);
    metricsDataForListVO.setMetricsType(metricsType);
    metricsDataForListVO.setListInfo(metricsDataList);
    return metricsDataForListVO;
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

  private List<String> executeQueryOneLine(SessionPool sessionPool, String sql)
      throws BaseException {
    SessionDataSetWrapper sessionDataSetWrapper = null;
    try {
      List<String> valueList = new ArrayList<>();
      sessionDataSetWrapper = sessionPool.executeQueryStatement(sql);
      if (sessionDataSetWrapper.hasNext()) {
        RowRecord rowRecord = sessionDataSetWrapper.next();
        valueList.add(rowRecord.getTimestamp() + "");
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

  private static String getNetFileSizeDescription(long size) {
    StringBuffer bytes = new StringBuffer();
    DecimalFormat format = new DecimalFormat("###.0");
    if (size >= 1024 * 1024 * 1024) {
      double i = (size / (1024.0 * 1024.0 * 1024.0));
      bytes.append(format.format(i)).append("GB");
    } else if (size >= 1024 * 1024) {
      double i = (size / (1024.0 * 1024.0));
      bytes.append(format.format(i)).append("MB");
    } else if (size >= 1024) {
      double i = (size / (1024.0));
      bytes.append(format.format(i)).append("KB");
    } else if (size < 1024) {
      if (size <= 0) {
        bytes.append("0B");
      } else {
        bytes.append((int) size).append("B");
      }
    }
    return bytes.toString();
  }
}
