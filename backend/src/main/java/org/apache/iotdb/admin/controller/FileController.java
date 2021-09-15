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
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(value = "导入导出数据相关")
public class FileController {
  @Autowired private FileService fileService;

  @Autowired private ConnectionService connectionService;

  @Autowired private ImportCsv importCsv;

  @Autowired private ExportCsv exportCsv;

  @ApiOperation("通过csv文件导入物理量数据  (新增2.8)")
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

    return BaseVO.success("导入数据成功", importDataVO);
  }

  @ApiOperation("将查询结果导出为csv文件  (新增2.10)")
  @PostMapping("/servers/{serverId}/exportData")
  public ResponseEntity<Resource> exportData(
      @PathVariable("serverId") Integer serverId,
      @RequestBody String sql,
      HttpServletRequest request)
      throws BaseException {
    check(request, serverId);
    Connection connection = connectionService.getById(serverId);

    String host = connection.getHost();
    Integer port = connection.getPort();
    String username = connection.getUsername();
    String password = connection.getPassword();
    String fileName = exportCsv.exportCsv(host, port, username, password, sql, null);

    org.springframework.core.io.Resource resource = fileService.loadFileAsResource(fileName);
    String contentType = "application/octet-stream";
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
        .body(resource);
  }

  @GetMapping("/downloadFile/{fileName}")
  public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) throws BaseException {
    Resource resource = fileService.loadFileAsResource(fileName);
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
