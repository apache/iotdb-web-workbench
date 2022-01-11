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

package org.apache.iotdb.admin.tool;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.config.FileProperties;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.RpcUtils;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.session.SessionDataSet;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.iotdb.tsfile.read.common.Field;
import org.apache.iotdb.tsfile.read.common.RowRecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class ExportCsv {

  private final Path fileStorageLocation;

  @Autowired
  public ExportCsv(FileProperties fileProperties) {
    this.fileStorageLocation = Paths.get(fileProperties.getTempDir()).toAbsolutePath().normalize();
  }

  private static final String TARGET_FILE = "dump";

  public String exportCsv(
      String host, Integer port, String username, String password, String sql, String timeZone)
      throws BaseException {
    Session session = null;
    try {
      session = new Session(host, port, username, password);
      session.open(false);

      if (timeZone != null) {
        session.setTimeZone(timeZone);
      }
      ZoneId zoneId = ZoneId.of(session.getTimeZone());

      return dumpResult(sql, session, zoneId);
    } catch (IoTDBConnectionException e) {
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    } catch (StatementExecutionException e) {
      throw new BaseException(
          ErrorCode.IMPORT_CSV_FAIL, ErrorCode.IMPORT_CSV_FAIL_MSG + e.getMessage());
    } finally {
      if (session != null) {
        try {
          session.close();
        } catch (IoTDBConnectionException e) {
          throw new BaseException(ErrorCode.CLOSE_DBCONN_FAIL, ErrorCode.CLOSE_DBCONN_FAIL_MSG);
        }
      }
    }
  }

  private String dumpResult(String sql, Session session, ZoneId zoneId) throws BaseException {
    final String fileName = TARGET_FILE + System.currentTimeMillis() + ".csv";
    final String path = fileStorageLocation.resolve(fileName).toString();
    File tf = new File(path);
    try {
      if (!tf.exists() && !tf.createNewFile()) {
        throw new BaseException(ErrorCode.CREATE_FILE_FAIL, ErrorCode.CREATE_FILE_FAIL_MSG);
      }
    } catch (IOException | BaseException e) {
      throw new BaseException(ErrorCode.CREATE_FILE_FAIL, ErrorCode.CREATE_FILE_FAIL_MSG);
    }
    log.info("Start exporting data from SQL statement:" + sql);
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(tf))) {
      SessionDataSet sessionDataSet = session.executeQueryStatement(sql);
      long startTime = System.currentTimeMillis();
      // write data in csv file
      List<String> columnNames = sessionDataSet.getColumnNames();
      writeMetadata(bw, columnNames);
      boolean isDataType = false;
      if ("Time".equals(columnNames.get(0))) {
        isDataType = true;
      }
      int line = writeResultSet(sessionDataSet, bw, zoneId, isDataType);
      String runTime = System.currentTimeMillis() - startTime + "ms";
      log.info("Export " + line + " line data from SQL:" + sql + ". Time consuming:" + runTime);
      return fileName;
    } catch (IoTDBConnectionException e) {
      throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
    } catch (IOException e) {
      throw new BaseException(ErrorCode.FILE_IO_FAIL, ErrorCode.FILE_IO_FAIL_MSG + e.getMessage());
    } catch (StatementExecutionException e) {
      throw new BaseException(
          ErrorCode.EXPORT_CSV_FAIL, ErrorCode.EXPORT_CSV_FAIL_MSG + e.getMessage());
    }
  }

  private void writeMetadata(BufferedWriter bw, List<String> columnNames) throws IOException {
    for (int i = 0; i < columnNames.size() - 1; i++) {
      bw.write(columnNames.get(i) + ",");
    }
    bw.write(columnNames.get(columnNames.size() - 1) + "\n");
  }

  private int writeResultSet(
      SessionDataSet rs, BufferedWriter bw, ZoneId zoneId, boolean isDataType)
      throws IOException, StatementExecutionException, IoTDBConnectionException {
    int line = 0;
    if (isDataType) {
      while (rs.hasNext()) {
        RowRecord rowRecord = rs.next();
        List<Field> fields = rowRecord.getFields();
        writeTime(rowRecord.getTimestamp(), bw, zoneId);
        writeValue(fields, bw);
        line++;
      }
    } else {
      while (rs.hasNext()) {
        RowRecord rowRecord = rs.next();
        List<Field> fields = rowRecord.getFields();
        writeValue(fields, bw);
        line++;
      }
    }
    return line;
  }

  private void writeTime(Long time, BufferedWriter bw, ZoneId zoneId) throws IOException {
    ZonedDateTime dateTime;
    String timestampPrecision = "ms";
    String str =
        RpcUtils.parseLongToDateWithPrecision(
            DateTimeFormatter.ISO_OFFSET_DATE_TIME, time, zoneId, timestampPrecision);
    bw.write(str + ",");
  }

  @SuppressWarnings("squid:S3776") // Suppress high Cognitive Complexity warning
  private void writeValue(List<Field> fields, BufferedWriter bw) throws IOException {
    for (int j = 0; j < fields.size() - 1; j++) {
      String value = fields.get(j).getStringValue();
      if ("null".equalsIgnoreCase(value)) {
        bw.write(",");
      } else {
        if (fields.get(j).getDataType() == TSDataType.TEXT) {
          int location = value.indexOf("\"");
          if (location > -1) {
            if (location == 0 || value.charAt(location - 1) != '\\') {
              bw.write("\"" + value.replace("\"", "\\\"") + "\",");
            } else {
              bw.write("\"" + value + "\",");
            }
          } else if (value.contains(",")) {
            bw.write("\"" + value + "\",");
          } else {
            bw.write(value + ",");
          }
        } else {
          bw.write(value + ",");
        }
      }
    }
    String lastValue = fields.get(fields.size() - 1).getStringValue();
    if ("null".equalsIgnoreCase(lastValue)) {
      bw.write("\n");
    } else {
      if (fields.get(fields.size() - 1).getDataType() == TSDataType.TEXT) {
        int location = lastValue.indexOf("\"");
        if (location > -1) {
          if (location == 0 || lastValue.charAt(location - 1) != '\\') {
            bw.write("\"" + lastValue.replace("\"", "\\\"") + "\"\n");
          } else {
            bw.write("\"" + lastValue + "\"\n");
          }
        } else if (lastValue.contains(",")) {
          bw.write("\"" + lastValue + "\"\n");
        } else {
          bw.write(lastValue + "\n");
        }
      } else {
        bw.write(lastValue + "\n");
      }
    }
  }
}
