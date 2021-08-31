package org.apache.iotdb.admin.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.iotdb.admin.config.FileProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 用于定时删除导入导出csv过程中产生的文件
 */
@Slf4j
@Component
public class ScheduledTask {
    private final Path fileStorageLocation;

    private final File fileStorageDirectory;

    @Autowired
    public ScheduledTask(FileProperties fileProperties) {
        fileStorageLocation = Paths.get(fileProperties.getTempDir()).toAbsolutePath().normalize();
        fileStorageDirectory = new File(fileStorageLocation.toString());
        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception e) {
            log.error("无法创建文件夹/Could not create the directory:" + fileStorageLocation, e);
            throw new RuntimeException("无法创建文件夹/Could not create the directory:" + fileStorageLocation, e);
        }
    }

    @Scheduled(fixedRate = 1000*60*10L)
    public void scheduledTask() {
        File[] files = fileStorageDirectory.listFiles();
        for (File file : files) {
            if (file.lastModified() < System.currentTimeMillis() - 1000*60*10L) {
                file.delete();
                log.info("删除文件/Delete file:" + file.getAbsolutePath());
            }
        }
    }
}
