package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class StorageGroupVO implements Serializable {
    private String groupName;
    private Integer groupId;
}