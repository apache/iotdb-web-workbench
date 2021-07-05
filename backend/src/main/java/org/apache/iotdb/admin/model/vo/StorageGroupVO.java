package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @anthor fyx 2021/7/2
 */
@Data
public class StorageGroupVO implements Serializable {
    private String groupName;
    private Integer groupId;
}