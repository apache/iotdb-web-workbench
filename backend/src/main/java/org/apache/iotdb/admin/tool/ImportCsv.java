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
import org.apache.iotdb.admin.model.vo.ImportDataVO;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.tsfile.common.constant.TsFileConstant;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class ImportCsv {

  private static final String[] STRING_TIME_FORMAT =
      new String[] {
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        "yyyy/MM/dd HH:mm:ss.SSS",
        "yyyy-MM-dd HH:mm:ss.SSS",
        "yyyy.MM.dd HH:mm:ss.SSS",
        "yyyy/MM/dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy.MM.dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd HH:mm:ss.SSSZZ",
        "yyyy/MM/dd HH:mm:ss.SSSZZ",
        "yyyy.MM.dd HH:mm:ss.SSSZZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSSZZ",
        "yyyy/MM/dd'T'HH:mm:ss.SSSZZ",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy/MM/dd HH:mm:ss",
        "yyyy.MM.dd HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy/MM/dd'T'HH:mm:ss",
        "yyyy.MM.dd'T'HH:mm:ss",
        "yyyy-MM-dd HH:mm:ssZZ",
        "yyyy/MM/dd HH:mm:ssZZ",
        "yyyy.MM.dd HH:mm:ssZZ",
        "yyyy-MM-dd'T'HH:mm:ssZZ",
        "yyyy/MM/dd'T'HH:mm:ssZZ",
        "yyyy.MM.dd'T'HH:mm:ssZZ",
      };

  private final Path fileStorageLocation;

  @Autowired
  public ImportCsv(FileProperties fileProperties) {
    this.fileStorageLocation = Paths.get(fileProperties.getTempDir()).toAbsolutePath().normalize();
  }

  public ImportDataVO importCsv(
      String host, Integer port, String username, String password, String filename, String timeZone)
      throws BaseException {
    Session session = null;
    try {
      session = new Session(host, port, username, password, false);
      session.open(false);
      if (timeZone != null) {
        session.setTimeZone(timeZone);
      }

      File file = new File(filename);
      if (file.isFile()) {
        return loadDataFromCSV(file, session);
      } else {
        throw new BaseException(ErrorCode.UPLOAD_FILE_FAIL, ErrorCode.UPLOAD_FILE_FAIL_MSG);
      }
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

  @SuppressWarnings("squid:S1135")
  private ImportDataVO loadDataFromCSV(File file, Session session) throws BaseException {
    log.info("Start import data from file:" + file.getName());
    String errorFileName = "error" + System.currentTimeMillis() + ".txt";
    File errorFile = new File(this.fileStorageLocation.resolve(errorFileName).toString());

    try (BufferedReader br =
            new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        BufferedWriter bw = new BufferedWriter(new FileWriter(errorFile))) {
      String header = br.readLine();
      String[] cols = splitCsvLine(header);
      if (cols.length <= 1) {
        throw new BaseException(
            ErrorCode.FILE_FIRST_LINE_ILLEGAL, ErrorCode.FILE_FIRST_LINE_ILLEGAL_MSG);
      }
      for (int i = 1; i < cols.length; i++) {
        if (!cols[i].startsWith("root.") || StringUtils.countMatches(cols[i], ".") < 2) {
          throw new BaseException(
              ErrorCode.FILE_FIRST_LINE_ILLEGAL, ErrorCode.FILE_FIRST_LINE_ILLEGAL_MSG);
        }
      }

      List<String> devices = new ArrayList<>();
      List<Long> times = new ArrayList<>();
      List<List<String>> measurementsList = new ArrayList<>();
      List<List<String>> valuesList = new ArrayList<>();
      Map<String, List<Integer>> devicesToPositions = new HashMap<>();
      Map<String, List<String>> devicesToMeasurements = new HashMap<>();

      for (int i = 1; i < cols.length; i++) {
        splitColToDeviceAndMeasurement(cols[i], devicesToPositions, devicesToMeasurements, i);
      }

      SimpleDateFormat timeFormatter = null;
      boolean useFormatter = false;

      int lineNumber = 0;
      List<String> insertErrorInfo = new ArrayList<>();
      String line;
      while ((line = br.readLine()) != null) {
        cols = splitCsvLine(line);
        lineNumber++;
        if (lineNumber == 1) {
          timeFormatter = formatterInit(cols[0]);
          useFormatter = (timeFormatter != null);
        }
        for (Map.Entry<String, List<Integer>> deviceToPositions : devicesToPositions.entrySet()) {
          List<String> values = new ArrayList<>();
          for (int position : deviceToPositions.getValue()) {
            values.add(cols[position]);
          }
          boolean isAllBlank = true;
          for (String value : values) {
            if (value != null && !"".equals(value)) {
              isAllBlank = false;
              break;
            }
          }
          if (isAllBlank) {
            continue;
          }
          valuesList.add(values);

          String device = deviceToPositions.getKey();
          devices.add(device);

          times.add(parseTime(cols[0], useFormatter, timeFormatter));

          measurementsList.add(devicesToMeasurements.get(device));
        }
        if (lineNumber % 10000 == 0) {
          try {
            session.insertRecords(devices, times, measurementsList, valuesList);
          } catch (StatementExecutionException e) {
            if (e.getMessage().contains("failed to insert measurements")) {
              insertErrorInfo.addAll(
                  Arrays.asList(
                      StringUtils.splitByWholeSeparator(
                          e.getMessage(),
                          "org.apache.iotdb.db.exception.StorageEngineException: ")));
            } else {
              throw new BaseException(
                  ErrorCode.IMPORT_CSV_FAIL, ErrorCode.IMPORT_CSV_FAIL_MSG + e.getMessage());
            }
          } catch (IoTDBConnectionException e) {
            throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
          }
          devices = new ArrayList<>();
          times = new ArrayList<>();
          measurementsList = new ArrayList<>();
          valuesList = new ArrayList<>();
        }
      }
      try {
        if (lineNumber % 10000 != 0) {
          session.insertRecords(devices, times, measurementsList, valuesList);
        }
      } catch (StatementExecutionException e) {
        if (e.getMessage().contains("failed to insert measurements")) {
          insertErrorInfo.addAll(
              Arrays.asList(
                  StringUtils.splitByWholeSeparator(
                      e.getMessage(), "org.apache.iotdb.db.exception.StorageEngineException: ")));
        } else {
          throw new BaseException(
              ErrorCode.IMPORT_CSV_FAIL, ErrorCode.IMPORT_CSV_FAIL_MSG + e.getMessage());
        }
      } catch (IoTDBConnectionException e) {
        throw new BaseException(ErrorCode.GET_SESSION_FAIL, ErrorCode.GET_SESSION_FAIL_MSG);
      }

      for (String s : insertErrorInfo) {
        bw.write(s + "\n");
      }
      int errorCount = insertErrorInfo.size();
      String fileDownloadUri = null;
      if (errorCount > 0) {
        fileDownloadUri = "/downloadFile/" + errorFileName;
      }
      Integer totalCount = lineNumber * (cols.length - 1);
      return new ImportDataVO(totalCount, errorCount, fileDownloadUri);
    } catch (FileNotFoundException e) {
      throw new BaseException(ErrorCode.UPLOAD_FILE_FAIL, ErrorCode.UPLOAD_FILE_FAIL_MSG);
    } catch (IOException e) {
      throw new BaseException(ErrorCode.FILE_IO_FAIL, ErrorCode.FILE_IO_FAIL_MSG + e.getMessage());
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new BaseException(ErrorCode.FILE_FORMAT_ILLEGAL, ErrorCode.FILE_FORMAT_ILLEGAL_MSG);
    }
  }

  private long parseTime(String str, boolean useFormatter, SimpleDateFormat timeFormatter)
      throws BaseException {
    try {
      if (useFormatter) {
        return timeFormatter.parse(str).getTime();
      } else {
        return Long.parseLong(str);
      }
    } catch (Exception e) {
      throw new BaseException(ErrorCode.FILE_TIME_ILLEGAL, ErrorCode.FILE_TIME_ILLEGAL_MSG);
    }
  }

  private SimpleDateFormat formatterInit(String time) {

    try {
      Long.parseLong(time);
      return null;
    } catch (Exception ignored) {
      // do nothing
    }

    for (String timeFormat : STRING_TIME_FORMAT) {
      SimpleDateFormat format = new SimpleDateFormat(timeFormat);
      try {
        format.parse(time).getTime();
        return format;
      } catch (java.text.ParseException ignored) {
        // do nothing
      }
    }
    return null;
  }

  private void splitColToDeviceAndMeasurement(
      String col,
      Map<String, List<Integer>> devicesToPositions,
      Map<String, List<String>> devicesToMeasurements,
      int position)
      throws BaseException {
    if (col.length() > 0) {
      if (col.charAt(col.length() - 1) == TsFileConstant.DOUBLE_QUOTE) {
        int endIndex = col.lastIndexOf('"', col.length() - 2);
        // if a double quotes with escape character
        while (endIndex != -1 && col.charAt(endIndex - 1) == '\\') {
          endIndex = col.lastIndexOf('"', endIndex - 2);
        }
        if (endIndex != -1 && (endIndex == 0 || col.charAt(endIndex - 1) == '.')) {
          putDeviceAndMeasurement(
              col.substring(0, endIndex - 1),
              col.substring(endIndex),
              devicesToPositions,
              devicesToMeasurements,
              position);
        } else {
          throw new BaseException(
              ErrorCode.FILE_FIRST_LINE_ILLEGAL, ErrorCode.FILE_FIRST_LINE_ILLEGAL_MSG);
        }
      } else if (col.charAt(col.length() - 1) != TsFileConstant.DOUBLE_QUOTE
          && col.charAt(col.length() - 1) != TsFileConstant.PATH_SEPARATOR_CHAR) {
        int endIndex = col.lastIndexOf(TsFileConstant.PATH_SEPARATOR_CHAR);
        if (endIndex < 0) {
          putDeviceAndMeasurement("", col, devicesToPositions, devicesToMeasurements, position);
        } else {
          putDeviceAndMeasurement(
              col.substring(0, endIndex),
              col.substring(endIndex + 1),
              devicesToPositions,
              devicesToMeasurements,
              position);
        }
      } else {
        throw new BaseException(
            ErrorCode.FILE_FIRST_LINE_ILLEGAL, ErrorCode.FILE_FIRST_LINE_ILLEGAL_MSG);
      }
    } else {
      putDeviceAndMeasurement("", col, devicesToPositions, devicesToMeasurements, position);
    }
  }

  private void putDeviceAndMeasurement(
      String device,
      String measurement,
      Map<String, List<Integer>> devicesToPositions,
      Map<String, List<String>> devicesToMeasurements,
      int position) {
    if (devicesToMeasurements.get(device) == null && devicesToPositions.get(device) == null) {
      List<String> measurements = new ArrayList<>();
      measurements.add(measurement);
      devicesToMeasurements.put(device, measurements);
      List<Integer> positions = new ArrayList<>();
      positions.add(position);
      devicesToPositions.put(device, positions);
    } else {
      devicesToMeasurements.get(device).add(measurement);
      devicesToPositions.get(device).add(position);
    }
  }

  private String[] splitCsvLine(String path) throws BaseException {
    List<String> nodes = new ArrayList<>();
    int startIndex = 0;
    for (int i = 0; i < path.length(); i++) {
      if (path.charAt(i) == ',') {
        nodes.add(path.substring(startIndex, i));
        startIndex = i + 1;
      } else if (path.charAt(i) == '"') {
        int[] result = nextNode(path, nodes, '"', startIndex, i);
        startIndex = result[0];
        i = result[1];
      } else if (path.charAt(i) == '\'') {
        int[] result = nextNode(path, nodes, '\'', startIndex, i);
        startIndex = result[0];
        i = result[1];
      }
    }
    if (path.charAt(path.length() - 1) == ',') {
      nodes.add("");
    }
    if (startIndex <= path.length() - 1) {
      nodes.add(path.substring(startIndex));
    }
    return nodes.toArray(new String[0]);
  }

  private int[] nextNode(String path, List<String> nodes, char enclose, int startIndex, int i)
      throws BaseException {
    int endIndex = path.indexOf(enclose, i + 1);
    // if a double quotes with escape character
    while (endIndex != -1 && path.charAt(endIndex - 1) == '\\') {
      endIndex = path.indexOf(enclose, endIndex + 1);
    }
    if (endIndex != -1 && (endIndex == path.length() - 1 || path.charAt(endIndex + 1) == ',')) {
      nodes.add(path.substring(startIndex + 1, endIndex));
      i = endIndex + 1;
      startIndex = endIndex + 2;
      return new int[] {startIndex, i};
    } else {
      throw new BaseException(ErrorCode.FILE_LINE_ILLEGAL, ErrorCode.FILE_LINE_ILLEGAL_MSG + path);
    }
  }
}
