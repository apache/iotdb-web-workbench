package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DeviceDTO implements Serializable {

  private String timeseries;

  private String dataType;

  private String encoding;

  private String description;

  private String alias;

  private String compression;

  private List<List<String>> tags;

  private List<List<String>> attributes;
}
