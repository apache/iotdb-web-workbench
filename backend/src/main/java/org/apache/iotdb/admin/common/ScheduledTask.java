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

package org.apache.iotdb.admin.common;

import org.apache.iotdb.admin.config.FileProperties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Periodically delete files generated during CSV import and export */
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
      log.error("Could not create the directory:" + fileStorageLocation, e);
      throw new RuntimeException("Could not create the directory:" + fileStorageLocation, e);
    }
  }

  @Scheduled(fixedRate = 1000 * 60 * 10L)
  public void scheduledTask() {
    File[] files = fileStorageDirectory.listFiles();
    for (File file : files) {
      if (file.lastModified() < System.currentTimeMillis() - 1000 * 60 * 10L) {
        file.delete();
        log.info("Delete file:" + file.getAbsolutePath());
      }
    }
  }
}
