package org.apache.iotdb.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *  展示别名及serverId
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnVO implements Serializable {
    private Integer id;
    private String alias;
}
