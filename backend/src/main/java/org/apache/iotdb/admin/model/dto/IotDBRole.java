package org.apache.iotdb.admin.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @anthor fyx 2021/5/31
 */
@Data
public class IotDBRole implements Serializable {

    private String roleName;
    private List<String> privileges;

}
