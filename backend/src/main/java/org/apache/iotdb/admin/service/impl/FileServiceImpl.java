package org.apache.iotdb.admin.service.impl;



import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.config.FileProperties;
import org.apache.iotdb.admin.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService {

    private final Path fileStorageLocation;

    @Autowired
    public FileServiceImpl(FileProperties fileProperties) {
        this.fileStorageLocation = Paths.get(fileProperties.getTempDir()).toAbsolutePath().normalize();
    }

    @Override
    public String storeFile(MultipartFile file, String fileName) throws BaseException {
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            String fileFullName = targetLocation.toString();
            return fileFullName;
        } catch (IOException ex) {
            throw new BaseException(ErrorCode.UPLOAD_FILE_FAIL, ErrorCode.UPLOAD_FILE_FAIL_MSG);
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) throws BaseException {
        Path filePath;
        try {
            filePath = this.fileStorageLocation.resolve(fileName).normalize();
            System.out.println(filePath.toUri());
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new BaseException(ErrorCode.FILE_NOT_FOUND,ErrorCode.FILE_NOT_FOUND_MSG);
            }
        } catch (MalformedURLException ex) {
            throw new BaseException(ErrorCode.FILE_NOT_FOUND,ErrorCode.FILE_NOT_FOUND_MSG);
        }
    }
}
