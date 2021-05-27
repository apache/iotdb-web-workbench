package org.apache.iotdb.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @anthor fyx 2021/5/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnVO implements Serializable {
    private Integer id;
    private String alias;
}
