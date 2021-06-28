package org.apache.iotdb.admin.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @anthor fyx 2021/6/28
 */
@Data
public class GroupVO implements Serializable {
    private String description;
    private String creator;
    private String createTime;
    private String ttl;
    private String ttiUnit;
}
