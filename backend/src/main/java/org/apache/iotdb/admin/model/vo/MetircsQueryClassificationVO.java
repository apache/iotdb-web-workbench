package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Erickin
 * @create 2022-04-25-上午 10:00
 */
@Data
public class MetircsQueryClassificationVO {
  private Integer serverId;
  private List<QueryClassificationVO> classificationList;
}
