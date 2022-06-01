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
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.JVMMetricsListDataVO;
import org.apache.iotdb.admin.model.vo.MetricsListDataVO;
import org.apache.iotdb.admin.service.MetricsResultService;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class MetricsResultServiceImpl implements MetricsResultService {

  public JVMMetricsListDataVO getCurrentThreadsCount(long currentTimeMillis) throws BaseException {
    String name = "JVM当前线程数";
    String metricType = "线程";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String latestScratchTime = simpleDateFormat.format(currentTimeMillis);
    Integer detailAvailable = 1;
    String sql =
        "select * from "
            + "root._metric.\"127.0.0.1:8086\".\"jvm.threads.daemon\","
            + " root._metric.\"127.0.0.1:8086\".\"jvm.threads.live\" "
            + "order by time desc limit 1";
    try {
    } catch (Exception e) {
      e.printStackTrace();
    }
    String latestResult = "[Just Test] 前台:20个，后台:39个，线程总数:59个";
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getYGCHappendCountAndCostTime(long currentTimeMillis)
      throws BaseException {
    // TODO 暂时写死
    String name = "YGC发生次数及总耗时";
    String metricType = "垃圾回收";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String latestScratchTime = simpleDateFormat.format(currentTimeMillis);
    Integer detailAvailable = 2;
    String latestResult = "[Just Test] 20次 200s";
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getUsedBufferSize(long currentTimeMillis) throws BaseException {
    String name = "已经使用的缓冲区大小";
    String metricType = "内存";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String latestScratchTime = simpleDateFormat.format(currentTimeMillis);
    Integer detailAvailable = 3;
    String latestResult = "[Just Test] 20G";
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public JVMMetricsListDataVO getJVMTotalUnloadClass(long currentTimeMillis) throws BaseException {
    String name = "JVM累计卸载的class数量";
    String metricType = "Classes";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String latestScratchTime = simpleDateFormat.format(currentTimeMillis);
    Integer detailAvailable = 4;
    String latestResult = "[Just Test] 30次";
    JVMMetricsListDataVO jvmMetricsListDataVO = new JVMMetricsListDataVO();
    jvmMetricsListDataVO.setMetricType(metricType);
    jvmMetricsListDataVO.setDetailAvailable(detailAvailable);
    jvmMetricsListDataVO.setLatestResult(latestResult);
    jvmMetricsListDataVO.setLatestScratchTime(latestScratchTime);
    jvmMetricsListDataVO.setName(name);
    return jvmMetricsListDataVO;
  }

  public MetricsListDataVO getCPUUsed(long currentTimeMillis) throws BaseException {
    String name = "CPU使用率";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String latestScratchTime = simpleDateFormat.format(currentTimeMillis);
    Integer detailAvailable = 1;
    String latestResult = "[Just Test] 50%";
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getCPUCores(long currentTimeMillis) throws BaseException {
    String name = "CPU核数";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String latestScratchTime = simpleDateFormat.format(currentTimeMillis);
    Integer detailAvailable = 0;
    String latestResult = "[Just Test] 4核";
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getIotDBProcessMemUsed(long currentTimeMillis) throws BaseException {
    String name = "IoTDB进程内存占用比例";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String latestScratchTime = simpleDateFormat.format(currentTimeMillis);
    Integer detailAvailable = 1;
    String latestResult = "[Just Test] 70%";
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getTotalMem(long currentTimeMillis) throws BaseException {
    String name = "物理内存大小";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String latestScratchTime = simpleDateFormat.format(currentTimeMillis);
    Integer detailAvailable = 0;
    String latestResult = "[Just Test] 4G";
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getDiskAvaliable(long currentTimeMillis) throws BaseException {
    String name = "磁盘剩余";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String latestScratchTime = simpleDateFormat.format(currentTimeMillis);
    Integer detailAvailable = 1;
    String latestResult = "[Just Test] 2G";
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO getDiskTotalSize(long currentTimeMillis) throws BaseException {
    String name = "磁盘总大小";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String latestScratchTime = simpleDateFormat.format(currentTimeMillis);
    Integer detailAvailable = 0;
    String latestResult = "[Just Test] 4G";
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO writeSucceedProportion(long currentTimeMillis) throws BaseException {
    String name = "写入成功率";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String latestScratchTime = simpleDateFormat.format(currentTimeMillis);
    Integer detailAvailable = 0;
    String latestResult = "[Just Test] 80%";
    MetricsListDataVO metricsListDataVO = new MetricsListDataVO();
    metricsListDataVO.setDetailAvailable(detailAvailable);
    metricsListDataVO.setLatestResult(latestResult);
    metricsListDataVO.setLatestScratchTime(latestScratchTime);
    metricsListDataVO.setName(name);
    return metricsListDataVO;
  }

  public MetricsListDataVO writeLatency(long currentTimeMillis) throws BaseException {
    String name = "写入延迟（最近一分钟）";
    String pattern = "yyyy-MM-dd' 'HH:mm:ss.SSS";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String latestScratchTime = simpleDateFormat.format(currentTimeMillis);
    Integer detailAvailable = 1;
    String latestResult = "[Just Test] 90%";
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
    long currentTimeMillis = System.currentTimeMillis();
    JVMMetricsListDataVO currentThreadsCount = getCurrentThreadsCount(currentTimeMillis);
    JVMMetricsListDataVO ygcHappendCountAndCostTime =
        getYGCHappendCountAndCostTime(currentTimeMillis);
    JVMMetricsListDataVO usedBufferSize = getUsedBufferSize(currentTimeMillis);
    JVMMetricsListDataVO jvmTotalUnloadClass = getJVMTotalUnloadClass(currentTimeMillis);
    // TODO 把所有指标都加进来
    list.add(currentThreadsCount);
    list.add(ygcHappendCountAndCostTime);
    list.add(usedBufferSize);
    list.add(jvmTotalUnloadClass);
    return list;
  }

  @Override
  public List<MetricsListDataVO> getCPUMetricsDataList(Connection connection) throws BaseException {
    List<MetricsListDataVO> list = new ArrayList<>();
    long currentTimeMillis = System.currentTimeMillis();
    MetricsListDataVO cpuUsed = getCPUUsed(currentTimeMillis);
    MetricsListDataVO cpuCores = getCPUCores(currentTimeMillis);
    list.add(cpuUsed);
    list.add(cpuCores);
    return list;
  }

  @Override
  public List<MetricsListDataVO> getMemMetricsDataList(Connection connection) throws BaseException {
    List<MetricsListDataVO> list = new ArrayList<>();
    long currentTimeMillis = System.currentTimeMillis();
    MetricsListDataVO iotDBProcessMemUsed = getIotDBProcessMemUsed(currentTimeMillis);
    MetricsListDataVO totalMem = getTotalMem(currentTimeMillis);
    list.add(iotDBProcessMemUsed);
    list.add(totalMem);
    return list;
  }

  @Override
  public List<MetricsListDataVO> getDiskMetricsDataList(Connection connection)
      throws BaseException {
    List<MetricsListDataVO> list = new ArrayList<>();
    long currentTimeMillis = System.currentTimeMillis();
    MetricsListDataVO metricsListDataVO = getDiskAvaliable(currentTimeMillis);
    MetricsListDataVO diskTotalSize = getDiskTotalSize(currentTimeMillis);
    list.add(metricsListDataVO);
    list.add(diskTotalSize);
    return list;
  }

  @Override
  public List<MetricsListDataVO> getWriteMetricsDataList(Connection connection)
      throws BaseException {
    List<MetricsListDataVO> list = new ArrayList<>();
    long currentTimeMillis = System.currentTimeMillis();
    MetricsListDataVO writeSucceedProportion = writeSucceedProportion(currentTimeMillis);
    MetricsListDataVO writeLatency = writeLatency(currentTimeMillis);
    list.add(writeSucceedProportion);
    list.add(writeLatency);
    return list;
  }
}
