package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.common.exception.BaseException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
  String storeFile(MultipartFile file, String fileName) throws BaseException;

  Resource loadFileAsResource(String fileName) throws BaseException;
}
