package org.apache.iotdb.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @anthor fyx 2021/6/22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupInfoVO implements Serializable {
    private String groupName;
    private Integer deviceCount;
    private String description;
}
