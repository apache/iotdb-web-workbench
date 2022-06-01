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

package org.apache.iotdb.admin.controller;

import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.common.utils.AuthenticationUtils;
import org.apache.iotdb.admin.model.entity.Connection;
import org.apache.iotdb.admin.model.vo.BaseVO;
import org.apache.iotdb.admin.model.vo.ImportDataVO;
import org.apache.iotdb.admin.service.ConnectionService;
import org.apache.iotdb.admin.service.FileService;
import org.apache.iotdb.admin.tool.ExportCsv;
import org.apache.iotdb.admin.tool.ImportCsv;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(value = "File related")
public class FileController {
  @Autowired private FileService fileService;

  @Autowired private ConnectionService connectionService;

  @Autowired private ImportCsv importCsv;

  @Autowired private ExportCsv exportCsv;

  @ApiOperation("Import measurement data from a CSV file")
  @PostMapping("/servers/{serverId}/importData")
  public BaseVO<ImportDataVO> importData(
      @RequestParam("file") MultipartFile file,
      @PathVariable("serverId") Integer serverId,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    if (!file.getOriginalFilename().toLowerCase().endsWith(".csv")) {
      throw new BaseException(ErrorCode.FILE_NAME_ILLEGAL, ErrorCode.FILE_NAME_ILLEGAL_MSG);
    }
    String fileName = "import" + System.currentTimeMillis() + ".csv";
    String fileFullName = fileService.storeFile(file, fileName);

    Connection connection = connectionService.getById(serverId);
    String host = connection.getHost();
    Integer port = connection.getPort();
    String username = connection.getUsername();
    String password = connection.getPassword();
    ImportDataVO importDataVO =
        importCsv.importCsv(host, port, username, password, fileFullName, null);

    return BaseVO.success("Import data successfully", importDataVO);
  }

  @ApiOperation("Export the query result as a CSV file")
  @GetMapping("/servers/{serverId}/exportData")
  public ResponseEntity<Resource> exportData(
      @PathVariable("serverId") Integer serverId,
      @RequestParam("sql") String sql,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);

    String host = connection.getHost();
    Integer port = connection.getPort();
    String username = connection.getUsername();
    String password = connection.getPassword();
    String fileName = exportCsv.exportCsv(host, port, username, password, sql, null);

    Resource resource = fileService.loadFileAsResource(fileName);
    return getResponseEntity(resource);
  }

  @GetMapping("/downloadFile/{fileName}")
  public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws BaseException {
    Resource resource = fileService.loadFileAsResource(fileName);
    return getResponseEntity(resource);
  }

  @ApiOperation("Download the CSV template file")
  @GetMapping("/downloadFile/template")
  public ResponseEntity<Resource> downloadTemplateFile() throws BaseException {
    Resource resource = new ClassPathResource("file/template.csv");
    if (!resource.exists()) {
      throw new BaseException(ErrorCode.FILE_NOT_FOUND, ErrorCode.FILE_NOT_FOUND_MSG);
    }
    return getResponseEntity(resource);
  }

  @ApiOperation("Download the query log file")
  @GetMapping("/downloadQueryLogFile")
  public ResponseEntity<Resource> downloadQueryLogFile(
      @RequestParam String SQLStatement, @RequestParam Long timeStamp) throws BaseException {
    Resource resource = new ClassPathResource("file/[Fake]QueryLog.txt");
    if (!resource.exists()) {
      throw new BaseException(ErrorCode.FILE_NOT_FOUND, ErrorCode.FILE_NOT_FOUND_MSG);
    }
    return getResponseEntity(resource);
  }

  private ResponseEntity<Resource> getResponseEntity(Resource resource) {
    String contentType = "application/octet-stream";

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
        .body(resource);
  }

  private void check(HttpServletRequest request, Integer serverId) throws BaseException {
    Integer userId = AuthenticationUtils.getUserId(request);
    connectionService.check(serverId, userId);
  }
}
