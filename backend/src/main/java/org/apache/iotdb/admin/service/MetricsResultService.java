package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.MetricsListDataVO;

import java.util.List;

/**
 * @author Erickin
 * @create 2022-04-26-下午 4:07
 */
public interface MetricsResultService {
  List<MetricsListDataVO> getJVMMetricsDataList(Connection connection) throws BaseException;

  List<MetricsListDataVO> getCPUMetricsDataList(Connection connection) throws BaseException;

  List<MetricsListDataVO> getMemMetricsDataList(Connection connection) throws BaseException;

  List<MetricsListDataVO> getDiskMetricsDataList(Connection connection) throws BaseException;

  List<MetricsListDataVO> getWriteMetricsDataList(Connection connection) throws BaseException;
}
