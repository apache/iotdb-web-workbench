package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ImportDataVO implements Serializable {
  private Integer totalCount;

  private Integer failCount;

  private String fileDownloadUri;

  public ImportDataVO(Integer totalCount, Integer failCount, String fileDownloadUri) {
    this.totalCount = totalCount;
    this.failCount = failCount;
    this.fileDownloadUri = fileDownloadUri;
  }
}
